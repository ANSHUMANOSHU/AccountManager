package com.example.accountmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.accountmanager.R;
import com.example.accountmanager.entity.FalseLogger;

import java.util.ArrayList;

public class LogAttemptsAdapter extends RecyclerView.Adapter<LogAttemptsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FalseLogger> falseLoggers = new ArrayList<>();

    public LogAttemptsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(context).inflate(R.layout.log_attempts_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(falseLoggers.get(position).stamp);
        holder.textView.setSelected(true);
        Glide.with(context).asBitmap().load(falseLoggers.get(position).image).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO-----------------Show Bigger Image
            }
        });

    }

    @Override
    public int getItemCount() {
        return falseLoggers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.personImage);
            textView = itemView.findViewById(R.id.capturedAtStamp);
        }
    }

    public void setFalseLoggers(ArrayList<FalseLogger> falseLoggers) {
        this.falseLoggers = falseLoggers;
        notifyDataSetChanged();
    }
}
