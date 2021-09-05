package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import login.api.LoginApi;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if (LoginApi.client().hasAccount() && LoginApi.client().isLoggedIn()) {
            String usernameText = LoginApi.client().getCurrentAccountName();
            TextView welcomeTextView = findViewById(R.id.verifyWelcomeText);
            if (usernameText != null && usernameText.length() > 0) {
                welcomeTextView.setText(String.format("Welcome %s!", usernameText));
            } else {
                welcomeTextView.setText("Welcome!");
            }
        } else {
            // redirect user to login page
            goToLogin();
            return;
        }

        // handle logout event
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogoutClick();
            }
        });
    }

    private void handleLogoutClick() {
        LoginApi.client().logout();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}