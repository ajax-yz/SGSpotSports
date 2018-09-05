package com.example.android.sgspotsports;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MarkersAdapter extends RecyclerView.Adapter<MarkersAdapter.MarkersViewHolder> {

    private Context mContext;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mUserDatabase;
    private List<Markers> mMarkersList;
    private OnItemClickListener mListener;

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

    public class MarkersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        public View mView;

        public MarkersViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            mView = itemView;
        }

        @Override
        public void onClick(View v) {

            if (mListener != null) {

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){

                    mListener.onItemClick(position);
                }
            }

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select Action:");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete marker");
            //MenuItem doWhatever = menu.add(Menu.NONE, 2, 2, "Something");
            delete.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (mListener != null) {

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }

            return false;
        }

        public void setFacilityName(String name) {

            TextView facilityView = (TextView) mView.findViewById(R.id.marker_single_name);
            facilityView.setText(name);

        }

        public void setAddress(String address) {

            TextView addressView = (TextView) mView.findViewById(R.id.marker_single_address);

            addressView.setText(address);
        }

        public void setMarkerPin(String type) {

            ImageView markerPinView = (ImageView) mView.findViewById(R.id.marker_single_pin);

            switch (type) {

                case "Select type...":
                    markerPinView.setImageResource(R.drawable.ic_default_pin);
                    break;
                case "Basketball":
                    markerPinView.setImageResource(R.drawable.ic_basketball_pin);
                    break;
                case "Badminton":
                    markerPinView.setImageResource(R.drawable.ic_badminton_pin);
                    break;
                case "Stadium":
                    markerPinView.setImageResource(R.drawable.ic_stadium_pin);
                    break;
                case "Soccer":
                    markerPinView.setImageResource(R.drawable.ic_soccer_pin);
                    break;
                case "Swimming":
                    markerPinView.setImageResource(R.drawable.ic_swim_pin);
                    break;
                case "Fitness Corner":
                    markerPinView.setImageResource(R.drawable.ic_fitness_pin);
                    break;
                default:
                    Log.d("MarkersViewHolder: ", "ERROR IN TYPE");
            }

        }

    }

    public interface OnItemClickListener{
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
