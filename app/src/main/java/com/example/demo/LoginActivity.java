package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import login.api.LoginApi;
import login.api.RegisterCallback;
import login.api.client.RegisterResponse;
import login.api.client.RegistrationOptions;

public class LoginActivity extends Activity {
    private static final String TAG = "FIDO_ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //check if signedIn
        if (LoginApi.client().hasAccount() && LoginApi.client().isLoggedIn()) {
            // redirect user to home page directly
            //goToHome();
            return;
        }

        // handle register button
        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterClick();
            }
        });
    }

    /**
     * function to handle register button event
     */
    private void handleRegisterClick() {
        final RegisterCallback registerCallback = new RegisterCallback() {
            @Override
            public void onComplete(RegisterResponse response) {
                if (response.success) {
                    // go to home activity
                    //goToHome();
                } else {
                    // display error message as toast
                    Log.e(TAG, "Register error: " + response.errorMessage);
                    Toast toast = Toast.makeText(LoginActivity.this, "Error: " + response.errorMessage, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 48);
                    toast.show();
                }
            }
        };

        final TextInputEditText usernameInputText = findViewById(R.id.usernameInputText);
        String username = usernameInputText.getText().toString();
        LoginApi.client().registerWithFido2(this, username, null, registerCallback);
    }
}