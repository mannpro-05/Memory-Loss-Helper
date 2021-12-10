package com.example.memory_loss_app.database.table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "secondary_contact")
public class SecondaryContactDB {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String contactName;
    private String contactNumber;

    public SecondaryContactDB(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
