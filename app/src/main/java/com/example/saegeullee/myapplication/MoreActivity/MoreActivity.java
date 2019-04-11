package com.example.saegeullee.myapplication.MoreActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.ChatActivity.ChatActivity;
import com.example.saegeullee.myapplication.NavigationActivity.MainActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.NavigationActivity.SearchActivity;
import com.example.saegeullee.myapplication.NavigationActivity.WishListActivity;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreActivity extends AppCompatActivity {

    private static final String TAG = "MoreActivity";

    //widgets
    private BottomNavigationView bottomNavigationView;
    private CircleImageView mProfileImage;
    private TextView mUserName;
    private ConstraintLayout mTopSection;
    private Button mRegisterTutorBtn;

    private User user;
    private Boolean isAdmin = false;

    private TextView numberOfAppliedCourses, numberOfRequestedCourses, numberOfMyCourses;
    private RelativeLayout relAppliedCourses, relRequestedCoursesForTutor, relMyCoursesForTutor;
    private RelativeLayout relNotice;
    private int appliedCoursesNum;
    private int myOpenendCoursesNum;

    //firebase
    private FirebaseAuth mAuth;

    private Boolean isRequested = false;
    private Boolean isTutor = false;

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: in");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        mAuth = FirebaseAuth.getInstance();
        initUI();

        Log.d(TAG, "onCreate: out");
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: in");
        super.onStart();

        getUserData();

    }

    private void initUI() {


        mProfileImage = findViewById(R.id.circleImageProfile);
        mUserName = findViewById(R.id.userName);
        mTopSection = findViewById(R.id.moreActivityTopPart);
        mRegisterTutorBtn = findViewById(R.id.registerTutorBtn);

        // Applied Course section
        numberOfAppliedCourses = findViewById(R.id.numberOfAppliedCourses);
        numberOfRequestedCourses = findViewById(R.id.numberOfRequestedCourses);
        numberOfMyCourses = findViewById(R.id.numberOfMyCourses);

        relAppliedCourses = findViewById(R.id.relAppliedCourses);
        relRequestedCoursesForTutor = findViewById(R.id.relRequestedCoursesForTutor);
        relMyCoursesForTutor = findViewById(R.id.relMyCoursesForTutor);

        relNotice = findViewById(R.id.relNotice);

        relNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, NoticeActivity.class);
                startActivity(intent);
            }
        });

        relAppliedCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, EnrolledCourseActivity.class);
                startActivity(intent);
            }
        });

        relRequestedCoursesForTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRequested) {
                    //startActivity
                    Intent intent = new Intent(MoreActivity.this, AppliedCourseCheckActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MoreActivity.this, "튜터 전용 공간입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        relMyCoursesForTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTutor) {
                    Intent intent = new Intent(MoreActivity.this, OpenedCourseActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MoreActivity.this, "강의 개설 완료 후 이용하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);


        mTopSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, AccountSettingActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(mProfileImage, "imageTransition");
                pairs[1] = new Pair<View, String>(mUserName, "nameTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MoreActivity.this, pairs);

                startActivity(intent, options.toBundle());
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.bottom_nav_home: {
                        Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_search: {
                        Intent intent = new Intent(MoreActivity.this, SearchActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_wishList: {
                        Intent intent = new Intent(MoreActivity.this, WishListActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_chat: {
                        Intent intent = new Intent(MoreActivity.this, ChatActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_more: {

                        break;
                    }
                }
                return false;
            }
        });

        mRegisterTutorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isTutor) {
                    Intent intent = new Intent(MoreActivity.this, AddCourseActivity.class);
                    startActivity(intent);
                } else if (isRequested) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MoreActivity.this);
                    alertDialogBuilder.setTitle("심사 대기중입니다.");

                    alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MoreActivity.this);
                    alertDialogBuilder.setTitle(getString(R.string.reg_tutor_notice));
                    alertDialogBuilder.setMessage(getString(R.string.reg_tutor_content));

                    alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MoreActivity.this, AddCourseActivity.class);
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }


    private void getUserData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);

                user = dataSnapshot.getValue(User.class);

                if (user != null) {

                    if (!(user.getProfile_image().equals(""))) {
                        ImageLoader.getInstance().displayImage(user.getProfile_image(), mProfileImage);
                    }

                    mUserName.setText(user.getName());
                    Boolean getAdmin = user.getAdmin();
                    if(getAdmin) {
                        isAdmin = true;
                    }
                    Log.d(TAG, "onDataChange: isAdmin ? : " + isAdmin);

                    Boolean getIsRequested = user.getRequested();
                    if(getIsRequested) {
                        isRequested = true;
                    }
                    Log.d(TAG, "onDataChange: isRequested ? : " + isRequested);

                    Boolean getIsTutor = user.getTutor();
                    if(getIsTutor) {
                        isTutor = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * check number of tutor requested courses and opened courses
         */

        DatabaseReference courseRequestReference = FirebaseDatabase.getInstance().getReference();

        courseRequestReference
                .child(getString(R.string.dbnode_courses_reg_req))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot for course request : " + dataSnapshot);
                        if(dataSnapshot.getValue() != null) {
                            numberOfRequestedCourses.setText("1");
                        } else {
                            numberOfRequestedCourses.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        DatabaseReference courseOpenedReference = FirebaseDatabase.getInstance().getReference();

        myOpenendCoursesNum = 0;

        courseOpenedReference
                .child(getString(R.string.dbnode_courses))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot for opened course : " + dataSnapshot);
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.getValue() != null) {
                        myOpenendCoursesNum += 1;
                        numberOfMyCourses.setText(String.valueOf(myOpenendCoursesNum));
                    } else {
                        numberOfMyCourses.setText("0");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference appliedCourseReference = FirebaseDatabase.getInstance().getReference();

        appliedCoursesNum = 0;

        appliedCourseReference
                .child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.dbnode_enrolled_courses))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: applied courses dataSnapshot : " + dataSnapshot);
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            appliedCoursesNum += 1;
                        }

                        numberOfAppliedCourses.setText(String.valueOf(appliedCoursesNum));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



//
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: in");
        super.onResume();
        Log.d(TAG, "onResume: out");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: in");
        super.onPause();
        Log.d(TAG, "onPause: out");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: in");
        super.onStop();
        Log.d(TAG, "onStop: out");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: in");
        super.onDestroy();
        Log.d(TAG, "onDestroy: out");
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: in");
        super.onRestart();
        Log.d(TAG, "onRestart: out");
    }
}