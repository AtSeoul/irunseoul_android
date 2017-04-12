package com.irunseoul.android.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.irunseoul.android.app.fragments.MyRunsFragment.OnMyRunFragmentInteractionListener;

import java.util.List;
import java.util.Locale;

import com.irunseoul.android.app.R;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.model.MyRun;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Event} and makes a call to the
 * specified {@link OnMyRunFragmentInteractionListener}.
 */

public class MyRunsRecycleViewAdapter extends RecyclerView.Adapter<MyRunsRecycleViewAdapter.ViewHolder> {

    private static final String TAG = MyRunsRecycleViewAdapter.class.getSimpleName();
    private final List<MyRun> mEvents;
    private final OnMyRunFragmentInteractionListener mListener;

    public MyRunsRecycleViewAdapter(List<MyRun> events, OnMyRunFragmentInteractionListener listener) {
        Log.d(TAG, "MyRunsRecycleViewAdapter");
        mEvents = events;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myruns_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mEvents.get(position);
        holder.mTitleView.setText(holder.mItem.title);
        holder.mDateView.setText(holder.mItem.date);
        holder.mRunDistance.setText(String.format(Locale.US, "%s KM",holder.mItem.distance));
        holder.mRunElapsedTime.setText(holder.mItem.moving_time);
        holder.mRunPace.setText(String.format(Locale.US, "%s KM/H",holder.mItem.average_speed));

        String photo_url = holder.mItem.photo_url;
        if(!photo_url.isEmpty()) {

            photo_url = photo_url.replace("-64x64","-512x512");
            Glide.with(holder.mDateView.getContext())
                    .load(photo_url)
                    .centerCrop()
                    .placeholder(R.drawable.run_background_2)
                    .crossFade()
                    .into(holder.mEventImage);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final ImageView mEventImage;
        public final TextView mDateView;
        public final TextView mRunDistance;
        public final TextView mRunPace;
        public final TextView mRunElapsedTime;
        public MyRun mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.eventTitle);
            mEventImage = (ImageView) view.findViewById(R.id.runImage);
            mDateView = (TextView) view.findViewById(R.id.eventDate);
            mRunDistance = (TextView) view.findViewById(R.id.runDistance);
            mRunPace = (TextView) view.findViewById(R.id.runPace);
            mRunElapsedTime = (TextView) view.findViewById(R.id.runElapsedTime);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
