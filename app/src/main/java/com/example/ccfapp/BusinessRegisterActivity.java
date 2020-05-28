package com.example.ccfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BusinessRegisterActivity extends AppCompatActivity {
    String errorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_register);
    }

    public void registerOnClick(View v) {
        errorMessage=""; // reset error message

        EditText usernameEdit = (EditText) findViewById(R.id.editText8);
        EditText passwordEdit = (EditText) findViewById(R.id.editText9);
        EditText nameEdit = (EditText) findViewById(R.id.editText10);
        EditText descriptionEdit = (EditText) findViewById(R.id.editText11);
        EditText targetEdit = (EditText) findViewById(R.id.editText12);
        EditText addressEdit = (EditText) findViewById(R.id.editText13);
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String name = nameEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        String targetGoal = targetEdit.getText().toString();
        String latitude = "";
        String longitude = "";
        String address = addressEdit.getText().toString();

        // convert address to latitude and longitude
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() != 0) {
                Address addr = addresses.get(0);
                latitude = Double.toString(addr.getLatitude());
                longitude = Double.toString(addr.getLongitude());
            }
            else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(BusinessRegisterActivity.this, "Invalid address", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(BusinessRegisterActivity.this, "Invalid address", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        // set up HTTP connection to register business
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("name", name)
                .add("description", description)
                .add("targetGoal", targetGoal)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .build();

        Request request = new Request.Builder()
                .url("https://covid-crowdfunding-api.herokuapp.com/register/business")
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
                    try {
                        JSONObject responseJSON = new JSONObject(response.body().string());
                        System.out.println(responseJSON);

                        // update error depending on type of error message
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
                            else if (innerJSON.has("description")) {
                                errorMessage = innerJSON.getJSONObject("description").getString("message");
                            }
                            else if (innerJSON.has("targetGoal")) {
                                errorMessage = innerJSON.getJSONObject("targetGoal").getString("message");
                            }
                            else if (innerJSON.has("latitude")) {
                                errorMessage = innerJSON.getJSONObject("latitude").getString("message");
                            }
                            else if (innerJSON.has("longitude")) {
                                errorMessage = innerJSON.getJSONObject("longitude").getString("message");
                            }
                        }
                        else if (responseJSON.has("errmsg")) {
                            errorMessage = "Username is already taken";
                        }

                        if(errorMessage != "") { // run if there is an error message
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(BusinessRegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            String businessID = responseJSON.getString("_id");

                            Intent intent = new Intent(BusinessRegisterActivity.this, BusinessEditingActivity.class);
                            intent.putExtra("businessID", businessID);
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
