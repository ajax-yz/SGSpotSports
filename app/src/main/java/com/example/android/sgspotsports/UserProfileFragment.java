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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private View view;

    private TextView mDisplayID;

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineReqBtn;

    private DatabaseReference mUsersDatabase;

    private String user_id;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private String mCurrent_State;

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
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = (CircleImageView) view.findViewById(R.id.user_profile_photo);
        mProfileName = (TextView) view.findViewById(R.id.user_profile_name);
        mProfileStatus = (TextView) view.findViewById(R.id.user_profile_status);
        mProfileFriendsCount = (TextView) view.findViewById(R.id.user_friends_count);
        mProfileSendReqBtn = (Button) view.findViewById(R.id.button_send_friend_req);
        mDeclineReqBtn = (Button) view.findViewById(R.id.button_decline_friend_req);

        mCurrent_State = "not_friends";

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


                // ---------------- FRIENDS LIST / REQUEST FEATURE ----------------

                mFriendReqDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mCurrent_State = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineReqBtn.setVisibility(View.VISIBLE);
                                mDeclineReqBtn.setEnabled(true);

                            } else if (req_type.equals("sent")) {

                                mCurrent_State = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);
                            }

                            mProgressDialog.dismiss();

                        } else {

                            mFriendDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)) {

                                        mCurrent_State = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this Person");

                                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mDeclineReqBtn.setEnabled(false);

                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        mProgressDialog.dismiss();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);

                // ---------------- NOT FRIENDS STATE ----------------

                if(mCurrent_State.equals("not_friends")) {

                    mFriendReqDatabase.child(mCurrent_User.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mFriendReqDatabase.child(user_id).child(mCurrent_User.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrent_User.getUid());
                                        notificationData.put("type", "request");

                                        mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()) {

                                                    mCurrent_State = "req_sent";
                                                    mProfileSendReqBtn.setText("Cancel Friend Request");

                                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                    mDeclineReqBtn.setEnabled(false);

                                                    Toast.makeText(getActivity(), "Request Sent Successfully", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    Toast.makeText(getActivity(), "Sending request failed", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });

                                    }
                                });

                            } else {

                                Toast.makeText(getActivity(), "Sending request failed", Toast.LENGTH_SHORT).show();

                            }

                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }


                // ---------------- CANCEL REQUEST STATE ----------------

                if (mCurrent_State.equals("req_sent")) {

                    mFriendReqDatabase.child(mCurrent_User.getUid()).child(user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        mFriendReqDatabase.child(user_id).child(mCurrent_User.getUid()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            mProfileSendReqBtn.setEnabled(true);
                                                            mCurrent_State = "not_friends";
                                                            mProfileSendReqBtn.setText("Send Friend Request");

                                                            mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                            mDeclineReqBtn.setEnabled(false);

                                                        } else {

                                                            Toast.makeText(getContext(), "Error in canceling request", Toast.LENGTH_SHORT).show();

                                                        }

                                                        mProfileSendReqBtn.setEnabled(true);

                                                    }
                                                });

                                    } else {

                                        Toast.makeText(getContext(), "Error in canceling request", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                }

                // ---------------- UNFRIEND STATE ----------------

                if(mCurrent_State.equals("friends")) {

                    mFriendDatabase.child(mCurrent_User.getUid()).child(user_id)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_State = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mDeclineReqBtn.setEnabled(false);
                                }
                            });

                        }
                    });
                }

                // ---------------- REQUEST RECEIVED STATE ----------------

                if(mCurrent_State.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendDatabase.child(mCurrent_User.getUid()).child(user_id).setValue(currentDate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mFriendDatabase.child(user_id)
                                        .child(mCurrent_User.getUid())
                                        .setValue(currentDate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    mFriendReqDatabase.child(mCurrent_User.getUid()).child(user_id)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {

                                                                        mFriendReqDatabase.child(user_id).child(mCurrent_User.getUid())
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()) {

                                                                                            mProfileSendReqBtn.setEnabled(true);
                                                                                            mCurrent_State = "friends";
                                                                                            mProfileSendReqBtn.setText("Unfriend this Person");

                                                                                            mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                                                            mDeclineReqBtn.setEnabled(false);

                                                                                        } else {

                                                                                            Toast.makeText(getContext(), "Error in canceling request", Toast.LENGTH_SHORT).show();

                                                                                        }

                                                                                        mProfileSendReqBtn.setEnabled(true);

                                                                                    }
                                                                                });

                                                                    } else {

                                                                        Toast.makeText(getContext(), "Error in canceling request", Toast.LENGTH_SHORT).show();

                                                                    }

                                                                }
                                                            });

                                                } else {

                                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                            } else {

                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            }
        });
    }
}
