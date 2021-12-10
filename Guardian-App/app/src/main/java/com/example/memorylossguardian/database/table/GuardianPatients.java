package com.example.memorylossguardian.database.table;

import androidx.room.Entity;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "guardian_patients")
public class GuardianPatients {

    private String uniqueId;
    private String name;
    private String dateOfBirth;
    @PrimaryKey
    @NotNull
    private String emailId;
    private String emergencyContactNumber;
    private String lastMemoryLossTrauma;

    public GuardianPatients(String uniqueId, String name, String dateOfBirth, String emailId, String emergencyContactNumber, String lastMemoryLossTrauma) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.emailId = emailId;
        this.emergencyContactNumber = emergencyContactNumber;
        this.lastMemoryLossTrauma = lastMemoryLossTrauma;
    }

    public GuardianPatients() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public String getLastMemoryLossTrauma() {
        return lastMemoryLossTrauma;
    }

    public void setLastMemoryLossTrauma(String lastMemoryLossTrauma) {
        this.lastMemoryLossTrauma = lastMemoryLossTrauma;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
