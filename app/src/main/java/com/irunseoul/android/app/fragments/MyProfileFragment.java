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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    public static final String MARATHON_EVENT_DATABASE = "event";

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
    TextView totalMarathons;

    @BindView(R.id.myMarathonNumber)
    TextView myMarathonNumbers;

    @BindView(R.id.userPhoto)
    ImageView userPhoto;

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
        void notifyMarathonCount(int count);
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
        int marathon_events = PreferencesHelper.getPrefVal(pref, PreferencesHelper.KEY_UPCOMING_MARATHON_EVENTS);
        if(marathon_events == 0) {

            DatabaseReference database = FirebaseDatabase.getInstance().getReference(MARATHON_EVENT_DATABASE);;
            Query eventsQuery = database.child(DateHelper.getCurrentYear()).orderByChild("date").startAt(DateHelper.getCurrentDate());;

            eventsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()) {

                        int count = (int) dataSnapshot.getChildrenCount();
                        mListener.notifyMarathonCount(count);
                        totalMarathons.setText(String.valueOf(count));

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {

            totalMarathons.setText(String.valueOf(marathon_events));
        }

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

            //START Setting activity

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
