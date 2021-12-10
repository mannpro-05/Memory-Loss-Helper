package com.example.memory_loss_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;
import com.example.memory_loss_app.userpage.home.HomePagePopulate;

import java.util.ArrayList;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.UserDetailsHolder> {

    private ArrayList<HomePagePopulate> homePagePopulate;


    UserPageHandler handler;

    public HomePageAdapter(ArrayList<HomePagePopulate> homePagePopulate, UserPageHandler handler) {
        this.homePagePopulate = homePagePopulate;
        this.handler = handler;
    }


    @NonNull
    @Override
    public UserDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.home_page_user_details_cardview,
                    parent,
                    false);
            return new UserDetailsHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailsHolder holder, int position) {
        HomePagePopulate currentItem = homePagePopulate.get(position);
        if (currentItem.getKeyword().equals("Emergency Contact")){
            holder.cardView.setBackgroundResource(R.drawable.daily_activity_card_design);
            holder.cardView.setOnClickListener(view -> {
                handler.onClickListener(holder.value.getText().toString());
            });
        }
        holder.icon.setImageResource(currentItem.getIcon());
        holder.keyword.setText(currentItem.getKeyword());
        holder.value.setText(currentItem.getValue());
    }


    @Override
    public int getItemCount() {
        return homePagePopulate.size();
    }


    class UserDetailsHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView keyword;
        TextView value;
        CardView cardView;
        public UserDetailsHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            keyword = itemView.findViewById(R.id.keyword);
            value = itemView.findViewById(R.id.value);
            cardView = itemView.findViewById(R.id.homePageCardView);
        }
    }

}
