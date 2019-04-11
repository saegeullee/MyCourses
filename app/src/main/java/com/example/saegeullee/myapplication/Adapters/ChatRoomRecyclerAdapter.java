package com.example.saegeullee.myapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private Context context;
    private List<ChatMessage> chatMessageList;
    private ChatMessage chatMessage;

    public ChatRoomRecyclerAdapter(Context context, List<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.chatmessage_row_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.chatmessage_row_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).getUser_id()
                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(chatMessageList.get(position).getUser_id()
                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    public void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {

        chatMessage = chatMessageList.get(position);

        myChatViewHolder.mDate.setText(chatMessage.getTimestamp());
        myChatViewHolder.mMessage.setText(chatMessage.getMessage());

    }

    public void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {

        chatMessage = chatMessageList.get(position);

        otherChatViewHolder.mName.setText(chatMessage.getName());
        otherChatViewHolder.mDate.setText(chatMessage.getTimestamp());
        otherChatViewHolder.mMessage.setText(chatMessage.getMessage());

        ImageLoader.getInstance().displayImage(chatMessage.getProfile_image(), otherChatViewHolder.mProfileImage);
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {

        private TextView mDate, mMessage;

        public MyChatViewHolder(@NonNull View itemView) {
            super(itemView);

            mMessage = itemView.findViewById(R.id.chatmessage_content);
            mDate = itemView.findViewById(R.id.chatmessage_date);

        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mProfileImage;
        private TextView mName, mMessage, mDate;

        public OtherChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfileImage = itemView.findViewById(R.id.chatImage);
            mName = itemView.findViewById(R.id.chat_opponent_name);
            mMessage = itemView.findViewById(R.id.chatmessage_content);
            mDate = itemView.findViewById(R.id.chatmessage_date);
        }
    }
}
