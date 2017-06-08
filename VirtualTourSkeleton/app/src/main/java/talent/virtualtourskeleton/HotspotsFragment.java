package talent.virtualtourskeleton;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { HotspotsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotspotsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotspotsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String selectedLoc;

    private final String LIST_CATEGORY = "TYPE";
    private final String NAME = "NAME";

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> locationCollection;
    ExpandableListView expListView;

    ImageView helpImage;

    public HotspotsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HotspotsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HotspotsFragment newInstance(String param1, String param2) {
        HotspotsFragment fragment = new HotspotsFragment();
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
        View view = inflater.inflate(R.layout.fragment_hotspots, container, false);

        expListView = (ExpandableListView) view.findViewById(R.id.hotspots_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                getActivity(), LocationsClass.createGroupList(), LocationsClass.createCollection());
        expListView.setAdapter(expListAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
                selectedLoc = selected;
                int index = -1;
                for (int i = 0; i < LocationsClass.spotNames.length; i++) {
                    if (LocationsClass.spotNames[i] == selected) {
                        index = i;
                        break;
                    }
                }
                int res = getResources().getIdentifier("loc_" + index, "drawable", getActivity().getPackageName());
                helpImage.setImageResource(res);
                mListener.changeImage(selected);
//                Toast.makeText(getContext(), selected, Toast.LENGTH_LONG)
//                        .show();

                return true;
            }
        });

        Button btn = (Button) view.findViewById(R.id.btn_go);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedLoc != null) {
                    mListener.changeToMap(selectedLoc);
                }
            }
        });

        helpImage = (ImageView) view.findViewById(R.id.help_image);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void changeToMap(String location);
        void changeImage(String location);
    }
}
