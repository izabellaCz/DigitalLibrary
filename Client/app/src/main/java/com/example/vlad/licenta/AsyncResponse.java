package com.example.vlad.licenta;

/**
 * Created by z003jyrf on 12.05.2017.
 */

public interface AsyncResponse<T> {
    void actionCompleted(T obj);
}
