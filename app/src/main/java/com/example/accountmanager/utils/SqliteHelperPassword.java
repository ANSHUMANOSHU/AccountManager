package com.example.accountmanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class SqliteHelperPassword extends SQLiteOpenHelper {

    private static final String DATABASENAME = "DatabasePassword";
    private static final int VERSION = 1;
    private static final String TABLENAME  = "entrypass";
    private static final String COLUMNNAME = "password";

    public SqliteHelperPassword(@Nullable Context context) {
        super(context,DATABASENAME,null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new StringBuilder().append("Create Table ").append(TABLENAME).append(" ( ").append(COLUMNNAME).append(" text);").toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(new StringBuilder().append("Drop Table if exists ").append(TABLENAME).append(";").toString());
        onCreate(db);
    }

    public void insert(String pin){
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLENAME,"",new String[]{});
        ContentValues values = new ContentValues();
        values.put(COLUMNNAME,pin);
        database.insert(TABLENAME,null,values);
        database.close();
    }

    public String fetch(){
        SQLiteDatabase database = null;
        try {
            database = getReadableDatabase();
            Cursor cursor = database.rawQuery("Select * from " + TABLENAME + ";", null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(COLUMNNAME);
                if (index != -1)
                    return cursor.getString(index);
            }
        }catch(CursorIndexOutOfBoundsException ex){

        }finally {
            if(database!=null)
                database.close();
        }
        return "";
    }

}
