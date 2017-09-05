package com.example.chinmay.smsautoreaderdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static  EditText mobile_number;
    private static String number,href_auth,nonce;
    private ProgressDialog loading;
    
    // These are the credentials
    
    private static final String Discovery_key = "88ae3b5d-18f1-4fc5-a97e-a6c5eae9cd5a";
    private static final String Discovery_Secret = "5d6c7979-7851-4b45-a81a-1acb12ba334d";
    private static final String Redirect_url = "http://localhost:8080/MCIndiaDummy/callback";
    private static final String Discovery_url = "https://india.discover.mobileconnect.io/gsma/v2/discovery";
    private static final String Token_key = "x-88ae3b5d-18f1-4fc5-a97e-a6c5eae9cd5a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Check sms permission 
        // Grant if not given
        
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!checkSmsPermission())
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},123);
        }

        mobile_number = (EditText)findViewById(R.id.Enter_Number);

        (findViewById(R.id.Login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mobile_number.getText().toString().equals("") && mobile_number.getText().length() == 10) {
                    number = mobile_number.getText().toString();
                    closeKeyboard(MainActivity.this, mobile_number.getWindowToken());
                    startDiscovery();
                } else if (mobile_number.getText().toString().equals(""))
                    Toast.makeText(MainActivity.this, "Enter number", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Incorrect number entered", Toast.LENGTH_SHORT).show();

            }
        });

    }

    // Method to check sms permission
    
    private boolean checkSmsPermission()
    {
        String permission = "android.permission.READ_SMS";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == 123){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                //Displaying another toast if permission is not granted
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }
    }

    // Discovering the operator
    
    private void startDiscovery()
    {

        loading = ProgressDialog.show(this,"Please wait...","Discovering...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Discovery_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        try {

                            try {
                                new JSONObject(response).getString("error");
                                Toast.makeText(getApplicationContext(),new JSONObject(response).getString("description"),Toast.LENGTH_SHORT).show();
                                return;
                            } catch (JSONException e) {
                            }

                            JSONObject jsonObject = new JSONObject(response).getJSONObject("response").getJSONObject("apis").getJSONObject("operatorid");
                            JSONArray jsonArray = jsonObject.getJSONArray("link");
                            Random random = new Random();
                            nonce = String.valueOf(random.nextInt(100000)+9999);
                            href_auth = jsonArray.getJSONObject(0).getString("href")+"?client_id="+Token_key+"&response_type=code&scope=openid+mc_india_tc&redirect_uri="+Redirect_url+"&acr_values=2&state="+nonce+"123456&nonce="+nonce+"&login_hint=MSISDN:91"+number;
                            
                            // Start webview activity and pass autherize url along with it
                            
                            Intent i = new Intent(MainActivity.this,Webview.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("url",href_auth);
                            i.putExtras(bundle);
                            startActivity(i);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();

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

}

