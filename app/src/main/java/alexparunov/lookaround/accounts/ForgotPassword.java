package alexparunov.lookaround.accounts;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import alexparunov.lookaround.R;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

  private EditText etEmail;
  FirebaseAuth firebaseAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);
    firebaseAuth = FirebaseAuth.getInstance();
    initializeWidgets();
  }

  private void initializeWidgets() {
    etEmail = (EditText) findViewById(R.id.activity_forgot_password_emailET);
    Button bSend = (Button) findViewById(R.id.activity_forgot_password_B);

    bSend.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.activity_forgot_password_B:
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
          Toast.makeText(ForgotPassword.this, "Please enter email", Toast.LENGTH_SHORT).show();
          return;
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                Log.d("FP", "Reset email sent: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                  Toast.makeText(ForgotPassword.this, "Email send failed.", Toast.LENGTH_LONG).show();
                  return;
                }

                Toast.makeText(ForgotPassword.this, "Reset password instructions were successfully send.", Toast.LENGTH_SHORT).show();
                finish();
              }
            });
        break;
    }
  }
}
