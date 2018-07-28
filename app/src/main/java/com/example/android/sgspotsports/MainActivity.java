package com.example.android.sgspotsports;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Variable for navigation drawer
    private DrawerLayout drawer;

    // Code for handling error to launch maps
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Passing context
        //LocatorFragment mLocatorFragment = new LocatorFragment(this);

        // Start of code for navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
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

        switch(item.getItemId()) {
            case R.id.nav_locator:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LocatorFragment()).commit();
                break;

            case R.id.nav_planner:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PlannerFragment()).commit();
                break;

            case R.id.nav_kaki:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new KakiFragment()).commit();
                break;

            case R.id.nav_tracker:

                TrackerFragment trackerFragment = new TrackerFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container,
                        new TrackerFragment()).commit();
                break;

            case R.id.nav_feedback:

                Toast.makeText(this,"Opening Feedback Page", Toast.LENGTH_SHORT).show();
                // OPEN FEEDBACK PAGE

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
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
}
