package com.example.memorylossguardian.auth;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.memorylossguardian.FingerPrintScanner;
import com.example.memorylossguardian.MainActivity;
import com.example.memorylossguardian.database.table.GuardianPatients;
import com.example.memorylossguardian.database.table.GuardianTable;
import com.example.memorylossguardian.database.viewmodel.GuardianPatientsViewModel;
import com.example.memorylossguardian.database.viewmodel.GuardianViewModel;
import com.example.memorylossguardian.userpage.patientdetails.Users;
import com.example.memorylossguardian.usersetup.UserSetup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetupComplete {

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference guardianInfo;
    private DatabaseReference guardianPatientInfo;
    private DatabaseReference userReference;
    private DatabaseReference primaryContactReference;
    private Guardians guardians;
    private GuardianViewModel guardianViewModel;
    private GuardianTable guardianTable;
    private GuardianPatientsViewModel guardianPatientsViewModel;
    private GuardianPatients patients;
    private Users users;
    String primaryContact;

    public SetupComplete(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        guardianInfo = FirebaseDatabase.getInstance().getReference()
                .child("Guardians/" + mAuth.getCurrentUser().getUid());
        guardianPatientInfo = FirebaseDatabase.getInstance().getReference()
                .child("GuardianPatient/" + mAuth.getCurrentUser().getUid());
        primaryContactReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userReference = FirebaseDatabase.getInstance().getReference().child("Users/");
    }

    public void isSetupComplete(boolean fromLogin) {
        guardianInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("setupComplete").getValue() == null) {
                    mAuth.signOut();
                    Toast.makeText(context, "This email is not registered with this application!", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, Login.class));
                } else if ((boolean) snapshot.child("setupComplete").getValue()) {
                    if (fromLogin) {
                        Thread getData = new Thread(new GetDataClass());
                        getData.start();
                        try {
                            getData.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        context.startActivity(new Intent(context, MainActivity.class));
                    } else {
                        context.startActivity(new Intent(context, FingerPrintScanner.class));
                    }

                } else {
                    context.startActivity(new Intent(context, UserSetup.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class GetDataClass implements Runnable {

        @Override
        public void run() {
            guardianViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(GuardianViewModel.class);
            guardianPatientsViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(GuardianPatientsViewModel.class);
            guardianInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    guardians = snapshot.getValue(Guardians.class);
                    guardianTable = new GuardianTable(guardians.getName(), guardians.getEmail(),
                            guardians.getDateOfBirth(), guardians.getAddress());
                    guardianViewModel.insert(guardianTable);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            guardianPatientInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot i : snapshot.getChildren()) {

                        Thread primaryContactThread = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                primaryContactReference.child(i.getKey() + "/Primary").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        primaryContact = snapshot.child("contactNumber").getValue().toString();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        };
                        Thread userReferenceThread = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                userReference.child(i.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        users = snapshot.getValue(Users.class);
                                        patients = new GuardianPatients();
                                        patients.setUniqueId(i.getKey());
                                        patients.setName(users.getName());
                                        patients.setEmailId(users.getEmail());
                                        patients.setDateOfBirth(users.getDateOfBirth());
                                        patients.setLastMemoryLossTrauma(users.getLastMemoryLossTrauma());
                                        patients.setEmergencyContactNumber(primaryContact);
                                        primaryContact = "";
                                        guardianPatientsViewModel.insert(patients);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        };

                        primaryContactThread.start();
                        try {
                            primaryContactThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        userReferenceThread.start();
                        try {
                            userReferenceThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
