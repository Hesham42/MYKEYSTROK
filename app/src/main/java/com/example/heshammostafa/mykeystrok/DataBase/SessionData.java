package com.example.heshammostafa.mykeystrok.DataBase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.heshammostafa.mykeystrok.BuildConfig;
import com.example.heshammostafa.mykeystrok.R;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */


public class SessionData {
    private final UUID mUuid;

    private final String mIdentity;
    private final String mKey;
    private final String mOsName;
    private final String mOsArch;
    private final String mOsVersion;
    private final long mStartTime;
    private final String mLocale;

    private final Set<String> mTags;

    private static final String TAG = "BioKeyboard/SessionData";

    public static SessionData fromRawValues(UUID uuid, String identity, String key, String osName, String osArch,
                                            String osVersion, long startTime, String locale, Set<String> tags) {
        return new SessionData(uuid, identity, key, osName, osArch, osVersion, startTime, locale, tags);
    }

    public static SessionData fromSystemData(Context context, Set<String> tags) {
        UUID uuid = UUID.randomUUID();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String identity = prefs.getString(context.getString(R.string.pref_identity_key), "");
        String key = "";

        if (BuildConfig.DEBUG) Log.v(TAG, "New session created for " + identity + " with id " + uuid);

        String osName = "Android";
        String osArch = System.getProperty("os.arch");
        String osVersion = Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ")";

        long time = System.currentTimeMillis();

        String locale = context.getResources().getConfiguration().locale.getDisplayName();

        Set<String> newTags = new HashSet<String>(tags);
        if (BuildConfig.DEBUG) newTags.add("debugging");

        return new SessionData(uuid, identity, key, osName, osArch, osVersion, time, locale, newTags);
    }

    private SessionData(UUID uuid, String identity, String key, String osName, String osArch, String osVersion,
                        long startTime, String locale, Set<String> tags) {
        this.mUuid = uuid;
        this.mIdentity = identity;
        this.mKey = key;
        this.mOsName = osName;
        this.mOsArch = osArch;
        this.mOsVersion = osVersion;
        this.mStartTime = startTime;
        this.mLocale = locale;
        this.mTags = Collections.unmodifiableSet(tags);
    }

    public String getPlatformDescription() {
        return String.format("%s %s - %s", mOsName, mOsVersion, mOsArch);
    }

    public UUID getUuid() {
        return mUuid;
    }

    public String getIdentity() {
        return mIdentity;
    }

    public String getKey() {
        return mKey;
    }

    public String getOsName() {
        return mOsName;
    }

    public String getOsArch() {
        return mOsArch;
    }

    public String getOsVersion() {
        return mOsVersion;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public String getLocale() {
        return mLocale;
    }

    public Set<String> getTags() {
        return mTags;
    }
}