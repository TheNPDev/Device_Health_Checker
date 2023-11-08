package com.example.devicehealthchecker;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


        btn = findViewById(R.id.start_btn);
        String[] permissions = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE
        };

        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to open the SecondActivity
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}