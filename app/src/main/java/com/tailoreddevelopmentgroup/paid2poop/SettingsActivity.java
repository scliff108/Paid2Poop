package com.tailoreddevelopmentgroup.paid2poop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseUser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class SettingsActivity extends Activity {

    boolean wageIsSalary = true;
    double mWage;
    ParseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mUser = ParseUser.getCurrentUser();

        final LinearLayout salaryLinearLayout = (LinearLayout) findViewById(R.id.salary_stuff_linear_layout);

        final LinearLayout hourlyLinearLayout = (LinearLayout) findViewById(R.id.hourly_stuff_linear_layout);


        final Button salaryButton = (Button) findViewById(R.id.salary_button);

        final Button hourlyButton = (Button) findViewById(R.id.hourly_button);

        //TODO get default wage type and set the correct background resources

        if (mUser.get("paidHourly").toString().equals("0")) {
            wageIsSalary = false;
        }

        salaryButton.setBackgroundResource(R.drawable.button_pressed);

        hourlyButton.setBackgroundResource(R.drawable.button_not_pressed);

        salaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                salaryButton.setBackgroundResource(R.drawable.button_pressed);

                hourlyButton.setBackgroundResource(R.drawable.button_not_pressed);

                wageIsSalary = true;

                salaryLinearLayout.setVisibility(View.VISIBLE);

                hourlyLinearLayout.setVisibility(View.INVISIBLE);

            }
        });

        hourlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hourlyButton.setBackgroundResource(R.drawable.button_pressed);

                salaryButton.setBackgroundResource(R.drawable.button_not_pressed);

                wageIsSalary = false;

                salaryLinearLayout.setVisibility(View.INVISIBLE);

                hourlyLinearLayout.setVisibility(View.VISIBLE);

            }
        });
    }

    public void setWage (View view) throws ParseException {

        if (wageIsSalary) {

            EditText salaryEditText = (EditText) findViewById(R.id.salary_edit_text);

            EditText hoursPerWeekEditText = (EditText) findViewById(R.id.hours_per_week_edit_text);

            EditText vacationWeeksEditText = (EditText) findViewById(R.id.vacation_weeks_edit_text);

            String salaryString = salaryEditText.getText().toString();

            String hoursPerWeekString = hoursPerWeekEditText.getText().toString();

            String vacationWeeksString = vacationWeeksEditText.getText().toString();

            // StringBuilder if there is an error
            boolean validationError = false;

            StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));

            // If the salary field is empty
            if (salaryString.length() == 0) {

                validationError = true;

                validationErrorMessage.append(getString(R.string.error_blank_salary));

            }

            // If the hours per week field is empty
            if (hoursPerWeekString.length() == 0) {

                if (validationError) {

                    validationErrorMessage.append(getString(R.string.error_join));

                }

                validationError = true;

                validationErrorMessage.append(getString(R.string.error_blank_hours_per_week));

            }

            // If the vacation weeks field is empty
            if (vacationWeeksString.length() == 0) {

                if (validationError) {

                    validationErrorMessage.append(getString(R.string.error_join));

                }

                validationError = true;

                validationErrorMessage.append(getString(R.string.error_blank_vacation_weeks));

            }

            validationErrorMessage.append(getString(R.string.error_end));

            // If there is a validation error, display the error
            if (validationError) {

                Toast.makeText(SettingsActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();

                return;

            }

            double salary = NumberFormat.getNumberInstance(Locale.US).parse(salaryString).doubleValue();

            double hoursPerWeek = NumberFormat.getNumberInstance(Locale.US).parse(hoursPerWeekString).doubleValue();

            double vacationWeeks = NumberFormat.getNumberInstance(Locale.US).parse(vacationWeeksString).doubleValue();

            mWage = salary / ((52 - vacationWeeks) * hoursPerWeek);

        } else {

            EditText hourlyWageEditText = (EditText) findViewById(R.id.hourly_wage_edit_text);

            String hourlyWageString = hourlyWageEditText.getText().toString();

            // StringBuilder if there is an error
            boolean validationError = false;

            StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));

            if (hourlyWageString.length() == 0) {

                validationError = true;

                validationErrorMessage.append(getString(R.string.error_blank_hourly_wage));

            }

            if (validationError) {

                Toast.makeText(SettingsActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();

                return;

            }

            mWage = NumberFormat.getNumberInstance(Locale.US).parse(hourlyWageString).doubleValue();

        }

        mUser.put("basePay", mWage);

        int paidHourly = wageIsSalary ? 1 : 0;

        mUser.put("paidHourly", paidHourly);
    }
}
