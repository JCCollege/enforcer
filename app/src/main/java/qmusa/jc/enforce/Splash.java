package qmusa.jc.enforce;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {

    Button btnFinish;
    Context context = this;
    SharedPreferences shared_preferences;
    SharedPreferences.Editor editor;
    EditText edit_staffName, edit_staffCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        edit_staffName = (EditText)findViewById(R.id.edit_staffName);
        edit_staffCode = (EditText)findViewById(R.id.edit_staffCode);

        shared_preferences = getSharedPreferences(getResources().getString(R.string.jcc_shared_prefs), Context.MODE_PRIVATE);
        editor = shared_preferences.edit();

        btnFinish = (Button)findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (!(edit_staffName.getText().length()<3) && !(edit_staffCode.getText().length()<3) && edit_staffCode.getText().toString().matches("[A-Za-z]{3}")){
                // Perform action on click
                editor.putString("Registered", "True");
                editor.putString("StaffName" , edit_staffName.getText().toString());
                editor.putString("StaffCode" , edit_staffCode.getText().toString().toUpperCase());
                editor.commit();
                finish();
            } else {
                Snackbar snack = Snackbar.make(v, "The fields are not correctly filled in. Please check that all fields are filled in.", Snackbar.LENGTH_LONG);
                ViewGroup group = (ViewGroup) snack.getView();
                group.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
                snack.show();
            }
            }
        });

    }
}
