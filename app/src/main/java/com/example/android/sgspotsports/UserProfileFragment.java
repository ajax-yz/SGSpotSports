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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private DatabaseReference mRootRef;

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
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = (CircleImageView) view.findViewById(R.id.user_profile_photo);
        mProfileName = (TextView) view.findViewById(R.id.user_profile_name);
        mProfileStatus = (TextView) view.findViewById(R.id.user_profile_status);
        mProfileFriendsCount = (TextView) view.findViewById(R.id.user_friends_count);
        mProfileSendReqBtn = (Button) view.findViewById(R.id.button_send_friend_req);
        mDeclineReqBtn = (Button) view.findViewById(R.id.button_decline_friend_req);

        mCurrent_State = "not_friends";

        mDeclineReqBtn.setVisibility(View.INVISIBLE);
        mDeclineReqBtn.setEnabled(false);

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

                    DatabaseReference newNotificationRef = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationRef.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrent_User.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();

                    requestMap.put("Friend_request/" + mCurrent_User.getUid() + "/" + user_id + "/request_type" , "sent");
                    requestMap.put("Friend_request/" + mCurrent_User.getUid() + "/" + user_id + "/timestamp" , ServerValue.TIMESTAMP);
                    requestMap.put("Friend_request/" + user_id + "/" + mCurrent_User.getUid() + "/request_type", "received");
                    requestMap.put("Friend_request/" + user_id + "/" + mCurrent_User.getUid() + "/timestamp", ServerValue.TIMESTAMP);
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                           if (databaseError != null) {

                               Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                           } else {

                               Toast.makeText(getActivity(), "Successfully sent friend request", Toast.LENGTH_SHORT).show();
                           }
                            mProfileSendReqBtn.setEnabled(true);

                            mCurrent_State = "req_sent";
                            mProfileSendReqBtn.setText("Cancel Friend Request");

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

                                                            Toast.makeText(getContext(), "Successfully cancelled friend request", Toast.LENGTH_SHORT).show();

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

                // Taken from comments, does not remove entirely

                if(mCurrent_State.equals("friends")) {


                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrent_User.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrent_User.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mCurrent_State = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }

                // ---------------- REQUEST RECEIVED STATE ----------------

                if(mCurrent_State.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrent_User.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrent_User.getUid() + "/date", currentDate);

                    friendsMap.put("Friend_request/" + mCurrent_User.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_request/" + user_id + "/" + mCurrent_User.getUid(), null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_State = "friends";
                                mProfileSendReqBtn.setText("Unfriend this Person");

                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }
            }
        });

        // Taken from comments (Decline friend request)
        mDeclineReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map declineMap = new HashMap();

                declineMap.put("Friend_request/" + mCurrent_User.getUid() + "/" + user_id, null);
                declineMap.put("Friend_request/" + user_id + "/" + mCurrent_User.getUid(), null);

                mRootRef.updateChildren(declineMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError == null)
                        {

                            mCurrent_State = "not friends";
                            mProfileSendReqBtn.setText("Send Friend Request");

                            mDeclineReqBtn.setVisibility(View.INVISIBLE);
                            mDeclineReqBtn.setEnabled(false);

                        }else{
                            String error = databaseError.getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        }

                        mProfileSendReqBtn.setEnabled(true);
                    }
                });

            }
        });


    }
}
