package com.example.vlad.licenta.model;

import java.io.Serializable;
import java.nio.charset.Charset;

public class User implements Serializable {

    private int id;
    private String fullname;
    private String username;
    private String password;
    private String type;

    public User() {
    }

    public User(int id, String fullname, String username, String password, String type) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(byte[] fullname) {
        this.fullname = new String(fullname, Charset.forName("UTF-8"));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
