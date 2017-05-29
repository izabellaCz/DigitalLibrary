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

        lv = (ListView) rootView.findViewById(R.id.list);

        bindListView();

        favouriteListFragment = this;

        return rootView;

    }

    public void bindListView() {
        new GetLibraryInformation(getActivity(),lv).execute("");
    }


    class GetLibraryInformation extends AsyncTask<Object, Void, JSONArray> {
        ListView mListView;
        Activity mContext;


        public GetLibraryInformation(Activity context,ListView gview) {
            this.mListView=gview;
            this.mContext=context;
        }

        @Override
        protected JSONArray doInBackground(Object... params) {
            JSONArray jsonResponse = null;

            // Get JSON data, all coming through fine
            return jsonResponse;
        }


        @Override
        protected void onPostExecute(JSONArray result) {
            mTasksData = result;
            /*String[] keys = {KEY_TITLE, KEY_AUTHOR };
            int[] ids = { android.R.id.text1, android.R.id.text2 };
            String author = "Mihaie Eminescu";*/
            /*favoritesObjects = new ArrayList<>();

            favoritesObjects.add(new FavoritesObject("Title", "Author.."));
            favoritesObjects.add(new FavoritesObject("Title1", "Author1.."));
            favoritesObjects.add(new FavoritesObject("Title2", "Author2.."));

            adapter= new CustomAdapterBooks(favoritesObjects,getContext());*/

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

                    Book selectedBook = favouriteBooks.get(position);

                    Bitmap bm;
                    if (selectedBook.getCover() == null) {
                        bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_book_image);
                    } else {
                        bm = BitmapFactory.decodeByteArray(selectedBook.getCover(), 0, selectedBook.getCover().length);
                    }
                    MiscFunctions.CreateAlertDialog(favouriteListFragment.getActivity(), bm, selectedBook.getTitle(), selectedBook.getAuthor().getName(), selectedBook.getDescription());


                    //         Snackbar.make(view, favoritesObject.getName()+"\n"+ favoritesObject.getAuthor(), Snackbar.LENGTH_LONG)
                    //                .setAction("No action", null).show();
                }
            });

        }
    }
}