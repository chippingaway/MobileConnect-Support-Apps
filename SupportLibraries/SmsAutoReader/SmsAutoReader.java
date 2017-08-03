import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SmsAutoReader {
    private static Context con;
    private static final String key1 ="";
    private static final String key2 ="";
    private static final String key3 ="";
    private static final String key4 ="";
    private static final String key5 ="";
    private static final String key6 ="";
    private static IntentFilter filter;


    public SmsAutoReader(Context con) {
        this.con = con;
        this.filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    }

    public final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"rec",Toast.LENGTH_SHORT).show();
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
//---display the new SMS message---
                if(str.contains("To login to") || str.contains("follow this link")) {

                   Web(str.substring(str.indexOf("link") + 5, str.indexOf("Don't") - 1));
                    con.unregisterReceiver(myReceiver);
                }
            }
        }
    };


    public void StartSmsAutoReader()
    {
        con.registerReceiver(myReceiver,filter);
    }

    private void Web(String url)
    {
        final Dialog dialog = new Dialog(con);
        WebView wv = new WebView(con);
        wv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        wv.loadUrl(url);
        wv.setVisibility(View.INVISIBLE);
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

        dialog.show();
    }



}
