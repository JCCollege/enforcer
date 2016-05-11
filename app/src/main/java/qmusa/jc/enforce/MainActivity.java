package qmusa.jc.enforce;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context = this;
    EditText edit_idNumber;
    String str_stud_num = null, str_id, str_info, str_incident;
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
    Gson gson;
    View v;
    static int bool_uploaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise main method
        initialise();
        v = new View(context);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.mipmap.error);
                    builder.setMessage("Permission denied to read barcode via the camera! Grant permission via settings or re-install the application.")
                            .setTitle("Camera Permission Denied!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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

    public void onResume(){
        super.onResume();
        if (shared_preferences.getString("Registered",null) == null) {
            Intent intent = new Intent(context, Splash.class);
            startActivity(intent);
        }
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
                uploadFirebase(v);
            }
        });

        btn_clear = (Button)findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new AlertDialog.Builder(context)
                        .setTitle("Clear fields")
                        .setMessage("Clear all fields?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //mScannerView.resumeCameraPreview(this);
                                clearData();
                            };
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
               clearData();
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

    public void uploadFirebase(final View v){

        calendar = Calendar.getInstance();
        str_id = edit_idNumber.getText().toString();
        str_info = edit_exinfo.getText().toString();
        str_incident = spinner_incident.getSelectedItem().toString();
        payload.put("Student ID number",str_id);
        payload.put("Incident Information",str_incident);
        payload.put("Extra Info",str_info);

        if (firebaseAuthData == null){
            initFirebase();
            Snackbar.make(v, "Still loading. Please wait...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            if (uploadCheck() == 0) {
                new AlertDialog.Builder(this)
                    .setTitle("Upload data?")
                    .setMessage("Are you sure you would like to submit this incident?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //mScannerView.resumeCameraPreview(this);
                            try {
                                myFirebaseRef.child("users").child(shared_preferences.getString("StaffCode",null)).child(firebaseAuthData.getUid()).child("Incident(s)").child(calendar.getTime().toString()).setValue(payload);

                                Snackbar snack = Snackbar.make(v, "Details of the incident have been submitted.", Snackbar.LENGTH_LONG);
                                ViewGroup group = (ViewGroup) snack.getView();
                                group.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen));
                                snack.show();
                                clearData();
                            } catch (Exception E) {
                                Snackbar snack = Snackbar.make(v, "Failed to upload!", Snackbar.LENGTH_LONG);
                                ViewGroup group = (ViewGroup) snack.getView();
                                group.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
                                snack.show();
                                Log.e("firebase upload failed:", E.toString());
                            }
                        };
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Snackbar.make(v, "Upload cancelled.", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            } else if (uploadCheck() == 1) {
                Snackbar snack = Snackbar.make(v, "ID number not scanned!", Snackbar.LENGTH_LONG);
                ViewGroup group = (ViewGroup) snack.getView();
                group.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
                snack.show();
            } else if (uploadCheck() == 2) {
                Snackbar snack = Snackbar.make(v, "Info field not correctly filled in", Snackbar.LENGTH_LONG);
                ViewGroup group = (ViewGroup) snack.getView();
                group.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
                snack.show();
            }
        }

    }

    public void clearData(){
        edit_exinfo.setText("");
        edit_idNumber.setText("");
    }

    public int uploadCheck(){
        if (str_id.length() != 6)
            return 1;
        else if (str_info.length() <1)
            return 2;
        else
            return 0;

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
