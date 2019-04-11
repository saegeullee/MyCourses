package com.example.saegeullee.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserForCreateAccount implements Parcelable{
    private String mEmail;
    private String mPassword;
    private String mName;

    public UserForCreateAccount(String mEmail, String mPassword, String mName) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mName = mName;
    }

    public UserForCreateAccount() {
    }

    protected UserForCreateAccount(Parcel in) {
        mEmail = in.readString();
        mPassword = in.readString();
        mName = in.readString();
    }

    public static final Creator<UserForCreateAccount> CREATOR = new Creator<UserForCreateAccount>() {
        @Override
        public UserForCreateAccount createFromParcel(Parcel in) {
            return new UserForCreateAccount(in);
        }

        @Override
        public UserForCreateAccount[] newArray(int size) {
            return new UserForCreateAccount[size];
        }
    };

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mEmail);
        parcel.writeString(mPassword);
        parcel.writeString(mName);
    }
}
