package com.example.vlad.licenta;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vlad.licenta.model.Book;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class FavouritesList extends Fragment {

    ListView lv;
    private final String KEY_TITLE = "Title";
    private final String KEY_AUTHOR = "Author";
    public static final String TAG = Client.class.getSimpleName();
    protected JSONArray mTasksData;


    List<Book> favouriteBooks;
    private static CustomAdapterBooks adapter;
    public static Fragment favouriteListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_main, container, false);

        favouriteListFragment = this;

        lv = (ListView) rootView.findViewById(R.id.list);

        favouriteBooks = new ArrayList<>();

        String url = ServerProperties.HOST;
        url += "/library/getFavouriteBooks?userId=" + ((Client)getActivity()).getCurrentUser().getId();

        ServerRequestGET<List<Book>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class),
                new AsyncResponse<List<Book>>() {
                    @Override
                    public void actionCompleted(List<Book> res) {
                        if (res == null) res = new ArrayList<>();
                        favouriteBooks = res;
                        adapter = new CustomAdapterBooks(favouriteBooks, getContext());
                        lv.setAdapter(adapter);
                    }
                });

        theServerRequest.execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MiscFunctions.CreateAlertDialog(favouriteListFragment.getActivity(), favouriteBooks.get(position), -1);
            }
        });


        return rootView;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ( this.isVisible() )
        {
            if ( isVisibleToUser )
            {
                String url = ServerProperties.HOST;
                url += "/library/getFavouriteBooks?userId=" + ((Client)getActivity()).getCurrentUser().getId();

                ServerRequestGET<List<Book>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class),
                        new AsyncResponse<List<Book>>() {
                            @Override
                            public void actionCompleted(List<Book> res) {
                                if (res == null) res = new ArrayList<>();
                                favouriteBooks = res;
                                adapter.refresh(favouriteBooks);
                            }
                        });

                theServerRequest.execute();
            }
        }

    }
}