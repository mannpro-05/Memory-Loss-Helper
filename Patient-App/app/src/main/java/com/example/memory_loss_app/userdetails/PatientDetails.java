package com.example.memory_loss_app.userdetails;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.alertdialog.AlertDialogBuilder;
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.datetimepicker.DatePickerEditText;
import com.example.memory_loss_app.framelayoutmanager.ObjectHandler;
import com.example.memory_loss_app.validation.PatientDetailsValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class will enter the patient details.
 */
public class PatientDetails extends Fragment {
    View view;
    ObjectHandler handler;
    TextInputLayout doctorName, doctorContactNumber;
    TextInputLayout diagnosedOn, lastAppointmentDate;
    TextInputLayout timeOfNotification;
    DatePickerEditText datePickerEditText;
    Button next;
    FirebaseAuth mAuth;
    AlertDialogBuilder alertDialogBuilderPatientDetails;
    android.app.AlertDialog.Builder builder;
    PatientDetailsValidation validation;
    Users user;
    MaterialTimePicker timePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_patient_details, container, false);
        doctorName = view.findViewById(R.id.doctorName);
        doctorContactNumber = view.findViewById(R.id.doctorContactNumber);
        diagnosedOn = view.findViewById(R.id.diagnosedOn);
        lastAppointmentDate = view.findViewById(R.id.lastAppointmentDate);
        timeOfNotification = view.findViewById(R.id.notification_time);
        next = view.findViewById(R.id.next);
        mAuth = FirebaseAuth.getInstance();

        user = new Users();
        builder = new android.app.AlertDialog.Builder(getContext());
        datePickerEditText = new DatePickerEditText();
        alertDialogBuilderPatientDetails = new AlertDialogBuilder(builder);
        validation = new PatientDetailsValidation(doctorName, doctorContactNumber,
                diagnosedOn, lastAppointmentDate, timeOfNotification);

        diagnosedOn.getEditText().setOnClickListener(view -> datePickerEditText.datePicker(diagnosedOn, getActivity()));

        lastAppointmentDate.getEditText().setOnClickListener(view -> datePickerEditText.datePicker(lastAppointmentDate, getActivity()));

        timeOfNotification.getEditText().setRawInputType(InputType.TYPE_NULL);
        timeOfNotification.getEditText().setTextIsSelectable(false);
        timeOfNotification.getEditText().setOnClickListener(view1 -> {
            setTimePicker();
        });

        // Button to move to the next activity.
        next.setOnClickListener(view -> {
            System.out.println(TextUtils.isEmpty(timeOfNotification.getEditText().getText()));
            //validating the user input.
            if (validation.validator()) {
                user.setDoctorName(doctorName.getEditText().getText().toString());
                user.setDoctorContactNumber(doctorContactNumber.getEditText().getText().toString());
                user.setDiagnosedOn(diagnosedOn.getEditText().getText().toString());
                user.setLastAppointmentDate(lastAppointmentDate.getEditText().getText().toString());
                user.setNotificationTime(timeOfNotification.getEditText().getText().toString());
                handler.patientDetails(user);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    void setTimePicker(){
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(9)
                .setMinute(00)
                .build();
        timePicker.show(getChildFragmentManager(), "TAG");

        timePicker.addOnPositiveButtonClickListener(view1 -> {
            timeOfNotification.getEditText().setText(timePicker.getHour()+":"+timePicker.getMinute());
        });
    }

    //Setting the context to the reference of ObjectHandler Interface.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ObjectHandler) {
            handler = (ObjectHandler) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentBListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }

}