package com.example.vlad.licenta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.licenta.model.Book;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CustomAdapterHistoryBooks extends ArrayAdapter<Book> implements View.OnClickListener{

    public final static long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
    public final static int LOAN_DAYS = 30;
    private List<Book> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
        TextView txtAuthor;
        TextView txtRentDate;
        TextView txtReturnDate;
        ImageView ivCover;
    }

    public CustomAdapterHistoryBooks(List<Book> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }


    public void refresh(List<Book> data)
    {
        this.dataSet = data;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);//

//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +favoritesObject.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
       Book currentBook = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_history, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.book_title);
            viewHolder.txtAuthor = (TextView) convertView.findViewById(R.id.book_author);
            viewHolder.ivCover = (ImageView) convertView.findViewById(R.id.book_image);
            viewHolder.txtRentDate = (TextView) convertView.findViewById(R.id.book_rented_on);
            viewHolder.txtReturnDate = (TextView) convertView.findViewById(R.id.book_status);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.txtTitle.setText(currentBook.getTitle());
        viewHolder.txtAuthor.setText(currentBook.getAuthor().getName());

        viewHolder.txtRentDate.setText("Rented on: " + currentBook.getHistory().getLoanDate().toString());
        if (currentBook.getHistory().getReturnDate() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentBook.getHistory().getLoanDate());
            calendar.add(Calendar.DATE, LOAN_DAYS);
            String message = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()).toString();
            viewHolder.txtReturnDate.setText("Return until: " + message);

            int days = (int) ((currentBook.getHistory().getLoanDate().getTime() - new java.sql.Date(new Date().getTime()).getTime()) / MILLISECONDS_IN_DAY);
            days += LOAN_DAYS;
            if (days <= 5) viewHolder.txtReturnDate.setTextColor(Color.rgb(255, 0, 0));
            else if (days <= 10) viewHolder.txtReturnDate.setTextColor(Color.rgb(255, 145, 0));
            else viewHolder.txtReturnDate.setTextColor(Color.rgb(0, 128, 0));

        } else {
            viewHolder.txtReturnDate.setText("Returned on: " + currentBook.getHistory().getReturnDate().toString());
            viewHolder.txtReturnDate.setTextColor(Color.rgb(40, 60, 200));
        }

        Bitmap bm;
        if (currentBook.getCover() == null || currentBook.getCover().length == 0) {
            bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_book_image);
        } else {
            bm = BitmapFactory.decodeByteArray(currentBook.getCover(), 0, currentBook.getCover().length);
        }

        viewHolder.ivCover.setImageBitmap(bm);

        viewHolder.ivCover.setOnClickListener(this);
        viewHolder.ivCover.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }
}
