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

public class HistoryList extends Fragment {

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

        refresh();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Book selectedBook = booksInHistory.get(position);
                MiscFunctions.createAlertDialog(historyListFragment.getActivity(), selectedBook);

            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefreshHistory);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refresh();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (isVisibleToUser) {
                refresh();
            }
        }

    }

    public void refresh() {
        String url = ServerProperties.HOST;
        url += "/library/getUserHistory?userId=";
        url += ((LoggedInActivity)getActivity()).getCurrentUser().getId();

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
    }

}