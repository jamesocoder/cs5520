package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
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
    private final CharSequence APOD_MIN = "1995-06-16T00:00:00.00Z";
    private final Handler jsonHandler = new Handler();
    private DatePicker datePicker;
    private LocalDate myDate;
    private TextView cpyrght, description;
    private ImageView iView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a06_activity_main);

        cpyrght = findViewById(R.id.a06_view_txtSource);
        description = findViewById(R.id.a06_view_txtDscrpt);
        iView = findViewById(R.id.a06_view_image);

        datePicker = findViewById(R.id.a06_view_datePicker);
        /* Return Current Time using java.time (Java 8 and up)
           https://docs.oracle.com/javase/10/docs/api/java/time/package-summary.html
           Instants are used here because LocalDate does not have a method that returns milliseconds
           since Epoch
         */
        datePicker.setMaxDate(Instant.now().toEpochMilli());
        datePicker.setMinDate(Instant.parse(APOD_MIN).toEpochMilli());
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.a06_view_btnPingWeb:
                /* android.widget.DatePicker months are zero-indexed
                   https://stackoverflow.com/a/4467894/13084818
                 */
                myDate = LocalDate.parse(
                        String.valueOf(datePicker.getYear()) + '-' + (datePicker.getMonth()+1) + '-' + datePicker.getDayOfMonth(),
                        DateTimeFormatter.ofPattern("yyyy-M-d")
                    );

                WebRunnable myRunnable = new WebRunnable();
                new Thread(myRunnable).start();
                break;
        }
    }

    /* https://api.nasa.gov/#apod
       TODO
     */
    class WebRunnable implements Runnable {

        String baseUrl = "https://api.nasa.gov/planetary/apod?";
        JSONObject jObject;
        Drawable nasaPic;

        @Override
        public void run() {
            try {
                URL launchUrl = new URL(baseUrl + "api_key=DEMO_KEY&date=" + myDate.toString());
                String resp = httpResponse(launchUrl);
                jObject = new JSONObject(resp);
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
                jsonHandler.post(() -> {
                    try {
                        if (jObject.has("title")) {
                            cpyrght.setText(jObject.getString("title"));
                        } else if (jObject.has("copyright")) {
                            cpyrght.setText(jObject.getString("copyright"));
                        }
                        if (jObject.has("explanation")) {
                            description.setText(jObject.getString("explanation"));
                            // This attribute contributes to accessibility (for the blind)
                            iView.setContentDescription(jObject.getString("explanation"));
                        }
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
                Log.e(E_TAG,"IOException");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(E_TAG,"Could not create a JSON Object.  Web Service may be returning malformed String.");
                e.printStackTrace();
            }
        }

        private String httpResponse(URL url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();

            // Read response.
            InputStream inputStream = conn.getInputStream();

            return convertStreamToString(inputStream);
        }

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