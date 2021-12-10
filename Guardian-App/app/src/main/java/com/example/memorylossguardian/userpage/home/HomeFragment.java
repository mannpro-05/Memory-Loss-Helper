package com.example.memorylossguardian.userpage.home;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.memorylossguardian.R;
import com.example.memorylossguardian.adapter.PatentListAdapter;
import com.example.memorylossguardian.database.table.GuardianPatients;
import com.example.memorylossguardian.database.viewmodel.GuardianPatientsViewModel;
import com.example.memorylossguardian.userpage.UserPageHandler;
import com.example.memorylossguardian.userpage.patientdetails.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    PatentListAdapter adapter;
    UserPageHandler handler;
    GuardianPatientsViewModel viewModel;
    FloatingActionButton addPatient;
    SwipeRefreshLayout refreshLayout;
    DatabaseReference reference;
    DatabaseReference userReference;
    DatabaseReference primaryContactReference;
    FirebaseAuth mAuth;
    GuardianPatients patients;
    Users users;
    String primaryContact;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setRecyclerView(view);
        viewModel = new ViewModelProvider(getActivity()).get(GuardianPatientsViewModel.class);
        addPatient = view.findViewById(R.id.add_patient);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("GuardianPatient/" + mAuth.getCurrentUser().getUid());
        userReference = FirebaseDatabase.getInstance().getReference().child("Users/");
        refreshLayout = view.findViewById(R.id.refreshPatients);
        primaryContactReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        addPatient.setOnClickListener(view1 -> {
            handler.startAddPatientFragment();
        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getPatients().observe(getActivity(), new Observer<List<GuardianPatients>>() {
            @Override
            public void onChanged(List<GuardianPatients> guardianPatients) {
                adapter.setPatientsList(guardianPatients);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.deleteAll();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            viewModel.insert(patients);
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
                refreshLayout.setRefreshing(false);
            }

        });

    }

    private void setRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.patientList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PatentListAdapter(handler);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                GuardianPatients patient = adapter.getPatientAt(viewHolder.getAdapterPosition());
                reference.child(patient.getUniqueId()).removeValue();
                viewModel.delete(patient);
            }
        }).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserPageHandler) {
            handler = (UserPageHandler) context;
        }
    }
}