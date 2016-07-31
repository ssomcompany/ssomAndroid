package com.ssomcompany.ssomclient.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SsomPreferences {
    public static final String CHATTING_PREF = "com.ssomcompany.ssomclient.chatting.pref";
    public static final String FILTER_PREF = "com.ssomcompany.ssomclient.filter.pref";
    public static final String LOGIN_PREF = "com.ssomcompany.ssomclient.login.pref";

    // session
    public static final String PREF_SESSION_TOKEN = "PREF_SESSION_TOKEN";
    public static final String PREF_SESSION_EMAIL = "PREF_SESSION_EMAIL";

    // chatting preferences
    public static final String PREF_CHATTING_GUIDE_IS_READ = "PREF_CHATTING_GUIDE_IS_READ";

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
        editor.apply();
    }

    public void put(String key, boolean value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void put(String key, int value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
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

    public boolean getBoolean(String key, boolean defValue) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, defValue);
        } catch(Exception e) {
            return defValue;
        }
    }
}
