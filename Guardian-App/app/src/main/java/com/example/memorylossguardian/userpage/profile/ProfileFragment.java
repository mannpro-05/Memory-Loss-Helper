package com.example.memorylossguardian.userpage.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.memorylossguardian.R;
import com.example.memorylossguardian.auth.Login;
import com.example.memorylossguardian.auth.Logout;
import com.example.memorylossguardian.database.table.GuardianTable;
import com.example.memorylossguardian.database.viewmodel.GuardianPatientsViewModel;
import com.example.memorylossguardian.database.viewmodel.GuardianViewModel;
import com.example.memorylossguardian.userpage.UserPageHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ProfileFragment extends Fragment {

    LinearLayout logout;
    LinearLayout editProfile;
    TextView guardianName;
    FirebaseAuth mAuth;
    GuardianPatientsViewModel guardianPatientsViewModel;
    GuardianViewModel guardianViewModel;
    UserPageHandler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        logout = view.findViewById(R.id.logout);
        guardianName = view.findViewById(R.id.name);
        editProfile = view.findViewById(R.id.editProfile);
        guardianViewModel = new ViewModelProvider(getActivity()).get(GuardianViewModel.class);
        guardianPatientsViewModel = new ViewModelProvider(getActivity()).get(GuardianPatientsViewModel.class);

        guardianViewModel.getGuardianData().observe(getActivity(), new Observer<List<GuardianTable>>() {
            @Override
            public void onChanged(List<GuardianTable> guardianTables) {
                if (guardianTables.size() > 0){
                    guardianName.setText(guardianTables.get(0).getName());
                }
            }
        });

        editProfile.setOnClickListener(view1 -> {
            handler.startEditPageFragment();
        });


        logout.setOnClickListener(view1 -> {
            Thread logoutThread = new Thread(new Logout(getActivity()));
            logoutThread.start();
            try {
                logoutThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAuth.signOut();
            startActivity(new Intent(getActivity(), Login.class));
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserPageHandler)
            handler = (UserPageHandler) context;
    }
}