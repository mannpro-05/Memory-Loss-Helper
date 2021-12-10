package com.example.memory_loss_app.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
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
import java.util.HashMap;
import java.util.Map;


public class AddSecondaryContacts extends Fragment {
    View view;
    ObjectHandler handler;
    Button addSecondaryContact;
    Button next;
    private int totalContactsAdded = 0;
    String phoneNumber, contactName;
    AlertDialogBuilder alertDialogBuilder;
    AlertDialog alertDialog;
    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;
    ArrayList<String> keyArrayList;
    Map<String, String> firebaseContactAdder;
    ArrayList<ContactDetails> contactDetailsList;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    Users user;
    ArrayList<ContactDetails> primaryContactDetails;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_secondary_contacts, container, false);
        addSecondaryContact = view.findViewById(R.id.addContacts);
        next = view.findViewById(R.id.next);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Contacts");

        firebaseContactAdder = new HashMap<>();

        keyArrayList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder = new AlertDialogBuilder(builder);
        setRecyclerView();

        /** Since startActivityForResult has been deprecated used ActivityResultLauncher
         * to access the contacts. Getting the selected contact in the onActivityResult method.
         * Cant add more then 5 emergency contacts. */
        ActivityResultLauncher<Intent> pickContactActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            contactPicked(data);
                        }
                    }
                }
        );


        // Adding a contact to the application.
        addSecondaryContact.setOnClickListener(view -> {
            if (totalContactsAdded < 5) {

                //Starting the intent for accessing the devices contact.
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                pickContactActivity.launch(intent);
            } else {

                // Showing error message to the user.
                alertDialog = alertDialogBuilder.setContactError("You cannot add more then 5 contacts",
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


            //Interface reference to the MainActivity.java file.
            handler.addSecondaryContacts(user, primaryContactDetails, firebaseContactAdder);

        });

        return view;

    }

    // Updating the user object once this activity is created.
    // This is done to insert the data of the previous activity to this so that the all the data
    // could be stored into the fire base when the user setup is complete.
    public void updateUserObjectInfo(Users user, ArrayList<ContactDetails> primaryContactDetails) {
        this.user = user;
        this.primaryContactDetails = primaryContactDetails;
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

            if (contactName.equals(primaryContactDetails.get(0).getContactName())) {
                AlertDialog alertDialog = alertDialogBuilder.setContactError(
                        "This contact is already a Primary Contact. Please chose another contact"
                        , "Primary Contact");
                alertDialog.show();

            }
            else if (!firebaseContactAdder.containsKey(contactName)) {
                keyArrayList.add(contactName);
                firebaseContactAdder.put(contactName, phoneNumber);

                // Adding the new object of the PrimaryContactDetails class to the arraylist.
                contactDetailsList.add(new ContactDetails(contactName, phoneNumber));

                // Notifying the adapter that a new change has been made.
                adapter.notifyItemInserted(adapter.getItemCount());
                totalContactsAdded++;
            } else {
                AlertDialog alertDialog = alertDialogBuilder.setContactError("This contact already exists", "Contact Exists");
                alertDialog.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adding values to the class object.


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
                firebaseContactAdder.remove(keyArrayList.get(viewHolder.getAdapterPosition()));
                contactDetailsList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                totalContactsAdded--;
            }
        }).attachToRecyclerView(recyclerView);


        // Setting the adapter to the recyclerView
        recyclerView.setAdapter(adapter);
    }
}