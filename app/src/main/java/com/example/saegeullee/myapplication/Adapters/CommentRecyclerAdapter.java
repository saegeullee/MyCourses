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
import com.example.saegeullee.myapplication.models.Comment;
import com.example.saegeullee.myapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {

    private static final String TAG = "CommentRecyclerAdapter";

    private Context context;
    private List<Comment> commentList;

    public CommentRecyclerAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        holder.mCommentText.setText(comment.getComment());
        holder.mDateText.setText(comment.getTimestamp());

        reference.child(context.getString(R.string.dbnode_users))
                .child(comment.getUser_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        User user = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: got user : " + user.toString());
                        holder.mUserName.setText(dataSnapshot.getValue(User.class).getName());
                        ImageLoader.getInstance().displayImage(dataSnapshot.getValue(User.class).getProfile_image(), holder.mProfileImage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mProfileImage;
        private TextView mCommentText, mDateText, mUserName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfileImage = itemView.findViewById(R.id.profileImage);
            mCommentText = itemView.findViewById(R.id.commentText);
            mDateText = itemView.findViewById(R.id.dateText);
            mUserName = itemView.findViewById(R.id.userName);
        }
    }
}
