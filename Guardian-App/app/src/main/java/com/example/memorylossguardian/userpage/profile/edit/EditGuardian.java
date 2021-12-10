package com.example.memorylossguardian.userpage.profile.edit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.memorylossguardian.R;
import com.example.memorylossguardian.auth.Guardians;
import com.example.memorylossguardian.database.table.GuardianTable;
import com.example.memorylossguardian.database.viewmodel.GuardianViewModel;
import com.example.memorylossguardian.datetimepicker.DatePickerEditText;
import com.example.memorylossguardian.userpage.UserPageHandler;
import com.example.memorylossguardian.validation.UserDetailsValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class EditGuardian extends Fragment {

    TextInputLayout name;
    TextInputLayout dateOfBirth;
    TextInputLayout address;
    Button update;
    UserDetailsValidation validation;
    Guardians guardians;
    FirebaseAuth mAuth;
    private DatabaseReference reference;
    DatePickerEditText datePickerEditText;
    GuardianTable guardianTable;
    GuardianViewModel guardianViewModel;
    UserPageHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_guardian, container, false);
        name = view.findViewById(R.id.name);
        dateOfBirth = view.findViewById(R.id.dateOfBirth);
        address = view.findViewById(R.id.address);
        update = view.findViewById(R.id.finish);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Guardians/" + mAuth.getCurrentUser().getUid());
        guardianViewModel = new ViewModelProvider(this).get(GuardianViewModel.class);
        datePickerEditText = new DatePickerEditText();

        guardianViewModel.getGuardianData().observe(getActivity(), new Observer<List<GuardianTable>>() {
            @Override
            public void onChanged(List<GuardianTable> guardianTables) {
                if (guardianTables.size() > 0) {
                    guardianTable = guardianTables.get(0);
                    name.getEditText().setText(guardianTable.getName());
                    address.getEditText().setText(guardianTable.getAddress());
                    dateOfBirth.getEditText().setText(guardianTable.getDateOfBirth());
                }
            }
        });

        dateOfBirth.getEditText().setOnClickListener(view1 -> {
            datePickerEditText.datePicker(dateOfBirth, getActivity());
        });

        update.setOnClickListener(view1 -> {
            validation = new UserDetailsValidation(name, dateOfBirth, address);

            if (validation.validator()) {
                guardians = new Guardians();
                guardians.setEmail(mAuth.getCurrentUser().getEmail());
                guardians.setSetupComplete(true);
                guardians.setAddress(address.getEditText().getText().toString());
                guardians.setName(name.getEditText().getText().toString());
                guardians.setDateOfBirth(dateOfBirth.getEditText().getText().toString());

                guardianTable.setName(name.getEditText().getText().toString());
                guardianTable.setAddress(address.getEditText().getText().toString());
                guardianTable.setEmail(mAuth.getCurrentUser().getEmail());
                guardianTable.setDateOfBirth(dateOfBirth.getEditText().getText().toString());

                reference.setValue(guardians);
                guardianViewModel.update(guardianTable);

                handler.startProfileFragment();

            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserPageHandler)
            handler = (UserPageHandler) context;
    }
}