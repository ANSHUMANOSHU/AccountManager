package com.example.accountmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accountmanager.Entity.DataTransferHelper;
import com.example.accountmanager.Entity.EntityAccount;
import com.example.accountmanager.Entity.SqliteHelperAccounts;

import java.util.Objects;

public class AccountDetailsActivity extends AppCompatActivity {

    EditText username,password,notes,websiteName,webUrl;
    TextView stamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_details);
        //initialize views
        initViews();
    }

    private void initViews() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        notes = findViewById(R.id.notes);
        websiteName = findViewById(R.id.website);
        webUrl = findViewById(R.id.url);
        stamp = findViewById(R.id.timeStamp);

        //recieve data  and set to views
        username.setText(DataTransferHelper.entityAccount.username);
        password.setText(DataTransferHelper.entityAccount.password);
        notes.setText(DataTransferHelper.entityAccount.notes);
        webUrl.setText(DataTransferHelper.entityAccount.web_url);
        websiteName.setText(DataTransferHelper.entityAccount.website);
        stamp.setText(DataTransferHelper.entityAccount.timeStamp);

    }

    public void modifyClicked(View view) {
        if(!DataTransferHelper.entityAccount.username.toLowerCase().equals(username.getText().toString().toLowerCase())
                || !DataTransferHelper.entityAccount.password.equals(password.getText().toString().toLowerCase())
                || !DataTransferHelper.entityAccount.web_url.equals(webUrl.getText().toString().toLowerCase())
                || !DataTransferHelper.entityAccount.website.equals(websiteName.getText().toString().toLowerCase())
                || !DataTransferHelper.entityAccount.notes.equals(notes.getText().toString().toLowerCase())){

            EntityAccount entityAccount = new EntityAccount(websiteName.getText().toString()
                    ,stamp.getText().toString()
                    ,username.getText().toString()
                    ,password.getText().toString()
                    ,notes.getText().toString()
                    ,webUrl.getText().toString());

            SqliteHelperAccounts sqliteHelperAccounts = new SqliteHelperAccounts(this);
            if (sqliteHelperAccounts.updateAccountInfo(entityAccount)){
                Toast.makeText(this, "Modification successful... ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Do you want to delete account details ?");
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SqliteHelperAccounts sqliteHelperAccounts = new SqliteHelperAccounts(AccountDetailsActivity.this);
                if (sqliteHelperAccounts.deleteAccountInfo(DataTransferHelper.entityAccount)){
                    Toast.makeText(AccountDetailsActivity.this, "Deletion successful...", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
        builder.create().show();
    }

    public void copyIconPass(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy",password.getText().toString());
        Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

    public void copyIconUser(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy",username.getText().toString());
        Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.APP_OPENED = false;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void browserClicked(View view) {
        try {
            String url = webUrl.getText().toString().trim();
            if (!url.contains("https://") || !url.contains("http://"))
                url = "https://"+url;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this, "Wrong Url", Toast.LENGTH_SHORT).show();
        }
    }
}
