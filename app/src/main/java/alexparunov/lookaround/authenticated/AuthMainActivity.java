package alexparunov.lookaround.authenticated;

import android.app.ActionBar;
import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;

import alexparunov.lookaround.MainActivity;
import alexparunov.lookaround.R;
import alexparunov.lookaround.authenticated.fragments.GMapFragment;
import alexparunov.lookaround.authenticated.fragments.NavigationDrawerFragment;
import alexparunov.lookaround.authenticated.fragments.ProfileFragment;

public class AuthMainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private CharSequence mTitle;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);
        firebaseAuth = FirebaseAuth.getInstance();

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.activity_auth_main_navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.activity_auth_main_navigation_drawer,
                (DrawerLayout) findViewById(R.id.activity_auth_main_drawer_layout));

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_auth_main_container,new GMapFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment = null;

        final int PROFILE_POSITION = 0;
        final int MY_EVENTS_POSITION = 1;
        final int SETTINGS_POSITION = 2;
        final int SIGNOUT_POSITION = 3;

        switch (position) {
            case PROFILE_POSITION:
                mTitle = "Profile";
                fragment = new ProfileFragment();
                break;
            case MY_EVENTS_POSITION:
                mTitle = "My Events";
                break;
            case SETTINGS_POSITION:
                mTitle = "Settings";
                break;
            case SIGNOUT_POSITION:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(AuthMainActivity.this, MainActivity.class));
                break;
            default:
                break;
        }

        ActionBar actionBar = getActionBar();

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.activity_auth_main_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            if(actionBar != null) {
                actionBar.setTitle(mTitle);
            }
        }

    }
}
