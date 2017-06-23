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


public class FavouritesList extends Fragment {

    private ListView lv;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Book> favouriteBooks;
    private CustomAdapterBooks adapter;
    public static Fragment favouriteListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.favourites_list, container, false);

        favouriteListFragment = this;

        refresh();

        lv = (ListView) rootView.findViewById(R.id.listFavourites);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MiscFunctions.createAlertDialog(favouriteListFragment.getActivity(), favouriteBooks.get(position));
            }
        });


        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefreshFavs);
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
    }
}