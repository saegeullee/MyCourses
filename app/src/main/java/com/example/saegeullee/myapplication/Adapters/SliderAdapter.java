package com.example.saegeullee.myapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private SliderAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(SliderAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.programming,
            R.drawable.drawing,
            R.drawable.takingpicture
    };

    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.viewpager_slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slideLayoutImage);
        TextView textView = view.findViewById(R.id.textView);
        textView.bringToFront();
        slideImageView.setImageResource(slide_images[position]);

        if(onItemClickListener !=null) {
            slideImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
