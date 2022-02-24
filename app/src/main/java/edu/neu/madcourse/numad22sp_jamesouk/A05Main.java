package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

public class A05Main extends AppCompatActivity {

    // A Google Play service that polls location data
    private FusedLocationProviderClient fusedLocationClient;
    // This constant determines how accurate we want the location data to be
    private final int LOC_PRIORITY = LocationRequest.PRIORITY_LOW_POWER;
    // The level of permission this app will use for Location data
    private final String myPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
    // For tagging the Log's output for an easier finding of debug notes
    private final String APP_TAG = "MY_LOCATION";
    // For determining if the user is requesting location updates.  Stored whenever the App is destroyed
    // Could be implemented with a UI switch, but set to true just for easy location demonstration purposes
    private final String REQUESTING_LOCATION_UPDATES = "LOC_UPDATES";
    private Boolean requestingLocationUpdates = true;
    // Contains the type of location updates we are requesting
    private LocationRequest locationRequest;
    // An object containing the code to handle the list of Locations returned by requestLocationUpdates()
    private LocationCallback locationCallback;

    /* This androidx handler for launching an Activity and monitoring for a result is used when the
       App checks the system settings to see if the Location service is enabled.  If not, it launches
       Android's standard dialogue for enabling the service.  It catches the result in an
       ActivityResult instance and names it "result".  We then use it to determine how the user
       responded to the dialogue.
     */
    private ActivityResultLauncher<IntentSenderRequest> locationSettingsLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            // Location service was turned on by the User
            Log.d(APP_TAG, "Location service enabled by user.");
        } else {
            // User elected not to turn on Location service
            Log.d(APP_TAG, "Location service left disabled by user.");
            Snackbar.make(findViewById(R.id.a05_layout), R.string.a05_locationOff, Snackbar.LENGTH_LONG).show();
        }
    });

    /* The object that handles requesting permissions
       See GetLocation project for more details on the permission request process

       This is also an example of using the latest ActivityResultLauncher<> API in place of the
       deprecated startActivityForResult() method.

       "isGranted -> {}" Is an example of using a lambda to define a reaction rather than @Override
       a Class's predefined functions
     */
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a LocationRequest object that represents how often and how precise this App wants
        // location data to be
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setPriority(LOC_PRIORITY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) { return; }
                for (Location location : locationResult.getLocations()) {
                    // Manipulate each location in the Location list
                    Log.d(APP_TAG, "Location Updated");
                    setCoordinates(location);
                }
            }
        };

        // The button on a05_activity_main
        Button go = findViewById(R.id.a05_btn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLocationServiceOn();
                getLocationIfPermitted(view);
            }
        });
    }

    // This method launches a dialogue if the device's location service is off (i.e. an Android user
    // has turned of GPS/Location services in System Settings)
    private void confirmLocationServiceOn(){
        // Access the Settings Client and return a Task with the Location Settings in it
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(A05Main.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // Location is enabled.  You can check the LocationSettingsResponse object for exactly what
        // kind of Location services are available
        task.addOnSuccessListener(A05Main.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // This is another entry point to begin using the Location service.  Entering here
                // means that GPS is on on the device.  You'll still want to request proper permissions
                // first though.
                Log.d(APP_TAG, "GPS is on? " + String.valueOf(locationSettingsResponse.getLocationSettingsStates().isGpsUsable()));
            }
        });

        // When our Task fails, Location service is disabled.  We can use the expected Exception the
        // failure throws to launch a standard dialogue asking the User to turn it on.
        task.addOnFailureListener(A05Main.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but the user can fix this by enabling
                    // the service
                    ResolvableApiException exception = (ResolvableApiException) e;
                    /* ResolvableApiException.getResolution() returns a PendingIntent, which
                       IntentSenderRequest has a .Builder() to turn it into an IntentSenderRequest
                       with.  We can then feed this IntentSenderRequest into our ActivityResultLauncher
                       that's set to handle this specific Class as denoted by its <IntentSenderRequest>
                       qualifier.
                     */
                    IntentSenderRequest requestEnableLocation = new IntentSenderRequest.Builder(exception.getResolution()).build();
                    locationSettingsLauncher.launch(requestEnableLocation);
                }
            }
        });
    }

    private void getLocationIfPermitted(View view){
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

    // Suppress MissingPermission warning since we handle that before calling this method
    @SuppressLint("MissingPermission")
    // Uses the Google Play service's Fused Location Provider to access Location data first using
    // the most efficient method: .getLastLocation()
    private void getPriorCoordinates() {
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
                            setCoordinates(location);
                        } else {
                            // Prior location data does not exist, attempt to pull Current data
                            getCurrentCoordinates(fusedLocationClient);
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentCoordinates(FusedLocationProviderClient client) {
        client.getCurrentLocation(LOC_PRIORITY, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            //Log.d("Current Location Success", location.toString());
                            setCoordinates(location);
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

    private void setCoordinates(Location location) {
        TextView tView = findViewById(R.id.a05_txt_latitude);
        tView.setText(String.valueOf(location.getLatitude()));
        tView = findViewById(R.id.a05_txt_longitude);
        tView.setText(String.valueOf(location.getLongitude()));
        tView = findViewById(R.id.a05_txt_timestamp);
        Date date = new Date(location.getTime());
        tView.setText(date.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(requestingLocationUpdates) { startLocationUpdates(); }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() { fusedLocationClient.removeLocationUpdates(locationCallback); }
}