package com.ars.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.ars.R;
import com.ars.auth.ARSAuthenticator;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
         {

    private EditText edtHost, edtUsername, edtPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccountsByType(ARSAuthenticator.AUTH_TYPE);
        if (accounts.length > 0) {
            // account found. redirecting to home screen.
            redirectToHome();
        }

        //code for Login object Initialization
        edtHost = (EditText) findViewById(R.id.edtHost);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        findViewById(R.id.btnLogin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnLogin) {

            edtHost.setError(null);
            if (edtHost.getText().toString().trim().isEmpty()) {
                edtHost.setError(getString(R.string.error_host_name_required));
                edtHost.requestFocus();
                return;
            }
            edtUsername.setError(null);
            if (edtUsername.getText().toString().trim().isEmpty()) {
                edtUsername.setError(getString(R.string.error_username_required));
                edtUsername.requestFocus();
                return;
            }
            edtPassword.setError(null);
            if (edtPassword.getText().toString().trim().isEmpty()) {
                edtPassword.setError(getString(R.string.error_password_required));
                edtPassword.requestFocus();
                return;
            }

            login();
        }
    }

    private void login() {
        String host_url = stripURL(edtHost.getText().toString().trim());
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String stripURL(String host) {
        if (host.contains("http://") || host.contains("https://")) {
            return host;
        } else {
            return "http://" + host;
        }
    }



    private void loginTo(String database) {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        //auth.authenticate(username, password, database, this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setMessage(getString(R.string.login_in_progress));
        progressDialog.show();
    }



    private void redirectToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }


    private void showDatabaseSelection(final List<String> databases) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_database);
        builder.setSingleChoiceItems(databases.toArray(new String[databases.size()]), 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String database = databases.get(which);
                        loginTo(database);
                    }
                });
        builder.create().show();
    }


}
