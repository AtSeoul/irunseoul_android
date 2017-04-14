package com.irunseoul.android.app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irunseoul.android.app.R;
import com.irunseoul.android.app.adapters.MyRunsRecycleViewAdapter;
import com.irunseoul.android.app.model.MyRun;
import com.irunseoul.android.app.utilities.PreferencesHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMyProfileFragmentInteractionListener}
 * interface.
 */
public class MyProfileFragment extends Fragment {

    private static final String TAG = MyProfileFragment.class.getSimpleName();
    public static final String MARATHON_EVENT_DATABASE = "marathon_event";

    private OnMyProfileFragmentInteractionListener mListener;


    private DatabaseReference mDatabase;
    private List<MyRun> myRunList;
    private Query mEventsQuery;

    private RecyclerView pastEventsRecyclerView;
    private AlertDialog progressDialog;

    @BindView(R.id.logout)
    Button logout;

    @BindView(R.id.root_view)
    View rootView;

    @BindView(R.id.userName)
    TextView userName;

    @BindView(R.id.marathonNumber)
    TextView totaMarathons;

    @BindView(R.id.myMarathonNumber)
    TextView myMarathonNumbers;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mEventsQuery = mDatabase.child("user-runs").child(getUid()).orderByChild("date");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        ButterKnife.bind(this, view);

        progressDialog = new SpotsDialog(getActivity(), getActivity().getResources().getString(R.string.fetching_data));

        if(myRunList == null) {
//            progressDialog.show();
        }

        Log.d(TAG,"photoURL : " + photoUrl());

        setUI();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    }

    private void setUI() {

        String user_name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        userName.setText(user_name);

        SharedPreferences pref = PreferencesHelper.getSharedPref(getActivity());
        int marathon_events = PreferencesHelper.getPrefVal(pref, PreferencesHelper.KEY_UPCOMING_MARATHON_EVENTS);
        int my_marathon_events = PreferencesHelper.getPrefVal(pref, PreferencesHelper.MY_MARATHON_EVENTS);

        myMarathonNumbers.setText(String.valueOf(my_marathon_events));
        totaMarathons.setText(String.valueOf(marathon_events));


    }

    public String getUid() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public Uri photoUrl() {

        return FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    }


}
