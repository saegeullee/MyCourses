package com.example.saegeullee.myapplication.MoreActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.saegeullee.myapplication.Adapters.CourseListAdapter;
import com.example.saegeullee.myapplication.CourseDetailsActivity;
import com.example.saegeullee.myapplication.CourseOpenedDetailsActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OpenedCourseActivity extends AppCompatActivity {

    private static final String TAG = "OpenedCourseActivity";

    private Toolbar mToolbar;

    //Course request
    private ArrayList<Course> coursesOpenedList;
    private RecyclerView courseOpenedRecyclerView;
    private CourseListAdapter courseOpenedAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_course);

        initUI();
    }

    private void initUI() {
        coursesOpenedList = new ArrayList<>();

        //Init Requested Course
        courseOpenedRecyclerView = findViewById(R.id.openedCourseList);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        courseOpenedRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        courseOpenedRecyclerView.setNestedScrollingEnabled(false);

        mToolbar = findViewById(R.id.openedCourseCheck);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.course_opened_completed_for_tutor));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: in");
        super.onStart();

        coursesOpenedList.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_courses))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: dataSnapshot.getChildren() : " + dataSnapshot.getChildren());

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

                    Log.d(TAG, "onDataChange: course get from database : "+ course.toString());

                    coursesOpenedList.add(course);
                }
                Log.d(TAG, "onStart: coursesRequestedList 에 추가된 아이템의 개수 : " + coursesOpenedList.size() + "개 \t @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                loadCourseList();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onStart: out");
    }

    private void loadCourseList() {
        courseOpenedAdapter = new CourseListAdapter(OpenedCourseActivity.this, coursesOpenedList);
        courseOpenedRecyclerView.setAdapter(courseOpenedAdapter);
        courseOpenedAdapter.notifyDataSetChanged();

        courseOpenedAdapter.setOnItemClickListener(new CourseListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {

                startActivity(courseDetailsIntent(OpenedCourseActivity.this, position));

            }
        });
    }

    public Intent courseDetailsIntent(Context context, int position) {
        Intent intent = new Intent(context, CourseOpenedDetailsActivity.class);
        intent.putExtra(getString(R.string.intent_course_detail), coursesOpenedList.get(position));
        return intent;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
