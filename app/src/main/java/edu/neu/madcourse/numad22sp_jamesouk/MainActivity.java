package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

        This method handles view clicking on the main activity.  It currently expects all button
        clicks to lead to new Activities, thus it defines an Intent outside of the switch statement,
        assigns it in the switch, and then launches it after the switch. If a button doesn't have an
        Activity coded for it, the method will do nothing.
     */
    public void onClick(View view){
        Intent launch;
        switch (view.getId()){
            case R.id.a01:
                launch = new Intent(this, A01.class);
                break;
            case R.id.a03:
                launch = new Intent(this, A03.class);
                break;
            case R.id.a04:
                launch = new Intent(this, A04Main.class);
                break;
            case R.id.a05:
                launch = new Intent(this, A05Main.class);
                break;
            default:
                return;
        }
        startActivity(launch);
    }
}