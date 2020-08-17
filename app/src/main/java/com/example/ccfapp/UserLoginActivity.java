package com.example.ccfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserLoginActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE); // turn off progress bar at start
    }

    public void loginOnClick(View v) {
        progressBar.setVisibility(View.VISIBLE); // activate progress bar


        EditText usernameEdit = (EditText) findViewById(R.id.editText1);
        EditText passwordEdit = (EditText) findViewById(R.id.editText2);
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        // set up HTTP connection to log in user
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://covid-crowdfunding-api.herokuapp.com/login/user")
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
                                Toast.makeText(UserLoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        try {
                            JSONObject obj = new JSONObject(dataString);
                            String token = obj.getString("token");
                            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("jtw_token", token);
                            editor.commit();

                            String cached_token = sharedPreferences.getString("jtw_token", "");
                            JWT jwt = new JWT(cached_token);
                            Date expiresAt = jwt.getExpiresAt();
                            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Log.e("token", cached_token);
                            Log.e("expiration_date", formatter.format(expiresAt));
                            Log.e("expired_bool", Boolean.toString(jwt.isExpired(10)));




                        } catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                        }


                        Intent intent = new Intent(UserLoginActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void registerOnClick(View v) {
        Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
        startActivity(intent);
    }
}
