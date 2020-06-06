package com.example.accountmanager.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.accountmanager.R;
import com.example.accountmanager.adapters.LogAdapter;
import com.example.accountmanager.entity.Intruder;
import com.example.accountmanager.utils.GeneralHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LogViewer extends AppCompatActivity {

    private LogAdapter adapter;
    private ArrayList<Intruder> intruders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CREATE VIEW AND SET
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new LogAdapter(this);
        recyclerView.setAdapter(adapter);
        setContentView(recyclerView);

        //FETCH ALL LOGGERS AND SHOW
        fetchAll();

    }

    private void fetchAll() {
        intruders.clear();
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MainActivity.PHOTO_STORAGE_FOLDER + "/");

        if (GeneralHelper.isGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                path.exists()) {

            if (path.listFiles() != null) {

                for (File f : Objects.requireNonNull(path.listFiles())) {
                    if (f.isFile()) {

                        if (f.getName().toLowerCase().contains(".jpeg")) {
                            Intruder intruder = new Intruder(f.getAbsolutePath(), f.getName().substring(0, f.getName().length() - 5));
                            intruders.add(intruder);
                        }

                    }
                }
            }
            adapter.setIntruders(intruders);
        }
    }

    private void clearAllLogs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm")
                .setMessage("Do you want to clear all logs ?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int count = 0;

                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MainActivity.PHOTO_STORAGE_FOLDER + "/");
                if (path.exists()) {
                    if (path.listFiles() != null) {
                        for (File f : Objects.requireNonNull(path.listFiles())) {
                            if (f.isFile()) {
                                if (f.getName().toLowerCase().contains(".jpeg")) {
                                    if (f.delete())
                                        count++;
                                }
                            }
                        }
                        fetchAll();

                        Toast.makeText(LogViewer.this, "Logs Deleted = " + count, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clearAll) {
            clearAllLogs();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}
