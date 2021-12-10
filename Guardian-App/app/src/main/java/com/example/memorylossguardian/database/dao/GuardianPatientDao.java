package com.example.memorylossguardian.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.memorylossguardian.database.table.GuardianPatients;

import java.util.List;

@Dao
public interface GuardianPatientDao {

    @Insert
    void insert(GuardianPatients guardianPatients);

    @Delete
    void delete(GuardianPatients guardianPatients);

    @Query("DELETE FROM guardian_patients")
    void deleteAll();

    @Query("SELECT * FROM guardian_patients")
    LiveData<List<GuardianPatients>> getPatients();

}
