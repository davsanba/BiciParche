package com.unal.davsanba.biciparche;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unal.davsanba.biciparche.Data.Bounds;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Util.MultiSelectionSpinner;
import com.unal.davsanba.biciparche.Util.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewPersonalRouteActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private Button mShowPlacePickerBtn;
    private Button mCreateRouteBtn;

    private ImageButton mShowTimePickerBtn;

    private EditText mRouteNameField;
    private EditText mRouteStartField;
    private EditText mRouteTimeField;

    private MultiSelectionSpinner mRouteDaysSpinner;

    private Place mStartLocation = null;
    private GoogleApiClient mGoogleApiClient;

    private final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_personal_route);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mShowTimePickerBtn = (ImageButton) findViewById(R.id.btn_show_time_picker);
        mShowTimePickerBtn.setOnClickListener(this);

        mShowPlacePickerBtn = (Button) findViewById(R.id.btn_show_place_picker);
        mShowPlacePickerBtn.setOnClickListener(this);

        mCreateRouteBtn = (Button) findViewById(R.id.btn_create_route);
        mCreateRouteBtn.setOnClickListener(this);

        mRouteNameField = (EditText) findViewById(R.id.field_route_name);

        mRouteStartField = (EditText) findViewById(R.id.field_route_start);
        mRouteStartField.setEnabled(false);

        mRouteTimeField = (EditText) findViewById(R.id.field_route_time);
        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = timeF.format(Calendar.getInstance().getTime());
        mRouteTimeField.setText(time);

        mRouteDaysSpinner = (MultiSelectionSpinner) findViewById(R.id.day_selection_spinner);
        mRouteDaysSpinner.setItems(getResources().getStringArray(R.array.week_days));

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseReferences.DATABASE_REFERENCE).child(FirebaseReferences.ROUTE_REFERENCE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
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
                if(validateData())
                    createNewRoute();
                break;
            case R.id.btn_show_place_picker:
                startActivity(new Intent(NewPersonalRouteActivity.this, CreateRouteActivity.class));
               /*
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(Bounds.BOGOTA_BOUND);
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                */
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
        if(mStartLocation == null){
            valid = false;
            mShowPlacePickerBtn.setError(getString(R.string.error_empty_location));
        }
        return valid;
    }

    private void createNewRoute() {
        Map<String, String> newRoute = new HashMap< >();
        newRoute.put(FirebaseReferences.ROUTE_OWNER_ID_KEY, mAuth.getCurrentUser().getUid());
        newRoute.put(FirebaseReferences.ROUTE_NAME_KEY, mRouteNameField.getText().toString());
        newRoute.put(FirebaseReferences.ROUTE_DAYS_KEY, mRouteDaysSpinner.getSelectedItemsAsString());
        newRoute.put(FirebaseReferences.ROUTE_HOUR_KEY, mRouteTimeField.getText().toString());
        newRoute.put(FirebaseReferences.ROUTE_START_KEY, mStartLocation.getName().toString());
        newRoute.put(FirebaseReferences.ROUTE_END_KEY, Bounds.UNAL_LOCATION);
        mDatabaseReference.push().setValue(newRoute);
        this.finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //TODO que hace el onConected?
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO que hace el onConnectionSuspended?
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO que hace el onConnectionFailed?
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                mStartLocation = PlacePicker.getPlace(this,data);
                mRouteStartField.setText(mStartLocation.getAddress());
            }
        }
    }
}
