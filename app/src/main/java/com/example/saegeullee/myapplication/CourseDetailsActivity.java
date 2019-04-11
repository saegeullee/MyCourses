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
import com.example.saegeullee.myapplication.ChatActivity.ChatActivity;
import com.example.saegeullee.myapplication.ChatActivity.ChatRoomActivity;
import com.example.saegeullee.myapplication.TabFragments.Tab1Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab2Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab3Fragment;
import com.example.saegeullee.myapplication.TabFragments.Tab4Fragment;
import com.example.saegeullee.myapplication.models.ChatRoom;
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

public class CourseDetailsActivity extends AppCompatActivity implements OnLikeListener {

    private static final String TAG = "CourseDetailsActivity";

    //widgets
    private ImageView courseImage;
    private TextView courseTitle, courseTitleBottom;
    private TextView coursePrice;
    private Button enrollCourseBtn;
    private Button inquireCourseBtn;
    private LikeButton mLikeButton;

    private ViewPager mViewPager;

    //vars
    private Boolean hasEnrolled = false;
    private Course course;
    private ChatRoom chatRoom;

    private SectionPageAdapter mSectionsPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details_test);

        initUI();
        checkIfLiked();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getChatInformation();

    }

    private void checkIfLiked() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.dbnode_liked_courses))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        Log.d(TAG, "onDataChange: dataSnapshot.getChildren() : " + dataSnapshot.getChildren());
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: snapshot : " + snapshot);
                            if( snapshot.getKey().equals(course.getCourseId())) {
                                Log.d(TAG, "onDataChange: liked_course list item : " + snapshot.getKey()
                                        + "matches this course : " + course.getCourseId());
                                mLikeButton.setLiked(true);
                                Log.d(TAG, "onDataChange: liked");
                                break;
                            } else {
                                Log.d(TAG, "onDataChange: snapshot.getKey() : " + snapshot.getKey() + "this course : " + course.getCourseId());
                                mLikeButton.setLiked(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: database error");
                    }
                });
    }

    private void initUI() {

        courseImage = findViewById(R.id.courseImageEdit);
        courseTitle = findViewById(R.id.detailsCourseTitle);
        courseTitleBottom = findViewById(R.id.courseTitle);

        coursePrice = findViewById(R.id.coursePricePerHour);

        enrollCourseBtn = findViewById(R.id.courseEnrollBtn);
        inquireCourseBtn = findViewById(R.id.inquireCourseBtn);

        mLikeButton = findViewById(R.id.heart_button);
        mLikeButton.setOnLikeListener(this);

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
            coursePrice.setText(String.valueOf(course.getCoursePricePerHour()) + "원");
            courseTitleBottom.setText(course.getCourseTitle());

            ImageLoader.getInstance().displayImage(course.getCourseImage(), courseImage);
//            Picasso.get().load(course.getCourseImage()).into(courseImage);
        }

        getCourseTutorInfo();

        //Tab layout implementation

        mSectionsPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText("수업소개");
        tabLayout.getTabAt(1).setText("커리큘럼");
        tabLayout.getTabAt(2).setText("장소/시간");
        tabLayout.getTabAt(3).setText("기대평");

        getEnrolledCourseInfo();

        // Course Enroll Button, Inquire Course Button
        enrollCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!hasEnrolled) {
                    Intent intent = new Intent(CourseDetailsActivity.this, EnrollCourseActivity.class);
                    intent.putExtra(getString(R.string.intent_course), course);
                    startActivity(intent);
                } else {
                    Toast.makeText(CourseDetailsActivity.this, "이미 이 수업을 신청하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 코스 상세 페이지에서 이전에 학생이 튜터와 대화한 기록이 있는지에 대한 정보를 가지고 있어서
         * 대화한 기록이 있다면 해당 chatRoom 에 대한 정보를 인텐트로 넘겨준다.
         */

        inquireCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(CourseDetailsActivity.this, ChatRoomActivity.class);
                chatIntent.putExtra(getString(R.string.intent_course), course);
                chatIntent.putExtra(getString(R.string.intent_chatroom), chatRoom);
                if(chatRoom != null) {
                    Log.d(TAG, "onClick: chatIntent: " + chatRoom.toString());
                }
                startActivity(chatIntent);

            }
        });
    }

    private void getCourseTutorInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbnode_users))
                .child(course.getCourseTutorId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        User user = dataSnapshot.getValue(User.class);

                        course.setCourseTutorImage(dataSnapshot.getValue(User.class).getProfile_image());
                        course.setCourseTutorName(dataSnapshot.getValue(User.class).getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void getChatInformation() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbnode_chatrooms))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null) {
                            Log.d(TAG, "onDataChange: user has no chatroom record");
                        } else {
                            Log.d(TAG, "onDataChange: user has chatroom record");

                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: ChatRoom singleSnapshot : " + singleSnapshot.getValue(ChatRoom.class));

                                ChatRoom mChatRoom = singleSnapshot.getValue(ChatRoom.class);
                                if(mChatRoom != null) {
                                    if (mChatRoom.getCourse_id().equals(course.getCourseId())) {
                                        Log.d(TAG, "onDataChange: user has a chatroom record of this course");
                                        chatRoom = mChatRoom;
                                    } else {
                                        Log.d(TAG, "onDataChange: chat room not matched");
                                    }
                                } else {
                                    Log.d(TAG, "onDataChange: mChatRoom is null");
                                }
                            }

                            if(chatRoom == null) {
                                Log.d(TAG, "onDataChange: user doesn't have a record of chatroom of this course");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void getEnrolledCourseInfo() {
        
        /**
         * Repeated course enroll is not possible
         */

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.dbnode_enrolled_courses))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: enrolled_courses dataSnapshot : " + dataSnapshot);

                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: enrolled_courses singleSnapshot : " + singleSnapshot);

                            if(singleSnapshot.getKey().equals(course.getCourseId())) {

                                Log.d(TAG, "onDataChange: user has already enrolled this class : " + singleSnapshot.getKey());
                                hasEnrolled = true;

                            } else {
                                Log.d(TAG, "onDataChange: user has not enrolled this class before.");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public void liked(LikeButton likeButton) {

        final String courseId = course.getCourseId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.dbnode_liked_courses))
                .child(courseId)
                .setValue(course)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: liked course 저장 완료 : course ID : " + courseId);
                        Toast.makeText(CourseDetailsActivity.this, "찜한 강의에 추가됨", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: liked course 저장 실패");
                Toast.makeText(CourseDetailsActivity.this, "찜한 강의 추가 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        final String courseId = course.getCourseId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.dbnode_liked_courses))
                .child(courseId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: liked course 목록에서 제거 완료 : course ID : " + courseId);
                        Toast.makeText(CourseDetailsActivity.this, "찜한 강의에서 제거됨", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: liked course 제거 실패");
                Toast.makeText(CourseDetailsActivity.this, "찜한 강의 제거 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
