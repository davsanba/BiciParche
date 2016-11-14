package com.unal.davsanba.biciparche.Forms;

import android.content.Intent;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.unal.davsanba.biciparche.Data.ActivitiesReferences;
import com.unal.davsanba.biciparche.Objects.User;
import com.unal.davsanba.biciparche.R;
import com.unal.davsanba.biciparche.Util.DatabaseOperations;
import com.unal.davsanba.biciparche.Views.MainActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ProfileOperationsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private final String TAG = "Profile_Operations";

    private Button mDoneButton;

    private EditText mUserNameField, mUserMailField, mUserPhoneField;

    private ImageView mUserPhotoContainer;

    private Spinner mUserCareerSpinner, mUserDepartmentSpinner;

    private String mode;

    private User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_operations);

        mDoneButton = (Button) findViewById(R.id.btn_profile_done);
        mDoneButton.setOnClickListener(this);

        mUserNameField  = (EditText) findViewById(R.id.field_profile_name);
        mUserNameField.setEnabled(false);
        mUserMailField  = (EditText) findViewById(R.id.field_profile_mail);
        mUserMailField.setEnabled(false);
        mUserPhoneField = (EditText) findViewById(R.id.field_profile_phone);

        mUserPhotoContainer = (ImageView) findViewById(R.id.image_profile_photo);

        mUserCareerSpinner     = (Spinner) findViewById(R.id.spinner_select_career);
        mUserCareerSpinner.setOnItemSelectedListener(this);

        mUserDepartmentSpinner = (Spinner) findViewById(R.id.spinner_select_department);

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter
                .createFromResource(this, R.array.Facultades,
                        android.R.layout.simple_spinner_item);


        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mUserDepartmentSpinner.setAdapter(departmentAdapter);
        mUserDepartmentSpinner.setOnItemSelectedListener(this);

        mode = getIntent().getStringExtra(ActivitiesReferences.EXTRA_PROFILE_CREATE_UPDATE_SHOW);
        mCurrentUser = getIntent().getParcelableExtra(ActivitiesReferences.EXTRA_PROFILE_USER);

        fill(!mode.equals(ActivitiesReferences.EXTRA_PROFILE_CREATE));

        switch (mode){
            case ActivitiesReferences.EXTRA_PROFILE_CREATE:
                break;

            case ActivitiesReferences.EXTRA_PROFILE_UPDATE:
                break;

            case ActivitiesReferences.EXTRA_PROFILE_SHOW:
                show();
                break;
        }
    }

    private void show(){
        mDoneButton.setVisibility(View.GONE);
        mUserPhoneField.setEnabled(false);
        mUserCareerSpinner.setEnabled(false);
        mUserDepartmentSpinner.setEnabled(false);

        List<String> your_array_list = new ArrayList<String>();
        your_array_list.add(mCurrentUser.getCareer());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list );

        mUserCareerSpinner.setAdapter(arrayAdapter);
    }

    private void fill(boolean edit_update){

        mUserNameField.setText(mCurrentUser.getName());
        mUserMailField.setText(mCurrentUser.getUsername());
        new DownloadImageTask((ImageView) findViewById(R.id.image_profile_photo))
                .execute(mCurrentUser.getPhotoUrl());

        if(edit_update) {
            mUserPhoneField.setText(mCurrentUser.getPhoneNumber());

            ArrayAdapter<CharSequence> departmentAdapter = (ArrayAdapter<CharSequence>) mUserDepartmentSpinner.getAdapter();
            mUserDepartmentSpinner.setSelection(departmentAdapter.getPosition(mCurrentUser.getDepartment()));
        }
    }


    @Override
    public void onClick(View v) {
        DatabaseOperations dbOper = new DatabaseOperations();
        switch (v.getId()){
            case R.id.btn_profile_done:
                if(validateData()){
                    mCurrentUser.setPhoneNumber(mUserPhoneField.getText().toString());
                    mCurrentUser.setDepartment(mUserDepartmentSpinner.getSelectedItem().toString());
                    mCurrentUser.setCareer(mUserCareerSpinner.getSelectedItem().toString());
                    if(mode.equals(ActivitiesReferences.EXTRA_PROFILE_CREATE)) {
                        dbOper.createNewUser(mCurrentUser);
                        Intent i = new Intent(this, MainActivity.class);
                        finish();
                        startActivity(i);
                    } else if(mode.equals(ActivitiesReferences.EXTRA_PROFILE_UPDATE)){
                        dbOper.updateUser(mCurrentUser);
                        finish();
                    }

                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner_select_department) {
            if(!mode.equals(ActivitiesReferences.EXTRA_PROFILE_SHOW)) {
                ArrayAdapter<CharSequence> careerAdapter = getArrayAdapter(mUserDepartmentSpinner.getSelectedItem().toString());
                careerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mUserCareerSpinner.setAdapter(careerAdapter);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {  }

    public boolean validateData(){
        boolean valid = true;
        if(mUserPhoneField.getText().toString().matches("")){
            valid = false;
            mUserPhoneField.setError(getString(R.string.error_empty_field));
        }
        return valid;
    }

    private ArrayAdapter<CharSequence> getArrayAdapter(String department){
        ArrayAdapter<CharSequence> careerAdapter;
        switch(department) {
            case "Ciencias Agrarias":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Ciencias_Agrarias,
                                android.R.layout.simple_spinner_item);
                break;

            case "Artes":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Artes,
                                android.R.layout.simple_spinner_item);
                break;

            case "Ciencias":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Ciencias,
                                android.R.layout.simple_spinner_item);
                break;
            case "Ciencias Económicas":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Ciencias_Económicas,
                                android.R.layout.simple_spinner_item);
                break;

            case "Ciencias Humanas":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Ciencias_Humanas,
                                android.R.layout.simple_spinner_item);
                break;

            case "Derecho, Ciencias Políticas y Sociales":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Politicas,
                                android.R.layout.simple_spinner_item);
                break;

            case "Enfermería":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Enfermería,
                                android.R.layout.simple_spinner_item);
                break;

            case "Ingeniería":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Ingeniería,
                                android.R.layout.simple_spinner_item);
                break;

            case "Medicina":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Medicina,
                                android.R.layout.simple_spinner_item);
                break;

            case "Medicina Veterinaria y Zootecnia":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Veterinaria_Zootecnia,
                                android.R.layout.simple_spinner_item);
                break;

            case "Odontología":
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Odontología,
                                android.R.layout.simple_spinner_item);
                break;

            default:
                careerAdapter = ArrayAdapter
                        .createFromResource(this, R.array.Ingeniería,
                                android.R.layout.simple_spinner_item);
                break;
        }
        return careerAdapter;
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
                mIcon11 = getCircularBitmap(mIcon11);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                Toast.makeText(ProfileOperationsActivity.this, getString(R.string.toast_error_profile_photo), Toast.LENGTH_LONG ).show();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

        public  Bitmap getCircularBitmap(Bitmap bitmap)
        {
            Bitmap output;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            float r = 0;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                r = bitmap.getHeight() / 2;
            } else {
                r = bitmap.getWidth() / 2;
            }

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(r, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }
}
