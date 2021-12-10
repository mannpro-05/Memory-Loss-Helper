package com.example.memory_loss_app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.memory_loss_app.database.table.UserDetailsDB;

import java.util.List;

@Dao
public interface UserDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserDetailsDB userDetailsDB);

    @Update
    void update(UserDetailsDB userDetailsDB);

    @Query("DELETE FROM user_details")
    void deleteAllNotes();

    @Query("SELECT * FROM user_details ")
    LiveData<List<UserDetailsDB>> getAllDetails();

    @Query("SELECT COUNT(*) FROM user_details")
    LiveData<Integer> userExist();

}
