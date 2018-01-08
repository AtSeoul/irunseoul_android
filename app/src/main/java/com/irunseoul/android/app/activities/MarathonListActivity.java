package com.irunseoul.android.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.irunseoul.android.app.R;

import com.firebase.ui.auth.IdpResponse;
import com.irunseoul.android.app.fragments.CrewListFragment;
import com.irunseoul.android.app.fragments.EventTabFragment;
import com.irunseoul.android.app.fragments.MyProfileFragment;
import com.irunseoul.android.app.fragments.MyRunsFragment;
import com.irunseoul.android.app.fragments.PastEventFragment;
import com.irunseoul.android.app.model.Crew;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.model.MyRun;
import com.irunseoul.android.app.utilities.Constants;
import com.irunseoul.android.app.utilities.PreferencesHelper;

import java.io.File;

import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MarathonListActivity extends AppCompatActivity implements PastEventFragment.OnListFragmentInteractionListener,
        MyRunsFragment.OnMyRunFragmentInteractionListener, MyProfileFragment.OnMyProfileFragmentInteractionListener,
        CrewListFragment.OnCrewListFragmentInteractionListener, EventTabFragment.OnMainFragmentInteractionListener {

    private static final String TAG = MarathonListActivity.class.getSimpleName();

    public static final String type = "image/*";

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;
    private Toolbar toolbar;

    @BindView(R.id.root_view)
    View mRootView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.d(TAG, "navigation_home");
                    toolbar.setTitle(getString(R.string.marathon_events_2017));
                    fragment = EventTabFragment.newInstance();
                    break;
                case R.id.navigation_dashboard:
                    toolbar.setTitle(getString(R.string.my_marathon_events));
                    fragment = MyRunsFragment.newInstance();
                    break;
                case R.id.navigation_crews:
                    toolbar.setTitle(getString(R.string.title_crews));
                    fragment = CrewListFragment.newInstance();
                    break;
                case R.id.navigation_notifications:
                    toolbar.setTitle(getString(R.string.my_profile));
                    fragment = MyProfileFragment.newInstance();
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

            SharedPreferences pref = PreferencesHelper.getSharedPref(this);
            int fragNo = PreferencesHelper.getPrefVal(pref, PreferencesHelper.WHICH_FRAGMENT);

            switch (fragNo) {

                case 1 :
                    toolbar.setTitle(getString(R.string.marathon_events_2017));
//                    fragment = PastEventFragment.newInstance();
                    fragment = EventTabFragment.newInstance();
                    navigation.setSelectedItemId(R.id.navigation_home);

                    break;
                case 2:
                    toolbar.setTitle(getString(R.string.my_marathon_events));
                    fragment = MyRunsFragment.newInstance();
                    navigation.setSelectedItemId(R.id.navigation_dashboard);
                    PreferencesHelper.writePref(pref, PreferencesHelper.WHICH_FRAGMENT, 1);
                    break;

                case 3:
                    toolbar.setTitle(getString(R.string.title_crews));
                    fragment = CrewListFragment.newInstance();
                    navigation.setSelectedItemId(R.id.navigation_crews);
                    break;
                case 4:
                    toolbar.setTitle(getString(R.string.my_profile));
                    fragment = MyProfileFragment.newInstance();
                    navigation.setSelectedItemId(R.id.navigation_notifications);
                    break;
                default:
//                    fragment = PastEventFragment.newInstance();
                    fragment = EventTabFragment.newInstance();
                    toolbar.setTitle(getString(R.string.marathon_events_2017));
                    break;
            }

            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        setContentView(R.layout.activity_marathon_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        fragmentManager = getSupportFragmentManager();
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

       /* if(!item.photo_url.isEmpty()) {

            createInstagramIntent(item.photo_url);
        }*/

        Intent intent = new Intent(this, SingleRunActivity.class);
        startActivity(intent);

    }

    @Override
    public void onClickSyncRunButton() {

        Log.d(TAG, "onClickSyncRunButton");

//        fragment = PastEventFragment.newInstance();
        fragment = EventTabFragment.newInstance();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).commit();

        navigation.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public void onCrewListFragmentInteraction(Crew item) {

        Log.d(TAG, "onCrewListFragmentInteraction");

        if(!item.instagram.isEmpty()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(Constants.INSTAGRAM_URL_PREFIX + item.instagram));
            startActivity(i);
        }
    }

    @Override
    public void onCrewWebsiteInteraction(String webURL) {

        Log.d(TAG, "onCrewWebsiteInteraction");

        if(!webURL.isEmpty()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(webURL));
            startActivity(i);
        }
    }

    @Override
    public void onCrewVideoInteraction(String videoURL) {

        Log.d(TAG, "onCrewVideoInteraction");

        if(!videoURL.isEmpty()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(videoURL));
            startActivity(i);
        }
    }

    @Override
    public void notifyMarathonCount(int count) {

        Log.d(TAG,"notifyMarathonCount : " + count);
        SharedPreferences pref = PreferencesHelper.getSharedPref(this);
        PreferencesHelper.writePref(pref,
                PreferencesHelper.KEY_UPCOMING_MARATHON_EVENTS,
                count);
    }

    @Override
    public void notifyMyMarathonCount(int count) {

        SharedPreferences pref = PreferencesHelper.getSharedPref(this);
        PreferencesHelper.writePref(pref,
                PreferencesHelper.MY_MARATHON_EVENTS,
                count);
    }

    @Override
    public void clickLogout() {

        signOut();

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

    private void createInstagramIntent(String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

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
    public void onFragmentInteraction(Uri uri) {

    }
}
