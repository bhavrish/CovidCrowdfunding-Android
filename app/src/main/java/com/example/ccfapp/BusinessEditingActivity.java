package com.example.ccfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class BusinessEditingActivity extends AppCompatActivity {
    private Button button;
    EditText usernameText;
    EditText passwordText;
    EditText nameText;
    EditText descriptionText;
    EditText targetGoalText;
    EditText moneySoFarText;
    EditText addressText;
    String errorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_editing);
        usernameText = (EditText) findViewById(R.id.editText14);
        passwordText = (EditText) findViewById(R.id.editText15);
        nameText = (EditText) findViewById(R.id.editText16);
        descriptionText = (EditText)findViewById(R.id.editText17);
        targetGoalText = (EditText)findViewById(R.id.editText18);
        moneySoFarText = (EditText)findViewById(R.id.editText);

        String businessID = getIntent().getStringExtra("businessID");
        String businessURL = "https://covid-crowdfunding-api.herokuapp.com/businesses/" + businessID;

        // set up HTTP request to populate editTexts with respective business data
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(businessURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String dataString = response.body().string();
                    try {
                        JSONObject object = new JSONObject(dataString);
                        final String username = object.getString("username");
                        final String password = object.getString("password");
                        final String name = object.getString("name");
                        final String description = object.getString("description");
                        final String targetGoal = Double.toString(object.getDouble("targetGoal"));
                        final String moneySoFar = Double.toString(object.getDouble("moneySoFar"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                usernameText.setText(username);
                                passwordText.setText(password);
                                nameText.setText(name);
                                descriptionText.setText(description);
                                targetGoalText.setText(targetGoal);
                                moneySoFarText.setText(moneySoFar);
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                }
            }
        });
    }

    public void updateOnClick(View v) {
        errorMessage=""; // reset error message
        String businessID = getIntent().getStringExtra("businessID");

        usernameText = (EditText) findViewById(R.id.editText14);
        passwordText = (EditText) findViewById(R.id.editText15);
        nameText = (EditText) findViewById(R.id.editText16);
        descriptionText = (EditText)findViewById(R.id.editText17);
        targetGoalText = (EditText)findViewById(R.id.editText18);
        moneySoFarText = (EditText)findViewById(R.id.editText);
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String name = nameText.getText().toString();
        String description = descriptionText.getText().toString();
        String targetGoal = targetGoalText.getText().toString();
        String moneySoFar = moneySoFarText.getText().toString();

        // set up HTTP request to push updated data to server
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("name", name)
                .add("description", description)
                .add("targetGoal", targetGoal)
                .add("moneySoFar", moneySoFar)
                .build();

        Request request = new Request.Builder()
                .url("https://covid-crowdfunding-api.herokuapp.com/businesses/edit/" + businessID)
                .put(formBody)
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

                        if (responseJSON.has("errmsg")) {
                            errorMessage = "Username is already taken";
                        }


                        if(errorMessage != "") {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(BusinessEditingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(BusinessEditingActivity.this, "Succesfully Updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                }
            }
        });
    }

    public void deleteOnClick(View v) {
        errorMessage=""; // reset error message
        String businessID = getIntent().getStringExtra("businessID");

        // set up HTTP connection to delete business from server
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://covid-crowdfunding-api.herokuapp.com/businesses/edit/" + businessID)
                .delete()
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

                        if (responseJSON.has("errmsg")) {
                            errorMessage = "Could Not Delete";
                        }


                        if(errorMessage != "") {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(BusinessEditingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(BusinessEditingActivity.this, "Succesfully Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Intent intent = new Intent(BusinessEditingActivity.this, ChoosingActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                }
            }
        });
    }

    public void onBack(View v) {
        Intent intent = new Intent(BusinessEditingActivity.this, ChoosingActivity.class);
        startActivity(intent);
    }
}
