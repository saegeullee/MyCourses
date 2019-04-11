package com.example.saegeullee.myapplication.NavigationActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.saegeullee.myapplication.Adapters.CourseListAdapter;
import com.example.saegeullee.myapplication.Adapters.CourseListAdapterNewRecommend;
import com.example.saegeullee.myapplication.Adapters.SliderAdapter;
import com.example.saegeullee.myapplication.ChatActivity.ChatActivity;
import com.example.saegeullee.myapplication.CourseDetailsActivity;
import com.example.saegeullee.myapplication.LoginActivity;
import com.example.saegeullee.myapplication.MoreActivity.MoreActivity;
import com.example.saegeullee.myapplication.MoreActivity.NoticeContentActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.Utility.UniversalImageLoader;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private BottomNavigationView bottomNavigationView;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewNewRecommend;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private CourseListAdapter adapter;
    private CourseListAdapterNewRecommend adapterNewRecommend;
    private TextView noCourseMatchedNoticeText;

    //vars
    private ArrayList<Course> courseList;
    private ArrayList<Course> userInterestCourseList;
    private ArrayList<Course> newestCourseList;
    private String currentUserInterest;

    //ViewPager
    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    private SliderAdapter mSliderAdapter;
    private int mCurrentSliderPage;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitymain_final);

        mAuth = FirebaseAuth.getInstance();

        setupFirebaseAuth();

        initUI();
    }

    private Runnable viewPagerAutoSlider = new Runnable() {
        @Override
        public void run() {
            if(mCurrentSliderPage == mDots.length - 1) {
                mViewPager.setCurrentItem(0);
            } else {
                mViewPager.setCurrentItem(mCurrentSliderPage + 1);
            }
            mHandler.postDelayed(this, 5000);
        }
    };

    private void initUI() {

        mRecyclerView = findViewById(R.id.courseRecyclerView);
        courseList = new ArrayList<>();
        newestCourseList = new ArrayList<>();
        userInterestCourseList = new ArrayList<>();

        noCourseMatchedNoticeText = findViewById(R.id.noCourseMatchedNoticeText);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // apply recyclerView smooth scroll
        mRecyclerView.setNestedScrollingEnabled(false);

        //COURSE_NEW_RECOMMEND
        mRecyclerViewNewRecommend = findViewById(R.id.courseRecyclerViewNewRecommend);
//        mRecyclerViewNewRecommend.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerViewNewRecommend.setLayoutManager(new GridLayoutManager(this, 2));

        mRecyclerViewNewRecommend.setNestedScrollingEnabled(false);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mToolbar = findViewById(R.id.main_page_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MyCourses");

        bottomNavigationView = findViewById(R.id.bottomNavViewBar);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        initImageLoader();

        //ViewPager for TOP Part
        mViewPager = findViewById(R.id.mainTopViewPager);
        mDotsLayout = findViewById(R.id.dotsLayout);
        mSliderAdapter = new SliderAdapter(this);

        mSliderAdapter.setOnItemClickListener(new SliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, NoticeContentActivity.class);
                startActivity(intent);
            }
        });

        mViewPager.setAdapter(mSliderAdapter);

        addDotsIndicator(0);
        mViewPager.addOnPageChangeListener(viewListener);

        mHandler = new Handler();
        
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.bottom_nav_home: {

                        break;
                    }

                    case R.id.bottom_nav_search: {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_wishList: {
                        Intent intent = new Intent(MainActivity.this, WishListActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_chat: {
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_more: {
                        Intent intent = new Intent(MainActivity.this, MoreActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

//                    case R.id.bottom_nav_sample: {
//                        Intent intent = new Intent(MainActivity.this, MoreActivity.class);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(0, 0);
//                        break;
//                    }
                }

                return false;
            }
        });
    }

    private void addDotsIndicator(int position) {

        mDots = new TextView[3];
        mDotsLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotsLayout.addView(mDots[i]);

        }

        if(mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            mCurrentSliderPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        courseList.clear();
        userInterestCourseList.clear();
        viewPagerAutoSlider.run();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_courses))
                .orderByValue()
                .limitToFirst(20);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: singleSnapshot : " + singleSnapshot);

                    for(DataSnapshot snapshot : singleSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: snapShot : " + snapshot);

                        final Course course = new Course();

                        /**
                         * course 튜터이름, 튜터 사진은 course 노드에서 전에 저장해놓은 사진, 이름을 가져오는 것이 아니라
                         * users node 에서 가져와서 set 해야 한다. 튜터가 프로필 이름과 이미지를 변경했을 수도 있기 때문
                         * 문제 발생 : 아래의 문제와 동일한 문제 발생 유저의 이름과 이미지를 쿼리하는데 시간이 걸린다.
                         *
                         */

//                        DatabaseReference referenceTutor = FirebaseDatabase.getInstance().getReference();
//
//                        String courseTutorId = snapshot.getValue(Course.class).getCourseTutorId();
//
//                        referenceTutor.child(getString(R.string.dbnode_users))
//                                .child(courseTutorId)
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
//                                        User user = dataSnapshot.getValue(User.class);
//
//                                        course.setCourseTutorName(user.getName());
//                                        course.setCourseTutorImage(user.getProfile_image());
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });

                      course.setCourseTutorName(snapshot.getValue(Course.class).getCourseTutorName());
                      course.setCourseTutorImage(snapshot.getValue(Course.class).getCourseTutorImage());


                        //7개
                        course.setCourseTitle(snapshot.getValue(Course.class).getCourseTitle());
                        course.setCoursePricePerHour(snapshot.getValue(Course.class).getCoursePricePerHour());
                        course.setCourseImage(snapshot.getValue(Course.class).getCourseImage());
                        course.setCourseId(snapshot.getValue(Course.class).getCourseId());
                        course.setCourseTutorId(snapshot.getValue(Course.class).getCourseTutorId());

                        //5개
                        course.setCourseHourPerClass(snapshot.getValue(Course.class).getCourseHourPerClass());
                        course.setCoursePlace(snapshot.getValue(Course.class).getCoursePlace());
                        course.setCourseMaxStudentNumber(snapshot.getValue(Course.class).getCourseMaxStudentNumber());
                        course.setCourseAccumulatedStudentNumber(snapshot.getValue(Course.class).getCourseAccumulatedStudentNumber());
                        course.setCourseNumberOfClasses(snapshot.getValue(Course.class).getCourseNumberOfClasses());

                        //4개
                        course.setCourseTutorIntroduction(snapshot.getValue(Course.class).getCourseTutorIntroduction());
                        course.setCourseTargetExplanation(snapshot.getValue(Course.class).getCourseTargetExplanation());
                        course.setCourseIntroduction(snapshot.getValue(Course.class).getCourseIntroduction());
                        course.setCourseCurriculum(snapshot.getValue(Course.class).getCourseCurriculum());

                        course.setCourseHour(snapshot.getValue(Course.class).getCourseHour());
                        course.setCourseDay(snapshot.getValue(Course.class).getCourseDay());
                        course.setCourseType(snapshot.getValue(Course.class).getCourseType());
                        course.setCourseRegisteredTime(snapshot.getValue(Course.class).getCourseRegisteredTime());

                        Log.d(TAG, "onDataChange: course get from database : " + course.toString());

                        courseList.add(course);
                    }
                }
                loadCourseForInterestOrder();
                loadCourseForNewRecommend();
//                setCourseTutorDetails();
                Log.d(TAG, "onStart: courseList에 추가된 아이템의 개수 : " + courseList.size() + "개 \t @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setCourseTutorDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for(int i = 0; i < courseList.size(); i++) {
            Log.d(TAG, "setCourseTutorDetails: searching for user Id : " + courseList.get(i).getCourseTutorId());
            final int j = i;
            if(courseList.get(i).getCourseTutorId() != null) {
                reference.child(getString(R.string.dbnode_users))
                        .orderByKey()
                        .equalTo(courseList.get(i).getCourseTutorId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onDataChange: dataSnapshot for course tutor detail: " + dataSnapshot);
                                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                                User user = singleSnapshot.getValue(User.class);
                                Log.d(TAG, "onDataChange: got user for course tutor detail : " + user.toString());
                                courseList.get(j).setCourseTutorImage(singleSnapshot.getValue(User.class).getProfile_image());
                                courseList.get(j).setCourseTutorName(singleSnapshot.getValue(User.class).getName());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        }
        loadCourseForNewRecommend();
        loadCourseForInterestOrder();
    }

    /**
     * 문제 발생 : 파이어베이스 DB에서 user Interest 를 읽어오는데 시간이 걸려서 courseType 과 매치를 하지 못했다.
     * 문제 해결 : for(int i = 0; i < courseList.size(); i ++) {
     * 윗 부분을 reference onDataChange 문 안에 넣어서 DB 에서 user Interest 를 읽어온 후 courseType 과 매칭을 시켜줬다.
     */

    private void loadCourseForInterestOrder() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: user dataSnapshot : " + dataSnapshot);

                        User user = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: got user : " + user.toString());

                        if(user != null) {
                            currentUserInterest = user.getInterest();
                            Log.d(TAG, "onDataChange: got user interest : " + user.getInterest());


                            Log.d(TAG, "loadCourseForInterestOrder: courseList size : " + courseList.size());

                            for(int i = 0; i < courseList.size(); i ++) {
                                Log.d(TAG, "loadCourseForInterestOrder: " + i + " 번째 course의 courseType : " + courseList.get(i).getCourseType());
                                if(courseList.get(i).getCourseType().equals(currentUserInterest)) {
                                    userInterestCourseList.add(courseList.get(i));
                                    Log.d(TAG, "loadCourseForInterestOrder: " + courseList.get(i).getCourseTitle() + " course added");
                                }
                            }

                            if(userInterestCourseList.size() == 0) {
                                noCourseMatchedNoticeText.setVisibility(View.VISIBLE);
                            }

                            Log.d(TAG, "loadCourseForInterestOrder: userInterestCourseList size : " + userInterestCourseList.size());

                            adapter = new CourseListAdapter(MainActivity.this, userInterestCourseList);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            adapter.setOnItemClickListener(new CourseListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                    startActivity(courseDetailsIntentForInterest(MainActivity.this, position));
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void loadCourseForNewRecommend() {

        //NEW RECOMMEND COURSE

        adapterNewRecommend = new CourseListAdapterNewRecommend(MainActivity.this, courseList);
        mRecyclerViewNewRecommend.setAdapter(adapterNewRecommend);
        adapterNewRecommend.notifyDataSetChanged();

        adapterNewRecommend.setOnItemClickListener(new CourseListAdapterNewRecommend.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(courseDetailsIntent(MainActivity.this, position));
            }
        });
    }

    public Intent courseDetailsIntentForInterest(Context context, int position) {
        Intent intent = new Intent(context, CourseDetailsActivity.class);
        intent.putExtra(getString(R.string.intent_course_detail), userInterestCourseList.get(position));
        return intent;
    }

    public Intent courseDetailsIntent(Context context, int position) {
        Intent intent = new Intent(context, CourseDetailsActivity.class);
        intent.putExtra(getString(R.string.intent_course_detail), courseList.get(position));
        return intent;
    }

    /**
     * init universal image loader
     */

    private void initImageLoader() {
        UniversalImageLoader imageLoader = new UniversalImageLoader(MainActivity.this);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(viewPagerAutoSlider);
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
    }

    /**
     *  --------------------------------- Set up Firebase ---------------------------------
     */

    private void setupFirebaseAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onRefresh() {
        loadCourseForNewRecommend();
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete() {
        adapter.notifyDataSetChanged();
        adapterNewRecommend.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
