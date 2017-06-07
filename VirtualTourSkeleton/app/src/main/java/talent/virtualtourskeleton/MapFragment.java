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
import com.google.android.gms.maps.model.MarkerOptions;



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

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Button bSatellite;
    private Button bNormal;
    private Boolean mapReady = false;
    private GoogleMap gMap;
    private boolean initialCameraSet = true;

    private OnFragmentInteractionListener mListener;

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
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
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
//        Toast.makeText(this, "Close Spots:\n\n" + closePoints, Toast.LENGTH_LONG).show();
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
