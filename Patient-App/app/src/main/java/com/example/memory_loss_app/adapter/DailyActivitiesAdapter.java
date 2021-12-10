package com.example.memory_loss_app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.database.table.DailyActivityDB;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;


import android.view.View;
import android.widget.TextView;


import java.util.List;

public class DailyActivitiesAdapter extends RecyclerView.Adapter<DailyActivitiesAdapter.ViewModel> {

    public List<DailyActivityDB> getDailyActivityDBS() {
        return dailyActivityDBS;
    }

    private List<DailyActivityDB> dailyActivityDBS;
    UserPageHandler handler;

    public DailyActivitiesAdapter(UserPageHandler handler) {
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.daily_activity_card,
                parent,
                false
        );
        return new ViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewModel holder, int position) {
        DailyActivityDB currentActivity = dailyActivityDBS.get(position);
        holder.title.setText(currentActivity.getTitle());
        holder.description.setText(currentActivity.getDescription());
        holder.cardView.setOnClickListener(view -> {

            handler.dailyActivityEditor(currentActivity.getId(),currentActivity.getTitle(),currentActivity.getDescription(), this);
        });
    }

    @Override
    public int getItemCount() {
        if (dailyActivityDBS!=null)
        {
            return dailyActivityDBS.size();
        }
        return 0;

    }

    public DailyActivityDB getDailyActivityAt(int position){
        return dailyActivityDBS.get(position);
    }

    public void setDailyActivityDBS(List<DailyActivityDB> dailyActivityDBS) {
        this.dailyActivityDBS = dailyActivityDBS;
        notifyDataSetChanged();
    }

    public class ViewModel extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        TextView description;
        public ViewModel(@NonNull android.view.View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            cardView = itemView.findViewById(R.id.dailyActivity);
            cardView.setBackgroundResource(R.drawable.daily_activity_card_design);
        }

    }
}
