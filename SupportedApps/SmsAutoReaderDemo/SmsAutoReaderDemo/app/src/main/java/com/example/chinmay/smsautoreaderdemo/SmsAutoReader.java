package com.example.chinmay.smsautoreaderdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  This is SmsAutoReader class which is used to auto read the sms and load the url with in it .
 */

public class SmsAutoReader {
    private static Context con;
    private static IntentFilter filter;

    // Only those SMS would be read which are mentioned against keys
    private static final String[] keys ={"2291","AH-MCONNT","ADMCONNT","MConnect","AD-MCONNT"};


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
                    StopSmsAutoReader();
                }



            }
        }
    };

    // After receiving the code invoke this method to stop the BroadcastReceiver
    // Do'nt try to invoke this method dynamically that means by the action of any button


    public void StopSmsAutoReader()
    {
        try {
            con.unregisterReceiver(myReceiver);
        }catch (IllegalArgumentException e){
        }
        catch (Exception e){
            Log.e("error",e.toString());}
    }

    // Invoke this method in order to start the SmsAutoReader
    public String StartSmsAutoReader() throws JSONException {
        if(checkSmsPermission()){
            con.registerReceiver(myReceiver,filter);
            return null;
        }
        else{
            JSONObject object = new JSONObject();
            object.put("error","READ_SMS error");
            object.put("error_description","READ_SMS permission not permitted");
            return object.toString();
        }

    }

    // This method is used to load the url

    private void Web(String url)
    {

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(con);
        requestQueue.add(stringRequest);

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

    // This method will return true if READ_SMS permission is given or viceversa
    private boolean checkSmsPermission()
    {
        String permission = "android.permission.READ_SMS";
        int res = con.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


}

