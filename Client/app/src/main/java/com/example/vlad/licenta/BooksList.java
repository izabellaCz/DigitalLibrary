package com.example.vlad.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vlad.licenta.model.Book;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class BooksList extends Fragment implements  View.OnClickListener{

    private ListView lv;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Book> listOfBooks;

    private static CustomAdapterBooks adapter;
    public static Fragment booksListFragment;

    private FloatingActionButton fab_filter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.books_list, container, false);

        lv = (ListView) rootView.findViewById(R.id.listBooks);
        fab_filter = (FloatingActionButton) rootView.findViewById(R.id.fab_filter);

        if ( ((LoggedInActivity)getActivity()).getCurrentUser().getType().equals("ADMINISTRATOR") )
            fab_filter.setVisibility(View.GONE);

        booksListFragment = this;

        listOfBooks = new ArrayList<>();

        String url = ServerProperties.HOST;
        url += "/library/list?userId=";
        url += ((LoggedInActivity)getActivity()).getCurrentUser().getId();

        ServerRequestGET<List<Book>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class),
                new AsyncResponse<List<Book>>() {
                    @Override
                    public void actionCompleted(List<Book> res) {
                        listOfBooks = res;
                        adapter = new CustomAdapterBooks(listOfBooks, getContext());
                        lv.setAdapter(adapter);
                    }
                });

        theServerRequest.execute();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MiscFunctions.createAlertDialog(booksListFragment.getActivity(), listOfBooks.get(position));

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                String url = ServerProperties.HOST;
                url += "/library/list?userId=";
                url += ((LoggedInActivity) getActivity()).getCurrentUser().getId();

                ServerRequestGET<List<Book>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class),
                        new AsyncResponse<List<Book>>() {
                            @Override
                            public void actionCompleted(List<Book> res) {
                                if (res == null) res = new ArrayList<>();
                                listOfBooks = res;
                                adapter.refresh(listOfBooks);
                            }
                        });

                theServerRequest.execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listBooks) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Pick Action");
            String[] menuItems = new String[]{"Add To Favorites",""};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ( this.isVisible() )
        {
            if ( isVisibleToUser ) {
                String url = ServerProperties.HOST;
                url += "/library/list?userId=";
                url += ((LoggedInActivity) getActivity()).getCurrentUser().getId();

                ServerRequestGET<List<Book>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class),
                        new AsyncResponse<List<Book>>() {
                            @Override
                            public void actionCompleted(List<Book> res) {
                                if (res == null) res = new ArrayList<>();
                                listOfBooks = res;
                                adapter.refresh(listOfBooks);
                            }
                        });

                theServerRequest.execute();
            }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_filter:
                Intent intent = new Intent(getContext().getApplicationContext(), FilterBooksActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}