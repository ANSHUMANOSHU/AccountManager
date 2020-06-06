package com.example.accountmanager.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.HiddenCameraActivity;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.example.accountmanager.R;
import com.example.accountmanager.adapters.AccountsAdapter;
import com.example.accountmanager.dialogs.AddAccountDialog;
import com.example.accountmanager.entity.Account;
import com.example.accountmanager.utils.BackupRestore;
import com.example.accountmanager.utils.GeneralHelper;
import com.example.accountmanager.utils.SQLiteHelperAccounts;
import com.example.accountmanager.utils.SQLiteHelperPassword;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends HiddenCameraActivity {

    private static final String DEFAULT_PASSWORD = "000000";
    public static final String PHOTO_STORAGE_FOLDER = "Accounts/Intruders";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private AccountsAdapter adapter;
    private ArrayList<Account> entities = new ArrayList<>();
    private ArrayList<Account> temp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //PERMISSION CHECK AND ASK FOR USER
        if (!GeneralHelper.isGranted(this,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            //ASK FOR PERMISSION
            requestPermission();

        } else {
            //IF ALREADY GRANTED
            initCamera();
            startSecurityModule();
        }
    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_REQUEST_CODE);
        Toast.makeText(this, "Application Will not start till Permissions are Granted", Toast.LENGTH_SHORT).show();
    }

    private void initCamera() {

        //CHECK IF PERMISSION NOT GRANTED THEN RETURN
        if (!GeneralHelper.isGranted(this, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE))
            return;

        //CREATE FOLDER AND FILE POINTERS
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PHOTO_STORAGE_FOLDER);
        File photo = new File(file.getAbsolutePath() + "/" + GeneralHelper.getFormattedDateTime() + ".jpeg");

        //CREATE FOLDER IF NOT EXISTS
        if (!file.exists()) {
            file.mkdirs();
        }

        //START CAMERA
        CameraConfig cameraConfig = new CameraConfig().getBuilder(this)
                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_270)
                .setImageFile(photo)
                .build();

        startCamera(cameraConfig);

    }

    @SuppressLint("InflateParams")
    private void startSecurityModule() {

        SQLiteHelperPassword sqliteHelperPassword = new SQLiteHelperPassword(this);
        final String actualPIN = sqliteHelperPassword.fetch();

        //IF USER IS NOT NEW (REQUIRES PASSWORD
        if (!actualPIN.isEmpty()) {

            //MAKE DIALOG
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //REQUIRED FOR SECURITY REASONS
                    finish();
                }
            });

            //inflate layout and manage views
            View view = LayoutInflater.from(this).inflate(R.layout.ask_password, null, false);
            final EditText passwordET = view.findViewById(R.id.securityPassword);
            passwordET.requestFocus();
            final TextView invalidPasswordTV = view.findViewById(R.id.invalidPasswordText);
            passwordET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    //IF NOT EMPTY AND LENGTH IS SIX
                    if (!s.toString().isEmpty() && s.toString().length() == 6) {

                        //PIN MATCHES THEN START APP
                        if (actualPIN.equals(s.toString())) {
                            setContentView(R.layout.activity_main);
                            startApp();
                            dialog.dismiss();
                        } else {
                            //ELSE TAKE PICTURE AND SHOW UI
                            takePicture();
                            passwordET.setText("");
                            invalidPasswordTV.setVisibility(View.VISIBLE);
                        }

                    } else {
                        //IF USER RESTART WRITING PASSWORD THE INVISIBLE WARNING
                        invalidPasswordTV.setVisibility(View.INVISIBLE);
                    }
                }
            });

            //SET VIEW AND SHOW DIALOG
            dialog.setContentView(view);
            dialog.show();

        } else {
            sqliteHelperPassword.insert(DEFAULT_PASSWORD);
            saveDefaultAccounts();
            setContentView(R.layout.activity_main);
            startApp();
        }

    }

    private void saveDefaultAccounts() {
        SQLiteHelperAccounts accounts = new SQLiteHelperAccounts(this);

        Account account1 = new Account("Gmail",
                new Date().toString(),
                "Username",
                "Password",
                "You Can Delete This Demo Account",
                "https://www.gmail.com");

        Account account2 = new Account("Facebook",
                new Date().toString(),
                "Username",
                "Password",
                "You Can Delete This Demo Account",
                "https://www.facebook.com");

        Account account3 = new Account("Yahoo",
                new Date().toString(),
                "Username",
                "Password",
                "You Can Delete This Demo Account",
                "https://www.yahoo.com");

        Account account4 = new Account("Outlook",
                new Date().toString(),
                "Username",
                "Password",
                "You Can Delete This Demo Account",
                "https://www.outlook.com");

        accounts.insert(account1);
        accounts.insert(account2);
        accounts.insert(account3);
        accounts.insert(account4);
    }

    @Override
    protected void onUserLeaveHint() {
        finish();
    }

    private void startApp() {

        //INIT VIEWS
        RecyclerView recyclerView = findViewById(R.id.recyclerSavedPasswords);
        ImageView addAccount = findViewById(R.id.addAccount);

        //SET ONCLICK LISTENER
        addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAccountDialog addAccountDialog = new AddAccountDialog(MainActivity.this);
                addAccountDialog.show();
            }
        });

        //CREATE OBJECTS
        adapter = new AccountsAdapter(this);


        //SET RECYCLER VIEW PROPERTIES
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //FETCH ALL ACCOUNT INFO
        fetchAllSavedAccounts();
    }

    public void fetchAllSavedAccounts() {
        entities.clear();
        SQLiteHelperAccounts SQLiteHelperAccounts = new SQLiteHelperAccounts(this);
        entities.addAll(SQLiteHelperAccounts.fetch());
        adapter.setEntities(entities);
    }

    public void addAccountClicked(View view) {
        //TODO :
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
                for (Account account : entities) {
                    if (account.website.toLowerCase().contains(newText.toLowerCase())
                            || account.notes.toLowerCase().contains(newText.toLowerCase())
                            || account.username.toLowerCase().contains(newText.toLowerCase())
                            || account.web_url.toLowerCase().contains(newText.toLowerCase())) {

                        temp.add(account);
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
                BackupRestore restore = new BackupRestore(this);
                restore.start();
                break;
            case R.id.about:
                startDevelopersDialog();
                break;
            case R.id.changePassword:
                startChangePasswordDialog();
                break;
            case R.id.logAttempts:
                Intent intent = new Intent(this, LogViewer.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @SuppressLint("InflateParams")
    private void startDevelopersDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.developers, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create().show();
    }

    @SuppressLint("InflateParams")
    private void startChangePasswordDialog() {

        //SHOW VIEW IN DIALOG
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //FETCH OLD SAVED PASSWORD
        final SQLiteHelperPassword sqliteHelperPassword = new SQLiteHelperPassword(this);
        final String oldSavedPassword = sqliteHelperPassword.fetch();

        //START VIEW INIT AND VIEW INFLATING
        View view = LayoutInflater.from(this).inflate(R.layout.change_password, null, false);

        final EditText oldET = view.findViewById(R.id.old_password);
        final EditText newpassET = view.findViewById(R.id.new_password);
        final EditText confirmET = view.findViewById(R.id.confirm_new_password);
        final TextView matchMisMatchTV = view.findViewById(R.id.status);

        oldET.addTextChangedListener(new TextWatcher() {
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
                        //IF VALID OLD PIN
                        newpassET.requestFocus();
                        newpassET.setText("");
                        matchMisMatchTV.setVisibility(View.INVISIBLE);
                    } else {
                        //IF PIN NOT VALID THEN SHOW ERROR
                        oldET.setText("");
                        matchMisMatchTV.setText("Invalid Old PIN");
                        matchMisMatchTV.setTextColor(Color.RED);
                        matchMisMatchTV.setVisibility(View.VISIBLE);
                    }
                } else {
                    matchMisMatchTV.setVisibility(View.INVISIBLE);
                }
            }
        });

        newpassET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6) {
                    matchMisMatchTV.setVisibility(View.INVISIBLE);
                    confirmET.setText("");
                    confirmET.requestFocus();
                }
            }
        });

        confirmET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty() &&
                        newpassET.getText().toString().length() == 6 &&
                        oldET.getText().toString().equals(oldSavedPassword) &&
                        newpassET.getText().toString().equals(s.toString())) {

                    matchMisMatchTV.setText("");
                    matchMisMatchTV.setVisibility(View.INVISIBLE);
                    sqliteHelperPassword.insert(confirmET.getText().toString());
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "New Password Set To : " + confirmET.getText().toString(), Toast.LENGTH_SHORT).show();

                } else if (!s.toString().equals(newpassET.getText().toString())) {

                    //IF PASSWORD DOES NOT MATCHES
                    matchMisMatchTV.setText("Password Doesn't Match");
                    matchMisMatchTV.setTextColor(Color.RED);
                    matchMisMatchTV.setVisibility(View.VISIBLE);
                } else {
                    matchMisMatchTV.setText("Invalid Password Length or Old Pin");
                    matchMisMatchTV.setTextColor(Color.RED);
                    matchMisMatchTV.setVisibility(View.VISIBLE);
                }
            }
        });


        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        stopCamera();
    }

    @Override
    public void onCameraError(int errorCode) {
        //NO NEED TO HANDLE CAMERA ERRORS
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            initCamera();
            startSecurityModule();
        }
    }

}
