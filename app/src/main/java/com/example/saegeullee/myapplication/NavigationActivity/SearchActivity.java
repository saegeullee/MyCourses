package com.example.saegeullee.myapplication.NavigationActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.Adapters.CourseListAdapter;
import com.example.saegeullee.myapplication.ChatActivity.ChatActivity;
import com.example.saegeullee.myapplication.CourseDetailsActivity;
import com.example.saegeullee.myapplication.MoreActivity.MoreActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private BottomNavigationView bottomNavigationView;
    private EditText mSearchField;
    private ImageView mSearchBtn;
    private RecyclerView mResultList;
    private TextView noSearchResultNoticeText;

    private CourseListAdapter mAdapter;
    private List<Course> courseSearchedList;

    private Course course;

    private DatabaseReference mCourseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initUI();
//        getPreviousSearchResult();
    }

    private void getPreviousSearchResult() {
        SharedPreferences getResultSharedPref = getSharedPreferences(getString(R.string.sharedpreference), Context.MODE_PRIVATE);
        if(getResultSharedPref.contains(getString(R.string.sharedpreference))) {
            String searchText = getResultSharedPref.getString(getString(R.string.sharedpreference), "searched string");
            mSearchField.setText(searchText);
            getSearchResultDataFromDB(searchText);
        }
    }

    private void getSearchResultDataFromDB(final String searchText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_courses_search))
                .orderByKey()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: singleSnapshot : " + singleSnapshot);
                            course = singleSnapshot.getValue(Course.class);
                            Log.d(TAG, "onDataChange: got course : " + course.toString());


                            if (course.getCourseTitle().contains(searchText)) {
                                courseSearchedList.add(course);
                            }
                        }
                        Log.d(TAG, "onDataChange: size of courseSearchedList : " + courseSearchedList.size());
                        if(courseSearchedList.size() == 0) {
                            noSearchResultNoticeText.setVisibility(View.VISIBLE);
                        }
                        loadSearchedCourse();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void firebaseCourseSearch() {

        if(!TextUtils.isEmpty(mSearchField.getText().toString())) {

            courseSearchedList.clear();

            final String searchText = mSearchField.getText().toString();
            getSearchResultDataFromDB(searchText);

        } else {
            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadSearchedCourse() {
        mAdapter = new CourseListAdapter(SearchActivity.this ,courseSearchedList);
        mResultList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CourseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(SearchActivity.this, CourseDetailsActivity.class);
                intent.putExtra(getString(R.string.intent_course_detail), courseSearchedList.get(position));
                startActivity(intent);
            }
        });
    }


    private void initUI() {

        bottomNavigationView = findViewById(R.id.bottomNavViewBar);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        courseSearchedList = new ArrayList<>();

        mSearchField = findViewById(R.id.search_field);
        mSearchBtn = findViewById(R.id.search_btn);
        mResultList = findViewById(R.id.result_list);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseCourseSearch();
            }
        });
        noSearchResultNoticeText = findViewById(R.id.noSearchResultNoticeText);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.bottom_nav_home: {
                        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_search: {

                        break;
                    }

                    case R.id.bottom_nav_wishList: {
                        Intent intent = new Intent(SearchActivity.this, WishListActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_chat: {
                        Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_more: {
                        Intent intent = new Intent(SearchActivity.this, MoreActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedpreference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!TextUtils.isEmpty(mSearchField.getText().toString())) {
            String searchText = mSearchField.getText().toString();
            editor.putString(getString(R.string.sharedpreference), searchText);
            editor.apply();
        } else {
            editor.clear();
        }
    }
}
