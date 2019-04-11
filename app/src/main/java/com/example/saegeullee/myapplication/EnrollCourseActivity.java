package com.example.saegeullee.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnrollCourseActivity extends AppCompatActivity {

    private static final String TAG = "EnrollCourseActivity";

    private CircleImageView mTutorImage;
    private TextView courseDay, courseHour, coursePlace;
    private Button purchaseCourseBtn;
    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;

    private Course course;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_course);

        getCourseInfo();
        initUI();
    }

    private void getCourseInfo() {

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_course))) {
            course = intent.getParcelableExtra(getString(R.string.intent_course));
            Log.d(TAG, "getCourseInfo: got course : " + course.toString());
        } else {
            Log.d(TAG, "getCourseInfo: course is null");
        }
    }

    private void initUI() {

        mTutorImage = findViewById(R.id.tutorProfileImage);
        courseDay = findViewById(R.id.courseDayDataText);
        courseHour = findViewById(R.id.courseHourDataText);
        coursePlace = findViewById(R.id.coursePlaceDataText);
        purchaseCourseBtn = findViewById(R.id.purchaseCourseBtn);

        mProgressDialog = new ProgressDialog(this);

        mToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.enroll_course_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(course != null) {
            ImageLoader.getInstance().displayImage(course.getCourseTutorImage(), mTutorImage);
            courseDay.setText(course.getCourseDay());
            courseHour.setText(course.getCourseHour());
            coursePlace.setText(course.getCoursePlace());

        } else {
            Log.d(TAG, "initUI: course is null");
        }

        purchaseCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.setMessage("강의 등록 및 결제 진행중입니다.");
                mProgressDialog.setTitle("강의 등록 및 결제");
                mProgressDialog.show();

                course.setCourseEnrolledDate(getTimeStamp());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                reference.child(getString(R.string.dbnode_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.dbnode_enrolled_courses))
                        .child(course.getCourseId())
                        .setValue(course)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: course added to the enrolled_courses node");
                                Toast.makeText(EnrollCourseActivity.this, "강의 결제 완료", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: course purchase failed");
                        Toast.makeText(EnrollCourseActivity.this, "강의 결제 실패", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });

                /**
                 * get user object to save at dbnode_courses node
                 */

                reference.child(getString(R.string.dbnode_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onDataChange: dataSnapshot of user : " + dataSnapshot);


                                user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    Log.d(TAG, "onDataChange: got user : " + user.toString());

                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

                                    reference1.child(getString(R.string.dbnode_courses))
                                            .child(course.getCourseTutorId())
                                            .child(course.getCourseId())
                                            .child(getString(R.string.dbnode_enrolled_students))
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d(TAG, "onComplete: user id added to the enrolled_students node SUCCEED");

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: user id added to the enrolled_students node FAILED");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(new Date());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
