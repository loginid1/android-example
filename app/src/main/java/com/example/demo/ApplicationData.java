package com.example.demo;

import android.app.Application;
import login.api.LoginApi;

public class ApplicationData extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final String BASE_URL = "https://691181e0-d24a-11eb-b6c8-af0cb09f6ea8.sandbox-usw1.api.loginid.io";
        final String CLIENT_ID = "spBnuRjKo-eMPKpol191bGezLQEy6GMzboarwDvsHQjuHEi5-JCtAlfXzVCv_492-xI5wMlRhpOe9bv4oDxfNA==";

        LoginApi.client().configure(this, CLIENT_ID, BASE_URL);
    }
}