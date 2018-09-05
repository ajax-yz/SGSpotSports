package com.example.android.sgspotsports;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageMarkersFragment extends Fragment implements MarkersAdapter.OnItemClickListener {

    private ImageView mAddMarkersBtn;
    private View view;
    private RecyclerView mRecyclerView;
    private MarkersAdapter mAdapter;

    private DatabaseReference mDatabaseRef;

    private DatabaseReference mRootRef;

    private ValueEventListener mDBListener;

    private List<Markers> mMarkersList;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;

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

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mProgressCircle = view.findViewById(R.id.progress_circle);

        mRecyclerView = view.findViewById(R.id.markers_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mMarkersList = new ArrayList<>();

        mAdapter = new MarkersAdapter(getContext(), mMarkersList);
        mRecyclerView.setAdapter(mAdapter);
        //startListening();

        mAdapter.setOnItemClickListener((MarkersAdapter.OnItemClickListener) this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mCurrent_user_id).child("Markers");

        mRootRef = FirebaseDatabase.getInstance().getReference().child("Markers");

        mStorage = FirebaseStorage.getInstance();

        // To load faster
        mDatabaseRef.keepSynced(true);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {

            // Whenever something is added, this method will be called
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mMarkersList.clear();
                // Loop through all the objects
                for (DataSnapshot markersSnapShot : dataSnapshot.getChildren()) {

                    Markers markers = markersSnapShot.getValue(Markers.class);

                    markers.setmKey(markersSnapShot.getKey());

                    mMarkersList.add(markers);
                }

                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

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

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(getContext(), "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        //Toast.makeText(getContext(), "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {

        Toast.makeText(getContext(), "Deleting", Toast.LENGTH_SHORT).show();

        Markers selectedItem = mMarkersList.get(position);
        final String selectedKey = selectedItem.getmKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mDatabaseRef.child(selectedKey).removeValue();
                mRootRef.child(selectedKey).removeValue();
                Toast.makeText(getActivity(), "Marker deleted", Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mDatabaseRef.removeEventListener(mDBListener);
    }
}
