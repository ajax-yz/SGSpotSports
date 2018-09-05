package com.example.android.sgspotsports;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MarkersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

    private View mView;
    private MarkersAdapter.OnItemClickListener mListener;

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
