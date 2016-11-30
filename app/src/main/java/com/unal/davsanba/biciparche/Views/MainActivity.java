package com.unal.davsanba.biciparche.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.unal.davsanba.biciparche.Data.ActRefs;
import com.unal.davsanba.biciparche.Data.FbRef;
import com.unal.davsanba.biciparche.Forms.GroupOperationsActivity;
import com.unal.davsanba.biciparche.Forms.NewPersonalRouteActivity;
import com.unal.davsanba.biciparche.Forms.ProfileOperationsActivity;
import com.unal.davsanba.biciparche.Objects.Group;
import com.unal.davsanba.biciparche.Objects.ListAdapters.GroupListAdapter;
import com.unal.davsanba.biciparche.Objects.ListAdapters.PersonalListAdapter;
import com.unal.davsanba.biciparche.Objects.ListAdapters.UserListAdapter;
import com.unal.davsanba.biciparche.Objects.Route;
import com.unal.davsanba.biciparche.Objects.User;
import com.unal.davsanba.biciparche.R;
import com.unal.davsanba.biciparche.Util.DatabaseOperations;
import com.unal.davsanba.biciparche.Util.RouteOperationsManager;
import com.unal.davsanba.biciparche.Util.UserOperationsManager;

import java.util.ArrayList;

import static com.unal.davsanba.biciparche.Util.DatabaseOperations.groupFromSnapshot;
import static java.lang.String.valueOf;

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

        mUserGroups = new ArrayList<>();

        mShowRouteLv = (ListView) findViewById(R.id.listView_main_route);
        mShowRouteLv.setOnItemClickListener(this);


        mShowGroupLv = (ListView) findViewById(R.id.listView_main_group);
        mShowGroupLv.setOnItemClickListener(this);
        mShowGroupLv.setAdapter(new GroupListAdapter(getApplicationContext(), mUserGroups));

        mBuscarParche = (Button) findViewById(R.id.btn_search);
        mBuscarParche.setOnClickListener(this);

        mNewRouteBtn = (Button) findViewById(R.id.btn_new_route);
        mNewRouteBtn.setOnClickListener(this);

        mNewGroupBtn = (Button) findViewById(R.id.btn_new_group);
        mNewGroupBtn.setOnClickListener(this);

        new LoadTask().execute();
        groupsListener();
    }

    private void groupsListener() {
        mDatabaseReference.child(FbRef.REQUEST_REFERENCE).child(mAuth.getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        printPopUp(dataSnapshot.getKey());
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {  }
                });
    }

    private void printPopUp(final String key) {
        final DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE);
        final Context context = MainActivity.this;
        final ArrayList<User> users = new ArrayList<>();
        final ArrayList<Group> groups = new ArrayList<>();

        LayoutInflater li = LayoutInflater.from(context);
        View getUsrIdView = li.inflate(R.layout.confirm_group_popup, null);

        AlertDialog.Builder groupConfirm = new AlertDialog.Builder(context);
        groupConfirm.setView(getUsrIdView);
        groupConfirm.setTitle("Nueva solcitud de grupo");

        final ListView mPopUpGroupLv = (ListView) getUsrIdView.findViewById(R.id.listView_popup_route);
        mPopUpGroupLv.setAdapter(new GroupListAdapter(context,groups));
        mPopUpGroupLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group cGroup = (Group) parent.getAdapter().getItem(position);
                Intent i = new Intent(MainActivity.this, GroupOperationsActivity.class);
                i.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_SHOW);
                i.putExtra(ActRefs.EXTRA_GROUP, cGroup);
                startActivity(i);
            }
        });



        final ListView mPopUpUsrLv = (ListView) getUsrIdView.findViewById(R.id.listView_popup_user);
        mPopUpUsrLv.setAdapter(new UserListAdapter(context,users));
        mPopUpUsrLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User cUser = (User) parent.getAdapter().getItem(position);
                Intent update  = new Intent(getApplicationContext(), ProfileOperationsActivity.class);
                update.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_SHOW);
                update.putExtra(ActRefs.EXTRA_USER,cUser);
                startActivity(update);
            }
        });


        addData(users, groups, mPopUpUsrLv, mPopUpGroupLv, key);

        // set dialog message
        groupConfirm.setCancelable(true)
                 .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         Query query = mDbRef.child(FbRef.LIST_REFERENCE).child(key);
                         query.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 if (dataSnapshot.exists()) {
                                     long index = dataSnapshot.getChildrenCount();
                                     mDbRef.child(FbRef.LIST_REFERENCE).child(key).child(String.valueOf(index))
                                             .setValue(mAuth.getCurrentUser().getUid());
                                     mDbRef.child(FbRef.REQUEST_REFERENCE).child(mAuth.getCurrentUser().getUid()).child(key).removeValue();
                                     mDbRef.child(FbRef.USER_REFERENCE).child(mAuth.getCurrentUser().getUid()).child(FbRef.USER_GROUPS_KEY).child(key).setValue(true);
                                 }
                             }
                             @Override
                             public void onCancelled(DatabaseError databaseError) { }
                         });
                     }
                 })
                     .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDbRef.child(FbRef.REQUEST_REFERENCE).child(mAuth.getCurrentUser().getUid()).child(key).removeValue();
                    }
                });
        groupConfirm.create().show();
    }


    protected void addData(final ArrayList<User> users, final ArrayList<Group> groups, final ListView userlv, final ListView grouplv, String key){
        final DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference(FbRef.DATABASE_REFERENCE);
        Query qGroup = mDbRef.child(FbRef.GROUP_REFERENCE).child(key);
        qGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group g = DatabaseOperations.groupFromSnapshot(dataSnapshot);
                groups.add(g);
                GroupListAdapter gl = (GroupListAdapter) grouplv.getAdapter();
                gl.notifyDataSetChanged();
                Query qUser = mDbRef.child(FbRef.USER_REFERENCE).child(g.getGroupAdminUserID());
                qUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users.add(UserOperationsManager.userFromDataSnapshot(dataSnapshot));
                        UserListAdapter gl = (UserListAdapter) userlv.getAdapter();
                        gl.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {  }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {  }
        });
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
            Log.d(TAG, valueOf(cGroup.getGroupRoute() == null));
            Intent i = new Intent(MainActivity.this, GroupOperationsActivity.class);
            i.putExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW, ActRefs.EXTRA_UPDATE);
            i.putExtra(ActRefs.EXTRA_GROUP, cGroup);
            startActivity(i);
        }
    }
    public void printUserGroups() {
        final ArrayList<Group> groups = new ArrayList<>();
        Query query = mDatabaseReference.child(FbRef.USER_REFERENCE).child(mAuth.getCurrentUser().getUid())
                .child(FbRef.USER_GROUPS_KEY).orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot ds: dataSnapshot.getChildren()){
                    Query q = mDatabaseReference.child(FbRef.GROUP_REFERENCE).child(ds.getKey());
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUserGroups.add(groupFromSnapshot(dataSnapshot));
                            GroupListAdapter gl = (GroupListAdapter) mShowGroupLv.getAdapter();
                            gl.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {  }
        });


        /*
            Query query = mDatabaseReference.child(FbRef.GROUP_REFERENCE).orderByChild(FbRef.GROUP_ADMIN_ID_KEY)
                .equalTo(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "hola " + valueOf(dataSnapshot.getChildrenCount()));
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
        */
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


    private class LoadTask extends AsyncTask<Void, Void, Void> {

        public LoadTask( ) {
        }
        @Override
        protected Void doInBackground(Void... params) {
            printUserRoutes();
            printUserGroups();
            return null;
        }
        protected void onPostExecute(Bitmap result) {
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
