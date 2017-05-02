package com.irunseoul.android.app.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by hassanabid on 13/04/2017.
 */

public class PreferencesHelper {

    public static final String TAG = PreferencesHelper.class.getSimpleName();
    public static final String SHARED_PREF = "mypref";

    public static final String KEY_UPCOMING_MARATHON_EVENTS = "upcoming_marathons";
    public static final String MY_MARATHON_EVENTS = "my_marathons";
    public static final String WHICH_FRAGMENT = "which_fragment";


    public static SharedPreferences getSharedPref(Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPref;

    }

    public static void writePref(SharedPreferences pref, String key, int value) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    public static int getPrefVal(SharedPreferences pref, String key) {

        return pref.getInt(key, 0);
    }




}
