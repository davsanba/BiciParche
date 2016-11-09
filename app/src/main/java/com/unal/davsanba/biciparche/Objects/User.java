package com.unal.davsanba.biciparche.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davsa on 18/10/2016.
 */
public class User implements Parcelable {

    private String name;
    private String username;
    private String photoUrl;
    private List<PersonalRoute> userRoutes;
    private List<Group> userGroups;

    public User(){  }

    public User(String name, String username, String photoUrl) {
        this.name = name;
        this.username = username;
        this.photoUrl = photoUrl;
        userRoutes = new ArrayList<PersonalRoute>();
        userGroups = new ArrayList<Group>();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<PersonalRoute> getUserRoutes() {
        return userRoutes;
    }

    public void setUserRoutes(List<PersonalRoute> userRoutes) {
        this.userRoutes = userRoutes;
    }

    public List<Group> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<Group> userGroups) {
        this.userGroups = userGroups;
    }

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
}

