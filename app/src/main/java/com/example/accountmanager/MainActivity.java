package com.example.accountmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.HiddenCameraActivity;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.example.accountmanager.Entity.BackupRestore;
import com.example.accountmanager.Entity.EntityAccount;
import com.example.accountmanager.Entity.OnBackupListener;
import com.example.accountmanager.Entity.OnRestoreListener;
import com.example.accountmanager.Entity.SqliteHelperAccounts;
import com.example.accountmanager.Entity.SqliteHelperPassword;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends HiddenCameraActivity implements OnBackupListener, OnRestoreListener {

    private RecyclerView recyclerView;
    private SavedAccountsAdapter adapter;
    private ArrayList<EntityAccount> entities;
    private ArrayList<EntityAccount> temp = new ArrayList<>();
    private CameraConfig cameraConfig;
    Dialog dialog;
    public static boolean APP_OPENED = true;
    public static final String STORAGE_NAME = "AccountManagerPhotos";
    private SqliteHelperAccounts sqliteHelperAccounts;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    public static final int RQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //permissions check
        if (!checkforPermission()) {
            requestPermission();
        } else {

            if (APP_OPENED) {
                cameraConfig = new CameraConfig();
                initCamera(); //inititalize Camera for false logger
                checkForPinSecurity();  //verify user
            } else {
                setContentView(R.layout.activity_main);
                startApp();
            }
        }
    }

    //verifying user
    private void checkForPinSecurity() {

        //check for valid password
        SqliteHelperPassword sqliteHelperPassword = new SqliteHelperPassword(this);
        final String pin = sqliteHelperPassword.fetch();
        if (!pin.isEmpty()) {

            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            //inflate layout and manage views
            View view = LayoutInflater.from(this).inflate(R.layout.entry_password_layout, null, false);
            final EditText text = view.findViewById(R.id.securityPassword);
            final TextView textView = view.findViewById(R.id.invalidPasswordText);
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().isEmpty() && s.toString().length() == 6) {
                        if (pin.equals(s.toString())) {
                            setContentView(R.layout.activity_main);
                            dialog.dismiss();
                            //stopCamera();
                            cameraConfig = null;
                            startApp();
                        } else {
                            text.setText("");
                            takePicture();    //take falseLogger Picture
                            textView.setVisibility(View.VISIBLE);   //make invalid user text visible
                        }
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                }
            });

            dialog.setContentView(view);  //set layout view to dialog
            dialog.setCanceledOnTouchOutside(false); //cancel only on back press
            //finish the activity when dialog is cancelled
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });

            dialog.show();                //display dailog

        } else {
            sqliteHelperPassword.insert("000000");
            setContentView(R.layout.activity_main);
            //stopCamera();
            cameraConfig = null;
            startApp();
            Toast.makeText(this, "Default Password : 000000", Toast.LENGTH_SHORT).show();
        }

    }

    private void initCamera() {

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + STORAGE_NAME);

        if (!file.exists())
            file.mkdirs();

        file = new File(file.getAbsolutePath() + "/" + getformatedDateAndTime() + ".jpeg");

        cameraConfig.getBuilder(this)
                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_270)
                .setImageFile(file)
                .build();

        startCamera(cameraConfig); //starting camera
    }

    private String getformatedDateAndTime() {

        String[] date = new Date().toString().split(" ");
        date[0] = date[2] + "_" + date[1] + "_" + date[5] + "_" + date[3];

        return date[0];
    }

    @Override
    protected void onUserLeaveHint() {
        finish();
    }


    private void startApp() {
        //init recycler view and adapter
        recyclerView = findViewById(R.id.recyclerSavedPasswords);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        entities = new ArrayList<>();
        adapter = new SavedAccountsAdapter(this);
        recyclerView.setAdapter(adapter);
        //---------------------------------------------

        sqliteHelperAccounts = new SqliteHelperAccounts(this);
        fetchAllSavedAccounts(); // To fetch all saved accounts
    }

    //fetch all saved accounts from database
    private void fetchAllSavedAccounts() {
        entities.clear();

        entities.addAll(sqliteHelperAccounts.fetchAccounts());

        adapter.setEntities(entities);
    }


    // button to add new account
    public void addAccountClicked(View view) {
        Intent intent = new Intent(this, AddNewAccountActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_layout, menu);

        MenuItem item = menu.findItem(R.id.search_bar);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if ("".equals(newText)) {
                    adapter.setEntities(entities);
                    return false;
                }
                temp.clear();
                for (EntityAccount entityAccount : entities) {
                    if (entityAccount.website.toLowerCase().contains(newText.toLowerCase())
                            || entityAccount.notes.toLowerCase().contains(newText.toLowerCase())
                            || entityAccount.username.toLowerCase().contains(newText.toLowerCase())
                            || entityAccount.web_url.toLowerCase().contains(newText.toLowerCase())) {

                        temp.add(entityAccount);
                    }
                }

                adapter.setEntities(temp);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backupRestore:
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AccountsBackup");
                if (!file.exists())
                    file.mkdirs();
                BackupRestore restore = new BackupRestore(this);
                restore.setBackUpCompleteListener(this);
                restore.setRestoreCompleteListener(this);
                restore.start();
                break;

            case R.id.about:
                diplayDevelopers();
                break;

            case R.id.changePassword:
                changePassword(); //change entry password
                break;

            case R.id.logAttempts:
                Intent intent = new Intent(this, LogAttemptsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    //display developers
    private void diplayDevelopers() {
        //inflate developers view
        View view = LayoutInflater.from(this).inflate(R.layout.developers, null, false);

        //display in alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create().show();
    }

    //change password of user
    private void changePassword() {

        final SqliteHelperPassword sqliteHelperPassword = new SqliteHelperPassword(this);
        final String oldSavedPassword = sqliteHelperPassword.fetch();

        //inflate change password
        View view = LayoutInflater.from(this).inflate(R.layout.change_password, null, false);

        //display in alert dailog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        final EditText old = view.findViewById(R.id.old_password);
        final EditText newpass = view.findViewById(R.id.new_password);
        final EditText confirm = view.findViewById(R.id.confirm_new__password);
        final TextView matchMisMatch = view.findViewById(R.id.matchMisMatch);

        //dynamic checking for old password
        old.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && s.toString().length() == 6) {
                    if (oldSavedPassword.equals(s.toString())) {
                        newpass.requestFocus();
                    } else {
                        old.setText("");
                        matchMisMatch.setText("Pin Mismatched");
                        matchMisMatch.setTextColor(Color.RED);
                        matchMisMatch.setVisibility(View.VISIBLE);   //make invalid user text visible
                    }
                } else {
                    matchMisMatch.setVisibility(View.GONE);
                }
            }
        });

        old.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                newpass.setText("");
                confirm.setText("");
                matchMisMatch.setVisibility(View.GONE);
            }
        });

        newpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6) {
                    confirm.requestFocus();
                }
            }
        });


        //dynamic listen to completion of password
        confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (confirm.getText().toString().length() <= newpass.getText().toString().length()) {
                    if (!newpass.getText().toString().substring(0, confirm.getText().toString().length())
                            .equals(confirm.getText().toString())) {

                        matchMisMatch.setText("Password Mismatch");
                        matchMisMatch.setTextColor(Color.RED);
                        matchMisMatch.setVisibility(View.VISIBLE);

                    } else if (confirm.getText().toString().length() == newpass.getText().toString().length()
                            && newpass.getText().toString().equals(confirm.getText().toString())) {

                        matchMisMatch.setText("Password Matched");
                        matchMisMatch.setTextColor(Color.GREEN);
                        matchMisMatch.setVisibility(View.VISIBLE);
                    } else
                        matchMisMatch.setVisibility(View.GONE);
                } else {
                    matchMisMatch.setText("Password Mismatch");
                    matchMisMatch.setTextColor(Color.RED);
                    matchMisMatch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sqliteHelperPassword.insert(confirm.getText().toString());
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //when image is captures
    @Override
    public void onImageCapture(@NonNull File imageFile) {
        initCamera();  //initialize camera again for verifying user
        Toast.makeText(this, "running", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraError(int errorCode) {
    }

    @Override
    public void OnBackupResponse(String MESSAGE) {
        Toast.makeText(this, MESSAGE, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        APP_OPENED = true;
        finish();
    }

    @Override
    public void OnRestoreResponse(String MESSAGE) {
        Toast.makeText(this, MESSAGE, Toast.LENGTH_SHORT).show();
        fetchAllSavedAccounts();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, RQ);
    }

    private boolean checkforPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RQ) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {

                Toast.makeText(this, "Permission is Required ", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                if (APP_OPENED) {
                    cameraConfig = new CameraConfig();
                    initCamera(); //initialize Camera for false logger
                    checkForPinSecurity();  //verify user
                } else {
                    setContentView(R.layout.activity_main);
                    startApp();
                }
            }
        }
    }
}
