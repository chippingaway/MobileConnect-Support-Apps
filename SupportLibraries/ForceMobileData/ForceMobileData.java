import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by Chinmay on 8/4/2017.
 */

public class ForceMobileData {

    private static Context context;
    private static ConnectivityManager cm;

    public ForceMobileData(Context context) {
        this.context = context;

    }

    private boolean checkMobileData()
    {
        boolean mobileDataEnabled = false;
        ConnectivityManager cmm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cmm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            mobileDataEnabled = (Boolean)method.invoke(cmm);
        } catch (Exception e) {
            Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
        }
        return mobileDataEnabled;
    }

    private boolean checkWifi()
    {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnectedOrConnecting();
    }


    public void startForceMobiledata()
    {

            if(checkWifi() && checkMobileData()) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkRequest req = new NetworkRequest.Builder().addCapability(12).addTransportType(0).build();
                    cm.requestNetwork(req, new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                try {
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        cm.bindProcessToNetwork(network);
                                    else {
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                        cm.setProcessDefaultNetwork(network);
                                    }


                                } catch (IllegalStateException e) {
                                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                }
            }
    }

    public void stopForceMobiledata()
    {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {cm.bindProcessToNetwork(null);

            }
            else{

                try {
                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    cm.setProcessDefaultNetwork(null);
                }catch (IllegalStateException e){Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();}
            }

        }
    }


}
