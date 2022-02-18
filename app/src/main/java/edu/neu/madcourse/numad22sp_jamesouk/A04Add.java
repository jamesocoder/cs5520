package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class A04Add extends AppCompatActivity {

    Intent dataOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a04_activity_add);

        dataOut = new Intent();
    }

    /* To return values to a parent Activity, we must:
       1) Create an Intent that has extras, Key:Value pairs of data sent across Activities
       2) Call Activity.setResult() passing in an appropriate Intent.RESULT_ code and the Intent we
          packed with data.
       3) Call super.finish() to then make this result object available to be parsed with the parent
          activity's onActivityResult() method.
     */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_add:
                EditText dataIn = findViewById(R.id.edit_url);
                /* I decided that we should only check URLs for a non-empty, valid string since the
                   Titles aren't used for anything important; titles can be whatever.
                 */
                if(validUrl(dataIn)) {
                    dataOut.putExtra("url", dataIn.getText().toString());
                    dataIn = findViewById(R.id.edit_title);
                    dataOut.putExtra("title", dataIn.getText().toString());
                    setResult(RESULT_OK, dataOut);
                } else {
                    Toast.makeText(A04Add.this, "ERROR: Bad URL.  Press the add button to try again.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED, dataOut);
                }
                super.finish();
                break;
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED, dataOut);
                super.finish();
                break;
            default:
                return;
        }
    }


    private boolean validUrl(EditText url) {
        String s = url.getText().toString();
        if (s.length() == 0) {return false;}
        if (!s.startsWith("http")) {return false;}
        // Unable to find an efficient way to check all the possibilities for the ".com/.net/etc."
        // part of a URL. Can't just check ending characters because there could be more after ".com"
        return true;
    }
}