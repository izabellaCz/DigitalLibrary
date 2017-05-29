package com.example.vlad.licenta;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class ServerRequestPOST<T> extends AsyncTask<Void, Void, T> {

    AsyncResponse<T> delegate = null;

    private String url;
    private JavaType javaType;
    private HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity;

    public ServerRequestPOST(String url, JavaType javaType, HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity, AsyncResponse<T> delegate) {
        this.delegate = delegate;
        this.url = url;
        this.javaType = javaType;
        this.httpEntity = httpEntity;
    }

    @Override
    protected T doInBackground(Void... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());


        String jsonResponse = restTemplate.postForObject(url, httpEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        T responseObject = null;
        try {
            responseObject = objectMapper.readValue(jsonResponse, this.javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseObject;
    }

    @Override
    protected void onPostExecute(T result) {
        //super.onPostExecute(result);
        this.delegate.actionCompleted(result);
    }
}
