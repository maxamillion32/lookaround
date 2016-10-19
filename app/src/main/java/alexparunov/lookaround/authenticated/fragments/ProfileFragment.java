package alexparunov.lookaround.authenticated.fragments;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import alexparunov.lookaround.R;
import alexparunov.lookaround.accounts.utils.AccountUtils;

public class ProfileFragment extends Fragment implements View.OnClickListener {

  Context context;
  View view;
  private EditText etFullName;
  private EditText etEmail;
  private EditText etPassword1;
  private EditText etPassword2;
  private FirebaseUser firebaseUser;
  private String fullName;
  private String email;
  private String password1;
  private String password2;

  public ProfileFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    context = view.getContext();
    this.view = view;
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getActivity().setTitle("Profile");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    firebaseUser = firebaseAuth.getCurrentUser();

    if (firebaseUser != null) {
      fullName = firebaseUser.getDisplayName();
      email = firebaseUser.getEmail();
    }

    etFullName = (EditText) view.findViewById(R.id.fragment_profile_nameET);
    etEmail = (EditText) view.findViewById(R.id.fragment_profile_emailET);
    etPassword1 = (EditText) view.findViewById(R.id.fragment_profile_newPassword1ET);
    etPassword2 = (EditText) view.findViewById(R.id.fragment_profile_newPassword2ET);
    Button bSubmit = (Button) view.findViewById(R.id.fragment_profile_submitB);


    if (fullName != null)
      etFullName.setText(fullName);
    if (email != null)
      etEmail.setText(email);

    bSubmit.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.fragment_profile_submitB:
        if (context == null)
          return;

        final String errorMessage1 = "Please enter valid email address";
        final String errorMessage2 = "Password should begin with letter, be 6-18 characters " +
            "long and must contain only letters, numbers, and underscore";
        final String errorMessage3 = "Passwords should match";

        if (etFullName != null)
          fullName = etFullName.getText().toString().trim();
        if (etEmail != null)
          email = etEmail.getText().toString().trim();
        if (etPassword1 != null && etPassword2 != null) {
          password1 = etPassword1.getText().toString().trim();
          password2 = etPassword2.getText().toString().trim();
        }

        UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();

        if (fullName != null) {
          if (!fullName.isEmpty()) {
            request.setDisplayName(fullName);
          }
        }

        if (email != null) {
          if (!email.isEmpty()) {
            if (AccountUtils.isValidEmail(email)) {
              firebaseUser.updateEmail(email)
                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      Toast.makeText(context,"Email was successfully updated",Toast.LENGTH_SHORT).show();
                    }
                  });
            }
            else {
              Toast.makeText(context, errorMessage1, Toast.LENGTH_SHORT).show();
            }
          }
        }

        firebaseUser.updateProfile(request.build())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  Log.d("PR", "User profile updated.");
                  Toast.makeText(context, "Profile was successfully updated", Toast.LENGTH_SHORT).show();
                } else {
                  Toast.makeText(context, "Error occurred during update. Please check Internet connection.", Toast.LENGTH_SHORT).show();
                }
              }
            });

        if (password1 != null && password2 != null) {
          if (!password1.isEmpty() && !password2.isEmpty()) {
            if (password1.contentEquals(password2)) {
              if (AccountUtils.isValidPassword(password1)) {
                firebaseUser.updatePassword(password1)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context,"Password was successfully updated", Toast.LENGTH_SHORT).show();
                      }
                    });
              } else {
                Toast.makeText(context, errorMessage2, Toast.LENGTH_SHORT).show();
              }
            } else {
              Toast.makeText(context,errorMessage3,Toast.LENGTH_SHORT).show();
            }
          }
        }
        break;
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    getActivity().setTitle("Events Nearby");

    CardView cardView = (CardView) getActivity().findViewById(R.id.activity_auth_main_cardview);
    if (cardView != null) {
      cardView.setVisibility(View.VISIBLE);
    }
  }
}
