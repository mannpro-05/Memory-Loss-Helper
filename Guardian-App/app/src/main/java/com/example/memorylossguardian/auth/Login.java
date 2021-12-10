package com.example.memorylossguardian.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memorylossguardian.R;
import com.example.memorylossguardian.validation.AuthValidation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    EditText email, password;
    TextView register;
    TextView forgotPassword;
    Button login;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    DatabaseReference reference;
    Intent verifyViaPassword;
    SetupComplete setupComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.registerLink);
        login = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgotPassword);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        verifyViaPassword = getIntent();
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ResetPassword.class));
        });

        if (verifyViaPassword.hasExtra("email")) {
            email.setText(verifyViaPassword.getStringExtra("email"));
        }

        if (mAuth.getCurrentUser() == null) {

            login.setOnClickListener(view -> {
                final String inputEmail = email.getText().toString().trim();
                final String inputPassword = password.getText().toString().trim();
                AuthValidation loginValidation = new AuthValidation(email, password);

                // Validating the user input form the Validation class in validation package.
                if (!loginValidation.loginValidator(inputEmail, inputPassword))
                    return;
                progressDialog.setMessage("Logging In");

                progressDialog.show();

                // Signing in the user.
                mAuth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        setupComplete = new SetupComplete(this);
                        setupComplete.isSetupComplete(true);
                        Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            //Navigating to register page.
            register.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Register.class)));
        }
    }
}