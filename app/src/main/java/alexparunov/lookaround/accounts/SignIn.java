package alexparunov.lookaround.accounts;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import alexparunov.lookaround.R;

public class SignIn extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initializeWidgets();
    }

    private void initializeWidgets() {
        EditText etUsernameEmailSignIn = (EditText) findViewById(R.id.activity_sign_in_usernameEmailET);
        EditText etPasswordSignIn = (EditText) findViewById(R.id.activity_sign_in_passwordET);
        Button bSignIn = (Button) findViewById(R.id.activity_sign_in_B);
        TextView tvSignUpSignIn = (TextView) findViewById(R.id.activity_sign_in_signUpTV);

    }
}
