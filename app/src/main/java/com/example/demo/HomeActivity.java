package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.UUID;

import login.api.LoginApi;
import login.api.TransactionConfirmationCallback;
import login.api.client.TransactionConfirmationResponse;
import login.api.client.TransactionOptions;
import login.api.client.TransactionPayload;

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

        // handle TX
        findViewById(R.id.buttonTxConfirmation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTransactionConfirmationClick();
            }
        });
    }

    /**
     * function to handle transaction confirmation button event
     */
    private void handleTransactionConfirmationClick() {
        final TransactionConfirmationCallback txCallback = new TransactionConfirmationCallback() {
            @Override
            public void onComplete(TransactionConfirmationResponse response) {
                if (response.success) {
                    // go to home activity
                    CustomToast.displayMessage(HomeActivity.this, "Transaction Confirmed!");
                } else {
                    CustomToast.displayError(HomeActivity.this, response.errorMessage);
                }
            }
        };

        final String username = LoginApi.client().getCurrentUsername();
        final TextInputEditText payloadInputText = findViewById(R.id.transactionInput);
        final String payloadText = payloadInputText.getText().toString();
        final String nonce = UUID.randomUUID().toString();
        final HashMap tokenParams = new HashMap();
        tokenParams.put("type", "tx.create");
        tokenParams.put("nonce", nonce);
        tokenParams.put("username", username);
        tokenParams.put("tx_payload", payloadText);

        Service.createToken(tokenParams, new TokenCallback<String>() {
            @Override
            public void onComplete(String token) {
                final TransactionOptions options = TransactionOptions.buildAuth(token);
                final TransactionPayload payload = TransactionPayload.buildText(nonce, payloadText);

                LoginApi.client().transactionConfirmation(
                        HomeActivity.this,
                        username,
                        payload,
                        options,
                        txCallback
                );
            }

            @Override
            public void onFail(String message) {
                CustomToast.displayError(HomeActivity.this, message);
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