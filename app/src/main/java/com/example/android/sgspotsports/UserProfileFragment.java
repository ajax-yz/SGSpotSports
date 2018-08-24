package com.example.android.sgspotsports;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserProfileFragment extends Fragment {

    private View view;

    private TextView mDisplayID;

    // private String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        //user_id = null;

        if (bundle != null) {
            String user_id = bundle.getString("user_id");
            mDisplayID = (TextView) view.findViewById(R.id.profile_displayName);
            mDisplayID.setText(user_id);
        }

        /*
        mDisplayID = (TextView) view.findViewById(R.id.profile_displayName);

        mDisplayID.setText(user_id);
        */

    }
}
