import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmsAutoReader {
    private static Context con;
    
    
    private static final String key1 ="2291";
    private static final String key2 ="AH-MCONNT";
    private static final String key3 ="ADMCONNT";
    private static final String key4 ="";
    private static final String key5 ="";
    private static final String key6 ="";
    private static IntentFilter filter;
    
    
    private static boolean a=false;

    // Constructor of SmsAutoReader Class .
    // Pass Activity Instance to the SmsAutoReader in order to use its methods .
    public SmsAutoReader(Context con) {
        this.con = con;
        this.filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    }

    
    // BroadcastReceiver to receive the messages .
    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            a=true;
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String str = "";
            if (bundle != null) {
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    str += "SMS from " + msgs[i].getOriginatingAddress();
                    str += " :";
                    str += msgs[i].getMessageBody().toString();
                    str += "\n";
                }
                if(str.contains(key1) || str.contains(key2) || str.contains(key3)) {
                   // The link is passing on to the Web method . 
                   Web(pullLinks(str));
                }
            }
        }
    };

    // After receiving the code invoke this method to stop the BroadcastReceiver
    // Also invoke this method in the onDestroy() method of the Activity
    public void StopSmsAutoReader()
    {
        if(a){
            con.unregisterReceiver(myReceiver);
            a=false;
        }

    }

    // Invoke this method in order to start the SmsAutoReader
    
    public void StartSmsAutoReader()
    {
        con.registerReceiver(myReceiver,filter);
    }

    // This method is used to load the url
    
    private void Web(String url)
    {
        final Dialog dialog = new Dialog(con,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        WebView wv = new WebView(con);
        wv.setVisibility(View.INVISIBLE);
        wv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        wv.loadUrl(url);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(wv);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

//  This method is used to pull link from Sms String  
    
    private String pullLinks(String text) {
        String link = "";

        String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            link = urlStr;
        }
        return link;
    }


}
