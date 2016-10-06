package alexparunov.lookaround;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import alexparunov.lookaround.accounts.SignIn;

public class MainActivity extends Activity {

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
                    firebaseAuth.signOut();
                    Log.d("MA","onAuthStateChanged:signed_in:" + user.getUid());
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
