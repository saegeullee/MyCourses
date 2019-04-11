package com.example.saegeullee.myapplication;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.graphics.Color;
        import android.net.Uri;
        import android.support.annotation.NonNull;
        import android.support.design.widget.AppBarLayout;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.saegeullee.myapplication.models.User;
        import com.example.saegeullee.myapplication.models.UserForCreateAccount;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.theartofdev.edmodo.cropper.CropImage;
        import com.theartofdev.edmodo.cropper.CropImageView;

        import de.hdodenhof.circleimageview.CircleImageView;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccountActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    //widgets
    private EditText mInputEmail, mInputPassword, mInputName, mInputPasswordConfirm;
    private Button mCreateAccountNextBtn;

    private UserForCreateAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        initUI();

    }

    private void sendUserToNextCreateAccountActivity() {
        if(!TextUtils.isEmpty(mInputName.getText().toString())
                && !TextUtils.isEmpty(mInputEmail.getText().toString())
                && !TextUtils.isEmpty(mInputPassword.getText().toString())
                && !TextUtils.isEmpty(mInputPasswordConfirm.getText().toString())) {

            if(mInputPassword.getText().toString().equals(mInputPasswordConfirm.getText().toString())) {
                Log.d(TAG, "onClick: Create Account Start");

                user = new UserForCreateAccount();

                user.setmName(mInputName.getText().toString());
                user.setmEmail(mInputEmail.getText().toString());
                user.setmPassword(mInputPassword.getText().toString());

                Intent intent = new Intent(CreateAccountActivity.this, CreateAccountNextActivity.class);
                intent.putExtra(getString(R.string.intent_create_account), user);
                startActivity(intent);

            } else {
                Toast.makeText(this, "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "모든 항목을 채우세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        mInputName = findViewById(R.id.createActUserName);
        mInputEmail = findViewById(R.id.createActUserId);
        mInputPassword = findViewById(R.id.createActPassword);
        mInputPasswordConfirm = findViewById(R.id.createActPassword2);
        mCreateAccountNextBtn = findViewById(R.id.createActNextActBtn);

        mCreateAccountNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToNextCreateAccountActivity();
            }
        });

    }

}
