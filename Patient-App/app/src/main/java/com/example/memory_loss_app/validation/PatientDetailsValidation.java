package com.example.memory_loss_app.validation;

import android.text.TextUtils;

import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * This class does the auth validation for the Patient Details package.
 * Note: In the validation methods i've set the error null because if there is an error and that
 * error is resolved it will still be there as it has not been removed to we have to set it to null.
 */
public class PatientDetailsValidation {

    TextInputLayout doctorName, doctorContactNumber;
    TextInputLayout diagnosedOn, lastAppointmentDate;
    TextInputLayout notificationTime;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    //Constructor for inti the class variables.
    public PatientDetailsValidation(TextInputLayout doctorName, TextInputLayout doctorContactNumber,
                                    TextInputLayout diagnosedOn, TextInputLayout lastAppointmentDate,
                                    TextInputLayout notificationTime) {
        this.doctorName = doctorName;
        this.doctorContactNumber = doctorContactNumber;
        this.diagnosedOn = diagnosedOn;
        this.lastAppointmentDate = lastAppointmentDate;
        this.notificationTime = notificationTime;

    }

    // Validator for patient details.
    public boolean validator() {
        if (TextUtils.isEmpty(doctorName.getEditText().getText().toString())) {
            doctorName.setError("Doctor's name is Required.");
            return false;
        } else {
            doctorName.setError(null);
        }
        if (TextUtils.isEmpty(doctorContactNumber.getEditText().getText().toString())) {
            doctorContactNumber.setError("Doctors Contact number is Required.");
            return false;
        } else {
            doctorContactNumber.setError(null);
        }

        if (doctorContactNumber.getEditText().getText().toString().length() != 10) {
            doctorContactNumber.setError("Enter a valid mobile number.");
            return false;
        } else {
            doctorContactNumber.setError(null);
        }

        if (TextUtils.isEmpty(diagnosedOn.getEditText().getText().toString())) {
            diagnosedOn.setError("This date is Required.");
            return false;
        } else {
            diagnosedOn.setError(null);
        }
        if (TextUtils.isEmpty(lastAppointmentDate.getEditText().getText().toString())) {
            lastAppointmentDate.setError("This date is Required.");
            return false;
        } else {
            lastAppointmentDate.setError(null);
        }

        if (TextUtils.isEmpty(notificationTime.getEditText().getText().toString())) {
            notificationTime.setError("Please specify the time on which you would like to receive notification.");
            return false;
        }
        else {
            notificationTime.setError(null);
        }

        try {
            if (sdf.parse(String.valueOf(lastAppointmentDate.getEditText().getText())).
                    before(sdf.parse(diagnosedOn.getEditText().getText().toString()))) {
                lastAppointmentDate.setError("Patient cannot have Last Appointment date before diagnosed.");
                return false;
            } else {
                lastAppointmentDate.setError(null);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }



        return true;
    }


}

