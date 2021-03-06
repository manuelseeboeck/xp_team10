package at.tugraz.xp10;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import at.tugraz.xp10.fragments.AllListFragment;
import at.tugraz.xp10.fragments.CategoriesFragment;
import at.tugraz.xp10.fragments.ManageFriendsFragment;
import at.tugraz.xp10.fragments.UserSettingsFragment;
import at.tugraz.xp10.model.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    public User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        getUser();
        displayView(R.id.nav_lists);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
    }
    public void displayView(int viewId) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (viewId) {
            case R.id.nav_lists:
                fragment = (Fragment) AllListFragment.newInstance(2);
                title = "Overview";
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                gotoLoginActivity();
                break;
            case R.id.nav_manage_friends:
                fragment = ManageFriendsFragment.newInstance();
                title = "Manage Friends";
                break;
            case R.id.nav_manage_categories:
                fragment = CategoriesFragment.newInstance();
                title = "Manage Categories";
                break;
            case R.id.nav_user_settings:
                fragment = UserSettingsFragment.newInstance();
                title = "User Settings";
                break;
        }
        // clear all left fragments from the backstack
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            fm.popBackStack();
        }
        if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    private void gotoLoginActivity() {
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
        finish();
        MainActivity.this.startActivity(myIntent);
    }
    protected void getUser() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
        Query userQuery = database.child("users").child(uid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser.setData(dataSnapshot.getValue(User.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public User getCurrentUser() {
        return currentUser;
    }
}