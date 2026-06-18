# Desktop Receiver Setup

## Step 1: Download and Run the Android Application

Open the project in Android Studio and run the application on your Android device.

---

## Step 2: Create Python Receiver

It is recommended to use Visual Studio Code.

Create a file named:

```text
receiver.py
```

Paste the provided Python receiver code into this file.

---

## Step 3: Install Python

Verify installation:

```bash
python --version
```

---

## Step 4: Install Required Packages

```bash
pip install opencv-python numpy
```

---

## Step 5: Start Receiver

Run:

```bash
python receiver.py
```

If successful, the terminal will display:

```text
Listening on port 5000...
```

---

## Step 6: Find PC IP Address

Open Command Prompt and run:

```bash
ipconfig
```

Locate the active network adapter and note the **IPv4 Address**.

Example:

```text
Wireless LAN adapter Wi-Fi

IPv4 Address . . . . . . . . . : 192.168.1.100
```

Use this IPv4 address in the Android application.

---

## Step 7: Start Streaming

1. Ensure the phone and PC are connected to the same Wi-Fi network.
2. Enter the PC's IPv4 address in the Android application.
3. Enter port:

```text
5000
```

4. Press **Connect**.
5. Press **Start Streaming**.

The video stream should appear in the Python receiver window.

Press **Q** to close the receiver.
