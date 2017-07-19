package com.tailoreddevelopmentgroup.paid2poop;

import android.app.Activity;
import android.content.Intent;
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

public class SignUpWagesActivity extends Activity {

    LinearLayout salaryLinearLayout;
    LinearLayout hourlyLinearLayout;
    Button salaryButton;
    Button hourlyButton;

    boolean wageIsSalary = true;
    double mWage;
    ParseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mUser = ParseUser.getCurrentUser();

        initializeViews();

        setSalaryOnClickListener();

        setHourlyOnClickListener();
    }

    private void initializeViews() {

        salaryLinearLayout = (LinearLayout) findViewById(R.id.salary_stuff_linear_layout_sign_up);

        hourlyLinearLayout = (LinearLayout) findViewById(R.id.hourly_stuff_linear_layout_sign_up);

        salaryButton = (Button) findViewById(R.id.salary_button_sign_up);

        hourlyButton = (Button) findViewById(R.id.hourly_button_sign_up);

        salaryButton.setBackgroundResource(R.drawable.button_pressed);

        hourlyButton.setBackgroundResource(R.drawable.button_not_pressed);

    }

    private void setSalaryOnClickListener() {

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

    }

    private void setHourlyOnClickListener() {

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


    // When Save button is clicked
    public void setWage (View view) throws ParseException {

        if (wageIsSalary) {

            validateSalaryTextFields();

        } else {

            validateHourlyTextFields();
        }

    }

    private void validateSalaryTextFields() throws ParseException {

        EditText salaryEditText = (EditText) findViewById(R.id.salary_edit_text_sign_up);

        EditText hoursPerWeekEditText = (EditText) findViewById(R.id.hours_per_week_edit_text_sign_up);

        EditText vacationWeeksEditText = (EditText) findViewById(R.id.vacation_weeks_edit_text_sign_up);

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

            Toast.makeText(SignUpWagesActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();

            return;

        } else {

            double salary = NumberFormat.getNumberInstance(Locale.US).parse(salaryString).doubleValue();

            double hoursPerWeek = NumberFormat.getNumberInstance(Locale.US).parse(hoursPerWeekString).doubleValue();

            double vacationWeeks = NumberFormat.getNumberInstance(Locale.US).parse(vacationWeeksString).doubleValue();

            mWage = salary / ((52 - vacationWeeks) * hoursPerWeek);

            initializeUserInfo();

            mUser.put("salary", salary);

            mUser.put("hoursWorkedPerWeek", hoursPerWeek);

            mUser.put("weeksVacation", vacationWeeks);

            mUser.put("paidHourly", 0);

            Intent intent = new Intent(SignUpWagesActivity.this, DispatchActivity.class);
            // Clear Task stack and start a new one
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

    }

    private void validateHourlyTextFields() throws ParseException {

        EditText hourlyWageEditText = (EditText) findViewById(R.id.hourly_wage_edit_text_sign_up);

        String hourlyWageString = hourlyWageEditText.getText().toString();

        // StringBuilder if there is an error
        boolean validationError = false;

        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));

        if (hourlyWageString.length() == 0) {

            validationError = true;

            validationErrorMessage.append(getString(R.string.error_blank_hourly_wage));

        }

        if (validationError) {

            Toast.makeText(SignUpWagesActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();

            return;

        } else {

            mWage = NumberFormat.getNumberInstance(Locale.US).parse(hourlyWageString).doubleValue();

            initializeUserInfo();

            mUser.put("salary", 0);

            mUser.put("hoursWorkedPerWeek", 0);

            mUser.put("weeksVacation", 0);

            mUser.put("paidHourly", 1);

        }
    }

    private void initializeUserInfo() {

        mUser.put("time", 0);

        mUser.put("earned", 0);

        mUser.put("basePay", mWage);

    }
}
