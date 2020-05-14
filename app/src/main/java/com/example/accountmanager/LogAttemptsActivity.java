package com.example.accountmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.accountmanager.Entity.FalseLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class LogAttemptsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LogAttemptsAdapter adapter;
    private ArrayList<FalseLogger> falseLoggers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_attempts);

        //init Views
        falseLoggers = new ArrayList<>();
        recyclerView = findViewById(R.id.falseloggerRecycler);
        adapter = new LogAttemptsAdapter(this);
        adapter = new LogAttemptsAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);

        fetchAllFalseLoggersImages(); //fetch images of false loggers

    }

    //fetching false loggers images
    private void fetchAllFalseLoggersImages() {
        falseLoggers.clear();
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+MainActivity.STORAGE_NAME+"/");
        if(path.exists()){
            if (path.listFiles() != null){
                for (File f : Objects.requireNonNull(path.listFiles())){
                    if (f.isFile()){
                        if (f.getName().toLowerCase().contains(".jpeg")){
                            FalseLogger falseLogger = new FalseLogger(f.getAbsolutePath(),f.getName().substring(0,f.getName().length()-5));
                            falseLoggers.add(falseLogger);
                        }
                    }
                }
            }
            adapter.setFalseLoggers(falseLoggers);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.APP_OPENED = false;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.clearAll:
                clearAllLogs();
                break;
        }
        return super.onOptionsItemSelected(item);
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
                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+MainActivity.STORAGE_NAME+"/");
                if(path.exists()){
                    if (path.listFiles() != null){
                        for (File f : Objects.requireNonNull(path.listFiles())){
                            if (f.isFile()){
                                if (f.getName().toLowerCase().contains(".jpeg")){
                                    if (f.delete())
                                        count++;
                                }
                            }
                        }
                        fetchAllFalseLoggersImages();
                        Toast.makeText(LogAttemptsActivity.this, "Logs Deleted = "+count, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }
}
