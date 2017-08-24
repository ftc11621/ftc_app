Android Studio Instant Run:
    Message from FTC engineer:
        If you are using Android Studio to write op modes for your FTC Robot Controller, then it is strongly recommended that you disable the Instant Run feature that is included with newer versions of Android Studio.
        
        Instant Run is problematic and can cause weird (and difficult to debug) run time errors with the FTC Robot Controller app.
        
        Details on how to disable Instant Run can be found on the developer.android.com website:
        
        https://developer.android.com/studio/run/index.html#disable-ir
        
 How to upload code wirelessly:
    1. Connect to USB and upload the code as usual.
    2. Go to the Programming Mode to get the WiFi name, password, and IP address.
    3. Connect to the phone Direct WiFi.
    4. On Android Studio Terminal, type:
        a. adb tcpip 5555
        b. adb connect <IP address>:5555
    5. Disconnect USB.
    6. If it loses connection, type "adb connect <IP address>:5555" again