package com.example.memory_loss_app.contacts;

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
import androidx.fragment.app.Fragment;
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
import com.example.memory_loss_app.auth.Users;
import com.example.memory_loss_app.framelayoutmanager.ObjectHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * This class takes care of adding Primary contact to the application.
 */
public class AddPrimaryContact extends Fragment {
    View view;
    ObjectHandler handler;
    Button addPrimaryContact;
    Button next;
    private int totalContactsAdded = 0;
    String phoneNumber, contactName;
    AlertDialogBuilder alertDialogBuilder;
    AlertDialog alertDialog;
    ArrayList<String> arrayList;
    ContactDetails contactDetails;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    Users user;
    RecyclerView recyclerView;
    ContactDisplayAdapter adapter;
    ArrayList<ContactDetails> contactDetailsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_primary_contact, container, false);

        addPrimaryContact = view.findViewById(R.id.addContacts);
        next = view.findViewById(R.id.next);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        alertDialogBuilder = new AlertDialogBuilder(builder);
        setRecyclerView();

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

        // Adding a contact to the application.
        addPrimaryContact.setOnClickListener(view -> {
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

        // Button to move to the next activity.
        next.setOnClickListener(view -> {
            if (totalContactsAdded == 0) {
                alertDialog = alertDialogBuilder.setContactError("Add at least 1 Contact.",
                        "No contacts added.");
                alertDialog.show();
                return;
            }
            addingIntoFirebase();

            //Interface reference to the MainActivity.java file.
            handler.addPrimaryContacts(user, contactDetailsList);

        });

        return view;
    }

    // Updating the user object once this activity is created.
    // This is done to insert the data of the previous activity to this so that the all the data
    // could be stored into the fire base when the user setup is complete.
    public void updateUserObjectInfo(Users user) {
        this.user = user;
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

    // This method will extract information out of the selected contact.
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

            // Adding the new object of the PrimaryContactDetails class to the arraylist.
            contactDetailsList.add(new ContactDetails(contactName, phoneNumber));
            adapter.setContactDetails(contactDetailsList);
            // Notifying the adapter that a new change has been made.
            totalContactsAdded++;
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adding values to the class object.
    private void addingIntoFirebase() {
        contactDetails = contactDetailsList.get(0);
    }

    // Setting up the recyclerview for this activity.
    private void setRecyclerView() {

        contactDetailsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.contactsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactDisplayAdapter(contactDetailsList, ContactDisplayAdapter.VIEW_TYPE_ADD_CONTACT);

        // Adding swipe functionality to the card.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // If the card is swiped to the right it will be deleted.
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                contactDetailsList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                totalContactsAdded--;


            }
        }).attachToRecyclerView(recyclerView);

        // Setting the adapter to the recyclerView
        recyclerView.setAdapter(adapter);
    }
}