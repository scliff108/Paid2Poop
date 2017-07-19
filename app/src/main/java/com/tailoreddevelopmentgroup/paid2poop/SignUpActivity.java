package com.tailoreddevelopmentgroup.paid2poop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Displays a SignUp form for the user
 * Also links to the login form if they already have an account
 * Creates the parse user
 */
public class SignUpActivity extends Activity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordAgainEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the Sign Up Form
        mUsernameEditText = (EditText) findViewById(R.id.username_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mPasswordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
    }

    // Called when signup button is clicked
    public void signup (View view) {
        // Get the username and passwords
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String passwordAgain = mPasswordAgainEditText.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        // If the username field is empty
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        // If the password field is empty
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        // If the passwords don't match
        if (!passwordAgain.equals(password)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error
        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        // Set up a new Parse User
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        // Call the parse Sign up method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // Dismiss the progress dialog
                dialog.dismiss();
                if (e != null) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } else {
                    Intent intent = new Intent(SignUpActivity.this, SignUpWagesActivity.class);
                    // Clear Task stack and start a new one
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    // Called when login textview is clicked
    public void switchToLogin (View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        // Clear Task stack and start a new one
        startActivity(intent);
    }
}
