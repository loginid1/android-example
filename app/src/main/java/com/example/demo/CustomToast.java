package com.example.demo;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class CustomToast {
    private static final String ERROR_TAG = "FIDO_ERROR";
    private static final String TAG = "FIDO";

    /**
     * function to display a toast message
     */
    static public void displayError(Activity activity, String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(ERROR_TAG,  message);
                Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 48);
                toast.show();
            }
        });
    }

    static public void displayMessage(Activity activity, String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,  message);
                Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            }
        });
    }
}
