package com.example.memory_loss_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;


import com.example.memory_loss_app.auth.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.example.memory_loss_app.auth.SetupFinished;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * This is the loading page activity of the application.
 */
public class LoadingPage extends AppCompatActivity {
    FirebaseAuth mAuth;
    SetupFinished setupFinished;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        setupFinished = new SetupFinished();
        if (mAuth.getCurrentUser() != null) {
            setupFinished.isSetupUserComplete(mAuth, reference, LoadingPage.this, false);
        } else {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }


}