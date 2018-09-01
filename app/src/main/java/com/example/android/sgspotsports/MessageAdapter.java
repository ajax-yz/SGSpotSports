package com.example.android.sgspotsports;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder (v);

    }

    public class MessageViewHolder extends  RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder(View view) {

            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int position) {

        Messages content = mMessageList.get(position);
        viewHolder.messageText.setText(content.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
