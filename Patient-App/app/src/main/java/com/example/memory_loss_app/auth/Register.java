package com.example.memory_loss_app.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memory_loss_app.validation.AuthValidation;
import com.example.memory_loss_app.MainActivity;
import com.example.memory_loss_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class handles the registering related details for the application.
 */
public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email, password, confirmPassword;
    TextView tv;
    DatabaseReference reference;
    ProgressDialog pd;
    Button register;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Wiring the contents of the xml file.
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        tv = findViewById(R.id.login);
        confirmPassword = findViewById(R.id.cpassword);
        mAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        // Creating objects of the references.
        pd = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        //Checking if the user is authenticated or not.
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
                pd.setMessage("Registering The User!!");
                pd.show();

                // Registering in the user.
                mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                users = new Users();

                                // Setting the values in the user class to insert into the firebase.
                                users.setEmail(inputEmail);
                                users.setUserSetupComplete(false);
                                reference.child(mAuth.getCurrentUser().getUid()).setValue(users);
                                pd.dismiss();

                                Toast.makeText(Register.this, "Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                            } else {
                                pd.dismiss();
                                Toast.makeText(Register.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            });
        }

        tv.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Login.class)));
    }

}