package com.tailoreddevelopmentgroup.paid2poop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Shows when the user is not logged in
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    // Called when sign up button is clicked
    public void startSignUp (View view) {
        startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
    }

    // Called when log in button is clicked
    public void startLogIn (View view) {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    @Override
    public void onBackPressed () {
    }
}
