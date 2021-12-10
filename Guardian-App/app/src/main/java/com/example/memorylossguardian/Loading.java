package com.example.memorylossguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.memorylossguardian.auth.Login;
import com.example.memorylossguardian.auth.SetupComplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Loading extends AppCompatActivity {
    FirebaseAuth mAuth;
    SetupComplete setupComplete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mAuth = FirebaseAuth.getInstance();
        System.out.println(mAuth.getCurrentUser());
        if (mAuth.getCurrentUser() != null){
            setupComplete = new SetupComplete(this);
            setupComplete.isSetupComplete(false);
        }
        else {
            startActivity(new Intent(this, Login.class));
        }
    }
}