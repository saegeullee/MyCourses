package com.example.saegeullee.myapplication;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.saegeullee.myapplication.Adapters.EnrolledStudentsListAdapter;
import com.example.saegeullee.myapplication.MoreActivity.MoreActivity;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnrolledStudentsCheckActivity extends AppCompatActivity {

    private static final String TAG = "EnrolledStudentsCheckAc";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private EnrolledStudentsListAdapter mAdapter;

    private TextView notYetEnrolledNotice;

    private User user;
    private List<User> enrolledStudentsList;
    private Course course;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_enrolled_students);

        getCourseData();
        initUI();
    }

    private void getCourseData() {
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_course))) {
            course = intent.getParcelableExtra(getString(R.string.intent_course));
            Log.d(TAG, "getCourseData: got course : " + course.toString());
        } else {
            Log.d(TAG, "getCourseData: course is null");
        }

    }

    private void initUI() {

        enrolledStudentsList = new ArrayList<>();

        mToolbar = findViewById(R.id.enrolledStudentsCheckToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.enrolled_stuents_list_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = findViewById(R.id.enrolledStudentsCheckRecyclerView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        notYetEnrolledNotice = findViewById(R.id.notYetEnrolledText);

        getEnrolledStudentsData();

    }

    private void getEnrolledStudentsData() {

        enrolledStudentsList.clear();

        String courseId = course.getCourseId();
        String tutorId = course.getCourseTutorId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_courses))
                .child(tutorId)
                .child(courseId)
                .child(getString(R.string.dbnode_enrolled_students))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapShot : " + dataSnapshot);

                        Log.d(TAG, "onDataChange: dataSnapshot.getChildren() : " + dataSnapshot.getChildren());

                        if(dataSnapshot.getValue() == null) {
                            notYetEnrolledNotice.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onDataChange: set textView visible");
                        } else {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: singleSnapshot : " + singleSnapshot);
                                user = singleSnapshot.getValue(User.class);
                                Log.d(TAG, "onDataChange: user : " + user.toString());
                                enrolledStudentsList.add(user);
                            }
                            Log.d(TAG, "onDataChange: enrolledStudents number : " + enrolledStudentsList.size());
                            loadEnrolledStudents();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadEnrolledStudents() {
        mAdapter = new EnrolledStudentsListAdapter(EnrolledStudentsCheckActivity.this, enrolledStudentsList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new EnrolledStudentsListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Intent intent = new Intent(EnrolledStudentsCheckActivity.this, EnrolledStudentsAccountCheckActivity.class);
                intent.putExtra(getString(R.string.intent_enrolled_students_info), enrolledStudentsList.get(position));

                android.util.Pair[] pairs = new android.util.Pair[2];
                pairs[0] = new android.util.Pair<>(view.findViewById(R.id.studentImage), "imageTransition");
                pairs[1] = new android.util.Pair<>(view.findViewById(R.id.studentName), "nameTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(EnrolledStudentsCheckActivity.this, pairs);

                startActivity(intent, options.toBundle());
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
