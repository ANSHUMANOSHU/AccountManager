package com.example.accountmanager.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.accountmanager.R;
import com.example.accountmanager.entity.Intruder;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Intruder> intruders;

    public LogAdapter(Context context) {
        this.context = context;
    }

    public void setIntruders(ArrayList<Intruder> intruders) {
        this.intruders = intruders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.log_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView.setText(intruders.get(position).stamp);
        holder.textView.setSelected(true);

        Bitmap bitmap = BitmapFactory.decodeFile(intruders.get(position).image);
        holder.imageView.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return intruders == null ? 0 : intruders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.personImage);
            textView = itemView.findViewById(R.id.capturedAtStamp);
        }
    }

}
