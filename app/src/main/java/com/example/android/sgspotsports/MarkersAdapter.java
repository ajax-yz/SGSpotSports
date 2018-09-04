package com.example.android.sgspotsports;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MarkersAdapter extends RecyclerView.Adapter<MarkersViewHolder> {

    private Context mContext;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mUserDatabase;
    private List<Markers> mMarkersList;

    public MarkersAdapter(Context mContext, List<Markers> mMarkersList) {
        this.mContext = mContext;
        this.mMarkersList = mMarkersList;
    }

    @NonNull
    @Override
    public MarkersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.markers_single_layout, parent, false);
        return new MarkersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkersViewHolder markersViewHolder, int position) {

        Markers markers = mMarkersList.get(position);
        markersViewHolder.setAddress(markers.getAddress());
        markersViewHolder.setFacilityName(markers.getFacility_name());
        markersViewHolder.setMarkerPin(markers.getFacility_type());

        /*
        Picasso.get().load(markers.getImageUrl())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_default_pin)
                .into(markersViewHolder.setImage);
                */
    }

    @Override
    public int getItemCount() {
        return mMarkersList.size();
    }
}
