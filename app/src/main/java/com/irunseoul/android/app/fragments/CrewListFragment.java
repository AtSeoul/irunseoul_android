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
import com.irunseoul.android.app.adapters.CrewRecyclerViewAdapter;
import com.irunseoul.android.app.model.Crew;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.utilities.DateHelper;
import com.irunseoul.android.app.utilities.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCrewListFragmentInteractionListener}
 * interface.
 */
public class CrewListFragment extends Fragment {

    private static final String TAG = CrewListFragment.class.getSimpleName();
    public static final String CREW_DATABASE = "crews";
    private static final String CREW_CITY = "seoul";

    private OnCrewListFragmentInteractionListener mListener;
    private DatabaseReference mDatabase;
    private List<Crew> mCrewList;
    private Query mCrewListQuery;

    private RecyclerView crewListRecyclerView;
    private AlertDialog progressDialog;
    private Context mContext;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CrewListFragment() {
    }


    public static CrewListFragment newInstance() {
        CrewListFragment fragment = new CrewListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setHasOptionsMenu(true);

        mDatabase = FirebaseDatabase.getInstance().getReference(CREW_DATABASE);
        mCrewListQuery = mDatabase.child(CREW_CITY).orderByChild("name");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_crew_list, container, false);
        progressDialog = new SpotsDialog(getActivity(), getActivity().getResources().getString(R.string.fetching_data));

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            crewListRecyclerView = (RecyclerView) view;
            Log.d(TAG, "crewListRecyclerView : " + crewListRecyclerView);

        }

        // Read from the database
        if(mCrewList == null) {
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
        if (context instanceof OnCrewListFragmentInteractionListener) {
            mListener = (OnCrewListFragmentInteractionListener) context;
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

    public interface OnCrewListFragmentInteractionListener {

        void onCrewListFragmentInteraction(Crew item);
        void onCrewWebsiteInteraction(String webURL);
        void onCrewVideoInteraction(String videoURL);
    }

    private void showToastMessage(String msg) {

        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void addFirebaseEventListener() {

        mCrewListQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mCrewList = new ArrayList<Crew>();

                if(dataSnapshot.exists()) {
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {

                        Crew crew = eventSnapshot.getValue(Crew.class);
                        mCrewList.add(crew);
                        Log.d(TAG, "eventSnapshot key(" + eventSnapshot.getKey() + "): " + crew.name);

                    }
                    crewListRecyclerView.setAdapter(new CrewRecyclerViewAdapter(mCrewList, mListener));
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
        mCrewListQuery = mDatabase.child(DateHelper.getCurrentYear())
                .orderByChild("date")
                .startAt("2017/03/01 08:00")
                .endAt(DateHelper.getTodaysDate());

        addFirebaseEventListener();
    }

    private void fetchNewMarathonEvents() {

        mCrewListQuery = mDatabase.child(DateHelper.getCurrentYear()).orderByChild("date").startAt(DateHelper.getCurrentDate());
        addFirebaseEventListener();

    }
}
