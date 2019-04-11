package com.example.saegeullee.myapplication;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saegeullee.myapplication.Adapters.SectionPageAdapter;
import com.example.saegeullee.myapplication.TabFragments.Tab1Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab2Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab3Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab4Fragment;
import com.example.saegeullee.myapplication.models.Course;
import com.squareup.picasso.Picasso;

public class TutorCourseCheckActivity extends AppCompatActivity {

    private static final String TAG = "CourseDetailsActivity";

    private ImageView courseImage;
    private TextView courseTitle;
    private TextView coursePrice;
    private Button registerCourseBtn;
    private Button realTimeTalkBtn;

    private SectionPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_course_check);

        initUI();

    }

    private void initUI() {

        courseImage = findViewById(R.id.courseImageEdit);
        courseTitle = findViewById(R.id.detailsCourseTitle);
        coursePrice = findViewById(R.id.coursePricePerHour);

        registerCourseBtn = findViewById(R.id.registerCourseBtn);
        realTimeTalkBtn = findViewById(R.id.realTimeTalkBtn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /**
         * get course from intent
         */
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_course_detail))) {
            course = intent.getParcelableExtra(getString(R.string.intent_course_detail));

            Log.d(TAG, "initUI: course : " + course.toString());

            //set up course title at the top tool bar
            getSupportActionBar().setTitle(course.getCourseTitle());

//            courseTitle.setText(course.getCourseTitle());
            coursePrice.setText(String.valueOf(course.getCoursePricePerHour()));

            Picasso.get().load(course.getCourseImage()).into(courseImage);
        }


        //Tab layout implementation

        mSectionsPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText("수업소개");
        tabLayout.getTabAt(1).setText("커리큘럼");
        tabLayout.getTabAt(2).setText("장소/시간");
        tabLayout.getTabAt(3).setText("리뷰");

    }

    //Tap Fragment
    private void setupViewPager(ViewPager mViewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.bundle_course), course);

        Tab1Fragment tab1Fragment = new Tab1Fragment();
        tab1Fragment.setArguments(bundle);
        adapter.addFragment(tab1Fragment);

        Tab2Fragment tab2Fragment = new Tab2Fragment();
        tab2Fragment.setArguments(bundle);
        adapter.addFragment(tab2Fragment);

        Tab3Fragment tab3Fragment = new Tab3Fragment();
        tab3Fragment.setArguments(bundle);
        adapter.addFragment(tab3Fragment);

        Tab4Fragment tab4Fragment = new Tab4Fragment();
        tab4Fragment.setArguments(bundle);
        adapter.addFragment(tab4Fragment);

        mViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
