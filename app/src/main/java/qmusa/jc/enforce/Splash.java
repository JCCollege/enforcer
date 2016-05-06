package qmusa.jc.enforce;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {

    Button btnFinish;
    Context context = this;
    SharedPreferences shared_preferences;
    SharedPreferences.Editor editor;
    final String jcc_prefs = "JCCPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        shared_preferences = getSharedPreferences(jcc_prefs, Context.MODE_PRIVATE);
        editor = shared_preferences.edit();

        btnFinish = (Button)findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                editor.putString("Registered", "True");
                editor.commit();
                finish();
            }
        });


    }
}
