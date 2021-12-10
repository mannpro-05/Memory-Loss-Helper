package com.example.memorylossguardian.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memorylossguardian.database.repository.GuardianRepository;
import com.example.memorylossguardian.database.table.GuardianTable;

import java.util.List;

public class GuardianViewModel extends AndroidViewModel {
    GuardianRepository repository;
    private LiveData<List<GuardianTable>> guardianData;

    public GuardianViewModel(@NonNull Application application) {
        super(application);
        repository = new GuardianRepository(application);
        guardianData = repository.getGuardianData();
    }

    public void insert(GuardianTable guardianTable) {
        repository.insert(guardianTable);
    }

    public void update(GuardianTable guardianTable) {
        repository.update(guardianTable);
    }

    public void delete() {
        repository.delete();
    }


    public LiveData<List<GuardianTable>> getGuardianData() {
        return guardianData;
    }
}
