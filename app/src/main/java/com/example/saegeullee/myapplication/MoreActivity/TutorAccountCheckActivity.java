package com.example.saegeullee.myapplication.MoreActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.CreateAccountNextActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.Utility.FilePaths;
import com.example.saegeullee.myapplication.models.ChatMessage;
import com.example.saegeullee.myapplication.models.ChatRoom;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorAccountCheckActivity extends AppCompatActivity {

    private static final String TAG = "TutorAccountCheckActivi";

    private CircleImageView mProfileImage;

    //widgets
    private TextView sex, age, job, interest, mUserEmail, mUserPhoneNum;
    public TextView mProfileName;
    private Toolbar mToolbar;

    private String mTutorId;
    private Course course;

    private ImageView courseImage;
    private TextView courseTitle;
    private TextView coursePrice;

    private TextView coursePlace;
    private TextView courseHourPerClass;
    private TextView courseMaxStudentNumber;
    private TextView courseAccumulatedStudentNumber;
    private TextView courseTotalTimes;
    private TextView courseTotalPrice;

    private TextView courseTutorIntroduction;
    private TextView courseTargetExplain;
    private TextView courseIntroduction;

    private RelativeLayout relLayoutForCourseType;
    private TextView courseTypeEditText;
    private String courseTypeSelection;

    private Button courseConfirmBtn;
    private Button courseDenyBtn;

    private ProgressDialog mProgressDialog;

    private FilePaths filePaths;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_account_check);

        getIntentFromAdminActivity();
        initUI();
        getTutorInfo();
    }

    private void getTutorInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(mTutorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        User user = dataSnapshot.getValue(User.class);

                        Log.d(TAG, "onDataChange: user : " + user.toString());

                        mProfileName.setText(dataSnapshot.getValue(User.class).getName());
                        mUserEmail.setText(dataSnapshot.getValue(User.class).getEmail());

                        mUserPhoneNum.setText(dataSnapshot.getValue(User.class).getPhone());
                        age.setText(dataSnapshot.getValue(User.class).getAge());
                        sex.setText(dataSnapshot.getValue(User.class).getSex());
                        job.setText(dataSnapshot.getValue(User.class).getJob());
                        interest.setText(dataSnapshot.getValue(User.class).getInterest());

                        ImageLoader.getInstance().displayImage(dataSnapshot.getValue(User.class).getProfile_image(), mProfileImage);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void getIntentFromAdminActivity() {
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_tutor_id))) {
            mTutorId = intent.getStringExtra(getString(R.string.intent_tutor_id));
            Log.d(TAG, "getIntentFromDialog: get tutor id : " + mTutorId);
        } else {
            Log.d(TAG, "getIntentFromDialog: tutor id null");
        }
        
        if(intent.hasExtra(getString(R.string.intent_course))) {
            course = intent.getParcelableExtra(getString(R.string.intent_course));
            Log.d(TAG, "getIntentFromAdminActivity: get course object : " + course.toString());
        } else {
            Log.d(TAG, "getIntentFromAdminActivity: course is null");
        }
    }

    private void initUI() {

        mProgressDialog = new ProgressDialog(this);

        sex = findViewById(R.id.tutor_account_check_sex_text);
        age = findViewById(R.id.tutor_account_check_age_text);
        job = findViewById(R.id.tutor_account_check_job_text);
        interest = findViewById(R.id.tutor_account_check_interest);

        mProfileImage = findViewById(R.id.tutor_account_check_profile_image);
        mProfileName = findViewById(R.id.tutor_account_check_user_name);
        mUserEmail = findViewById(R.id.tutor_account_check_user_email);
        mUserPhoneNum = findViewById(R.id.tutor_account_check_user_phone_number);

        courseTypeEditText = findViewById(R.id.courseTypeEditText);
        relLayoutForCourseType = findViewById(R.id.relLayoutForCourseType);



        mToolbar = findViewById(R.id.tutor_account_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.tutor_course_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ///

        coursePlace = findViewById(R.id.coursePlace);
        courseHourPerClass = findViewById(R.id.courseHourPerClass);
        courseMaxStudentNumber = findViewById(R.id.courseMaxNumberOfStudent);
        courseAccumulatedStudentNumber = findViewById(R.id.courseAccumulatedNumberOfStudent);
        courseTotalTimes = findViewById(R.id.courseTotalTimes);

        courseImage = findViewById(R.id.courseImageEdit);
        courseTutorIntroduction = findViewById(R.id.courseTutorIntroduction);
        courseTargetExplain = findViewById(R.id.courseTargetExplain);
        courseIntroduction = findViewById(R.id.courseIntroductionID);
        courseTotalPrice = findViewById(R.id.courseTotalPrice);
        coursePrice = findViewById(R.id.coursePricePerHour);
        courseTitle = findViewById(R.id.courseTitle);

        courseConfirmBtn = findViewById(R.id.courseConfirmBtn);
        courseDenyBtn = findViewById(R.id.courseDenyBtn);

        //

        coursePlace.setText(course.getCoursePlace());
        courseHourPerClass.setText("1회 수업 " + String.valueOf(course.getCourseHourPerClass()) + "시간");
        courseMaxStudentNumber.setText("최대 수강인원 " + String.valueOf(course.getCourseMaxStudentNumber()) + "명");
        courseAccumulatedStudentNumber.setText("누적 참여인원 " + String.valueOf(course.getCourseAccumulatedStudentNumber())+ "명");
        courseTotalTimes.setText("총 " + String.valueOf(course.getCourseNumberOfClasses()) + "회 " +
                course.getCourseHourPerClass() * course.getCourseNumberOfClasses() + "시간");

        courseTutorIntroduction.setText(course.getCourseTutorIntroduction());
        courseTargetExplain.setText(course.getCourseTargetExplanation());
        courseIntroduction.setText(course.getCourseIntroduction());

        courseTotalPrice.setText(String.valueOf(course.getCoursePricePerHour() * course.getCourseNumberOfClasses() * course.getCourseHourPerClass())
                + "원");

        courseTitle.setText(course.getCourseTitle());
        coursePrice.setText(String.valueOf(course.getCoursePricePerHour()));
        Picasso.get().load(course.getCourseImage()).into(courseImage);

        //

        courseConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewCourse();
            }
        });


        relLayoutForCourseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] courseTypeListItems = {"영어회화", "댄스", "프로그래밍", "사진", "미술", "테니스", "축구"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TutorAccountCheckActivity.this);
                alertDialogBuilder.setSingleChoiceItems(courseTypeListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        courseTypeSelection = courseTypeListItems[position];
                        courseTypeEditText.setText(courseTypeSelection);
                        course.setCourseType(courseTypeSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.course_type);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private void registerNewCourse() {

//        String title = course.getCourseTitle();
//        String tutorName = course.getCourseTutorName();
//        String price = String.valueOf(course.getCoursePricePerHour());
//        String tutorImageUri = course.getCourseTutorImage();
//        String courseImageUri = course.getCourseImage();
        if(!TextUtils.isEmpty(courseTypeEditText.getText().toString())) {


            mProgressDialog.setMessage("요청 강의 승인 및 등록중입니다.");
            mProgressDialog.show();

            uploadNewPhotoToStorage();

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            /**
             * 강의 DB에 저장, 저장하기 전에 TutorId set 한 뒤 저장
             */

            course.setCourseTutorId(mTutorId);

            /**
             * CourseRegisteredTime set 한 뒤 저장
             */

            course.setCourseRegisteredTime(getCourseRegisteredTimeStamp());


            /**
             * course 객체를 dbnode_courses_search 노드에 저장(코스 검색용)
             */

            reference.child(getString(R.string.dbnode_courses_search))
                    .child(course.getCourseId())
                    .setValue(course)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: course dbnode_courses_search node 에 저장 완료");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: course dbnode_courses_search node 에 저장 실패");
                }
            });

            /**
             * course 객체를 튜터 ID 하위에 저장 (튜터가 개설한 모든 코스 조회용)
             */

            reference.child(getString(R.string.dbnode_courses))
                    .child(mTutorId)
                    .child(course.getCourseId())
                    .setValue(course)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mProgressDialog.dismiss();
                            Toast.makeText(TutorAccountCheckActivity.this, "강의 등록 완료", Toast.LENGTH_SHORT).show();

                            /**
                             * 강의 등록 완료시 해당 튜터에게 채팅 메시지를 보내 강의 등록이 완료되었음을 알림
                             */

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                            // get the new chatRoom unique id
                            String ChatRoomId = reference
                                    .child(getString(R.string.dbnode_chatrooms))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .push().getKey();

                            // create the chatroom
                            ChatRoom mChatRoom = new ChatRoom();
                            mChatRoom.setChatroom_id(ChatRoomId);
                            mChatRoom.setCreator_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            mChatRoom.setTimestamp(getTimeStamp());
                            mChatRoom.setReceiver_id(course.getCourseTutorId());
                            mChatRoom.setCourse_id(course.getCourseId());

                            //insert the new chatroom into the database
                            //for admin
                            reference.child(getString(R.string.dbnode_chatrooms))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(ChatRoomId)
                                    .setValue(mChatRoom)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onComplete: create chatroom SUCCEED");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: create chatroom FAILED");
                                        }
                                    });

                            //for tutor
                            reference.child(getString(R.string.dbnode_chatrooms))
                                    .child(course.getCourseTutorId())
                                    .child(ChatRoomId)
                                    .setValue(mChatRoom);

                            //create a unique id for the message
                            String messageId = reference
                                    .child(getString(R.string.dbnode_chatrooms))
                                    .push().getKey();

                            //insert the first message in the chatroom
                            ChatMessage message = new ChatMessage();

                            message.setMessage("요청하신 " + course.getCourseTitle() + " 강의 등록이 완료되었습니다. " +
                                    "개설 완료 강의 탭에서 해당 강의를 확인하실 수 있습니다.");
                            message.setTimestamp(getTimeStamp());
                            message.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //message save at ADMIN NODE
                            reference.child(getString(R.string.dbnode_chatrooms))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(ChatRoomId)
                                    .child(getString(R.string.field_chat_messages))
                                    .child(messageId)
                                    .setValue(message)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onComplete: message input to the chatroom SUCCEED");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: message input to the chatroom FAILED");
                                        }
                                    });

                            //message save at TUTOR NODE
                            reference.child(getString(R.string.dbnode_chatrooms))
                                    .child(course.getCourseTutorId())
                                    .child(ChatRoomId)
                                    .child(getString(R.string.field_chat_messages))
                                    .child(messageId)
                                    .setValue(message);

                            /**
                             * 강의 등록 완료시 해당 튜터의 istutor 값 true 로 변경
                             */

                            reference.child(getString(R.string.dbnode_users))
                                    .child(mTutorId)
                                    .child(getString(R.string.field_is_tutor))
                                    .setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onComplete: complete set tutor true");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: failed to set tutor true");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(TutorAccountCheckActivity.this, "강의 등록 실패", Toast.LENGTH_SHORT).show();
                }
            });

            /**
             * 강의 요청 NODE 에 저장해놓았던 강의 정보 삭제
             */

            DatabaseReference deleteReference = FirebaseDatabase.getInstance().getReference();

            deleteReference
                    .child(getString(R.string.dbnode_courses_reg_req))
                    .child(mTutorId)
                    .child(course.getCourseId())
                    .removeValue();

            StorageReference deleteRequestedImageReference = FirebaseStorage.getInstance().getReference();
            deleteRequestedImageReference
                    .child(filePaths.FIREBASE_IMAGE_FOR_REQEUSTED_COURSE)
                    .child(mTutorId)
                    .child(course.getCourseId())
                    .delete();

//        StorageReference deleteCourseImageReference = FirebaseStorage.getInstance().getReference();
//        deleteCourseImageReference
//                .child(filePaths.FIREBASE_IMAGE_FOR_REQEUSTED_COURSE)
//                .child(mTutorId)
//                .child(course.getCourseId())
//                .delete();
        } else {
            Toast.makeText(this, getString(R.string.course_type) + " 를 반드시 지정해야 코스 등록이 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(new Date());
    }

    private String getCourseRegisteredTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(new Date());
    }

    private void uploadNewPhotoToStorage() {

        filePaths = new FilePaths();

        final String courseId = course.getCourseId();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//                .child(filePaths.FIREBASE_IMAGE_FOR_COURSE + "/" + courseId + "/" + "tutor_image");

//        StorageReference courseImageReference = FirebaseStorage.getInstance().getReference();
//                .child(filePaths.FIREBASE_IMAGE_FOR_COURSE + "/"  + courseId +"/" + "course_image");

        Log.d(TAG, "uploadNewPhotoToStorage: getCourseTutorImage() : " + course.getCourseTutorImage());
        Log.d(TAG, "uploadNewPhotoToStorage: course.getCourseTutorId() : " + course.getCourseTutorId());

        /**
         * course.getCourseId() 말고 mTutorId 에 인텐트로 가져온 TutorId 정보 저장해둠
         */

        storageReference
                .child("images")
                .child("courses")
                .child(mTutorId)
                .child(courseId)
                .child("tutor_image")
                .putFile(Uri.parse(course.getCourseTutorImage()))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri firebaseURL = taskSnapshot.getDownloadUrl();
                        Toast.makeText(TutorAccountCheckActivity.this, "튜터 이미지 업로드 완료", Toast.LENGTH_SHORT).show();

                        FirebaseDatabase.getInstance().getReference()
                                .child(getString(R.string.dbnode_courses))
                                .child(course.getCourseTutorId())
                                .child(courseId)
                                .child(getString(R.string.field_tutor_image))
                                .setValue(firebaseURL.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(TutorAccountCheckActivity.this, "튜터 이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: 튜터 이미지 업로드 실패");
            }
        });

        storageReference
                .child("images")
                .child("courses")
                .child(mTutorId)
                .child(courseId)
                .child("tutor_image")
                .putFile(Uri.parse(course.getCourseImage()))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri firebaseURL = taskSnapshot.getDownloadUrl();
                        Toast.makeText(TutorAccountCheckActivity.this, "코스 이미지 업로드 완료", Toast.LENGTH_SHORT).show();

                        FirebaseDatabase.getInstance().getReference()
                                .child(course.getCourseTutorId())
                                .child(getString(R.string.dbnode_courses))
                                .child(courseId)
                                .child(getString(R.string.field_course_image))
                                .setValue(firebaseURL.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(TutorAccountCheckActivity.this, "코스 이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: 코스 이미지 업로드 실패");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
