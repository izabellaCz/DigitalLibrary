package com.example.vlad.licenta;

import java.io.Serializable;

public class Filters  implements Serializable {

    String title = "";
    String author = "";
    String publisher = "";
    String available = "";

    private static final Filters instance = new Filters();

    private Filters () {}

    public static Filters getInstance() { return instance;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
