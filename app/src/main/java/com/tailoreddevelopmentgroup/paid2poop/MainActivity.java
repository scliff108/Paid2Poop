package com.tailoreddevelopmentgroup.paid2poop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    // Timer Variables
    long startDate = 0;
    long endDate = 0;
    long milliseconds = 0;
    Handler mHandler = new Handler();
    SharedPreferences sharedPreferences;

    // Views
    private TextView mPoopTimer;
    private TextView mWageEarnedTextView;
    private TextView mWageTextView;
    private TextView mTotalTimeTextView;
    private TextView mTotalEarnedTextView;
    private Button mCrapButton;
    private RadioGroup mRadioGroup;
    private RadioButton mCurrentRadioButton;

    // View data
    private double mWage = 30;
    private double mWagePerSecond = mWage / 3600.0;
    private double mMultiplier;
    private double mSeconds = 0;
    private double mWageEarned;
    private int mTotalTime = 0;
    private double mTotalEarned = 0;

    // Helpers
    private boolean isPooping = false;
    private boolean saveEnabled = false;
    ParseUser mUser;
    private NumberFormat mCurrencyFormat = NumberFormat.getCurrencyInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForOngoingPoop();

        initializeViews();

        setRadioGroupOnChangeListener();
    }

    private void checkForOngoingPoop() {
        // Initialize sharedPreferences
        sharedPreferences = this.getSharedPreferences("com.tailoreddevelopmentgroup.paid2poop", Context.MODE_PRIVATE);

        // Remove poop information if poop took over 20 minutes
        // Get it if under 20 minutes
        if (new Date().getTime() - sharedPreferences.getLong("startDate", 0) > 1200000) {

            if (sharedPreferences.getLong("milliseconds", 0) != 0) {

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Welcome Back")
                        .setMessage("Your last poop was aborted. We remember that you left the app " + milliseconds + " seconds into your poop. Would you like to save it?")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveToParse();
                            }
                        })
                        .setNegativeButton("Abort", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeTimeInformation();
                            }
                        })
                        .show();
            }

        } else {

            getTimeInformation();

        }
    }

    private void initializeViews() {

        // Initialize Text Views and Buttons
        mPoopTimer = (TextView) findViewById(R.id.poop_timer);

        mWageEarnedTextView = (TextView) findViewById(R.id.wages_earned);

        mWageTextView = (TextView) findViewById(R.id.wage_text_view);

        mTotalTimeTextView = (TextView) findViewById(R.id.total_time_main);

        mTotalEarnedTextView = (TextView) findViewById(R.id.total_earned_main);

        mCrapButton = (Button) findViewById(R.id.start_pooping_button);

        mUser = ParseUser.getCurrentUser();

        // Initialize Radio Group
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mCurrentRadioButton = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());

        // Initialize Wage
        mWage = Double.parseDouble(mUser.get("basePay").toString());

        mWageTextView.setText(getString(R.string.start_wage) + mCurrencyFormat.format(mWage));


        // Initialize Wage Multiplier
        mMultiplier = Double.parseDouble(String.valueOf(mCurrentRadioButton.getTag()));

        // Set the total stats
        mTotalTime = Integer.parseInt(String.valueOf(mUser.get("time")));

        mTotalEarned = Double.parseDouble(String.valueOf(mUser.get("earned")));

        // Set stat text views
        mTotalTimeTextView.setText(String.valueOf(mTotalTime));

        mTotalEarnedTextView.setText(mCurrencyFormat.format(mTotalEarned));
    }

    private void setRadioGroupOnChangeListener() {

        // Change multiplier when radio button is clicked
        // Update wage per second and wage earned
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Get the pay rate button
                mCurrentRadioButton = (RadioButton) findViewById(checkedId);

                // Get the pay rate button's tag which is the multiplier
                mMultiplier = Double.parseDouble(mCurrentRadioButton.getTag().toString());

                // Set the the wage text view to equal the wage times the pay rate
                mWageTextView.setText(getString(R.string.start_wage) + mCurrencyFormat.format(mWage * mMultiplier));

                // Update the wage earned text view to reflect the new effective hourly wage
                mWageEarned = mWage * mMultiplier * mSeconds;

                mWageEarnedTextView.setText(mCurrencyFormat.format(mWageEarned));
            }
        });

    }


    // When Start or Stop Button is hit
    public void startStopPooping (View view) {

        if (!isPooping) {

            hitStartButton();

        } else {

            hitStopButton();

        }

        // Toggle isPooping;
        isPooping = !isPooping;

    }

    private void hitStartButton() {

        startDate = new Date().getTime() - milliseconds;

        // Make Wages Visible
        mWageEarnedTextView.setVisibility(View.VISIBLE);

        // Change Button
        mCrapButton.setBackgroundResource(R.drawable.button_stop);

        mCrapButton.setText(getString(R.string.stop_button));

        // Disable Save
        saveEnabled = false;

        removeTimeInformation();
        setTimeInformation();

        mHandler.postDelayed(updateTimer, 0);

    }

    private void hitStopButton() {

        // Stop the timer
        mHandler.removeCallbacks(updateTimer);

        endDate = new Date().getTime();

        removeTimeInformation();
        setTimeInformation();

        // Change Button
        mCrapButton.setBackgroundResource(R.drawable.button_start);

        mCrapButton.setText(getString(R.string.start_button));

        // Enable Saving
        saveEnabled = true;

    }

    public void savePoop (View view) {

        if (saveEnabled) {
            // AlertDialog to ask if the user should save, save and share, or cancel
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_save)
                    .setTitle("Save")
                    .setMessage("Another poop, another dollar.\nWould you like to share your poop?")
                    .setPositiveButton("Save & Share", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            sharePoop();
                            saveToParse();

                        }
                    })
                    .setNegativeButton("Save", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            saveToParse();

                        }
                    })
                    .setNeutralButton("Cancel", null)
                    .show();

        } else {

            // Alerts the user that they must complete a poop before saving
            Toast.makeText(MainActivity.this, getString(R.string.save_disabled), Toast.LENGTH_LONG).show();

        }
    }

    private void sharePoop () {

        Toast.makeText(MainActivity.this, "Sharing Poop", Toast.LENGTH_SHORT).show();

    }

    // Parse Information
    private void saveToParse () {

        // Parse Data
        savePoopInformation();
        updateUserInformation();

        // System Preferences
        removeTimeInformation();

        // Start Next Activity
        Intent intent = new Intent(MainActivity.this, StatsActivity.class);
        startActivity(intent);

    }

    private void savePoopInformation() {

        ParseObject poop = new ParseObject("Poop");

        poop.put("creator", mUser.getObjectId());

        poop.put("time", mSeconds);

        poop.put("worth", mWageEarned);

        // payScale of 0 means standard, 1 means overtime, 2 means time and a half
        poop.put("payScale", (mMultiplier - 1) * 2);

        poop.put("payRate", mWage * mMultiplier);

        poop.saveInBackground();

    }

    private void updateUserInformation() {

        mUser.put("time", mTotalTime);

        mUser.put("earned", mTotalEarned);

        mUser.saveInBackground();

    }

    // System Preferences
    private void removeTimeInformation () {

        sharedPreferences.edit().remove("startDate").apply();

        sharedPreferences.edit().remove("endDate").apply();

        sharedPreferences.edit().remove("milliseconds").apply();

    }

    private void getTimeInformation () {

        startDate = sharedPreferences.getLong("startDate", 0);

        endDate = sharedPreferences.getLong("endDate", 0);

        milliseconds = sharedPreferences.getLong("milliseconds", 0);

    }

    private void setTimeInformation () {

        sharedPreferences.edit().putLong("startDate", startDate).apply();

        sharedPreferences.edit().putLong("endDate", endDate).apply();

        sharedPreferences.edit().putLong("milliseconds", milliseconds).apply();

    }

    // Options Menu
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.history:
                Intent history = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(history);
                return true;

            case R.id.settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                return true;

        }

        return super.onOptionsItemSelected(item);

    }

    // Timer
    public Runnable updateTimer = new Runnable() {
        @Override
        public void run() {

            milliseconds = new Date().getTime() - startDate;

            int secs = (int) milliseconds / 1000;

            mSeconds = (double) secs;

            long mins = secs / 60;

            secs = secs % 60;

            long millis = milliseconds % 1000 / 10;

            String formattedTime = String.valueOf(mins) + ":" + String.format(Locale.US, "%02d", secs) + ":" + String.format(Locale.US, "%02d", millis);

            mPoopTimer.setText(formattedTime);

            // Update Wage Earned Text View
            mWageEarned = mWagePerSecond * mMultiplier * mSeconds;

            mWageEarnedTextView.setText(mCurrencyFormat.format(mWageEarned));

            // Update Stats and Text Views

            mTotalTimeTextView.setText(String.valueOf(mTotalTime + secs));

            mTotalEarnedTextView.setText(mCurrencyFormat.format(mTotalEarned + mWageEarned));

            mHandler.postDelayed(this, 0);

        }
    };

    @Override
    protected void onPause () {
        super.onPause();

        removeTimeInformation();

        setTimeInformation();
    }

    @Override
    public void onBackPressed () {
    }

}
