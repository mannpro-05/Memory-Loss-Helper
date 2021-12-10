package com.example.memory_loss_app.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memory_loss_app.alertdialog.AlertDialogBuilder;
import com.example.memory_loss_app.R;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.example.memory_loss_app.validation.AuthValidation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * This class handles the login related details for the application.
 */
public class Login extends AppCompatActivity {
    EditText email, password;
    TextView register;
    TextView forgotPassword;
    Button login;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    DatabaseReference reference;
    SetupFinished setupFinished;
    AlertDialog.Builder builder;
    AlertDialogBuilder alertDialogBuilderLogin;
    Intent verifyViaPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Wiring the contents of the xml file.
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.registerLink);
        login = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgotPassword);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        verifyViaPassword = getIntent();

        if (verifyViaPassword.hasExtra("email")) {
            email.setText(verifyViaPassword.getStringExtra("email"));
        }

        // Creating objects of the references.
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        setupFinished = new SetupFinished();
        builder = new AlertDialog.Builder(Login.this);
        alertDialogBuilderLogin = new AlertDialogBuilder(builder);
        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ResetPassword.class));
        });

        //Checking if the user is authenticated or not.
        if (mAuth.getCurrentUser() != null) {
            setupFinished.isSetupUserComplete(mAuth, reference, Login.this, false);
        } else {
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
                        setupFinished.isSetupUserComplete(mAuth, reference, Login.this, true);
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

    // Asking user to exit or continue the app when the back button is pressed.
    @Override
    public void onBackPressed() {
        AlertDialog alert = alertDialogBuilderLogin.createOnBackPressedBuilder("Are you sure that you want to exit ?", "Want to exit application?", Login.this);
        alert.show();
    }


}