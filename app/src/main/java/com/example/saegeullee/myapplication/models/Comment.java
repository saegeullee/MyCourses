package com.example.saegeullee.myapplication.models;

public class Comment {

    private String comment;
    private String userName;
    private String userImage;
    private String comment_id;
    private String user_id;
    private String course_id;
    private String timestamp;

    public Comment(String comment, String userName, String userImage, String comment_id, String user_id, String course_id, String timestamp) {
        this.comment = comment;
        this.userName = userName;
        this.userImage = userImage;
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.course_id = course_id;
        this.timestamp = timestamp;
    }

    public Comment() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "comment : " + getComment() + "\n" +
                "userName : " + getUserName() + "\n" +
                 "course_id : " + getCourse_id() + "\n" +
                "comment_id : " + getComment_id() + "\n" +
                "user_id : " + getUser_id() + "\n" +
                "timestamp : " + getTimestamp() + "\n" ;
    }
}
