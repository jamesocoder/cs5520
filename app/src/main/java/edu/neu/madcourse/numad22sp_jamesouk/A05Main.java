package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

public class A05Main extends AppCompatActivity {

    private Button go;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
               if (isGranted) {
                   // TODO Call method that edits textviews to display coordinates
               } else {
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
                    // TODO: Call method that edits textviews to display coordinates
                } else if (shouldShowRequestPermissionRationale(myPermission)) {
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
                    requestPermissionLauncher.launch(myPermission);
                }
            }
        });
    }
}