package com.unal.davsanba.biciparche.Util;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Objects.Route;

import java.util.List;

/**
 * Created by davsa on 14/11/2016.
 */
public class getRoutes implements ValueEventListener {
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    private List<Route> Routes;

    private static final String TAG = "Get_Routes_Operations";

    public getRoutes() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(FirebaseReferences.DATABASE_REFERENCE).child(FirebaseReferences.ROUTE_REFERENCE);
    }

    public void getByUser(){
        Query query = mDatabaseReference.orderByChild(FirebaseReferences.ROUTE_OWNER_ID_KEY).equalTo(mAuth.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
