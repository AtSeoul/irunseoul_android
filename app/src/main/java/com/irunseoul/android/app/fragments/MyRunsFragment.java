package com.irunseoul.android.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irunseoul.android.app.R;
import com.irunseoul.android.app.activities.SingleEventActivity;
import com.irunseoul.android.app.adapters.MyRunsRecycleViewAdapter;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.model.MyRun;
import com.irunseoul.android.app.utilities.NetworkHelper;
import com.irunseoul.android.app.utilities.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMyRunFragmentInteractionListener}
 * interface.
 */
public class MyRunsFragment extends Fragment {

    private static final String TAG = MyRunsFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnMyRunFragmentInteractionListener mListener;


    private DatabaseReference mDatabase;
    private List<MyRun> myRunList;
    private Query mEventsQuery;

    private RecyclerView pastEventsRecyclerView;
    private AlertDialog progressDialog;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyRunsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MyRunsFragment newInstance() {
        MyRunsFragment fragment = new MyRunsFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mEventsQuery = mDatabase.child("user-runs").child(getUid()).orderByChild("date");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_runs_list, container, false);


        pastEventsRecyclerView = (RecyclerView) view.findViewById(R.id.myRunlist);

        progressDialog = new SpotsDialog(getActivity(), getActivity().getResources().getString(R.string.fetching_data));

        if(myRunList == null) {
            progressDialog.show();
        }

        if(!NetworkHelper.isNetworkAvailable(getActivity())) {

            showToastMessage(getActivity().getResources().getString(R.string.network_not_available));
            progressDialog.dismiss();
            return view;
        }
        addFirebaseEventListener();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyRunFragmentInteractionListener) {
            mListener = (OnMyRunFragmentInteractionListener) context;
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
            addFirebaseEventListener();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnMyRunFragmentInteractionListener {

        void onListFragmentInteraction(MyRun item);
        void onClickSyncRunButton();
        void notifyMyMarathonCount(int count);
    }

    public String getUid() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void showToastMessage(String msg) {

        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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
                        myRunList.add(event);
                        Log.d(TAG, "eventSnapshot key("+ eventSnapshot.getKey() + "): " + event.title);

                    }
                    pastEventsRecyclerView.setAdapter(new MyRunsRecycleViewAdapter(myRunList, mListener));
                    Log.d(TAG, "Count is: " + dataSnapshot.getChildrenCount());

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
}
