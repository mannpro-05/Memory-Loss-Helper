package com.example.memory_loss_app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.memory_loss_app.database.table.DailyActivityDB;

import java.util.List;

@Dao
public interface DailyActivityDao {

    @Insert
    void insert(DailyActivityDB dailyActivityDB);

    @Update
    void update(DailyActivityDB dailyActivityDB);

    @Delete
    void delete(DailyActivityDB dailyActivityDB);

    @Query("DELETE FROM daily_activity")
    void deleteAllDailyActivities();

    @Query("SELECT * FROM daily_activity ")
    LiveData<List<DailyActivityDB>> getAllDailyActivities();

}
