package com.example.memorylossguardian.auth;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.memorylossguardian.database.viewmodel.GuardianPatientsViewModel;
import com.example.memorylossguardian.database.viewmodel.GuardianViewModel;

public class Logout implements Runnable {

    private GuardianPatientsViewModel guardianPatientsViewModel;
    private GuardianViewModel guardianViewModel;

    public Logout(Context context) {
        guardianViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(GuardianViewModel.class);
        guardianPatientsViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(GuardianPatientsViewModel.class);

    }

    @Override
    public void run() {
        guardianViewModel.delete();
        guardianPatientsViewModel.deleteAll();
        System.out.println("done deleting");
    }
}
