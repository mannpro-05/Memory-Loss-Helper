package com.example.memory_loss_app.userpage;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.memory_loss_app.FingerPrintScanner;
import com.example.memory_loss_app.userpage.profile.editprofile.UpdatePrimaryContacts;
import com.example.memory_loss_app.userpage.profile.editprofile.UpdateSecondaryContact;
import com.example.memory_loss_app.userpage.profile.editprofile.UpdatePatientDetails;
import com.example.memory_loss_app.userpage.profile.editprofile.UpdatePersonalQuestion;
import com.example.memory_loss_app.userpage.profile.editprofile.UpdateUserDetails;
import com.example.memory_loss_app.adapter.DailyActivitiesAdapter;
import com.example.memory_loss_app.userpage.profile.editprofile.UpdateProfilePage;
import com.example.memory_loss_app.userpage.profile.editprofile.UpdateProfilePhoto;
import com.example.memory_loss_app.auth.Login;
import com.example.memory_loss_app.userpage.dailyactivity.EditDailyActivity;
import com.example.memory_loss_app.R;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;
import com.example.memory_loss_app.userpage.dailyactivity.DailyActivity;
import com.example.memory_loss_app.userpage.home.HomeFragment;
import com.example.memory_loss_app.userpage.primarycontact.PrimaryContactDisplay;
import com.example.memory_loss_app.userpage.profile.ProfileFragment;
import com.example.memory_loss_app.userpage.profile.ProfileHandler;
import com.example.memory_loss_app.userpage.secondarycontact.SecondaryContactDisplay;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;


public class UserPage extends AppCompatActivity implements UserPageHandler, ProfileHandler {
    BottomNavigationView bottomNav;
    FragmentTransaction transactionManager;
    Bitmap bitmap;
    FirebaseAuth mAuth;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Executor executor;
    List<String> backButtonAccess = new ArrayList<>(Arrays.asList("UPDATE_PROFILE_PHOTO",
            "UPDATE_PATIENT_DETAILS",
            "UPDATE_USER_DETAILS",
            "UPDATE_PERSONAL_QUESTION",
            "UPDATE_PROFILE"));
    private List<String> ignoreFingerprintScanner = new ArrayList<>(
            Arrays.asList(
                    "UPDATE_PRIMARY_CONTACT",
                    "UPDATE_SECONDARY_CONTACT",
                    "UPDATE_PROFILE_PHOTO"
                    ));
    boolean fromOnRestart;
    boolean fromProfile;
    int totalNumberOfTries = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromOnRestart = false;
        fromProfile = false;
        transactionManager = getSupportFragmentManager().beginTransaction();
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_user_page);
        bottomNav = findViewById(R.id.nav_view);


        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");
        bitmap = BitmapFactory.decodeFile(mypath.getAbsolutePath());
        replaceFragment(new HomeFragment(bitmap), "HOME");
        executor = ContextCompat.getMainExecutor(this);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    replaceFragment(new HomeFragment(bitmap), "HOME");
                    break;
                case R.id.navigation_dashboard:
                    replaceFragment(new DailyActivity(), "DAILY_ACTIVITY");
                    break;
                case R.id.profile:
                    if (totalNumberOfTries < 4) {
                        fromOnRestart = false;
                        fromProfile = true;
                        biometricPrompt.authenticate(promptInfo);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "You have maxed out the total number of tries please lock " +
                                        "and unlock the phone again", Toast.LENGTH_SHORT).show();
                        replaceFragment(new HomeFragment(bitmap), "HOME");
                    }

                    break;
                case R.id.primaryContact_navigation:
                    replaceFragment(new PrimaryContactDisplay(), "PRIMARY_DISPLAY");
                    break;
                case R.id.secondaryContact_navigation:
                    replaceFragment(new SecondaryContactDisplay(), "SECONDARY_DISPLAY");
                    break;

            }
            return true;
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Edit Page.")
                .setSubtitle("Please verify yourself before changing any of the value.")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt = new BiometricPrompt(UserPage.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                replaceFragment(new ProfileFragment(bitmap), "PROFILE");

            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                totalNumberOfTries = 0;
                if (fromProfile) {
                    replaceFragment(new ProfileFragment(bitmap), "PROFILE");
                } else if (fromOnRestart) {

                }

            }

            @Override
            public void onAuthenticationFailed() {
                if (!fromOnRestart) {
                    biometricPrompt.cancelAuthentication();
                    Toast.makeText(getApplicationContext(), "Error reading fingerprint.", Toast.LENGTH_SHORT).show();
                    bottomNav.setSelectedItemId(R.id.navigation_home);
                    replaceFragment(new HomeFragment(bitmap), "HOME");
                } else {

                }
                totalNumberOfTries++;

            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        if (! ignoreFingerprintScanner.contains(tag)) {
            startActivity(new Intent(getApplicationContext(), FingerPrintScanner.class));
        }

    }


    void replaceFragment(Fragment fragment, String tag) {
        transactionManager = getSupportFragmentManager().beginTransaction();
        transactionManager.replace(R.id.userPage, fragment, tag).addToBackStack(tag);
        transactionManager.commit();
        System.out.println(tag);
    }


    @Override
    public void homePage() {
        replaceFragment(new HomeFragment(bitmap), "HOME");
    }

    @Override
    public void profilePage() {
        replaceFragment(new ProfileFragment(bitmap), "PROFILE");
    }

    @Override
    public void dashboard() {
        replaceFragment(new DailyActivity(), "DAILY_ACTIVITY");
    }

    @Override
    public void onClickListener(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void dailyActivityEditor(String id, String title, String description, DailyActivitiesAdapter adapter) {
        replaceFragment(new EditDailyActivity(id, title, description, true, false, adapter), "DAILY_ACTIVITY_EDITOR");

    }

    @Override
    public void dailyActivityAdder(DailyActivitiesAdapter adapter) {
        replaceFragment(new EditDailyActivity(false, true, adapter), "DAILY_ACTIVITY_ADDER");

    }

    @Override
    public void editProfile() {
        replaceFragment(new UpdateProfilePage(bitmap), "UPDATE_PROFILE");
    }

    @Override
    public void editProfile(Bitmap bitmap) {
        this.bitmap = bitmap;

        replaceFragment(new UpdateProfilePage(bitmap), "UPDATE_PROFILE");
    }

    @Override
    public void updateProfilePhoto() {
        replaceFragment(new UpdateProfilePhoto(bitmap), "UPDATE_PROFILE_PHOTO");
    }

    @Override
    public void updatePatientDetails() {
        replaceFragment(new UpdatePatientDetails(), "UPDATE_PATIENT_DETAILS");
    }

    @Override
    public void updateUserDetails() {

        replaceFragment(new UpdateUserDetails(), "UPDATE_USER_DETAILS");
    }

    @Override
    public void updatePersonalQuestion() {
        replaceFragment(new UpdatePersonalQuestion(), "UPDATE_PERSONAL_QUESTION");
    }

    @Override
    public void logout() {

        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    @Override
    public void editPrimaryContacts() {
        replaceFragment(new UpdatePrimaryContacts(), "UPDATE_PRIMARY_CONTACT");
    }

    @Override
    public void editSecondaryContacts() {
        replaceFragment(new UpdateSecondaryContact(), "UPDATE_SECONDARY_CONTACT");
    }

    @Override
    public void onBackPressed() {
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        System.out.println(tag);
        if (backButtonAccess.contains(tag)) {
            super.onBackPressed();
        }
    }

}