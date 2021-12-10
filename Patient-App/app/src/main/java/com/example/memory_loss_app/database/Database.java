package com.example.memory_loss_app.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.memory_loss_app.database.dao.DailyActivityDao;
import com.example.memory_loss_app.database.dao.PrimaryContactDao;
import com.example.memory_loss_app.database.dao.SecondaryContactDao;
import com.example.memory_loss_app.database.dao.UserDetailsDao;
import com.example.memory_loss_app.database.table.DailyActivityDB;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;

@androidx.room.Database(entities = {UserDetailsDB.class,
        PrimaryContactDB.class, SecondaryContactDB.class, DailyActivityDB.class}, version = 4)
public abstract class Database extends RoomDatabase {

    private static Database instance;

    public abstract UserDetailsDao userDetailsDao();

    public abstract PrimaryContactDao primaryContactDao();

    public abstract SecondaryContactDao secondaryContactDao();

    public abstract DailyActivityDao dailyActivityDao();



    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Database.class,"App_Database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;

    }



}
