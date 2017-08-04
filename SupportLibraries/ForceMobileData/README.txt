
Supported Android Versions -->
Android 5 and above


Permissions needed in order to use the class -->
 android.permission.INTERNET
 android.permission.ACCESS_NETWORK_STATE
 android.permission.ACCESS_WIFI_STATE
 android:name="android.permission.CHANGE_NETWORK_STATE
 
 
How to use ForceMobileData -->
1.  Initialize ForceMobileData object by Passing Activity instance 
2.  Call startForceMobileData() method to switch the internet to the mobile data
3.  start the process under the cm.setProcessDefaultNetwork(network) line in onAvailable() method that will run on mobile data
4.  Call stopForceMobileData() method to switch the internet to the wifi 
