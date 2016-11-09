package com.unal.davsanba.biciparche;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.Objects.User;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private Button mNewRouteBtn;

    private User currentUser;

    private final int RC_CREATE_PERSONAL_ROUTE = 1;

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
                startActivityForResult(new Intent(MainActivity.this, NewPersonalRouteActivity.class),RC_CREATE_PERSONAL_ROUTE);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CREATE_PERSONAL_ROUTE) {
            if (resultCode == Activity.RESULT_OK) {
                Route newRoute = data.getParcelableExtra("route");
                String text = getString(R.string.toast_route_created) + newRoute.getRouteName();
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, R.string.toast_error_route_not_created, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
