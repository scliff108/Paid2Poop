package com.tailoreddevelopmentgroup.paid2poop;

import com.parse.Parse;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.parse_app_id))
                .clientKey(getString(R.string.parse_client_id))
                .server(getString(R.string.parse_server_url))
                .build());
    }
}
