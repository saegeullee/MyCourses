package com.example.saegeullee.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private String email;
    private String name;
    private String phone;
    private String profile_image;
    private String user_id;
    private String sex;
    private String age;
    private String job;
    private String interest;
    private Boolean isTutor;
    private Boolean isAdmin;
    private Boolean isRequested;

    public User(String email, String name, String phone, String profile_image, String user_id,
                String sex, String age, String job, String interest, Boolean isTutor, Boolean isAdmin,
                Boolean isRequested) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.profile_image = profile_image;
        this.user_id = user_id;
        this.sex = sex;
        this.age = age;
        this.job = job;
        this.interest = interest;
        this.isTutor = isTutor;
        this.isAdmin = isAdmin;
        this.isRequested = isRequested;
    }

    public User() {
    }

    protected User(Parcel in) {
        email = in.readString();
        name = in.readString();
        phone = in.readString();
        profile_image = in.readString();
        user_id = in.readString();
        sex = in.readString();
        age = in.readString();
        job = in.readString();
        interest = in.readString();
        byte tmpIsTutor = in.readByte();
        isTutor = tmpIsTutor == 0 ? null : tmpIsTutor == 1;
        byte tmpIsAdmin = in.readByte();
        isAdmin = tmpIsAdmin == 0 ? null : tmpIsAdmin == 1;
        byte tmpIsRequested = in.readByte();
        isRequested = tmpIsRequested == 0 ? null : tmpIsRequested == 1;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Boolean getTutor() {
        return isTutor;
    }

    public void setTutor(Boolean tutor) {
        isTutor = tutor;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public Boolean getRequested() {
        return isRequested;
    }

    public void setRequested(Boolean requested) {
        isRequested = requested;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", is_tutor='" + isTutor + '\'' +
                ", is_admin='" + isAdmin + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(profile_image);
        parcel.writeString(user_id);
        parcel.writeString(sex);
        parcel.writeString(age);
        parcel.writeString(job);
        parcel.writeString(interest);
        parcel.writeByte((byte) (isTutor == null ? 0 : isTutor ? 1 : 2));
        parcel.writeByte((byte) (isAdmin == null ? 0 : isAdmin ? 1 : 2));
        parcel.writeByte((byte) (isRequested == null ? 0 : isRequested ? 1 : 2));
    }
}
