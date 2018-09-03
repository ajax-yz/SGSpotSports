package com.example.android.sgspotsports;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView mRequestsList;

    private DatabaseReference mRequestsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id, userName, userThumb, userStatus, requestType;

    private View mMainView;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);

        return mMainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRequestsList = (RecyclerView) mMainView.findViewById(R.id.requests_list);

        mAuth = FirebaseAuth.getInstance();

        // Users must login first if not app will crash
        //if (mAuth.getCurrentUser() != null)
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        // Set online icon to invisible
        // userOnlineView = (ImageView) mMainView.findViewById(R.id.user_single_online_icon);
        // userOnlineView.setVisibility(View.INVISIBLE);

        // Requests pointing to current user id in Friend_request (testing)
        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request").child(mCurrent_user_id);
        mRequestsDatabase.keepSynced(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Requests> options =
                new FirebaseRecyclerOptions.Builder<Requests>()
                        .setQuery(mRequestsDatabase, Requests.class)
                        .build();

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> requestsRecyclerViewAdapter =
                new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(options) {

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new RequestsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder requestsViewHolder, int position, @NonNull Requests model) {

                // get user id from database
                final String list_user_id = getRef(position).getKey();

                // list_user_id returning mavewonders id
                Log.d("LIST USER ID: ", list_user_id);

                /*
                Redundant because can use the same if statements for both conditions,
                since mCurrent User & LIST_USER_ID is dynamic
                if (requestType.equals(sent) || requestType.equals(received)) {
                 */

                mRequestsDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        requestType = dataSnapshot.child("request_type").getValue().toString();

                        if (requestType.equals("sent")){

                            mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    userName = dataSnapshot.child("name").getValue().toString();
                                    userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                    userStatus = dataSnapshot.child("status").getValue().toString();


                                    requestsViewHolder.setName(userName);
                                    requestsViewHolder.setUserImage(userThumb, getContext());
                                    requestsViewHolder.setStatus(userStatus);

                                    setRecyclerViewClickable(requestsViewHolder, list_user_id);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else if (requestType.equals("received")) {

                            mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    userName = dataSnapshot.child("name").getValue().toString();
                                    userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                    userStatus = dataSnapshot.child("status").getValue().toString();


                                    requestsViewHolder.setName(userName);
                                    requestsViewHolder.setUserImage(userThumb, getContext());
                                    requestsViewHolder.setStatus(userStatus);

                                    setRecyclerViewClickable(requestsViewHolder, list_user_id);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {

                            Log.d("REQUEST TYPE: ", requestType);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(getContext(), (CharSequence) databaseError, Toast.LENGTH_LONG).show();

                    }
                });


                /*

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        userName = dataSnapshot.child("name").getValue().toString();
                        userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        userStatus = dataSnapshot.child("status").getValue().toString();


                        requestsViewHolder.setName(userName);
                        requestsViewHolder.setUserImage(userThumb, getContext());
                        requestsViewHolder.setStatus(userStatus);

                        requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Accept/Decline Friend Request"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Please select:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {

                                            // Send user id to another fragment
                                            UserProfileFragment userProfileFragment = new UserProfileFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("user_id", list_user_id);
                                            userProfileFragment.setArguments(bundle);

                                            FragmentManager manager = getFragmentManager();
                                            manager.beginTransaction().replace(R.id.fragment_container,
                                                    userProfileFragment).commit();

                                        }

                                    }
                                });

                                builder.show();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                */

            }
        };

        mRequestsList.setAdapter(requestsRecyclerViewAdapter);
        requestsRecyclerViewAdapter.startListening();
    }

    public void setRecyclerViewClickable(RequestsViewHolder requestsViewHolder, final String intent_user_name){

        requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{"Accept/Decline Friend Request"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Please select:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {

                            // Send user id to another fragment
                            UserProfileFragment userProfileFragment = new UserProfileFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", intent_user_name);
                            userProfileFragment.setArguments(bundle);

                            FragmentManager manager = getFragmentManager();
                            manager.beginTransaction().replace(R.id.fragment_container,
                                    userProfileFragment).commit();

                        }

                    }
                });

                builder.show();

            }
        });

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        public RequestsViewHolder(View itemView){
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);

        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);

            userNameView.setText(name);
        }

        public void setStatus(String status) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);

        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_default_avatar).into(userImageView);

        }

    }
}
