package com.unal.davsanba.biciparche.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.ActRefs;
import com.unal.davsanba.biciparche.Data.FbRef;
import com.unal.davsanba.biciparche.Forms.GroupOperationsActivity;
import com.unal.davsanba.biciparche.Forms.NewPersonalRouteActivity;
import com.unal.davsanba.biciparche.Objects.Group;
import com.unal.davsanba.biciparche.Objects.ListAdapters.GroupListAdapter;
import com.unal.davsanba.biciparche.Objects.ListAdapters.PersonalListAdapter;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.R;
import com.unal.davsanba.biciparche.Util.DatabaseOperations;
import com.unal.davsanba.biciparche.Util.RouteOperationsManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    private Button mNewRouteBtn;
    private Button mNewGroupBtn;
    private Button mBuscarParche;

    private ListView mShowRouteLv;
    private ListView mShowGroupLv;

    private ArrayList<Group> mUserGroups;

    private static final String TAG = "Main_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(FbRef.DATABASE_REFERENCE);

        mShowRouteLv = (ListView) findViewById(R.id.listView_main_route);
        mShowRouteLv.setOnItemClickListener(this);

        mShowGroupLv = (ListView) findViewById(R.id.listView_main_group);
        mShowGroupLv.setOnItemClickListener(this);

        mBuscarParche = (Button) findViewById(R.id.btn_search);
        mBuscarParche.setOnClickListener(this);

        mNewRouteBtn = (Button) findViewById(R.id.btn_new_route);
        mNewRouteBtn.setOnClickListener(this);

        mNewGroupBtn = (Button) findViewById(R.id.btn_new_group);
        mNewGroupBtn.setOnClickListener(this);

        mUserGroups = new ArrayList<>();

        printUserGroups();
        printUserRoutes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId() ){

            case R.id.btn_new_route:
                Intent cnr = new Intent(MainActivity.this, NewPersonalRouteActivity.class);
                cnr.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_CREATE);
                startActivityForResult(cnr, ActRefs.RC_NEW_PERSONAL_ROUTE);
                break;

            case R.id.btn_new_group:
                Intent cng = new Intent(MainActivity.this, GroupOperationsActivity.class);
                cng.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_CREATE);
                startActivityForResult(cng, ActRefs.RC_CREATE_GROUP);
                break;

            case R.id.btn_search:
                startActivity(new Intent(MainActivity.this, null));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.listView_main_route) {

            Route mRoute = (Route) parent.getAdapter().getItem(position);
            Log.d(TAG, "Ruta seleccionada " + mRoute.getRouteID());
            Intent i = new Intent(MainActivity.this, NewPersonalRouteActivity.class);
            i.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_UPDATE);
            i.putExtra(ActRefs.EXTRA_ROUTE, mRoute);
            startActivityForResult(i, ActRefs.RC_NEW_PERSONAL_ROUTE);

        }else if(parent.getId() == R.id.listView_main_group){

            Group cGroup = (Group) parent.getAdapter().getItem(position);
            Log.d(TAG, String.valueOf(cGroup.getGroupRoute() == null));
            Intent i = new Intent(MainActivity.this, GroupOperationsActivity.class);
            i.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_UPDATE);
            i.putExtra(ActRefs.EXTRA_GROUP, cGroup);
            startActivity(i);
        }
    }
    public void printUserGroups() {
        Query query = mDatabaseReference.child(FbRef.GROUP_REFERENCE).orderByChild(FbRef.GROUP_ADMIN_ID_KEY)
                .equalTo(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "hola " + String.valueOf(dataSnapshot.getChildrenCount()));
                ArrayList<Group> groups = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    groups.add(DatabaseOperations.groupFromSnapshot(postSnapshot));
                }
                GroupListAdapter adapter = new GroupListAdapter(getApplicationContext(), groups);
                mShowGroupLv.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {  }
        });
    }


    public void printUserRoutes(){
        Query query = mDatabaseReference.child(FbRef.ROUTE_REFERENCE).orderByChild(FbRef.ROUTE_OWNER_ID_KEY)
                .equalTo(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Route> routes = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    routes.add(RouteOperationsManager.RouteFromSnapshot(postSnapshot));
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
        if (requestCode == ActRefs.RC_NEW_PERSONAL_ROUTE) {
            if (resultCode == Activity.RESULT_OK) {
                Route newRoute = data.getParcelableExtra("route");
                String text = getString(R.string.toast_route_created) + newRoute.getRouteName();
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, R.string.toast_error_route_not_created, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == ActRefs.RC_CREATE_GROUP) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(MainActivity.this, getString(R.string.toast_group_created), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, R.string.toast_error_route_not_created, Toast.LENGTH_SHORT).show();
            }
        }
    }

/*
    private void printUserGroups() {
      Query query = mDatabaseReference.child(FbRef.USER_REFERENCE).child(mAuth.getCurrentUser().getUid()).child(FbRef.USER_ROUTES_KEY).orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                   getUserGroups(postSnapshot.getKey());
                }
                GroupListAdapter adapter = new GroupListAdapter(getApplicationContext(), mUserGroups);
                mShowGroupLv.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getUserGroups(String groupId) {
        Query query = mDatabaseReference.child(FbRef.GROUP_REFERENCE).orderByKey().equalTo(groupId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "entra aqui? : " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mUserGroups.add(DatabaseOperations.groupFromSnapshot(postSnapshot));
                    Log.d(TAG, String.valueOf(mUserGroups.size()));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {  }
        });
    }
*/
}
