package com.example.chinmay.msisdnshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button MobileConnect = (Button)findViewById(R.id.MobileConnect);
        MobileConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobileConnectShareScope scope = new MobileConnectShareScope(MainActivity.this,MainActivity.this);
                scope.mConnectShareScope();
            }
        });

    }
}
