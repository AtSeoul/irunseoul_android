package com.irunseoul.android.app.adapters;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.irunseoul.android.app.R;
import com.irunseoul.android.app.fragments.CrewListFragment.OnCrewListFragmentInteractionListener;
import com.irunseoul.android.app.model.Crew;
import com.irunseoul.android.app.model.Event;
import com.irunseoul.android.app.utilities.DateHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Event} and makes a call to the
 * specified {@link OnCrewListFragmentInteractionListener}.
 */

public class CrewRecyclerViewAdapter extends RecyclerView.Adapter<CrewRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = CrewRecyclerViewAdapter.class.getSimpleName();
    private final List<Crew> mCrews;
    private final OnCrewListFragmentInteractionListener mListener;

    public CrewRecyclerViewAdapter(List<Crew> crews, OnCrewListFragmentInteractionListener listener) {
        Log.d(TAG, "CrewRecyclerViewAdapter");
        mCrews = crews;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.crew_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mCrews.get(position);
        holder.mNameView.setText(holder.mItem.name);
        holder.mDescriptionView.setText(holder.mItem.description);
//        holder.mInstaText.setText(holder.mItem.instagram);
        Resources res = holder.mNameView.getContext().getResources();

        if(!holder.mItem.logo_url.isEmpty()) {

            Glide.with(holder.mNameView.getContext())
                    .load(holder.mItem.logo_url)
                    .centerCrop()
                    .crossFade()
                    .into(holder.mCrewIcon);
        }

        holder.mInstaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onCrewListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.mVideoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onCrewVideoInteraction(holder.mItem.video);
                }
            }
        });

        holder.mWebsiteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onCrewVideoInteraction(holder.mItem.website);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mCrews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mDescriptionView;
        public final TextView mInstaText;
        public final ImageView mCrewIcon;
        public final TextView mVideoText;
        public final ImageView mVideoIcon;
        public final TextView mWebsiteText;
        public final ImageView mWebsiteIcon;

        public Crew mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.crewName);
            mDescriptionView = (TextView) view.findViewById(R.id.crewDescription);
            mInstaText = (TextView) view.findViewById(R.id.instaId);
            mCrewIcon = (ImageView) view.findViewById(R.id.runCrewIcon);
            mVideoText = (TextView) view.findViewById(R.id.youtubeText);
            mVideoIcon = (ImageView) view.findViewById(R.id.youtubeVideo);
            mWebsiteText = (TextView) view.findViewById(R.id.websiteText);
            mWebsiteIcon = (ImageView) view.findViewById(R.id.websiteImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
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
