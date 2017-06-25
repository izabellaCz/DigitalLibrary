package com.example.vlad.licenta;

import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

public class BooksList extends Fragment implements  DialogInterface.OnDismissListener, View.OnClickListener{

    private ListView lv;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Book> listOfBooks = new ArrayList<>();

    private static CustomAdapterBooks adapter;
    public static Fragment booksListFragment;

    private FloatingActionButton fab_filter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.books_list, container, false);

        lv = (ListView) rootView.findViewById(R.id.listBooks);
        fab_filter = (FloatingActionButton) rootView.findViewById(R.id.fab_filter);
        fab_filter.setOnClickListener(this);

        if ( ((LoggedInActivity)getActivity()).getCurrentUser().getType().equals("ADMINISTRATOR") )
            fab_filter.setVisibility(View.GONE);

        booksListFragment = this;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MiscFunctions.createAlertDialog(booksListFragment.getActivity(), listOfBooks.get(position), (BooksList)booksListFragment);

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
                refresh();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
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
                refresh();
            }
        }

    }

    private void refresh() {
        Filters filters = Filters.getInstance();

        String url = ServerProperties.HOST;
        url += "/library/filter?userId=";
        url += ((LoggedInActivity) getActivity()).getCurrentUser().getId();
        url += "&author=" + filters.getAuthor();
        url += "&title=" + filters.getTitle();
        url += "&publisher=" + filters.getPublisher();
        url += "&isAvailable=" + filters.getAvailable();

        ServerRequestGET<List<Book>> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructCollectionType(List.class, Book.class),
                new AsyncResponse<List<Book>>() {
                    @Override
                    public void actionCompleted(List<Book> res) {
                        if (res == null) res = new ArrayList<>();
                        listOfBooks.clear();
                        listOfBooks.addAll(res);
                        adapter = new CustomAdapterBooks(listOfBooks, getContext());
                        lv.setAdapter(adapter);
                        if (listOfBooks.size() == 0)
                            MiscFunctions.createToast(getContext().getApplicationContext(), "No books were found!");
                    }
                });

        theServerRequest.execute();
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        refresh();
    }
}