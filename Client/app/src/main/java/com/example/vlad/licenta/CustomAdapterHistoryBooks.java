package com.example.vlad.licenta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vlad.licenta.model.Book;
import com.example.vlad.licenta.model.History;

import java.util.ArrayList;
import java.util.List;


public class CustomAdapterHistoryBooks extends ArrayAdapter<History> implements View.OnClickListener{

    private List<History> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtDay;
        ImageView info;
    }

    public CustomAdapterHistoryBooks(List<History> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }


    public void refresh(List<History> data)
    {
        this.dataSet = data;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);//

//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +favoritesObject.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
       History historyObject = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.book_title);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.book_author);
            viewHolder.txtDay = (TextView) convertView.findViewById(R.id.book_days);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.book_image);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(historyObject.getBook().getTitle());
        viewHolder.txtType.setText(historyObject.getBook().getAuthor().getName());
        viewHolder.txtDay.setText(historyObject.getLoanDate().toString());        // getDaysToReturn());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
