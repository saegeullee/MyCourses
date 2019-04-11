package com.example.saegeullee.myapplication.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.MoreActivity.TutorAccountCheckActivity;
import com.example.saegeullee.myapplication.TutorCourseCheckActivity;
import com.example.saegeullee.myapplication.models.Course;

public class RequestedCourseCheckDialog extends DialogFragment {

    private static final String TAG = "RequestedCourseCheckDia";

    private TextView checkTutorInfo, checkCourseInfo;
    
    private String mTutorId;
    private Course course;

    //Create a new Bundle and set the arguments to avoid a null pointer
    public RequestedCourseCheckDialog() {
        super();
        setArguments(new Bundle());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started");
        
        mTutorId = getArguments().getString(getString(R.string.bundle_tutor));
        if(mTutorId != null) {
            Log.d(TAG, "onCreate: get the tutor user id : " + mTutorId);

        } else {
            Log.d(TAG, "onCreate: tutor is null");
        }

        course = getArguments().getParcelable(getString(R.string.bundle_course));
        if(course != null) {
            Log.d(TAG, "onCreate: get the course : " + course.toString());
        } else {
            Log.d(TAG, "onCreate: course is null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_coursecheck, container, false);

        checkTutorInfo = view.findViewById(R.id.checkTutorInfo);
        checkCourseInfo = view.findViewById(R.id.checkCourseInfo);

        checkTutorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TutorAccountCheckActivity.class);
                intent.putExtra(getString(R.string.intent_tutor_id), mTutorId);
                startActivity(intent);
            }
        });

        checkCourseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TutorCourseCheckActivity.class);
                intent.putExtra(getString(R.string.intent_course), course);
                startActivity(intent);
            }
        });

        return view;
    }

}
