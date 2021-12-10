package com.example.memory_loss_app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;

import java.util.List;

@Dao
public interface SecondaryContactDao {

    @Insert
    void insert(SecondaryContactDB secondaryContactDB);

    @Update
    void update(SecondaryContactDB secondaryContactDB);

    @Delete
    void delete(SecondaryContactDB secondaryContactDB);

    @Query("DELETE FROM secondary_contact")
    void deleteAllNotes();

    @Query("SELECT * FROM secondary_contact ")
    LiveData<List<SecondaryContactDB>> getAllContacts();
}
