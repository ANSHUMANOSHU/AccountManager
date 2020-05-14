package com.example.accountmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountmanager.Entity.DataTransferHelper;
import com.example.accountmanager.Entity.EntityAccount;
import com.example.accountmanager.Entity.SqliteHelperAccounts;

import java.util.ArrayList;

public class SavedAccountsAdapter extends RecyclerView.Adapter<SavedAccountsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EntityAccount> entities = new ArrayList<>();

    public SavedAccountsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.stamp.setText(entities.get(position).timeStamp);
        holder.stamp.setSelected(true);
        holder.website.setText(entities.get(position).website);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize transmission helper
                DataTransferHelper.entityAccount = entities.get(position);
                Intent intent = new Intent(context,AccountDetailsActivity.class);
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm").setMessage("Do you wawnt to delete account details ?");
                builder.setCancelable(false);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SqliteHelperAccounts sqliteHelperAccounts = new SqliteHelperAccounts(context);
                        if (sqliteHelperAccounts.deleteAccountInfo(entities.get(position))) {
                            Toast.makeText(context, "Deletion Successful...", Toast.LENGTH_SHORT).show();
                            entities.remove(entities.get(position));
                            notifyDataSetChanged();
                        } else
                            Toast.makeText(context, "Sorry! Deletion Failed...", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }

        });

    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView edit , delete;
        TextView stamp , website;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            stamp = itemView.findViewById(R.id.stamp);
            website = itemView.findViewById(R.id.webName);
        }
    }

    public void setEntities(ArrayList<EntityAccount> entities) {
        this.entities = entities;
        notifyDataSetChanged();
    }
}
