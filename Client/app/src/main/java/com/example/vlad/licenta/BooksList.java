package com.example.vlad.licenta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class BooksList extends Fragment {

    ListView lv;
    private final String KEY_TITLE = "Title";
    private final String KEY_AUTHOR = "Author";

    List<Book> listOfBooks;

    private static CustomAdapterBooks adapter;
    public static Fragment booksListFragment;

    public static final String TAG = Client.class.getSimpleName();
    protected JSONArray mTasksData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.books_list, container, false);

        lv = (ListView) rootView.findViewById(R.id.listBooks);

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

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.list) {
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
}