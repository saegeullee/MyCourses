package com.example.saegeullee.myapplication.TabFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.saegeullee.myapplication.Adapters.CommentRecyclerAdapter;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Comment;
import com.example.saegeullee.myapplication.models.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Tab4Fragment extends Fragment{
    private static final String TAG = "Tab4Fragment";

    private Button commentSubmitBtn;
    private EditText commentEditText;
    private RecyclerView commentListRecyclerView;
    private CommentRecyclerAdapter mAdapter;
    private List<Comment> commentList;

    //Bundle 에 담은 User 객체
    private Course course;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            course = getArguments().getParcelable(getString(R.string.bundle_course));
            Log.d(TAG, "onCreate: got course : " + course.toString());
        } else {
            Log.d(TAG, "onCreate: course is null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement4_layout, container, false);

        context = container.getContext();

        commentEditText = view.findViewById(R.id.commentEditText);
        commentSubmitBtn = view.findViewById(R.id.commentSubmitBtn);
        commentListRecyclerView = view.findViewById(R.id.commentListRecyclerView);
        commentListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        commentListRecyclerView.setNestedScrollingEnabled(false);

        commentList = new ArrayList<>();

        commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(commentEditText.getText().toString())) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                    String commentId = reference
                            .child(getString(R.string.dbnode_comments))
                            .push().getKey();

                    Comment comment = new Comment();
                    comment.setComment(commentEditText.getText().toString());
                    comment.setCourse_id(course.getCourseId());
                    comment.setComment_id(commentId);
                    comment.setTimestamp(getTimeStamp());
                    comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    reference.child(getString(R.string.dbnode_comments))
                            .child(course.getCourseId())
                            .child(commentId)
                            .setValue(comment);

                    commentEditText.setText("");

                }
            }
        });

        getComment();

        return view;
    }

    private void getComment() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbnode_comments))
                .child(course.getCourseId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        commentList.clear();
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: singleSnapshot : " + singleSnapshot);
                            Comment comment = singleSnapshot.getValue(Comment.class);
                            Log.d(TAG, "onDataChange: got comment : " + comment.toString());

                            commentList.add(comment);
                        }
                        Log.d(TAG, "onDataChange: commentList size : " + commentList.size());
                        loadComment();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


//        mAdapter = new CommentRecyclerAdapter(context, )
    }

    private void loadComment() {

        mAdapter = new CommentRecyclerAdapter(context, commentList);
        commentListRecyclerView.setAdapter(mAdapter);

    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(new Date());
    }


}
