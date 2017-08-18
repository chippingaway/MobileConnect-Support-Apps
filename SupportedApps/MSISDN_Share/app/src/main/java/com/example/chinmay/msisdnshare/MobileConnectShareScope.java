package com.example.chinmay.msisdnshare;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MobileConnectShareScope {
    private static Context con;

    private static final String Discovery_key = "0758d7f6-5cb3-47a6-8aa7-b393ace51d96";
    private static final String Discovery_Secret = "0d42d633-d665-4f68-8c0c-b1d70938fe29";
    private static final String Token_key = "x-0758d7f6-5cb3-47a6-8aa7-b393ace51d96";
    private static final String Token_Secret = "x-0d42d633-d665-4f68-8c0c-b1d70938fe29";
    private static final String Redirect_url = "https://e-complaintmanager.000webhostapp.com/";
    private static final String Discovery_url = "https://india.discover.mobileconnect.io/gsma/v2/discovery";
    private TextView textDiscover,textAuth,textToken,textUserInfo;
    private static String href_auth , href_token,href_user;
    private LinearLayout DialogLayout;
    private AlertDialog dialog;
    private  long starttime;

    public MobileConnectShareScope(Context context, Activity activity) {
        con = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(con);
        DialogLayout = ((LinearLayout)activity.getLayoutInflater().inflate(R.layout.dialog_layout, null));
        builder.setView(DialogLayout);
        dialog = builder.create();
    }



    private  void  addTextView(final TextView textView)
    {
        DialogLayout.addView(textView);
    }

    private void showAlertMessage(String paramString1 , String paramString2)
    {
        AlertDialog localAlertDialog = new AlertDialog.Builder(con).create();
        localAlertDialog.setTitle(paramString1);
        localAlertDialog.setMessage(paramString2);
        localAlertDialog.setCancelable(false);
        localAlertDialog.setButton(-3, "OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                paramAnonymousDialogInterface.dismiss();

            }
        });
        localAlertDialog.show();
    }


    public void mConnectShareScope()
    {
        mConnectShareScopeDiscovery();
    }

    private void mConnectShareScopeDiscovery()
    {
        starttime=System.currentTimeMillis();
        //loading = ProgressDialog.show(this.con,"Please wait...","Discovery...",false,false);
        final Handler mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run () {

                mHandler.post(new Runnable() {
                    @Override
                    public void run () {
                        dialog.show();            }
                });
            }
        }).start();

         textDiscover = new TextView(con);
         textDiscover.setText("Discovering...");
        addTextView(textDiscover);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Discovery_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  loading.dismiss();
                        try {
                            new JSONObject(response).getJSONArray("links").getJSONObject(0).getString("href");
                            Toast.makeText(con,"Use mobile data instead of WIFI",Toast.LENGTH_SHORT).show();
                            return;
                        } catch (JSONException e) {
                        }
                        try {
                            new JSONObject(response).getString("error");
                            Toast.makeText(con,new JSONObject(response).getString("description"),Toast.LENGTH_SHORT).show();
                            return;
                        } catch (JSONException e) {
                        }


                        try {
                            JSONObject jsonObject = new JSONObject(response).getJSONObject("response").getJSONObject("apis").getJSONObject("operatorid");
                            JSONArray jsonArray = jsonObject.getJSONArray("link");
                            href_auth = jsonArray.getJSONObject(0).getString("href")+"?client_id="+Token_key+"&response_type=code&scope=openid+mc_attr_vm_share&redirect_uri="+Redirect_url+"&acr_values=2&state=123456&nonce=1234567";
                            href_token = jsonArray.getJSONObject(1).getString("href");
                            href_user = jsonArray.getJSONObject(2).getString("href");
                            textDiscover.setText("Discovered  "+String.valueOf(System.currentTimeMillis()-starttime)+" ms");
                                                  mConnectShareScopeAuthentication();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(con,e.toString(),Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // loading.dismiss();
                        dialog.dismiss();
                        if(error instanceof com.android.volley.NoConnectionError)
                            showAlertMessage("ERROR","NO INTERNET CONNECTION");

                        if(!(error instanceof com.android.volley.NoConnectionError))
                            showAlertMessage("ERROR",error.toString());

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("Redirect_URL",Redirect_url);
                params.put("Using-MobileData","true");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                String credentials = Discovery_key+":"+Discovery_Secret;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Accept", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this.con);
        requestQueue.add(stringRequest);



    }

    private void mConnectShareScopeAuthentication()
    {
        starttime=System.currentTimeMillis();
        //loading = ProgressDialog.show(this.con,"Please wait...","Authentication...",false,false);

        textAuth = new TextView(con);
        textAuth.setText("Authenticating...");
        addTextView(textAuth);

        WebView wv = new WebView(con);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.loadUrl(href_auth);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                if(url.contains("code=")){
                    textAuth.setText("Authenticated  "+String.valueOf(System.currentTimeMillis()-starttime)+" ms");



                    //loading.dismiss();
                    mConnectShareScopeToken(url.substring(url.indexOf("code=")+5,url.length()));
                }
                if(url.contains("error")){
                    dialog.dismiss();
                    Toast.makeText(con,url.substring(url.indexOf("error")+5),Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }


    private void mConnectShareScopeToken(final String code)
    {
        starttime=System.currentTimeMillis();
        textToken = new TextView(con);
        textToken.setText("Fetching  Token...");
        addTextView(textToken);

        //loading = ProgressDialog.show(this.con,"Please wait...","Fetching...  Token",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,href_token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mConnectShareScopeUserInfo(jsonObject.getString("access_token"));
                            textToken.setText("Token Fetched  "+String.valueOf(System.currentTimeMillis()-starttime)+" ms");



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(con, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if(error instanceof com.android.volley.NoConnectionError)
                            showAlertMessage("ERROR","NO INTERNET CONNECTION");

                        if(!(error instanceof com.android.volley.NoConnectionError))
                            showAlertMessage("ERROR",error.toString());


                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("code",code);
                params.put("grant_type","authorization_code");
                params.put("redirect_uri",Redirect_url);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = Token_key+":"+Token_Secret;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", auth);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(con);
        requestQueue.add(stringRequest);

    }


    private  void mConnectShareScopeUserInfo(final String code)
    {

        starttime=System.currentTimeMillis();
        //loading = ProgressDialog.show(con,"Please wait..","Fetching... Number",false,false);
        textUserInfo = new TextView(con);
        textUserInfo.setText("Fetching Number ...");
        addTextView(textUserInfo);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, href_user,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //loading.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            textUserInfo.setText("Number : "+jsonObject.getString("device_msisdn")+"   "+String.valueOf(System.currentTimeMillis()-starttime)+" ms");

                            final Handler handler = new Handler();
                            final Runnable r = new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                    handler.postDelayed(this, 10000);
                                }
                            };
                            handler.postDelayed(r, 10000);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(con, e.toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if(error instanceof com.android.volley.NoConnectionError)
                            showAlertMessage("ERROR","NO INTERNET CONNECTION");

                        if(!(error instanceof com.android.volley.NoConnectionError))
                            showAlertMessage("ERROR",error.toString());

                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+code);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(con);
        requestQueue.add(stringRequest);


    }




}

