package com.example.android.sgspotsports;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageMarkersFragment extends Fragment {

    private ImageView mAddMarkersBtn;
    private View view;

    public ManageMarkersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_markers, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAddMarkersBtn = (ImageView) view.findViewById(R.id.add_markers_btn);
        mAddMarkersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Access the marker settings (To delete / edit)

                // -------------- SUPPOSE TO OPEN UP GOOGLE MAP TO PASS LAT LNG FIRST --------------
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().addToBackStack(null)
                        .replace(R.id.fragment_container,
                                new MapsFragment()).commit();

            }
        });
    }
}
