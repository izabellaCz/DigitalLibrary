package com.example.vlad.licenta.model;

import java.nio.charset.Charset;

public class Author {

    private int id;
    private String name;

    public Author() {
    }

    public Author(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = new String(name, Charset.forName("UTF-8"));
    }
}
