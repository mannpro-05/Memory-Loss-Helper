package com.example.memory_loss_app.userpage.profile.editprofile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.example.memory_loss_app.datetimepicker.DatePickerEditText;
import com.example.memory_loss_app.userpage.profile.ProfileHandler;
import com.example.memory_loss_app.validation.UserDetailsValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class UpdateUserDetails extends Fragment {

    TextInputLayout name;
    TextInputLayout dateOfBirth, age;
    TextInputLayout address;
    AutoCompleteTextView bloodGroup;
    DatePickerEditText datePickerEditText;
    Button update;
    String selectedBloodType = null;
    UserDetailsValidation validation;
    Users user;
    ProfileHandler handler;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    String[] test;
    UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.name);
        dateOfBirth = view.findViewById(R.id.dateOfBirth);
        address = view.findViewById(R.id.address);
        bloodGroup = view.findViewById(R.id.bloodGroup);
        age = view.findViewById(R.id.age);
        update = view.findViewById(R.id.update);
        test = getActivity().getResources().getStringArray(R.array.bloodGroup);
        mAuth = FirebaseAuth.getInstance();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        reference = FirebaseDatabase.getInstance().getReference().child("Users/" + mAuth.getCurrentUser().getUid());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.bloodGroup,
                R.layout.blood_group_dropdown);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroup.setAdapter(adapter);

        bloodGroup.setOnItemClickListener((adapterView, view1, pos, l) -> {
            selectedBloodType = adapterView.getItemAtPosition(pos).toString();
        });


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                name.getEditText().setText(snapshot.child("name").getValue().toString());
                dateOfBirth.getEditText().setText(snapshot.child("dateOfBirth").getValue().toString());
                age.getEditText().setText(snapshot.child("age").getValue().toString());
                address.getEditText().setText(snapshot.child("address").getValue().toString());
                bloodGroup.setText(bloodGroup.getAdapter()
                        .getItem(Arrays.asList(test)
                                .indexOf(snapshot
                                        .child("bloodGroup")
                                        .getValue()
                                        .toString())).toString(), false);
                selectedBloodType = snapshot.child("bloodGroup")
                        .getValue()
                        .toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        datePickerEditText = new DatePickerEditText();
        validation = new UserDetailsValidation(name, dateOfBirth, age, address);


        //Specifying blood_group_dropdown as the dropdown style for the dropdown activity.


        // Getting the value and position of the selected bloodgroup.


        dateOfBirth.getEditText().setOnClickListener(view1 -> datePickerEditText.datePicker(dateOfBirth, age, getActivity()));

        // Button to move to the next activity.
        update.setOnClickListener(view1 -> {

            // Validating the user input.
            if (validation.validator() && selectedBloodType != null) {
                user.setName(name.getEditText().getText().toString());
                user.setDateOfBirth(dateOfBirth.getEditText().getText().toString());
                user.setAge(Integer.parseInt(age.getEditText().getText().toString()));
                user.setBloodGroup(selectedBloodType);
                user.setAddress(address.getEditText().getText().toString());
                reference.setValue(user);

                UserDetailsDB userDetailsDB = new UserDetailsDB(mAuth.getCurrentUser().getUid(),
                        mAuth.getCurrentUser().getEmail(), true, user.getDoctorName(), user.getDiagnosedOn(),
                        user.getLastAppointmentDate(), user.getDoctorContactNumber(), String.valueOf(user.getNotificationTime()),
                        user.getName(), user.getDateOfBirth(), user.getBloodGroup(), user.getAddress(),
                        user.getAge(), user.getPersonalQuestion(), user.getAnswer(), user.getLastMemoryLossTrauma());

                userViewModel.update(userDetailsDB);

                handler.editProfile();

            } else if (selectedBloodType == null)
                bloodGroup.setError("Blood Group cannot be empty.");
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileHandler)
            handler = (ProfileHandler) context;
    }
}