package com.example.accountmanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.accountmanager.entity.Account;
import java.util.ArrayList;
import java.util.Date;
import androidx.annotation.Nullable;

public class SQLiteHelperAccounts extends SQLiteOpenHelper {

    public static final String DB_NAME_ACCOUNTS = "Accounts";
    public static final String TABLE_NAME = "accounts";
    public static final String COLUMN_1 = "stamp";
    public static final String COLUMN_2 = "website";
    public static final String COLUMN_3 = "weburl";
    public static final String COLUMN_4 = "username";
    public static final String COLUMN_5 = "password";
    public static final String COLUMN_6 = "notes";
    private static final int VERSION = 1;

    public SQLiteHelperAccounts(@Nullable Context context) {
        super(context, DB_NAME_ACCOUNTS, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE " +
                TABLE_NAME + " ( " +
                COLUMN_1 + " TEXT," +
                COLUMN_2 + " TEXT," +
                COLUMN_3 + " TEXT," +
                COLUMN_4 + " TEXT," +
                COLUMN_5 + " TEXT," +
                COLUMN_6 + " TEXT)";

        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_1, account.timeStamp);
        values.put(COLUMN_2, account.website);
        values.put(COLUMN_3, account.web_url);
        values.put(COLUMN_4, account.username);
        values.put(COLUMN_5, account.password);
        values.put(COLUMN_6, account.notes);
        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public void update(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_1, new Date().toString());
        values.put(COLUMN_2, account.website);
        values.put(COLUMN_3, account.web_url);
        values.put(COLUMN_4, account.username);
        values.put(COLUMN_5, account.password);
        values.put(COLUMN_6, account.notes);
        database.update(TABLE_NAME, values, COLUMN_1 + " = ?", new String[]{account.timeStamp});
        database.close();
    }

    public ArrayList<Account> fetch() {
        ArrayList<Account> accounts = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * from " + TABLE_NAME, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Account account = new Account(
                        cursor.getString(cursor.getColumnIndex(COLUMN_2))
                        , cursor.getString(cursor.getColumnIndex(COLUMN_1))
                        , cursor.getString(cursor.getColumnIndex(COLUMN_4))
                        , cursor.getString(cursor.getColumnIndex(COLUMN_5))
                        , cursor.getString(cursor.getColumnIndex(COLUMN_6))
                        , cursor.getString(cursor.getColumnIndex(COLUMN_3)));
                accounts.add(account);
            }
            cursor.close();
        }
        database.close();
        return accounts;
    }

    public void delete(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, COLUMN_1 + " = ?", new String[]{account.timeStamp});
        database.close();
    }

}
