package com.example.android.sgspotsports;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class StatusFragment extends Fragment {

    private View view;

    private TextInputLayout mStatus;
    private Button mSaveBtn;

    // private Toolbar mToolbar;

    // Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    // Progress
    private ProgressDialog mProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_status, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        mToolbar = (Toolbar) view.findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         */

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mStatus = (TextInputLayout) view.findViewById(R.id.status_input);
        mSaveBtn = (Button) view.findViewById(R.id.status_save_btn);


        // Read from the database
        mStatusDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String status = dataSnapshot.child("status").getValue().toString();
                mStatus.getEditText().setText(status);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Progress
                mProgress = new ProgressDialog(getActivity());
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();

                String status = mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            mProgress.dismiss();
                            hideSoftKeyboard();

                            FragmentManager manager = getFragmentManager();
                            manager.beginTransaction().replace(R.id.fragment_container,
                                    new SettingsFragment()).commit();

                        } else {

                            Toast.makeText(getActivity(), "There was some error in saving changes.", Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }
        });

    }

    // Hides the keyboard after searching
    private void hideSoftKeyboard() {

        FragmentActivity activity = getActivity();

        InputMethodManager in = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}
