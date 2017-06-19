package com.example.vlad.licenta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vlad.licenta.model.Book;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.ArrayList;
import java.util.List;

public class AdminHistoryList extends Fragment {

    private ListView lv;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Book> booksInHistory;
    private static CustomAdapterHistoryBooks adapter;
    public static Fragment historyListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_list, container, false);

        lv = (ListView) rootView.findViewById(R.id.listHistory);

        historyListFragment = this;

        booksInHistory = new ArrayList<>();

        String url = ServerProperties.HOST;
        url += "/library/getAdminHistory?userId="
                + ((LoggedInActivity) getActivity()).getCurrentUser().getId();

        ServerRequestGET<List<Book>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class),
                new AsyncResponse<List<Book>>() {
                    @Override
                    public void actionCompleted(List<Book> res) {
                        if (res == null) res = new ArrayList<>();
                        booksInHistory = res;
                        adapter = new CustomAdapterHistoryBooks(booksInHistory, getContext());
                        lv.setAdapter(adapter);
                    }
                });

        theServerRequest.execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Book selectedBook = booksInHistory.get(position);
                MiscFunctions.createAdminHistoryDetailsDialog(historyListFragment.getActivity(), selectedBook);

            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefreshHistory);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                String url = ServerProperties.HOST;
                url += "/library/getAdminHistory?userId=";
                url += ((LoggedInActivity) getActivity()).getCurrentUser().getId();

                ServerRequestGET<List<Book>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class),
                        new AsyncResponse<List<Book>>() {
                            @Override
                            public void actionCompleted(List<Book> res) {
                                if (res == null) res = new ArrayList<>();
                                booksInHistory = res;
                                adapter.refresh(booksInHistory);
                            }
                        });

                theServerRequest.execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

    }
}
