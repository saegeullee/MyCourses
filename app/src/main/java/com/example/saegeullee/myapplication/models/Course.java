package com.example.saegeullee.myapplication.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {


    //19개 멤버
    private String courseId;
    private String courseTutorId;
    private String courseTitle;
    private String courseTutorName;
    private int courseMaxStudentNumber;
    private int courseAccumulatedStudentNumber;
    private int coursePricePerHour;
    private int courseHourPerClass;
    private int courseNumberOfClasses;
    private String courseDay;

    private String courseHour;
    private String courseTargetExplanation;
    private String courseIntroduction;
    private String courseTutorIntroduction;
    private String courseCurriculum;
    private String coursePlace;
    private String courseImage;
    private String courseTutorImage;
    private String courseType;
    private String courseEnrolledDate;
    private String courseRegisteredTime;

    public Course(String courseId, String courseTitle, String courseTutorName, int coursePricePerHour, String courseImage, String courseTutorImage) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseTutorName = courseTutorName;
        this.coursePricePerHour = coursePricePerHour;
        this.courseImage = courseImage;
        this.courseTutorImage = courseTutorImage;
    }

    public Course(String courseId, String courseTutorId, String courseTitle, String courseTutorName,
                  int courseMaxStudentNumber, int courseAccumulatedStudentNumber, int coursePricePerHour,
                  int courseHourPerClass, int courseNumberOfClasses, String courseDay, String courseHour,
                  String courseTargetExplanation, String courseIntroduction, String courseTutorIntroduction,
                  String courseCurriculum, String coursePlace, String courseImage, String courseTutorImage,
                  String courseType, String courseEnrolledDate, String courseRegisteredTime) {

        //19개
        this.courseId = courseId;
        this.courseTutorId = courseTutorId;
        this.courseTitle = courseTitle;
        this.courseTutorName = courseTutorName;
        this.courseMaxStudentNumber = courseMaxStudentNumber;
        this.courseAccumulatedStudentNumber = courseAccumulatedStudentNumber;
        this.coursePricePerHour = coursePricePerHour;
        this.courseHourPerClass = courseHourPerClass;
        this.courseNumberOfClasses = courseNumberOfClasses;
        this.courseDay = courseDay;

        this.courseHour = courseHour;
        this.courseTargetExplanation = courseTargetExplanation;
        this.courseIntroduction = courseIntroduction;
        this.courseTutorIntroduction = courseTutorIntroduction;
        this.courseCurriculum = courseCurriculum;
        this.coursePlace = coursePlace;
        this.courseImage = courseImage;
        this.courseTutorImage = courseTutorImage;
        this.courseType = courseType;
        this.courseEnrolledDate = courseEnrolledDate;
        this.courseRegisteredTime = courseRegisteredTime;
    }

    public Course() {
    }

    protected Course(Parcel in) {
        courseId = in.readString();
        courseTutorId = in.readString();
        courseTitle = in.readString();
        courseTutorName = in.readString();
        courseMaxStudentNumber = in.readInt();
        courseAccumulatedStudentNumber = in.readInt();
        coursePricePerHour = in.readInt();
        courseHourPerClass = in.readInt();
        courseNumberOfClasses = in.readInt();
        courseDay = in.readString();
        courseHour = in.readString();
        courseTargetExplanation = in.readString();
        courseIntroduction = in.readString();
        courseTutorIntroduction = in.readString();
        courseCurriculum = in.readString();
        coursePlace = in.readString();
        courseImage = in.readString();
        courseTutorImage = in.readString();
        courseType = in.readString();
        courseEnrolledDate = in.readString();
        courseRegisteredTime = in.readString();
    }


    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public int getCourseImageResourceId(Context context) {

        //context.getResources().getIdentifier(리소스 이름, 리소스 타입, 패키지명);

        return context.getResources().getIdentifier(this.courseImage, "drawable", context.getPackageName());
    }

    public int getCourseTutorImageResourceId(Context context) {
        return context.getResources().getIdentifier(this.courseTutorImage, "drawable", context.getPackageName());
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseTutorId() {
        return courseTutorId;
    }

    public void setCourseTutorId(String courseTutorId) {
        this.courseTutorId = courseTutorId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseTutorName() {
        return courseTutorName;
    }

    public void setCourseTutorName(String courseTutorName) {
        this.courseTutorName = courseTutorName;
    }

    public int getCourseMaxStudentNumber() {
        return courseMaxStudentNumber;
    }

    public void setCourseMaxStudentNumber(int courseMaxStudentNumber) {
        this.courseMaxStudentNumber = courseMaxStudentNumber;
    }

    public int getCourseAccumulatedStudentNumber() {
        return courseAccumulatedStudentNumber;
    }

    public void setCourseAccumulatedStudentNumber(int courseAccumulatedStudentNumber) {
        this.courseAccumulatedStudentNumber = courseAccumulatedStudentNumber;
    }

    public int getCoursePricePerHour() {
        return coursePricePerHour;
    }

    public void setCoursePricePerHour(int coursePricePerHour) {
        this.coursePricePerHour = coursePricePerHour;
    }

    public int getCourseHourPerClass() {
        return courseHourPerClass;
    }

    public void setCourseHourPerClass(int courseHourPerClass) {
        this.courseHourPerClass = courseHourPerClass;
    }

    public int getCourseNumberOfClasses() {
        return courseNumberOfClasses;
    }

    public void setCourseNumberOfClasses(int courseNumberOfClasses) {
        this.courseNumberOfClasses = courseNumberOfClasses;
    }

    public String getCourseDay() {
        return courseDay;
    }

    public void setCourseDay(String courseDay) {
        this.courseDay = courseDay;
    }

    public String getCourseHour() {
        return courseHour;
    }

    public void setCourseHour(String courseHour) {
        this.courseHour = courseHour;
    }

    public String getCourseTargetExplanation() {
        return courseTargetExplanation;
    }

    public void setCourseTargetExplanation(String courseTargetExplanation) {
        this.courseTargetExplanation = courseTargetExplanation;
    }

    public String getCourseIntroduction() {
        return courseIntroduction;
    }

    public void setCourseIntroduction(String courseIntroduction) {
        this.courseIntroduction = courseIntroduction;
    }

    public String getCourseTutorIntroduction() {
        return courseTutorIntroduction;
    }

    public void setCourseTutorIntroduction(String courseTutorIntroduction) {
        this.courseTutorIntroduction = courseTutorIntroduction;
    }

    public String getCourseCurriculum() {
        return courseCurriculum;
    }

    public void setCourseCurriculum(String courseCurriculum) {
        this.courseCurriculum = courseCurriculum;
    }

    public String getCoursePlace() {
        return coursePlace;
    }

    public void setCoursePlace(String coursePlace) {
        this.coursePlace = coursePlace;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public String getCourseTutorImage() {
        return courseTutorImage;
    }

    public void setCourseTutorImage(String courseTutorImage) {
        this.courseTutorImage = courseTutorImage;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCourseEnrolledDate() {
        return courseEnrolledDate;
    }

    public void setCourseEnrolledDate(String courseEnrolledDate) {
        this.courseEnrolledDate = courseEnrolledDate;
    }

    public String getCourseRegisteredTime() {
        return courseRegisteredTime;
    }

    public void setCourseRegisteredTime(String courseRegisteredTime) {
        this.courseRegisteredTime = courseRegisteredTime;
    }

    @Override
    public String toString() {
        return "Course : \n +" +
                "courseName : " + getCourseTitle() + "\n" +
                "courseId : " + getCourseId() + "\n" +
                "courseImage: " + getCourseImage() + "\n" +
                "courseTutorImage: " + getCourseTutorImage() + "\n" +
                "coursePrice: " + getCoursePricePerHour() + "\n" +
                "courseCurriculum: " + getCourseCurriculum() + "\n";

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(courseId);
        parcel.writeString(courseTutorId);
        parcel.writeString(courseTitle);
        parcel.writeString(courseTutorName);
        parcel.writeInt(courseMaxStudentNumber);
        parcel.writeInt(courseAccumulatedStudentNumber);
        parcel.writeInt(coursePricePerHour);
        parcel.writeInt(courseHourPerClass);
        parcel.writeInt(courseNumberOfClasses);
        parcel.writeString(courseDay);
        parcel.writeString(courseHour);
        parcel.writeString(courseTargetExplanation);
        parcel.writeString(courseIntroduction);
        parcel.writeString(courseTutorIntroduction);
        parcel.writeString(courseCurriculum);
        parcel.writeString(coursePlace);
        parcel.writeString(courseImage);
        parcel.writeString(courseTutorImage);
        parcel.writeString(courseType);
        parcel.writeString(courseEnrolledDate);
        parcel.writeString(courseRegisteredTime);
    }
}