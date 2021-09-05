package com.example.demo;

import android.app.Application;
import login.api.LoginApi;

public class ApplicationData extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final String BASE_URL = "https://e385f8a0-0e60-11ec-b42a-bb8e0fc28366.usw1.loginid.io";
        final String CLIENT_ID = "GoZseD8OHWX3evR9GR2LtNBwWZ31lptN8IGeWNSdl7JL_XXzWEz6eolfwogIdyU8KsoiDS6mB_LbnUI_3_sOAA==";

        LoginApi.client().configure(this, CLIENT_ID, BASE_URL);
    }
}