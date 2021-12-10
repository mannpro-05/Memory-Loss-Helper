package com.example.memory_loss_app.auth;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.memory_loss_app.database.viewmodel.DailyActivitiesViewModel;
import com.example.memory_loss_app.database.viewmodel.PrimaryContactViewModel;
import com.example.memory_loss_app.database.viewmodel.SecondaryContactsViewModel;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;

public class Logout implements Runnable{

    private final UserViewModel userViewModel;
    private final PrimaryContactViewModel primaryContactViewModel;
    private final SecondaryContactsViewModel secondaryContactsViewModel;
    private final DailyActivitiesViewModel dailyActivitiesViewModel;

    public Logout(Context context) {
        userViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);
        primaryContactViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PrimaryContactViewModel.class);
        secondaryContactsViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SecondaryContactsViewModel.class);
        dailyActivitiesViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(DailyActivitiesViewModel.class);
    }

    @Override
    public void run() {
        userViewModel.deleteAllNotes();
        primaryContactViewModel.deleteAllNotes();
        secondaryContactsViewModel.deleteAllNotes();
        dailyActivitiesViewModel.deleteAll();
        System.out.println("done deleting");
    }
}
