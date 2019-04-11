package com.example.saegeullee.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseListAdapterForRequest extends RecyclerView.Adapter<CourseListAdapterForRequest.ViewHolder> {

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

    private Bitmap photoExample;

    public CourseListAdapterForRequest(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseListAdapterForRequest.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_row_for_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CourseListAdapterForRequest.ViewHolder holder, int position) {
        final Context context = holder.courseTitle.getContext();
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

