package com.example.saegeullee.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.NavigationActivity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;

    //widgets
    private TextInputLayout loginUserName, loginUserPassword;
    private Button loginBtn, createActBtn;
    private ProgressDialog mProgressDialog;

    private AnimationDrawable animationDrawable;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);

        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void userSignIn() {
        if(!TextUtils.isEmpty(loginUserName.getEditText().getText().toString())
                && !TextUtils.isEmpty(loginUserPassword.getEditText().getText().toString())) {

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("로그인");
            mProgressDialog.setMessage("로그인 중입니다.");
            mProgressDialog.show();

            String email = loginUserName.getEditText().getText().toString();
            String password = loginUserPassword.getEditText().getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Log in!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {

                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                            }
                            mProgressDialog.dismiss();

                            // ...
                        }
                    });
        } else {
            Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        loginUserName = findViewById(R.id.idInputLayout);
        loginUserPassword = findViewById(R.id.passwordInputLayout);
        loginBtn = findViewById(R.id.loginButton);
        createActBtn = findViewById(R.id.createActButton);

        frameLayout = findViewById(R.id.myFrameLayout);
        animationDrawable = (AnimationDrawable) frameLayout.getBackground();

        //Add time changes to animation
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();



        createActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmInput();
            }
        });

    }

    private boolean validateId() {
        String idInput = loginUserName.getEditText().getText().toString().trim();
        if(idInput.isEmpty()) {
            loginUserName.setError("아이디를 입력하세요");
            return true;
        } else if(idInput.length() > 20){
            loginUserName.setError("아이디는 20자를 초과할 수 없습니다.");
            return true;
        } else {
            loginUserName.setError(null);
            return false;
        }
    }

    private boolean validatePassword() {
        String idInput = loginUserPassword.getEditText().getText().toString().trim();
        if(idInput.isEmpty()) {
            loginUserPassword.setError("비밀번호를 입력하세요");
            return true;
        } else if(idInput.length() > 20){
            loginUserPassword.setError("비밀번호는 20자를 초과할 수 없습니다.");
            return true;
        } else {
            loginUserPassword.setError(null);
            return false;
        }
    }

    public void confirmInput() {
        if(!validateId() && !validatePassword()) {
            userSignIn();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
