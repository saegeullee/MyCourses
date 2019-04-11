package com.example.saegeullee.myapplication.MoreActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.Utility.FilePaths;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddCourseActivity extends AppCompatActivity {

    private static final String TAG = "AddCourseActivity";

    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String courseId;

    //widgets
    private EditText courseTitle;
    private EditText courseTutorName;
    private EditText coursePricePerHour;
    private ImageView courseImage;
    private ImageView courseTutorImage;
    private Button courseRegisterBtn;

    private RelativeLayout relCourseDay, relCourseHour, relCoursePlace, relCourseMaxNumOfStudent, relCourseMaxNumOfClasses;
    private String courseDaySelection, courseHourSelection, coursePlaceSelection, courseMaxNumOfStudentSelection, courseMaxNumOfClassesSelection;
    private TextView courseDayDataText, courseHourDataText, coursePlaceDataText, courseMaxNumOfStudentDataText, courseTotalNumOfClassesDataText;

    //vars
    private Course course;
    private User mUser;

    private ProgressDialog mProgressDialogue;

    public static final int GALLERY_CODE_PROFILE_IMAGE = 1;
    public static final int GALLERY_CODE_COURSE_IMAGE = 2;

    public static final int CROP_TUTOR_IMAGE_ACTIVITY_REQUEST_CODE = 3;
    public static final int CROP_COURSE_IMAGE_ACTIVITY_REQUEST_CODE = 4;

    private Uri mProfileImageUri;
    private Uri mProfileResultUri;
    private Uri mCourseImageUri;
    private Uri mCourseResultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        initUI();
        getTutorImage();
    }

    private void getTutorImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        mUser = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: got user : " + mUser.toString());

                        if(mUser != null) {
                            ImageLoader.getInstance().displayImage(mUser.getProfile_image(), courseTutorImage);
                            courseTutorName.setText(mUser.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initUI() {

        courseTitle = findViewById(R.id.courseTitleEdit);
        courseTutorName = findViewById(R.id.courseTutorNameEdit);
        courseTutorName.setFocusable(false);

        coursePricePerHour = findViewById(R.id.coursePriceEdit);
        courseTitle.clearFocus();
        courseTutorName.clearFocus();
        coursePricePerHour.clearFocus();

        courseImage = findViewById(R.id.courseImageEdit);
        courseTutorImage= findViewById(R.id.courseTutorImageEdit);
        courseRegisterBtn = findViewById(R.id.courseRegisterBtn);

        mProgressDialogue = new ProgressDialog(this);

        relCourseDay = findViewById(R.id.relLayoutCourseDay);
        relCourseHour = findViewById(R.id.relLayoutCourseHour);
        relCoursePlace = findViewById(R.id.relLayoutCoursePlace);
        relCourseMaxNumOfStudent = findViewById(R.id.relLayoutCourseMaxNumberOfStudents);
        relCourseMaxNumOfClasses = findViewById(R.id.relLayoutCourseTotalNumberOfTimes);

        courseDayDataText = findViewById(R.id.courseDayDataText);
        courseHourDataText = findViewById(R.id.courseHourDataText);
        coursePlaceDataText = findViewById(R.id.coursePlaceDataText);
        courseMaxNumOfStudentDataText = findViewById(R.id.courseMaxNumberOfStudentDataText);
        courseTotalNumOfClassesDataText = findViewById(R.id.courseTotalNumberOfTimesDataText);


//        courseTutorImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent tutorImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                tutorImageIntent.setType("image/*");
//                startActivityForResult(tutorImageIntent, GALLERY_CODE_PROFILE_IMAGE);
//            }
//        });

        courseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent courseImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                courseImageIntent.setType("image/*");
//                startActivityForResult(courseImageIntent, GALLERY_CODE_COURSE_IMAGE);

                Intent intent = CropImage.activity(mCourseImageUri)
                        .setAspectRatio(3, 2)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(AddCourseActivity.this);
                startActivityForResult(intent, CROP_COURSE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        courseRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: courseRegiser Button Clicked ");

                registerNewCourse();

            }
        });

        /**
         * For Relative Layout selection
         */

        relCourseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] courseDayListItems = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddCourseActivity.this);
                alertDialogBuilder.setSingleChoiceItems(courseDayListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        courseDaySelection = courseDayListItems[position];
                        courseDayDataText.setText(courseDaySelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.course_day);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relCourseHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] courseHourItems = {"12시~2시", "2시~4시", "4시~6시", "6시~8시", "8시~10시", "10시~12시"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddCourseActivity.this);
                alertDialogBuilder.setSingleChoiceItems(courseHourItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        courseHourSelection = courseHourItems[position];
                        courseHourDataText.setText(courseHourSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.course_hour);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relCoursePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] coursePlaceListItems = {"홍대", "강남", "혜화", "종로", "종각", "이수", "일산", "신촌"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddCourseActivity.this);
                alertDialogBuilder.setSingleChoiceItems(coursePlaceListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        coursePlaceSelection = coursePlaceListItems[position];
                        coursePlaceDataText.setText(coursePlaceSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.course_place);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relCourseMaxNumOfClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] courseTotalNumListItems = {"1", "2", "3","4","5","6","7","8","9","10","11","12","13"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddCourseActivity.this);
                alertDialogBuilder.setSingleChoiceItems(courseTotalNumListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        courseMaxNumOfClassesSelection = courseTotalNumListItems[position];
                        courseTotalNumOfClassesDataText.setText(courseMaxNumOfClassesSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.course_total_number_of_times);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relCourseMaxNumOfStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] courseMaxNumOfStudentsListItems = {"1", "2", "3","4","5","6","7","8","9","10","11","12","13" ,"14","15","16","17","18","19"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddCourseActivity.this);
                alertDialogBuilder.setSingleChoiceItems(courseMaxNumOfStudentsListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        courseMaxNumOfStudentSelection = courseMaxNumOfStudentsListItems[position];
                        courseMaxNumOfStudentDataText.setText(courseMaxNumOfStudentSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.course_max_number_of_student);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        Log.d(TAG, "onCreate: out");
    }


    private void registerNewCourse() {

        String title = courseTitle.getText().toString();
        String tutorName = mUser.getName();
        String price = coursePricePerHour.getText().toString();
        String tutorImageUri = "";
        String courseImageUri = "";
        String courseDay = courseDayDataText.getText().toString();
        String courseHour = courseHourDataText.getText().toString();
        String coursePlace = coursePlaceDataText.getText().toString();
        String courseTotalNumOfClasses = courseTotalNumOfClassesDataText.getText().toString();
        String courseMaxNumOfStudents = courseMaxNumOfStudentDataText.getText().toString();

        try {
//            tutorImageUri = mProfileResultUri.toString();
            tutorImageUri = mUser.getProfile_image();
            courseImageUri = mCourseResultUri.toString();
        } catch(NullPointerException e) {
            Toast.makeText(this, "사진을 설정하세요", Toast.LENGTH_SHORT).show();
        }

        if ((!TextUtils.isEmpty(title))
                && (!TextUtils.isEmpty(tutorName))
                && (!TextUtils.isEmpty(price))
                && (!TextUtils.isEmpty(tutorImageUri))
                && (!TextUtils.isEmpty(courseImageUri))
                && (!TextUtils.isEmpty(courseDay))
                && (!TextUtils.isEmpty(courseHour))
                && (!TextUtils.isEmpty(coursePlace))
                && (!TextUtils.isEmpty(courseTotalNumOfClasses))
                && (!TextUtils.isEmpty(courseMaxNumOfStudents))) {

            Log.d(TAG, "registerNewCourse: All fields are completed ");

            saveNewCourseToDB(title, tutorName, price, tutorImageUri, courseImageUri,
                    courseDay, courseHour, coursePlace, courseTotalNumOfClasses, courseMaxNumOfStudents);

        } else {
            Toast.makeText(this, "강의 항목을 모두 채우세요", Toast.LENGTH_LONG).show();
        }
    }

    private void saveNewCourseToDB(String title, String tutorName, String price, String tutorImageUri,
                                   String courseImageUri, String courseDay, String courseHour, String coursePlace,
                                   String courseTotalNumOfClasses, String courseMaxNumOfStudents) {

        Log.d(TAG, "saveNewCourseToDB: saving new course to database started");

        mProgressDialogue.setMessage("새로운 강의를 등록중입니다.");
        mProgressDialogue.show();

        //이미지는 Storage 에 저장하고 그 주소를 데이터베이스에 저장한다.
        uploadNewPhotoToStorage();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //강의 객체 생성
        Course course = new Course();

        //essential
        course.setCourseTitle(title);
        course.setCourseTutorName(tutorName);
        course.setCoursePricePerHour(Integer.parseInt(price));
        course.setCourseTutorImage(tutorImageUri);
        course.setCourseImage(courseImageUri);

        //id
        course.setCourseId(courseId);
        course.setCourseTutorId(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //fixed data
        course.setCourseAccumulatedStudentNumber(50);
        course.setCourseHourPerClass(2);

        course.setCourseMaxStudentNumber(Integer.parseInt(courseMaxNumOfStudents));
        course.setCourseNumberOfClasses(Integer.parseInt(courseTotalNumOfClasses));
        course.setCourseDay(courseDay);

        course.setCourseTargetExplanation(title + " 수업의 수업 대상 설명입니다. " + "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into ele");
        course.setCourseIntroduction(title + " 수업 소개 입니다. " + "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into ele");
        course.setCourseTutorIntroduction(tutorName + " 튜터 선생님의 소개글입니다. " + "Lorem Ipsum is simply dummy text of the printing and typesetting industry. ed it to make a type specimen book.");
        course.setCourseCurriculum(title + " 수업의 커리큘럼 소개입니다. " + "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, " +
                "remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, " +
                "a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, " +
                "very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
                "\n" + "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.");
        course.setCoursePlace(coursePlace);
        course.setCourseHour(courseHour);

        //course Type 은 어드민에서 해당 코스의 타입을 지정하여 저장한다.
//        course.setCourseType("프로그래밍");

        //강의 요청 DB에 저장
        reference.child(getString(R.string.dbnode_courses_reg_req))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(courseId)
                .setValue(course)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialogue.dismiss();
                        Toast.makeText(AddCourseActivity.this, "강의 등록 요청 완료", Toast.LENGTH_SHORT).show();

                        /**
                         * requested state change to true
                         */
                        reference.child(getString(R.string.dbnode_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.field_is_requested))
                                .setValue(true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: succeed setting isRequested true.");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: failed setting isRequested true");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialogue.dismiss();
                        Toast.makeText(AddCourseActivity.this, "강의 등록 요청 실패 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadNewPhotoToStorage() {

        FilePaths filePaths = new FilePaths();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //강의 고유 ID 생성
        courseId = databaseReference
                .child(getString(R.string.dbnode_courses_reg_req))
                .push().getKey();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference tutorImageReference = FirebaseStorage.getInstance().getReference()
                .child(filePaths.FIREBASE_IMAGE_FOR_REQEUSTED_COURSE + "/" + user.getUid() + "/" + courseId + "/" +"tutor_image");

        StorageReference courseImageReference = FirebaseStorage.getInstance().getReference()
                .child(filePaths.FIREBASE_IMAGE_FOR_REQEUSTED_COURSE + "/" + user.getUid() + "/" + courseId + "/"+ "course_image");


//        tutorImageReference.putFile(Uri.parse(mUser.getProfile_image()))
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri firebaseURL = taskSnapshot.getDownloadUrl();
//                Toast.makeText(AddCourseActivity.this, "튜터 이미지 업로드 완료", Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbnode_courses_reg_req))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(courseId)
                .child(getString(R.string.field_tutor_image))
                .setValue(mUser.getProfile_image());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(AddCourseActivity.this, "튜터 이미지 업로드 실패", Toast.LENGTH_SHORT).show();
//            }
//        });

        courseImageReference.putFile(mCourseResultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri firebaseURL = taskSnapshot.getDownloadUrl();
                        Toast.makeText(AddCourseActivity.this, "코스 이미지 업로드 완료", Toast.LENGTH_SHORT).show();

                        FirebaseDatabase.getInstance().getReference()
                                .child(getString(R.string.dbnode_courses_reg_req))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(courseId)
                                .child(getString(R.string.field_course_image))
                                .setValue(firebaseURL.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddCourseActivity.this, "코스 이미지 업로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE_PROFILE_IMAGE && resultCode == RESULT_OK) {
            mProfileImageUri = data.getData();

            CropImage.activity(mProfileImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .setBorderLineColor(Color.RED)
                    .setGuidelinesColor(Color.GREEN)
                    .setBorderLineThickness(getResources().getDimensionPixelSize(R.dimen.thickness))
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            mProfileImageUri = data.getData();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProfileResultUri = result.getUri();
                courseTutorImage.setImageURI(mProfileResultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        ///////////////////////

        if (requestCode == CROP_COURSE_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mCourseResultUri = result.getUri();
                courseImage.setImageURI(mCourseResultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
