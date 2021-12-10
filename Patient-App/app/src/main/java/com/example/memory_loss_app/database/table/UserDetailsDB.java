package com.example.memory_loss_app.database.table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "user_details")
public class UserDetailsDB {
    @PrimaryKey
    @NotNull
    private String uid;

    private String email;
    private Boolean userSetupComplete;
    //Patient Details
    private String doctorName, diagnosedOn, lastAppointmentDate, doctorContactNumber;

    private String notificationTime;
    //Personal Information
    private String name, dateOfBirth;
    private String bloodGroup, address;
    private int age;
    //Personal Question
    private String personalQuestion, answer;



    private String lastMemoryLossTrauma;

    public UserDetailsDB(String uid, String email, Boolean userSetupComplete, String doctorName, String diagnosedOn, String lastAppointmentDate, String doctorContactNumber, String notificationTime, String name, String dateOfBirth, String bloodGroup, String address, int age, String personalQuestion, String answer, String lastMemoryLossTrauma) {
        this.uid = uid;
        this.email = email;
        this.userSetupComplete = userSetupComplete;
        this.doctorName = doctorName;
        this.diagnosedOn = diagnosedOn;
        this.lastAppointmentDate = lastAppointmentDate;
        this.doctorContactNumber = doctorContactNumber;
        this.notificationTime = notificationTime;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.age = age;
        this.personalQuestion = personalQuestion;
        this.answer = answer;
        this.lastMemoryLossTrauma = lastMemoryLossTrauma;
    }

    public String getLastMemoryLossTrauma() {
        return lastMemoryLossTrauma;
    }

    public void setLastMemoryLossTrauma(String lastMemoryLossTrauma) {
        this.lastMemoryLossTrauma = lastMemoryLossTrauma;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getUserSetupComplete() {
        return userSetupComplete;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDiagnosedOn() {
        return diagnosedOn;
    }

    public String getLastAppointmentDate() {
        return lastAppointmentDate;
    }

    public String getDoctorContactNumber() {
        return doctorContactNumber;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public int getAge() {
        return age;
    }

    public String getPersonalQuestion() {
        return personalQuestion;
    }

    public String getAnswer() {
        return answer;
    }
}
