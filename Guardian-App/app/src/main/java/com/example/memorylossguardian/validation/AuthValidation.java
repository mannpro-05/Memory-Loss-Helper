package com.example.memorylossguardian.validation;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * This class does the auth validation for the auth package.
 * Note: In the validation methods i've set the error null because if there is an error and that
 * error is resolved it will still be there as it has not been removed to we have to set it to null.
 */
public class AuthValidation {
    EditText email, password, confirmPassword;

    //Constructor for registration page.
    public AuthValidation(EditText email, EditText password, EditText confirmPassword) {
        this.email = email;
        this.confirmPassword = confirmPassword;
        this.password = password;
    }

    //Constructor for registration page.
    public AuthValidation(EditText email, EditText password) {
        this.email = email;
        this.password = password;
    }

    //Validator for registration.
    public boolean registerValidator(String inpEmail, String inputPassword, String inputConfirmPassword) {

        if (TextUtils.isEmpty(inpEmail)) {
            email.setError("Email is Required.");
            return false;
        } else {
            email.setError(null);
        }

        if (TextUtils.isEmpty(inputPassword)) {
            password.setError("Password is Required.");
            return false;
        } else {
            password.setError(null);
        }

        if (inputPassword.length() < 6) {
            password.setError("Password Must be >= 6 Characters");
            return false;
        } else {
            password.setError(null);
        }
        if (inputConfirmPassword.length() < 6) {
            confirmPassword.setError("Password Must be >= 6 Characters");
            return false;
        } else {
            confirmPassword.setError(null);
        }
        if (TextUtils.isEmpty(inputConfirmPassword)) {
            confirmPassword.setError("Field is required");
            return false;
        } else {
            confirmPassword.setError(null);
        }
        if (!inputPassword.equals(inputConfirmPassword)) {
            password.setError("The Passwords Do not match");
            password.setFocusable(true);
            return false;
        } else {
            password.setError(null);
        }
        return true;
    }

    //Validator for login.
    public boolean loginValidator(String inpEmail, String inputPassword) {
        if (TextUtils.isEmpty(inpEmail)) {
            email.setError("Email is Required.");
            return false;
        } else {
            email.setError(null);
        }

        if (TextUtils.isEmpty(inputPassword)) {
            password.setError("Password is Required.");
            return false;
        } else {
            password.setError(null);
        }

        if (inputPassword.length() < 6) {
            password.setError("Password Must be >= 6 Characters");
            return false;
        } else {
            password.setError(null);
        }
        return true;
    }

}
