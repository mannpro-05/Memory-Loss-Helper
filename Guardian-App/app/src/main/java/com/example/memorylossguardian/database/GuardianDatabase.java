package com.example.memorylossguardian.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.memorylossguardian.database.dao.GuardianDao;
import com.example.memorylossguardian.database.dao.GuardianPatientDao;
import com.example.memorylossguardian.database.table.GuardianPatients;
import com.example.memorylossguardian.database.table.GuardianTable;

@Database(entities = {GuardianTable.class, GuardianPatients.class}, version = 3)
public abstract class GuardianDatabase extends RoomDatabase {
    private static GuardianDatabase instance;

    public abstract GuardianDao guardianDao();
    public abstract GuardianPatientDao guardianPatientDao();

    public static synchronized GuardianDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    GuardianDatabase.class,"Guardian_Database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
