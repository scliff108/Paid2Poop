package com.tailoreddevelopmentgroup.paid2poop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ParseAdapter extends ArrayAdapter<ParseObject> {

    public ParseAdapter (Context context, ArrayList<ParseObject> parseObjects) {
        super(context, 0, parseObjects);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        // Get the ParseObject
        ParseObject poop = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_poop, parent, false);

        }

        // Lookup view to populate with data
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date_text_view);

        TextView timeTextView = (TextView) convertView.findViewById(R.id.time_text_view);

        TextView worthTextView = (TextView) convertView.findViewById(R.id.poop_worth_text_view);

        TextView lengthTextView = (TextView) convertView.findViewById(R.id.poop_length_text_view);


        // Populate the data
        Date date = poop.getCreatedAt();

        dateTextView.setText(DateFormat.getDateInstance().format(date));

        timeTextView.setText(DateFormat.getTimeInstance().format(date));

        worthTextView.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(poop.get("worth"))));

        int length = Integer.parseInt(String.valueOf(poop.get("time")));

        lengthTextView.setText(String.valueOf(length / 60) + ":" + String.format(Locale.US, "%02d", length % 60));

        return convertView;
    }
}