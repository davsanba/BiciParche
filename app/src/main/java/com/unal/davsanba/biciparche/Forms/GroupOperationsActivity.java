package com.unal.davsanba.biciparche.Forms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.ActRefs;
import com.unal.davsanba.biciparche.Data.FbRef;
import com.unal.davsanba.biciparche.Objects.Group;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.Objects.User;
import com.unal.davsanba.biciparche.R;
import com.unal.davsanba.biciparche.Util.DatabaseOperations;
import com.unal.davsanba.biciparche.Util.MultiSelectionSpinner;
import com.unal.davsanba.biciparche.Util.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GroupOperationsActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<LatLng> mMarkerPoints;
    private ArrayList<String> mGroupUsersId;
    private ArrayList<User>   mGroupUsers;

    private String mode;
    private Route mCurrentRoute;
    private Group mCurrentGroup;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private DatabaseOperations mDbOperations;

    private Button mShowPlacePickerBtn;
    private Button mCreateLeaveGroupBtn;
    private Button mSearchUserBtn;

    private ImageButton mShowTimePickerBtn;

    private EditText mGroupNameField;
    private EditText mRouteTimeField;

    private MultiSelectionSpinner mRouteDaysSpinner;

    private ListView mGroupUsersView;

    private final String TAG = "Create_Group";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_operations);

        mShowTimePickerBtn = (ImageButton) findViewById(R.id.btn_show_time_picker);
        mShowTimePickerBtn.setOnClickListener(this);

        mShowPlacePickerBtn = (Button) findViewById(R.id.btn_show_place_picker);
        mShowPlacePickerBtn.setOnClickListener(this);

        mCreateLeaveGroupBtn = (Button) findViewById(R.id.btn_group_done);
        mCreateLeaveGroupBtn.setOnClickListener(this);

        mSearchUserBtn = (Button) findViewById(R.id.btn_search_user);
        mSearchUserBtn .setOnClickListener(this);

        mGroupNameField = (EditText) findViewById(R.id.field_group_name);

        mRouteTimeField = (EditText) findViewById(R.id.field_group_time);
        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = timeF.format(Calendar.getInstance().getTime());
        mRouteTimeField.setText(time);

        mRouteDaysSpinner = (MultiSelectionSpinner) findViewById(R.id.day_selection_spinner);
        mRouteDaysSpinner.setItems(getResources().getStringArray(R.array.week_days));

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE);
        mDbOperations = new DatabaseOperations();

        mGroupUsers = new ArrayList<>();
        mGroupUsersId = new ArrayList<>();
        mode = getIntent().getStringExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW);

        if(!mode.equals(ActRefs.EXTRA_CREATE)) {
            mCurrentGroup = getIntent().getParcelableExtra(ActRefs.EXTRA_GROUP);
            mCurrentRoute = mCurrentGroup.getGroupRoute();
            fill();
        }


    }

    private void fill() {
        mGroupNameField.setText(mCurrentGroup.getGroupName());
        mRouteTimeField.setText(mCurrentRoute.getRouteHour());
        mGroupUsersId = (ArrayList<String>) mCurrentGroup.getGroupUsers();
        if(!mCurrentGroup.getGroupAdminUserID().equals(mAuth.getCurrentUser().getUid())){
            mGroupNameField.setEnabled(false);
            mRouteTimeField.setEnabled(false);
            mSearchUserBtn.setEnabled(false);
            mShowPlacePickerBtn.setEnabled(false);
            mCreateLeaveGroupBtn.setText(getString(R.string.btn_text_group_leave));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_group_done:
                if(mode.equals(ActRefs.EXTRA_CREATE))
                    validateAndCreate();
                else if(!mCurrentGroup.getGroupAdminUserID().equals(mAuth.getCurrentUser().getUid()))
                    leaveGroup();
                break;

            case R.id.btn_search_user:
                getUsrMail();
                break;

            case R.id.btn_show_time_picker:
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
                break;

            case R.id.btn_show_place_picker:
                startActivityForResult(new Intent(getApplicationContext(), CreateRouteActivity.class),
                        ActRefs.RC_CREATE_ROUTE);
                break;
        }
    }

    private void leaveGroup() {
    }

    private void validateAndCreate(){
        if(validateData()) {
            Group result = createUpdateGroup();
            Intent returnIntent = new Intent();
            if(result != null){
                returnIntent.putExtra("route",result);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
    }

    private Group createUpdateGroup(){
        Group group = new Group(mGroupNameField.getText().toString(), mAuth.getCurrentUser().getUid());
        if(mode.equals(ActRefs.EXTRA_CREATE)) {
            group.setGroupId(mDbOperations.allocateNewGroup(group));

            Route route = new Route(group.getGroupId(), group.getGroupName(), mRouteDaysSpinner.getSelectedItemsAsString(),
                    mRouteTimeField.getText().toString(), mMarkerPoints.remove(0), mMarkerPoints.remove(0), mMarkerPoints);
            route.setRouteID(mDbOperations.createNewRoute(route));

            mDbOperations.createGroup(group, route, mGroupUsersId);
        }

        return group;
    }

    private Route createUpdateRoute() {
        return null;
    }

    private boolean validateData() {
        boolean valid = true;

        if(mGroupNameField.getText().toString().matches("")){
            valid = false;
            mGroupNameField.setError(getString(R.string.error_empty_name));
        }
        if(mRouteDaysSpinner.getSelectedStrings().isEmpty()){
            valid = false;
            TextView errorText = (TextView)mRouteDaysSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getString(R.string.error_empty_days));//changes the selected item text to this
        }
        if(mRouteTimeField.getText().toString().matches("")){
            valid = false;
            mRouteTimeField.setError(getString(R.string.error_empty_hour));
        }
        if(mMarkerPoints == null){
            valid = false;
            mShowPlacePickerBtn.setError(getString(R.string.error_empty_location));
        }
        return valid;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActRefs.RC_CREATE_ROUTE) {
            if (resultCode == Activity.RESULT_OK) {
                mMarkerPoints = data.getParcelableArrayListExtra("points");
            }else{
                Toast.makeText(getApplicationContext(), R.string.toast_error_route_not_created, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getUsrMail(){
        final Context context = GroupOperationsActivity.this;
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
                            mDatabaseReference = mDatabaseReference.child(FbRef.USER_REFERENCE);
                            Query query = mDatabaseReference.orderByChild(FbRef.USER_USERNAME_KEY).equalTo(mail);
                            Log.d(TAG,mail + " " + mDatabaseReference);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            mGroupUsers.add(new User(postSnapshot.child(FbRef.USER_NAME_KEY).getValue().toString(),
                                                    postSnapshot.child(FbRef.USER_USERNAME_KEY).getValue().toString(),
                                                    postSnapshot.child(FbRef.USER_PHOTO_KEY).getValue().toString(),
                                                    postSnapshot.child(FbRef.USER_DEPARTMENT_KEY).getValue().toString(),
                                                    postSnapshot.child(FbRef.USER_CAREER_KEY).getValue().toString(),
                                                    postSnapshot.child(FbRef.USER_PHONENUMBER_KEY).getValue().toString()
                                            ));
                                            mGroupUsersId.add(postSnapshot.getKey());
                                            break;
                                        }
                                    }else{
                                        Toast.makeText(context, R.string.user_not_found, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } else{
                        }
                    }
                }).create()
                .show();
    }

}
