package com.example.saegeullee.myapplication.NavigationActivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.saegeullee.myapplication.Adapters.CourseListAdapterNewRecommend;
import com.example.saegeullee.myapplication.ChatActivity.ChatActivity;
import com.example.saegeullee.myapplication.CourseDetailsActivity;
import com.example.saegeullee.myapplication.MoreActivity.MoreActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WishListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "WishListActivity";

    //widgets
    private BottomNavigationView bottomNavigationView;
    private Toolbar mToolbar;
    private RecyclerView wishListRecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private CourseListAdapterNewRecommend mAdapter;
    private ArrayList<Course> wishCourseList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Course course;

    private Menu menu;
    private boolean isListView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: in");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        initUI();
        getCourseData();

        Log.d(TAG, "onCreate: out");
    }


    private void loadCourse() {
        Log.d(TAG, "loadCourse: start");
        mAdapter = new CourseListAdapterNewRecommend(WishListActivity.this, wishCourseList);
        wishListRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new CourseListAdapterNewRecommend.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(WishListActivity.this, CourseDetailsActivity.class);
                intent.putExtra(getString(R.string.intent_course_detail), wishCourseList.get(position ));
                startActivity(intent);
            }
        });
    }

    private void getCourseData() {
        Log.d(TAG, "getCourseData: start");

        wishCourseList.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.dbnode_liked_courses));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: snapshot : " + snapshot);
                    course = snapshot.getValue(Course.class);

                    if(course != null) {

                        Log.d(TAG, "onDataChange: course : " + course.toString());
                        wishCourseList.add(course);
                    } else {
                        Log.d(TAG, "onDataChange: no course has been added to the wish list");
                    }
                }
                Log.d(TAG, "onDataChange: wishCourseList 에 추가된 아이템의 개수 : " + wishCourseList.size());
                loadCourse();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initUI() {

        wishListRecyclerView = findViewById(R.id.wishListRecyclerView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        wishListRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        wishListRecyclerView.setNestedScrollingEnabled(false);

        wishCourseList = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.bottomNavViewBar);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        mToolbar = findViewById(R.id.wish_list_page_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.wish_lectures);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.bottom_nav_home: {
                        Intent intent = new Intent(WishListActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_search: {
                        Intent intent = new Intent(WishListActivity.this, SearchActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_wishList: {

                        break;
                    }

                    case R.id.bottom_nav_chat: {
                        Intent intent = new Intent(WishListActivity.this, ChatActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_more: {
                        Intent intent = new Intent(WishListActivity.this, MoreActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wishlist, menu);

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_toggle) {
                toggle();
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if(isListView) {
            showGridView();
        } else {
            showListView();
        }
    }

    private void showListView() {
        staggeredGridLayoutManager.setSpanCount(1);
        MenuItem item = menu.findItem(R.id.action_toggle);
        item.setIcon(R.drawable.ic_view_module);
        item.setTitle(getString(R.string.show_as_grid));
        isListView = true;
    }

    private void showGridView() {
        staggeredGridLayoutManager.setSpanCount(2);
        MenuItem item = menu.findItem(R.id.action_toggle);
        item.setIcon(R.drawable.ic_view_list);
        item.setTitle(getString(R.string.show_as_list));
        isListView = false;
    }



    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: in");
        super.onResume();
        Log.d(TAG, "onResume: out");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: in");
        super.onStop();
//        wishCourseList.clear();
        Log.d(TAG, "onStop: out");
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: in");
        super.onRestart();
        Log.d(TAG, "onRestart: out");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: in");
        super.onDestroy();
        Log.d(TAG, "onDestroy: out");
    }

    @Override
    public void onRefresh() {
        getCourseData();
        onItemLoadComplete();
    }

    private void onItemLoadComplete() {
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
