package com.example.demo;

import android.content.Intent;
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
            goToHome(LoginApi.client().getCurrentToken());
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
                    goToHome(response.jwt);
                } else {
                    // display error message as toast
                    displayToast(response.errorMessage);
                }
            }
        };

        final TextInputEditText usernameInputText = findViewById(R.id.usernameInputText);
        String username = usernameInputText.getText().toString();
        Service.createToken("auth.register", null, new TokenCallback<String>() {
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
                displayToast(message);
            }
        });
    }

    private void goToHome(String jwt) {
        Service.verifyToken(jwt, new TokenCallback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                if (result) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    displayToast("Invalid Token");
                }
            }

            @Override
            public void onFail(String message) {
                displayToast(message);
            }
        });
    }

    private void displayToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,  message);
                Toast toast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 48);
                toast.show();
            }
        });
    }
}