package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActA02 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_a02);
    }

    /*
        Obtains references to the Views that need to be interacted with: the pressed button and the
        TextView of the Activity.
        We then use member methods to change the text displayed in the TextView.
     */
    public void onClick(View view){
        Button btn = (Button) findViewById(view.getId());
        TextView pressed = (TextView) findViewById(R.id.txt_pressed);

        /*
            In order to work with translations (stored in the XML resources), pull the currently
            displayed text out of the Views and use them instead of hard-coded Strings.
         */
        String str = getString(R.string.txt_pressed);
        pressed.setText(
                str.replace(
                        "-",
                        btn.getText().toString()
                )
        );
    }
}