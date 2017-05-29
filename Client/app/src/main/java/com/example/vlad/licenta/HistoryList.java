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
import com.example.vlad.licenta.model.History;
import com.example.vlad.licenta.model.User;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HistoryList extends Fragment {

    ListView lv;
    private final String KEY_TITLE = "Title";
    private final String KEY_AUTHOR = "Author";
    List<History> historyObjects;
    private static CustomAdapterHistoryBooks adapter;
    public static Fragment historyListFragment;


    public static final String TAG = Client.class.getSimpleName();
    protected JSONArray mTasksData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.history_list, container, false);

        lv = (ListView) rootView.findViewById(R.id.listHistory);

        historyListFragment = this;

        historyObjects = new ArrayList<>();

        String url = ServerProperties.HOST;
        if ( getActivity() instanceof Client )
            url += "/library/getUserHistory?userId=" + ((Client)getActivity()).getCurrentUser().getId();
        else
            url += "/library/getUserHistory?userId=" + ((Administrator)getActivity()).getCurrentUser().getId();


        ServerRequestGET<List<History>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, History.class),
                new AsyncResponse<List<History>>() {
                    @Override
                    public void actionCompleted(List<History> res) {
                        if (res == null) res = new ArrayList<>();
                        historyObjects = res;
                        adapter = new CustomAdapterHistoryBooks(historyObjects, getContext());
                        lv.setAdapter(adapter);
                    }
                });

        theServerRequest.execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Book selectedBook = historyObjects.get(position).getBook();

                String url = ServerProperties.HOST;
                url += "/library/isFavourite";
                if ( historyListFragment.getActivity() instanceof Client )
                    url += "?userId=" + ((Client)historyListFragment.getActivity()).getCurrentUser().getId();
                else
                    url += "?userId=" + ((Administrator)historyListFragment.getActivity()).getCurrentUser().getId();

                url += "&bookId=" + historyObjects.get(position).getId();

                ServerRequestGET<String> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(String.class),
                        new AsyncResponse<String>() {
                            @Override
                            public void actionCompleted(String obj) {
                                if ( obj != null && obj.compareTo("1") == 0 )
                                    MiscFunctions.CreateAlertDialog(historyListFragment.getActivity(), selectedBook, Integer.parseInt(obj));
                            }
                        });

                theServerRequest.execute();

            }
        });

        return rootView;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ( this.isVisible() )
        {
            if ( isVisibleToUser ) {
                String url = ServerProperties.HOST;
                url += "/library/list";

                ServerRequestGET<List<History>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, History.class),
                        new AsyncResponse<List<History>>() {
                            @Override
                            public void actionCompleted(List<History> res) {
                                if (res == null) res = new ArrayList<>();
                                historyObjects = res;
                                adapter.refresh(historyObjects);
                            }
                        });

                theServerRequest.execute();
            }
        }

    }

}