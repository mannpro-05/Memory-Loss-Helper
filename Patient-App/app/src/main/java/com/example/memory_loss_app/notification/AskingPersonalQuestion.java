package com.example.memory_loss_app.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.alertdialog.AlertDialogBuilder;
import com.example.memory_loss_app.auth.Login;
import com.example.memory_loss_app.database.viewmodel.PrimaryContactViewModel;
import com.example.memory_loss_app.database.viewmodel.SecondaryContactsViewModel;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.List;

public class AskingPersonalQuestion extends AppCompatActivity {
    TextView question;
    TextInputLayout answer;
    UserViewModel userViewModel;
    private PrimaryContactViewModel primaryContactViewModel;
    SecondaryContactsViewModel secondaryContactsViewModel;
    Button submit;
    UserDetailsDB userDetailsDB;
    List<SecondaryContactDB> secondaryContactDB;
    PrimaryContactDB primaryContactDB;
    android.app.AlertDialog.Builder builder;
    AlertDialogBuilder alertDialogBuilder;
    private int totalNumberOfChances = 2;
    LocationManager locationManager;
    Location location;
    Double longitude;
    Double latitude;
    FirebaseAuth mAuth;
    DatabaseReference reference;

    private static final int MULTIPLE_PERMISSION_CODE = 103;
    private String googleMapLocation = "";
    private String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asking_personal_question);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS};

        if (!hasPermission(this, permissions)) {
            ActivityCompat.requestPermissions(AskingPersonalQuestion.this, permissions, 103);
        }

        question = findViewById(R.id.personalQuestion);
        answer = findViewById(R.id.answer);
        submit = findViewById(R.id.submit);
        reference = FirebaseDatabase.getInstance().getReference().child("Users/" + mAuth.getCurrentUser().getUid());
        builder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder = new AlertDialogBuilder(builder);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        primaryContactViewModel = new ViewModelProvider(this).get(PrimaryContactViewModel.class);
        secondaryContactsViewModel = new ViewModelProvider(this).get(SecondaryContactsViewModel.class);
        userViewModel.getAllNotes().observe(this, userDetailsDBS -> {
            System.out.println(userDetailsDBS.size());
            userDetailsDB = userDetailsDBS.get(0);
            question.setText(userDetailsDB.getPersonalQuestion());
        });

        secondaryContactsViewModel.getAllSecondaryContacts().observe(this, secondaryContactDBS -> {
            secondaryContactDB = secondaryContactDBS;
        });

        primaryContactViewModel.getPrimaryContact().observe(this, primaryContactDBS -> {
            primaryContactDB = primaryContactDBS.get(0);
        });


        submit.setOnClickListener(view -> {
            String userAnswer = answer.getEditText().getText().toString();
            String actualAnswer = userDetailsDB.getAnswer();
            totalNumberOfChances--;
            if (userAnswer.equalsIgnoreCase(actualAnswer)) {
                android.app.AlertDialog alertDialog = alertDialogBuilder.correctAnswerAlert("You have given the answere correctely.",
                        "Correct answer given!", this);
                alertDialog.show();
            } else if (totalNumberOfChances > 0) {
                android.app.AlertDialog alertDialog = alertDialogBuilder.wrongAnswerAlert("You have given a wrong answer." +
                                "\n You have " + totalNumberOfChances + " remaining to answere correctly.",
                        "Wrong Answer given!", this);

                alertDialog.show();
            } else {
                long now = System.currentTimeMillis();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String date = df.format(now);
                builder.setMessage("You have not given correct answers in all the trials." +
                        "\nNow we will be calling your emergency contact and share your location to " +
                        "everyone so that they can help you out. ")
                        .setTitle("Failed to answer correctly!").setCancelable(true)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            dialogInterface.cancel();
                            dialogInterface.dismiss();
                            handleMemoryLossSituation();

                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);

                Thread firebaseUpdation = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        reference.child("lastMemoryLossTrauma")
                                .setValue(date);
                    }
                };
                firebaseUpdation.start();

                builder.create();
                builder.show();
            }

        });

    }

    private void handleMemoryLossSituation() {
        googleMapLocation = "http://www.google.com/maps/place/" + latitude + "," + longitude;
        sendSms();
        callPrimary();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            System.out.println(provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private boolean hasPermission(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else if (permission == Manifest.permission.ACCESS_FINE_LOCATION) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                }
            }
        }
        return true;
    }

//    private void checkPermission(String permission, int requestCode) {
//        if (ActivityCompat.checkSelfPermission(AskingPersonalQuestion.this, permission) == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(AskingPersonalQuestion.this, new String[]{permission}, requestCode);
//        } else if (requestCode == LOCATION_PERMISSION_CODE){
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if (location == null) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
//            } else {
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
//            }
//        }
//
//    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(requestCode + " " + grantResults.length);
        if (requestCode == MULTIPLE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                } else {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                builder.setMessage("You have not given the permission to get the location of the device!\n" +
                        "Go into the settings, enable it and try again!").setTitle("Enable location permission.")
                        .setPositiveButton(android.R.string.ok,
                                (dialogInterface, i) -> {
                                    dialogInterface.cancel();
                                    dialogInterface.dismiss();
                                });
                builder.create();
                builder.show();
            } else if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Phone Call Denied!", Toast.LENGTH_SHORT).show();
            } else if (grantResults[2] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Sms Send Denied!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sendSms() {
        String message = userDetailsDB.getName() + " might have experienced Memory Loss Disorder. " +
                ": " + googleMapLocation;
        System.out.println(message);
        for (SecondaryContactDB db : secondaryContactDB) {
            String phoneNumber = db.getContactNumber();
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(phoneNumber, null, message, null, null);

        }

        String phoneNumber = primaryContactDB.getContactNumber();
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(phoneNumber, null, message, null, null);
        answer.getEditText().setText(googleMapLocation);

    }

    public void callPrimary() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + primaryContactDB.getContactNumber()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}