package com.example.ccfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BusinessLoginActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_login);

        progressBar = findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.GONE); // turn off progress bar initially
    }

    public void loginOnClick(View v) {
        progressBar.setVisibility(View.VISIBLE); // activate progress bar


        EditText usernameEdit = (EditText) findViewById(R.id.editText6);
        EditText passwordEdit = (EditText) findViewById(R.id.editText7);
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        // set up HTTP request to log in business
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://covid-crowdfunding-api.herokuapp.com/login/business")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.GONE); // turn off progress bar after HTTP response
                        }
                    });

                    String dataString = response.body().string();
                    if (dataString.equals("null")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BusinessLoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        try {
                            JSONObject object = new JSONObject(dataString);
                            final String businessID = object.getString("_id");

                            Intent intent = new Intent(BusinessLoginActivity.this, BusinessEditingActivity.class);
                            intent.putExtra("businessID", businessID);
                            startActivity(intent);
                        } catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                        }
                    }
                }
            }
        });
    }

    public void registerOnClick(View v) {
        Intent intent = new Intent(BusinessLoginActivity.this, BusinessRegisterActivity.class);
        startActivity(intent);
    }
}
