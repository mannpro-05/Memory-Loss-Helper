package com.example.memorylossguardian.userpage;

import com.example.memorylossguardian.database.table.GuardianPatients;

public interface UserPageHandler {
    public void onClickListener(GuardianPatients patient);
    public void startAddPatientFragment();
    public void startHomeFragment();
    public void startEditPageFragment();
    public void startProfileFragment();
}
