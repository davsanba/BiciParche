package com.unal.davsanba.biciparche.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davsa on 18/10/2016.
 */
public class User {

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
}

