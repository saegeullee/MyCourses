package com.example.saegeullee.myapplication.TabFragments;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.CourseDetailsActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Tab1Fragment extends Fragment  {
    private static final String TAG = "Tab1Fragment";

    private TextView coursePlace;
    private TextView courseHourPerClass;
    private TextView courseMaxStudentNumber;
    private TextView courseAccumulatedStudentNumber;
    private TextView courseTotalTimes;
    private TextView courseTotalPrice;

    private ImageView courseImage;
    private ImageView courseTutorImage;
    private TextView courseTutorName;
    private TextView courseTutorIntroduction;
    private TextView courseTargetExplain;
    private TextView courseIntroduction;
    private TextView coursePricePerHour;

    //Bundle 에 담은 User 객체
    private Course course;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            course = getArguments().getParcelable(getString(R.string.bundle_course));
//            Log.d(TAG, "onCreate: course get through bundle : " + course.toString() + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//            Query query = reference.child(getString(R.string.dbnode_courses))
//                    .child(courseId);
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
//
//                    course = dataSnapshot.getValue(Course.class);
//
//                    Log.d(TAG, "onDataChange: course : " + course.toString());
//
//                    setDataToView();
//                }

//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement1_layout, container, false);

        coursePlace = view.findViewById(R.id.coursePlace);
        courseHourPerClass = view.findViewById(R.id.courseHourPerClass);
        courseMaxStudentNumber = view.findViewById(R.id.courseMaxNumberOfStudent);
        courseAccumulatedStudentNumber = view.findViewById(R.id.courseAccumulatedNumberOfStudent);
        courseTotalTimes = view.findViewById(R.id.courseTotalTimes);

        courseImage = view.findViewById(R.id.courseImageEdit);
        courseTutorImage = view.findViewById(R.id.courseTutorImageEdit);
        courseTutorName = view.findViewById(R.id.courseTutorName);
        courseTutorIntroduction = view.findViewById(R.id.courseTutorIntroduction);
        courseTargetExplain = view.findViewById(R.id.courseTargetExplain);
        courseIntroduction = view.findViewById(R.id.courseIntroductionID);
        courseTotalPrice = view.findViewById(R.id.courseTotalPrice);

        setDataToView();

        return view;
    }

    private void setDataToView() {

        coursePlace.setText(course.getCoursePlace());
        courseHourPerClass.setText("1회 수업 " + String.valueOf(course.getCourseHourPerClass()) + "시간");
        courseMaxStudentNumber.setText("최대 수강인원 " + String.valueOf(course.getCourseMaxStudentNumber()) + "명");
        courseAccumulatedStudentNumber.setText("누적 참여인원 " + String.valueOf(course.getCourseAccumulatedStudentNumber())+ "명");
        courseTotalTimes.setText("총 " + String.valueOf(course.getCourseNumberOfClasses()) + "회 " +
                course.getCourseHourPerClass() * course.getCourseNumberOfClasses() + "시간");

        courseTutorName.setText(course.getCourseTutorName());
        courseTutorIntroduction.setText(course.getCourseTutorIntroduction());
        courseTargetExplain.setText(course.getCourseTargetExplanation());
        courseIntroduction.setText(course.getCourseIntroduction());

        courseTotalPrice.setText(String.valueOf(course.getCoursePricePerHour() * course.getCourseNumberOfClasses() * course.getCourseHourPerClass())
                + "원");

        ImageLoader.getInstance().displayImage(course.getCourseTutorImage(), courseTutorImage);

    }


}