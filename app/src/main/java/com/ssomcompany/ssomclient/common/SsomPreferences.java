package com.ssomcompany.ssomclient.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Aaron Choi on 2016-02-04.
 */
public class SsomPreferences {
    public static final String FILTER_PREF = "com.ssomcompany.ssomclient.pref";

    // filter preferences
    public static final String PREF_FILTER_AGE = "PREF_FILTER_AGE";
    public static final String PREF_FILTER_PEOPLE = "PREF_FILTER_PEOPLE";

    private final Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private final String prefName;

    public SsomPreferences(Context ctx, String prefName) {
        this.mContext = ctx;
        this.prefName = prefName;
    }

    public void put(String key, String value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getString(String key, String defValue) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, defValue);
        } catch(Exception e) {
            return defValue;
        }
    }

    public int getInt(String key, int defValue) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);

        try {
            return pref.getInt(key, defValue);
        } catch(Exception e) {
            return defValue;
        }
    }
}
