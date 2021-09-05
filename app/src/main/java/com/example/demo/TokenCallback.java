package com.example.demo;

public interface TokenCallback {
    public void onComplete(String token);
    public void onFail(String message);
}
