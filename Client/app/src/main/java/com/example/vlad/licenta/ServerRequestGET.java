package com.example.vlad.licenta;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


public class ServerRequestGET<T> extends AsyncTask<Void, Void, T> {

    AsyncResponse<T> delegate = null;

    private String url;
    private JavaType javaType;

    public ServerRequestGET(String url, JavaType javaType, AsyncResponse<T> delegate) {
        this.delegate = delegate;
        this.url = url;
        this.javaType = javaType;
    }



    @Override
    protected T doInBackground(Void... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        String jsonResponse = restTemplate.getForObject(url, String.class);

        if (jsonResponse == null) return null;  // no results found

        ObjectMapper objectMapper = new ObjectMapper();
        T list = null;
        try {
            list = objectMapper.readValue(jsonResponse, this.javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    protected void onPostExecute(T result) {
        //super.onPostExecute(result);
        this.delegate.actionCompleted(result);
    }
}
