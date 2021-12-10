package com.example.memorylossguardian.userpage.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.memorylossguardian.userpage.patientdetails.ContactDetails;
import com.example.memorylossguardian.R;
import com.example.memorylossguardian.userpage.patientdetails.Users;
import com.example.memorylossguardian.database.table.GuardianPatients;
import com.example.memorylossguardian.database.viewmodel.GuardianPatientsViewModel;
import com.example.memorylossguardian.userpage.UserPageHandler;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddPatient extends Fragment {
    TextInputLayout receivedUniqueId;
    Button add;
    DatabaseReference userReference;
    DatabaseReference primaryContactReference;
    DatabaseReference guardianPatientRecord;
    FirebaseAuth mAuth;
    Users users;
    String primaryContact;
    ContactDetails contactDetails;
    GuardianPatients patient;
    GuardianPatientsViewModel viewModel;
    UserPageHandler handler;
    String refactoredUniqueId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_patient, container, false);
        receivedUniqueId = view.findViewById(R.id.unique_id);
        add = view.findViewById(R.id.add_patient);
        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users/");
        primaryContactReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        guardianPatientRecord = FirebaseDatabase.getInstance().getReference()
                .child("GuardianPatient/" + mAuth.getCurrentUser().getUid());
        viewModel = new ViewModelProvider(getActivity()).get(GuardianPatientsViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        add.setOnClickListener(view1 -> {
            refactoredUniqueId = receivedUniqueId.getEditText().getText().toString().replaceAll("\\s+","");
            Thread primaryContactThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    primaryContactReference.child( refactoredUniqueId+ "/Primary")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() == null) {
                                        Toast.makeText(getActivity(), "The entered key is incorrect!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    contactDetails = snapshot.getValue(ContactDetails.class);
                                    primaryContact = contactDetails.getContactNumber();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            };
            Thread usersThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    userReference.child(refactoredUniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() == null) {
                                return;
                            }
                            users = snapshot.getValue(Users.class);
                            patient = new GuardianPatients(
                                    refactoredUniqueId,
                                    users.getName(),
                                    users.getDateOfBirth(),
                                    users.getEmail(),
                                    primaryContact,
                                    users.getLastMemoryLossTrauma());

                            if (viewModel.insert(patient)) {
                                guardianPatientRecord.child(refactoredUniqueId).setValue(patient);
                            } else {
                                Toast.makeText(getActivity(), "This user already exist.", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            };

            primaryContactThread.start();
            try {
                primaryContactThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            usersThread.start();
            try {
                usersThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.startHomeFragment();
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserPageHandler)
            handler = (UserPageHandler) context;
    }
}