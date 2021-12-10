package com.example.memory_loss_app.userpage.profile.editprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.adapter.ContactDisplayAdapter;
import com.example.memory_loss_app.alertdialog.AlertDialogBuilder;
import com.example.memory_loss_app.database.viewmodel.PrimaryContactViewModel;
import com.example.memory_loss_app.database.viewmodel.SecondaryContactsViewModel;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class UpdatePrimaryContacts extends Fragment {

    View view;
    Button addPrimaryContact;
    Button update;
    private int totalContactsAdded = 0;
    String phoneNumber, contactName;
    AlertDialogBuilder alertDialogBuilder;
    AlertDialog alertDialog;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    RecyclerView recyclerView;
    ContactDisplayAdapter adapter;
    PrimaryContactViewModel primaryContactViewModel;
    SecondaryContactsViewModel secondaryContactsViewModel;
    BottomNavigationView navigationView;
    UserPageHandler handler;
    List<SecondaryContactDB> secondaryContactDBList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update_primary_contacts, container, false);
        setRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addPrimaryContact = view.findViewById(R.id.addContacts);
        update = view.findViewById(R.id.update);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Contacts/"+mAuth.getCurrentUser().getUid());
        navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        alertDialogBuilder = new AlertDialogBuilder(builder);
        primaryContactViewModel = new ViewModelProvider(this).get(PrimaryContactViewModel.class);
        secondaryContactsViewModel = new ViewModelProvider(this).get(SecondaryContactsViewModel.class);
        primaryContactViewModel.getPrimaryContact().observe(getActivity(), new Observer<List<PrimaryContactDB>>() {
            @Override
            public void onChanged(List<PrimaryContactDB> primaryContactDBS) {
                totalContactsAdded = primaryContactDBS.size();
                adapter.setPrimaryContactDBList(primaryContactDBS);
            }
        });

        secondaryContactsViewModel.getAllSecondaryContacts().observe(getActivity(), new Observer<List<SecondaryContactDB>>() {
            @Override
            public void onChanged(List<SecondaryContactDB> secondaryContactDBS) {
                secondaryContactDBList = secondaryContactDBS;
            }
        });
        /** Since startActivityForResult has been deprecated used ActivityResultLauncher
         * to access the contacts. Getting the selected contact in the onActivityResult method.
         * Cant add more then 5 emergency contacts. */
        ActivityResultLauncher<Intent> pickContactActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        contactPicked(data);
                    }
                }
        );

        addPrimaryContact.setOnClickListener(view1 -> {
            if (totalContactsAdded < 1) {

                //Starting the intent for accessing the devices contact.
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                pickContactActivity.launch(intent);
            } else {
                // Showing error message to the user.
                alertDialog = alertDialogBuilder.setContactError("You cannot add more then 1 contact",
                        "Contacts limit reached!");
                alertDialog.show();
            }
        });

        update.setOnClickListener(view1 -> {
            if (totalContactsAdded == 0) {
                alertDialog = alertDialogBuilder.setContactError("Add at least 1 Contact.",
                        "No contacts added.");
                alertDialog.show();
            } else {
                handler.profilePage();
                navigationView.setVisibility(View.VISIBLE);
            }


            //Interface reference to the MainActivity.java file.


        });


    }

    private void contactPicked(Intent data) {
        Cursor cursor;
        try {
            Uri uri = data.getData();
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNumber = cursor.getString(phoneIndex);
            contactName = cursor.getString(name);
            boolean uniqueContact = true;
            for (SecondaryContactDB i : secondaryContactDBList) {
                if (contactName.equals(i.getContactName())) {

                    AlertDialog alertDialog = alertDialogBuilder.setContactError(
                            "This contact already exist in the secondaty contacts",
                            "Contact Already Exists!");
                    alertDialog.show();
                    uniqueContact = false;
                    break;
                }

            }
            if (uniqueContact){
                totalContactsAdded++;
                primaryContactViewModel.insert(new PrimaryContactDB(contactName, phoneNumber));
                reference.child("Primary").child("contactName").setValue(contactName);
                reference.child("Primary").child("contactNumber").setValue(phoneNumber);
            }
                // Adding the new object of the PrimaryContactDetails class to the arraylist.


            // Notifying the adapter that a new change has been made.


            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerView() {

        recyclerView = view.findViewById(R.id.updatePrimaryContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactDisplayAdapter(ContactDisplayAdapter.VIEW_TYPE_ADD_CONTACT);

        // Adding swipe functionality to the card.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // If the card is swiped to the right it will be deleted.
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                reference.child("Primary").removeValue();
                primaryContactViewModel.deleteAllNotes();
                totalContactsAdded--;
            }
        }).attachToRecyclerView(recyclerView);

        // Setting the adapter to the recyclerView
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserPageHandler)
            handler = (UserPageHandler) context;
    }
}