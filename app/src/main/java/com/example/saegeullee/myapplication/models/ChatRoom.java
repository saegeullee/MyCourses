package com.example.saegeullee.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ChatRoom implements Parcelable {

    private String chatroom_id;
    private String creator_id;
    private String receiver_id;
    private String course_id;
    private String timestamp;
    private List<ChatMessage> chatMessageList;

    public ChatRoom(String chatroom_id, String creator_id, String receiver_id, String course_id, String timestamp, List<ChatMessage> chatMessageList) {
        this.chatroom_id = chatroom_id;
        this.creator_id = creator_id;
        this.receiver_id = receiver_id;
        this.course_id = course_id;
        this.timestamp = timestamp;
        this.chatMessageList = chatMessageList;
    }

    public ChatRoom() {
    }

    protected ChatRoom(Parcel in) {
        chatroom_id = in.readString();
        creator_id = in.readString();
        receiver_id = in.readString();
        course_id = in.readString();
        timestamp = in.readString();
    }

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(String chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(chatroom_id);
        parcel.writeString(creator_id);
        parcel.writeString(receiver_id);
        parcel.writeString(course_id);
        parcel.writeString(timestamp);
    }

    @Override
    public String toString() {

//        String[] chatMessages = {};
//
//        for(int i = 0; i < getChatMessageList().size(); i++) {
//            chatMessages = new String[getChatMessageList().size()];
//            String chatMessage = getChatMessageList().get(i).toString();
//            chatMessages[i] = chatMessage;
//        }

        return "chatroom_id " + getChatroom_id() + "\n" +
                "creator_id " + getCreator_id() + "\n" +
                "receiver_id " + getReceiver_id() + "\n" +
                "course_id " + getCourse_id() + "\n" +
                "timestamp " + getTimestamp() + "\n" ;
//                "chatMessagesList " + chatMessages.toString();
    }
}
