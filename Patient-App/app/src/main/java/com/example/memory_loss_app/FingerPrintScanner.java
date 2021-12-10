package com.example.memory_loss_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memory_loss_app.auth.Login;
import com.example.memory_loss_app.auth.Logout;
import com.example.memory_loss_app.database.viewmodel.DailyActivitiesViewModel;
import com.example.memory_loss_app.database.viewmodel.PrimaryContactViewModel;
import com.example.memory_loss_app.database.viewmodel.SecondaryContactsViewModel;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.userpage.UserPage;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

public class FingerPrintScanner extends AppCompatActivity {
    Button scan;
    Button password;
    FirebaseAuth mAuth;
    String emailId;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Executor executor;
    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print_scanner);
        scan = findViewById(R.id.scanFinger);
        password = findViewById(R.id.enterPassword);
        errorMessage = findViewById(R.id.errorMessage);
        mAuth = FirebaseAuth.getInstance();
        emailId = mAuth.getCurrentUser().getEmail();
        executor = ContextCompat.getMainExecutor(FingerPrintScanner.this);
        biometricPrompt = new BiometricPrompt(FingerPrintScanner.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                System.out.println(errorCode);
                switch (errorCode){
                    case 11:
                        scan.setEnabled(false);
                        errorMessage.setText("You don't have a fingerprint registered. Login via password");
                    case 12:
                        scan.setEnabled(false);
                        errorMessage.setText(errString);
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(getApplicationContext(), UserPage.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
        Thread logout = new Thread(new Logout(this));

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Verify yourself.")
                .setSubtitle("Please verify yourself once again. \n If not with fingerprint then with password.")
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(promptInfo);

        password.setOnClickListener(view -> {
            logout.start();
            try {
                logout.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAuth.signOut();
            Intent loginPage = new Intent(getApplicationContext(), Login.class);
            loginPage.putExtra("email", emailId);
            startActivity(loginPage);
        });

        scan.setOnClickListener(view -> {
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Verify yourself.")
                    .setSubtitle("Please verify yourself once again. \n If not with fingerprint then with password.")
                    .setNegativeButtonText("Cancel")
                    .build();
            biometricPrompt.authenticate(promptInfo);
        });


    }

}