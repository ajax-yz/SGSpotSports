package com.example.android.sgspotsports;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class MarkerInfoActivity extends AppCompatActivity implements Serializable {

    private Markers markers;
    private ImageView mImageView;
    private TextView mFacilityName, mAddress, mDescription;
    private Button mAddToPlanner;
    private TextView mDirections;
    private TextView mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);

        /*
        markers = (Markers) getIntent().getSerializableExtra("Markers");

        mImageView = findViewById(R.id.facility_background_image);
        mFacilityName = findViewById(R.id.facility_name);
        mAddress = findViewById(R.id.address_text);
        mDescription = findViewById(R.id.description_content);

        mAddToPlanner = findViewById(R.id.add_to_planner);
        mDirections = findViewById(R.id.directions_button);

        Picasso.get()
                .load(markers.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(mImageView);

        mFacilityName.setText(markers.getFacility_name());
        mAddress.setText(markers.getAddress());
        mDescription.setText(markers.getDescription());

        mAddToPlanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open planner

            }
        });

        mDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open Navigation

            }
        });
        */

    }
}
