package org.tracker.prashu.navigationApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.tracker.prashu.maps.MapsActivity;
import org.tracker.prashu.savedRoutes.SavedRoutesActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    // initialization of the variables

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    TextView tvUserWelcome1;
    TextView tvUserWelcome2;
    TextView tvUserWelcome3;
    Button startBtn, savedBtn, SettingsBtn;
    public static final int RC_SIGN_IN = 1;
    public static final int PERMISSION_REQUEST_CODE = 101;
    public static final int SETTINGS_REQUEST = 102;
    private CoordinatorLayout coordinatorLayout;
    public String userName;
    public String userEmail;
    String userDefinedColor;
    Integer minDistanceChangeFromSettings = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // setting the default color as black
        userDefinedColor = "#000000";
        // creating object with reference to id from the xml file
        tvUserWelcome1 = (TextView) findViewById(R.id.welcomeUserText);
        tvUserWelcome2 = (TextView) findViewById(R.id.welcomeUserText2);
        tvUserWelcome3 = (TextView) findViewById(R.id.welcomeUserText3);

        startBtn = (Button) findViewById(R.id.startRouteBtn);
        savedBtn = (Button) findViewById(R.id.savedRoutesBtn);
        SettingsBtn = (Button) findViewById(R.id.settingsBtn);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.homeCoordinatorLayout);
        startBtn.setOnClickListener(this);
        savedBtn.setOnClickListener(this);
        SettingsBtn.setOnClickListener(this);
         //getting instance to firebase authentication
        mFirebaseAuth = FirebaseAuth.getInstance();

        // firebase listener for the change in auth state.
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // getting the current user after the firebase authentication
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //geting the username of the current signed-in user.
                    userName = user.getDisplayName();
                    //getting the email of the current signed-in user.
                    userEmail = user.getEmail();


                    tvUserWelcome1.setText("Welcome: " + userName);
                    tvUserWelcome2.setBackgroundColor(Color.parseColor(userDefinedColor));
                    tvUserWelcome3.setText("Update Distance: " + minDistanceChangeFromSettings + "m");


                } else {
                    // start the activity for result for creating the authentication UI for signing-in
                    // creating the UI for the email and google login.
                    // and setting the smart lock emabled.
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // if request code is for sign in and the result is ok then user signs in successfully.
                Toast.makeText(this, "Signed-In", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // else the reason for signing in may be mostly internet connectivity.
                Toast.makeText(this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == SETTINGS_REQUEST) {
            try {
                // getting the colour and min update distance from the intent if settings request comes.
                userDefinedColor = data.getStringExtra("selectedColorStringKey");
                minDistanceChangeFromSettings = data.getIntExtra("minDistanceChangeFromSettings", 1);

                tvUserWelcome2.setBackgroundColor(Color.parseColor(userDefinedColor));
                tvUserWelcome3.setText("Update Distance: " + minDistanceChangeFromSettings);
            } catch (Exception e) {
                // Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflating the menu created in resources file in menu directory.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                //sign-out a logged in user from the app.
                AuthUI.getInstance().signOut(this);
                break;

            case R.id.exit_menu:
                // first sign-out from the app and then exits.
                AuthUI.getInstance().signOut(this);
                Toast.makeText(this, "Thank You for using my App", Toast.LENGTH_SHORT).show();
                this.finishAffinity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // on resume add the auth state listener.
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // on pause remove the auth state listener.
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startRouteBtn:
                //on click of button to start new route this method is called
                startNewRoute();
                break;
            case R.id.savedRoutesBtn:
                //button to show saved routes offline
                Intent savedRoutesIntent = new Intent(HomeActivity.this, SavedRoutesActivity.class);
                savedRoutesIntent.putExtra("emailKey", userEmail);
                startActivity(savedRoutesIntent);
                break;
            case R.id.settingsBtn:
                //show settings.
                Intent settingsIntent = new Intent(HomeActivity.this, MySettingsActivity.class);
                startActivityForResult(settingsIntent, SETTINGS_REQUEST);
                break;

        }
    }

// method called on pressing the start new route button
    private void startNewRoute() {

        if (Build.VERSION.SDK_INT < 23) {
           //keeps the path color distance and user email to the mapsintent
            Intent mapsIntent = new Intent(HomeActivity.this, MapsActivity.class);
            mapsIntent.putExtra("myUserEmail", userEmail);
            mapsIntent.putExtra("customPathColorKey", userDefinedColor);
            mapsIntent.putExtra("minDistanceChangeFromSettings", minDistanceChangeFromSettings);
            startActivity(mapsIntent);
        } else {
            if (checkAndRequestPermissions()) {
                //If you have already permitted the permission

            }
        }
    }


    private boolean checkAndRequestPermissions() {
        if (isMyPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) && isMyPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //if all the permissions are granted then snackbar comes up.
            Snackbar.make(coordinatorLayout, "All Required Permissions Granted.", Snackbar.LENGTH_SHORT).show();
            // firing intent to the MapsActivity and putting in extras as the user email, path colour and min distance update.
            Intent mapsIntent = new Intent(HomeActivity.this, MapsActivity.class);
            mapsIntent.putExtra("myUserEmail", userEmail);
            mapsIntent.putExtra("customPathColorKey", userDefinedColor);
            mapsIntent.putExtra("minDistanceChangeFromSettings", minDistanceChangeFromSettings);
            startActivity(mapsIntent);
            // return true if the permissions are granted.
            return true;
        } else {
            // if permissions are not granted then request user for fine location and external write permissions.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        // return false if the permissions are not granted.
        return false;
    }

    // method that checks whether permission is granted or not.
    public boolean isMyPermissionGranted(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

            }
            return false;
        }
        return true;
    }

}
