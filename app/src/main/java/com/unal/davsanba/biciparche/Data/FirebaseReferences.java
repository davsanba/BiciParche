package com.unal.davsanba.biciparche.Data;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.unal.davsanba.biciparche.R;

/**
 * Created by davsa on 19/10/2016.
 */
public class FirebaseReferences {
    public static final String DATABASE_REFERENCE   = "Biciparche";
    public static final String USER_REFERENCE       = "users";
    public static final String ROUTE_REFERENCE      = "routes";

    public static final String USER_USERNAME_KEY    = "user_username";
    public static final String USER_NAME_KEY    = "user_username";
    public static final String USER_DEPARTMENT_KEY  = "user_department";
    public static final String USER_CAREER_KEY      = "user_career";
    public static final String USER_PHONENUMBER_KEY = "user_phone_number";
    public static final String USER_PHOTO_KEY       = "user_photoUrl";

    public static final String ROUTE_OWNER_ID_KEY   = "route_owner_id";
    public static final String ROUTE_NAME_KEY       = "route_name";
    public static final String ROUTE_DAYS_KEY       = "route_days";
    public static final String ROUTE_HOUR_KEY       = "route_hour";
    public static final String ROUTE_START_KEY      = "route_start";
    public static final String ROUTE_END_KEY        = "route_end";

    public static class SearchUserActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search_user);
        }
    }
}
