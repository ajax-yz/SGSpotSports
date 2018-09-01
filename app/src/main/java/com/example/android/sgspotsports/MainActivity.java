package com.example.android.sgspotsports;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Variable for navigation drawer
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;

    private DatabaseReference mUserRef;

    private Toolbar mToolbar;

    // Code for handling error to launch maps
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // Passing context to fragments
    public static Context contextOfApplication;
    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        // Passing context
        contextOfApplication = getApplicationContext();

        // Start of code for navigation drawer
        mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        // Set title of toolbar (doesn't work)
        // getSupportActionBar().setTitle("Sports Facility Locator");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sports Facility Locator");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LocatorFragment()).commit();
            //if (isServicesOK()) {
                navigationView.setCheckedItem(R.id.nav_locator);
            //}
        }
        // End of code for navigation drawer

    }

    // OnActivityResult

    @Override
    // Code for navigation drawer
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Clear all the back stack
        //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        switch(item.getItemId()) {
            case R.id.nav_locator:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,
                        new LocatorFragment()).commit();
                break;

            case R.id.nav_planner:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,
                        new PlannerFragment()).commit();
                break;

            case R.id.nav_kaki:

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container,
                                    new KakiFragment()).commit();
                } else {
                    Toast.makeText(this,"Please login first to access the kaki function", Toast.LENGTH_SHORT).show();
                }


                break;

            /*
            case R.id.nav_tracker:

                TrackerFragment trackerFragment = new TrackerFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                .add(new LocatorFragment(), "LocatorFragment")
                        .addToBackStack("LocatorFragment")
                .replace(R.id.fragment_container,
                        new TrackerFragment()).commit();
                break;

             */

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,
                        new LogInFragment()).commit();
                break;

            case R.id.nav_feedback:

                Toast.makeText(this,"Opening Feedback Page", Toast.LENGTH_SHORT).show();
                // OPEN FEEDBACK PAGE

                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,
                        new FeedbackFragment()).commit();

                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    // Code for navigation drawer
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            // everything is ine and the user can make map requests
            Log.d(TAG, "isServicessOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // an error occured but can be resolved
            Log.d(TAG, "isServicesOK: an errr occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available,ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    // FOR THE LOGOUT BUTTON
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager manager = getSupportFragmentManager();

        switch (item.getItemId()) {

            case R.id.menuLogout:

                FirebaseAuth.getInstance().signOut();

                Toast.makeText(this, "You have been successfully logged out", Toast.LENGTH_SHORT).show();

                //finish();
                manager.beginTransaction().replace(R.id.fragment_container,
                        new LogInFragment()).commit();

                break;

            case R.id.main_setting:

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    manager.beginTransaction().replace(R.id.fragment_container,
                            new SettingsFragment()).commit();
                } else {
                    Toast.makeText(this,"Please login first to access the account settings", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.main_all_btn:

                manager.beginTransaction().replace(R.id.fragment_container,
                        new UsersFragment()).commit();

                break;
        }



        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {

            // Forces users to login, CAN EXCLUDE THE CODE. BUT LOOKS GOOD
            sendToLogin();

        } else {

            mUserRef.child("online").setValue("true");
        }
    }

    // Whenever app is minimise
    @Override
    protected void onStop() {
        super.onStop();

        if (mAuth.getCurrentUser() != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            //mUserRef.child("online").setValue(false);
            //mUserRef.child("last_seen").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToLogin() {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container,
                new LogInFragment()).commit();

    }

    /* Consider to check if the user is currently logged in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
        FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container,
                        new LogInFragment()).commit();
                        finish();
        }

        updateUI(currentUser);
    }
    */

}
