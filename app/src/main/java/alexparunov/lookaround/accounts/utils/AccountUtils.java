package alexparunov.lookaround.accounts.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class AccountUtils {

    private static boolean isValidEmail(String target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private static boolean isValidPassword(String target){
        return Pattern.compile("^[a-zA-Z]\\w{6,18}$").matcher(target).matches();
    }

    private static boolean isEmpty(String target) {
        return TextUtils.isEmpty(target);
    }

    public static String validateSignUpForm(String email, String password, String passwordRpt) {
        if(isEmpty(email) || isEmpty(password) || isEmpty(passwordRpt)) {
            return "All fields are required";
        }

        if(!isValidEmail(email)) {
            return "Email is invalid type";
        }

        if(!isValidPassword(password)) {
            return "Password should begin with letter, be 6-18 characters " +
                    "long and must contain only letters, numbers, and underscore";
        }

        if(!password.contentEquals(passwordRpt)) {
            return "Passwords should match";
        }

        return null;
    }

    public static String validateSignInForm(String email, String password) {
        if(isEmpty(email) || isEmpty(password)) {
            return "All fields are required";
        }

        return null;
    }
}
