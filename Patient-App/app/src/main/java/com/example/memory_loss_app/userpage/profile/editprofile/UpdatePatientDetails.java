package com.example.memory_loss_app.userpage.profile.editprofile;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.notification.alarmmanager.AlarmReceiver;
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.example.memory_loss_app.datetimepicker.DatePickerEditText;
import com.example.memory_loss_app.userpage.profile.ProfileHandler;
import com.example.memory_loss_app.validation.PatientDetailsValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdatePatientDetails extends Fragment {
    TextInputLayout doctorName, doctorContactNumber;
    TextInputLayout diagnosedOn, lastAppointmentDate;
    TextInputLayout timeOfNotification;
    Button update;
    FirebaseAuth mAuth;
    PatientDetailsValidation validation;
    Users user;
    DatabaseReference reference;
    ProfileHandler handler;
    DatePickerEditText datePickerEditText;
    UserViewModel userViewModel;
    MaterialTimePicker timePicker;
    Calendar calendar;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_patient_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doctorName = view.findViewById(R.id.doctorName);
        createNotificationChannel();
        doctorContactNumber = view.findViewById(R.id.doctorContactNumber);
        diagnosedOn = view.findViewById(R.id.diagnosedOn);
        lastAppointmentDate = view.findViewById(R.id.lastAppointmentDate);
        update = view.findViewById(R.id.update);
        timeOfNotification = view.findViewById(R.id.notification_time);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        validation = new PatientDetailsValidation(doctorName, doctorContactNumber,
                diagnosedOn, lastAppointmentDate, timeOfNotification);
        user = new Users();
        datePickerEditText = new DatePickerEditText();
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users/" + mAuth.getCurrentUser().getUid());

        diagnosedOn.getEditText().setOnClickListener(view1 -> datePickerEditText.datePicker(diagnosedOn, getActivity()));

        lastAppointmentDate.getEditText().setOnClickListener(view1 -> datePickerEditText.datePicker(lastAppointmentDate, getActivity()));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                doctorName.getEditText().setText(snapshot.child("doctorName").getValue().toString());
                doctorContactNumber.getEditText().setText(snapshot.child("doctorContactNumber").getValue().toString());
                diagnosedOn.getEditText().setText(snapshot.child("diagnosedOn").getValue().toString());
                lastAppointmentDate.getEditText().setText(snapshot.child("lastAppointmentDate").getValue().toString());
                timeOfNotification.getEditText().setText(snapshot.child("notificationTime").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        update.setOnClickListener(view1 -> {

            if (validation.validator()){

                setNotification();
                System.out.println("Notifications done!");
                user.setDoctorName(doctorName.getEditText().getText().toString());
                user.setDiagnosedOn(diagnosedOn.getEditText().getText().toString());
                user.setDoctorContactNumber(doctorContactNumber.getEditText().getText().toString());
                user.setLastAppointmentDate(lastAppointmentDate.getEditText().getText().toString());
                user.setNotificationTime(timeOfNotification.getEditText().getText().toString());
                reference.setValue(user);

                UserDetailsDB userDetailsDB = new UserDetailsDB(mAuth.getCurrentUser().getUid(),
                        mAuth.getCurrentUser().getEmail(), true, user.getDoctorName(), user.getDiagnosedOn(),
                        user.getLastAppointmentDate(), user.getDoctorContactNumber(), String.valueOf(user.getNotificationTime()),
                        user.getName(), user.getDateOfBirth(), user.getBloodGroup(), user.getAddress(),
                        user.getAge(), user.getPersonalQuestion(), user.getAnswer(), user.getLastMemoryLossTrauma());

                userViewModel.update(userDetailsDB);
                handler.editProfile();

            }

        });
        timeOfNotification.getEditText().setRawInputType(InputType.TYPE_NULL);
        timeOfNotification.getEditText().setTextIsSelectable(false);
        timeOfNotification.getEditText().setOnClickListener(view1 -> {
            setTimePicker();
        });


    }

    void setTimePicker(){
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(9)
                .setMinute(00)
                .build();
        timePicker.show(getChildFragmentManager(), "TAG");

        timePicker.addOnPositiveButtonClickListener(view1 -> {
            timeOfNotification.getEditText().setText(timePicker.getHour()+":"+timePicker.getMinute());
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        });
    }

    private void setNotification() {
        if (calendar == null){
            calendar = Calendar.getInstance();
            String time = timeOfNotification.getEditText().getText().toString();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        System.out.println("Notification Done"+user.getNotificationTime().split(":")[0]);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "PersonalQuestion";
            String description = "This is the notification for personal question.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("personalQuestion", name, importance);
            channel.setDescription(description);

            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            System.out.println("Channel Set");
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileHandler)
            handler = (ProfileHandler) context;
    }
}