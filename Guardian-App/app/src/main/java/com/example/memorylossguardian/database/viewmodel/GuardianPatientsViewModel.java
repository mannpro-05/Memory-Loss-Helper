package com.example.memorylossguardian.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memorylossguardian.database.repository.GuardianPatientsRepository;
import com.example.memorylossguardian.database.table.GuardianPatients;

import java.util.List;

public class GuardianPatientsViewModel extends AndroidViewModel {
    GuardianPatientsRepository repository;

    private LiveData<List<GuardianPatients>> patients;
    public GuardianPatientsViewModel(@NonNull Application application) {
        super(application);
        repository = new GuardianPatientsRepository(application);
        patients = repository.getPatients();
    }

    public boolean insert(GuardianPatients guardianPatients){
        return repository.insert(guardianPatients);
    }

    public void delete(GuardianPatients guardianPatients){
        repository.delete(guardianPatients);
    }

    public void deleteAll(){
        repository.deleteAll();
    }

    public LiveData<List<GuardianPatients>> getPatients() {
        return patients;
    }
}
