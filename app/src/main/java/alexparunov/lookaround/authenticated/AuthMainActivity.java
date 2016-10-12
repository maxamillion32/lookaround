package alexparunov.lookaround.authenticated;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import alexparunov.lookaround.MainActivity;
import alexparunov.lookaround.R;
import alexparunov.lookaround.authenticated.fragments.GMapFragment;
import alexparunov.lookaround.authenticated.fragments.ProfileFragment;

public class AuthMainActivity extends AppCompatActivity {

    private CharSequence mTitle;
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);
        firebaseAuth = FirebaseAuth.getInstance();

        initializeWidgets();

        Fragment mFragment = getFragmentManager().findFragmentById(R.id.activity_auth_main_drawer_layout);
        if(!(mFragment instanceof GMapFragment)) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.activity_auth_main_container, new GMapFragment());
            fragmentTransaction.commit();
        }
    }

    private void initializeWidgets() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.activity_auth_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav_view);

        navigationView.setNavigationItemSelectedListener(new NavItemSelectedListener());
    }

    private class NavItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.menu_nav_profile:
                    mTitle = "Profile";
                    fragment = new ProfileFragment();
                    break;

                case R.id.menu_nav_signout:
                    firebaseAuth.signOut();
                    finish();
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.activity_auth_main_container)).commit();
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
                setTitle(mTitle);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            return false;
        }
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.activity_auth_main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
