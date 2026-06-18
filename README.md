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

Paste the following code into the file:

```python
import socket
import struct
import cv2
import numpy as np

HOST = "0.0.0.0"
PORT = 5000

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind((HOST, PORT))
server.listen(1)

print(f"Listening on port {PORT}...")

conn, addr = server.accept()

print("Connected by:", addr)

def recvall(sock, size):
    data = b''

    while len(data) < size:
        packet = sock.recv(size - len(data))

        if not packet:
            return None

        data += packet

    return data

while True:

    length_bytes = recvall(conn, 4)

    if not length_bytes:
        break

    frame_size = struct.unpack(">I", length_bytes)[0]
    print("Frame Size:", frame_size)

    frame_data = recvall(conn, frame_size)

    if frame_data is None:
        break

    frame = np.frombuffer(
        frame_data,
        dtype=np.uint8
    )

    image = cv2.imdecode(
        frame,
        cv2.IMREAD_COLOR
    )

    if image is not None:

        cv2.imshow(
            "Android Stream",
            image
        )

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

conn.close()
server.close()
cv2.destroyAllWindows()
```

---

## Step 3: Install Python

Download and install Python.

Verify installation:

```bash
python --version
```

---

## Step 4: Install Required Packages

Install OpenCV and NumPy:

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

## Step 6: Start Android Streaming

Open Command Prompt and run:

ipconfig

Locate the active network adapter and note the IPv4 Address.

Example:

Wireless LAN adapter Wi-Fi

IPv4 Address . . . . . . . . . : 192.168.1.100

Use this IPv4 address in the Android application.

1. Enter the PC IP address.
2. Enter port:

```text
5000
```

3. Connect.
4. Start Streaming.

Ensure the phone and PC are connected to the same Wi-Fi network.
Enter the PC's IPv4 address in the Android application.
Enter port:
5000
Press Connect.
Press Start Streaming

The video stream should appear in the Python receiver window.

Press **Q** to stop the receiver.
