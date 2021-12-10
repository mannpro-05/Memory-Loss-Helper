package com.example.memory_loss_app.userpage.primarycontact;

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
import com.example.memory_loss_app.database.viewmodel.PrimaryContactViewModel;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PrimaryContactDisplay extends Fragment {
    RecyclerView recyclerView;
    ContactDisplayAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    UserPageHandler handler;
    PrimaryContactViewModel primaryContactViewModel;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_primary_contact_display, container, false);
        setRecyclerView();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        primaryContactViewModel = new ViewModelProvider(getActivity()).get(PrimaryContactViewModel.class);

        primaryContactViewModel.getPrimaryContact().observe(getActivity(), new Observer<List<PrimaryContactDB>>() {
            @Override
            public void onChanged(List<PrimaryContactDB> primaryContactDBS) {
                adapter.setPrimaryContactDBList(primaryContactDBS);
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.refreshPrimaryContact);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                primaryContactViewModel.deleteAllNotes();
                reference.child("Contacts/" + mAuth.getCurrentUser().getUid() + "/Primary").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ContactDetails primaryContact = snapshot.getValue(ContactDetails.class);
                        primaryContactViewModel.insert(new PrimaryContactDB(primaryContact.getContactName(), primaryContact.getContactNumber()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }

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
        recyclerView = view.findViewById(R.id.primaryContactDisplay);
        adapter = new ContactDisplayAdapter(
                ContactDisplayAdapter.VIEW_TYPE_DISPLAY_CONTACT, handler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }


}