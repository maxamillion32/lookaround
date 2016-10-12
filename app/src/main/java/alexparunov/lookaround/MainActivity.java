package alexparunov.lookaround;

/*
* This class is playing a role of a router.
* If user is signed in, it redirects to ,available user, activity.
* If user is not signed in, it redirects to Sign In activity.*/
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import alexparunov.lookaround.accounts.SignIn;
import alexparunov.lookaround.authenticated.AuthMainActivity;

public class MainActivity extends AppCompatActivity {

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
                if(user != null) {
                    Log.d("MA","onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(MainActivity.this, AuthMainActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this,SignIn.class));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        finish();
    }
}
