package com.irunseoul.android.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.irunseoul.android.app.R;

import com.firebase.ui.auth.IdpResponse;
import com.irunseoul.android.app.fragments.MyRunsFragment;
import com.irunseoul.android.app.fragments.PastEventFragment;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.model.MyRun;

import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MarathonListActivity extends AppCompatActivity implements PastEventFragment.OnListFragmentInteractionListener,
        MyRunsFragment.OnMyRunFragmentInteractionListener{

    private static final String TAG = MarathonListActivity.class.getSimpleName();

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;

    @BindView(R.id.root_view)
    View mRootView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.d(TAG, "navigation_home");
                    fragment = PastEventFragment.newInstance();
                    break;
                case R.id.navigation_dashboard:
                    fragment = MyRunsFragment.newInstance();
                    break;
                case R.id.navigation_notifications:
//                    fragment = PastEventFragment.newInstance();
                    signOut();
                    break;
            }

            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();
            return true;
        }

    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if(navigation != null) {

            fragment = MyRunsFragment.newInstance();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();

            navigation.setSelectedItemId(R.id.navigation_dashboard);


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        setContentView(R.layout.activity_marathon_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        fragmentManager = getSupportFragmentManager();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, MarathonListActivity.class);
        return in;
    }

    @Override
    public void onListFragmentInteraction(Event item) {
        Log.d(TAG, "Event clicked :" + item.title );

        Intent intent = new Intent(MarathonListActivity.this, SingleEventActivity.class);
        intent.putExtra(Event.ARG_TITLE, item.title);
        intent.putExtra(Event.ARG_CITY, item.city);
        intent.putExtra(Event.ARG_DATE, item.date);
        intent.putExtra(Event.ARG_WEATHER, item.weather);
        intent.putExtra(Event.ARG_WEBSITE, item.website);
        intent.putExtra(Event.ARG_LATITUDE, item.latitude);
        intent.putExtra(Event.LONGITUDE, item.longitude);
        intent.putExtra(Event.ARG_EMAIL, item.email);
        intent.putExtra(Event.ARG_HOST, item.host);
        intent.putExtra(Event.ARG_LOCATION, item.location);
        intent.putExtra(Event.ARG_MAP_URL, item.map_url);
        intent.putExtra(Event.ARG_TEMPERATURE, item.temperature);
        intent.putExtra(Event.ARG_PHONE, item.phone);
        intent.putExtra(Event.ARG_APPLICATION_PERIOD, item.application_period);
//        intent.putExtra(Event.ARG_RACE, new HashMap<Integer, String>(item.race));

        startActivity(intent);

    }

    @Override
    public void onListFragmentInteraction(MyRun item) {
        Log.d(TAG,"clicked myrun ");
    }

    @Override
    public void onClickSyncRunButton() {

        Log.d(TAG, "onClickSyncRunButton");

        fragment = PastEventFragment.newInstance();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).commit();

        navigation.setSelectedItemId(R.id.navigation_home);
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(MainActivity.createIntent(MarathonListActivity.this));
                            finish();
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
