package com.example.saegeullee.myapplication.TabFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    //widgets
    private TextView courseCurriculumExplain;

    //vars
    private Course course;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            course = getArguments().getParcelable(getString(R.string.bundle_course));
            Log.d(TAG, "onCreate: course get through bundle : " + course.toString() + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement2_layout, container, false);

        courseCurriculumExplain = view.findViewById(R.id.courseCurriculumExplain);

        setDataToView();

        return view;
    }

    private void setDataToView() {
        courseCurriculumExplain.setText(course.getCourseCurriculum());
    }
}