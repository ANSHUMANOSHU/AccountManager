package com.example.accountmanager.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.accountmanager.R;
import com.example.accountmanager.activity.MainActivity;
import com.example.accountmanager.entity.Account;
import com.example.accountmanager.utils.SQLiteHelperAccounts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class ViewAccountDialog extends Dialog {

    private EditText username, password, notes, websiteName, webUrl, stamp;
    private ImageView copyIconPass, copyIconUser, visitWebUrl;
    private Button modify, delete;
    private Context context;
    private Account account;

    public ViewAccountDialog(@NonNull Context context, Account account) {
        super(context);
        this.context = context;
        this.account = account;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //HIDE TITLE BAR
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //INFLATE VIEW AND GET VIEWS
        View view = LayoutInflater.from(context).inflate(R.layout.view_account, null, false);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        notes = view.findViewById(R.id.notes);
        websiteName = view.findViewById(R.id.website);
        webUrl = view.findViewById(R.id.url);
        stamp = view.findViewById(R.id.timeStamp);
        delete = view.findViewById(R.id.delete);
        modify = view.findViewById(R.id.modify);
        visitWebUrl = view.findViewById(R.id.browser);
        copyIconUser = view.findViewById(R.id.copyu);
        copyIconPass = view.findViewById(R.id.copyp);

        //SET VIEW AND DATA
        setContentView(view);
        loadData();

    }

    private void loadData() {
        //LOAD DATA
        username.setText(account.username);
        password.setText(account.password);
        notes.setText(account.notes);
        webUrl.setText(account.web_url);
        websiteName.setText(account.website);
        stamp.setText(account.timeStamp);

        //ATTACH LISTENERS
        copyIconPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyIconPass();
            }
        });

        copyIconUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyIconUser();
            }
        });

        visitWebUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserClicked();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteClicked();
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyClicked();
            }
        });

    }

    private void modifyClicked() {
        if (!account.username.toLowerCase().equals(username.getText().toString().toLowerCase())
                || !account.password.equals(password.getText().toString().toLowerCase())
                || !account.web_url.equals(webUrl.getText().toString().toLowerCase())
                || !account.website.equals(websiteName.getText().toString().toLowerCase())
                || !account.notes.equals(notes.getText().toString().toLowerCase())) {

            Account account = new Account(websiteName.getText().toString()
                    , stamp.getText().toString()
                    , username.getText().toString()
                    , password.getText().toString()
                    , notes.getText().toString()
                    , webUrl.getText().toString());

            SQLiteHelperAccounts SQLiteHelperAccounts = new SQLiteHelperAccounts(context);
            SQLiteHelperAccounts.update(account);
            ((MainActivity) context).fetchAllSavedAccounts();
            Toast.makeText(context, "Changes Saved Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                SQLiteHelperAccounts SQLiteHelperAccounts = new SQLiteHelperAccounts(context);
                SQLiteHelperAccounts.delete(account);
                ((MainActivity) context).fetchAllSavedAccounts();
                dialog.dismiss();
                dismiss();
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();

            }
        });
        builder.create().show();
    }

    private void copyIconPass() {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy", password.getText().toString());
        Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
        Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

    private void copyIconUser() {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy", username.getText().toString());
        Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
        Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

    private void browserClicked() {
        try {
            new URL(webUrl.getText().toString().trim());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl.getText().toString().trim()));
            context.startActivity(intent);
        } catch (MalformedURLException e) {
            Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show();
        }
    }

}
