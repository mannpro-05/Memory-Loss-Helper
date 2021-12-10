package com.example.memory_loss_app.userpage.profile.editprofile;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.userpage.profile.ProfileHandler;


public class UpdateProfilePage extends Fragment {
    CardView editPatientDetails;
    CardView editUserDetails;
    CardView editPersonalQuestion;
    TextView choosePhoto;
    ImageView imageView;
    Bitmap bitmap;
    ProfileHandler handler;
    public UpdateProfilePage(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setArguments(savedInstanceState);

        imageView = view.findViewById(R.id.profilePicture);
        editPatientDetails = view.findViewById(R.id.changePatientDetails);
        choosePhoto = view.findViewById(R.id.changePhoto);
        editUserDetails = view.findViewById(R.id.changeUserDetils);
        editPersonalQuestion = view.findViewById(R.id.changePersonalQuestion);
        imageView.setImageBitmap(bitmap);

        choosePhoto.setOnClickListener(view1 -> {
            handler.updateProfilePhoto();
        });

        editPatientDetails.setOnClickListener(view1 -> {
            handler.updatePatientDetails();
        });

        editUserDetails.setOnClickListener(view1 -> {
            handler.updateUserDetails();
        });

        editPersonalQuestion.setOnClickListener(view1 -> {
            handler.updatePersonalQuestion();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileHandler){
            handler = (ProfileHandler) context;
        }
    }

}