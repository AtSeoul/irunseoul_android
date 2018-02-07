package com.irunseoul.android.app.adapters;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irunseoul.android.app.fragments.PastEventFragment.OnListFragmentInteractionListener;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.irunseoul.android.app.R;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.utilities.DateHelper;
import com.irunseoul.android.app.utilities.PreferencesHelper;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Event} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */

public class PastEventRecyclerViewAdapter extends RecyclerView.Adapter<PastEventRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = PastEventRecyclerViewAdapter.class.getSimpleName();
    private final List<Event> mEvents;
    private final OnListFragmentInteractionListener mListener;

    public PastEventRecyclerViewAdapter(List<Event> events, OnListFragmentInteractionListener listener) {
        Log.d(TAG, "PastEventRecyclerViewAdapter");
        mEvents = events;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mEvents.get(position);
        holder.mTitleView.setText(holder.mItem.title);
        holder.mDateView.setText(holder.mItem.date);
        holder.mRunCity.setText(holder.mItem.location);
        Resources res = holder.mTitleView.getContext().getResources();

        if(!holder.mItem.weather.isEmpty()) {
            String weather_name = holder.mItem.weather.replace("-", "_");
            String drawableName = "ic_" + weather_name;
            int drawableResourceId = res.getIdentifier(drawableName,"drawable", holder.mTitleView.getContext().getPackageName());
            try {
                Drawable drawable = res.getDrawable(drawableResourceId);
                holder.mTempIcon.setImageDrawable(drawable);
            } catch (Exception e) {

                Log.d(TAG, "exception while getting drawable : "  + e.getMessage());
            }

        }

        int diffDays = DateHelper.getDaysDiff(holder.mItem.date);

        if(diffDays < 0) {
            diffDays = -1 * diffDays;
            holder.mdaysLeft.setText(String.format(Locale.US, res.getString(R.string.d_days_ago), diffDays));
        } else if(diffDays == 0) {
            holder.mdaysLeft.setText(res.getString(R.string.today));
        } else {
            holder.mdaysLeft.setText(String.format(Locale.US, res.getString(R.string.d_day), diffDays));
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

        String[] result =  holder.mItem.application_period.split("-\\s");
        if(result != null) {

            if(result.length > 1) {
                if (DateHelper.isApplicationPeriod(result[1])) {
                    holder.mApplicationStatus.setVisibility(View.VISIBLE);
                } else {
                    holder.mApplicationStatus.setVisibility(View.GONE);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mRunCity;
        public final TextView mDateView;
        public final ImageView mTempIcon;
        public final TextView mdaysLeft;
        public final LinearLayout mApplicationStatus;
        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.eventTitle);
            mDateView = (TextView) view.findViewById(R.id.eventDate);
            mRunCity = (TextView) view.findViewById(R.id.runCity);
            mTempIcon = (ImageView) view.findViewById(R.id.runTempIcon);
            mdaysLeft = (TextView) view.findViewById(R.id.daysLeft);
            mApplicationStatus = (LinearLayout) view.findViewById(R.id.applicationStatus);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
