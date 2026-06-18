package com.example.videostreamer.network

import java.io.DataOutputStream
import java.net.Socket

class SocketClient {

    private var socket: Socket? = null
    private var outputStream: DataOutputStream? = null

    fun connect(
        ip: String,
        port: Int
    ): Boolean {

        return try {

            socket = Socket(ip, port)

            outputStream =
                DataOutputStream(
                    socket!!.getOutputStream()
                )

            true

        } catch (e: Exception) {

            e.printStackTrace()
            false
        }
    }

    fun sendFrame(
        frameBytes: ByteArray
    ) {

        try {

            outputStream?.writeInt(
                frameBytes.size
            )

            outputStream?.write(
                frameBytes
            )

            outputStream?.flush()

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    fun disconnect() {

        try {

            outputStream?.close()
            socket?.close()

            outputStream = null
            socket = null

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}