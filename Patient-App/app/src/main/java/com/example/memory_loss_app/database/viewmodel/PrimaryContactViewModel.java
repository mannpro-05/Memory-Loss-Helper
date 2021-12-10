package com.example.memory_loss_app.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memory_loss_app.database.repo.PrimaryContactRepository;
import com.example.memory_loss_app.database.table.PrimaryContactDB;

import java.util.List;

public class PrimaryContactViewModel extends AndroidViewModel {
    PrimaryContactRepository repository;
    private LiveData<List<PrimaryContactDB>> primaryContact;
    public PrimaryContactViewModel(@NonNull Application application) {
        super(application);
        repository = new PrimaryContactRepository(application);
        primaryContact = repository.getAllNotes();
    }

    public void insert(PrimaryContactDB primaryContactDB){
        repository.insert(primaryContactDB);
    }

    public void update(PrimaryContactDB primaryContactDB){
        repository.update(primaryContactDB);
    }

    public void deleteAllNotes(){
        repository.deleteAllPrimaryContacts();
    }

    public LiveData<List<PrimaryContactDB>> getPrimaryContact(){
        return primaryContact;
    }
}
