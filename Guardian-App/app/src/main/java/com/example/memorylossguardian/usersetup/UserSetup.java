package com.example.memorylossguardian.usersetup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.memorylossguardian.MainActivity;
import com.example.memorylossguardian.R;
import com.example.memorylossguardian.auth.Guardians;
import com.example.memorylossguardian.database.table.GuardianTable;
import com.example.memorylossguardian.database.viewmodel.GuardianViewModel;
import com.example.memorylossguardian.datetimepicker.DatePickerEditText;
import com.example.memorylossguardian.validation.UserDetailsValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSetup extends AppCompatActivity {
    TextInputLayout name;
    TextInputLayout dateOfBirth;
    TextInputLayout address;
    Button finish;
    UserDetailsValidation validation;
    Guardians guardians;
    FirebaseAuth mAuth;
    private DatabaseReference reference;
    DatePickerEditText datePickerEditText;
    GuardianTable guardianTable;
    GuardianViewModel guardianViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);
        name = findViewById(R.id.name);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        address = findViewById(R.id.address);
        finish = findViewById(R.id.finish);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Guardians/" + mAuth.getCurrentUser().getUid());
        guardianViewModel = new ViewModelProvider(this).get(GuardianViewModel.class);
        datePickerEditText = new DatePickerEditText();

        dateOfBirth.getEditText().setOnClickListener(view -> {
            datePickerEditText.datePicker(dateOfBirth, this);
        });

        finish.setOnClickListener(view -> {

            validation = new UserDetailsValidation(name, dateOfBirth, address);

            if (validation.validator()) {
                guardians = new Guardians();
                guardians.setEmail(mAuth.getCurrentUser().getEmail());
                guardians.setSetupComplete(true);
                guardians.setAddress(address.getEditText().getText().toString());
                guardians.setName(name.getEditText().getText().toString());
                guardians.setDateOfBirth(dateOfBirth.getEditText().getText().toString());

                guardianTable = new GuardianTable(guardians.getName(), guardians.getEmail(),
                        guardians.getDateOfBirth(), guardians.getAddress());
                reference.setValue(guardians);

                guardianViewModel.insert(guardianTable);
                System.out.println(guardianTable.getName()+" Done4");

                startActivity(new Intent(this, MainActivity.class));

            }

        });
    }
}