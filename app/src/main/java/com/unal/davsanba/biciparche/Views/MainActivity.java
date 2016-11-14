package com.unal.davsanba.biciparche.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.ActivitiesReferences;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Forms.NewPersonalRouteActivity;
import com.unal.davsanba.biciparche.Forms.ProfileOperationsActivity;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.Objects.User;
import com.unal.davsanba.biciparche.R;
import com.unal.davsanba.biciparche.Util.DatabaseOperations;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mNewRouteBtn;
    private Button mSearchUsrBtn;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mAuth;

    private DatabaseOperations mDBoperations;

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
        mNewRouteBtn.setOnClickListener(this);

        mSearchUsrBtn = (Button) findViewById(R.id.btn_search_usr);
        mSearchUsrBtn.setOnClickListener(this);

        mDBoperations = new DatabaseOperations();

        getUserData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId() ){

            case R.id.btn_new_route:

                startActivityForResult(new Intent(MainActivity.this, NewPersonalRouteActivity.class),
                        ActivitiesReferences.RC_NEW_PERSONAL_ROUTE);
                break;

            case R.id.btn_search_usr:
                Intent create = new Intent(MainActivity.this, ProfileOperationsActivity.class);
                create.putExtra(ActivitiesReferences.EXTRA_PROFILE_CREATE_UPDATE_SHOW, ActivitiesReferences.EXTRA_PROFILE_CREATE);
                create.putExtra(ActivitiesReferences.EXTRA_PROFILE_USER,currentUser);
                startActivity(create);
                //getUsrMail();
                break;
        }
    }

    public void getUsrMail(){

        LayoutInflater li = LayoutInflater.from(this);
        View getUsrIdView = li.inflate(R.layout.dialog_get_user_mail, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set dialog_get_emp_id.xml to alertdialog builder
        alertDialogBuilder.setView(getUsrIdView);

        final EditText userInput = (EditText) getUsrIdView.findViewById(R.id.editTextDialogUserInput);
        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,int id) {

                        mDBoperations.getUserByMail(userInput.getText().toString(),MainActivity.this);
                    /*

                        companyOperations= new CompanyOperations(MainActivity.this);
                        companyOperations.open();
                        Company comp = companyOperations.getCompany(Long.parseLong(userInput.getText().toString()));

                        if (comp != null) {
                            companyOperations.removeCompany(comp);
                            Toast t = Toast.makeText(MainActivity.this,"Employee removed successfully!",Toast.LENGTH_SHORT);
                            t.show();
                        }else{
                            Toast t = Toast.makeText(MainActivity.this,"Company not found",Toast.LENGTH_SHORT);
                            t.show();
                        }
                        companyOperations.close();
                        */
                    }
                }).create()
                .show();

    }

    private void getUserData(){
        mDatabaseReference.child(FirebaseReferences.USER_REFERENCE).child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUser = new User(dataSnapshot.child(FirebaseReferences.USER_NAME_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_USERNAME_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_PHOTO_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_DEPARTMENT_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_CAREER_KEY).getValue().toString(),
                                dataSnapshot.child(FirebaseReferences.USER_PHONENUMBER_KEY).getValue().toString()
                        );

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

        if (requestCode == ActivitiesReferences.RC_NEW_PERSONAL_ROUTE) {
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
