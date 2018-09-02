package com.example.android.sgspotsports;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter{

    private List<Messages> mMessageList;
    private Context mContext;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public MessageAdapter(Context context, List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        /* Original code
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);


        return new MessageViewHolder (v);
        */

        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_message_single_layout, parent, false);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receiver_message_single_layout,parent, false);
        }

        return null;

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        Messages message = (Messages) mMessageList.get(position);

        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);

            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
        }
    }

        /*Old code
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        Log.d("Current User:", current_user_id);
        Messages content = mMessageList.get(position);
        // Retrieve the user that sent the message, throwing error when no messages was made
        if (content != null) {
            String from_user = content.getFrom();
            String message_type = content.getType();

            Log.d("From User:", from_user);


            if (message_type.equals("text")) {
                viewHolder.messageText.setText(content.getMessage());
                viewHolder.messageImage.setVisibility(View.INVISIBLE);

            // Nest this inside equals text
            if (from_user.equals(current_user_id)) {
                // If we are the one sending the message
                viewHolder.messageText.setBackgroundColor(Color.WHITE);
                viewHolder.messageText.setTextColor(Color.BLACK);
            } else {
                // The other party sent the message
                viewHolder.messageText.setBackgroundResource(R.drawable.sender_sender_message_text_background);
                viewHolder.messageText.setTextColor(Color.WHITE);
            }
            //

            } else {
                viewHolder.messageText.setVisibility(View.INVISIBLE);
                Picasso.get().load(content.getMessage())
                        .placeholder(R.drawable.ic_default_avatar).into(viewHolder.messageImage);
            }
        }
        */


    public class SentMessageHolder extends RecyclerView.ViewHolder {

        public TextView messageText, timeText;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_layout);
            messageImage = (ImageView) itemView.findViewById(R.id.message_image_layout);
            timeText = (TextView) itemView.findViewById(R.id.message_time_layout);
        }

        public void bind(Messages message) {

            String from_user = message.getFrom();
            String message_type = message.getType();

            // Set time
            timeText.setText(DateUtils.formatDateTime(mContext, message.getTime(), 0));

            // Set profile photo
            mUserDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.ic_default_avatar).into(profileImage);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // set messageImage
            if (message_type.equals("text")) {

                // Set text
                messageText.setText(message.getMessage());
                messageImage.setVisibility(View.INVISIBLE);
            } else {

                messageText.setVisibility(View.INVISIBLE);
                Picasso.get().load(message.getMessage())
                        .placeholder(R.drawable.ic_default_avatar).into(messageImage);
            }

        }

    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        public TextView messageText, timeText;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_layout);
            messageImage = (ImageView) itemView.findViewById(R.id.message_image_layout);
            timeText = (TextView) itemView.findViewById(R.id.message_time_layout);
        }

        public void bind(Messages message) {

            String from_user = message.getFrom();
            String message_type = message.getType();

            // Set time
            timeText.setText(DateUtils.formatDateTime(mContext, message.getTime(), 0));

            // Set profile photo
            mUserDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.ic_default_avatar).into(profileImage);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // set messageImage
            if (message_type.equals("text")) {

                // Set text
                messageText.setText(message.getMessage());
                messageImage.setVisibility(View.INVISIBLE);
            } else {

                messageText.setVisibility(View.INVISIBLE);
                Picasso.get().load(message.getMessage())
                        .placeholder(R.drawable.ic_default_avatar).into(messageImage);
            }

        }

    }

    /* Old code


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, timeText;
        public CircleImageView profileImage;
        // public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View view) {

            super(view);

            // displayName = (TextView) view.findViewById(____);
            // messageImage = (ImageView) view.findViewById(____);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
        }

    }

    */

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();

        Messages message = (Messages) mMessageList.get(position);
        String from_user = message.getFrom();

        if (from_user.equals(current_user_id)){
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

}
