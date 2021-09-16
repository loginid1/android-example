package com.example.demo;

import android.app.Application;
import login.api.LoginApi;

public class ApplicationData extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final String BASE_URL = "<BASE_URL>";
        final String CLIENT_ID = "<CLIENT_ID>";

        LoginApi.client().configure(this, CLIENT_ID, BASE_URL);
    }
}