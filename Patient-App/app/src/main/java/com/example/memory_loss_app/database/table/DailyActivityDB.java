package com.example.memory_loss_app.database.table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "daily_activity")
public class DailyActivityDB {
    @PrimaryKey()
    @NotNull
    private String id;
    private String title;
    private String description;

    public DailyActivityDB(String id,String title, String description) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
