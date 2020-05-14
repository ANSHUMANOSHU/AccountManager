package com.example.accountmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accountmanager.entity.Account;
import com.example.accountmanager.utils.SqliteHelperAccounts;
import com.example.accountmanager.adapters.SavedAccountsAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AddNewAccountActivity extends AppCompatActivity {

    EditText username, password, webSite, webUrl, notes;
    TextView stamp;

    private ArrayList<Account> accounts;
    private SavedAccountsAdapter savedAccountsAdapter;

    // for Password Generation---------------------------------------------------
    private CheckBox capitals,small,numbers,specials;
    private Button generateButton;
    public EditText lengthofpass;
    public static EditText required;
    public ListView listView;
    public ArrayList<String> passwords;
    public ArrayAdapter<String> adapter;
    public Dialog builder;

    private static Random random = new Random();
    private static String result ="";
    static int i =0;
    private static final String capitalletters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String smallletters = "abcdefghijklmnopqrstuvwxyz";
    private static final String number = "0123456789";
    private static final String special = "@!#$%^&*+-/_|";
    public  static String value = "";
    //---------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_account);

        //initialize views
        initViews();
    }

    private void initViews() {
        stamp = findViewById(R.id.newtimeStamp);
        username = findViewById(R.id.newusername);
        password = findViewById(R.id.newpassword);
        webSite = findViewById(R.id.newwebsite);
        webUrl = findViewById(R.id.newurl);
        notes = findViewById(R.id.newnotes);

        stamp.setText(new Date().toString());
        notes.setText(" ");

        accounts = new ArrayList<>();
    }

    public void saveClicked(View view) {

        if( username.getText().toString().isEmpty() || password.getText().toString().isEmpty()
                || webUrl.getText().toString().isEmpty() || webSite.getText().toString().isEmpty()){
            Toast.makeText(this, "Fields cannot be empty...", Toast.LENGTH_SHORT).show();
            return;
        }
        Account account = new Account(webSite.getText().toString()
                ,stamp.getText().toString()
                ,username.getText().toString()
                ,password.getText().toString()
                ,notes.getText().toString()
                ,webUrl.getText().toString());

        SqliteHelperAccounts sqliteHelperAccounts = new SqliteHelperAccounts(this);
        if (sqliteHelperAccounts.insertAccountInfo(account)){
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }else
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
    }

    //password generator
    public void passwordSuggestionClicked(View view) {
        builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //inflate password generator view
        View v = LayoutInflater.from(this).inflate(R.layout.password_generator,null,false);
        builder.setContentView(v);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        initDialogViews(v,builder);// initialize views of dialog
        builder.show();

    }

    //initialization of  views of dialog
    private void initDialogViews(View view, final Dialog builder) {

        capitals = view.findViewById(R.id.capitals);
        small = view.findViewById(R.id.smalls);
        numbers = view.findViewById(R.id.numbers);
        specials = view.findViewById(R.id.specials);
        lengthofpass = view.findViewById(R.id.length);
        required = view.findViewById(R.id.required);
        listView = view.findViewById(R.id.passwordslist);
        generateButton = view.findViewById(R.id.generateBtnClicked);

        passwords = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,passwords);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                password.setText(passwords.get(position));
                builder.dismiss();
            }
        });

        //when generate button is clicked
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateClicked();
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (required.isFocused() && manager!=null)
                    manager.hideSoftInputFromWindow(required.getWindowToken(),0);
                else if(lengthofpass.isFocused() && manager!=null)
                    manager.hideSoftInputFromWindow(lengthofpass.getWindowToken(),0);
            }
        });

    }


    // generator list of password management
    public void generateClicked() {

        boolean selected = false;
        boolean written = false;

        passwords.clear();

        if(capitals.isChecked()){
            value = value + capitalletters;
            selected = true;
        }
        if (small.isChecked()){
            value = value + smallletters;
            selected = true;
        }
        if (numbers.isChecked()){
            value = value + number;
            selected = true;
        }
        if (specials.isChecked()){
            value = value + special;
            selected = true;
        }

        if(!lengthofpass.getText().toString().isEmpty() && !required.getText().toString().isEmpty()){
            written = true;
        }

        if(!(written && selected)){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            return;
        }

        Progression progression = new Progression();
        progression.execute();
    }


    //Generator Algorithm
    private static String passwordGenerator(int len, String values) {
        result = "";
        for(i=0;i<len;i++){
            result +=values.charAt(random.nextInt(values.length()));
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.APP_OPENED = false;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    //Asynk tasking to handle calculations and progressDialog
    class Progression extends AsyncTask<Void,Integer,Void> {

        int requiredpass = Integer.parseInt(required.getText().toString());
        int length = Integer.parseInt(lengthofpass.getText().toString());
        int j =0;

        ProgressDialog dialog = new ProgressDialog(AddNewAccountActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for(;j<requiredpass;j++) {
                publishProgress(j);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passwords.add(passwordGenerator(length, value));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.setMessage("Generated : "+values[0]+" of " + requiredpass);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    }

}
