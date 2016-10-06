package alexparunov.lookaround.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import alexparunov.lookaround.R;

public class SignIn extends Activity implements View.OnClickListener{

    private EditText etUsernameEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initializeWidgets();
    }

    private void initializeWidgets() {
        etUsernameEmail = (EditText) findViewById(R.id.activity_sign_in_usernameEmailET);
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

                break;
            case R.id.activity_sign_in_signUpTV:
                startActivity(new Intent(SignIn.this,SignUp.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
