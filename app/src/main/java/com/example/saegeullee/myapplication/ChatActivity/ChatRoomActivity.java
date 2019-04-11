package com.example.saegeullee.myapplication.ChatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.saegeullee.myapplication.Adapters.ChatRoomRecyclerAdapter;
import com.example.saegeullee.myapplication.CourseDetailsActivity;
import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.ChatMessage;
import com.example.saegeullee.myapplication.models.ChatRoom;
import com.example.saegeullee.myapplication.models.Course;
import com.example.saegeullee.myapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "ChatRoomActivity";

    //firebase
    private DatabaseReference mMessagesReference;

    //widgets
    private Toolbar mToolbar;
    private Button mCourseDetailBtn;
    private RecyclerView mMessagesRecyclerView;
    private ChatRoomRecyclerAdapter mAdapter;
    private EditText mInputMessage;
    private ImageView mCheckMark;
    private Button submitBtn;

    //vars
    private String courseTutorName;
    private Course course;
    private List<ChatMessage> mMessageList;
    private ChatRoom chatRoom;
    private Boolean hasChatRoomRecord = false;
    private String chatRoomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        initUI();
        getChatRoom();
    }

    private void getChatRoom() {
        /**
         * GET INTENT
         */

        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.intent_chatroom))) {
            chatRoom = intent.getParcelableExtra(getString(R.string.intent_chatroom));
            if(chatRoom == null) {
                Log.d(TAG, "initUI: chatRoom == null");
                hasChatRoomRecord = false;
            } else {
                hasChatRoomRecord = true;
                Log.d(TAG, "initUI: got chatroom : " + chatRoom.toString());
                enableChatRoomListener();
            }
        } else {

            Log.d(TAG, "initUI: chatroom is null");
        }
    }

    private void getChatRoomMessages() {
        mMessageList = new ArrayList<>();
        if(mMessageList.size() > 0 ) {
            mMessageList.clear();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        if(chatRoomId == null) {
            chatRoomId = chatRoom.getChatroom_id();
        }

        reference.child(getString(R.string.dbnode_chatrooms))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatRoomId)
                .child(getString(R.string.field_chat_messages))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot);
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                            DataSnapshot snapshot = singleSnapshot;

                            try {
                                ChatMessage message = new ChatMessage();
                                String userId = snapshot.getValue(ChatMessage.class).getUser_id();
                                if (userId != null) {
                                    message.setMessage(snapshot.getValue(ChatMessage.class).getMessage());
                                    message.setTimestamp(snapshot.getValue(ChatMessage.class).getTimestamp());
                                    message.setUser_id(userId);
                                    mMessageList.add(message);
                                } else {
                                    Log.d(TAG, "onDataChange: userId is null");
                                }
                            } catch (NullPointerException e) {
                                Log.e(TAG, "onDataChange: NullPointerException", e.getCause());
                            }
                        }
                        getUserDetails();
                        initMessagesList();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }

    private void initMessagesList() {
        mAdapter = new ChatRoomRecyclerAdapter(ChatRoomActivity.this, mMessageList);
        mMessagesRecyclerView.setAdapter(mAdapter);
        mMessagesRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void getUserDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mMessageList.size(); i++) {
            Log.d(TAG, "getUserDetails: searching for userId : " + mMessageList.get(i).getUser_id());
            final int j = i;
            if(mMessageList.get(i).getUser_id() != null) {
                Query query = reference.child(getString(R.string.dbnode_users))
                        .orderByKey()
                        .equalTo(mMessageList.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                        Log.d(TAG, "onDataChange: found user id: " + singleSnapshot.getValue(User.class).getUser_id());
                        mMessageList.get(j).setProfile_image(singleSnapshot.getValue(User.class).getProfile_image());
                        mMessageList.get(j).setName(singleSnapshot.getValue(User.class).getName());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void initUI() {

        mToolbar = findViewById(R.id.chatRoomTopBar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_course))) {
            course = intent.getParcelableExtra(getString(R.string.intent_course));
            Log.d(TAG, "initUI: got course : " + course.toString());

            courseTutorName = course.getCourseTutorName();
            getSupportActionBar().setTitle(courseTutorName);

        } else {
            Log.d(TAG, "initUI: course is null");
        }

        mCourseDetailBtn = findViewById(R.id.courseDetailBtn);

        mMessagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessagesRecyclerView.setNestedScrollingEnabled(false);


        mInputMessage = findViewById(R.id.input_message);
//        mCheckMark = findViewById(R.id.check_mark);
        submitBtn = findViewById(R.id.submit_button);

        mCourseDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent courseDetailIntent = new Intent(ChatRoomActivity.this, CourseDetailsActivity.class);
                courseDetailIntent.putExtra(getString(R.string.intent_course_detail), course);
                startActivity(courseDetailIntent);
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(mInputMessage.getText().toString())) {

                    ChatMessage chatMessage = new ChatMessage();

                    chatMessage.setMessage(mInputMessage.getText().toString());
                    chatMessage.setTimestamp(getTimeStamp());

                    /**
                     * chatRoom Record 가 있으면 해당 Chatroom 을 가져오고 그 chatroom 에 메세지를 더한다.
                     * chatRoom Record 가 없으면 새로운 Chatroom 을 생성하고 그 Chatroom 에 메세지를 더한다.
                     */

                    if(hasChatRoomRecord) {

                        /**
                         * 유저가 처음 튜터와 대화를 하고 뒤로가기를 누른 뒤 다시 대화를 걸면 chatRoomId == null 이 된다.
                         * 처음 메시지를 보내고 다음 메시지를 이어서 보내면 chatRoomId 를 안다.
                         */

                        if(chatRoomId == null) {
                            chatRoomId = chatRoom.getChatroom_id();
                        }

                        Log.d(TAG, "onClick: User has a chatRoom");

                        // if user has a chatroom for this course, don't need to make new chatRoom

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        //create a unique id for the message
                        String messageId = reference
                                .child(getString(R.string.dbnode_chatrooms))
                                .push().getKey();

                        //insert the first message in the chatroom
                        ChatMessage message = new ChatMessage();

                        message.setMessage(mInputMessage.getText().toString());
                        message.setTimestamp(getTimeStamp());
                        message.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        /**
                         * 문제점 : 지금의 코드는 학생이 ChatRoomActivity 에 접속했을 때만 고려하여 짠 코드이다
                         * 튜터가 접속했다면 튜터 노드에 메시지를 두번 저장하는 꼴이 된다.
                         * Above problem solved by checking if current user is tutor or student.
                         */

                        //현재 접속자가 튜터라면
                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(course.getCourseTutorId())) {

                            //message save at Tutor NODE
                            reference.child(getString(R.string.dbnode_chatrooms))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(chatRoomId)
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


                            //message save at Student NODE
                            reference.child(getString(R.string.dbnode_chatrooms))
                                    .child(chatRoom.getCreator_id())
                                    .child(chatRoomId)
                                    .child(getString(R.string.field_chat_messages))
                                    .child(messageId)
                                    .setValue(message);


                            //현재 접속자가 학생이라면
                        } else {

                            //message save at STUDENT NODE
                            reference.child(getString(R.string.dbnode_chatrooms))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(chatRoomId)
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
                                    .child(chatRoomId)
                                    .child(getString(R.string.field_chat_messages))
                                    .child(messageId)
                                    .setValue(message);

                        }

                        mInputMessage.setText("");

                    } else {
                        /**
                         * 이 튜터와 채팅이 처음이라면 튜터에게 처음 메세지를 보낼 때 새로운 채팅방을 생성해야 한다.
                         * 그리고 메세지를 보내면 보낸 메시지를 바로 확인 할 수 있어야 한다. enableChatRoomListener();
                         */

                        Log.d(TAG, "onClick: user doesn't have a chatRoom");

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        // get the new chatRoom unique id
                        String ChatRoomId = reference
                                .child(getString(R.string.dbnode_chatrooms))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .push().getKey();

                        chatRoomId = ChatRoomId;

                        // create the chatroom
                        ChatRoom mChatRoom = new ChatRoom();
                        mChatRoom.setChatroom_id(ChatRoomId);
                        mChatRoom.setCreator_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mChatRoom.setTimestamp(getTimeStamp());
                        mChatRoom.setReceiver_id(course.getCourseTutorId());
                        mChatRoom.setCourse_id(course.getCourseId());

                        //insert the new chatroom into the database
                        //for student
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

                        message.setMessage(mInputMessage.getText().toString());
                        message.setTimestamp(getTimeStamp());
                        message.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        //message save at STUDENT NODE
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

                        hasChatRoomRecord = true;
                        mInputMessage.setText("");

                        enableChatRoomListener();
                    }
                }
            }
        });
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(new Date());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMessagesReference != null) {
            mMessagesReference.removeEventListener(mValueEventListener);
        }
    }


    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getChatRoomMessages();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void enableChatRoomListener() {
        /**
         * --------------- Listener that will watch the 'chatRoom messages' node ------------
         */

        // 최초에 리스너 설정

        if (chatRoom == null) {
            Log.d(TAG, "enableChatRoomListener: chatRoom == null");
            mMessagesReference = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbnode_chatrooms))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(chatRoomId)
                    .child(getString(R.string.field_chat_messages));

            // 이미 chatRoom 이 있을 때 리스너 설정
        } else {
            Log.d(TAG, "enableChatRoomListener: chatRoom != null");
            mMessagesReference = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbnode_chatrooms))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(chatRoom.getChatroom_id())
                    .child(getString(R.string.field_chat_messages));

        }

        mMessagesReference.addValueEventListener(mValueEventListener);
    }
}