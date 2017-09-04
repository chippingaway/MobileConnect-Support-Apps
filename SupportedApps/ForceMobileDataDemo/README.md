# Android Force Mobile Data

Example on how to autherize via "Mobile Data" even when mobile is connected to Wifi

Obtaining the Mobile Data "Network" object from ConnectivityManager.requestNetwork specifying Capability and TransportType(TRANSPORT_CELLULAR)

https://developer.android.com/reference/android/net/ConnectivityManager.html#requestNetwork(android.net.NetworkRequest, android.net.ConnectivityManager.NetworkCallback)

Then with the Network object of "Mobile Data", perform a

Network.openConnection 

https://developer.android.com/reference/android/net/Network.html#openConnection(java.net.URL)

