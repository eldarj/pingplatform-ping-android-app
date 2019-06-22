package com.eldarja.ping.domains.chat.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eldarja.ping.R;
import com.eldarja.ping.domains.login.dtos.ContactDto;

import java.util.List;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder> {
    private ContactDto[] dataset;

    public ContactsRecyclerAdapter(ContactDto[] dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ContactsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout root = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_contact_list_item, parent, false);

        return new ContactsRecyclerAdapter.ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsRecyclerAdapter.ViewHolder holder, int position) {
        holder.textContactName.setText(dataset[position].getContactName());
        holder.textContactPhoneNumber.setText(dataset[position].getContactPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout contactListRoot;
        private TextView
                textContactName,
                textContactPhoneNumber;

        ViewHolder(LinearLayout root) {
            super(root);

            contactListRoot = root;
            textContactName = root.findViewById(R.id.textContactName);
            textContactPhoneNumber = root.findViewById(R.id.textContactPhoneNumber);
        }
    }
}