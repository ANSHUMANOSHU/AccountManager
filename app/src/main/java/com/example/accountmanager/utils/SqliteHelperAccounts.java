package com.example.accountmanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.accountmanager.entity.Account;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class SqliteHelperAccounts extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String TABLE_NAME = "accounts";
    private static final String DATABASENAME = "Database";
    private static final String COLUMN_1 = "stamp";
    private static final String COLUMN_2 = "website";
    private static final String COLUMN_3 = "weburl";
    private static final String COLUMN_4 = "username";
    private static final String COLUMN_5 = "password";
    private static final String COLUMN_6 = "notes";
    private Context context;

    public SqliteHelperAccounts(@Nullable Context context) {
        super(context, DATABASENAME, null, VERSION);
        this.context = context;
    }

    public SQLiteDatabase getDataabase(){
        return getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "create table " +
                TABLE_NAME + " ( " +
                COLUMN_1 + " text," +
                COLUMN_2 + " text," +
                COLUMN_3 + " text," +
                COLUMN_4 + " text," +
                COLUMN_5 + " text," +
                COLUMN_6 + " text)";

        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if Exists " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAccountInfo(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_1, account.timeStamp);
            values.put(COLUMN_2, account.website);
            values.put(COLUMN_3, account.web_url);
            values.put(COLUMN_4, account.username);
            values.put(COLUMN_5, account.password);
            values.put(COLUMN_6, account.notes);
            database.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            return false;
        } finally {
            database.close();
        }
        return true;
    }

    public boolean updateAccountInfo(Account account){
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_1,new Date().toString());
            values.put(COLUMN_2,account.website);
            values.put(COLUMN_3,account.web_url);
            values.put(COLUMN_4,account.username);
            values.put(COLUMN_5,account.password);
            values.put(COLUMN_6,account.notes);
            database.update(TABLE_NAME,values,COLUMN_1+" = ?",new String[]{account.timeStamp});
        }catch (Exception e){
            return false;
        }finally {
            database.close();
        }
        return true;
    }


    public ArrayList<Account> fetchAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery("Select * from " + TABLE_NAME , null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Account account = new Account( cursor.getString(cursor.getColumnIndex(COLUMN_2))
                            , cursor.getString(cursor.getColumnIndex(COLUMN_1))
                            , cursor.getString(cursor.getColumnIndex(COLUMN_4))
                            , cursor.getString(cursor.getColumnIndex(COLUMN_5))
                            , cursor.getString(cursor.getColumnIndex(COLUMN_6))
                            , cursor.getString(cursor.getColumnIndex(COLUMN_3)));
                    accounts.add(account);
                }
                cursor.close();
            }
        } catch (Exception e) {return accounts;} finally { database.close();}
        return accounts;
    }


    public boolean deleteAccountInfo(Account account) {

        SQLiteDatabase database = this.getWritableDatabase();
        try {
            database.delete(TABLE_NAME,COLUMN_1 + " = ?",new String[]{account.timeStamp});
        }catch (Exception e){
            return false;
        }finally {
            database.close();
        }
        return true;
    }
}
