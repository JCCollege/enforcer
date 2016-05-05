package qmusa.jc.enforce;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context = this;
    EditText edit_idNumber;
    String str_stud_num = null;
    Button btn_submit, btn_clear, btn_br;
    EditText edit_staff_id, edit_exinfo;
    Spinner spinner_incident;
    Firebase myFirebaseRef;
    Map<String, String> payload;
    AuthData firebaseAuthData;
    Calendar calendar;
    final String jcc_prefs = "JCCPrefs";
    SharedPreferences shared_preferences;
    SharedPreferences.Editor editor;
    String fireAuthStr;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialise();
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * @author Qasim Musa
     */
    public void initialise(){

        //initialise variables.
        shared_preferences = getSharedPreferences(jcc_prefs, Context.MODE_PRIVATE);
        editor = shared_preferences.edit();
        gson = new Gson();
        calendar = Calendar.getInstance();

        initFirebase();

        //validate if firebase authcode is stored in device.
        //validateFireAuth();

        //Find all EditTexts
        payload = new HashMap<String,String>();
        edit_staff_id = (EditText)findViewById(R.id.edit_staff_id);
        edit_exinfo = (EditText)findViewById(R.id.edit_exinfo);
        edit_idNumber = (EditText)findViewById(R.id.edit_id);
        spinner_incident = (Spinner)findViewById(R.id.spinner_incident);

        //Find all Buttons and initialise button clicks.
        btn_submit = (Button)findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                uploadFirebase();
            }
        });

        btn_clear = (Button)findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                edit_staff_id.setText("");
                edit_exinfo.setText("");
                edit_idNumber.setText("");
            }
        });

        btn_br = (Button) findViewById(R.id.btn_launchBR);
        btn_br.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(context, LaunchBarcode.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    public void initFirebase(){
        Firebase.setAndroidContext(this);
//        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        myFirebaseRef = new Firebase("https://shining-inferno-9227.firebaseio.com/");

        myFirebaseRef.authAnonymously(new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                firebaseAuthData = authData;
                String json = gson.toJson(authData);
                editor.putString("fireAuthStr",json);
                editor.commit();
                Log.i("Firebase anonAuth", "Successfully logged in anonymously. uid: "+ authData.getUid().toString());
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.e("Firebase anonAuth", "Error logging in.");
            }
        });

        myFirebaseRef.keepSynced(true);
    }

//    public void validateFireAuth(){
//        if (!shared_preferences.contains("fireAuthStr")){
//            initFirebase();
//        } else {
//            Log.e("on else","running this");
//            String json = shared_preferences.getString("fireAuthStr",null);
//            AuthData authdata_convert = gson.fromJson(json, AuthData.class);
//            firebaseAuthData = authdata_convert;
//
//        }
//        //Initialise Firebase.
//
//    }

    public void uploadFirebase(){
        calendar = Calendar.getInstance();
        String str_id = edit_idNumber.getText().toString();
        String str_info = edit_exinfo.getText().toString();
        String str_staff = edit_staff_id.getText().toString();
        String str_incident = spinner_incident.getSelectedItem().toString();
        payload.put("Staff ID number",str_staff);
        payload.put("Student ID number",str_id);
        payload.put("Incident Information",str_incident);
        payload.put("Extra Info",str_info);

        if (firebaseAuthData == null){
            initFirebase();
        } else {
            myFirebaseRef.child("users").child(firebaseAuthData.getUid()).child(calendar.getTime().toString()).setValue(payload);
        }

//        myFirebaseRef.child("Staff ID number").setValue(str_staff);
//        myFirebaseRef.child("Student ID number").setValue(str_id);
//        myFirebaseRef.child("Incident Information").setValue(str_incident);
//        myFirebaseRef.child("Extra Info").setValue(str_info);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                str_stud_num = data.getStringExtra("barcode");
                if (str_stud_num.matches("[a-zA-Z]{1}[0-9]{5}")){
                    edit_idNumber.setText(str_stud_num);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Error!")
                            .setMessage("The barcode you scanned doesn't appear to be that of a JCC student! Please scan the barcode again.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //cancel OR in other words don't do anything, as there is nothing to do here! simple.
                                }
                            })
                            .setIcon(R.mipmap.error)
                            .show();
                }
            }

        }
    }
}
