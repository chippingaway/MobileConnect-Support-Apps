package com.example.chinmay.forcemobiledata;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String Discovery_key = "88ae3b5d-18f1-4fc5-a97e-a6c5eae9cd5a";
    private static final String Discovery_Secret = "5d6c7979-7851-4b45-a81a-1acb12ba334d";
    private static final String Redirect_url = "http://localhost:8080/MCIndiaDummy/callback";
    private static final String Discovery_url = "https://india.discover.mobileconnect.io/gsma/v2/discovery";
    private static final String Token_key = "x-88ae3b5d-18f1-4fc5-a97e-a6c5eae9cd5a";
    private static String href_auth ,number;
    private ProgressDialog loading;
    Button force,normal;
    private static EditText text;
    private static ConnectivityManager connectivityManager;
    Network_Call network_call;
    NetworkRequest networkRequestM;
    private static volatile Network cellularNetwork;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        cellularNetwork = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            network_call = new Network_Call();
            networkRequestM = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build();
         }else
            Toast.makeText(this,"Required android version is android 5 or above",Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.requestNetwork(networkRequestM,network_call);
        }

        text = (EditText)findViewById(R.id.editText);
        normal = (Button)findViewById(R.id.normal_button);
        force = (Button)findViewById(R.id.force_button);
        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().equals("") && text.getText().length() == 10) {
                    number = text.getText().toString();
                    closeKeyboard(MainActivity.this, text.getWindowToken());
                    startDiscovery(1);
                } else if (text.getText().toString().equals(""))
                    Toast.makeText(MainActivity.this, "Enter number", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Incorrect number entered", Toast.LENGTH_SHORT).show();
            }
        });

        force.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (!text.getText().toString().equals("") && text.getText().length() == 10) {
                        number = text.getText().toString();
                        closeKeyboard(MainActivity.this, text.getWindowToken());
                        startDiscovery(2);
                    } else if (text.getText().toString().equals(""))
                        Toast.makeText(MainActivity.this, "Enter number", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(MainActivity.this, "Incorrect number entered", Toast.LENGTH_SHORT).show();

            }

        });


    }



    public void startDiscovery(final int type)
    {

        loading = ProgressDialog.show(this,"Please wait...","Discovering...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Discovery_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            new JSONObject(response).getString("error");
                            Toast.makeText(getApplicationContext(),new JSONObject(response).getString("description"),Toast.LENGTH_SHORT).show();
                            return;
                        } catch (JSONException e) {
                        }


                        try {
                            JSONObject jsonObject = new JSONObject(response).getJSONObject("response").getJSONObject("apis").getJSONObject("operatorid");
                            JSONArray jsonArray = jsonObject.getJSONArray("link");
                            href_auth = jsonArray.getJSONObject(0).getString("href")+"?client_id="+Token_key+"&response_type=code&scope=openid+mc_mnv_validate&redirect_uri="+Redirect_url+"&acr_values=2&state=123456&nonce=12345&login_hint=MSISDN:91"+number;
                            if (type==1)
                                startAuth(type);
                            else{


                                if(cellularNetwork!=null)
                                {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        loading = ProgressDialog.show(MainActivity.this,"Please wait...","Loading...",false,false);
                                        startAuthMobileData();
                                    }else
                                        Toast.makeText(getApplicationContext(),"Android version must be 5 or above",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Force Condition not found",Toast.LENGTH_SHORT).show();
                                    startAuth(1);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();

                        if(error instanceof com.android.volley.NoConnectionError){
                            Toast.makeText(getApplicationContext(),"NO INTERNET CONNECTION",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String responseBody = null;
                        try {
                            responseBody = new String( error.networkResponse.data, "utf-8" );
                            JSONObject jsonObject = new JSONObject( responseBody );
                            Toast.makeText(MainActivity.this,jsonObject.getString("description"),Toast.LENGTH_SHORT).show();
                            return;
                        } catch (UnsupportedEncodingException e) {} catch (JSONException e) {}
                        catch (Exception e){Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();}


                        if(!(error instanceof com.android.volley.NoConnectionError)){
                            Toast.makeText(getApplicationContext(), error.toString(),Toast.LENGTH_SHORT).show();
                            return;
                        }


                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("Redirect_URL",Redirect_url);
                params.put("MSISDN","91"+number);
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }


    void startAuth(final int type)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type==5000) {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), "Low internet connection on mobile data", Toast.LENGTH_SHORT).show();
                }LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view =  inflater.inflate(R.layout.force_webview, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                WebView webView = (WebView)view.findViewById(R.id.force_web);
                final ProgressBar bar = (ProgressBar)view.findViewById(R.id.force_pro);
                webView.loadUrl(href_auth);
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        handler.proceed();
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.e("url",url);
                        view.loadUrl(url);

                        if(url.contains("code=")){
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Success : "+url.substring(url.indexOf("code=")+5,url.length()),Toast.LENGTH_SHORT).show();
                        }
                        if(url.contains("error_description")){

                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),url.substring(url.indexOf("error_description")+18,url.length()),Toast.LENGTH_SHORT).show();
                        }
                        else if(url.contains("error")){

                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),url.substring(url.indexOf("error"),url.length()),Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (view.getProgress() == 100) {
                            bar.setVisibility(View.GONE);
                            view.setVisibility(View.VISIBLE);
                        }
                    }
                });
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                dialog.show();

            }
        });
    }






    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public class Network_Call extends ConnectivityManager.NetworkCallback{
        public Network_Call() {
            super();
        }

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            cellularNetwork = network;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            }
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            cellularNetwork = null;
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            cellularNetwork=null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private  void  startAuthMobileData()
    {

        final int[] hit = {0};
        final boolean[] redirect = {false};

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) cellularNetwork.openConnection(new URL(href_auth));
                    conn.setInstanceFollowRedirects(true);
                    conn.setChunkedStreamingMode(0);
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.setReadTimeout(5000);
                    conn.connect();

                    int status = conn.getResponseCode();
                    if (status != HttpURLConnection.HTTP_OK) {
                        if (status == HttpURLConnection.HTTP_MOVED_TEMP
                                || status == HttpURLConnection.HTTP_MOVED_PERM
                                || status == HttpURLConnection.HTTP_SEE_OTHER)
                            redirect[0] = true;
                    }

                    while (redirect[0])
                    {
                        redirect[0] = false;
                        String newUrl;

                        if (conn.getHeaderField("Location").contains(" "))
                            newUrl = conn.getHeaderField("Location").replace(" ","%20");
                         else
                             newUrl = conn.getHeaderField("Location");

                        if (newUrl.contains("code="))
                        {
                            loading.dismiss();
                            startToast("Success : "+newUrl.substring(newUrl.indexOf("code=")+5,newUrl.length()));
                            conn.disconnect();
                            break;

                        }
                         if((newUrl.charAt(newUrl.indexOf("msisdn_header")+14))=='&'){
                            conn.disconnect();
                            hit[0] =1;
                            loading.dismiss();
                            break;
                        }



                        conn = (HttpURLConnection) cellularNetwork.openConnection(new URL(newUrl));
                        conn.setInstanceFollowRedirects(true);
                        conn.setChunkedStreamingMode(0);
                        conn.setRequestProperty("Accept-Encoding", "identity");
                        conn.connect();
                        try{
                            status = conn.getResponseCode();
                            if (status != HttpURLConnection.HTTP_OK) {
                                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                                        || status == HttpURLConnection.HTTP_MOVED_PERM
                                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                                    redirect[0] = true;
                            }
                        }catch (java.net.SocketTimeoutException e){ }
                        catch (IOException e){Log.e("e",e.toString());
                        conn.disconnect();}
                    }


                    if (hit[0] ==1){
                        conn.disconnect();
                        startAuth(1);
                    }


                }catch (java.net.SocketTimeoutException e){ startAuth(5000);}
                catch (IOException e) {
                    Log.e("e",e.toString());
                }


            }
        }).start();



    }


    private void startToast(final String code)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),code,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
