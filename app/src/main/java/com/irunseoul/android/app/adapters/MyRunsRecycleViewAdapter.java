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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Log.d(TAG, "MyRunsRecycleViewAdapter | events :" + events + " listener: " + listener);
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

/*
        String photo_url = holder.mItem.photo_url;
        if(!photo_url.isEmpty()) {

            Log.d(TAG, "photo_url : " + photo_url);
            String res="";
            String final_res = "";
            Matcher m= Pattern.compile("-\\d+x\\d+").matcher(photo_url);
            if(m.find()) {
                res=m.group();

                int width = Integer.valueOf(res.substring(1,3));
                int height = Integer.valueOf(res.substring(4,6));

                width = width * 8;
                height = height * 8;
                final_res = String.format(Locale.US, "-%dx%d", width, height);
                Log.d(TAG, "res : " + res + " final_res : " + final_res);
            }

            photo_url = photo_url.replaceFirst("-\\d+x\\d+",final_res);
            Glide.with(holder.mDateView.getContext())
                    .load(photo_url)
                    .centerCrop()
                    .placeholder(R.drawable.run_background_2)
                    .crossFade()
                    .into(holder.mEventImage);
        }
*/

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        if(mListener != null)
            mListener.notifyMyMarathonCount(mEvents.size());
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mDateView;
        public final TextView mRunDistance;
        public final TextView mRunPace;
        public final TextView mRunElapsedTime;
        public MyRun mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.eventTitle);
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
