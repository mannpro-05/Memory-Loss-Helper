package com.example.memory_loss_app.userpage.dailyactivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.adapter.DailyActivitiesAdapter;
import com.example.memory_loss_app.database.viewmodel.DailyActivitiesViewModel;
import com.example.memory_loss_app.database.table.DailyActivityDB;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DailyActivity extends Fragment {
    View view;
    FirebaseAuth mAuth;
    UserPageHandler handler;
    RecyclerView recyclerView;
    DailyActivitiesAdapter adapter;
    FloatingActionButton addDailyActivity;
    DailyActivitiesViewModel dailyActivitiesViewModel;
    DatabaseReference reference;
    SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_daily_activity, container, false);
        setRecyclerView();
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("DailyActivity/" + mAuth.getCurrentUser().getUid());
        dailyActivitiesViewModel = new ViewModelProvider(getActivity()).get(DailyActivitiesViewModel.class);
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.refreshDailyActivity);
        addDailyActivity = view.findViewById(R.id.addDailyActivity);
        dailyActivitiesViewModel.getDailyActivities().observe(getActivity(),
                dailyActivityDBS ->
                        adapter.setDailyActivityDBS(dailyActivityDBS));

        addDailyActivity.setOnClickListener(view1 -> {
            handler.dailyActivityAdder(adapter);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {

            dailyActivitiesViewModel.deleteAll();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot i : snapshot.getChildren()) {
                        String title = "";
                        String description = "";
                        for (DataSnapshot j : i.getChildren()) {
                            if(j.getKey().equals("title"))
                                title = j.getValue().toString();
                            else
                                description = j.getValue().toString();
                        }
                        dailyActivitiesViewModel.insert(new DailyActivityDB(i.getKey(), title, description));
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
        if (context instanceof UserPageHandler) {
            handler = (UserPageHandler) context;
        }
    }

    void setRecyclerView() {
        recyclerView = view.findViewById(R.id.dailyActivityDisplay);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new DailyActivitiesAdapter(handler);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                DailyActivityDB dailyActivityDB = adapter.getDailyActivityAt(viewHolder.getAdapterPosition());
                reference.child(dailyActivityDB.getId()).removeValue();
                dailyActivitiesViewModel.delete(dailyActivityDB);
            }
        }).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }

}