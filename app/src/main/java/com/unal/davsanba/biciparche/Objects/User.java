package com.unal.davsanba.biciparche.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by davsa on 18/10/2016.
 */
public class User implements Parcelable {

    private String name;
    private String username;
    private String photoUrl;
    private String department;
    private String career;
    private String phoneNumber;
    private List<Route> userRoutes;
    private List<Group> userGroups;


    public User(){  }

    public User(String name, String username, String photoUrl) {
        this.name = name;
        this.username = username;
        this.photoUrl = photoUrl;
    }

    public User(String name, String username, String photoUrl, String department, String career, String phoneNumber) {
        this.name = name;
        this.username = username;
        this.photoUrl = photoUrl;
        this.department = department;
        this.career = career;
        this.phoneNumber = phoneNumber;
    }

    public User(String name, String username, String photoUrl, String department, String career, String phoneNumber, List<Route> userRoutes, List<Group> userGroups) {
        this.name = name;
        this.username = username;
        this.photoUrl = photoUrl;
        this.department = department;
        this.career = career;
        this.phoneNumber = phoneNumber;
        this.userRoutes = userRoutes;
        this.userGroups = userGroups;
    }


    protected User(Parcel in) {
        name = in.readString();
        username = in.readString();
        photoUrl = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(photoUrl);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Route> getUserRoutes() {
        return userRoutes;
    }

    public void setUserRoutes(List<Route> userRoutes) {
        this.userRoutes = userRoutes;
    }

    public List<Group> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<Group> userGroups) {
        this.userGroups = userGroups;
    }
}

