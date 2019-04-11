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
import com.example.saegeullee.myapplication.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnrolledStudentsListAdapter extends RecyclerView.Adapter<EnrolledStudentsListAdapter.ViewHolder> {

    private static final String TAG = "EnrolledStudentsListAda";

    public OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private List<User> enrolledStudentsList;
    private Context context;
    private User user;

    public EnrolledStudentsListAdapter(Context context, List<User> enrolledStudentsList) {
        this.context = context;
        this.enrolledStudentsList = enrolledStudentsList;
    }

    @NonNull
        @Override
        public EnrolledStudentsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_row_for_check_enrolled_students, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EnrolledStudentsListAdapter.ViewHolder holder, int position) {

            user = enrolledStudentsList.get(position);

            ImageLoader.getInstance().displayImage(user.getProfile_image(), holder.mStudentImage);
            holder.mStudentName.setText(user.getName());


            if(onItemClickListener != null) {

                final int pos = position;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.OnItemClick(view, pos);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return enrolledStudentsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView mStudentImage;
            private TextView mStudentName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mStudentImage = itemView.findViewById(R.id.studentImage);
                mStudentName = itemView.findViewById(R.id.studentName);

            }
        }
    }
