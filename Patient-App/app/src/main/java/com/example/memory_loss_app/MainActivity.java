package com.example.memory_loss_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.memory_loss_app.alertdialog.AlertDialogBuilder;
import com.example.memory_loss_app.auth.Login;
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.contacts.AddPrimaryContact;
import com.example.memory_loss_app.contacts.AddSecondaryContacts;
import com.example.memory_loss_app.contacts.ContactDetails;
import com.example.memory_loss_app.framelayoutmanager.ObjectHandler;
import com.example.memory_loss_app.userdetails.ChooseProfilePhoto;
import com.example.memory_loss_app.userdetails.PatientDetails;
import com.example.memory_loss_app.userdetails.PersonalQuestion;
import com.example.memory_loss_app.userdetails.UserDetails;
import com.example.memory_loss_app.userpage.UserPage;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ObjectHandler {
    private FirebaseAuth mAuth;
    TextView name;
    android.app.AlertDialog.Builder builder;
    FragmentTransaction fragmentTransaction;
    AlertDialogBuilder alertDialogBuilder;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        name = findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        builder = new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder = new AlertDialogBuilder(builder);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,new PatientDetails()).commit();

    }

    void replaceFragment(Fragment fragment){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.logout:
                builder.setMessage("Are you sure that you want to logout?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("logout.");
                alertDialog.show();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void patientDetails(Users user) {
        UserDetails userDetails = new UserDetails();
        userDetails.updateUserObjectInfo(user);
        replaceFragment(userDetails);
    }

    @Override
    public void userDetails(Users user) {
        PersonalQuestion personalQuestion = new PersonalQuestion();
        personalQuestion.updateUserObjectInfo(user);
        replaceFragment(personalQuestion);
    }

    @Override
    public void personalQuestions(Users user) {
        AddPrimaryContact primaryContact = new AddPrimaryContact();
        primaryContact.updateUserObjectInfo(user);
        replaceFragment(primaryContact);
    }

    @Override
    public void addPrimaryContacts(Users user, ArrayList<ContactDetails> primaryContactDetails) {
        AddSecondaryContacts secondaryContacts = new AddSecondaryContacts();
        secondaryContacts.updateUserObjectInfo(user, primaryContactDetails);
        replaceFragment(secondaryContacts);

    }

    @Override
    public void addSecondaryContacts(Users user, ArrayList<ContactDetails> primaryContactDetails, Map secondaryContactDetails) {
        ChooseProfilePhoto chooseProfilePhoto = new ChooseProfilePhoto();
        chooseProfilePhoto.updateUserObjectInfo(user, primaryContactDetails, secondaryContactDetails);
        replaceFragment(chooseProfilePhoto);
    }

    @Override
    public void choosePhoto() {
        startActivity(new Intent(getApplicationContext(), UserPage.class));

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() == 0) {
            android.app.AlertDialog alert = alertDialogBuilder.createOnBackPressedBuilder("Are you sure that you want to exit ?","Want to exit application?", MainActivity.this);
            alert.show();


        } else {
            fragmentManager.popBackStack();
        }
    }

}