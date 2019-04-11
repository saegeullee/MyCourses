package com.example.saegeullee.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.Adapters.SectionPageAdapter;
import com.example.saegeullee.myapplication.TabFragments.Tab1Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab2Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab3Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab4Fragment;
import com.example.saegeullee.myapplication.models.Course;
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
import com.squareup.picasso.Picasso;

public class CourseOpenedDetailsActivity extends AppCompatActivity {

    private static final String TAG = "CourseDetailsActivity";

    private ImageView courseImage;
    private TextView courseTitle, courseTitleBottom;
    private TextView coursePrice;
    private Button courseCheckEnrolledStudentsListBtn;

    private SectionPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_opened_details);

        initUI();
    }


    private void initUI() {

        courseImage = findViewById(R.id.courseImageEdit);
        courseTitle = findViewById(R.id.detailsCourseTitle);
        courseTitleBottom = findViewById(R.id.courseTitle);

        coursePrice = findViewById(R.id.coursePricePerHour);

        courseCheckEnrolledStudentsListBtn = findViewById(R.id.courseCheckEnrolledStudentsListBtn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get course from main Activity Intent
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_course_detail))) {
            course = intent.getParcelableExtra(getString(R.string.intent_course_detail));

            Log.d(TAG, "initUI: course : " + course.toString());

            //set up course title at the top tool bar
            getSupportActionBar().setTitle(course.getCourseTitle());


//            courseTitle.setText(course.getCourseTitle());
            coursePrice.setText(String.valueOf(course.getCoursePricePerHour()));
            courseTitleBottom.setText(course.getCourseTitle());

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


        courseCheckEnrolledStudentsListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkIntent = new Intent(CourseOpenedDetailsActivity.this, EnrolledStudentsCheckActivity.class);
                checkIntent.putExtra(getString(R.string.intent_course), course);
                startActivity(checkIntent);
            }
        });

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