package com.example.videostreamer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.videostreamer.network.SocketClient
import com.example.videostreamer.utils.ImageUtils
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val cameraExecutor =
        java.util.concurrent.Executors.newSingleThreadExecutor()
    private val CAMERA_PERMISSION_REQUEST = 100


    private val socketClient = SocketClient()
    private var isConnected = false
    private var isStreaming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnStream =
            findViewById<Button>(
                R.id.btnStream
            )
        val etIp = findViewById<EditText>(R.id.etIp)
        val etPort = findViewById<EditText>(R.id.etPort)
        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)


        val btnDisconnect =
            findViewById<Button>(R.id.btnDisconnect)
        btnDisconnect.setOnClickListener {

            socketClient.disconnect()

            isConnected = false
            isStreaming = false

            tvStatus.text = "Disconnected"

            btnStream.text = "Start Streaming"
        }

        btnStream.setOnClickListener {

            if (!isConnected) {

                tvStatus.text =
                    "Connect First"

                return@setOnClickListener
            }

            isStreaming = !isStreaming

            if (isStreaming) {

                btnStream.text =
                    "Stop Streaming"

                tvStatus.text =
                    "Streaming"

            } else {

                btnStream.text =
                    "Start Streaming"

                tvStatus.text =
                    "Connected"
            }
        }
        btnConnect.setOnClickListener {

            val ip = etIp.text.toString().trim()

            val port =
                etPort.text.toString()
                    .trim()
                    .toIntOrNull()

            if (ip.isEmpty() || port == null) {

                tvStatus.text = "Enter IP and Port"

                return@setOnClickListener
            }

            tvStatus.text = "Connecting..."

            CoroutineScope(Dispatchers.IO).launch {

                val connected =
                    socketClient.connect(
                        ip,
                        port
                    )

                withContext(Dispatchers.Main) {

                    if (connected) {

                        isConnected = true

                        tvStatus.text =
                            "Connected"

                    } else {

                        tvStatus.text =
                            "Connection Failed"
                    }
                }
            }
        }


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            startCamera()

        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                startCamera()
            }
        }
    }

    private fun startCamera() {

        val previewView =
            findViewById<PreviewView>(R.id.previewView)

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider =
                cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(
                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                )
                .build()

            val cameraExecutor =
                java.util.concurrent.Executors.newSingleThreadExecutor()

            imageAnalysis.setAnalyzer(
                cameraExecutor
            ) { imageProxy ->

                try {

                    if (isConnected &&
                        isStreaming
                    ) {

                        val jpegBytes =
                            ImageUtils.imageProxyToJpeg(
                                imageProxy
                            )
                        CoroutineScope(Dispatchers.IO).launch {

                            socketClient.sendFrame(
                                jpegBytes
                            )
                        }
                     }

                } catch (e: Exception) {

                    e.printStackTrace()

                } finally {

                    imageProxy.close()
                }
            }

            preview.surfaceProvider =
                previewView.surfaceProvider

            val cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA

            try {

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

            } catch (e: Exception) {

                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }
}