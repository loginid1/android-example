package com.example.demo;

public interface TokenCallback<T> {
    public void onComplete(T result);
    public void onFail(String message);
}
