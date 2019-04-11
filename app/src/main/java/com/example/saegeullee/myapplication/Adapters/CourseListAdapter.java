package com.example.saegeullee.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.CourseDetailsActivity;
import com.example.saegeullee.myapplication.R;
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
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    private static final String TAG = "CourseListAdapter";

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    private Context context;
    private List<Course> courseList;

    private String courseImageUri;
    private String courseTutorImageUri;

    private Course course;

    public CourseListAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_row, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CourseListAdapter.ViewHolder holder, int position) {
//        final Context context = holder.courseTitle.getContext();
        String currencyType = "원";
        course = courseList.get(position);

        courseImageUri = course.getCourseImage();
        courseTutorImageUri = course.getCourseTutorImage();

        //ImageLoader
        ImageLoader.getInstance().displayImage(courseImageUri, holder.courseImageView);
//        ImageLoader.getInstance().displayImage(courseTutorImageUri, holder.courseTutorImageView);

        holder.courseTitle.setText(course.getCourseTitle());
        holder.coursePrice.setText(String.valueOf(course.getCoursePricePerHour()) + currencyType);
//        holder.courseTutorName.setText(course.getCourseTutorName());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(context.getString(R.string.dbnode_users))
                .child(course.getCourseTutorId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        User user = dataSnapshot.getValue(User.class);

                        ImageLoader.getInstance().displayImage(dataSnapshot.getValue(User.class).getProfile_image(), holder.courseTutorImageView);
                        holder.courseTutorName.setText(dataSnapshot.getValue(User.class).getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        if(onItemClickListener != null) {

            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(pos);
                }
            });
        }
//        try {
//            if( Uri.parse(course.getCourseImage()) != null) {
//                photoExample = MediaStore.Images.Media.getBitmap(context.getContentResolver() , Uri.parse(course.getCourseImage()));
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "onBindViewHolder: ", e.getCause());
//        }
//        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), course.getCourseImageResourceId(context));
//        Palette.from(photoExample).generate(new Palette.PaletteAsyncListener() {
//            @Override
//            public void onGenerated(@NonNull Palette palette) {
//                int bgColor = palette.getMutedColor(ContextCompat.getColor(context, android.R.color.black));
//                holder.courseTutorImageView.setBorderColor(bgColor);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView courseTitle;
        public TextView courseTutorName;
        public TextView coursePrice;
        public ImageView courseImageView;
        public CircleImageView courseTutorImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseTutorName = itemView.findViewById(R.id.courseTutorName);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            courseImageView = itemView.findViewById(R.id.courseImageEdit);
            courseTutorImageView = itemView.findViewById(R.id.courseAuthorImage);
        }
    }
}

