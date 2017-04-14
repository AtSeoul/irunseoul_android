package com.irunseoul.android.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irunseoul.android.app.BuildConfig;
import com.irunseoul.android.app.R;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.model.MyRun;
import com.irunseoul.android.app.model.StravaResultWrapper;
import com.irunseoul.android.app.model.User;
import com.irunseoul.android.app.utilities.PreferencesHelper;
import com.sweetzpot.stravazpot.activity.api.ActivityAPI;
import com.sweetzpot.stravazpot.activity.api.PhotoAPI;
import com.sweetzpot.stravazpot.activity.model.Activity;
import com.sweetzpot.stravazpot.activity.model.Photo;
import com.sweetzpot.stravazpot.authenticaton.api.AccessScope;
import com.sweetzpot.stravazpot.authenticaton.api.ApprovalPrompt;
import com.sweetzpot.stravazpot.authenticaton.api.AuthenticationAPI;
import com.sweetzpot.stravazpot.authenticaton.api.StravaLogin;
import com.sweetzpot.stravazpot.authenticaton.model.AppCredentials;
import com.sweetzpot.stravazpot.authenticaton.model.LoginResult;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginActivity;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginButton;
import com.sweetzpot.stravazpot.common.api.AuthenticationConfig;
import com.sweetzpot.stravazpot.common.api.StravaConfig;
import com.sweetzpot.stravazpot.common.model.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SingleEventActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private static final String TAG = SingleEventActivity.class.getSimpleName();
    private static final int RQ_LOGIN = 109;
    private static final int ITEMS_PER_PAGE = 20;

    private static final double SEOUL_LAT = 37.5559341;
    private static final double SEOUL_LNG  = 126.9759789;

    private String mLocation;
    private String mLat;
    private String mLng;
    private String mTitle;
    private String mWebsite;
    private String mMapUrl;
    private String mPhoneNo;
    private String mAppPeriod;
    private String mDate;
    private String mTemperature;
    private String mWeather;
    private String mCity;
    private String mEmail;
    private String mHost;

    private DatabaseReference mDatabase;
    private Query mExistingRunQuery;

    private AlertDialog progressDialog;

    @BindView(R.id.strava_login_button)
    StravaLoginButton stravaLoginButton;

    @BindView(R.id.event_title)
    TextView eventTitle;

    @BindView(R.id.eventDate)
    TextView eventDate;

    @BindView(R.id.appPeriod)
    TextView eventAppPerid;

    @BindView(R.id.eventTemp)
    TextView eventTemp;

    @BindView(R.id.eventSynced)
    ImageView syncEventImg;

    @BindView(R.id.eventWebsite)
    Button eventWebsite;

    @BindView(R.id.eventMapUrl)
    Button eventMapUrl;

    @BindView(R.id.root_view)
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLat = getIntent().getStringExtra(Event.ARG_LATITUDE);
        mLng = getIntent().getStringExtra(Event.LONGITUDE);
        mTitle = getIntent().getStringExtra(Event.ARG_TITLE);
        mLocation = getIntent().getStringExtra(Event.ARG_LOCATION);
        mWebsite = getIntent().getStringExtra(Event.ARG_WEBSITE);
        mMapUrl = getIntent().getStringExtra(Event.ARG_MAP_URL);
        mTemperature = getIntent().getStringExtra(Event.ARG_TEMPERATURE);
        mPhoneNo = getIntent().getStringExtra(Event.ARG_PHONE);
        mWeather = getIntent().getStringExtra(Event.ARG_WEATHER);
        mAppPeriod = getIntent().getStringExtra(Event.ARG_APPLICATION_PERIOD);
        mDate = getIntent().getStringExtra(Event.ARG_DATE);
        mCity = getIntent().getStringExtra(Event.ARG_CITY);
        mEmail = getIntent().getStringExtra(Event.ARG_EMAIL);
        mHost = getIntent().getStringExtra(Event.ARG_HOST);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUI();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new SpotsDialog(this, getResources().getString(R.string.processing));


    }

    @OnClick(R.id.strava_login_button)
    public void connectStrava(View view) {

        String redirctURL = BuildConfig.STRAVA_REDIRECT_URL;
        int clientId = BuildConfig.STRAVA_CLIENT_ID;
        Log.d(TAG, "redirectURL : " + redirctURL + " clientId: "  + clientId);
        Intent intent = StravaLogin.withContext(this)
                .withClientID(clientId)
                .withRedirectURI(redirctURL)
                .withApprovalPrompt(ApprovalPrompt.AUTO.AUTO)
                .withAccessScope(AccessScope.VIEW_PRIVATE_WRITE)
                .makeIntent();
        startActivityForResult(intent, RQ_LOGIN);

    }

    @OnClick(R.id.eventWebsite)
    public void openWebsite(View view) {

        if(!mWebsite.isEmpty()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mWebsite));
            startActivity(i);
        }
    }

    @OnClick(R.id.eventMapUrl)
    public void openMapView(View view) {

        if(!mMapUrl.isEmpty()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mMapUrl));
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MarathonListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng eventLatLng;

        if(!mLat.isEmpty() && !mLng.isEmpty()) {
            eventLatLng = new LatLng(Double.valueOf(mLat), Double.valueOf(mLng));
        } else {
            eventLatLng = new LatLng(SEOUL_LAT, SEOUL_LNG);

        }

//        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 14));

        googleMap.addMarker(new MarkerOptions()
                .title(mTitle)
                .snippet(mLocation)
                .position(eventLatLng));
    }

    private void setUI() {


        eventTitle.setText(mTitle);
        eventDate.setText(mDate);
        eventAppPerid.setText(String.format(getString(R.string.app_perioid), mAppPeriod));
        eventTemp.setText(String.format(getString(R.string.predict_weather), mTemperature));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_LOGIN && resultCode == RESULT_OK && data != null) {

            progressDialog.show();
            String code = data.getStringExtra(StravaLoginActivity.RESULT_CODE);
            // Use code to obtain token
            getSravaToken(code);
        }

    }

    private void getSravaToken(String code) {

        Log.d(TAG, "code : " + code);
        new StravaTokenTask().execute(code, mDate);



    }

    private class StravaTokenTask extends AsyncTask<String, Void, StravaResultWrapper> {


        @Override
        protected StravaResultWrapper doInBackground(String... params) {

            StravaResultWrapper wrapper = new StravaResultWrapper();
            String code = params[0];
            String dateString = params[1];
            AuthenticationConfig config = AuthenticationConfig.create()
                    .debug()
                    .build();
            AuthenticationAPI api = new AuthenticationAPI(config);
            LoginResult result = api.getTokenForApp(AppCredentials
                    .with(BuildConfig.STRAVA_CLIENT_ID,BuildConfig.STRAVA_CLIENT_SECRET))
                    .withCode(code)
                    .execute();

            StravaConfig stravaConfig = StravaConfig.withToken(result.getToken())
                    .debug()
                    .build();

//            2017/03/19 08:00
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            Date convertedDate = new Date();
            Date finishDate = new Date();
            Calendar calendar = Calendar.getInstance();
            Calendar finishCalendar = Calendar.getInstance();
            try {
                convertedDate = dateFormat.parse(dateString);
                finishDate = dateFormat.parse(dateString);

                Log.d(TAG, "afterDate : "  + convertedDate);
                calendar.setTime(convertedDate);
                finishCalendar.setTime(finishDate);
                finishCalendar.add(Calendar.HOUR, 6);

            } catch (ParseException e) {
                Log.d(TAG, "exception : " + e.getMessage());
            }

            ActivityAPI activityAPI = new ActivityAPI(stravaConfig);
            List<Activity> activities = activityAPI.listMyActivities()
//                    .before(Time.seconds(calendar.get(Calendar.SECOND)))
                    .after(Time.seconds(calendar.get(Calendar.SECOND)))
//                    .inPage(1)
//                    .perPage(ITEMS_PER_PAGE)
                    .execute();
            for (com.sweetzpot.stravazpot.activity.model.Activity activity : activities) {
                if (activity.getStartDateLocal().before(finishCalendar.getTime()) && activity.getStartDateLocal().after(calendar.getTime())) {

                    wrapper.activity = activity;
                    PhotoAPI photoAPI = new PhotoAPI(stravaConfig);
                    List<Photo> photos = photoAPI.listAcivityPhotos(activity.getID())
                            .execute();
                    if(photos.size() > 0) {
                        Log.d(TAG, "photos : " + photos.get(0).getUrls().get("0"));
                        wrapper.photo_url  = photos.get(0).getUrls().get("0");

                    }

                    Log.d(TAG,  "resultActivity : start_date " + activity.getStartDate() + "local_date : " + activity.getStartDateLocal() +
                            " timezone" + activity.getTimezone() + " "  + activity.getDistance());

                }
                Log.d(TAG,  "start_date " + activity.getStartDate() + "local_date : " + activity.getStartDateLocal() +
                        " timezone" + activity.getTimezone() + " "  + activity.getDistance());
                Log.d(TAG, "photos: " + activity.getPhotos() + " avgSpeed : " + activity.getAverageSpeed().getMetersPerSecond() + " type : "  + activity.getType());

            }


            return wrapper;
        }

        @Override
        protected void onPostExecute(StravaResultWrapper wrapper) {
            super.onPostExecute(wrapper);

            if(wrapper != null) {
                Log.d(TAG, "result :" + wrapper);
                addNewRun(wrapper);
            } else {
                progressDialog.dismiss();
            }




        }
    }

    private void addNewRun(final StravaResultWrapper result) {


        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                           showToastMessage(getResources().getString(R.string.error_user_fetch));
                            progressDialog.dismiss();
                        } else {
                            // Write new post
                            writeNewRun(userId, result);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }


    public String getUid() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void writeNewRun(final String userId, final StravaResultWrapper result) {

        final com.sweetzpot.stravazpot.activity.model.Activity activity = result.activity;
        final String photo_url = result.photo_url;

        mExistingRunQuery = mDatabase.child("runs").orderByChild("run_id").equalTo(String.valueOf(activity.getID()));

        mExistingRunQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Log.d(TAG, "datasnapshot exists ");
                    for (DataSnapshot runSnapShot : dataSnapshot.getChildren()) {
                        MyRun myRun = runSnapShot.getValue(MyRun.class);
                        Log.d(TAG, "myrun : " + myRun.run_name);
                        progressDialog.dismiss();
                        showToastMessage(getResources().getString(R.string.already_added));
                    }
                } else {

                    // Create new post at /user-runs/$userid/$runid and at
                    // /runs/$runid simultaneously
                    String key = mDatabase.child("runs").push().getKey();
                    MyRun run = new MyRun(userId, mCity, mDate, mHost, mTitle, mWeather, mWebsite, mLat,
                            mLng, mLocation, mMapUrl, mTemperature, "strava", activity, photo_url);
                    Map<String, Object> postValues = run.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/runs/" + key, postValues);
                    childUpdates.put("/user-runs/" + userId + "/" + key, postValues);

                    mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(rootView, getResources().getString(R.string.run_synced), Snackbar.LENGTH_LONG)
                                    .show();
                            progressDialog.dismiss();
                            SharedPreferences pref = PreferencesHelper.getSharedPref(SingleEventActivity.this);
                            PreferencesHelper.writePref(pref, PreferencesHelper.WHICH_FRAGMENT, 2);
                            showToastMessage(getResources().getString(R.string.run_synced));
                            finish();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d(TAG, "onCancelled :" + databaseError.getMessage());

            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showToastMessage(String msg) {

        Toast.makeText(SingleEventActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}
