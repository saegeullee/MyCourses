package com.example.saegeullee.myapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.ChatMessage;
import com.example.saegeullee.myapplication.models.ChatRoom;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ChatRecyclerAdapter";

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private Context context;
    private List<ChatRoom> chatRoomList;
    private ChatRoom chatRoom;

    public ChatRecyclerAdapter(Context context, List<ChatRoom> chatRoomList) {
        this.context = context;
        this.chatRoomList = chatRoomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        chatRoom = chatRoomList.get(position);
        Log.d(TAG, "onBindViewHolder: got chatRoom : " + chatRoom.toString());
//        for(int i = 0; i < chatRoom.getChatMessageList().size(); i ++) {
//            Log.d(TAG, "onBindViewHolder: chatRoom Messages getMessage() : " + chatRoom.getChatMessageList().get(i).getMessage());
//            Log.d(TAG, "onBindViewHolder: chatRoom Messages getTimestamp() : " + chatRoom.getChatMessageList().get(i).getTimestamp());
//
//        }

        String creatorId = chatRoom.getCreator_id();
        String receiverId = chatRoom.getReceiver_id();
        String courseId = chatRoom.getCourse_id();

        holder.mChatDate.setText(chatRoom.getChatMessageList().get(chatRoom.getChatMessageList().size() - 1).getTimestamp());
        holder.mChatLastMessage.setText(chatRoom.getChatMessageList().get(chatRoom.getChatMessageList().size() - 1).getMessage());

        //get Course Title
        DatabaseReference courseReference = FirebaseDatabase.getInstance().getReference();
        courseReference.child(context.getString(R.string.dbnode_courses))
                .child(receiverId)
                .child(courseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: course dataSnapshot : " + dataSnapshot);
                        Course course = dataSnapshot.getValue(Course.class);
                        Log.d(TAG, "onDataChange: got course : " + course.toString() );
                        holder.mCourseTitle.setText(course.getCourseTitle());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        for(int i=0; i < chatRoom.getChatMessageList().size(); i++) {
            Log.d(TAG, "onBindViewHolder: " + position + " 번째 chatRoom의 chatMessages : "
                    + chatRoom.getChatMessageList().get(i).toString());
        }

        /**
         * 채팅방에는 서로 상대방의 프로필이 뜨게끔 채팅방을 설정해주어야 한다.
         * 현재 유저의 아이디가 creator ID 와 같다면 이 사람은 학생이기 때문에 튜터의 프로필을 보여줘야 하고,
         * 현재 유저의 아이디가 receiver ID 와 같다면 이 사람은 튜터이기 때문에 학생의 프로필을 보여줘야 한다.
         *
         * */
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(creatorId)) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            reference.child(context.getString(R.string.dbnode_users))
                    .child(receiverId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                            User user = dataSnapshot.getValue(User.class);
                            Log.d(TAG, "onDataChange: got user : " + user.toString());

                            holder.mChatOpponentName.setText(user.getName());
                            ImageLoader.getInstance().displayImage(user.getProfile_image(), holder.mProfileImage);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            Log.d(TAG, "onBindViewHolder: current user is creator");

        } else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            reference.child(context.getString(R.string.dbnode_users))
                    .child(creatorId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                            User user = dataSnapshot.getValue(User.class);
                            Log.d(TAG, "onDataChange: got user : " + user.toString());

                            holder.mChatOpponentName.setText(user.getName());
                            ImageLoader.getInstance().displayImage(user.getProfile_image(), holder.mProfileImage);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        if(onItemClickListener != null) {

            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(pos);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mProfileImage;
        private TextView mChatOpponentName, mChatLastMessage, mChatDate, mCourseTitle;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProfileImage = itemView.findViewById(R.id.chatImage);
            mChatOpponentName = itemView.findViewById(R.id.chat_opponent_name);
            mChatLastMessage = itemView.findViewById(R.id.chat_last_message);
            mChatDate = itemView.findViewById(R.id.chat_date);
            mCourseTitle = itemView.findViewById(R.id.course_title);

        }
    }

}
