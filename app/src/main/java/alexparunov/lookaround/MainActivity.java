package alexparunov.lookaround;

/*
* This class is playing a role of a router.
* If user is signed in, it redirects to ,available user, activity.
* If user is not signed in, it redirects to Sign In activity.*/

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import alexparunov.lookaround.accounts.SignIn;
import alexparunov.lookaround.authenticated.AuthMainActivity;

public class MainActivity extends AppCompatActivity {

    private final String ARE_ALL_PERMISSIONS_GRANTED = "areAllPermissionsGranted";

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("MA", "onAuthStateChanged:signed_in:" + user.getUid());
                        startActivity(new Intent(MainActivity.this, AuthMainActivity.class));
                } else {
                        startActivity(new Intent(MainActivity.this, SignIn.class));
                       }
                }
            };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth != null) {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        if(!sharedPreferences.getBoolean(ARE_ALL_PERMISSIONS_GRANTED,false)) {
            askForPermissions();
        }
        
        boolean allPermissionsGranted = sharedPreferences.getBoolean(ARE_ALL_PERMISSIONS_GRANTED,false);

        if(!allPermissionsGranted) {
            onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        finish();
    }

    private float getAPIVersion() {
        float version = 1f;
        try {
            version = Float.valueOf(Build.VERSION.RELEASE.substring(0,2));
        } catch (NumberFormatException e) {
            Log.e("MA", "Error retrieve api version" + e.getMessage());
        }

        return version;
    }


    private final int MY_PERMISSION_FINE_LOCATION = 1;
    private final int MY_PERMISSION_INTERNET = 2;
    private final int MY_PERMISSION_NETWORK_STATE = 3;

    private void askForPermissions() {
        if(Float.compare(getAPIVersion(),6.0f) >= 0) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }

            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET}, MY_PERMISSION_INTERNET);
            }

            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, MY_PERMISSION_NETWORK_STATE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
            case MY_PERMISSION_INTERNET:
            case MY_PERMISSION_NETWORK_STATE:

                if(grantResults.length <= 0
                        || grantResults[0] == PackageManager.PERMISSION_DENIED ) {
                    Toast.makeText(MainActivity.this,"All permissions must be granted!",Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ARE_ALL_PERMISSIONS_GRANTED,true);
                editor.apply();
                break;
        }
    }

}
