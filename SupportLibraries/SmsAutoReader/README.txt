Supported Android Versions -->
Android 4.0 and above


Permissions needed in order to use the class -->
 android.permission.INTERNET
 android.permission.RECEIVE_SMS
 android.permission.READ_SMS
 
 
How to use SmsAutoReader -->
1.  Initialize SmsAutoReader object by Passing Activity instance 
2.  Call StartSmsAutoReader() method in order to detect and read SMS 
3.  Call StopSmsAutoReader() method after receiving the code 
4.  Do'nt try to invoke this method dynamically that means by the action of any button
5.  Dont forget to grant SMS permission to the app that has to be run on Android version 6 or above
