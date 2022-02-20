package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

public class A05Main extends AppCompatActivity {

    // A Google Play service that polls location data
    private FusedLocationProviderClient fusedLocationClient;
    // The button on a05_activity_main
    private Button go;

    // The object that handles requesting permissions
    // See GetLocation project for more details on the permission request process
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
               if (isGranted) {
                   // Pull and display location coordinates
                   getPriorCoordinates();
               } else {
                   // The user denied the app permission to read location data
                   Snackbar.make(findViewById(R.id.a05_layout), R.string.a05_denied, Snackbar.LENGTH_LONG).show();
               }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a05_activity_main);

        go = findViewById(R.id.a05_btn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myPermission = Manifest.permission.ACCESS_COARSE_LOCATION;

                if (ContextCompat.checkSelfPermission(A05Main.this, myPermission) == PackageManager.PERMISSION_GRANTED) {
                    // Permission has already been granted to this app previously.  Proceed with gathering Location data
                    getPriorCoordinates();
                } else if (shouldShowRequestPermissionRationale(myPermission)) {
                    // Provide a rationale for requesting permission data and then allow the User to launch the
                    // permission dialogue
                    Snackbar.make(view, R.string.a05_required, Snackbar.LENGTH_LONG)
                            .setAction(R.string.a05_ok,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            requestPermissionLauncher.launch(myPermission);
                                        }
                                    }
                            )
                            .show();
                } else {
                    // User has set this app's permissions to "Ask every time" previously
                    requestPermissionLauncher.launch(myPermission);
                }
            }
        });
    }

    // Suppress MissingPermission warning since we handle that before calling this method
    @SuppressLint("MissingPermission")
    // Uses the Google Play service's Fused Location Provider to access Location data first using
    // the most efficient method: .getLastLocation()
    private void getPriorCoordinates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        /* Got last known location. Check if null
                           Location is null when:
                           * Location is turned off in the device settings
                           * A device never recorded its location yet (new or factory reset device)
                           * Google Play services has restarted and no app has yet made a call to
                             fusedLocationClient.getCurrentLocation()
                         */
                        if (location != null) {
                            //Log.d("Last Location Success", location.toString());
                            // Location data successfully pulled.  Display it on this Activity's TextViews
                            setCoordinates(location.getLatitude(), location.getLongitude());
                        } else {
                            // Prior location data does not exist, attempt to pull Current data
                            getCurrentCoordinates(fusedLocationClient);
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentCoordinates(FusedLocationProviderClient client) {
        client.getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            //Log.d("Current Location Success", location.toString());
                            setCoordinates(location.getLatitude(), location.getLongitude());
                        } else {
                            /* .getCurrentLocation() will return a null Location object if the device
                               is unable to determine its location within tens of seconds.  Tell the
                               user if such a "request timed out" event occurs
                             */
                            Snackbar.make(findViewById(R.id.a05_layout), R.string.a05_timeout, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setCoordinates(double latitude, double longitude) {
        TextView tView = findViewById(R.id.a05_txt_latitude);
        tView.setText(String.valueOf(latitude));
        tView = findViewById(R.id.a05_txt_longitude);
        tView.setText(String.valueOf(longitude));
    }
}