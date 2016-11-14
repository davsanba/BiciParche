package com.unal.davsanba.biciparche.Util;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.Objects.User;

/**
 * Created by davsa on 10/11/2016.
 */
public class DatabaseOperations {

    DatabaseReference mDatabaseReference;

    public DatabaseOperations() {

    }

    public boolean createNewUser(User user) {
        boolean succes = false;
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


    public void getUserByMail(final String mail, final Context context){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseReferences.DATABASE_REFERENCE)
                .child(FirebaseReferences.USER_REFERENCE);

        Query queryRef = mDatabaseReference.orderByChild(FirebaseReferences.USER_USERNAME_KEY).equalTo(mail);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Toast.makeText(context ,"Usuario " + mail +" no existe " + dataSnapshot.toString(),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context ,dataSnapshot.child(FirebaseReferences.USER_NAME_KEY).toString(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
