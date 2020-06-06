package com.example.accountmanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class SQLiteHelperPassword extends SQLiteOpenHelper {

    private static final String DB_NAME_PASSWORD = "AppPassword";
    private static final String TABLE_NAME = "entrypass";
    private static final String COLUMN_1 = "password";
    private static final int VERSION = 1;

    public SQLiteHelperPassword(@Nullable Context context) {
        super(context, DB_NAME_PASSWORD, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_1 + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(String pin) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_NAME, "", new String[]{});
        ContentValues values = new ContentValues();
        values.put(COLUMN_1, pin);
        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public String fetch() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("Select * from " + TABLE_NAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(COLUMN_1);
            String pin = cursor.getString(index);
            cursor.close();
            return pin;
        }
        database.close();
        return "";
    }

}
