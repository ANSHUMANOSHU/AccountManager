package com.example.accountmanager.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accountmanager.R;
import com.example.accountmanager.activity.MainActivity;
import com.example.accountmanager.entity.Account;
import com.example.accountmanager.utils.SQLiteHelperAccounts;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import androidx.annotation.NonNull;

public class AddAccountDialog extends Dialog {

    //FOR ADD ACCOUNT DIALOG
    private EditText username, password, webSite, webUrl, notes;
    private TextView stamp;

    //FOR GENERATE PASSWORD DIALOG
    private CheckBox capitals, smalls, numbers, specials;
    private EditText lengthofpass, noofpassword;

    //REST GLOBAL VARIABLES
    private Context context;
    private Random random = new Random();
    private static final String capital = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String small = "abcdefghijklmnopqrstuvwxyz";
    private static final String number = "0123456789";
    private static final String special = "@!#$%^&*+-/_|";

    public AddAccountDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //HIDE TITLE BAR
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //GET VIEW AND INITIALIZE
        View view = LayoutInflater.from(context).inflate(R.layout.add_account, null, false);

        stamp = view.findViewById(R.id.timeStamp);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        webSite = view.findViewById(R.id.website);
        webUrl = view.findViewById(R.id.url);
        notes = view.findViewById(R.id.notes);

        Button addButton = view.findViewById(R.id.addAccount);
        ImageView settings = view.findViewById(R.id.settings);

        //ONCLICK LISTENERS
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountDetails();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGeneratedPasswordDialog();
            }
        });

        //SET TIME STAMP TO NOW
        stamp.setText(new Date().toString());

        //SET CONTENT VIEW
        setContentView(view);

    }

    private void saveAccountDetails() {

        //CHECK IF NOT EMPTY FIELDS
        if (username.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() ||
                webUrl.getText().toString().isEmpty() ||
                webSite.getText().toString().isEmpty()) {

            Toast.makeText(context, "Some Field are Missing", Toast.LENGTH_SHORT).show();
            return;

        }

        //MAKE ACCOUNT OBJECT AND SAVE
        Account account = new Account(webSite.getText().toString()
                , stamp.getText().toString()
                , username.getText().toString()
                , password.getText().toString()
                , notes.getText().toString()
                , webUrl.getText().toString());

        SQLiteHelperAccounts SQLiteHelperAccounts = new SQLiteHelperAccounts(context);
        SQLiteHelperAccounts.insert(account);
        ((MainActivity) context).fetchAllSavedAccounts();
        dismiss();
        Toast.makeText(context, "Account Added Successfully", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("InflateParams")
    private void showGeneratedPasswordDialog() {

        //SHOW DIALOG AND HIDE TITLE BAR
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //GET VIEW AND INITIALIZE
        View view = LayoutInflater.from(context).inflate(R.layout.password_generator, null, false);

        capitals = view.findViewById(R.id.capitals);
        smalls = view.findViewById(R.id.smalls);
        numbers = view.findViewById(R.id.numbers);
        specials = view.findViewById(R.id.specials);
        lengthofpass = view.findViewById(R.id.length);
        noofpassword = view.findViewById(R.id.required);
        lengthofpass = view.findViewById(R.id.length);

        Button generateButton = view.findViewById(R.id.generateBtnClicked);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HIDE KEYBOARD AND GENERATE PASSWORDS
                InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager != null) {
                    manager.hideSoftInputFromWindow(lengthofpass.getWindowToken(), 0);
                }

                generatePasswords(dialog);
            }
        });

        //SET VIEW AND SHOW DIALOG
        dialog.setContentView(view);
        dialog.show();
    }

    private void generatePasswords(Dialog dialog) {

        String value = "";

        if (capitals.isChecked())
            value = value + capital;

        if (smalls.isChecked())
            value = value + small;

        if (numbers.isChecked())
            value = value + number;

        if (specials.isChecked())
            value = value + special;

        //CHECK ALL FIELDS ARE THERE IF NOT RETURN
        if (lengthofpass.getText().toString().isEmpty() ||
                noofpassword.getText().toString().isEmpty() ||
                value.isEmpty() ||
                lengthofpass.getText().toString().equals("0") ||
                noofpassword.getText().toString().equals("0")) {

            Toast.makeText(context, "Some Fields are Missing", Toast.LENGTH_SHORT).show();
            return;

        }

        //IF LENGTH OR NO IS VER LARGE
        if (Integer.parseInt(lengthofpass.getText().toString()) > 500 ||
                Integer.parseInt(noofpassword.getText().toString()) > 1000) {

            Toast.makeText(context, "Values Too Large to Generate", Toast.LENGTH_SHORT).show();
            return;

        }

        //GENERATE PASSWORDS AND HIDE THIS DIALOG
        ArrayList<String> passwords = new ArrayList<>();
        for (int i = 1; i < Integer.parseInt(noofpassword.getText().toString()); i++) {
            passwords.add(generatePassword(Integer.parseInt(lengthofpass.getText().toString()), value));
        }
        dialog.show();

        listPasswords(passwords,dialog);
    }

    private void listPasswords(final ArrayList<String> passwords,final Dialog MainDialog) {

        //SHOW DIALOG AND HIDE TITLE BAR
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //CREATE VIEW AND INITIALIZE
        ListView listView = new ListView(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, passwords);
        listView.setAdapter(adapter);

        //SET CLICK LISTENER
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                password.setText(passwords.get(i));
                dialog.dismiss();
                MainDialog.dismiss();
            }
        });

        //SET VIEW AND SHOW
        dialog.setContentView(listView);
        dialog.show();

    }

    private String generatePassword(int len, String values) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            result.append(values.charAt(random.nextInt(values.length())));
        }
        return result.toString();
    }

}
