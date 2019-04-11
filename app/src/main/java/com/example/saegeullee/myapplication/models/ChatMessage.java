package com.example.saegeullee.myapplication.models;

public class ChatMessage {

    private String message;
    private String timestamp;
    private String name;
    private String user_id;
    private String profile_image;

    public ChatMessage(String message, String timestamp, String name, String user_id, String profile_image) {
        this.message = message;
        this.timestamp = timestamp;
        this.name = name;
        this.user_id = user_id;
        this.profile_image = profile_image;
    }

    public ChatMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    @Override
    public String toString() {
        return "message : " + getMessage() + "\n" +
                "name : " + getName() + "\n" +
                "timestamp : " + getTimestamp() + "\n" +
                "user_id : " + getUser_id() + "\n" +
                "profile_image : " + getProfile_image() + "\n";

    }
}
