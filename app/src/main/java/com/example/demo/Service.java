package com.example.demo;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
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
    final static private String baseURL = "http://10.0.2.2:3000/token";

    public static void createToken(HashMap payload, TokenCallback callback) {
        try {
            Request request = createRequest("", payload);
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFail(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) return;
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        callback.onComplete(json.getString("token"));
                    } catch (JSONException e) {
                        callback.onFail(e.getMessage());
                    }
                }
            });
        } catch (IOException ioe) {
            callback.onFail(ioe.getMessage());
        }
    }

    public static void verifyToken(String jwt, TokenCallback callback) {
        try {
            HashMap payload = new HashMap();
            payload.put("token", jwt);
            Request request = createRequest("/verify", payload);
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFail(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onComplete(true);
                    } else {
                        callback.onComplete(false);
                    }
                }
            });
        } catch (IOException ioe) {
            callback.onFail(ioe.getMessage());
        }
    }

    private static Request createRequest(String suffix, HashMap payload) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter requestPayload = new JsonWriter(sw);
        requestPayload.beginObject();

        Iterator it = payload.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String key = pair.getKey().toString();
            String value = pair.getValue().toString();
            requestPayload.name(key).value(value);
        }

        requestPayload.endObject();
        RequestBody body = RequestBody.create(sw.toString(), JSON);
        Request request = new Request.Builder().url(baseURL + suffix).post(body).build();

        return request;
    }
}