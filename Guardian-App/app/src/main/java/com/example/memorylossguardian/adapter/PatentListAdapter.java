package com.example.memorylossguardian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorylossguardian.R;
import com.example.memorylossguardian.database.table.GuardianPatients;
import com.example.memorylossguardian.userpage.UserPageHandler;

import java.util.List;

public class PatentListAdapter extends RecyclerView.Adapter<PatentListAdapter.ViewHolder> {

    List<GuardianPatients> patientsList;
    UserPageHandler handler;

    public PatentListAdapter(UserPageHandler handler) {
        this.handler = handler;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.patient_list_card_view,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GuardianPatients currentPatient = patientsList.get(position);

        holder.name.setText(currentPatient.getName());
        holder.patientCard.setOnClickListener(view -> {
            handler.onClickListener(currentPatient);
        });

    }

    @Override
    public int getItemCount() {
        if (patientsList != null) {
            return patientsList.size();
        }
        return 0;
    }

    public void setPatientsList(List<GuardianPatients> patientsList) {
        this.patientsList = patientsList;
        notifyDataSetChanged();
    }

    public GuardianPatients getPatientAt(int position){
        return patientsList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CardView patientCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.patientName);
            patientCard = itemView.findViewById(R.id.patientCard);
        }
    }
}
