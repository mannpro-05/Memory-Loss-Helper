package com.example.memory_loss_app.auth;

/**
 * This is the class to store the user details in the setup process.
 */
public class Users {
    //Registration Details
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

    public Users() {

    }

    public String getLastMemoryLossTrauma() {
        return lastMemoryLossTrauma;
    }

    public void setLastMemoryLossTrauma(String lastMemoryLossTrauma) {
        this.lastMemoryLossTrauma = lastMemoryLossTrauma;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDiagnosedOn() {
        return diagnosedOn;
    }

    public void setDiagnosedOn(String diagnosedOn) {
        this.diagnosedOn = diagnosedOn;
    }

    public String getLastAppointmentDate() {
        return lastAppointmentDate;
    }

    public void setLastAppointmentDate(String lastAppointmentDate) {
        this.lastAppointmentDate = lastAppointmentDate;
    }

    public String getDoctorContactNumber() {
        return doctorContactNumber;
    }

    public void setDoctorContactNumber(String doctorContactNumber) {
        this.doctorContactNumber = doctorContactNumber;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPersonalQuestion() {
        return personalQuestion;
    }

    public void setPersonalQuestion(String personalQuestion) {
        this.personalQuestion = personalQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Boolean getUserSetupComplete() {
        return userSetupComplete;
    }

    public void setUserSetupComplete(Boolean userSetupComplete) {
        this.userSetupComplete = userSetupComplete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
