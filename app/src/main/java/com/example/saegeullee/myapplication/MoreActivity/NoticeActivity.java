package com.example.saegeullee.myapplication.MoreActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.saegeullee.myapplication.R;

public class NoticeActivity extends AppCompatActivity {

    //widgets
    private Toolbar mToolbar;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        initUI();
    }

    private void initUI() {
        mToolbar = findViewById(R.id.notice_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.notice_board);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relativeLayout = findViewById(R.id.noticeRelLayout);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoticeActivity.this, NoticeContentActivity.class);
                startActivity(intent);
            }
        });




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
