package com.irunseoul.android.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.irunseoul.android.app.R;
import com.irunseoul.android.app.utilities.PreferencesHelper;

public class SingleRunActivity extends AppCompatActivity {

    private static final String TAG = SingleRunActivity.class.getSimpleName();
    public static final String TYPE = "image/*";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_run);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void createInstagramIntent(String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(TYPE);

        // Create the URI from the media
//        File media = new File(mediaPath);
//        Uri uri = Uri.fromFile(media);
        Uri uri = Uri.parse(mediaPath);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed");
        SharedPreferences sharedPref = PreferencesHelper.getSharedPref(this);

        PreferencesHelper.writePref(sharedPref, PreferencesHelper.WHICH_FRAGMENT, 2);

        super.onBackPressed();
    }
}
