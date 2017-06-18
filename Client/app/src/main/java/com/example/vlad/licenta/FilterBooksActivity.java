package com.example.vlad.licenta;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

public class FilterBooksActivity extends AppCompatActivity {
    private FloatingActionButton fab_filter;
    private AutoCompleteTextView tv_filterTitle, tv_filterAuthor, tv_filterPublisher;
    private CheckBox cb_Availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters_layout);

        fab_filter = (FloatingActionButton) findViewById(R.id.fab_filter);
        tv_filterTitle = (AutoCompleteTextView) findViewById(R.id.tv_titleToFilter);
        tv_filterAuthor = (AutoCompleteTextView) findViewById(R.id.tv_authorToFilter);
        tv_filterPublisher = (AutoCompleteTextView) findViewById(R.id.tv_publisherToFilter);

        cb_Availability = (CheckBox) findViewById(R.id.cb_availabilityToFilter);

        Filters tempFilter = Filters.getInstance();
        tv_filterAuthor.setText(tempFilter.getAuthor());
        tv_filterTitle.setText(tempFilter.getTitle());
        tv_filterPublisher.setText(tempFilter.getPublisher());
        if ( !tempFilter.getAvailable().equals("") )
            cb_Availability.setChecked(true);
    }

    public void applyFilters(View v)
    {
        Filters tempFilter = Filters.getInstance();
        tempFilter.setAuthor(tv_filterAuthor.getText().toString());
        tempFilter.setTitle(tv_filterTitle.getText().toString());
        tempFilter.setPublisher(tv_filterPublisher.getText().toString());
        if (cb_Availability.isChecked())
            tempFilter.setAvailable(String.valueOf(cb_Availability.isChecked()));
        else
            tempFilter.setAvailable("");
        finish();
    }
}
