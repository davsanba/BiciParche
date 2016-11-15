package com.unal.davsanba.biciparche.Forms;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewPersonalRouteActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    private Button mShowPlacePickerBtn;

    private Button mCreateRouteBtn;
    private Button mCancelRouteBtn;

    private ImageButton mShowTimePickerBtn;

    private ImageView mMapPreview;

    private EditText mRouteNameField;
    private EditText mRouteTimeField;

    private DatabaseOperations mDbOperations;

    private MultiSelectionSpinner mRouteDaysSpinner;

    private ArrayList<LatLng> mMarkerPoints;

    private String mode;
    private Route mCurrentRoute;

    private final String TAG = "Create_Route";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_personal_route);

        mMapPreview = (ImageView) findViewById(R.id.container_map_create_preview);

        mShowTimePickerBtn = (ImageButton) findViewById(R.id.btn_show_time_picker);
        mShowTimePickerBtn.setOnClickListener(this);

        mShowPlacePickerBtn = (Button) findViewById(R.id.btn_show_place_picker);
        mShowPlacePickerBtn.setOnClickListener(this);

        mCreateRouteBtn = (Button) findViewById(R.id.btn_create_route);
        mCreateRouteBtn.setOnClickListener(this);

        mCancelRouteBtn = (Button) findViewById(R.id.btn_cancel_route);
        mCancelRouteBtn .setOnClickListener(this);

        mRouteNameField = (EditText) findViewById(R.id.field_route_name);

        mRouteTimeField = (EditText) findViewById(R.id.field_route_time);
        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = timeF.format(Calendar.getInstance().getTime());
        mRouteTimeField.setText(time);

        mRouteDaysSpinner = (MultiSelectionSpinner) findViewById(R.id.day_selection_spinner);
        mRouteDaysSpinner.setItems(getResources().getStringArray(R.array.week_days));

        mAuth = FirebaseAuth.getInstance();

        mDbOperations = new DatabaseOperations();

        mode = getIntent().getStringExtra(ActivitiesReferences.EXTRA_ROUTE_CREATE_UPDATE);

        if(mode.equals(ActivitiesReferences.EXTRA_ROUTE_UPDATE)) {
            mCurrentRoute = getIntent().getParcelableExtra(ActivitiesReferences.EXTRA_ROUTE);
            Log.d(TAG, "Ruta para editar " + mCurrentRoute.getRouteID());
            fill();
        }
    }

    private void fill() {
        Log.d(TAG, mCurrentRoute.getRouteID());
        mRouteNameField.setText(mCurrentRoute.getRouteName());
        mRouteTimeField.setText(mCurrentRoute.getRouteHour());
        String[] days = mCurrentRoute.getRouteDays().split(", ");

        mMarkerPoints = (ArrayList<LatLng>) mCurrentRoute.getRouteMarks();
        mMarkerPoints.add(0,mCurrentRoute.getRouteEnd());
        mMarkerPoints.add(0,mCurrentRoute.getRouteStart());

        mRouteDaysSpinner.setSelection(days);
        mCreateRouteBtn.setText(getString(R.string.btn_text_update_route));
        mCancelRouteBtn.setText(getString(R.string.btn_text_delete_route));

        printMap();
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
                    Route result = createUpdateRoute();
                    Intent returnIntent = new Intent();
                    if(result != null){
                        returnIntent.putExtra("route",result);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                }
                break;

            case R.id.btn_cancel_route:
                if(mode.equals(ActivitiesReferences.EXTRA_ROUTE_CREATE)) {
                    setResult(Activity.RESULT_CANCELED);
                }else {
                    mDbOperations.removeRoute(mCurrentRoute);
                }
                finish();
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

    private Route createUpdateRoute() {

        Route route = new Route(mAuth.getCurrentUser().getUid(), mRouteNameField.getText().toString(),
                mRouteDaysSpinner.getSelectedItemsAsString(), mRouteTimeField.getText().toString(),
                mMarkerPoints.remove(0), mMarkerPoints.remove(0), mMarkerPoints);

        if(mode.equals(ActivitiesReferences.EXTRA_ROUTE_UPDATE)){
            route.setRouteID(mCurrentRoute.getRouteID());
            mDbOperations.upDateRoute(route);
        } else
            mDbOperations.createNewRoute(route);

        return route;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivitiesReferences.RC_CREATE_ROUTE) {
            if (resultCode == Activity.RESULT_OK) {
                mMarkerPoints = data.getParcelableArrayListExtra("points");
                printMap();
            }else{
                Toast.makeText(NewPersonalRouteActivity.this, R.string.toast_error_route_not_created, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void printMap(){
        String url = "https://maps.googleapis.com/maps/api/staticmap?size=200x200&markers=";

        url += mMarkerPoints.get(0).latitude + "," + mMarkerPoints.get(0).longitude + "|";
        url += mMarkerPoints.get(1).latitude + "," + mMarkerPoints.get(1).longitude;
        url += "&key=" + getResources().getString(R.string.google_api_key);

        new DownloadImageTask(mMapPreview).execute(url);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                Toast.makeText(NewPersonalRouteActivity.this, getString(R.string.toast_error_profile_photo), Toast.LENGTH_LONG ).show();

            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
