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

        bindListView();

        historyListFragment = this;

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

            historyObjects = new ArrayList<>();

            String url = ServerProperties.HOST;
            url += "/library/getUserHistory?userId=" + ((Client)getActivity()).getCurrentUser().getId();

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

                    Book selectedBook = historyObjects.get(position).getBook();

                    Bitmap bm;
                    if (selectedBook.getCover() == null) {
                        bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_book_image);
                    } else {
                        bm = BitmapFactory.decodeByteArray(selectedBook.getCover(), 0, selectedBook.getCover().length);
                    }
                    MiscFunctions.CreateAlertDialog(historyListFragment.getActivity(), bm, selectedBook.getTitle(), selectedBook.getAuthor().getName(), selectedBook.getDescription());

                    // TODO Display loan and return date

            /*String[] keys = {KEY_TITLE, KEY_AUTHOR };
            int[] ids = { android.R.id.text1, android.R.id.text2 };
            String author = "Mihaie Eminescu";
            historyObjects = new ArrayList<>();

            historyObjects.add(new HistoryObject("Title", "Author..","1day"));
            historyObjects.add(new HistoryObject("Title1", "Author1..","2days"));
            historyObjects.add(new HistoryObject("Title2", "Author2..","3days"));

            adapter= new CustomAdapterHistoryBooks(historyObjects,getContext());

            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    HistoryObject historyObject = historyObjects.get(position);


                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_book_image);


                    MiscFunctions.CreateHistoryAlertDialog(historyListFragment.getActivity(), bm, "title", "author", "Descrierea cartii \n va fi scrisa \n pe mai multe randuri");
*/
                    //     Snackbar.make(view, favoritesObject.getName()+"\n"+ favoritesObject.getAuthor(), Snackbar.LENGTH_LONG)
                    //            .setAction("No action", null).show();
                }
            });

        }
    }
}