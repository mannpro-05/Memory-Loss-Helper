package com.example.memory_loss_app.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memory_loss_app.database.repo.UserDetailsRepository;
import com.example.memory_loss_app.database.table.UserDetailsDB;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserDetailsRepository repository;
    private LiveData<List<UserDetailsDB>> userDetails;
    private LiveData<Integer> userExists;


    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserDetailsRepository(application);
        userDetails = repository.getAllDetails();
        userExists = repository.userExists();

    }

    public void insert(UserDetailsDB note){
        repository.insert(note);
    }

    public void update(UserDetailsDB note){
        repository.update(note);
    }

    public void deleteAllNotes(){
        repository.deleteAllNotes();
    }

    public LiveData<List<UserDetailsDB>> getAllNotes(){
        return userDetails;
    }

}
