package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
        A method becomes compatible for use with a View's onClick Event when a View is the only
        parameter in its declaration.
        Source: https://developer.android.com/training/basics/firstapp/starting-activity#java
     */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.a01:
                displayMe();
                break;
            case R.id.a03:
                // Open Assignment 2's Activity
                Intent startA02 = new Intent(this, A03.class);
                startActivity(startA02);
                break;
        }
    }

    // Display a toast notification supplying my information
    // Source: https://developer.android.com/guide/topics/ui/notifiers/toasts#java
    public void displayMe() {
        Context context = getApplicationContext();
        CharSequence aboutMe = "James Ouk - ouk.j@northeastern.edu";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, aboutMe, duration);
        toast.show();
    }
}