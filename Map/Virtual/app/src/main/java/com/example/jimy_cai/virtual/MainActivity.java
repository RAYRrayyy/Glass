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
    private final String spotNames[] = {
            // Transports
            "UQ Chancellor's Place",
            "UQ Lakes Bus Stop",
            "Citycat Stop - Brisbane River",

            //Help
            "Student Services",
            "UQ Security",

            //Landmarks
            "UQ Great Court",
            "UQ Lakes Area",
            "Coop Bookshop",
            "Schonell Theater - Pizza Cafe",
            "UQ Centre",
            "GP South - 3010 Rover Memorial",

            // Buildings with Libraries
            "Forgan Smith - Law Library",
            "Biological Science Library",
            "Hawken Engineering Library",
            "Zelman Cowen - Architecture/Music Library",
            "Duhig North - Social Sciences Building Library",

            // Buildings with Museums
            "Parnell Building - Physics Museum",
            "Michie Building - Anthropology Museum",
            "UQ Art Museum",
            "Chemistry Building - Centre for Organic Photonoics",
            "Advanced Engineering Building - Superior Centre for Electronic Material Manufacture",

            // Food and Drinks
            "Physiol Cafeteria",
            "Main Refectory - Main Course, Pizza Cafe, Red Room",

            // Recreation and Sporting
            "Fitness Centre",
            "Aquatic Centre",
            "Tennis Centre/Basketball Courts",
            "Athletics and Playing Fields"
            };
            
    private final LatLng hotSpots[] = {
            new LatLng(-27.495431, 153.012030), // Chancellors
            new LatLng(-27.497704, 153.017949), // Lakes Bus Stop
            new LatLng(-27.496761, 153.019534), // Ferry Terminal
            new LatLng(-27.495431, 153.012030), // Student Services
            new LatLng(-27.498879, 153.013759), // UQ Security
            new LatLng(-27.497542, 153.013302), // Great Court
            new LatLng(-27.500020, 153.016158), // Lakes Area
            new LatLng(-27.497968, 153.014384), // Coop Bookshop
            new LatLng(-27.497485, 153.016564), // Schonell Theater
            new LatLng(-27.495977, 153.016281), // UQ Centre
            new LatLng(-27.499996, 153.015170), // GPSouth 3010 Rover Memorial
            new LatLng(-27.496752, 153.013700), // Forgan Smith
            new LatLng(-27.499996, 153.015170), // BioScience
            new LatLng(-27.499999, 153.013676), // Hawken Engineering
            new LatLng(-27.499014, 153.014724), // Zelman Cowen
            new LatLng(-27.496040, 153.013634), // Duhig North Library
            new LatLng(-27.498204, 153.013039), // Parnell
            new LatLng(-27.497224, 153.011762), // Michie
            new LatLng(-27.496499, 153.012020), // Art Museum
            new LatLng(-27.499648, 153.013039), // Chemistry Building
            new LatLng(-27.499464, 153.015045), // Advanced Engineering
            new LatLng(-27.499075, 153.012261), // Physiol Cafeteria
            new LatLng(-27.497405, 153.015882), // Main Refectory
            new LatLng(-27.496000, 153.015631), // UQ Fitness Centre
            new LatLng(-27.494987, 153.016422), // Aquatic Centre
            new LatLng(-27.494567, 153.015145), // Tennis and Basketbal Courts
            new LatLng(-27.493431, 153.012188) // Athletics Field
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