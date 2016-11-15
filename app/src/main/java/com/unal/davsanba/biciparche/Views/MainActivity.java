package com.unal.davsanba.biciparche.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.ActivitiesReferences;
import com.unal.davsanba.biciparche.Data.FirebaseReferences;
import com.unal.davsanba.biciparche.Forms.NewPersonalRouteActivity;
import com.unal.davsanba.biciparche.Objects.PersonalListAdapter;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.R;
import com.unal.davsanba.biciparche.Util.RouteOperationsManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    private Button mNewRouteBtn;
    private Button mSearchUsrBtn;

    private ListView mShowRouteLv;

    private static final String TAG = "Main_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(FirebaseReferences.DATABASE_REFERENCE).child(FirebaseReferences.ROUTE_REFERENCE);

        mShowRouteLv = (ListView) findViewById(R.id.listV_main_route);
        mShowRouteLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getId() == R.id.listV_main_route) {
                    Route mRoute = (Route) parent.getAdapter().getItem(position);
                    Log.d(TAG, "Ruta seleccionada " + mRoute.getRouteID());
                    Intent i = new Intent(MainActivity.this, NewPersonalRouteActivity.class);
                    i.putExtra(ActivitiesReferences.EXTRA_ROUTE_CREATE_UPDATE, ActivitiesReferences.EXTRA_ROUTE_UPDATE);
                    i.putExtra(ActivitiesReferences.EXTRA_ROUTE, mRoute);
                    startActivityForResult(i, ActivitiesReferences.RC_NEW_PERSONAL_ROUTE);
                }
            }
        });

        mNewRouteBtn = (Button) findViewById(R.id.btn_new_route);
        mNewRouteBtn.setOnClickListener(this);

        mSearchUsrBtn = (Button) findViewById(R.id.btn_search_usr);
        mSearchUsrBtn.setOnClickListener(this);

        printUserRoutes();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId() ){

            case R.id.btn_new_route:
                Intent i = new Intent(MainActivity.this, NewPersonalRouteActivity.class);
                i.putExtra(ActivitiesReferences.EXTRA_ROUTE_CREATE_UPDATE, ActivitiesReferences.EXTRA_ROUTE_CREATE);
                startActivityForResult(i, ActivitiesReferences.RC_NEW_PERSONAL_ROUTE);
                break;

            case R.id.btn_search_usr:

                break;
        }
    }

    public void printUserRoutes(){
        Query query = mDatabaseReference.orderByChild(FirebaseReferences.ROUTE_OWNER_ID_KEY)
                .equalTo(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,String.valueOf(dataSnapshot.getChildrenCount()));
                ArrayList<Route> routes = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Route route = new Route(
                            postSnapshot.getKey(),
                            postSnapshot.child(FirebaseReferences.ROUTE_OWNER_ID_KEY).getValue().toString(),
                            postSnapshot.child(FirebaseReferences.ROUTE_NAME_KEY).getValue().toString(),
                            postSnapshot.child(FirebaseReferences.ROUTE_DAYS_KEY).getValue().toString(),
                            postSnapshot.child(FirebaseReferences.ROUTE_HOUR_KEY).getValue().toString(),
                            RouteOperationsManager.strToLatLng(postSnapshot.child(FirebaseReferences.ROUTE_START_KEY)),
                            RouteOperationsManager.strToLatLng(postSnapshot.child(FirebaseReferences.ROUTE_END_KEY)),
                            RouteOperationsManager.toLatLngList(postSnapshot.child(FirebaseReferences.ROUTE_MARKS_KEY))
                            );
                    routes.add(route);

                }
                PersonalListAdapter adapter = new PersonalListAdapter(getApplicationContext(), routes);
                mShowRouteLv.setAdapter(adapter);
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
                printUserRoutes();
                Route newRoute = data.getParcelableExtra("route");
                String text = getString(R.string.toast_route_created) + newRoute.getRouteName();
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, R.string.toast_error_route_not_created, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
