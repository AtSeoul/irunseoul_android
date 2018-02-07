package com.irunseoul.android.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.irunseoul.android.app.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {


    public static final String TAG = SettingsActivity.class.getSimpleName();

    public static final String GUIDE_LINK = "https://blog.naver.com/hassanabid89/221170402971";
    public static final String STRAVA_APP = "https://play.google.com/store/apps/details?id=com.strava&hl=en";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @OnClick(R.id.run_korea_guide)
    public void onClickGuide(View view) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(GUIDE_LINK));
        startActivity(i);

    }

    @OnClick(R.id.strava_app)
    public void onClickStrava(View view) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(STRAVA_APP));
        startActivity(i);

    }
}
