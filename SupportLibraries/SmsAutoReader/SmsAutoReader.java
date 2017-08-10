import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  This is SmsAutoReader class which is used to auto read the sms and load the url with in it .
 */

public class SmsAutoReader {
    private static Context con;
    private static IntentFilter filter;
    private static final String[] keys ={"2291","AH-MCONNT","ADMCONNT"};

    //status will be true if the broadcast receiver is on
    //status will be false if the broadcast receiver is off
    private static boolean status=false;

    // Constructor of SmsAutoReader Class
    // Pass Activity Instance to the SmsAutoReader in order to use its methods

    public SmsAutoReader(Context con) {
        this.con = con;
        this.filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    }

    // BroadcastReceiver to receive the messages
    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


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
                    str += "-->";
                    str += msgs[i].getMessageBody().toString();
                    str += "\n";
                }
//---display the new SMS message---

                if(Arrays.asList(keys).contains(str.substring(str.indexOf("SMS from ") + 9, str.indexOf("-->")))) {
                   Web(pullLinks(str));
                }
            }
        }
    };

    // After receiving the code invoke this method to stop the BroadcastReceiver
    // Do'nt try to invoke this method dynamically that means by the action of any button


    public void StopSmsAutoReader()
    {
        try {
            if(status){
                con.unregisterReceiver(myReceiver);
                status=false;
            }
        }catch (IllegalArgumentException e){
            Toast.makeText(con,e.toString(),Toast.LENGTH_SHORT).show();}
        catch (Exception e){
            Toast.makeText(con,e.toString(),Toast.LENGTH_SHORT).show();}



    }
    // Invoke this method in order to start the SmsAutoReader
    public void StartSmsAutoReader()
    {
        status=true;
        con.registerReceiver(myReceiver,filter);
    }

    // This method is used to load the url

    private void Web(String url)
    {
        WebView wv = new WebView(con);
        wv.loadUrl(url);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return false;
            }
        });
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
