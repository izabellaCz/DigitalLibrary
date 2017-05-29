package com.example.vlad.licenta;

public interface AsyncResponse<T> {
    void actionCompleted(T obj);
}
