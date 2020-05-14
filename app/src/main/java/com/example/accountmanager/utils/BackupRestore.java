package com.example.accountmanager.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;
import com.example.accountmanager.entity.Account;
import com.example.accountmanager.interfaces.OnBackupListener;
import com.example.accountmanager.interfaces.OnRestoreListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;

public class BackupRestore {

    private Context context;
    private String BACKUP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AccountsBackup/"; //WITH / AT END
    private String PACKAGE_NAME = "com.example.accountmanager";
    private OnBackupListener onBackupListener;
    private OnRestoreListener onRestoreListener;
    private static final String TABLE_NAME = "accounts";
    private static final String DATABASENAME = "Database";
    private static final String COLUMN_1 = "stamp";
    private static final String COLUMN_2 = "website";
    private static final String COLUMN_3 = "weburl";
    private static final String COLUMN_4 = "username";
    private static final String COLUMN_5 = "password";
    private static final String COLUMN_6 = "notes";

    public BackupRestore(Context context) {
        this.context = context;
    }

    public void start() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose");
        builder.setCancelable(true);
        String[] options = {"Backup", "Restore"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        //ASK BACKUP FILE NAME
                        startBackup();
                        break;
                    case 1:
                        startRestore();
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public void setBackUpCompleteListener(OnBackupListener onBackupListener) {
        this.onBackupListener = onBackupListener;
    }

    public void setRestoreCompleteListener(OnRestoreListener onRestoreListener) {
        this.onRestoreListener = onRestoreListener;
    }

    private void startRestore() {
        //FETCH BACKUP FILE AND SHOW
        HashMap<String, String> map = fetAllFiles(BACKUP_PATH);

        if (map == null) {
            if (onRestoreListener != null) {
                onRestoreListener.OnRestoreResponse("NO FILE FOUND");
            }
            return;
        }

        //KEYS -> PATHS
        //VALUES -> NAMES

        final String[] NAMES = new String[map.size()];
        final String[] PATHS = new String[map.size()];

        int index = 0;
        for (String name : map.values()) {
            NAMES[index++] = name;
        }

        index = 0;
        for (String path : map.keySet()) {
            PATHS[index++] = path;
        }

        //------------------------

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Backup File to Restore : ");
        builder.setCancelable(false);
        builder.setItems(NAMES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doRestore(PATHS[i]);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void startBackup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Backup File Name : ");

        //CREATE EDIT TEXT
        final EditText editText = new EditText(context);
        editText.setText(getFormattedDate());

        builder.setView(editText);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Backup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editText.getText().toString().isEmpty()) {
                    dialogInterface.dismiss();
                    Toast.makeText(context, "File Name Cannot Be Empty....", Toast.LENGTH_SHORT).show();
                    startBackup();
                } else {
                    doBackup(editText.getText().toString());
                }
            }
        });
        builder.create();
        builder.show();
    }

    private void doRestore(String PATH) {
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(new File(PATH), null);
        if (database != null) {
            String QUERY = String.format("Select * FROM %s", TABLE_NAME);
            Cursor cursor = database.rawQuery(QUERY, null);
            if (cursor != null) {
                String COL1, COL2, COL3, COL4, COL5, COL6;
                int NO = 0;
                SqliteHelperAccounts accounts = new SqliteHelperAccounts(context);
                try {
                    while (cursor.moveToNext()) {
                        NO++;
                        COL1 = cursor.getString(cursor.getColumnIndex(COLUMN_1));
                        COL2 = cursor.getString(cursor.getColumnIndex(COLUMN_2));
                        COL3 = cursor.getString(cursor.getColumnIndex(COLUMN_3));
                        COL4 = cursor.getString(cursor.getColumnIndex(COLUMN_4));
                        COL5 = cursor.getString(cursor.getColumnIndex(COLUMN_5));
                        COL6 = cursor.getString(cursor.getColumnIndex(COLUMN_6));

                        Account account = new Account(COL2,COL1,COL4,COL5,COL6,COL3);
                        accounts.insertAccountInfo(account);

                    }
                    if (onRestoreListener != null) {
                        onRestoreListener.OnRestoreResponse("Successfully Imported " + NO + " Entries");
                    }
                } catch (Exception ex) {
                    if (onRestoreListener != null) {
                        onRestoreListener.OnRestoreResponse("Invalid Database File");
                    }
                } finally {
                    cursor.close();
                }
            } else {
                if (onRestoreListener != null) {
                    onRestoreListener.OnRestoreResponse("Invalid Database File");
                }
            }
        }
        else {
            if (onRestoreListener != null) {
                onRestoreListener.OnRestoreResponse("Invalid Database File");
            }
        }
    }

    private void doBackup(String filename) {
        try {
            String currentDBPath = "/data/data/" + PACKAGE_NAME + "/databases/" + DATABASENAME;
            File currentDB = new File(currentDBPath);
            BACKUP_PATH += filename + ".db";
            File backupDB = new File(BACKUP_PATH);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                if (onBackupListener != null) {
                    onBackupListener.OnBackupResponse("Backup Successful\nLocation : " + BACKUP_PATH);
                }

            } else {

                if (onBackupListener != null) {
                    onBackupListener.OnBackupResponse("No Database File Found");
                }

            }
        } catch (Exception e) {
            if (onBackupListener != null) {
                onBackupListener.OnBackupResponse("Error : " + e);
            }
        }
    }

    private String getFormattedDate() {
        String[] date = new Date().toString().split(" ");
        date[0] = date[2] + "" + date[1] + "" + date[5] + "_" + date[3];
        return date[0];
    }

    private HashMap<String, String> fetAllFiles(String backup_path) {
        HashMap<String, String> map = new HashMap<>();
        File file = new File(backup_path);
        if (file.exists()) {
            for (File f : file.listFiles()) {
                if (f.isFile() && f.getAbsolutePath().endsWith(".db")) {
                    map.put(f.getAbsolutePath(), f.getName());
                }
            }
            return map;
        }
        return null;
    }

}


