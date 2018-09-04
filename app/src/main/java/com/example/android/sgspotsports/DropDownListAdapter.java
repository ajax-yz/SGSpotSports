package com.example.android.sgspotsports;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DropDownListAdapter extends ArrayAdapter<MarkerItems> {

    public DropDownListAdapter(Context context, ArrayList<MarkerItems> markerList) {
        super(context, 0, markerList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_spinner_layout, parent, false);


        }

        ImageView imageViewFlag = convertView.findViewById(R.id.spinner_marker);
        TextView textViewName = convertView.findViewById(R.id.spinner_sports_type);

        MarkerItems currentItem = getItem(position);

        if(currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getmMarkerImage());
            textViewName.setText(currentItem.getmSportsType());
        }

        return convertView;
    }

}
