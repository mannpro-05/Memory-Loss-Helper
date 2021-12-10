package com.example.memorylossguardian.userpage.guardiandetails;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.memorylossguardian.R;
import com.example.memorylossguardian.database.table.GuardianTable;
import com.example.memorylossguardian.database.viewmodel.GuardianViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class GuardianDetails extends Fragment {
    TextView name;
    TextView email;
    TextView dateOfBirth;
    TextView address;
    GuardianViewModel viewModel;
    SwipeRefreshLayout refreshLayout;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    GuardianTable guardianTable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guardian_details, container, false);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        dateOfBirth = view.findViewById(R.id.dateOfBirth);
        address = view.findViewById(R.id.address);
        refreshLayout = view.findViewById(R.id.refreshGuardians);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Guardians/" + mAuth.getCurrentUser().getUid());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.delete();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        guardianTable = snapshot.getValue(GuardianTable.class);
                        viewModel.insert(guardianTable);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                refreshLayout.setRefreshing(false);

            }
        });

        viewModel = new ViewModelProvider(getActivity()).get(GuardianViewModel.class);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getGuardianData().observe(getActivity(), new Observer<List<GuardianTable>>() {
            @Override
            public void onChanged(List<GuardianTable> guardianTables) {
                if (guardianTables.size() > 0) {
                    name.setText(guardianTables.get(0).getName());
                    email.setText(guardianTables.get(0).getEmail());
                    dateOfBirth.setText(guardianTables.get(0).getDateOfBirth());
                    address.setText(guardianTables.get(0).getAddress());
                }
            }
        });


    }
}