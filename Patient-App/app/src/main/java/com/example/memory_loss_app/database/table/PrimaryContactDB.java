package com.example.memory_loss_app.database.table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "primary_contact")
public class PrimaryContactDB {
    @PrimaryKey
    @NotNull
    private String contactName;
    private String contactNumber;

    public PrimaryContactDB(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}
