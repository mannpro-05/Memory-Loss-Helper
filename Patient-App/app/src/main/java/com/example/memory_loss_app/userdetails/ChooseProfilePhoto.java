package com.example.memory_loss_app.userdetails;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.notification.alarmmanager.AlarmReceiver;
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.contacts.ContactDetails;
import com.example.memory_loss_app.database.viewmodel.PrimaryContactViewModel;
import com.example.memory_loss_app.database.viewmodel.SecondaryContactsViewModel;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.example.memory_loss_app.framelayoutmanager.ObjectHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will be choosing the profile photo of the user.
 */
public class ChooseProfilePhoto extends Fragment {
    private View view;
    private ObjectHandler handler;
    private ImageView profilePhoto;
    private Button choosePhoto, takePhoto, next;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference, contactReference;
    private StorageReference storageReference;
    private Users user;
    private ArrayList<ContactDetails> primaryContactDetails;
    private Map<String, String> secondaryContactDetails;
    private Map<String, Object> secondaryContactDetailsArranger;
    private Bitmap chosenPhoto;
    private UserViewModel userViewModel;
    private PrimaryContactViewModel primaryContactViewModel;
    private SecondaryContactsViewModel secondaryContactsViewModel;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_choose_profile_photo, container, false);

        profilePhoto = view.findViewById(R.id.displayPhoto);
        choosePhoto = view.findViewById(R.id.choosePhoto);
        takePhoto = view.findViewById(R.id.takePhoto);
        next = view.findViewById(R.id.next);

        profilePhoto.setImageResource(R.drawable.ic_default_profile_icon_24);

        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        primaryContactViewModel = new ViewModelProvider(getActivity()).get(PrimaryContactViewModel.class);
        secondaryContactsViewModel = new ViewModelProvider(getActivity()).get(SecondaryContactsViewModel.class);
        secondaryContactDetailsArranger = new HashMap<>();
        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        contactReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePhoto/" + mAuth.getCurrentUser().getUid());

        /** Since startActivityForResult has been deprecated used ActivityResultLauncher
         * to access the Device camera. Getting the photo is in the onActivityResult method.
         */
        ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(new
                        ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        //Making a BitMap as the ImageView cannot read bundle data.
                        //bundle.get("data") will get the clicked image from the camera and set a
                        //thumbnail photo format in the ImageView.
                        chosenPhoto = (Bitmap) bundle.get("data");
                        profilePhoto.setImageBitmap(chosenPhoto);
                    }
                });

        /** Since startActivityForResult has been deprecated used ActivityResultLauncher
         * to access the Device photo galley. Getting the photo is in the onActivityResult method.
         */
        ActivityResultLauncher<Intent> choosePhotoLauncher = registerForActivityResult(new
                        ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        //Making a BitMap as the ImageView cannot read bundle data.
                        Uri imageUri = data.getData();
                        try {
                            chosenPhoto = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        profilePhoto.setImageBitmap(chosenPhoto);

                    }
                });
        // This will open up the device camera
        takePhoto.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /**This is to check if we have the android resource/application to full fill this request. */
            takePhotoLauncher.launch(intent);
        });

        // This is to open the device photo gallery.
        choosePhoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            choosePhotoLauncher.launch(intent);

        });

        // Button to move to the next activity.
        next.setOnClickListener(view -> {

            if (profilePhoto.getDrawable() != null) {
                System.out.println("In choose Photo!!!!!");
                uploadProfilePhotoToFirebase();
                saveImageToDevice();
                uploadUserData();
                uploadPrimaryContact();
                uploadSecondaryContact();
                setNotification();
                handler.choosePhoto();

            } else {
                Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setNotification() {
        createNotificationChannel();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(user.getNotificationTime().split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(user.getNotificationTime().split(":")[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);


    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "personalQuestion";
            String description = "This is the notification for personal question.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("personalQuestion", name, importance);
            channel.setDescription(description);

            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

    private void saveImageToDevice() {
        ContextWrapper cw = new ContextWrapper(getActivity());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(mypath);
            chosenPhoto.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            System.out.println("Image saved to the device!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Updating the user object once this activity is created.
    // This is done to insert the data of the previous activity to this so that the all the data
    // could be stored into the fire base when the user setup is complete.
    public void updateUserObjectInfo(Users user, ArrayList<ContactDetails> primaryContactDetails,
                                     Map secondaryContactDetails) {
        this.user = user;
        this.primaryContactDetails = primaryContactDetails;
        this.secondaryContactDetails = secondaryContactDetails;

    }

    //Setting the context to the reference of ObjectHandler Interface.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ObjectHandler) {
            handler = (ObjectHandler) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ObjectHandler");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }

    // This method will upload the profile photo to the firebase storage.
    void uploadProfilePhotoToFirebase() {
        profilePhoto.setDrawingCacheEnabled(true);
        profilePhoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profilePhoto.getDrawable()).getBitmap();
        chosenPhoto = bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), "Nope", Toast.LENGTH_SHORT).show();
                System.out.println("Nope!!!!");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                System.out.println("Uploaded!!!!");
            }
        });

    }

    // This will upload the user data to the firebase and mark the setup complete as true.
    void uploadUserData() {
        user.setUserSetupComplete(true);
        user.setLastMemoryLossTrauma("Yet to come!");
        user.setEmail(mAuth.getCurrentUser().getEmail());
        userReference.child(mAuth.getCurrentUser().getUid()).setValue(user);
        UserDetailsDB userDetailsDB = new UserDetailsDB(mAuth.getCurrentUser().getUid(),
                mAuth.getCurrentUser().getEmail(), true, user.getDoctorName(), user.getDiagnosedOn(),
                user.getLastAppointmentDate(), user.getDoctorContactNumber(), user.getNotificationTime(),
                user.getName(), user.getDateOfBirth(), user.getBloodGroup(), user.getAddress(),
                user.getAge(), user.getPersonalQuestion(), user.getAnswer(), user.getLastMemoryLossTrauma());
        userViewModel.insert(userDetailsDB);
        System.out.println("User Data Uploaded!");
    }

    // This will upload the primary data to the firebase and mark the setup complete as true.
    void uploadPrimaryContact() {
        contactReference.child(mAuth.getCurrentUser().getUid()).child("Primary").setValue(primaryContactDetails.get(0));
        PrimaryContactDB primaryContactDB = new PrimaryContactDB(primaryContactDetails.get(0).getContactName(),
                primaryContactDetails.get(0).getContactNumber());
        primaryContactViewModel.insert(primaryContactDB);
        System.out.println("primary contacts uploaded");
    }

    // This method will iterate through all the objects in the secondary arraylist and map the
    // contact info into the firebase.
    void uploadSecondaryContact() {
        int pos = 1;
        for (String i : secondaryContactDetails.keySet()) {
            SecondaryContactDB secondaryContactDB = new SecondaryContactDB(i, secondaryContactDetails.get(i));
            secondaryContactsViewModel.insert(secondaryContactDB);
            secondaryContactDetailsArranger.put(i, secondaryContactDetails.get(i));
        }
        contactReference.child(mAuth.getCurrentUser().getUid()).child("Secondary").setValue(secondaryContactDetailsArranger);
        System.out.println("Secondary contacts uploaded");
    }
}