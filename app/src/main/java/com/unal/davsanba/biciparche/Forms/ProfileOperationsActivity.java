package com.unal.davsanba.biciparche.Forms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.unal.davsanba.biciparche.Data.ActivitiesReferences;
import com.unal.davsanba.biciparche.Objects.User;
import com.unal.davsanba.biciparche.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ProfileOperationsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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

        mUserNameField.setText(mCurrentUser.getName());
        mUserMailField.setText(mCurrentUser.getUsername());
        loadUserPhoto(mCurrentUser.getPhotoUrl());

        switch (mode){
            case ActivitiesReferences.EXTRA_PROFILE_CREATE:
                break;

            case ActivitiesReferences.EXTRA_PROFILE_UPDATE:
                break;

            case ActivitiesReferences.EXTRA_PROFILE_SHOW:
                break;
        }
    }

    private void update(){
        mUserPhoneField.setText(mCurrentUser.getPhoneNumber());
    }

    public void loadUserPhoto(String surl){
        Bitmap mIcon;
        try {
            URL url = new URL(surl);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();
            mIcon = BitmapFactory.decodeStream(in);
            mUserPhotoContainer.setImageBitmap(mIcon);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            Toast.makeText(ProfileOperationsActivity.this, getString(R.string.toast_error_profile_photo), Toast.LENGTH_LONG ).show();
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(view.getId() == R.id.spinner_select_department) {
            ArrayAdapter<CharSequence> careerAdapter;
            switch(mUserCareerSpinner.getSelectedItem().toString()) {
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
            careerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
