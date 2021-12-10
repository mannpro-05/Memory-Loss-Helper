package com.example.memory_loss_app.userpage.dailyactivity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.adapter.DailyActivitiesAdapter;
import com.example.memory_loss_app.database.viewmodel.DailyActivitiesViewModel;
import com.example.memory_loss_app.database.table.DailyActivityDB;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditDailyActivity extends Fragment {

    boolean editButton, addButton;
    View view;
    EditText title;
    EditText description;
    DailyActivity dailyActivity;
    Button edit;
    Button add;
    UserPageHandler handler;
    DailyActivitiesViewModel dailyActivitiesViewModel;
    DailyActivitiesAdapter adapter;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    String editTile;
    String editDescription;
    String id;

    public EditDailyActivity(){

    }

    public EditDailyActivity(String id, String title, String description, boolean editButton, boolean addButton, DailyActivitiesAdapter adapter) {
        this.id = id;
        this.editTile = title;
        this.editDescription = description;
        this.addButton = addButton;
        this.editButton = editButton;
        this.adapter = adapter;
    }

    public EditDailyActivity(boolean editButton, boolean addButton, DailyActivitiesAdapter adapter) {
        this.adapter = adapter;
        this.addButton = addButton;
        this.editButton = editButton;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_daily_activity, container, false);
        dailyActivitiesViewModel = new ViewModelProvider(getActivity()).get(DailyActivitiesViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("DailyActivity/" + mAuth.getCurrentUser().getUid());
        adapter = new DailyActivitiesAdapter(handler);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.editTitle);
        description = view.findViewById(R.id.editDescription);
        edit = view.findViewById(R.id.edit);
        add = view.findViewById(R.id.add);
        dailyActivity = new DailyActivity();
        if (addButton) {
            add.setVisibility(View.VISIBLE);
            add.setEnabled(addButton);
            edit.setEnabled(editButton);
            edit.setVisibility(View.INVISIBLE);
        } else {
            add.setVisibility(View.INVISIBLE);
            add.setEnabled(addButton);
            edit.setEnabled(editButton);
            edit.setVisibility(View.VISIBLE);
            title.setText(this.editTile);
            description.setText(this.editDescription);

        }

        add.setOnClickListener(view1 -> {
            String uniqueId = reference.push().getKey();
            DailyActivityDB dailyActivityDB = new DailyActivityDB(uniqueId,title.getText().toString()
                    , description.getText().toString());
            dailyActivitiesViewModel.insert(dailyActivityDB);
            reference.child(uniqueId).child("title")
                    .setValue(title.getText().toString());
        reference.child(uniqueId).child("description")
                            .setValue(description.getText().toString());
            handler.dashboard();
        });

        edit.setOnClickListener(view1 -> {
            reference.child(id).child("title").setValue(title.getText().toString());
            reference.child(id).child("description").setValue(description.getText().toString());
            dailyActivitiesViewModel.update(new DailyActivityDB(id, title.getText().toString(), description.getText().toString()));
            handler.dashboard();
        });
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserPageHandler) {
            handler = (UserPageHandler) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }
}