package talent.virtualtourskeleton;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        OnMapReadyCallback {

    // Google Services variables
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Button bSatellite;
    private Button bNormal;
    private Boolean mapReady = false;
    private GoogleMap gMap;
    private boolean initialCameraSet = true;
    // Notification variables
    private Toast notifier;
    private int currentUserLocation = -1; // Default to non existing location
    private boolean popNotification = true;

    // TAG
    // Variables for points of interests
    private ArrayList<Marker> pointsOfInterests;
    private final String LOG_TAG = "VirtualTestApp";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bSatellite = (Button) view.findViewById(R.id.Satellite_button);
        bNormal = (Button) view.findViewById(R.id.Normal_button);

        bSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSatellite(view);
            }
        });
        bNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNormal(view);
            }
        });

        // Marker HashSets
        pointsOfInterests = new ArrayList<Marker>();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Attempt to Connect
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
        String closePoint;
        int closestIndex = -1;// Default to none
        float minDistance = 1000; // Default to high value

        // Focus camera on initial location
        if (mapReady == true && initialCameraSet == true) {
            LatLng initialLocation = new LatLng(location.getLatitude(), location.getLongitude());
            gMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));
            initialCameraSet = false; // Initial location already displayed
        }
        // Check if spot is close
        for (int i = 0; i < LocationsClass.spotsCoordinates.length; ++i) {
            target.setLatitude(LocationsClass.spotsCoordinates[i].latitude);
            target.setLongitude(LocationsClass.spotsCoordinates[i].longitude);
            if (location.distanceTo(target) < minDistance) {
                closestIndex = i; //Save closes index
                minDistance = location.distanceTo(target); // update minDistance
            }
        }

        if (minDistance < 200 && minDistance > 20) {
            Toast.makeText(getActivity(), "Location: " + LocationsClass.spotNames[closestIndex] +
                    " is within 200 meters!\n" + "Go check it out!", Toast.LENGTH_LONG).show();
            pointsOfInterests.get(closestIndex).showInfoWindow();
            gMap.getUiSettings().setMapToolbarEnabled(true);
            popNotification = true; // Allow notification to trigger when user reaches destination
        } else if (minDistance < 20) {
            if (closestIndex != currentUserLocation) {
//                showArrivalNotification(R.drawable.download, spotNames[closestIndex]);
                currentUserLocation = closestIndex; // Update user location
            }
        }
    }

    // MAP UI CALLBACKS
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        gMap = googleMap;
        populateMap(gMap);
    }

    private void populateMap(GoogleMap map) {
        Marker currentMarker;
        for (int i = 0; i < LocationsClass.spotsCoordinates.length; ++i) {
            currentMarker =
                    map.addMarker(new MarkerOptions().position(LocationsClass.spotsCoordinates[i]).title(LocationsClass.spotNames[i]));
            pointsOfInterests.add(currentMarker);
        }
        gMap.setMyLocationEnabled(true);
    }

    // UI interactive functions

    /*** Sets the current view of the map to a satellite view ***/
    public void setSatellite(View view) {
        if (mapReady) {
            gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            showArrivalNotification(R.drawable.download, "HAWKEN");
        }
    }

    /*** Sets the current view of the map to a normal view ***/
    public void setNormal(View view) {
        if (mapReady) {
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    /*** Creates a dialog showing the user's arrival ***/
    private void showArrivalNotification(int image, String location) {
        NotificationFragment notification = new NotificationFragment(getActivity());
        notification.setNotifierImage(R.drawable.download);
        notification.setNotifierText("You are at " + location +
                "\n\n Look for the marker shown for a nice AR experience!");
        notification.show(getActivity().getFragmentManager(), "pop up");
    }
}