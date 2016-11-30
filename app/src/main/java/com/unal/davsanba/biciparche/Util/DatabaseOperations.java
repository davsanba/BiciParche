package com.unal.davsanba.biciparche.Util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unal.davsanba.biciparche.Data.FbRef;
import com.unal.davsanba.biciparche.Objects.Group;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.Objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.unal.davsanba.biciparche.Data.FbRef.*;

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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.USER_REFERENCE);

        DatabaseReference newUser =  mDatabaseReference.child(mAuth.getCurrentUser().getUid());
        newUser.child(FbRef.USER_NAME_KEY).setValue(user.getName());
        newUser.child(FbRef.USER_USERNAME_KEY).setValue(user.getUsername());
        newUser.child(USER_PHOTO_KEY).setValue(user.getPhotoUrl());
        newUser.child(USER_DEPARTMENT_KEY).setValue(user.getDepartment());
        newUser.child(USER_CAREER_KEY).setValue(user.getCareer());
        newUser.child(USER_PHONENUMBER_KEY).setValue(user.getPhoneNumber());

        return succes;
    }

    public boolean updateUser(User user){
        boolean succes = true;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.USER_REFERENCE);
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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.ROUTE_REFERENCE);

        DatabaseReference newRoute = mDatabaseReference.push();
        newRoute.setValue(route);
        newRouteKey = newRoute.getKey();

        return newRouteKey;
    }

    public String allocateNewGroup(Group group){
        String newRouteKey;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.GROUP_REFERENCE);

        DatabaseReference newRoute = mDatabaseReference.push();
        newRoute.setValue(group);
        newRouteKey = newRoute.getKey();

        return newRouteKey;
    }

    public void createGroup(Group group, Route route, ArrayList<String> users){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.GROUP_REFERENCE).child(group.getGroupId());

        Map<String, Object> groupCreate = new HashMap<String, Object>();
        groupCreate.put(FbRef.GROUP_NAME_KEY     , group.getGroupName());
        groupCreate.put(FbRef.GROUP_ROUTE_ID_KEY , route.getRouteID());
        //groupCreate.put(FbRef.GROUP_USERS_ID_KEY , users);
        groupCreate.put(FbRef.GROUP_ADMIN_ID_KEY , group.getGroupAdminUserID());
        mDatabaseReference.updateChildren(groupCreate);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.LIST_REFERENCE).child(group.getGroupId());
        mDatabaseReference.child("0").setValue(group.getGroupAdminUserID());

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.USER_REFERENCE).child(group.getGroupAdminUserID()).child(USER_GROUPS_KEY).child(group.getGroupId());
        mDatabaseReference.setValue(true);

        for(String user: users){
            mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                    .child(FbRef.REQUEST_REFERENCE).child(user);
            mDatabaseReference.child(group.getGroupId()).setValue(false);
        }
    }


    public void removeRoute(Route mCurrentRoute) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.ROUTE_REFERENCE).child(mCurrentRoute.getRouteID());
        mDatabaseReference.removeValue();
    }

    public void upDateRoute(Route route) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE)
                .child(FbRef.ROUTE_REFERENCE).child(route.getRouteID());

        Map<String, Object> routeUpate = new HashMap<String, Object>();

        routeUpate.put(FbRef.ROUTE_OWNER_ID_KEY , route.getRouteOwnerID());
        routeUpate.put(FbRef.ROUTE_NAME_KEY     , route.getRouteName());
        routeUpate.put(FbRef.ROUTE_DAYS_KEY     , route.getRouteDays());
        routeUpate.put(FbRef.ROUTE_HOUR_KEY     , route.getRouteHour());
        routeUpate.put(FbRef.ROUTE_START_KEY    , route.getRouteStart());
        routeUpate.put(FbRef.ROUTE_END_KEY      , route.getRouteEnd());
        routeUpate.put(FbRef.ROUTE_MARKS_KEY    , route.getRouteMarks());
        mDatabaseReference.updateChildren(routeUpate);
    }


    public static Group groupFromSnapshot(DataSnapshot postSnapshot) {
        Group group = new Group(
                postSnapshot.getKey(),
                postSnapshot.child(FbRef.GROUP_NAME_KEY).getValue().toString(),
                postSnapshot.child(FbRef.GROUP_ROUTE_ID_KEY).getValue().toString(),
                postSnapshot.child(FbRef.GROUP_ADMIN_ID_KEY).getValue().toString()
        );

        return group;
    }

}
