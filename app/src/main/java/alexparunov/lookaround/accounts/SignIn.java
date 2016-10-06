package alexparunov.lookaround.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import alexparunov.lookaround.MainActivity;
import alexparunov.lookaround.R;
import alexparunov.lookaround.accounts.utils.AccountUtils;

public class SignIn extends Activity implements View.OnClickListener{

    private EditText etEmail;
    private EditText etPassword;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeWidgets();
    }

    private void initializeWidgets() {
        etEmail = (EditText) findViewById(R.id.activity_sign_in_usernameEmailET);
        etPassword = (EditText) findViewById(R.id.activity_sign_in_passwordET);
        Button bSignIn = (Button) findViewById(R.id.activity_sign_in_B);
        TextView tvSignUp = (TextView) findViewById(R.id.activity_sign_in_signUpTV);

        bSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_sign_in_B:

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                String errorMessage = AccountUtils.validateSignInForm(email,password);

                if(errorMessage != null) {
                    Toast.makeText(SignIn.this,errorMessage,Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("SI", "signInWithEmail:onComplete:" + task.isSuccessful());

                                if(!task.isSuccessful()) {
                                    Log.w("SI", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(SignIn.this, "Sign In failed. Please check credentials.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                /* We start MainActivity Class because this class will redirect
                                * us to, available user activity. */
                                startActivity(new Intent(SignIn.this, MainActivity.class));
                            }
                        });
                break;
            case R.id.activity_sign_in_signUpTV:
                startActivity(new Intent(SignIn.this,SignUp.class));
                break;
        }
    }

    //Since this is our last activity in Activity Stack, we want to exit app.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /*This is called whenever we click Sign In button
    and redirection to MainActivity Class is performed*/
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
