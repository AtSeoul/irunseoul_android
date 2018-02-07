package com.irunseoul.android.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irunseoul.android.app.R;
import com.irunseoul.android.app.model.MyRun;
import com.irunseoul.android.app.utilities.DateHelper;
import com.irunseoul.android.app.utilities.PreferencesHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMyProfileFragmentInteractionListener}
 * interface.
 */
public class MyProfileFragment extends Fragment implements OnChartGestureListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = MyProfileFragment.class.getSimpleName();
    public static final String MARATHON_EVENT_DATABASE = "event";

    private OnMyProfileFragmentInteractionListener mListener;


    private DatabaseReference mDatabase;
    private List<MyRun> myRunList;
    private Query mEventsQuery;

    private RecyclerView pastEventsRecyclerView;
    private AlertDialog progressDialog;

    private LineChart mLineChart;

    private boolean isTenSelected = true;
    private boolean isfiveSelected = false;
    private boolean isHalfSelected = false;
    private boolean isFullSelected = false;


    //Full Data Values
    private List<Float> allTimesFull;
    private List<Float> allDistanceFull;
    private List<String> allDatesFull;
    private List<Integer> allColorsFull;

    //Half Data Values
    private List<Float> allTimesHalf;
    private List<Float> allDistanceHalf;
    private List<String> allDatesHalf;
    private List<Integer> allcolorsHalf;

    //Ten KM Data Values
    private List<Float> allTimesTenKm;
    private List<Float> allDistanceTenKm;
    private List<String> allDatesTenKm;
    private List<Integer> allColorsTenKm;

    //Five KM Data Values
    private List<Float> allTimesFiveKm;
    private List<Float> allDistanceFiveKm;
    private List<String> allDatesFiveKm;
    private List<Integer> allColorsFiveKm;
    private Context mContext;

    private float mTotalDistance = 0;
    private float mTotalTime = 0;


    @BindView(R.id.logout)
    Button logout;

    @BindView(R.id.root_view)
    View rootView;

    @BindView(R.id.userName)
    TextView userName;

    @BindView(R.id.myMarathonNumber)
    TextView myMarathonNumbers;

    @BindView(R.id.distanceNumber)
    TextView mMarathonDistance;

    @BindView(R.id.marathonTime)
    TextView mMarathonTime;


    @BindView(R.id.userPhoto)
    ImageView userPhoto;


    @BindView(R.id.ten_segment)
    RadioButton tenKMRadioBtn;

    @BindView(R.id.full_segment)
    RadioButton overAllRadioBtn;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyProfileFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mEventsQuery = mDatabase.child("user-runs").child(getUid()).orderByChild("date");

        initializeLists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        ButterKnife.bind(this, view);

        mLineChart = (LineChart) view.findViewById(R.id.run_chart);
        mLineChart.setOnChartGestureListener(this);

        final SegmentedGroup runSegmentedGroup = (SegmentedGroup)
                view.findViewById(R.id.overall_duration_segment);
        tenKMRadioBtn.toggle();

        runSegmentedGroup.setOnCheckedChangeListener(this);

        progressDialog = new SpotsDialog(getActivity(), getActivity().getResources().getString(R.string.fetching_data));

        if(myRunList == null) {
//            progressDialog.show();
        }

        Log.d(TAG,"photoURL : " + photoUrl());

        addFirebaseEventListener();
        setUI();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnMyProfileFragmentInteractionListener) {
            mListener = (OnMyProfileFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.logout)
    public void logOut(View view) {

        if(mListener != null)
            mListener.clickLogout();
    }

    public interface OnMyProfileFragmentInteractionListener {

        void clickLogout();
        void notifyMarathonCount(int count);
        void clickSettings();
    }

    private void setUI() {

        if(photoUrl() != null) {

            Glide.with(getActivity())
                    .load(photoUrl().toString())
                    .centerCrop()
                    .crossFade()
                    .into(userPhoto);

            userPhoto.setVisibility(View.VISIBLE);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_name = user.getDisplayName();
        String email = user.getEmail();
        Log.d(TAG, "user email : " + email);
        userName.setText(user_name);

        SharedPreferences pref = PreferencesHelper.getSharedPref(getActivity());

        int my_marathon_events = PreferencesHelper.getPrefVal(pref, PreferencesHelper.MY_MARATHON_EVENTS);

        myMarathonNumbers.setText(String.valueOf(my_marathon_events));


    }

    public String getUid() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public Uri photoUrl() {

        return FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_settings){

            if(mListener != null)
                mListener.clickSettings();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addFirebaseEventListener() {

        // Read from the database
        mEventsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
                myRunList = new ArrayList<>();
                if(dataSnapshot.exists()) {
                    for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {

                        MyRun event = eventSnapshot.getValue(MyRun.class);
                        event.runKey = eventSnapshot.getKey();
                        myRunList.add(event);


                        Log.d(TAG, "eventSnapshot key("+ eventSnapshot.getKey() + "): " + event.title);

                    }
                    Log.d(TAG, "Count is: " + dataSnapshot.getChildrenCount());


                    for (MyRun myRun: myRunList) {


                        float distance = Float.valueOf(myRun.distance);
                        float timeMin = Float.valueOf(myRun.moving_time_min);
                        float speedKm = Float.valueOf(myRun.average_speed);
                        mTotalDistance = mTotalDistance + distance;
                        mTotalTime = mTotalTime + timeMin;

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                        SimpleDateFormat dateFormatShort = new SimpleDateFormat("MMM d");
                        String date_str = myRun.date;
                        String dateText = myRun.date;
                        Date convertedDate = new Date();


                        try {
                            convertedDate = dateFormat.parse(date_str);
                            dateText = dateFormatShort.format(convertedDate);
                            Log.d(TAG, "dateText : " + dateText);

                        } catch (ParseException e) {


                            Log.d(TAG, "error parsing date");
                        }



                        if (distance >= 4.0 && distance < 6.0) {

                            allDatesFiveKm.add(dateText);
                            allDistanceFiveKm.add(distance);
//                            allTimesFiveKm.add(timeMin);
                            allTimesFiveKm.add(speedKm);
                            setBarColorFivKm(Math.round(timeMin));

                        } else if(distance >= 9.0 && distance < 11.0) {

                            allDatesTenKm.add(dateText);
                            allDistanceTenKm.add(distance);
//                            allTimesTenKm.add(timeMin);
                            allTimesTenKm.add(speedKm);
                            setBarColorTenKm(Math.round(timeMin));

                        }  else if(distance >= 20.0 && distance < 22.0) {

                            allDatesHalf.add(dateText);
                            allDistanceHalf.add(distance);
//                            allTimesHalf.add(timeMin);
                            allTimesHalf.add(speedKm);
                            setBarColorHalf(Math.round(timeMin));


                        }  else if(distance >= 40.0 && distance < 44.0) {


                            allDatesFull.add(dateText);
                            allDistanceFull.add(distance);
//                            allTimesFull.add(timeMin);
                            allTimesFull.add(speedKm);
                            setBarColorFull(Math.round(timeMin));


                        } else {

                            Log.d(TAG, "unknown");
                        }

                    }

                    setChart(allDatesTenKm, allTimesTenKm, allColorsTenKm);
                    setStatsUI();

                }

                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setStatsUI() {


        String distance_str = String.valueOf(Math.round(mTotalDistance * 10.0 / 10.0));
        String time_str = String.valueOf(Math.round(mTotalTime * 10.0 / 10.0));

        mMarathonDistance.setText(distance_str);
        mMarathonTime.setText(time_str);

    }

    private void changeChartValues() {

        if(isTenSelected) {

            setChart(allDatesTenKm, allTimesTenKm, allColorsTenKm);

        } else if (isfiveSelected) {

            setChart(allDatesFiveKm, allTimesFiveKm, allColorsFiveKm);

        } else if (isHalfSelected) {

            setChart(allDatesHalf, allTimesHalf, allcolorsHalf);

        } else if (isFullSelected) {

            setChart(allDatesFull, allTimesFull, allColorsFull);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {


        switch (checkedId) {


            case R.id.ten_segment:
                isTenSelected = true;
                isfiveSelected = false;
                isHalfSelected = false;
                isFullSelected = false;
                changeChartValues();
                break;

            case R.id.five_segment:
                isTenSelected = false;
                isfiveSelected = true;
                isHalfSelected = false;
                isFullSelected = false;
                changeChartValues();
                break;

            case R.id.half_segment:
                isTenSelected = false;
                isfiveSelected = false;
                isHalfSelected = true;
                isFullSelected = false;
                changeChartValues();
                break;

            case R.id.full_segment:
                isTenSelected = false;
                isHalfSelected = false;
                isHalfSelected = false;
                isFullSelected = true;
                changeChartValues();
                break;

            default:break;
        }

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    private void initializeLists() {


        //Full Data Values
       allTimesFull = new ArrayList<>();
       allDistanceFull = new ArrayList<>();
       allDatesFull = new ArrayList<>();
       allColorsFull = new ArrayList<>();

        //Half Data Values
       allTimesHalf = new ArrayList<>();
       allDistanceHalf = new ArrayList<>();;
       allDatesHalf = new ArrayList<>();;
        allcolorsHalf = new ArrayList<>();;

        //Ten KM Data Values
        allTimesTenKm = new ArrayList<>();;
        allDistanceTenKm = new ArrayList<>();
        allDatesTenKm = new ArrayList<>();
        allColorsTenKm = new ArrayList<>();

        //Five KM Data Values
        allTimesFiveKm = new ArrayList<>();;
        allDistanceFiveKm = new ArrayList<>();;
        allDatesFiveKm = new ArrayList<>();;
        allColorsFiveKm = new ArrayList<>();;

    }

    private void setBarColorFivKm(int value) {

        if (mContext == null)
            return;
        Resources res = mContext.getResources();

        int colorId = res.getColor(R.color.status_level_color_1);

        if (value < 25) {
            //GREEN
            colorId =res.getColor(R.color.status_level_color_4);


        } else if (value >= 25 && value <= 30) {

            //Yellow
            colorId = res.getColor(R.color.status_level_color_3);



        } else if (value > 30 && value <= 35) {
            // Orange
            colorId = res.getColor(R.color.status_level_color_2);

        } else if (value > 35) {

            // RED
            colorId = res.getColor(R.color.status_level_color_1);

        }

        allColorsFiveKm.add(colorId);
    }


    private void setBarColorTenKm(int value) {

        if (mContext == null)
            return;
        Resources res = mContext.getResources();

        int colorId = res.getColor(R.color.status_level_color_1);

        if (value < 50) {
            //GREEN
            colorId =res.getColor(R.color.status_level_color_4);


        } else if (value >= 50 && value <= 60) {

            //Yellow
            colorId = res.getColor(R.color.status_level_color_3);



        } else if (value > 60 && value <= 70) {
            // Orange
            colorId = res.getColor(R.color.status_level_color_2);

        } else if (value > 70) {

            // RED
            colorId = res.getColor(R.color.status_level_color_1);

        }

        allColorsTenKm.add(colorId);
    }



    private void setBarColorHalf(int value) {

        if (mContext == null)
            return;
        Resources res = mContext.getResources();

        int colorId = res.getColor(R.color.status_level_color_1);

        if (value < 80) {
            //GREEN
            colorId =res.getColor(R.color.status_level_color_4);


        } else if (value >= 80 && value <= 120) {

            //Yellow
            colorId = res.getColor(R.color.status_level_color_3);



        } else if (value > 120 && value <= 150) {
            // Orange
            colorId = res.getColor(R.color.status_level_color_2);

        } else if (value > 150) {

            // RED
            colorId = res.getColor(R.color.status_level_color_1);

        }

        allcolorsHalf.add(colorId);
    }


    private void setBarColorFull(int value) {

        if (mContext == null)
            return;
        Resources res = mContext.getResources();

        int colorId = res.getColor(R.color.status_level_color_1);

        if (value < 240) {
            //GREEN
            colorId =res.getColor(R.color.status_level_color_4);


        } else if (value >= 240 && value <= 270) {

            //Yellow
            colorId = res.getColor(R.color.status_level_color_3);



        } else if (value > 270 && value <= 300) {
            // Orange
            colorId = res.getColor(R.color.status_level_color_2);

        } else if (value > 300) {

            // RED
            colorId = res.getColor(R.color.status_level_color_1);

        }

        allColorsFull.add(colorId);
    }

    private void setChart(List<String> dataPoints, List<Float> values,
                          List<Integer> colors) {

        if(dataPoints.size() == 0 || dataPoints == null) {
            Log.d(TAG, "set chart with zero values");
            mLineChart.setNoDataText("Not Enough Data");
            mLineChart.setNoDataTextColor(Color.WHITE);
            mLineChart.setData(new LineData());
            mLineChart.getDescription().setEnabled(false);
            mLineChart.invalidate();
            return;
        }
        Log.d(TAG, "setChart : " + dataPoints.size() + values.size() + colors.size());
        ArrayList<Entry> dataEntries = new ArrayList<>();

        mLineChart.setDrawGridBackground(true);
        mLineChart.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        mLineChart.setGridBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        for (int i = 0; i < dataPoints.size(); i++) {

            Log.d(TAG, "Create new data entry  for i :" + i + " val : " + values.get(i));

            Entry dataEntry = new Entry(i, values.get(i));
            dataEntries.add(dataEntry);
        }
        LineDataSet lineChartDataSet;
        if(true){

            lineChartDataSet = new LineDataSet(dataEntries, "");
            lineChartDataSet.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                lineChartDataSet.setFillColor(mContext
                        .getResources()
                        .getColor(R.color.text_primary));
                lineChartDataSet.setFillAlpha(50);

            }
            else {
                lineChartDataSet.setFillColor(mContext
                        .getResources()
                        .getColor(R.color.text_primary));
                lineChartDataSet.setFillAlpha(50);
            }
            lineChartDataSet.setColor(Color.WHITE);
            lineChartDataSet.setCircleColors(colors);

            //        lineChartDataSet.setColors();

            YAxis yAxisLeft = mLineChart.getAxisLeft();
            YAxis yAxisRight = mLineChart.getAxisRight();

            // get the legend (only possible after setting data)
            Legend l = mLineChart.getLegend();
            l.setEnabled(false);
            yAxisLeft.setTextColor(Color.WHITE);
            yAxisLeft.setLabelCount(5);

            yAxisLeft.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    String label = String.valueOf(Math.round(value * 10.0) / 10.0) + " Km/h";
                    return label;
                }
            });

            yAxisRight.setLabelCount(0);
            yAxisRight.setDrawLabels(false);

            LineData lineChartData = new LineData(lineChartDataSet);
            lineChartData.setDrawValues(false);

            mLineChart.getXAxis().setTextColor(Color.WHITE);
            mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

            mLineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dataPoints));

            mLineChart.setData(lineChartData);
            Description desc = new Description();
            desc.setText("");
            mLineChart.setDescription(desc);
            mLineChart.getLineData().setValueTextColor(Color.WHITE);
            mLineChart.setExtraRightOffset(15f);

            if (dataPoints.size() > 20 && dataPoints.size() <= 45) {
                lineChartDataSet.setCircleHoleRadius(3.0f);
                lineChartDataSet.setCircleRadius(9.0f);
            } else if (dataPoints.size() > 45) {
                lineChartDataSet.setCircleHoleRadius(2.5f);
                lineChartDataSet.setCircleRadius(7.0f);
            } else {
                lineChartDataSet.setCircleHoleRadius(3.0f);
                lineChartDataSet.setCircleRadius(8.0f);
            }


            mLineChart.invalidate();
        }


    }
}
