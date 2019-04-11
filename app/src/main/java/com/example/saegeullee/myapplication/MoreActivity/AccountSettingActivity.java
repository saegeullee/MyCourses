package com.example.saegeullee.myapplication.MoreActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.Dialog.ProfileName_Dialog;
import com.example.saegeullee.myapplication.LoginActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.Utility.FilePaths;
import com.example.saegeullee.myapplication.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingActivity extends AppCompatActivity implements ProfileName_Dialog.OnInputListener {

    private static final String TAG = "AccountSettingActivity";

    @Override
    public void sendInput(String input) {
        Log.d(TAG, "sendInput: got the input: " + input);
        mProfileName.setText(input);
    }

    //widgets
    private Toolbar mToolbar;
    private TextView sex, age, job, interest, mUserEmail, mUserPhoneNum;
    public TextView mProfileName;
    private RelativeLayout relSex, relAge, relJob, relTopBar, relInterest;
    private CircleImageView mProfileImage;
    private Button saveBtn, logOutBtn;
    private ProgressBar mProgressBar;
    private Button mAdminPageBtn;

    //vars
    private String sexSelection;
    private String ageSelection;
    private String jobSelection;
    private String interestSelection;

    private Uri mProfileImageUri;
    private Uri mProfileResultUri;
    private boolean mStoragePermissions;
    private byte[] mBytes;
    private double progress;

    private Boolean mIsAdmin = false;

    private static final int REQUEST_CODE = 1234;
    public static final double MB_THRESHHOLD = 1.0;
    public static final double MB = 1000000.0;


    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    public static final int GALLERY_CODE_PROFILE_IMAGE = 1;
    public static final int CROP_COURSE_IMAGE_ACTIVITY_REQUEST_CODE = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        mAuth = FirebaseAuth.getInstance();

        verifyStoragePermissions();
        isAdmin();
        initUI();

    }

    private void isAdmin() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getAdmin()) {
                    Log.d(TAG, "onDataChange: admin ? " + user.getAdmin());
                    mIsAdmin = true;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void verifyStoragePermissions() {
        Log.d(TAG, "verifyStoragePermissions: asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissions = true;
        } else {
            ActivityCompat.requestPermissions(
                    AccountSettingActivity.this,
                    permissions,
                    REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: requestCode: " + requestCode);
        switch (requestCode) {
            case REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: user has allowed permission to access");
                }
                break;
        }
    }

    private void initUI() {

        getUserAccountsData();

        mToolbar = findViewById(R.id.account_setting_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.edit_profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sex = findViewById(R.id.account_setting_sex_text);
        age = findViewById(R.id.account_setting_age_text);
        job = findViewById(R.id.account_setting_job_text);
        interest = findViewById(R.id.account_setting_interest);

        relSex = findViewById(R.id.relLayoutForSex);
        relAge = findViewById(R.id.relLayoutForAge);
        relJob = findViewById(R.id.relLayoutForJob);
        relInterest = findViewById(R.id.relLayoutForInterest);
        relTopBar = findViewById(R.id.relLayoutTopBar);

        mProfileImage = findViewById(R.id.account_setting_profile_image);
        mProfileName = findViewById(R.id.account_setting_user_name);
        mUserEmail = findViewById(R.id.account_setting_user_email);
        mUserPhoneNum = findViewById(R.id.account_setting_user_phone_number);

        saveBtn = findViewById(R.id.account_setting_save_btn);
        logOutBtn = findViewById(R.id.account_setting_logout_btn);

        mProgressBar = findViewById(R.id.progressBar);


        //어드민 페이지 버튼
        mAdminPageBtn = findViewById(R.id.adminPageBtn);

        mAdminPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsAdmin) {
                    Intent intent = new Intent(AccountSettingActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AccountSettingActivity.this, "어드민 계정만 접속 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        relSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] sexListItems = {"남자", "여자"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccountSettingActivity.this);
                alertDialogBuilder.setSingleChoiceItems(sexListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        sexSelection = sexListItems[position];
                        sex.setText(sexSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.sex);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] ageListItems = {"2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002"
                , "2001", "2000", "1999", "1998", "1997", "1996", "1995","1994", "1993", "1992", "1991"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccountSettingActivity.this);
                alertDialogBuilder.setSingleChoiceItems(ageListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        ageSelection = ageListItems[position];
                        int ageSelected = Integer.valueOf(ageSelection);
                        ageSelection = String.valueOf(2018-ageSelected);
                        age.setText(ageSelection + getString(R.string.SE_age));
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.birth_year);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] jobListItems = {"대학생", "직장인", "취업준비", "중/고등학생", "주부", "프리랜서"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccountSettingActivity.this);
                alertDialogBuilder.setSingleChoiceItems(jobListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        jobSelection = jobListItems[position];
                        job.setText(jobSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.job);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] jobListItems = {"영어회화", "댄스", "프로그래밍", "사진", "미술", "테니스", "축구"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccountSettingActivity.this);
                alertDialogBuilder.setSingleChoiceItems(jobListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        jobSelection = jobListItems[position];
                        interest.setText(jobSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.interested_field);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileName_Dialog dialog = new ProfileName_Dialog();
                dialog.show(getSupportFragmentManager(), "AccountSetting_Dialog");
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null) {
                    mAuth.signOut();
                    Toast.makeText(AccountSettingActivity.this, "sign out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AccountSettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent imageIntent = CropImage.activity(mProfileImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .setBorderLineColor(Color.RED)
                        .setGuidelinesColor(Color.GREEN)
                        .setBorderLineThickness(getResources().getDimensionPixelSize(R.dimen.thickness))
                        .getIntent(AccountSettingActivity.this);
                startActivityForResult(imageIntent, CROP_COURSE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(mUserEmail.getText().toString())
                        &&!TextUtils.isEmpty(sex.getText().toString())
                        &&!TextUtils.isEmpty(age.getText().toString())
                        &&!TextUtils.isEmpty(job.getText().toString())
                        &&!TextUtils.isEmpty(mProfileName.getText().toString())) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                    /**
                     * Change Name
                     */
                    if(!TextUtils.isEmpty(mProfileName.getText().toString())) {
                        reference.child(getString(R.string.dbnode_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.field_name))
                                .setValue(mProfileName.getText().toString());
                    }

                    /**
                     * Change Phone Number
                     */
                    if(!TextUtils.isEmpty(mUserPhoneNum.getText().toString())) {
                        reference.child(getString(R.string.dbnode_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.field_phone))
                                .setValue(mUserPhoneNum.getText().toString());
                    }

                    /**
                     * Change User sex
                     */
                    if(!TextUtils.isEmpty(sex.getText().toString())) {
                        reference.child(getString(R.string.dbnode_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.field_sex))
                                .setValue(sex.getText().toString());
                    }

                    /**
                     * Change User age
                     */
                    if(!TextUtils.isEmpty(age.getText().toString())) {
                        reference.child(getString(R.string.dbnode_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.field_age))
                                .setValue(age.getText().toString());
                    }

                    /**
                     * Change User job
                     */
                    if(!TextUtils.isEmpty(job.getText().toString())) {
                        reference.child(getString(R.string.dbnode_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.field_job))
                                .setValue(job.getText().toString());
                    }

                    /**
                     * Change User Interest
                     */

                    if(!TextUtils.isEmpty(interest.getText().toString())) {
                        reference.child(getString(R.string.dbnode_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.field_interest))
                                .setValue(interest.getText().toString());
                    }

                    /**
                     * Upload new Photo
                     */
                    if(mProfileResultUri != null) {
                        Log.d(TAG, "onClick: upload photo start");
                        uploadNewPhoto(mProfileResultUri);
                    }

                    Toast.makeText(AccountSettingActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountSettingActivity.this, "모든 항목을 채우세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadNewPhoto(Uri mProfileResultUri) {
        Log.d(TAG, "uploadNewPhoto: uploading new profile photo to firebase storage");

        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(mProfileResultUri);
    }


    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bm) {
            if(bm != null) {
                mBitmap = bm;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
            Toast.makeText(AccountSettingActivity.this, "이미지 압축중..", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started");

            if(mBitmap == null) {

                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(AccountSettingActivity.this.getContentResolver(), uris[0]);
                } catch (IOException e) {
                    Log.d(TAG, "doInBackground: IOException : " + e.getCause());
                }
            }

            byte[] bytes = null;
            for(int i = 1; i < 11; i++) {
                if(i == 10) {
                    Toast.makeText(AccountSettingActivity.this, "이미지 사이즈가 너무 큽니다.", Toast.LENGTH_LONG).show();
                    break;
                }
                bytes = getBytesFromBitmap(mBitmap, 100/i);
                Log.d(TAG, "doInBackground: megabytes: (" + (11-i) + "0%" + bytes.length/MB + " MB");
                if(bytes.length/MB < MB_THRESHHOLD) {
                    return bytes;
                }
            }
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            hideDialog();
            mBytes = bytes;

            //execute upload
            executeUploadTask();
        }
    }

    private void executeUploadTask() {
        showDialog();
        FilePaths filePaths = new FilePaths();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                 + "/profile_image");

        if(mBytes.length/MB < MB_THRESHHOLD) {

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .setContentLanguage("en")
                    .build();

            /**
             * if the image size is valid then we can submit to database
             */

            UploadTask uploadTask;
            uploadTask = storageReference.putBytes(mBytes, metadata);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseURL = taskSnapshot.getDownloadUrl();
                    Toast.makeText(AccountSettingActivity.this, "이미지 업로드 성공", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onSuccess: firebase download url : " + firebaseURL.toString());
                    FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.dbnode_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.field_profile_image))
                            .setValue(firebaseURL.toString());

                    hideDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccountSettingActivity.this, "이미지 업로드 불가능", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (currentProgress > (progress + 15)) {
                        progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Toast.makeText(AccountSettingActivity.this, progress + "%", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "이미지 크기가 너무 큽니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //convert from bitmap to byte array and also compress it
    private static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        if(mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode == GALLERY_CODE_PROFILE_IMAGE && resultCode == RESULT_OK) {
//            mProfileImageUri = data.getData();
//
//            CropImage.activity(mProfileImageUri)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setCropShape(CropImageView.CropShape.OVAL)
//                    .setAspectRatio(1, 1)
//                    .setBorderLineColor(Color.RED)
//                    .setGuidelinesColor(Color.GREEN)
//                    .setBorderLineThickness(getResources().getDimensionPixelSize(R.dimen.thickness))
//                    .start(this);
//
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                mProfileResultUri = result.getUri();
//                mProfileImage.setImageURI(mProfileResultUri);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }

        if (requestCode == CROP_COURSE_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProfileResultUri = result.getUri();
                mProfileImage.setImageURI(mProfileResultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    private void getUserAccountsData() {
        Log.d(TAG, "getUserAccountsData: getting the users account information");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users))
                .orderByKey()
                .equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    User user = singleSnapshot.getValue(User.class);

                    mProfileName.setText(user.getName());
                    mUserEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    sex.setText(user.getSex());
                    age.setText(user.getAge());
                    job.setText(user.getJob());
                    interest.setText(user.getInterest());

                    if(!user.getProfile_image().equals("")) {
                        ImageLoader.getInstance().displayImage(user.getProfile_image(), mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    ValueEventListener mValueEventListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//
//            update
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//        }
//    };
//
//    private void enableDataChangeListener() {
//        /**
//         * --------------- Listener that will watch the 'user' node ------------
//         */
//
//            Log.d(TAG, "enableChatRoomListener: chatRoom == null");
//            mUserReference = FirebaseDatabase.getInstance().getReference()
//                    .child(getString(R.string.dbnode_users))
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .child(getString(R.string.field_name));
//
////                    .child(getString(R.string.dbnode_chatrooms))
////                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                    .child(chatRoomId)
////                    .child(getString(R.string.field_chat_messages));
//
//            mUserReference.addValueEventListener(mValueEventListener);
//
//    }
}
