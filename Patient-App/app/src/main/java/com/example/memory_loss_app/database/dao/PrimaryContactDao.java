package com.example.memory_loss_app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;

import java.util.List;

@Dao
public interface PrimaryContactDao {
    @Insert
    void insert(PrimaryContactDB PrimaryContactDB);

    @Update
    void update(PrimaryContactDB PrimaryContactDB);

    @Query("DELETE FROM primary_contact")
    void deleteAllNotes();

    @Query("SELECT * FROM primary_contact ")
    LiveData<List<PrimaryContactDB>> getAllContacts();
}
