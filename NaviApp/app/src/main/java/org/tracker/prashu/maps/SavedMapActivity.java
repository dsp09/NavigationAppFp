package org.tracker.prashu.maps;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

import org.tracker.prashu.navigationApp.R;

import static org.tracker.prashu.maps.MapsActivity.strSeparator;

public class SavedMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    public String id;
    public String name;
    public String startLat;
    public String startLon;
    public String currentLat;
    public String currentLon;
    public String polylineArray;
    public String duration;
    public String distance;
    public String color;
    String[] polylinesArr;


    TextView tvName;
    TextView tvDuration;
    TextView tvDistance;
    FloatingActionButton backButtonOnSavedMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_map1);
        // setting the map fragment by referencing to the id in xml file.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // map fragment get the map async as this for the on request callback.
        mapFragment.getMapAsync(this);

        tvName = (TextView)findViewById(R.id.savedMapName);
        tvDistance = (TextView)findViewById(R.id.savedMapDistance);
        tvDuration = (TextView)findViewById(R.id.savedMapDuration);
        backButtonOnSavedMap = (FloatingActionButton)findViewById(R.id.backButtonOnSavedMap);

        backButtonOnSavedMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on pressing the back button the activity should finish.
                finish();
            }
        });

        // getting some values from the intent

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        startLat = getIntent().getStringExtra("startLat");
        startLon = getIntent().getStringExtra("startLon");
        currentLat = getIntent().getStringExtra("currentLat");
        currentLon = getIntent().getStringExtra("currentLon");
        polylineArray = getIntent().getStringExtra("polylineArray");
        duration = getIntent().getStringExtra("duration");
        distance = getIntent().getStringExtra("distance");
        color = getIntent().getStringExtra("color");


        polylinesArr = convertStringToArray(polylineArray);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // create source and destination markers and draw the path between them.
        createCustomMarker(startLat, startLon, "Source",BitmapDescriptorFactory.HUE_RED);
        createCustomMarker(currentLat, currentLon, "Destination",BitmapDescriptorFactory.HUE_AZURE);
        createPath(polylinesArr);





        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                // create the LatLng bounds for the starting LatLng and ending LatLng then build the bounds and animate the camera to the bounds.
                // and set the padding of the bound as 250dp.
                LatLng startLatlang = new LatLng(Double.parseDouble(startLat), Double.parseDouble(startLon));
                LatLng currentLatlang = new LatLng(Double.parseDouble(currentLat), Double.parseDouble(currentLon));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(startLatlang);
                builder.include(currentLatlang);
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));

            }
        });


        // set the textviews with the distance and duration values calculated.
        tvName.setText(name);
        tvDuration.setText("Duration: "+duration+"sec");
        tvDistance.setText("Distance: "+distance+"m    ");
    }


    // method to create the custom marker with the passed latitute, longitude, title and colour.
    public void createCustomMarker(String latitute, String longitude, String title, Float color) {

        // create marker options on position of latlang with passed titles and colour then add the markerOptions to the marker.
        LatLng latLng = new LatLng(Double.parseDouble(latitute), Double.parseDouble(longitude));
        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));
        mMap.addMarker(markerOptions);
    }


    // method to create the path of single polyline on the map by taking as argument as string array.
    public void createPath(String[] polylinePaths) {
        for (int k = 0; k < polylinePaths.length; k++) {
            // creating the polyline options object and having colour as passed colour and width of path as 13dp.
            PolylineOptions options = new PolylineOptions();
            options.color(Color.parseColor(color));
            options.width(13);
            options.addAll(PolyUtil.decode(polylinePaths[k]));
            // add the options to map.
            mMap.addPolyline(options);
        }
    }

    // method that converts string into array by using the split method.
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }
}
