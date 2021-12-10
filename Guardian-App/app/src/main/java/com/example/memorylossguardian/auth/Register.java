package com.example.memorylossguardian.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorylossguardian.MainActivity;
import com.example.memorylossguardian.R;
import com.example.memorylossguardian.usersetup.UserSetup;
import com.example.memorylossguardian.validation.AuthValidation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email, password, confirmPassword;
    TextView tv;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    Button register;
    Guardians guardians;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        tv = findViewById(R.id.login);
        confirmPassword = findViewById(R.id.cpassword);
        mAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register);
        reference = FirebaseDatabase.getInstance().getReference().child("Guardians");

        progressDialog = new ProgressDialog(this);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            register.setOnClickListener(view -> {
                final String inputEmail = email.getText().toString().trim(),
                        inputPassword = password.getText().toString().trim(),
                        inputConfirmPassword = confirmPassword.getText().toString().trim();

                //Object of validation class.
                AuthValidation validation = new AuthValidation(email, password, confirmPassword);
                // Validating the user input form the Validation class in validation package.
                if (!validation.registerValidator(inputEmail, inputPassword, inputConfirmPassword)) {
                    return;
                }
                progressDialog.setMessage("Registering The User!!");
                progressDialog.show();

                // Registering in the user.
                mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                guardians = new Guardians();

                                // Setting the values in the user class to insert into the firebase.
                                guardians.setEmail(inputEmail);
                                guardians.setSetupComplete(false);
                                reference.child(mAuth.getCurrentUser().getUid()).setValue(guardians);
                                progressDialog.dismiss();

                                Toast.makeText(Register.this, "Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), UserSetup.class);
                                startActivity(intent);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            });
        }

        tv.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Login.class)));
    }
}
