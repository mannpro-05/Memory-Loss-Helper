package com.example.memory_loss_app.userpage.profile;

import android.graphics.Bitmap;

public interface ProfileHandler {
    public void logout();
    public void editPrimaryContacts();
    public void editSecondaryContacts();
    public void editProfile();
    public void editProfile(Bitmap bitmap);
    public void updateProfilePhoto();
    public void updatePatientDetails();
    public void updateUserDetails();
    public void updatePersonalQuestion();

}
