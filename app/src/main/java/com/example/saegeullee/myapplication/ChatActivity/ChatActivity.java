package com.example.saegeullee.myapplication.ChatActivity;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.saegeullee.myapplication.Adapters.ChatRecyclerAdapter;
import com.example.saegeullee.myapplication.Adapters.ChatRoomRecyclerAdapter;
import com.example.saegeullee.myapplication.MoreActivity.MoreActivity;
import com.example.saegeullee.myapplication.NavigationActivity.MainActivity;
import com.example.saegeullee.myapplication.NavigationActivity.SearchActivity;
import com.example.saegeullee.myapplication.NavigationActivity.WishListActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.ChatMessage;
import com.example.saegeullee.myapplication.models.ChatRoom;
import com.example.saegeullee.myapplication.models.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ChatActivity";

    //widgets
    private BottomNavigationView bottomNavigationView;
    private Toolbar mToolbar;
    private RecyclerView mChatRoomRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    //vars
    private List<ChatRoom> chatRoomList;
    private List<Course> courseList;
    private ChatRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getChatRooms();
    }

    private void getChatRooms() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        chatRoomList.clear();
        courseList.clear();


        reference.child(getString(R.string.dbnode_chatrooms))
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: singleSnapshot : " + singleSnapshot);

                        ChatRoom chatRoom = singleSnapshot.getValue(ChatRoom.class);

                        Log.d(TAG, "onDataChange: chatRoom : " + chatRoom.toString());

                        //read all the chat messages in this chatroom ans set ChatMessage to this chatRoom
                        ArrayList<ChatMessage> messageList = new ArrayList<>();
                        for(DataSnapshot snapshot : singleSnapshot.child(getString(R.string.field_chat_messages)).getChildren()) {
                            Log.d(TAG, "onDataChange: snapshot : " + snapshot);

                            ChatMessage message = new ChatMessage();
                            message.setTimestamp(snapshot.getValue(ChatMessage.class).getTimestamp());
                            message.setMessage(snapshot.getValue(ChatMessage.class).getMessage());
                            message.setUser_id(snapshot.getValue(ChatMessage.class).getUser_id());
                            messageList.add(message);
                        }
                        chatRoom.setChatMessageList(messageList);
                        chatRoomList.add(chatRoom);

                        /**
                         * 코스 객체를 얻어야 하는데 현재 courses 노드 하위에 튜터 아이디 하위에 코스 아이디로 저장이 되어 있기 때문에
                         * 현재 일반적인 경우에는 receiver 는 튜터이기 때문에 getReceiver_id() 로 코스 객체를 얻자
                         */

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        reference.child(getString(R.string.dbnode_courses))
                                .child(chatRoom.getReceiver_id())
                                .child(chatRoom.getCourse_id())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d(TAG, "onDataChange: course dataSnapshot : " + dataSnapshot);
                                        Course course = dataSnapshot.getValue(Course.class);
                                        Log.d(TAG, "onDataChange: got course : " + course.toString());
                                        courseList.add(course);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                    Log.d(TAG, "onDataChange: courseList size : " + courseList.size());
                    Log.d(TAG, "onDataChange: size of chatRoomList2 : " + chatRoomList.size());

                        for(int i = 0; i < chatRoomList.size(); i++ ) {
                            ChatRoom chatRoom = chatRoomList.get(i);
                            Log.d(TAG, "onDataChange: chatRoom number" + i + " : " + chatRoom.toString());
                            for(int j = 0; j < chatRoom.getChatMessageList().size(); j++) {
                                ChatMessage chatMessage = chatRoom.getChatMessageList().get(j);
                                Log.d(TAG, "onDataChange: chatMessage : " + chatMessage.toString());
                            }
                        }

                    loadChatRoomList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }


    private void loadChatRoomList() {
        mAdapter = new ChatRecyclerAdapter(ChatActivity.this, chatRoomList);
        mChatRoomRecyclerView.setAdapter(mAdapter);

        Log.d(TAG, "loadChatRoomList: chatRoomList size : " + chatRoomList.size());

        mAdapter.setOnItemClickListener(new ChatRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ChatActivity.this, ChatRoomActivity.class);
                intent.putExtra(getString(R.string.intent_chatroom), chatRoomList.get(position));
                intent.putExtra(getString(R.string.intent_course), courseList.get(position));

                Log.d(TAG, "onItemClick: intent_chatroom : " + chatRoomList.get(position).toString());
                Log.d(TAG, "onItemClick: intent_course : " + courseList.get(position).toString());

                startActivity(intent);
            }
        });

    }

    private void initUI() {

        chatRoomList = new ArrayList<>();
        courseList = new ArrayList<>();

        mChatRoomRecyclerView = findViewById(R.id.chatRoom_recyclerView);
        mChatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRoomRecyclerView.setNestedScrollingEnabled(false);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mToolbar = findViewById(R.id.chatroom_page_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.require_talk);

        bottomNavigationView = findViewById(R.id.bottomNavViewBar);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.bottom_nav_home: {
                        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_search: {
                        Intent intent = new Intent(ChatActivity.this, SearchActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_wishList: {
                        Intent intent = new Intent(ChatActivity.this, WishListActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        break;
                    }

                    case R.id.bottom_nav_chat: {

                        break;
                    }

                    case R.id.bottom_nav_more: {
                        Intent intent = new Intent(ChatActivity.this, MoreActivity.class);
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
    public void onRefresh() {
        getChatRooms();
        onItemLoadComplete();
    }

    private void onItemLoadComplete() {
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
