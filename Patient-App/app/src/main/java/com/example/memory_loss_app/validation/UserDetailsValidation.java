package com.example.memory_loss_app.validation;

import android.text.TextUtils;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;

/**
 * This class does the auth validation for the Personal Question.
 * Note: In the validation methods i've set the error null because if there is an error and that
 * error is resolved it will still be there as it has not been removed to we have to set it to null.
 */
public class UserDetailsValidation {

    TextInputLayout name;
    TextInputLayout dateOfBirth, age;
    TextInputLayout address;
    AutoCompleteTextView bloodGroup;

    //Constructor for inti the class variables.
    public UserDetailsValidation(TextInputLayout name, TextInputLayout dateOfBirth,
                                 TextInputLayout age, TextInputLayout address) {
        this.address = address;
        this.age = age;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

    // Validator for user details.
    public boolean validator() {

        if (TextUtils.isEmpty(name.getEditText().getText().toString())) {
            name.setError("Name is Required.");
            return false;
        } else {
            name.setError(null);
        }
        if (TextUtils.isEmpty(dateOfBirth.getEditText().getText().toString())) {
            dateOfBirth.setError("Date Of Birth is Required.");
            return false;
        } else {
            dateOfBirth.setError(null);
        }
        if (Integer.parseInt(age.getEditText().getText().toString()) < 0) {
            dateOfBirth.setError("Please enter a valid Date Of Birth.");
            age.setError("Please enter a valid Date Of Birth.");
            return false;
        } else {
            dateOfBirth.setError(null);
            age.setError(null);
        }
        if (TextUtils.isEmpty(address.getEditText().getText().toString())) {
            address.setError("Address is Required.");
            return false;
        } else {
            address.setError(null);
        }
        return true;
    }
}
