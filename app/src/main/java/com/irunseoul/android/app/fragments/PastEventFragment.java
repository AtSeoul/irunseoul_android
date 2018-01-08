package com.irunseoul.android.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irunseoul.android.app.R;
import com.irunseoul.android.app.adapters.PastEventRecyclerViewAdapter;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.utilities.DateHelper;
import com.irunseoul.android.app.utilities.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PastEventFragment extends Fragment {

    private static final String TAG = PastEventFragment.class.getSimpleName();
    public static final String MARATHON_EVENT_DATABASE = "event";
    public static final String INTL_MARATHON_EVENT_DATABASE = "intl-event";
    private static final String PAST_2017_START_DATE = "2017/03/18 08:00";

    private static final String ARG_TAB_INDEX = "arg_tab_index";

    private OnListFragmentInteractionListener mListener;
    private DatabaseReference mDatabase;
    private List<Event> mEventList;
    private Query mEventsQuery;

    private RecyclerView pastEventsRecyclerView;
    private AlertDialog progressDialog;
    private Context mContext;
    private int mTabIndex = 0;



    public PastEventFragment() {
    }

    public static PastEventFragment newInstance(int tabIndex) {
        PastEventFragment fragment = new PastEventFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_INDEX, tabIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mDatabase = FirebaseDatabase.getInstance().getReference(MARATHON_EVENT_DATABASE);

        if (getArguments() != null) {
            mTabIndex = getArguments().getInt(ARG_TAB_INDEX);

            if (mTabIndex == 0) {
                mEventsQuery = mDatabase
                        .child(DateHelper.getCurrentYear())
                        .orderByChild("date").startAt(DateHelper.getCurrentDate());

            } else if(mTabIndex == 1) {

                mEventsQuery = mDatabase.child(DateHelper.getCurrentYear())
                        .orderByChild("date")
                        .startAt("2018/01/01 08:00")
                        .endAt(DateHelper.getTodaysDate());

            } else if (mTabIndex == 2) {

                mEventsQuery = mDatabase
                        .child("2017")
                        .orderByChild("date").startAt(PAST_2017_START_DATE);
            } else if (mTabIndex == 3) {
                mDatabase = FirebaseDatabase.getInstance().getReference(INTL_MARATHON_EVENT_DATABASE);
                mEventsQuery = mDatabase
                        .child(DateHelper.getCurrentYear())
                        .orderByChild("date");
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_past_event_list, container, false);
        progressDialog = new SpotsDialog(getActivity(), getActivity().getResources().getString(R.string.fetching_data));

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            pastEventsRecyclerView = (RecyclerView) view;
            Log.d(TAG, "pastEventsRecyclerView : " + pastEventsRecyclerView);

        }

        // Read from the database
        if(mEventList == null) {
            //TODO: Find a way to handle this better
//            progressDialog.show();
        }
        if(!NetworkHelper.isNetworkAvailable(getActivity())) {

            showToastMessage(getActivity().getResources().getString(R.string.network_not_available));
            progressDialog.dismiss();
            return view;
        }

        if(savedInstanceState == null) {
            addFirebaseEventListener();
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_marathon_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_refresh){

//            createFilterDialog();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(Event item);
    }

    private void showToastMessage(String msg) {

        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void addFirebaseEventListener() {

        mEventsQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mEventList = new ArrayList<Event>();

                if(dataSnapshot.exists()) {
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {

                        Event event = eventSnapshot.getValue(Event.class);
                        mEventList.add(event);
                        Log.d(TAG, "eventSnapshot key(" + eventSnapshot.getKey() + "): " + event.title);

                    }
                    pastEventsRecyclerView.setAdapter(new PastEventRecyclerViewAdapter(mEventList, mListener));
                    progressDialog.dismiss();
                    Log.d(TAG, "Count is: " + dataSnapshot.getChildrenCount());

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }


        });
    }

    private void createFilterDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.filter_marathon_events)
                .setItems(R.array.filter_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d(TAG, "which : " + which);

                        switch (which) {
                            case 0:
                                fetchUpdatedMarathonEvents();
                                break;
                            default:
                                fetchNewMarathonEvents();
                        }
                    }
                });

        builder.create().show();
    }

    private void fetchUpdatedMarathonEvents() {

        if(mTabIndex == 0) {
            mDatabase = FirebaseDatabase.getInstance().getReference(MARATHON_EVENT_DATABASE);
            mEventsQuery = mDatabase
                    .child(DateHelper.getCurrentYear())
                    .orderByChild("date").startAt(DateHelper.getCurrentDate());
        } else if (mTabIndex == 1) {
            mDatabase = FirebaseDatabase.getInstance().getReference(MARATHON_EVENT_DATABASE);
            mEventsQuery = mDatabase.child(DateHelper.getCurrentYear())
                    .orderByChild("date")
                    .startAt("2018/01/01 08:00")
                    .endAt(DateHelper.getTodaysDate());
        } else if (mTabIndex == 2) {
            mDatabase = FirebaseDatabase.getInstance().getReference(MARATHON_EVENT_DATABASE);
            mEventsQuery = mDatabase.child("2017")
                    .orderByChild("date")
                    .startAt("2017/03/18 08:00")
                    .endAt(DateHelper.getTodaysDate());
        } else if (mTabIndex == 3) {

            mDatabase = FirebaseDatabase.getInstance().getReference(INTL_MARATHON_EVENT_DATABASE);
            mEventsQuery = mDatabase
                    .child(DateHelper.getCurrentYear())
                    .orderByChild("date");
        }

        addFirebaseEventListener();
    }

    private void fetchNewMarathonEvents() {

        mEventsQuery = mDatabase.child(DateHelper.getCurrentYear()).orderByChild("date").startAt(DateHelper.getCurrentDate());
        addFirebaseEventListener();

    }
}
