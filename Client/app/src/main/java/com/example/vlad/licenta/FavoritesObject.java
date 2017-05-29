package com.example.vlad.licenta;



public class FavoritesObject {

    String BookName;

    String Author;

    public FavoritesObject(String name, String type) {
        this.BookName=name;
        this.Author=type;

    }

    public String getName() {
        return BookName;
    }

    public String getAuthor() {
        return Author;
    }

}