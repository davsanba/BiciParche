package com.unal.davsanba.biciparche.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.ActivitiesReferences;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Forms.ProfileOperationsActivity;
import com.unal.davsanba.biciparche.Objects.User;
import com.unal.davsanba.biciparche.R;

/**
 * Created by davsa on 14/11/2016.
 */
public class UserOperationsManager {

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    private static final String TAG = "User_Operations";

    public UserOperationsManager(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(FirebaseReferences.DATABASE_REFERENCE).child(FirebaseReferences.USER_REFERENCE);
    }

    public void getUsrMail(){

        LayoutInflater li = LayoutInflater.from(context);
        View getUsrIdView = li.inflate(R.layout.dialog_get_user_mail, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(getUsrIdView);

        final EditText userInput = (EditText) getUsrIdView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,int id) {
                        String mail = userInput.getText().toString();
                        if (mail.length() > 3) {
                            Query query = mDatabaseReference.orderByChild(FirebaseReferences.USER_USERNAME_KEY).startAt(mail);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        ShowUserData(postSnapshot.getKey());
                                        break;
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } else{
                            Toast.makeText(context, R.string.error_empty_name, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create()
                .show();
    }


    public void ShowUserData(String userId){
        mDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    User user;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = new User(dataSnapshot.child(FirebaseReferences.USER_NAME_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_USERNAME_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_PHOTO_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_DEPARTMENT_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_CAREER_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_PHONENUMBER_KEY).getValue().toString()
                        );
                        Log.d(TAG, "usuario " + user.getPhoneNumber() + " " + user.getDepartment());
                        Intent update  = new Intent(context, ProfileOperationsActivity.class);
                        update.putExtra(ActivitiesReferences.EXTRA_PROFILE_CREATE_UPDATE_SHOW, ActivitiesReferences.EXTRA_PROFILE_SHOW);
                        update.putExtra(ActivitiesReferences.EXTRA_PROFILE_USER,user);
                        context.startActivity(update);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void updateUserData(){
        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    User user;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = new User(dataSnapshot.child(FirebaseReferences.USER_NAME_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_USERNAME_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_PHOTO_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_DEPARTMENT_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_CAREER_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_PHONENUMBER_KEY).getValue().toString()
                        );
                        Log.d(TAG, "usuario " + user.getPhoneNumber() + " " + user.getDepartment());
                        Intent update  = new Intent(context, ProfileOperationsActivity.class);
                        update.putExtra(ActivitiesReferences.EXTRA_PROFILE_CREATE_UPDATE_SHOW, ActivitiesReferences.EXTRA_PROFILE_UPDATE);
                        update.putExtra(ActivitiesReferences.EXTRA_PROFILE_USER,user);
                        context.startActivity(update);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
