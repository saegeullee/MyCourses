package com.example.saegeullee.myapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseEnrolledListAdapter extends RecyclerView.Adapter<CourseEnrolledListAdapter.ViewHolder> {

    private static final String TAG = "CourseEnrolledListAdapt";

    public OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    private List<Course> courseList;
    private Context context;
    private Course course;

    public CourseEnrolledListAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
        @Override
        public CourseEnrolledListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_row_for_enrolled, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseEnrolledListAdapter.ViewHolder holder, int position) {

            course = courseList.get(position);

            ImageLoader.getInstance().displayImage(course.getCourseTutorImage(), holder.mCourseTutorImage);
            holder.mCourseTutorName.setText(course.getCourseTutorName());
            holder.mCourseTitle.setText(course.getCourseTitle());
            holder.mCourseEnrolledDate.setText(course.getCourseEnrolledDate());

            if(onItemClickListener != null) {

                final int pos = position;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.OnItemClick(pos);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView mCourseTutorImage;
            private TextView mCourseTitle, mCourseEnrolledDate, mCourseTutorName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mCourseTutorImage = itemView.findViewById(R.id.courseTutorImage);
                mCourseTitle = itemView.findViewById(R.id.courseTitle);
                mCourseEnrolledDate = itemView.findViewById(R.id.courseEnrolledDate);
                mCourseTutorName = itemView.findViewById(R.id.tutorName);

            }
        }
    }
