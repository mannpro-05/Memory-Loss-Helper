package com.example.memory_loss_app.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memory_loss_app.R;
import com.example.memory_loss_app.contacts.ContactDetails;
import com.example.memory_loss_app.database.table.PrimaryContactDB;
import com.example.memory_loss_app.database.table.SecondaryContactDB;
import com.example.memory_loss_app.framelayoutmanager.UserPageHandler;

import java.util.ArrayList;
import java.util.List;

public class ContactDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public void setContactDetails(ArrayList<ContactDetails> contactDetails) {
        this.contactDetails = contactDetails;
        notifyDataSetChanged();
    }

    private ArrayList<ContactDetails> contactDetails;
    private List<PrimaryContactDB> primaryContactDBList;

    public void setSecondaryContactDBList(List<SecondaryContactDB> secondaryContactDBList) {
        this.secondaryContactDBList = secondaryContactDBList;
        notifyDataSetChanged();
    }

    private List<SecondaryContactDB> secondaryContactDBList;
    public static final int VIEW_TYPE_ADD_CONTACT = 1;
    public static final int VIEW_TYPE_DISPLAY_CONTACT = 2;
    private int viewType;
    UserPageHandler handler;
    public ContactDisplayAdapter(ArrayList<ContactDetails> contactDetails, int viewType) {
        this.contactDetails = contactDetails;
        this.viewType = viewType;
    }

    public ContactDisplayAdapter(int viewType, UserPageHandler handler) {
        this.viewType = viewType;
        this.handler = handler;
    }

    public ContactDisplayAdapter(ArrayList<ContactDetails> contactDetails, int viewType, UserPageHandler handler) {
        this.contactDetails = contactDetails;
        this.viewType = viewType;
        this.handler = handler;
    }

    public ContactDisplayAdapter(int viewType){
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_layout, parent, false);

        if (this.viewType == VIEW_TYPE_ADD_CONTACT)
        {
            return new ViewHolder(v);
        }
        else{
            return new ClickableViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder && (primaryContactDBList == null
                && secondaryContactDBList == null)){
            ViewHolder addContacts = (ViewHolder) holder;
            ContactDetails details = contactDetails.get(position);
            addContacts.name.setText("Contact Name: "+details.getContactName());
            addContacts.number.setText("Contact Number: "+details.getContactNumber());
        }
        else if(holder instanceof ViewHolder && primaryContactDBList != null){
            ViewHolder addContacts = (ViewHolder) holder;
            PrimaryContactDB currentContact = primaryContactDBList.get(position);
            addContacts.name.setText("Contact Name: "+currentContact.getContactName());
            addContacts.number.setText("Contact Number: "+currentContact.getContactNumber());
        }

        else if(holder instanceof ViewHolder && secondaryContactDBList != null){
            ViewHolder addContacts = (ViewHolder) holder;
            SecondaryContactDB currentContact = secondaryContactDBList.get(position);
            addContacts.name.setText("Contact Name: "+currentContact.getContactName());
            addContacts.number.setText("Contact Number: "+currentContact.getContactNumber());
        }

        else if (holder instanceof ClickableViewHolder && primaryContactDBList != null){
            ClickableViewHolder clickableViewHolder = (ClickableViewHolder) holder;
            PrimaryContactDB currentContact = primaryContactDBList.get(position);
            clickableViewHolder.name.setText("Contact Name: "+currentContact.getContactName());
            clickableViewHolder.number.setText("Contact Number: "+currentContact.getContactNumber());
            clickableViewHolder.cardView.setOnClickListener(view -> {
                handler.onClickListener(currentContact.getContactNumber());
            });

        }
        else if (holder instanceof  ClickableViewHolder && secondaryContactDBList!= null){
            ClickableViewHolder clickableViewHolder = (ClickableViewHolder) holder;
            SecondaryContactDB currentContact = secondaryContactDBList.get(position);
            clickableViewHolder.name.setText("Contact Name: "+currentContact.getContactName());
            clickableViewHolder.number.setText("Contact Number: "+currentContact.getContactNumber());
            clickableViewHolder.cardView.setOnClickListener(view -> {
                handler.onClickListener(currentContact.getContactNumber());
            });
        }
    }



    @Override
    public int getItemCount() {
        if (contactDetails == null && primaryContactDBList!=null){
            return primaryContactDBList.size();
        }
        else if(secondaryContactDBList!=null){
            return secondaryContactDBList.size();
        }
        else if (contactDetails == null){
            return 0;
        }
        else {
            return contactDetails.size();
        }

    }

    public void setPrimaryContactDBList(List<PrimaryContactDB> primaryContactDBList) {
        this.primaryContactDBList = primaryContactDBList;
        notifyDataSetChanged();
    }

    public SecondaryContactDB getContact(int position) {
        return secondaryContactDBList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,number;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            number = itemView.findViewById(R.id.contactNumber);
        }
    }

    public class ClickableViewHolder extends RecyclerView.ViewHolder{
        TextView name,number;
        CardView cardView;
        public ClickableViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            number = itemView.findViewById(R.id.contactNumber);
            cardView = itemView.findViewById(R.id.primaryContactCard);
        }
    }
}
