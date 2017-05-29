package com.example.vlad.licenta.model;

import java.nio.charset.Charset;

public class Book {

    private int id;
    private String title;
    private Author author;
    private String publisher;
    private String description;
    private int total;
    private int available;
    private byte[] cover;
    private History history;
    private boolean favourite;

    public Book() {
    }

    public Book(int id, String title, Author author, String publisher, String description, int total, int available, byte[] cover, History history, boolean favourite) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.total = total;
        this.available = available;
        this.cover = cover;
        this.history = history;
        this.favourite = favourite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(byte[] title) {
        this.title = new String(title, Charset.forName("UTF-8"));
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(byte[] publisher) {
        this.publisher = new String(publisher, Charset.forName("UTF-8"));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(byte[] description) {
        this.description = new String(description, Charset.forName("UTF-8"));
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
