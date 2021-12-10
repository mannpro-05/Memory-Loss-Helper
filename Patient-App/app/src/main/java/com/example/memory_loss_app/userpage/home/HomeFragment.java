package com.example.memory_loss_app.userpage.home;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.adapter.HomePageAdapter;
import com.example.memory_loss_app.alertdialog.AlertDialogBuilder;
import com.example.memory_loss_app.database.viewmodel.PrimaryContactViewModel;
import com.example.memory_loss_app.database.viewmodel.UserViewModel;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.UserDetailsDB;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ImageView profilePicture;
    View view;
    AlertDialogBuilder dialogBuilder;
    android.app.AlertDialog.Builder builder;
    Bitmap bitmap;
    RecyclerView recyclerView;
    HomePageAdapter adapter;
    ArrayList<HomePagePopulate> homePagePopulates;
    UserPageHandler handler;
    UserViewModel userViewModel;
    PrimaryContactViewModel primaryContactViewModel;
    PrimaryContactDB primaryContactDB;
    UserDetailsDB userDetailsDB;


    public HomeFragment(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        primaryContactViewModel = new ViewModelProvider(getActivity()).get(PrimaryContactViewModel.class);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.homePageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        builder = new android.app.AlertDialog.Builder(getActivity());
        dialogBuilder = new AlertDialogBuilder(builder);
        profilePicture = view.findViewById(R.id.profilePicture);
        homePagePopulates = new ArrayList<>();
        adapter = new HomePageAdapter(homePagePopulates, handler);
        recyclerView.setAdapter(adapter);
        userViewModel.getAllNotes().observe(getActivity(), new Observer<List<UserDetailsDB>>() {
            @Override
            public void onChanged(List<UserDetailsDB> userDetailsDBS) {
                if (userDetailsDBS.size() > 0) {
                    userDetailsDB = userDetailsDBS.get(0);
                    homePagePopulates.add(new HomePagePopulate("Name", userDetailsDB.getName(), R.drawable.ic_person));
                    homePagePopulates.add(new HomePagePopulate("Date Of Birth", userDetailsDB.getDateOfBirth(), R.drawable.ic_date));
                    homePagePopulates.add(new HomePagePopulate("Blood Group", userDetailsDB.getBloodGroup(), R.drawable.ic_hospital));
                    homePagePopulates.add(new HomePagePopulate("Address", userDetailsDB.getAddress(), R.drawable.ic_location));
                    adapter.notifyDataSetChanged();
                    primaryContactViewModel.getPrimaryContact().observe(getActivity(), new Observer<List<PrimaryContactDB>>() {
                        @Override
                        public void onChanged(List<PrimaryContactDB> primaryContactDBS) {
                            if (primaryContactDBS.size() > 0) {
                                primaryContactDB = primaryContactDBS.get(0);
                                homePagePopulates.add(new HomePagePopulate("Emergency Contact", primaryContactDB.getContactNumber(), R.drawable.ic_phone));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }


            }
        });


        profilePicture.setImageBitmap(bitmap);

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserPageHandler)
            handler = (UserPageHandler) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler = null;
    }
}