package com.example.memory_loss_app.framelayoutmanager;

import android.graphics.Bitmap;

import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.contacts.ContactDetails;


import java.util.ArrayList;
import java.util.Map;

/**
 * Interface for handling the user setup fragments.
 */
public interface ObjectHandler {

    public void patientDetails(Users user);

    public void userDetails(Users user);

    public void personalQuestions(Users usert);

    public void addPrimaryContacts(Users user, ArrayList<ContactDetails> primaryContactDetails);

    public void addSecondaryContacts(Users user, ArrayList<ContactDetails> primaryContactDetails,
                                     Map secondaryContactDetails);

    public void choosePhoto();

}
