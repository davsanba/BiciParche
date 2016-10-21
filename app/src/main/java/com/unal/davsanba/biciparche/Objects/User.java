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



}

