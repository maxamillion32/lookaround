package alexparunov.lookaround.accounts;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import alexparunov.lookaround.R;
import alexparunov.lookaround.accounts.utils.AccountUtils;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

  private EditText etEmail;
  private EditText etPassword;
  private EditText etPasswordRpt;

  private FirebaseAuth firebaseAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    firebaseAuth = FirebaseAuth.getInstance();

    initializeWidgets();
  }

  private void initializeWidgets() {
    etEmail = (EditText) findViewById(R.id.activity_sign_up_emailET);
    etPassword = (EditText) findViewById(R.id.activity_sign_up_passwordET);
    etPasswordRpt = (EditText) findViewById(R.id.activity_sign_up_passwordRptET);
    Button bSignUp = (Button) findViewById(R.id.activity_sign_up_B);
    TextView tvCancel = (TextView) findViewById(R.id.activity_sign_up_CancelTV);

    bSignUp.setOnClickListener(this);
    tvCancel.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.activity_sign_up_B:
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordRpt = etPasswordRpt.getText().toString().trim();

        String errorMessage = AccountUtils.validateSignUpForm(email, password, passwordRpt);

        if (errorMessage != null) {
          Toast.makeText(SignUp.this, errorMessage, Toast.LENGTH_SHORT).show();
          return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("SU", "createUserWithEmail:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                  Toast.makeText(getBaseContext(), "Registration failed. Please check Internet connection and requested credentials.", Toast.LENGTH_SHORT).show();
                }
              }
            });
        break;
      case R.id.activity_sign_up_CancelTV:
        startActivity(new Intent(SignUp.this, SignIn.class));
        finish();
        break;
    }
  }
}
