package com.example.saegeullee.myapplication.MoreActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.saegeullee.myapplication.Adapters.CourseEnrolledListAdapter;
import com.example.saegeullee.myapplication.CourseDetailsActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnrolledCourseActivity extends AppCompatActivity {

    private static final String TAG = "EnrolledCourseActivity";

    private Toolbar mToolbar;

    //widgets
    private RecyclerView mRecyclerView;
    private CourseEnrolledListAdapter mAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    //vars
    private List<Course> enrolledCourseList;
    private Course course;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_course);

        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        enrolledCourseList.clear();

        getCourseData();
    }

    private void getCourseData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.dbnode_enrolled_courses))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: singleSnapshot : " + singleSnapshot);


                            /**
                             * 빈 course
                             */
                            course = new Course();

                            //7개
                            course.setCourseTitle(singleSnapshot.getValue(Course.class).getCourseTitle());
                            course.setCourseTutorName(singleSnapshot.getValue(Course.class).getCourseTutorName());
                            course.setCoursePricePerHour(singleSnapshot.getValue(Course.class).getCoursePricePerHour());
                            course.setCourseTutorImage(singleSnapshot.getValue(Course.class).getCourseTutorImage());
                            course.setCourseImage(singleSnapshot.getValue(Course.class).getCourseImage());
                            course.setCourseId(singleSnapshot.getValue(Course.class).getCourseId());
                            course.setCourseTutorId(singleSnapshot.getValue(Course.class).getCourseTutorId());

                            //5개
                            course.setCourseHourPerClass(singleSnapshot.getValue(Course.class).getCourseHourPerClass());
                            course.setCoursePlace(singleSnapshot.getValue(Course.class).getCoursePlace());
                            course.setCourseMaxStudentNumber(singleSnapshot.getValue(Course.class).getCourseMaxStudentNumber());
                            course.setCourseAccumulatedStudentNumber(singleSnapshot.getValue(Course.class).getCourseAccumulatedStudentNumber());
                            course.setCourseNumberOfClasses(singleSnapshot.getValue(Course.class).getCourseNumberOfClasses());

                            //4개
                            course.setCourseTutorIntroduction(singleSnapshot.getValue(Course.class).getCourseTutorIntroduction());
                            course.setCourseTargetExplanation(singleSnapshot.getValue(Course.class).getCourseTargetExplanation());
                            course.setCourseIntroduction(singleSnapshot.getValue(Course.class).getCourseIntroduction());
                            course.setCourseCurriculum(singleSnapshot.getValue(Course.class).getCourseCurriculum());

                            course.setCourseHour(singleSnapshot.getValue(Course.class).getCourseHour());
                            course.setCourseDay(singleSnapshot.getValue(Course.class).getCourseDay());

                            course.setCourseEnrolledDate(singleSnapshot.getValue(Course.class).getCourseEnrolledDate());

                            Log.d(TAG, "onDataChange: course get from database : "+ course.toString());


                            enrolledCourseList.add(course);
                        }
                        Log.d(TAG, "onDataChange: enrolledCourseList 에 저장된 course 의 개수 : " + enrolledCourseList.size());
                        loadCourse();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadCourse() {
        mAdapter = new CourseEnrolledListAdapter(EnrolledCourseActivity.this, enrolledCourseList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new CourseEnrolledListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

                Intent intent = new Intent(EnrolledCourseActivity.this, CourseDetailsActivity.class);

                /**
                 * intent Key name 잘못 줘서 계속 null pointer exception error 났잖아..
                 */

                intent.putExtra(getString(R.string.intent_course_detail), enrolledCourseList.get(position));
                startActivity(intent);
            }
        });
    }

    private void initUI() {

        mRecyclerView = findViewById(R.id.enrolledCourseRecyclerView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);

        enrolledCourseList = new ArrayList<>();

        mToolbar = findViewById(R.id.enrolledCourseToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.enrolled_courses);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
