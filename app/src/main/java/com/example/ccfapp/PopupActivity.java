package com.example.ccfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PopupActivity extends AppCompatActivity {
    TextView nameText;
    TextView descriptionText;
    TextView targetGoalText;
    TextView moneySoFarText;
    TextView percentageText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        nameText = (TextView)findViewById(R.id.textView7);
        descriptionText = (TextView)findViewById(R.id.textView14);
        targetGoalText = (TextView)findViewById(R.id.textView12);
        moneySoFarText = (TextView)findViewById(R.id.textView13);
        percentageText = (TextView)findViewById(R.id.textView8);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        String markerID = getIntent().getStringExtra("markerID");
        String markerURL = "https://covid-crowdfunding-api.herokuapp.com/businesses/" + markerID;

        // set up HTTP connection to get specific business data
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(markerURL)
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
                        final String name = object.getString("name");
                        final String description = object.getString("description");
                        final String targetGoal = Double.toString(object.getDouble("targetGoal"));
                        final String moneySoFar = Double.toString(object.getDouble("moneySoFar"));
                        final int percentage = (int) (100 * (Double.parseDouble(moneySoFar)/Double.parseDouble(targetGoal)));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nameText.setText(name);
                                descriptionText.setText("\"" + description + "\"");
                                targetGoalText.setText(targetGoal);
                                moneySoFarText.setText(moneySoFar);
                                percentageText.setText(percentage+"% goal met");
                                progressBar.setProgress(percentage);
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                }
            }
        });

    }

    public void logoutOnClick(View v) {
        Intent intent = new Intent(PopupActivity.this, ChoosingActivity.class);
        startActivity(intent);
    }

    public void onBack(View v) {
        Intent intent = new Intent(PopupActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}
