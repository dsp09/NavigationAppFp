package org.tracker.prashu.maps;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;

import org.tracker.prashu.navigationApp.MyDatabaseHelper;
import org.tracker.prashu.navigationApp.R;

import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.tracker.prashu.maps.GPS_Service.mClient;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        DataInterface {

    private GoogleMap mMap;
    private Location startLocation;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationListener locationListener;
    private MarkerOptions markerOptions;

    private String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?";
    private String origin = "origin";
    private String destination = "destination";
    private String waypoints = "waypoints";
    private String key = "key";

    private FloatingActionButton fabStart, fabStop, fabForceStop;
    TextView textView;
    static TextView textViewStatus;
    private static CoordinatorLayout myCoordinatorMapLayout;
    private LocationManager locationManager;
    public BroadcastReceiver broadcastReceiver;

    JSONArray stepsJsonArray;
    String[] polylines;
    String encodedPolylines = "";
    String latlongWaypoints = "";
    String directionUrl = "";
    StringBuffer sb;
    static String[] polylinePaths;
    public static String strSeparator = "__,__";
    private Integer minDistanceChangeFromSettings;
    private boolean isStartingMarked = false;
    private boolean isMapTrackingStarted = false;
    private Location extractedServiceLocation;

    // Database related data.
    protected static MyDatabaseHelper myDatabaseHelper;
    protected static String email;
    protected static String name;
    protected static String date;
    protected static String time;
    protected static double start_latitute = 0.0;
    protected static double start_longitude = 0.0;
    protected static double current_latitiude = 0.0;
    protected static double current_longitude = 0.0;
    protected static int distanceValue = 0;
    protected static int durationValue = 0;
    protected static String pathCustomColor;


    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    extractedServiceLocation = (Location) intent.getExtras().get("myLocation");
                    customLocationChanged(extractedServiceLocation);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("GPS_location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_maps);

        email = getIntent().getStringExtra("myUserEmail");
        pathCustomColor = getIntent().getStringExtra("customPathColorKey");
        minDistanceChangeFromSettings = getIntent().getIntExtra("minDistanceChangeFromSettings", 1);


        fabStart = (FloatingActionButton) findViewById(R.id.fabStart);
        fabStop = (FloatingActionButton) findViewById(R.id.fabStop);
        fabForceStop = (FloatingActionButton) findViewById(R.id.fabForceStop);
        fabStart.setEnabled(true);
        fabStop.setEnabled(false);

        fabForceStop.setVisibility(View.VISIBLE);

        myCoordinatorMapLayout = (CoordinatorLayout) findViewById(R.id.myCoordinatorMapLayout);
        textView = (TextView) findViewById(R.id.myTextCardMap);
        textViewStatus = (TextView) findViewById(R.id.myTextCardMapStatus);
        textViewStatus.setText("STATUS: Stopped.");
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isGpsOn()) {
                    if (!isMapTrackingStarted) {

                        isMapTrackingStarted = true;
                        textViewStatus.setText("STATUS: Tracking...");

                        current_latitiude = start_latitute;
                        current_longitude = start_longitude;

                        mMap.clear();

                        Snackbar.make(myCoordinatorMapLayout, "Map Tracker Started Successfully!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Intent gpsServiceIntent = new Intent(MapsActivity.this, GPS_Service.class);
                        gpsServiceIntent.putExtra("minDistanceChangeFromSettings",minDistanceChangeFromSettings);
                        startService(gpsServiceIntent);

                    } else
                        Snackbar.make(myCoordinatorMapLayout, "Map is already tracking...", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                }else {
                    createGpsConnectionDialog();
                }

            }
        });

        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mClient.isConnected()) {

                    String currentTitle = "Distance: " + distanceValue + "m, Duration: " + durationValue + "sec";
                    textView.setText("Location: " + distanceValue + "m , " + durationValue + "sec");
                    createCustomMarker(current_latitiude, current_longitude, currentTitle, BitmapDescriptorFactory.HUE_AZURE);


                    MyDialogBoxOnStopTracking dialogBoxOnStopTracking = new MyDialogBoxOnStopTracking();
                    dialogBoxOnStopTracking.setContext(MapsActivity.this);
                    dialogBoxOnStopTracking.show(getSupportFragmentManager(), "RouteNameDialog");
                    fabStart.setEnabled(false);


                } else {
                    Snackbar.make(myCoordinatorMapLayout, "No started route to stop!!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        fabForceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(0f));

        myDatabaseHelper = new MyDatabaseHelper(MapsActivity.this);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

    }


    public void createCustomMarker(Double latitude, Double longitude, String title, Float markerColor) {

        //BitmapDescriptorFactory.HUE_GREEN
        int cameraPadding = 300;

        LatLng currentlatLng = new LatLng(latitude, longitude);
        LatLng startLatLang = new LatLng(start_latitute, start_longitude);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startLatLang);
        builder.include(currentlatLng);
        LatLngBounds bounds = builder.build();


        markerOptions = new MarkerOptions();
        markerOptions.position(currentlatLng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColor));
        mMap.addMarker(markerOptions);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlatLng, 17f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, cameraPadding));
    }


    public void customLocationChanged(Location location) {

        if (!isStartingMarked) {
            try {
                start_latitute = location.getLatitude();
                start_longitude = location.getLongitude();
                isStartingMarked = true;

                Toast.makeText(MapsActivity.this, "Started", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(MapsActivity.this, "Sorry!! : " + e, Toast.LENGTH_LONG).show();
                Snackbar.make(myCoordinatorMapLayout, "Please Switch ON device GPS.", Snackbar.LENGTH_LONG).show();
            }

            // starting marker.
            createCustomMarker(start_latitute, start_longitude, "Source", BitmapDescriptorFactory.HUE_RED);

        } else {
            currentLocation = location;
            current_latitiude = location.getLatitude();
            current_longitude = location.getLongitude();


        /*directionUrl = baseUrl + origin + "=" + start_latitute + "," + start_longitude + "&"
                + destination + "=" + current_latitiude + "," + current_longitude + "&"
                + waypoints + "=via:" + encodedPolylines + "&"
                + key + "=" + getString(R.string.google_api_key_paras);


        directionUrl = baseUrl + origin + "=" + start_latitute + "," + start_longitude + "&"
                + destination + "=" + current_latitiude + "," + current_longitude + "&"
                + waypoints + "=via:" + latlongWaypoints + "&"
                + key + "=" + getString(R.string.google_api_key_paras);*/


            directionUrl = baseUrl + origin + "=" + start_latitute + "," + start_longitude + "&"
                    + destination + "=" + current_latitiude + "," + current_longitude + "&"
                    + key + "=" + getString(R.string.google_api_key_paras);


            new FetchMapData(MapsActivity.this, directionUrl, MapsActivity.this).execute();

            mMap.clear();
            // starting marker.
            createCustomMarker(start_latitute, start_longitude, "Source", BitmapDescriptorFactory.HUE_RED);

            // current marker.
            String currentTitle = "Distance: " + distanceValue/*(int) results[0]*/ + "m, Duration: " + durationValue + "sec";
            textView.setText("Location: " + distanceValue + "m , " + durationValue + "sec");
            createCustomMarker(current_latitiude, current_longitude, currentTitle, BitmapDescriptorFactory.HUE_AZURE);
            fabStop.setEnabled(true);
            fabForceStop.setVisibility(View.GONE);

        }
    }


    @Override
    public void onBackPressed() {
        try {
            if (mClient.isConnected()) {
                Snackbar.make(myCoordinatorMapLayout, "Please stop tracking to go back!!", Snackbar.LENGTH_LONG).show();
            } else if (!mClient.isConnected()) {
                super.onBackPressed();
                mMap.clear();
            }
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public void fetchedData(String data) {

        try {
            if (data != null) {
                JSONObject mainJosnObj = new JSONObject(data);
                String parsingStatus = mainJosnObj.getString("status");

                if (parsingStatus.equals("OK")) {
                    JSONArray routesJsonArray = mainJosnObj.getJSONArray("routes");

                    JSONArray legsJsonArray = routesJsonArray.getJSONObject(0).getJSONArray("legs");

                    stepsJsonArray = legsJsonArray.getJSONObject(0).getJSONArray("steps");

                    polylinePaths = getPaths(stepsJsonArray);

                    for (int k = 0; k < polylinePaths.length; k++) {
                        PolylineOptions options = new PolylineOptions();
                        options.color(Color.parseColor(pathCustomColor));
                        options.width(13);
                        options.addAll(PolyUtil.decode(polylinePaths[k]));

                        mMap.addPolyline(options);
                    }


                    for (int j = 0; j < routesJsonArray.length(); j++) {
                        JSONObject innerObj2 = legsJsonArray.getJSONObject(j);
                        JSONObject distanceObj = innerObj2.getJSONObject("distance");
                        JSONObject durationObj = innerObj2.getJSONObject("duration");
                        JSONObject endLocationObj = innerObj2.getJSONObject("end_location");


                        distanceValue = distanceObj.getInt("value");
                        durationValue = durationObj.getInt("value");

                        /*endLat = endLocationObj.getString("lat");
                        endLon = endLocationObj.getString("lng");
                        latlongWaypoints += endLat + "," + endLon + "%7C";*/

                        Toast.makeText(this, "" + distanceValue + " " + durationValue, Toast.LENGTH_SHORT).show();
                    }
                } else if (parsingStatus.equals("REQUEST_DENIED")) {
                    Toast.makeText(this, "REQUEST_DENIED", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //textView.setText("Location: " + distanceValue + "m , " + durationValue + "sec");
    }


    public String getPath(JSONObject googlePathJson) {
        String polyline = null;
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }


    public String[] getPaths(JSONArray googleStepsJson) {
        int count = googleStepsJson.length();

        sb = new StringBuffer("");
        polylines = new String[count];

        for (int i = 0; i < count; i++) {
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
                sb.append("enc:" + polylines[i] + ":");
                if (i < count - 1) {
                    sb.append("|");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        encodedPolylines = sb.toString();

        return polylines;
    }


    private boolean isGpsOn() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    protected void createGpsConnectionDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("App requires GPS to be switched ON to track your location seamlessly.")
                .setTitle("Need GPS Location")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(gpsIntent);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(), "We are sorry! We really need GPS enabled for this service.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                );
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    public static class MyDialogBoxOnStopTracking extends AppCompatDialogFragment {
        static Context mContext;
        EditText editText;

        public void setContext(Context context) {
            this.mContext = context;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Toast.makeText(mContext, "dialog is created", Toast.LENGTH_SHORT).show();

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // object of the AlertBox.
            LayoutInflater inflater = LayoutInflater.from(mContext);    // setting the layout inflater to inflate the layout (xml file)
            final View view = inflater.inflate(R.layout.on_stop_tracking_dialog_layout, null);   // inflating the layout for the activity.
            editText = (EditText) view.findViewById(R.id.etNameRoute);

            builder.setView(view);  // setting the view for the Alert box.
            builder.setPositiveButton("Save", null);     // positive button as save the data.
            builder.setNegativeButton("cancel", null);   // negative button as cancel the dialog box.
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCancelable(false);
            mAlertDialog.setCanceledOnTouchOutside(false);

            mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    // giving the positive and negative buttons as variable names.
                    Button pos = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button neg = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    // set onClickListener for the negative button.
                    neg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), "Route is NOT saved.", Toast.LENGTH_SHORT).show();
                            ((Activity) mContext).finish();
                            mAlertDialog.dismiss();

                        }
                    });
                    // set onClickListener for the positive button.
                    pos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            String routeName = editText.getText().toString();

                            textViewStatus.setText("STATUS: Stopped.");
                            if (routeName.isEmpty()) {
                                Toast.makeText(mContext, "Enter Valid Name", Toast.LENGTH_SHORT).show();
                            } else {
                                name = routeName;

                                Date today = new Date();
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
                                String dateToStr = format.format(today);
                                String[] dateTime = dateToStr.split(" ");
                                date = dateTime[0];
                                time = dateTime[1] + dateTime[2];
                                Snackbar.make(myCoordinatorMapLayout, "Map Tracker Stopped !!", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.WHITE)
                                        .setAction("Action", null).show();
                                insertDataInDatabase();
                                Toast.makeText(mContext, "Route Saved.", Toast.LENGTH_SHORT).show();
                                Intent gpsServiceIntent2 = new Intent(mContext, GPS_Service.class);
                                mContext.stopService(gpsServiceIntent2);
                                mAlertDialog.dismiss();
                                ((Activity) mContext).finish();
                            }
                        }
                    });
                }
            });
            return mAlertDialog;
        }

        private void insertDataInDatabase() {

            myDatabaseHelper = new MyDatabaseHelper(mContext);
            SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();

            // setting data to insert.

            String polylineBigString = convertArrayToString(polylinePaths);


            // inserting data.
            ContentValues cv = new ContentValues();
            cv.put(MyDatabaseHelper.EMAIL, email);
            cv.put(MyDatabaseHelper.NAME, name);
            cv.put(MyDatabaseHelper.DATE, date);
            cv.put(MyDatabaseHelper.TIME, time);
            cv.put(MyDatabaseHelper.START_LAT, start_latitute);
            cv.put(MyDatabaseHelper.START_LONG, start_longitude);
            cv.put(MyDatabaseHelper.CURRENT_LAT, current_latitiude);
            cv.put(MyDatabaseHelper.CURRENT_LONG, current_longitude);
            cv.put(MyDatabaseHelper.POLYLINE_ARRAY, polylineBigString);
            cv.put(MyDatabaseHelper.DURATION, durationValue);
            cv.put(MyDatabaseHelper.DISTANCE, distanceValue);
            cv.put(MyDatabaseHelper.COLOR, pathCustomColor);
            db.insert(MyDatabaseHelper.TABLE_NAME, null, cv);
        }


        public static String convertArrayToString(String[] array) {
            String str = "";

            try {
                for (int i = 0; i < array.length; i++) {
                    str = str + array[i];
                    // Do not append comma at the end of last element
                    if (i < array.length - 1) {
                        str = str + strSeparator;
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                return str;
            }
        }

    }// dialog class ends.


}// main class ends.

