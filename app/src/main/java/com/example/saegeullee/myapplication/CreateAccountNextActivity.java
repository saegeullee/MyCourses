package com.example.saegeullee.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.NavigationActivity.MainActivity;
import com.example.saegeullee.myapplication.models.User;
import com.example.saegeullee.myapplication.models.UserForCreateAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class CreateAccountNextActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    //widgets
    private RelativeLayout relSex, relAge, relJob, relInterest;
    private TextView mBirthYear, mSex, mJob, mInterests;
    private Button mCreateAccountBtn;
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;

    private UserForCreateAccount userForCreateAccount;

    private String getUserEmail, getUserPassword, getUserName;

    //vars
    private String sexSelection;
    private String ageSelection;
    private String jobSelection;
    private String interestSelection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_next);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        initUI();

    }

    private void initUI() {
        mBirthYear = findViewById(R.id.create_account_birth_year_et);
        mSex = findViewById(R.id.create_account_sex_et);
        mJob = findViewById(R.id.create_account_job_et);
        mInterests = findViewById(R.id.create_account_interest_et);

        relAge = findViewById(R.id.create_account_relLayoutForBirthYear);
        relSex = findViewById(R.id.create_account_relLayoutForSex);
        relJob = findViewById(R.id.create_account_relLayoutForJob);
        relInterest = findViewById(R.id.create_account_relLayoutForInterestField);

        mCreateAccountBtn = findViewById(R.id.createActButton);

        mToolbar = findViewById(R.id.create_account_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.create_account));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        relAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] ageListItems = {"2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002"
                        , "2001", "2000", "1999", "1998", "1997", "1996", "1995","1994", "1993", "1992", "1991"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccountNextActivity.this);
                alertDialogBuilder.setSingleChoiceItems(ageListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        ageSelection = ageListItems[position];
                        int ageSelected = Integer.valueOf(ageSelection);
                        ageSelection = String.valueOf(2018-ageSelected);
                        mBirthYear.setText(ageSelection + getString(R.string.SE_age));
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.birth_year);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] sexListItems = {"남자", "여자"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccountNextActivity.this);
                alertDialogBuilder.setSingleChoiceItems(sexListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        sexSelection = sexListItems[position];
                        mSex.setText(sexSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.sex);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        relJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] jobListItems = {"대학생", "직장인", "취업준비", "중/고등학생", "주부", "프리랜서"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccountNextActivity.this);
                alertDialogBuilder.setSingleChoiceItems(jobListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        jobSelection = jobListItems[position];
                        mJob.setText(jobSelection);
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
                final String[] interestListItems = {"영어회화", "댄스", "프로그래밍", "사진", "미술", "테니스", "축구"};

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccountNextActivity.this);
                alertDialogBuilder.setSingleChoiceItems(interestListItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        interestSelection = interestListItems[position];
                        mInterests.setText(interestSelection);
                        dialogInterface.dismiss();
                    }
                });

                alertDialogBuilder.setTitle(R.string.interested_field);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void createAccount() {
        if(!TextUtils.isEmpty(mBirthYear.getText().toString())
                && !TextUtils.isEmpty(mSex.getText().toString())
                && !TextUtils.isEmpty(mJob.getText().toString())
                && !TextUtils.isEmpty(mInterests.getText().toString())) {

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("회원가입");
            mProgressDialog.setMessage("회원가입을 진행중입니다.");
            mProgressDialog.show();

            Log.d(TAG, "onClick: Create Account Start");

            getUserBundle();

            final String birthYear = mBirthYear.getText().toString();
            final String sex = mSex.getText().toString();
            final String job = mJob.getText().toString();
            final String interest = mInterests.getText().toString();

            mAuth.createUserWithEmailAndPassword(getUserEmail, getUserPassword)
                    .addOnCompleteListener(CreateAccountNextActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                User user = new User();

                                user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                user.setName(getUserName);
                                user.setPhone("1");
                                user.setAdmin(false);
                                user.setTutor(false);
                                user.setRequested(false);
                                user.setProfile_image("");
                                user.setSex(sex);
                                user.setAge(birthYear);
                                user.setJob(job);
                                user.setInterest(interest);
                                user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                FirebaseDatabase.getInstance().getReference()
                                        .child(getString(R.string.dbnode_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                            Toast.makeText(CreateAccountNextActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");

                                            FirebaseAuth.getInstance().signOut();
                                            redirectUserToLoginScreen();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        FirebaseAuth.getInstance().signOut();
                                        redirectUserToLoginScreen();
                                        Toast.makeText(CreateAccountNextActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(CreateAccountNextActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            mProgressDialog.dismiss();
                        }
                    });

        } else {
            Toast.makeText(this, "모든 항목을 채우세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserBundle() {

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_create_account))) {
            userForCreateAccount = intent.getParcelableExtra(getString(R.string.intent_create_account));
            getUserName = userForCreateAccount.getmName();
            getUserEmail = userForCreateAccount.getmEmail();
            getUserPassword = userForCreateAccount.getmPassword();
        }

    }

    private void redirectUserToLoginScreen() {
        Intent intent = new Intent(CreateAccountNextActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}