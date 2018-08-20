package com.example.android.sgspotsports;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UsersFragment extends Fragment {

    private View view;

    // private Toolbar mToolbar;

    private RecyclerView mUsersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // If using individual toolbar for each page***
        /*
        mToolbar = (Toolbar) view.findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar.setTitle("All Users");
        getSupportActionBar.setDisplayHomeAsUpEnabled(true);
        */
        // Need to add activity into the manifest for the up button to work

        mUsersList = (RecyclerView) view.findViewById(R.id.users_list);

    }
}
