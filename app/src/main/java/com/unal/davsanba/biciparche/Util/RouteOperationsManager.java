package com.unal.davsanba.biciparche.Util;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unal.davsanba.biciparche.Data.FbRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davsa on 14/11/2016.
 */
public class RouteOperationsManager {

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    private static final String TAG = "Route_Operations";

    public RouteOperationsManager(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(FbRef.DATABASE_REFERENCE).child(FbRef.ROUTE_REFERENCE);
    }

    public static LatLng strToLatLng(DataSnapshot data){
        Double lat = (Double) data.child("latitude").getValue();
        Double lng = (Double) data.child("longitude").getValue();
        return new LatLng(lat,lng);
    }

    public static List<LatLng> toLatLngList(DataSnapshot data){
        ArrayList<LatLng> list = new ArrayList<>();
        if(data != null) {
            for (DataSnapshot a : data.getChildren()) {
            Double lat = (Double) a.child("latitude").getValue();
            Double lng = (Double) a.child("longitude").getValue();
            list.add(new LatLng(lat,lng));
            }
        }
        return list;
    }
}
