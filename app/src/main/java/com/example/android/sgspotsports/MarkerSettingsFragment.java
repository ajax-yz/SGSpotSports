package com.example.android.sgspotsports;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerSettingsFragment extends Fragment {


    private View view;
    private ArrayList<MarkerItems> mMarkerList;
    private DropDownListAdapter mAdapter;

    public MarkerSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_marker_settings, container, false);
        return view;
        
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();

        Spinner spinnerMarkers = view.findViewById(R.id.sports_type_drop_down_list);
        mAdapter = new DropDownListAdapter(getContext(), mMarkerList);
        spinnerMarkers.setAdapter(mAdapter);

        spinnerMarkers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedSportsType = "";
                MarkerItems selectedItem = (MarkerItems) parent.getItemAtPosition(position);


                    selectedSportsType = selectedItem.getmSportsType();

                    if (selectedSportsType.equals("Select type...")) {
                        selectedSportsType = "Default";
                    }

                    // Store type into firebase (to decide which marker image to use)
                    Toast.makeText(getContext(), selectedSportsType + " selected", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // No item selected
                // String selectedSportsType = "Default";
                // Toast.makeText(getContext(), selectedSportsType + " selected", Toast.LENGTH_SHORT).show();

            }

        });
    }

    private void initList() {

        mMarkerList = new ArrayList<>();
        mMarkerList.add(new MarkerItems("Select type...", R.drawable.ic_default_pin));
        mMarkerList.add(new MarkerItems("Basketball", R.drawable.ic_basketball_pin));
        mMarkerList.add(new MarkerItems("Badminton", R.drawable.ic_badminton_pin));
        mMarkerList.add(new MarkerItems("Stadium", R.drawable.ic_stadium_pin));
        mMarkerList.add(new MarkerItems("Soccer", R.drawable.ic_basketball_pin));
        mMarkerList.add(new MarkerItems("Swimming", R.drawable.ic_swim_pin));
        mMarkerList.add(new MarkerItems("Fitness Corner", R.drawable.ic_fitness_pin));

    }
}
