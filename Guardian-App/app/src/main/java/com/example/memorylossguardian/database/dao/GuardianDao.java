package com.example.memorylossguardian.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.memorylossguardian.database.table.GuardianTable;

import java.util.List;

@Dao
public interface GuardianDao {

    @Insert
    void insert(GuardianTable guardianTable);

    @Update
    void update(GuardianTable guardianTable);

    @Query("DELETE FROM guardians_table")
    void delete();

    @Query("SELECT * FROM guardians_table")
    LiveData<List<GuardianTable>> getGuardian();

}
