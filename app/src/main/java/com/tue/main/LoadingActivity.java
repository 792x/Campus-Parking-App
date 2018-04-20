package com.tue.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mert.testproj2.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    Timer timer;
    int counter = 0;
    final int waitingTime = 4;
//    final String remote_ip = "paulwijsen.com";
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        timer = new Timer();

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        /**
         * Count seconds passed since this activity started.
         * If X amount of seconds haven't passed after fetching the data from the server is done,
         * then wait. (So that the user does not simply see a flashing screen that is quickly gone)
         */
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        counter++;
                    }
                });
            }
        }, 1000, 1000);

        new DataTask().execute("");
    }

    private class DataTask extends AsyncTask<String, Void, Map<String, String>> {

        @Override
        protected Map<String, String> doInBackground(String... params) {

            Map<String, String> result = new HashMap<String, String>();
            //Wait 5 seconds before launching next activity
            try {
//                Thread.sleep(5000);
                //Get parking areas
//                Document areasDoc = Jsoup.connect("http://" + remote_ip + ":8080/building").get();
//                final String areas = areasDoc.body().html().toString();
//
//                Get building areas
//                Document buildingsDoc = Jsoup.connect("http://" + remote_ip + ":8080/building").get();
//                final String buildings = areasDoc.body().html().toString();

//                result.put("areas", areas);
//                result.put("buildings", buildings);

                while(counter < waitingTime){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
//                Log.e("JSON", e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            timer.cancel();
            Intent i = new Intent(LoadingActivity.this, MapsActivity.class);
            //Attach data to intent for later retrival when MapsActivity is started
//            i.putExtra("areas", result.get("areas"));
//            i.putExtra("buildings", result.get("buildings"));
            spinner.setVisibility(View.GONE);
            startActivity(i);
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}