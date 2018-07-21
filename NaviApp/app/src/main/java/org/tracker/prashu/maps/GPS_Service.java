package org.tracker.prashu.maps;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

// a class extending the service class and implements connection callback
public class GPS_Service extends Service implements GoogleApiClient.ConnectionCallbacks {

    public LocationListener locationListener;
    public static GoogleApiClient mClient;
    private LocationRequest locationRequest;
    public final int REQUEST_CODE = 0;
    public final int FLAG = 0;
    String minDistanceChangeFromSettings = "1";


    // method to create the api client by attaching the connection call back and adding the location servieces api
    protected synchronized void buildGoogleApiClient() {

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mClient.disconnect();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // calling the method to create the api client and then connect it.
        buildGoogleApiClient();
        mClient.connect();

        // creating the object of the location listener.
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // firing an intent for sending the broadcast and putting in extras the new location.
                Intent intent = new Intent("GPS_location_update");
                intent.putExtra("myLocation", location);
                sendBroadcast(intent);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get the min update distance from the intent on start of the activity.
        minDistanceChangeFromSettings = String.valueOf(intent.getExtras().get("minDistanceChangeFromSettings"));

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // on connected create the location request for the distance update interval as the user sets it and priority as balanced power accuracy
        // which consumes less battery.
        locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(100);
        locationRequest.setSmallestDisplacement(Integer.parseInt(minDistanceChangeFromSettings));
        locationRequest.setMaxWaitTime(1800000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // check if any active internet connectivity is there.
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        // if there is an active internet connectivity then request location updates for the previously set location request.
        if (netInfo != null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, locationRequest, locationListener);
        } else {
            // else prompt the user to connect to internet
            Toast.makeText(this, "connect to internet", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // disconnect the client when the activity gets destroyed.
        mClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


}




