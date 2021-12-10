package com.example.memory_loss_app.userdetails;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.datetimepicker.DatePickerEditText;
import com.example.memory_loss_app.framelayoutmanager.ObjectHandler;
import com.example.memory_loss_app.validation.UserDetailsValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

/**
 * This class will enter the User details.
 */
public class UserDetails extends Fragment {
    TextInputLayout name;
    ObjectHandler handler;
    TextInputLayout dateOfBirth, age;
    TextInputLayout address;
    AutoCompleteTextView bloodGroup;
    DatePickerEditText datePickerEditText;
    Button next;
    String selectedBloodType = null;
    UserDetailsValidation validation;
    Users user;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_details, container, false);
        name = view.findViewById(R.id.name);
        dateOfBirth = view.findViewById(R.id.dateOfBirth);
        address = view.findViewById(R.id.address);
        bloodGroup = view.findViewById(R.id.bloodGroup);
        age = view.findViewById(R.id.age);
        next = view.findViewById(R.id.next);
        datePickerEditText = new DatePickerEditText();
        validation = new UserDetailsValidation(name, dateOfBirth, age, address);

        //Specifying blood_group_dropdown as the dropdown style for the dropdown activity.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.bloodGroup,
                R.layout.blood_group_dropdown);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroup.setAdapter(adapter);

        // Getting the value and position of the selected bloodgroup.
        bloodGroup.setOnItemClickListener((adapterView, view, pos, l) -> {
            selectedBloodType = adapterView.getItemAtPosition(pos).toString();
            Log.d("hello", String.valueOf(pos));
        });

        dateOfBirth.getEditText().setOnClickListener(view -> datePickerEditText.datePicker(dateOfBirth, age, getActivity()));

        // Button to move to the next activity.
        next.setOnClickListener(view -> {

            // Validating the user input.
            if (validation.validator() && selectedBloodType != null) {
                user.setName(name.getEditText().getText().toString());
                user.setDateOfBirth(dateOfBirth.getEditText().getText().toString());
                user.setAge(Integer.parseInt(age.getEditText().getText().toString()));
                user.setBloodGroup(selectedBloodType);
                user.setAddress(address.getEditText().getText().toString());
                handler.userDetails(user);
            } else if (selectedBloodType == null)
                bloodGroup.setError("Blood Group cannot be empty.");
        });

        return view;
    }

    // Updating the user object once this activity is created.
    // This is done to insert the data of the previous activity to this so that the all the data
    // could be stored into the fire base when the user setup is complete.
    public void updateUserObjectInfo(Users user) {
        this.user = user;
    }



    //Setting the context to the reference of ObjectHandler Interface.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ObjectHandler) {
            handler = (ObjectHandler) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ObjectHandler");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }
}