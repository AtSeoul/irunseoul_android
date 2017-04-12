package com.irunseoul.android.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irunseoul.android.app.R;
import com.irunseoul.android.app.adapters.PastEventRecyclerViewAdapter;
import com.irunseoul.android.app.model.Event;

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

        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference(MARATHON_EVENT_DATABASE);
        //TODO: Date Format for today 2016/09/15 08:00 - Add Tabs UpComing and Past
//        final Query mEventsQuery = mDatabase.child("2016").orderByChild("date").limitToLast(20).startAt("2016/09/15 08:00");
        mEventsQuery = mDatabase.child("2017").orderByChild("date").limitToFirst(10).startAt("2017/03/15 08:00");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_event_list, container, false);
        progressDialog = new SpotsDialog(getActivity(), getActivity().getResources().getString(R.string.fetching_data));

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            pastEventsRecyclerView = (RecyclerView) view;
            Log.d(TAG, "pastEventsRecyclerView : " + pastEventsRecyclerView);

        }

        // Read from the database
        if(mEventList == null) {
            progressDialog.show();
        }
        mEventsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
                mEventList = new ArrayList<Event>();
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {

                    Event event = eventSnapshot.getValue(Event.class);
                    mEventList.add(event);
                    Log.d(TAG, "eventSnapshot key("+ eventSnapshot.getKey() + "): " + event.title);

                }
                pastEventsRecyclerView.setAdapter(new PastEventRecyclerViewAdapter(mEventList, mListener));
                progressDialog.dismiss();
                Log.d(TAG, "Count is: " + dataSnapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }


        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Event item);
    }
}
