package com.example.memory_loss_app.userpage.profile.editprofile;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.userpage.profile.ProfileHandler;
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


public class UpdateProfilePhoto extends Fragment {
    ImageView profilePhoto;
    Button choosePhoto, takePhoto, update;
    Bitmap chosenPhoto;
    Bitmap currentPhoto;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    ProfileHandler handler;

    public UpdateProfilePhoto(Bitmap currentPhoto){
        this.currentPhoto = currentPhoto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profile_photo, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileHandler)
            handler = (ProfileHandler) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePhoto = view.findViewById(R.id.profilePicture);
        choosePhoto = view.findViewById(R.id.choosePhoto);
        takePhoto = view.findViewById(R.id.takePhoto);
        update = view.findViewById(R.id.update);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePhoto/" + mAuth.getCurrentUser().getUid());

        profilePhoto.setImageBitmap(currentPhoto);

        update.setOnClickListener(view1 -> {
            uploadProfilePhotoToFirebase();
            saveImageToDevice();
            handler.editProfile(chosenPhoto);
        });

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
                            chosenPhoto = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        profilePhoto.setImageBitmap(chosenPhoto);

                    }
                });

        takePhoto.setOnClickListener(view1 -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /**This is to check if we have the android resource/application to full fill this request. */
            takePhotoLauncher.launch(intent);
        });

        // This is to open the device photo gallery.
        choosePhoto.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            choosePhotoLauncher.launch(intent);

        });
    }

    private void saveImageToDevice() {
        ContextWrapper cw = new ContextWrapper(getActivity());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,"profile.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(mypath);
            chosenPhoto.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }



}