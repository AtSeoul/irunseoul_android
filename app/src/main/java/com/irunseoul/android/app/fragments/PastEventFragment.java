package com.irunseoul.android.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.irunseoul.android.app.utilities.PreferencesHelper;

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

    private OnListFragmentInteractionListener mListener;
    private DatabaseReference mDatabase;
    private List<Event> mEventList;
    private Query mEventsQuery;

    private RecyclerView pastEventsRecyclerView;
    private AlertDialog progressDialog;
    private Context mContext;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PastEventFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PastEventFragment newInstance() {
        PastEventFragment fragment = new PastEventFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mDatabase = FirebaseDatabase.getInstance().getReference(MARATHON_EVENT_DATABASE);
        //TODO: Date Format for today 2016/09/15 08:00 - Add Tabs UpComing and Past
//        final Query mEventsQuery = mDatabase.child("2016").orderByChild("date").limitToLast(20).startAt("2016/09/15 08:00");
        mEventsQuery = mDatabase.child(DateHelper.getCurrentYear()).orderByChild("date").startAt(DateHelper.getCurrentDate());

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
        if(id == R.id.action_filter){

            createFilterDialog();

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
                                fetchPastMarathonEvents();
                                break;
                            default:
                                fetchNewMarathonEvents();
                        }
                    }
                });

        builder.create().show();
    }

    private void fetchPastMarathonEvents() {
        mEventsQuery = mDatabase.child(DateHelper.getCurrentYear())
                .orderByChild("date")
                .startAt("2017/03/01 08:00")
                .endAt(DateHelper.getTodaysDate());

        addFirebaseEventListener();
    }

    private void fetchNewMarathonEvents() {

        mEventsQuery = mDatabase.child(DateHelper.getCurrentYear()).orderByChild("date").startAt(DateHelper.getCurrentDate());
        addFirebaseEventListener();

    }
}
