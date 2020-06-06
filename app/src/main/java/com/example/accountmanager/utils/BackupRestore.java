package com.example.accountmanager.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import com.example.accountmanager.activity.MainActivity;
import com.example.accountmanager.entity.Account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class BackupRestore {

    private Context context;
    private String BACKUP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Accounts/Backups";
    private static final String PACKAGE_NAME = "com.example.accountmanager";

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

    private void startRestore() {
        HashMap<String, String> map = fetAllFiles(BACKUP_PATH);

        if (map == null || map.size() == 0) {
            Toast.makeText(context, "No Backup File Found", Toast.LENGTH_SHORT).show();
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
        editText.setText(GeneralHelper.getFormattedDateTime());

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
            String QUERY = String.format("Select * FROM %s", SQLiteHelperAccounts.TABLE_NAME);
            Cursor cursor = database.rawQuery(QUERY, null);
            if (cursor != null) {
                String COL1, COL2, COL3, COL4, COL5, COL6;
                int NO = 0;
                SQLiteHelperAccounts accounts = new SQLiteHelperAccounts(context);
                try {
                    while (cursor.moveToNext()) {
                        NO++;
                        COL1 = cursor.getString(cursor.getColumnIndex(SQLiteHelperAccounts.COLUMN_1));
                        COL2 = cursor.getString(cursor.getColumnIndex(SQLiteHelperAccounts.COLUMN_2));
                        COL3 = cursor.getString(cursor.getColumnIndex(SQLiteHelperAccounts.COLUMN_3));
                        COL4 = cursor.getString(cursor.getColumnIndex(SQLiteHelperAccounts.COLUMN_4));
                        COL5 = cursor.getString(cursor.getColumnIndex(SQLiteHelperAccounts.COLUMN_5));
                        COL6 = cursor.getString(cursor.getColumnIndex(SQLiteHelperAccounts.COLUMN_6));

                        Account account = new Account(COL2, COL1, COL4, COL5, COL6, COL3);
                        accounts.insert(account);

                    }
                    ((MainActivity) context).fetchAllSavedAccounts();
                    Toast.makeText(context, "Successfully Imported : " + NO + " Entries", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(context, "Invalid Database File", Toast.LENGTH_SHORT).show();
                } finally {
                    cursor.close();
                }
            } else {
                Toast.makeText(context, "Invalid Database File", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Permission Denied for Reading Backup File", Toast.LENGTH_SHORT).show();
        }
    }

    private void doBackup(String filename) {
        try {
            String currentDBPath = "/data/data/" + PACKAGE_NAME + "/databases/" + SQLiteHelperAccounts.DB_NAME_ACCOUNTS;
            File currentDB = new File(currentDBPath);
            if (!new File(BACKUP_PATH).exists()) {
                new File(BACKUP_PATH).mkdirs();
            }
            BACKUP_PATH += "/" + filename + ".db";
            File backupDB = new File(BACKUP_PATH);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(context, "Backup Successful\nLocation : " + BACKUP_PATH, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "No Database File Found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
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


