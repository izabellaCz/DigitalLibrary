package com.example.vlad.licenta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.licenta.model.Book;

import java.util.ArrayList;
import java.util.List;


public class CustomAdapterBooks extends ArrayAdapter<Book> {

    private List<Book> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        ImageView cover;
        ImageView info;
    }

    public CustomAdapterBooks(List<Book> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public void refresh(List<Book> data)
    {
        this.dataSet = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Nullable
    @Override
    public Book getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Book selectedBook = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.book_title);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.book_author);
            viewHolder.cover = (ImageView) convertView.findViewById(R.id.book_image);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(selectedBook.getTitle());
        viewHolder.txtType.setText(selectedBook.getAuthor().getName());

        Bitmap bm;
        if (selectedBook.getCover() == null || selectedBook.getCover().length == 0) {
            bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_book_image);
        } else {
            bm = BitmapFactory.decodeByteArray(selectedBook.getCover(), 0, selectedBook.getCover().length);
        }

        viewHolder.cover.setImageBitmap(bm);
        viewHolder.cover.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
