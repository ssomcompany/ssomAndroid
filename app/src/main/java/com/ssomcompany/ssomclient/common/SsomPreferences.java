package com.ssomcompany.ssomclient.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SsomPreferences {
    public static final String CHATTING_PREF = "com.ssomcompany.ssomclient.chatting.pref";
    public static final String FILTER_PREF = "com.ssomcompany.ssomclient.filter.pref";
    public static final String LOGIN_PREF = "com.ssomcompany.ssomclient.login.pref";

    // session
    public static final String PREF_SESSION_HEART_REFILL_TIME = "PREF_SESSION_HEART_REFILL_TIME";
    public static final String PREF_SESSION_TOKEN = "PREF_SESSION_TOKEN";
    public static final String PREF_SESSION_HEART = "PREF_SESSION_HEART";
    public static final String PREF_SESSION_USER_ID = "PREF_SESSION_USER_ID";
    public static final String PREF_SESSION_TODAY_IMAGE_URL = "PREF_SESSION_TODAY_IMAGE_URL";
    public static final String PREF_SESSION_UNREAD_COUNT = "PREF_SESSION_UNREAD_COUNT";

    // chatting preferences
    public static final String PREF_CHATTING_GUIDE_IS_READ = "PREF_CHATTING_GUIDE_IS_READ";

    // filter preferences
    public static final String PREF_FILTER_AGE = "PREF_FILTER_AGE";
    public static final String PREF_FILTER_PEOPLE = "PREF_FILTER_PEOPLE";


    private final Context mContext;
    private SharedPreferences pref;
    private final String prefName;

    public SsomPreferences(Context ctx, String prefName) {
        this.mContext = ctx;
        this.prefName = prefName;
    }

    public void put(String key, String value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        pref.edit().putString(key, value).apply();
    }

    public void put(String key, boolean value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        pref.edit().putBoolean(key, value).apply();
    }

    public void put(String key, int value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        pref.edit().putInt(key, value).apply();
    }

    public void put(String key, long value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        pref.edit().putLong(key, value).apply();
    }

    public void put(String key, ArrayList<String> value) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        StringBuilder str = new StringBuilder();
        for (int i=0 ; i<value.size() ; i++) {
            str.append(value.get(i));
            if(i != value.size() - 1) str.append(",");
        }
        pref.edit().putString(key, str.toString()).apply();
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

    public long getLong(String key, long defValue) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);

        try {
            return pref.getLong(key, defValue);
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

    public ArrayList<String> getStringArray(String key, ArrayList<String> defValue) {
        pref = mContext.getSharedPreferences(prefName, Activity.MODE_PRIVATE);

        String savedString = pref.getString(key, "");
        if(TextUtils.isEmpty(savedString)) {
            return defValue;
        }

        StringTokenizer st = new StringTokenizer(savedString, ",");
        ArrayList<String> savedList = new ArrayList<>();
        while (st.hasMoreTokens()) {
            savedList.add(st.nextToken());
        }
        return savedList;
    }
}
