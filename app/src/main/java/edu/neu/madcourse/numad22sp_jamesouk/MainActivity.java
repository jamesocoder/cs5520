package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.a01:
                displayMe();
        }
    }

    // Source: https://developer.android.com/guide/topics/ui/notifiers/toasts#java
    public void displayMe() {
        Context context = getApplicationContext();
        CharSequence aboutMe = "James Ouk - ouk.j@northeastern.edu";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, aboutMe, duration);
        toast.show();
    }
}