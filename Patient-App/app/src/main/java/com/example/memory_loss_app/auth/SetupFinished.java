package com.example.memory_loss_app.auth;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;


import com.example.memory_loss_app.FingerPrintScanner;
import com.example.memory_loss_app.MainActivity;
import com.example.memory_loss_app.notification.alarmmanager.AlarmReceiver;
import com.example.memory_loss_app.contacts.ContactDetails;
import com.example.memory_loss_app.database.viewmodel.DailyActivitiesViewModel;
import com.example.memory_loss_app.database.viewmodel.PrimaryContactViewModel;
import com.example.memory_loss_app.database.viewmodel.SecondaryContactsViewModel;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.DailyActivityDB;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.example.memory_loss_app.userpage.UserPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * This is a class which checks if the user has finished user setup.
 * This is done so that the user can use the application properly.
 */
public class SetupFinished extends Activity {

    StorageReference storageReference;
    Bitmap bitmap;
    UserViewModel userViewModel;
    PrimaryContactViewModel primaryContactViewModel;
    SecondaryContactsViewModel secondaryContactsViewModel;
    DailyActivitiesViewModel dailyActivitiesViewModel;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    Context context;
    Users user;
    Calendar calendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    ProgressDialog pd;

    // This method checks if the user setup is finished or not
    // If it is finished then the user will be sent to UserPage of the application.
    // Else will be sent to setup page.

    public void isSetupUserComplete(FirebaseAuth mAuth, DatabaseReference reference, Context context, Boolean fromLogin) {
        this.reference = reference;
        this.context = context;
        this.mAuth = mAuth;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                pd.setMessage("Please wait.");
                pd.show();
            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users/" + mAuth.getCurrentUser().getUid() + "/userSetupComplete").getValue() == null){
                    mAuth.signOut();
                    Toast.makeText(context, "This email is not registered with this application!", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, Login.class));
                    return;
                }
                boolean value = (boolean) snapshot.child("Users/" + mAuth.getCurrentUser().getUid() + "/userSetupComplete").getValue();
                userViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);
                if (value && fromLogin) {
                    setNotificationChannel();
                    new InitialSetup().execute();
                } else if (value) {
                    context.startActivity(new Intent(context, FingerPrintScanner.class));
                } else {
                    context.startActivity(new Intent(context, MainActivity.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class RetriveImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                bitmap = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    void getDataFromFireBase() {

        userViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);
        primaryContactViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PrimaryContactViewModel.class);
        secondaryContactsViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SecondaryContactsViewModel.class);
        dailyActivitiesViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(DailyActivitiesViewModel.class);

        reference.child("Users/" + mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                setNotification();
                UserDetailsDB userDetailsDB = new UserDetailsDB(mAuth.getCurrentUser().getUid(),
                        mAuth.getCurrentUser().getEmail(), true, user.getDoctorName(), user.getDiagnosedOn(),
                        user.getLastAppointmentDate(), user.getDoctorContactNumber(), String.valueOf(user.getNotificationTime()),
                        user.getName(), user.getDateOfBirth(), user.getBloodGroup(), user.getAddress(),
                        user.getAge(), user.getPersonalQuestion(), user.getAnswer(), user.getLastMemoryLossTrauma());
                userViewModel.insert(userDetailsDB);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("Contacts/" + mAuth.getCurrentUser().getUid() + "/Primary").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ContactDetails primaryContact = snapshot.getValue(ContactDetails.class);
                primaryContactViewModel.insert(new PrimaryContactDB(primaryContact.getContactName(), primaryContact.getContactNumber()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("Contacts/" + mAuth.getCurrentUser().getUid() + "/Secondary").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    SecondaryContactDB secondaryContactDB = new SecondaryContactDB(i.getKey(), i.getValue().toString());
                    secondaryContactsViewModel.insert(secondaryContactDB);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("DailyActivity/" + mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    String title = "";
                    String description = "";
                    for (DataSnapshot j : i.getChildren()) {
                        if(j.getKey().equals("title"))
                            title = j.getValue().toString();
                        else
                            description = j.getValue().toString();
                    }
                    dailyActivitiesViewModel.insert(new DailyActivityDB(i.getKey(), title, description));
                }
                pd.dismiss();
                context.startActivity(new Intent(context, UserPage.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public class InitialSetup extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePhoto/" + mAuth.getCurrentUser().getUid());
            ContextWrapper cw = new ContextWrapper(context);
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath = new File(directory, "profile.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(mypath);
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    bitmap = new RetriveImage().doInBackground(uri.toString());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    try {
                        fos.close();
                        getDataFromFireBase();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).addOnFailureListener(e -> e.printStackTrace());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    void setNotification(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(user.getNotificationTime().split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(user.getNotificationTime().split(":")[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    void setNotificationChannel(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "personalQuestion";
            String description = "This is the notification for personal question.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("personalQuestion", name, importance);
            channel.setDescription(description);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}
