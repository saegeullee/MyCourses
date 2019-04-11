package com.example.saegeullee.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseListAdapterNewRecommend extends RecyclerView.Adapter<CourseListAdapterNewRecommend.ViewHolder> {

    private static final String TAG = "CourseListAdapter";

    private Context context;
    private List<Course> courseList;

    private String courseImageUri;
    private String courseTutorImageUri;

    public CourseListAdapterNewRecommend(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseListAdapterNewRecommend.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_row_new_recommend, parent, false);

        return new CourseListAdapterNewRecommend.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CourseListAdapterNewRecommend.ViewHolder holder, final int position) {


//        final Context context = holder.courseTitle.getContext();
        String currencyType = "원";
        Course course = courseList.get(position);

        courseImageUri = course.getCourseImage();
        courseTutorImageUri = course.getCourseTutorImage();

        //ImageLoader
        ImageLoader.getInstance().displayImage(courseImageUri, holder.courseImageView);
        ImageLoader.getInstance().displayImage(courseTutorImageUri, holder.courseTutorImageView);

//        Picasso.get().load(courseImageUri).into(holder.courseImageView);
//        Picasso.get().load(courseTutorImageUri).into(holder.courseTutorImageView);
        holder.courseTitle.setText(course.getCourseTitle());
        holder.coursePrice.setText(String.valueOf(course.getCoursePricePerHour()) + currencyType);
        holder.courseTutorName.setText(course.getCourseTutorName());
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        reference.child(context.getString(R.string.dbnode_users))
//                .child(course.getCourseTutorId())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
//                        User user = dataSnapshot.getValue(User.class);
//
//                        ImageLoader.getInstance().displayImage(dataSnapshot.getValue(User.class).getProfile_image(), holder.courseTutorImageView);
//                        holder.courseTutorName.setText(dataSnapshot.getValue(User.class).getName());
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });




//        if(onItemClickListener != null) {
//
////            final int pos = holder.getAdapterPosition();
////            if(pos != RecyclerView.NO_POSITION) {
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        onItemClickListener.onItemClick(position);
//                    }
//                });
////            }
//        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView courseTitle;
        public TextView courseTutorName;
        public TextView coursePrice;
        public ImageView courseImageView;
        public CircleImageView courseTutorImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseTutorName = itemView.findViewById(R.id.courseTutorName);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            courseImageView = itemView.findViewById(R.id.courseImageEdit);
            courseTutorImageView = itemView.findViewById(R.id.courseAuthorImage);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

}
