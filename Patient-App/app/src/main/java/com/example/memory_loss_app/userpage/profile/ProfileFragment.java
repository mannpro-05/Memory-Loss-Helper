package com.example.memory_loss_app.userpage.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.auth.Logout;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;


public class ProfileFragment extends Fragment {

    private View view;
    private LinearLayout changePrimaryContact;
    private LinearLayout changeSecondaryContacts;
    private LinearLayout editProfile;
    private LinearLayout logout;
    private FirebaseAuth mAuth;
    private ImageView profilePicture;
    private TextView name;
    private Bitmap bitmap;
    private ProfileHandler handler;
    private UserViewModel userViewModel;
    private UserDetailsDB userDetailsDB;
    private Button shareUniqueId;
    private String uniqueId;

    public ProfileFragment(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        changePrimaryContact = view.findViewById(R.id.changePrimaryContact);
        changeSecondaryContacts = view.findViewById(R.id.changeSecondaryContact);
        editProfile = view.findViewById(R.id.editProfile);
        logout = view.findViewById(R.id.logout);
        profilePicture = view.findViewById(R.id.profilePicture);
        profilePicture.setImageBitmap(bitmap);
        shareUniqueId = view.findViewById(R.id.share_id);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        uniqueId = mAuth.getCurrentUser().getUid();

        userViewModel.getAllNotes().observe(getActivity(), new Observer<List<UserDetailsDB>>() {
            @Override
            public void onChanged(List<UserDetailsDB> userDetailsDBS) {
                if (userDetailsDBS.size() > 0) {
                    userDetailsDB = userDetailsDBS.get(0);
                    name.setText(userDetailsDB.getName());
                }

            }
        });


        logout.setOnClickListener(view1 -> {
            Thread logout = new Thread(new Logout(getActivity()));
            logout.start();
            try {
                logout.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.logout();
        });

        editProfile.setOnClickListener(view1 -> {
            handler.editProfile();
        });

        changePrimaryContact.setOnClickListener(view1 -> {
            handler.editPrimaryContacts();
        });

        changeSecondaryContacts.setOnClickListener(view1 -> {
            handler.editSecondaryContacts();
        });
        shareUniqueId.setOnClickListener(view1 -> {
            String mimeType = "text/plain";
            String title = "Unique Id";
            ShareCompat.IntentBuilder.from(getActivity())
                    .setChooserTitle(title)
                    .setType(mimeType)
                    .setText(uniqueId)
                    .startChooser();
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileHandler) {
            handler = (ProfileHandler) context;
        }
    }

}