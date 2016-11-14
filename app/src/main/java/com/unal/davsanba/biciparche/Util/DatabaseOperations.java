package com.unal.davsanba.biciparche.Util;

import android.content.Context;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.Objects.User;

import java.util.HashMap;
import java.util.Map;

import static com.unal.davsanba.biciparche.Data.FirebaseReferences.*;

/**
 * Created by davsa on 10/11/2016.
 */
public class DatabaseOperations {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    private final String TAG = "Database_Operations";

    public DatabaseOperations() {
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean createNewUser(User user) {
        boolean succes = true;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseReferences.DATABASE_REFERENCE)
                .child(FirebaseReferences.USER_REFERENCE);

        DatabaseReference newUser =  mDatabaseReference.child(mAuth.getCurrentUser().getUid());
        newUser.child(FirebaseReferences.USER_NAME_KEY).setValue(user.getName());
        newUser.child(FirebaseReferences.USER_USERNAME_KEY).setValue(user.getUsername());
        newUser.child(USER_PHOTO_KEY).setValue(user.getPhotoUrl());
        newUser.child(USER_DEPARTMENT_KEY).setValue(user.getDepartment());
        newUser.child(USER_CAREER_KEY).setValue(user.getCareer());
        newUser.child(USER_PHONENUMBER_KEY).setValue(user.getPhoneNumber());

        return succes;
    }

    public boolean updateUser(User user){
        boolean succes = true;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseReferences.DATABASE_REFERENCE)
                .child(FirebaseReferences.USER_REFERENCE);
        DatabaseReference currentUser =  mDatabaseReference.child(mAuth.getCurrentUser().getUid());

        Map<String, Object> update = new HashMap<String, Object>();
        update.put(USER_DEPARTMENT_KEY, user.getDepartment());
        update.put(USER_CAREER_KEY, user.getCareer());
        update.put(USER_PHONENUMBER_KEY, user.getPhoneNumber());

        currentUser.updateChildren(update);

        return succes;
    }

    public String createNewRoute(Route route){

        String newRouteKey;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseReferences.DATABASE_REFERENCE)
                .child(FirebaseReferences.ROUTE_REFERENCE);

        DatabaseReference newRoute = mDatabaseReference.push();
        newRoute.setValue(route);
        newRouteKey = newRoute.getKey();

        return newRouteKey;
    }




}
