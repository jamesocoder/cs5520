package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class A06Main extends AppCompatActivity {

    private final String E_TAG = "ERROR A06Main";
    /* This is the earliest date we can ping NASA's web service for.
       https://apod.nasa.gov/apod/archivepix.html

       The date is in this format so that java.time.Instant.parse() can read it without error.
     */
    private final CharSequence APOD_MIN = "1995-06-16T00:00:00.00Z";
    // This Handler allows worker threads to manipulate Views on the UI Thread
    private final Handler jsonHandler = new Handler();
    // These 2 variables allow us to use the DatePicker widget to get a date to pull an image for
    // from the user
    private DatePicker datePicker;
    private LocalDate myDate;
    // These 3 variables handle the UI when retrieving and displaying the data we get from NASA
    private ProgressBar pBar;
    private TextView cpyrght, description;
    private ImageView iView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a06_activity_main);

        datePicker = findViewById(R.id.a06_view_datePicker);
        /* Return Current Time using java.time (Java 8 and up)
           https://docs.oracle.com/javase/10/docs/api/java/time/package-summary.html
           Instants are used here because LocalDate does not have a method that returns milliseconds
           since Epoch, which is the expected format for DatePicker.set___Date()
         */
        datePicker.setMaxDate(Instant.now().toEpochMilli());
        datePicker.setMinDate(Instant.parse(APOD_MIN).toEpochMilli());

        // Find our Views so that worker threads can manipulate them
        pBar = findViewById(R.id.a06_view_progress);
        cpyrght = findViewById(R.id.a06_view_txtSource);
        description = findViewById(R.id.a06_view_txtDscrpt);
        iView = findViewById(R.id.a06_view_image);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.a06_view_btnPingWeb:
                /* android.widget.DatePicker months are zero-indexed
                   https://stackoverflow.com/a/4467894/13084818

                   This is an example of using java.time's updated APIs to convert a date string to
                   a datetime Java object.  Learn more about formatting:
                   https://docs.oracle.com/javase/10/docs/api/java/time/format/DateTimeFormatter.html
                 */
                myDate = LocalDate.parse(
                        String.valueOf(datePicker.getYear()) + '-' + (datePicker.getMonth()+1) + '-' + datePicker.getDayOfMonth(),
                        DateTimeFormatter.ofPattern("yyyy-M-d")
                    );

                // Launch a new Thread to interact with the Web Service
                WebRunnable myRunnable = new WebRunnable();
                new Thread(myRunnable).start();
                break;
        }
    }

    // Web Service: https://api.nasa.gov/#apod
    class WebRunnable implements Runnable {

        String baseUrl = "https://api.nasa.gov/planetary/apod?";
        // Web services output JSONs identifying the resources they return
        JSONObject jObject;
        // This drawable object will hold the image held by the URL we pull from NASA
        Drawable nasaPic;

        @Override
        public void run() {
            // Display an indeterminate ProgressBar until we have successfully pulled the web data
            // Special thanks to this tutorial: https://abhiandroid.com/ui/progressbar
            jsonHandler.post(() -> { pBar.setVisibility(View.VISIBLE); });
            // The Web Service returns very quickly on the WiFi connection this App was developed on
            // so we artificially delay the worker thread to show a working ProgressBar
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Ping web service and validate the data returned
            try {
                /* A DEMO_KEY should be sufficient enough for demonstrating this App works.
                   NASA allows each IP address to use this key 50 times per day.
                   See "DEMO_KEY Rate Limits" on: https://api.nasa.gov

                   We pass multiple arguments through HTML after a '?', separating each argument
                   with '&'
                 */
                URL launchUrl = new URL(baseUrl + "api_key=DEMO_KEY&date=" + myDate.toString());
                String resp = httpResponse(launchUrl);
                jObject = new JSONObject(resp);

                /* Load the image pointed to by the Web Service's response
                   Based off of: https://stackoverflow.com/a/6407554/13084818
                 */
                try {
                    if (jObject.has("url")) {
                        InputStream is = (InputStream) new URL(jObject.getString("url")).getContent();
                        nasaPic = Drawable.createFromStream(is, "nasa");
                    }
                } catch (MalformedURLException e) {
                    Log.e(E_TAG, "NASA's image URL is malformed.");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(E_TAG, "Had trouble accessing NASA's image.");
                    e.printStackTrace();
                }

                // Handle the text data returned by the Web Service
                jsonHandler.post(() -> {
                    try {
                        if (jObject.has("title")) {
                            cpyrght.setText(jObject.getString("title"));
                        } else if (jObject.has("copyright")) {
                            cpyrght.setText(jObject.getString("copyright"));
                        }
                        if (jObject.has("explanation")) {
                            description.setText(jObject.getString("explanation").replace('\n', ' '));
                            // This attribute contributes to accessibility (for the blind)
                            iView.setContentDescription(jObject.getString("explanation").replace('\n', ' '));
                        }
                        pBar.setVisibility(View.INVISIBLE);
                        if (jObject.has("url")) { iView.setImageDrawable(nasaPic); }
                    } catch (JSONException e) {
                        Log.e(E_TAG, "Retrieved JSON Object may be malformed.");
                        e.printStackTrace();
                    }
                });
            } catch (MalformedURLException e) {
                Log.e(E_TAG,"Prebuilt Web Service URL is malformed.");
                e.printStackTrace();
            } catch (ProtocolException e) {
                Log.e(E_TAG,"ProtocolException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(E_TAG,"IOException thrown when attempting to access the Web Service's URL");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(E_TAG,"Could not create a JSON Object.  Web Service may be returning malformed String.");
                e.printStackTrace();
            }
        }

        // The 2 methods below were borrowed from Module 07's NetworkExample3-1 WebServiceActivity.java
        private String httpResponse(URL url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();

            // Read response.
            InputStream inputStream = conn.getInputStream();

            return convertStreamToString(inputStream);
        }

        // This method presumably converts the raw data returned by a URL Connection into a format
        // that is acceptable to JSONObject
        private String convertStreamToString(InputStream inputStream){
            StringBuilder stringBuilder=new StringBuilder();
            try {
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String len;
                while((len=bufferedReader.readLine())!=null){
                    stringBuilder.append(len);
                }
                bufferedReader.close();
                return stringBuilder.toString().replace(",", ",\n");
            } catch (Exception e) {
                Log.e(E_TAG, e.getClass().toString());
                e.printStackTrace();
            }
            return "";
        }
    }
}