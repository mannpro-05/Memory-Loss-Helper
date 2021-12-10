package com.example.memory_loss_app.userpage.secondarycontact;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.adapter.ContactDisplayAdapter;
import com.example.memory_loss_app.contacts.ContactDetails;
import com.example.memory_loss_app.database.viewmodel.SecondaryContactsViewModel;
import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SecondaryContactDisplay extends Fragment {
    RecyclerView recyclerView;
    ContactDisplayAdapter adapter;
    ArrayList<ContactDetails> contactDetails;
    View view;
    UserPageHandler handler;
    BottomNavigationView navigationView;
    SecondaryContactsViewModel secondaryContactsViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_secondary_contact_display, container, false);
        navigationView = getActivity().findViewById(R.id.nav_view);
        swipeRefreshLayout = view.findViewById(R.id.refreshSecondaryContact);
        setRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        secondaryContactsViewModel = new ViewModelProvider(getActivity()).get(SecondaryContactsViewModel.class);

        secondaryContactsViewModel.getAllSecondaryContacts().observe(getActivity(), new Observer<List<SecondaryContactDB>>() {
            @Override
            public void onChanged(List<SecondaryContactDB> secondaryContactDBS) {
                adapter.setSecondaryContactDBList(secondaryContactDBS);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(()-> {
            secondaryContactsViewModel.deleteAllNotes();
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

            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserPageHandler){
            handler = (UserPageHandler) context;
        }
    }
    public void setRecyclerView(){
        recyclerView = view.findViewById(R.id.secondaryContactDisplay);
        adapter = new ContactDisplayAdapter(contactDetails, ContactDisplayAdapter.VIEW_TYPE_DISPLAY_CONTACT, handler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}