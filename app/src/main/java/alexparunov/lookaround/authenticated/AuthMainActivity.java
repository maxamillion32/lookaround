package alexparunov.lookaround.authenticated;

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

public class AuthMainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private FirebaseAuth firebaseAuth;

    private final int PROFILE_POSITION = 0;
    private final int MY_EVENTS_POSITION = 1;
    private final int SIGNOUT_POSITION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);
        firebaseAuth = FirebaseAuth.getInstance();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.activity_auth_main_navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.activity_auth_main_navigation_drawer,
                (DrawerLayout) findViewById(R.id.activity_auth_main_drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment = null;

        switch (position) {
            case PROFILE_POSITION:
                break;
            case MY_EVENTS_POSITION:
                break;
            case SIGNOUT_POSITION:
                firebaseAuth.signOut();
                startActivity(new Intent(AuthMainActivity.this, MainActivity.class));
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.activity_auth_main_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }
}
