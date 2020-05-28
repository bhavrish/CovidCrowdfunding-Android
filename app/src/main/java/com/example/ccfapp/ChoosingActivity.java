package com.example.ccfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

public class ChoosingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing);
    }

    // redirect to user interface
    public void userOnClick(View v) {
        Intent intent = new Intent(ChoosingActivity.this, UserLoginActivity.class);
        startActivity(intent);
    }

    // redirect to business interface
    public void businessOnClick(View v) {
        Intent intent = new Intent(ChoosingActivity.this, BusinessLoginActivity.class);
        startActivity(intent);
    }
}
