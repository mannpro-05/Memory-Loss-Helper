package com.example.memory_loss_app.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memory_loss_app.database.repo.SecondaryContactRepository;
import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;

import java.util.List;

public class SecondaryContactsViewModel extends AndroidViewModel {

    SecondaryContactRepository repository;
    LiveData<List<SecondaryContactDB>> secondaryContact;

    public SecondaryContactsViewModel(@NonNull Application application) {
        super(application);
        repository = new SecondaryContactRepository(application);
        secondaryContact = repository.getAllSecondaryContacts();
    }
    public void insert(SecondaryContactDB secondaryContactDB){
        repository.insert(secondaryContactDB);
    }

    public void update(SecondaryContactDB secondaryContactDB){
        repository.update(secondaryContactDB);
    }

    public void delete(SecondaryContactDB secondaryContactDB){
        repository.delete(secondaryContactDB);
    }

    public void deleteAllNotes(){
        repository.deleteAll();
    }

    public LiveData<List<SecondaryContactDB>> getAllSecondaryContacts(){
        return secondaryContact;
    }
}
