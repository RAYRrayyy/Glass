package com.example.jimy_cai.virtual;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        OnMapReadyCallback {
    //private TextView textLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Button bSatellite;
    private Button bNormal;
    private Boolean mapReady = false;
    private GoogleMap gMap;
    private boolean initialCameraSet = true;

    // TAG
    private final String LOG_TAG = "VirtualTestApp";
    // Declare Hotspots name and coordinates for map
    private final String spotNames[] = {"TP2-Labs", "3010 Rover Memorial", "UQ Lakes", "Regatta"};
    private final LatLng hotSpots[] = {
            new LatLng(-27.500097, 153.014526),
            new LatLng(-27.499996, 153.015170),
            new LatLng(-27.500231, 153.015988),
            new LatLng(-27.483230, 152.996814)
    };

    // Activity callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // Text view
        //textLocation = (TextView) findViewById(R.id.tv_location);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bSatellite = (Button) findViewById(R.id.Satellite_button);
        bNormal = (Button) findViewById(R.id.Normal_button);

    }

    /*** Activity Life cycle functions ***/

    @Override
    protected void onStart() {
        super.onStart();
        // Attempt to Connect
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Disconnect Api Client
        mGoogleApiClient.disconnect();
    }


    /*** Google Location Services callbacks ***/
    // Google API callbacks
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(4000); // Update location every 4 second
        //Retrieve current location
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "Connection Failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "Connection Suspended");
    }

    // On Location Change callback
    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, "Location Change");
        Location target = new Location("target");
        String closePoints = "";

        // Focus camera on initial location
        if(mapReady == true && initialCameraSet == true) {
            LatLng initialLocation = new LatLng( location.getLatitude(), location.getLongitude());
            gMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));
            initialCameraSet = false; // Initial location already displayed
        }
        // Check if spot is close
        for (int i = 0; i < hotSpots.length; ++i) {
            target.setLatitude(hotSpots[i].latitude);
            target.setLongitude(hotSpots[i].longitude);
            if (location.distanceTo(target) < 1500) {
                closePoints = closePoints + spotNames[i] + "\n";
            }
        }
        Toast.makeText(this, "Close Spots:\n\n" + closePoints, Toast.LENGTH_LONG).show();
    }

    // MAP UI CALLBACKS
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        gMap = googleMap;
        for (int i = 0; i < hotSpots.length; ++i) {
            googleMap.addMarker(new MarkerOptions().position(hotSpots[i]).title(spotNames[i]));
        }
        gMap.setMyLocationEnabled(true);
    }


// UI interactive functions

    /*** Sets the current view of the map to a satellite view ***/
    public void setSatellite(View view) {
        if (mapReady) {
            gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }

    /*** Sets the current view of the map to a normal view ***/
    public void setNormal(View view) {
        if (mapReady) {
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }
}