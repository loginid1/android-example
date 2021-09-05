package com.example.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import login.api.LoginApi;
import login.api.RegisterCallback;
import login.api.client.RegisterResponse;
import login.api.client.RegistrationOptions;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FIDO_ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //check if signedIn
        if (LoginApi.client().hasAccount() && LoginApi.client().isLoggedIn()) {
            // redirect user to home page directly
            //goToHome();
            //return;
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
                    Log.i(TAG, "GOOD");
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
        Service.createToken("auth.register", null, new TokenCallback() {
            @Override
            public void onComplete(String token) {
                RegistrationOptions options = RegistrationOptions.buildAuth(token);
                LoginApi.client().registerWithFido2(
                        LoginActivity.this,
                        username,
                        options,
                        registerCallback
                );
            }

            @Override
            public void onFail(String message) {
                //displayToast(message);
                Log.i("FIDO_MESSAGE", message);
            }
        });

        //LoginApi.client().registerWithFido2(this, username, null, registerCallback);
    }

    private void goToHome() {

    }

    private void displayToast(String message) {
        Log.e(TAG,  message);
        Toast toast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 48);
        toast.show();
    }
}