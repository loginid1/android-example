package com.example.demo;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Service {
    final static private OkHttpClient httpClient = new OkHttpClient
            .Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
                @NonNull
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request request = chain
                            .request()
                            .newBuilder()
                            .header("X-Requested-With", "XMLHttpRequest")
                            .build();
                    return chain.proceed(request);
                }
            })
            .build();
    final static private MediaType JSON = MediaType.get("application/json; charset=utf-8");
    final static private int NUM_CORES = Runtime.getRuntime().availableProcessors();
    static Executor m_executor = new ThreadPoolExecutor(
            NUM_CORES * 2,
            NUM_CORES * 2,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>()
    );
    final static private String baseURL = "http://localhost:3000";

    public static String createToken(String type, String payload) {
        try {
            StringWriter sw = new StringWriter();
            JsonWriter requestPayload = new JsonWriter(sw);
            requestPayload.beginObject().name("type").value(type);

            if (payload != null) {
                requestPayload.name("payload").value(payload);
            }

            requestPayload.endObject();

            RequestBody body = RequestBody.create(sw.toString(), JSON);
            Request request = new Request.Builder().url(baseURL + "/token").post(body).build();
            Response response = httpClient.newCall(request).execute();

            if (response != null && response)
        } catch (IOException ioe) {

        }
        return "";
    }
}