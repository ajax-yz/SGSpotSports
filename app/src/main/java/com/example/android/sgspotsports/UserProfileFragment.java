package com.example.android.sgspotsports;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class UserProfileFragment extends Fragment {

    private View view;

    private TextView mDisplayID;

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn;

    private DatabaseReference mUsersDatabase;

    private String user_id;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;

    private String mCurrent_state;

    private FirebaseUser mCurrent_User;

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
            user_id = bundle.getString("user_id");
            // mDisplayID = (TextView) view.findViewById(R.id.profile_displayName);
            //
            // mDisplayID.setText(user_id);
        }

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = (ImageView) view.findViewById(R.id.user_profile_photo);
        mProfileName = (TextView) view.findViewById(R.id.user_profile_name);
        mProfileStatus = (TextView) view.findViewById(R.id.user_profile_status);
        mProfileFriendsCount = (TextView) view.findViewById(R.id.user_friends_count);
        mProfileSendReqBtn = (Button) view.findViewById(R.id.button_send_friend_req);

        mCurrent_state = "not_friends";

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.ic_avatar).into(mProfileImage);

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(mCurrent_state.equals("not_friends")) {

                    mFriendReqDatabase.child(mCurrent_User.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mFriendReqDatabase.child(user_id).child(mCurrent_User.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getActivity(), "Request Sent Succesffuly", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            } else {

                                Toast.makeText(getActivity(), "Sending request failed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

            }
        });
    }
}
