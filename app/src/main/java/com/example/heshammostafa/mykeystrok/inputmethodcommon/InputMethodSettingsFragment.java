package com.example.heshammostafa.mykeystrok.inputmethodcommon;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * This is a helper class for an IME's settings preference fragment. It's recommended for every
 * IME to have its own settings preference fragment which inherits this class.
 */
public abstract class InputMethodSettingsFragment
        extends PreferenceFragment
{
    private final InputMethodSettings mSettings = new InputMethodSettings();
    public static final String TAG = "BioKeyboard";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity();
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(context));
        mSettings.init(context, getPreferenceScreen());
        Log.i(TAG, "InputMethodSettingsFragment/onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        mSettings.updateSubtypeEnabler();
        Log.i(TAG, "InputMethodSettingsFragment/onResume");

    }

    public void setSubtypeEnablerTitle(int resId) {
        Log.i(TAG, "InputMethodSettingsFragment/setSubtypeEnablerTitle");

        mSettings.setSubtypeEnablerTitle(resId);
    }

    public void setSubtypeEnablerTitle(CharSequence title) {
        mSettings.setSubtypeEnablerTitle(title);
    }

    public void setSubtypeEnablerIcon(int resId) {
        mSettings.setSubtypeEnablerIcon(resId);
    }

    public void setSubtypeEnablerIcon(Drawable drawable) {
        mSettings.setSubtypeEnablerIcon(drawable);
    }
}
