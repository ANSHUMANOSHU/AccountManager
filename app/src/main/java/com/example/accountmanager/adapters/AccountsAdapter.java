package com.example.accountmanager.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accountmanager.R;
import com.example.accountmanager.activity.MainActivity;
import com.example.accountmanager.dialogs.ViewAccountDialog;
import com.example.accountmanager.entity.Account;
import com.example.accountmanager.utils.SQLiteHelperAccounts;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Account> accounts;

    public AccountsAdapter(Context context) {
        this.context = context;
    }

    public void setEntities(ArrayList<Account> entities) {
        this.accounts = entities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.stamp.setText(accounts.get(position).timeStamp);
        holder.stamp.setSelected(true);

        holder.website.setText(accounts.get(position).website);
        holder.website.setSelected(true);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewAccountDialog viewAccountDialog = new ViewAccountDialog(context, accounts.get(position));
                viewAccountDialog.show();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm");
                builder.setMessage("Do you want to delete account details ?");
                builder.setCancelable(false);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteHelperAccounts SQLiteHelperAccounts = new SQLiteHelperAccounts(context);
                        SQLiteHelperAccounts.delete(accounts.get(position));
                        ((MainActivity) context).fetchAllSavedAccounts();
                        dialog.dismiss();
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }

        });

    }

    @Override
    public int getItemCount() {
        return accounts == null ? 0 : accounts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView edit, delete;
        TextView stamp, website;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            stamp = itemView.findViewById(R.id.stamp);
            website = itemView.findViewById(R.id.webName);
        }
    }

}
