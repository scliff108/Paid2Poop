package com.tailoreddevelopmentgroup.paid2poop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditPoop extends Activity {

    EditText mMinutesEditText;
    EditText mSecondsEditText;
    EditText mPayRateEditText;
    ParseObject mShit;
    int mMinutes;
    int mSeconds;
    double mPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_poop);

        Intent i = getIntent();

        String objectId = i.getStringExtra("Poop");

        Log.i("Poop", "Editing Poop " + objectId);

        mMinutesEditText = (EditText) findViewById(R.id.minutes_edit_text);

        mSecondsEditText = (EditText) findViewById(R.id.seconds_edit_text);

        mPayRateEditText = (EditText) findViewById(R.id.base_pay_edit_text);

        mShit = null;

        mMinutes = 0;

        mSeconds = 0;

        mPay = 0;

        Button saveButton = (Button) findViewById(R.id.save_button);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Poop");

        query.whereEqualTo("objectId", objectId);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    for (ParseObject poop : objects) {

                        mShit = poop;

                        int time = Integer.parseInt(poop.get("time").toString());

                        mMinutes = time / 60;

                        mSeconds = time % 60;

                        mPay = Double.parseDouble(poop.get("payRate").toString());

                        mMinutesEditText.setText(String.valueOf(mMinutes));

                        mSecondsEditText.setText(String.valueOf(mSeconds));

                        mPayRateEditText.setText(NumberFormat.getCurrencyInstance().format(mPay));

                    }

                } else {

                    e.printStackTrace();

                }

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String minutes = mMinutesEditText.getText().toString();

                String seconds = mSecondsEditText.getText().toString();

                String pay = mPayRateEditText.getText().toString();

                if (minutes.length() == 0) {

                    mMinutes = 0;

                }

                if (seconds.length() == 0) {

                    mSeconds = 0;

                }

                if (pay.length() == 0) {

                    mPay = Double.parseDouble(ParseUser.getCurrentUser().get("basePay").toString());

                }

                try {

                    mMinutes = NumberFormat.getNumberInstance(Locale.US).parse(minutes).intValue();

                    mSeconds = NumberFormat.getNumberInstance(Locale.US).parse(seconds).intValue();

                    mPay = NumberFormat.getCurrencyInstance(Locale.US).parse(pay).doubleValue();

                } catch (java.text.ParseException e) {

                    e.printStackTrace();

                }

                int totalTime = mMinutes * 60 + mSeconds;

                mShit.put("time", totalTime);

                mShit.put("payRate", mPay);

                mShit.put("worth", mPay / 3600 * totalTime);

                mShit.saveInBackground();

                Intent i = new Intent(EditPoop.this, StatsActivity.class);

                startActivity(i);

            }
        });

    }
}
