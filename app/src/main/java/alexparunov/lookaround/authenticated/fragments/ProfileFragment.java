package alexparunov.lookaround.authenticated.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import alexparunov.lookaround.R;
import alexparunov.lookaround.accounts.utils.AccountUtils;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    public  ProfileFragment(){}

    Context context;
    private EditText etFullName;
    private EditText etEmail;
    private EditText etPassword;
    private FirebaseUser firebaseUser;

    private String fullName;
    private String email;
    private String password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            fullName = firebaseUser.getDisplayName();
            email = firebaseUser.getEmail();
        }

        etFullName = (EditText) view.findViewById(R.id.fragment_profile_nameET);
        etEmail = (EditText) view.findViewById(R.id.fragment_profile_emailET);
        etPassword = (EditText) view.findViewById(R.id.fragment_profile_passwordET);
        Button bSubmit = (Button) view.findViewById(R.id.fragment_profile_submitB);

        if(etFullName != null & etEmail != null) {
            if(fullName != null)
                etFullName.setText(fullName);
            if(email != null)
                etEmail.setText(email);
        }
        if(bSubmit != null) {
            bSubmit.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_profile_submitB:
                if(context == null)
                    return;

                String errorMessage1 = "Please enter valid email address";

                String errorMessage2 = "Password should begin with letter, be 6-18 characters " +
                        "long and must contain only letters, numbers, and underscore";

                if(etFullName != null)
                    fullName = etFullName.getText().toString().trim();
                if(etEmail!= null)
                    email = etEmail.getText().toString().trim();
                if(etPassword!= null)
                    password = etPassword.getText().toString().trim();

                UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();

                    if(fullName != null) {
                        if (!fullName.isEmpty()) {
                            request.setDisplayName(fullName);
                        }
                    }

                    if(email != null) {
                        if (!email.isEmpty()) {
                            if (AccountUtils.isValidEmail(email))
                                firebaseUser.updateEmail(email);
                            else
                                Toast.makeText(context, errorMessage1, Toast.LENGTH_SHORT).show();
                        }
                    }

                firebaseUser.updateProfile(request.build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("PR", "User profile updated.");
                                Toast.makeText(context,"Profile was successfully updated",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(context,"Error occurred during update",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                if(password != null) {
                    if (!password.isEmpty()) {
                        Log.d("PR", password);
                        if (AccountUtils.isValidPassword(password))
                            firebaseUser.updatePassword(password);
                        else
                            Toast.makeText(context, errorMessage2, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
