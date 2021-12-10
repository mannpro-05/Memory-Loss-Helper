package com.example.memorylossguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.memorylossguardian.database.table.GuardianPatients;
import com.example.memorylossguardian.database.viewmodel.GuardianViewModel;
import com.example.memorylossguardian.userpage.UserPageHandler;
import com.example.memorylossguardian.userpage.guardiandetails.GuardianDetails;
import com.example.memorylossguardian.userpage.home.AddPatient;
import com.example.memorylossguardian.userpage.home.DisplayPatientDetails;
import com.example.memorylossguardian.userpage.home.HomeFragment;
import com.example.memorylossguardian.userpage.profile.ProfileFragment;
import com.example.memorylossguardian.userpage.profile.edit.EditGuardian;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements UserPageHandler {
    GuardianViewModel guardianViewModel;
    FirebaseAuth mAuth;
    TextView name;
    Button button;
    FragmentTransaction transactionManager;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        name = findViewById(R.id.name);
//        button = findViewById(R.id.logout);
//        guardianViewModel = new ViewModelProvider(this).get(GuardianViewModel.class);
//        mAuth = FirebaseAuth.getInstance();
//        guardianViewModel.getGuardianData().observe(this, new Observer<List<GuardianTable>>() {
//            @Override
//            public void onChanged(List<GuardianTable> guardianTables) {
//                if (guardianTables.size() > 0)
//                    name.setText(guardianTables.get(0).getName());
//            }
//        });
//
//        button.setOnClickListener(view -> {
//            mAuth.signOut();
//            guardianViewModel.delete();
//            startActivity(new Intent(this, Login.class));
//            finish();
//        });
        bottomNav = findViewById(R.id.nav_view);
        transactionManager = getSupportFragmentManager().beginTransaction();
        mAuth = FirebaseAuth.getInstance();
        transactionManager.replace(R.id.userPage, new HomeFragment()).addToBackStack(null);
        transactionManager.commit();

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment setFragment = null;
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        setFragment = new HomeFragment();
                        break;
                    case R.id.navigation_profile:
                        setFragment = new ProfileFragment();
                        break;
                    case R.id.navigation_guardian_details:
                        setFragment = new GuardianDetails();
                }
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.userPage, setFragment).addToBackStack(null)
                        .commit();
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(new Intent(getApplicationContext(), FingerPrintScanner.class));
    }

    void replaceFragment(Fragment fragment){
        transactionManager = getSupportFragmentManager().beginTransaction();
        transactionManager.replace(R.id.userPage, fragment).addToBackStack(null);
        transactionManager.commit();
    }

    @Override
    public void onClickListener(GuardianPatients patient) {
        replaceFragment(new DisplayPatientDetails(patient));
    }

    @Override
    public void startAddPatientFragment() {
        replaceFragment(new AddPatient());
    }

    @Override
    public void startHomeFragment() {
        replaceFragment(new HomeFragment());
    }

    @Override
    public void startEditPageFragment() {
        replaceFragment(new EditGuardian());
    }

    @Override
    public void startProfileFragment() {
        replaceFragment(new ProfileFragment());
    }
}

