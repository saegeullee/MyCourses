package com.example.saegeullee.myapplication.MoreActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.saegeullee.myapplication.Adapters.CourseListAdapterNewRecommend;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    private Button addCourseBtn;
    private RecyclerView recyclerViewCourseRequestedList;
    private CourseListAdapterNewRecommend mAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ArrayList<Course> requestedCourseList;
    private ArrayList<String> requestedCourseTutorIdList;

    private String userId = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initUI();
        getTutorData();

    }

    private void getTutorData() {

    }

    private void initUI() {

        addCourseBtn = findViewById(R.id.addCourseBtn);
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });

        recyclerViewCourseRequestedList = findViewById(R.id.recyclerViewCourseRequestedList);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewCourseRequestedList.setLayoutManager(staggeredGridLayoutManager);

        requestedCourseList = new ArrayList<>();
        requestedCourseTutorIdList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getCourseData();
    }

    private void getCourseData() {

        requestedCourseList.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_courses_reg_req))
                .orderByKey()
                .limitToFirst(6);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot courseSnapshot : singleSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: courseSnapshot : " + courseSnapshot);

                        final Course course = new Course();

                        //7개
                        course.setCourseTitle(courseSnapshot.getValue(Course.class).getCourseTitle());
                        course.setCourseTutorName(courseSnapshot.getValue(Course.class).getCourseTutorName());
                        course.setCoursePricePerHour(courseSnapshot.getValue(Course.class).getCoursePricePerHour());
                        course.setCourseTutorImage(courseSnapshot.getValue(Course.class).getCourseTutorImage());
                        course.setCourseImage(courseSnapshot.getValue(Course.class).getCourseImage());
                        course.setCourseId(courseSnapshot.getValue(Course.class).getCourseId());
                        course.setCourseTutorId(singleSnapshot.getValue(Course.class).getCourseTutorId());

                        //5개
                        course.setCourseHourPerClass(courseSnapshot.getValue(Course.class).getCourseHourPerClass());
                        course.setCoursePlace(courseSnapshot.getValue(Course.class).getCoursePlace());
                        course.setCourseMaxStudentNumber(courseSnapshot.getValue(Course.class).getCourseMaxStudentNumber());
                        course.setCourseAccumulatedStudentNumber(courseSnapshot.getValue(Course.class).getCourseAccumulatedStudentNumber());
                        course.setCourseNumberOfClasses(courseSnapshot.getValue(Course.class).getCourseNumberOfClasses());

                        //4개
                        course.setCourseTutorIntroduction(courseSnapshot.getValue(Course.class).getCourseTutorIntroduction());
                        course.setCourseTargetExplanation(courseSnapshot.getValue(Course.class).getCourseTargetExplanation());
                        course.setCourseIntroduction(courseSnapshot.getValue(Course.class).getCourseIntroduction());
                        course.setCourseCurriculum(courseSnapshot.getValue(Course.class).getCourseCurriculum());

                        course.setCourseHour(courseSnapshot.getValue(Course.class).getCourseHour());
                        course.setCourseDay(courseSnapshot.getValue(Course.class).getCourseDay());


                        Log.d(TAG, "onDataChange: course get from database : " + course.toString());

                        String tutorId = courseSnapshot.getValue(Course.class).getCourseTutorId();

                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference();

                        Query queryUser = userReference
                                .child(getString(R.string.dbnode_users))
                                .child(tutorId);

                        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);

                                if(dataSnapshot.getValue(User.class) != null) {
                                    userId = dataSnapshot.getValue(User.class).getUser_id();
                                    Log.d(TAG, "onDataChange: userId : " + userId);
                                    requestedCourseTutorIdList.add(userId);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        requestedCourseList.add(course);
                    }
                }
                Log.d(TAG, "onStart: requestedCourseList 에 추가된 아이템의 개수 : " + requestedCourseList.size() + "개 \t @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                loadCourseList();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

        private void loadCourseList(){
        mAdapter = new CourseListAdapterNewRecommend(AdminActivity.this, requestedCourseList);
        recyclerViewCourseRequestedList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new CourseListAdapterNewRecommend.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(AdminActivity.this, TutorAccountCheckActivity.class);
                intent.putExtra(getString(R.string.intent_tutor_id), requestedCourseTutorIdList.get(position));
                intent.putExtra(getString(R.string.intent_course), requestedCourseList.get(position));
                startActivity(intent);
            }
        });

    }
}
