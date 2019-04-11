package com.example.saegeullee.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.saegeullee.myapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnrolledStudentsAccountCheckActivity extends AppCompatActivity {

    private static final String TAG = "EnrolledStudentsAccount";

    private Toolbar mToolbar;
    private User user;

    private CircleImageView mStudentImage;
    private TextView mStudentName, mStudentEmail, mStudentSex, mStudentAge, mStudentJob, mStudentInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_students_account_check);

        initUI();
    }

    private void initUI() {

        mStudentImage = findViewById(R.id.user_image);
        mStudentName = findViewById(R.id.user_name);
        mStudentEmail = findViewById(R.id.user_email);
        mStudentSex = findViewById(R.id.user_sex);
        mStudentAge = findViewById(R.id.user_age);
        mStudentJob = findViewById(R.id.user_job);
        mStudentInterest = findViewById(R.id.user_interest);

        mToolbar = findViewById(R.id.enrolled_students_account_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.enrolled_students_account_detail_info);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_enrolled_students_info))) {
            user = intent.getParcelableExtra(getString(R.string.intent_enrolled_students_info));
            Log.d(TAG, "initUI: got user : " + user.toString());

            setDataToView();
        } else {
            Log.d(TAG, "initUI: user is null");
        }

    }

    private void setDataToView() {

        ImageLoader.getInstance().displayImage(user.getProfile_image(), mStudentImage);

        String userId = user.getUser_id();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        User user = dataSnapshot.getValue(User.class);

                            mStudentName.setText(user.getName());
                            mStudentEmail.setText(user.getEmail());
                            mStudentSex.setText(user.getSex());
                            mStudentAge.setText(user.getAge());
                            mStudentJob.setText(user.getJob());
                            mStudentInterest.setText(user.getInterest());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


//        mStudentName.setText(user.getName());
//        mStudentEmail.setText(user.getEmail());
//        mStudentSex.setText(user.getSex());
//        mStudentAge.setText(user.getAge());
//        mStudentJob.setText(user.getJob());
//        mStudentInterest.setText(user.getInterest());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
