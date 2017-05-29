package com.example.vlad.licenta;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vlad on 4/8/2017.
 */


public class HistoryObject {

    String BookName;

    String Author;

    String DaysToReturn;

    public HistoryObject(String name, String type,String daysToReturn) {
        this.BookName=name;
        this.Author=type;
        this.DaysToReturn=daysToReturn;

    }

    public String getBookName() {
        return BookName;
    }

    public String getAuthor() {
        return Author;
    }

    public String getDaysToReturn() { return DaysToReturn; }

}



