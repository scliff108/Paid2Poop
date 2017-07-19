package com.tailoreddevelopmentgroup.paid2poop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends Activity {

    int mSeconds = 0;
    double mWorth = 0;
    ArrayList<ParseObject> mPoopArrayList;

    TextView mTotalNumPoops;
    TextView mTotalTimePooping;
    TextView mTotalWagesEarned;
    ListView mPoopsListView;

    NumberFormat mCurrency = NumberFormat.getCurrencyInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mPoopArrayList = new ArrayList<>();


        // Initialize Text Views
        mTotalNumPoops = (TextView) findViewById(R.id.number_total_poops);

        mTotalTimePooping = (TextView) findViewById(R.id.number_total_time);

        mTotalWagesEarned = (TextView) findViewById(R.id.number_total_wages_earned);

        // Query for poop history
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Poop");

        query.whereEqualTo("creator", ParseUser.getCurrentUser().getObjectId());

        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> poops, ParseException e) {

                if (e == null) {

                    for (ParseObject deuce : poops) {

                        // Gets all of the poops from Parse onto an array list
                        mPoopArrayList.add(deuce);

                        mSeconds += Integer.parseInt(deuce.get("time").toString());

                        mWorth +=  Double.parseDouble(deuce.get("worth").toString());

                    }

                    // Set text of the text views for total stats
                    mTotalNumPoops.setText(String.valueOf(poops.size()));

                    mTotalTimePooping.setText(String.valueOf(mSeconds));

                    mTotalWagesEarned.setText(mCurrency.format(mWorth));

                    ParseUser user = ParseUser.getCurrentUser();

                    user.put("time", mSeconds);

                    user.put("earned", mWorth);

                    // Individual poop list view with Parse Adapter
                    mPoopsListView = (ListView) findViewById(R.id.poop_history_list_view);

                    ParseAdapter adapter = new ParseAdapter(StatsActivity.this, mPoopArrayList);

                    mPoopsListView.setAdapter(adapter);

                    mPoopsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent i = new Intent(StatsActivity.this, EditPoop.class);

                            i.putExtra("Poop", mPoopArrayList.get(position).getObjectId());

                            startActivity(i);

                        }
                    });

                } else {

                    e.printStackTrace();

                }
            }
        });
    }
}
