package com.example.memory_loss_app.alertdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

import com.example.memory_loss_app.auth.Login;
import com.example.memory_loss_app.userpage.UserPage;
import com.google.firebase.auth.FirebaseAuth;


/**
 * This class creates AlertDialogs
 */
public class AlertDialogBuilder {
    AlertDialog.Builder builder;

    // This constructor will get the reference of the AlertDialog.Builder form the activity and init
    // it over here. With this builder we can create AlertDialog.
    public AlertDialogBuilder(AlertDialog.Builder builder) {
        this.builder = builder;
    }

    // This AlertDialog will ask for user confirmation whether they wan to quit the application or not.
    public AlertDialog createOnBackPressedBuilder(String messgae, String title, Context context) {

        builder.setMessage(messgae)
                .setCancelable(false).setPositiveButton("Yes", (dialogInterface, i) -> {
            ActivityCompat.finishAffinity((Activity) context);
            System.exit(0);
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setTitle(title);

        return builder.create();
    }

    // This AlertDialog will ask for user confirmation whether they wan to Logout or not.
    public AlertDialog logoutConfirmation(String message, String title, Context context, FirebaseAuth mAuth) {

        builder.setMessage(message).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                context.startActivity(new Intent(context, Login.class));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setTitle(title);

        return builder.create();
    }


    // This AlertDialog will show an error message to the user for exceeding the
    // total number of contacts added.
    public AlertDialog setContactError(String message, String title) {

        builder.setMessage(message).setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert).
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).setTitle(title);

        return builder.create();


    }

    // This AlertDialog will ask for user confirmation that if they want to call on the number or not.
    public AlertDialog phoneCallConfirmation(String message, String title, long phoneNumber, Context context) {

        builder.setMessage(message).setTitle(title).setCancelable(false)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    dialogInterface.cancel();

                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });

        return builder.create();
    }

    public AlertDialog wrongAnswerAlert(String message, String title, Context context) {

        builder.setMessage(message).setTitle(title).setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    dialogInterface.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        return builder.create();

    }

    public AlertDialog correctAnswerAlert(String message, String title, Context context) {

        builder.setMessage(message).setTitle(title).setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    context.startActivity(new Intent(context.getApplicationContext(), UserPage.class));
                    dialogInterface.cancel();
                    dialogInterface.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_info);

        return builder.create();
    }

}
