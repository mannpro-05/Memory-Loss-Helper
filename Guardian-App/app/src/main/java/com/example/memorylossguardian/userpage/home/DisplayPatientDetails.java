package com.example.memorylossguardian.userpage.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.memorylossguardian.R;
import com.example.memorylossguardian.database.table.GuardianPatients;

public class DisplayPatientDetails extends Fragment {

    private GuardianPatients patient;
    private TextView name;
    private TextView email;
    private TextView emergencyContact;
    private TextView dateOfBirth;
    private TextView lastMemoryLossTrauma;
    public DisplayPatientDetails(GuardianPatients patient) {
        this.patient = patient;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_patient_details, container, false);

        name = view.findViewById(R.id.patientName);
        email = view.findViewById(R.id.patientEmail);
        emergencyContact = view.findViewById(R.id.patientEmergencyContact);
        dateOfBirth = view.findViewById(R.id.patientDob);
        lastMemoryLossTrauma = view.findViewById(R.id.patientLastTrauma);

        name.setText(patient.getName());
        email.setText(patient.getEmailId());
        emergencyContact.setText(patient.getEmergencyContactNumber());
        dateOfBirth.setText(patient.getDateOfBirth());
        lastMemoryLossTrauma.setText(patient.getLastMemoryLossTrauma());

        return view;
    }
}