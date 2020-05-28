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
import org.json.JSONArray;
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

public class UserRegisterActivity extends AppCompatActivity {
    String errorMessage = "";
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE); // turn off progress bar at start
    }

    public void registerOnClick(View v) {
        errorMessage=""; // reset error message
        progressBar.setVisibility(View.VISIBLE); // activate progress bar

        EditText usernameEdit = (EditText) findViewById(R.id.editText3);
        EditText passwordEdit = (EditText) findViewById(R.id.editText4);
        EditText nameEdit = (EditText) findViewById(R.id.editText5);
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String name = nameEdit.getText().toString();

        // set up connection register user
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("name", name)
                .build();

        Request request = new Request.Builder()
                .url("https://covid-crowdfunding-api.herokuapp.com/register/user")
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

                    try {
                        JSONObject responseJSON = new JSONObject(response.body().string());

                        // update error message depending on type of error
                        if (responseJSON.has("errors")) {
                            JSONObject innerJSON = responseJSON.getJSONObject("errors");
                            if (innerJSON.has("username")) {
                                errorMessage = innerJSON.getJSONObject("username").getString("message");
                            }
                            else if (innerJSON.has("password")) {
                                errorMessage = innerJSON.getJSONObject("password").getString("message");
                            }
                            else if (innerJSON.has("name")) {
                                errorMessage = innerJSON.getJSONObject("name").getString("message");
                            }
                        }
                        else if (responseJSON.has("errmsg")) {
                            errorMessage = "Username is already taken";
                        }


                        if(errorMessage != "") {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(UserRegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Intent intent = new Intent(UserRegisterActivity.this, MapsActivity.class);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                }
            }
        });
    }
}
