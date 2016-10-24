package com.unal.davsanba.biciparche;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Objects.User;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private Button mNewRouteBtn;

    private User currentUser;

    private static final String TAG = "Main_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(FirebaseReferences.DATABASE_REFERENCE);

        mNewRouteBtn = (Button) findViewById(R.id.btn_new_route);

        mNewRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewPersonalRouteActivity.class));

            }
        });

        getUserData();
    }

    private void getUserData(){
        mDatabaseReference.child(FirebaseReferences.USER_REFERENCE).child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUser = new User(dataSnapshot.child(FirebaseReferences.USER_NAME_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_USERNAME_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_PHOTO_KEY).getValue().toString());

                        Log.d(TAG, "usuario " + currentUser.getUsername() + " " + currentUser.getName());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
