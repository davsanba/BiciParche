package com.unal.davsanba.biciparche.Forms;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.unal.davsanba.biciparche.Data.ActivitiesReferences;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.R;
import com.unal.davsanba.biciparche.Util.DatabaseOperations;
import com.unal.davsanba.biciparche.Util.MultiSelectionSpinner;
import com.unal.davsanba.biciparche.Util.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewPersonalRouteActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    private Button mShowPlacePickerBtn;
    private Button mCreateRouteBtn;

    private ImageButton mShowTimePickerBtn;

    private EditText mRouteNameField;
    private EditText mRouteTimeField;

    private DatabaseOperations mDbOperations;

    private MultiSelectionSpinner mRouteDaysSpinner;

    private ArrayList<LatLng> mMarkerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_personal_route);


        mShowTimePickerBtn = (ImageButton) findViewById(R.id.btn_show_time_picker);
        mShowTimePickerBtn.setOnClickListener(this);

        mShowPlacePickerBtn = (Button) findViewById(R.id.btn_show_place_picker);
        mShowPlacePickerBtn.setOnClickListener(this);

        mCreateRouteBtn = (Button) findViewById(R.id.btn_create_route);
        mCreateRouteBtn.setOnClickListener(this);

        mRouteNameField = (EditText) findViewById(R.id.field_route_name);

        mRouteTimeField = (EditText) findViewById(R.id.field_route_time);
        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = timeF.format(Calendar.getInstance().getTime());
        mRouteTimeField.setText(time);

        mRouteDaysSpinner = (MultiSelectionSpinner) findViewById(R.id.day_selection_spinner);
        mRouteDaysSpinner.setItems(getResources().getStringArray(R.array.week_days));

        mAuth = FirebaseAuth.getInstance();

        mDbOperations = new DatabaseOperations();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
       super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_time_picker:
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
                break;
            case R.id.btn_create_route:
                if(validateData()) {
                    Route result = createNewRoute();
                    Intent returnIntent = new Intent();
                    if(result != null){
                        returnIntent.putExtra("route",result);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }else{
                        setResult(Activity.RESULT_CANCELED);
                    }
                }
                break;
            case R.id.btn_show_place_picker:
                startActivityForResult(new Intent(NewPersonalRouteActivity.this, CreateRouteActivity.class),
                        ActivitiesReferences.RC_CREATE_ROUTE);
                break;
        }
    }



    public boolean validateData(){
        boolean valid = true;
        if(mRouteNameField.getText().toString().matches("")){
            valid = false;
            mRouteNameField.setError(getString(R.string.error_empty_name));
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

    private Route createNewRoute() {
        Route route = new Route(mAuth.getCurrentUser().getUid(), mRouteNameField.getText().toString(),
                mRouteDaysSpinner.getSelectedItemsAsString(), mRouteTimeField.getText().toString(),
                mMarkerPoints.remove(0), mMarkerPoints.remove(0), mMarkerPoints);

        mDbOperations.createNewRoute(route);
        
        /*
        Map<String, String> newRoute = new HashMap< >();
        newRoute.put(FirebaseReferences.ROUTE_OWNER_ID_KEY, mAuth.getCurrentUser().getUid());
        newRoute.put(FirebaseReferences.ROUTE_NAME_KEY, mRouteNameField.getText().toString());
        newRoute.put(FirebaseReferences.ROUTE_DAYS_KEY, mRouteDaysSpinner.getSelectedItemsAsString());
        newRoute.put(FirebaseReferences.ROUTE_HOUR_KEY, mRouteTimeField.getText().toString());
        newRoute.put(FirebaseReferences.ROUTE_START_KEY, mStartLocation.getName().toString());
        newRoute.put(FirebaseReferences.ROUTE_END_KEY, Bounds.UNAL_LOCATION);
        mDatabaseReference.push().setValue(newRoute);
        this.finish();
        */
        return route;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivitiesReferences.RC_CREATE_ROUTE) {
            if (resultCode == Activity.RESULT_OK) {
                mMarkerPoints = data.getParcelableArrayListExtra("points");

            }else{
                Toast.makeText(NewPersonalRouteActivity.this, R.string.toast_error_route_not_created, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
