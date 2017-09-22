package com.example.heshammostafa.mykeystrok.inputmethodcommon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import java.util.List;

import static com.example.heshammostafa.mykeystrok.biokeyboard.BioKeyboard.TAG;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */

class InputMethodSettings {
    private Context mContext;
    private InputMethodManager mManager;
    private InputMethodInfo mMethodInfo;

    private Preference mSubtypeEnablerPreference;

    private int mSubtypeEnablerTitleRes;
    private CharSequence mSubtypeEnablerTitle;
    private int mSubtypeEnablerIconRes;
    private Drawable mSubtypeEnablerIcon;

    /**
     * Initialize internal states of this object.
     *
     * @param context the context for this application.
     * @param prefScreen a PreferenceScreen of PreferenceActivity or PreferenceFragment.
     * @return true if this application is an IME and has two or more subtypes, false otherwise.
     */
    public boolean init(final Context context, final PreferenceScreen prefScreen) {
        Log.i(TAG, "InputMethodSettings/init");

        mContext = context;
        mManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mMethodInfo = getInputMethodInfo(context, mManager);
        if (mMethodInfo == null || mMethodInfo.getSubtypeCount() <= 1) return false;

        mSubtypeEnablerPreference = new Preference(context);
        mSubtypeEnablerPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        CharSequence title = getSubtypeEnablerTitle(context);
                        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);
                        intent.putExtra(Settings.EXTRA_INPUT_METHOD_ID, mMethodInfo.getId());
                        if (!TextUtils.isEmpty(title)) {
                            intent.putExtra(Intent.EXTRA_TITLE, title);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        return true;
                    }
                });
        prefScreen.addPreference(mSubtypeEnablerPreference);
        updateSubtypeEnabler();

        return true;
    }

    private static InputMethodInfo getInputMethodInfo(Context context, InputMethodManager manager)
    {
        Log.i(TAG, "InputMethodSettings/InputMethodInfo");

        final List<InputMethodInfo> infosList = manager.getInputMethodList();
        for (InputMethodInfo info : infosList) {
            if (info.getPackageName().equals(context.getPackageName())) return info;
        }
        return null;
    }

    private static String getEnabledSubtypesLabel(Context context, InputMethodManager manager, InputMethodInfo info) {
        Log.i(TAG, "InputMethodSettings/getEnabledSubtypesLabel");

        if (context == null || manager == null || info == null) return null;

        ApplicationInfo appInfo = info.getServiceInfo().applicationInfo;
        List<InputMethodSubtype> subtypesList = manager.getEnabledInputMethodSubtypeList(info, true);
        StringBuilder builder = new StringBuilder();
        for (InputMethodSubtype subtype : subtypesList) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(subtype.getDisplayName(context, info.getPackageName(), appInfo));
        }
        return builder.toString();
    }

    public void setSubtypeEnablerTitle(int resId) {
        Log.i(TAG, "InputMethodSettings/setSubtypeEnablerTitle");

        mSubtypeEnablerTitleRes = resId;
        updateSubtypeEnabler();
    }

    public void setSubtypeEnablerTitle(CharSequence title) {
        Log.i(TAG, "InputMethodSettings/setSubtypeEnablerTitle");

        mSubtypeEnablerTitleRes = 0;
        mSubtypeEnablerTitle = title;
        updateSubtypeEnabler();
    }

    public void setSubtypeEnablerIcon(int resId) {
        Log.i(TAG, "InputMethodSettings/setSubtypeEnablerIcon");

        mSubtypeEnablerIconRes = resId;
        updateSubtypeEnabler();
    }

    public void setSubtypeEnablerIcon(Drawable drawable) {
        Log.i(TAG, "InputMethodSettings/setSubtypeEnablerIcon");

        mSubtypeEnablerIconRes = 0;
        mSubtypeEnablerIcon = drawable;
        updateSubtypeEnabler();
    }

    private CharSequence getSubtypeEnablerTitle(Context context) {
        Log.i(TAG, "InputMethodSettings/getSubtypeEnablerTitle");

        if (mSubtypeEnablerTitleRes != 0) {
            return context.getString(mSubtypeEnablerTitleRes);
        } else {
            return mSubtypeEnablerTitle;
        }
    }

    public void updateSubtypeEnabler() {
        Log.i(TAG, "InputMethodSettings/updateSubtypeEnabler");

        if (mSubtypeEnablerPreference == null) return;

        if (mSubtypeEnablerTitleRes != 0) {
            mSubtypeEnablerPreference.setTitle(mSubtypeEnablerTitleRes);
        } else if (!TextUtils.isEmpty(mSubtypeEnablerTitle)) {
            mSubtypeEnablerPreference.setTitle(mSubtypeEnablerTitle);
        }

        String summary = getEnabledSubtypesLabel(mContext, mManager, mMethodInfo);
        if (!TextUtils.isEmpty(summary)) {
            mSubtypeEnablerPreference.setSummary(summary);
        }

        if (mSubtypeEnablerIconRes != 0) {
            mSubtypeEnablerPreference.setIcon(mSubtypeEnablerIconRes);
        } else if (mSubtypeEnablerIcon != null) {
            mSubtypeEnablerPreference.setIcon(mSubtypeEnablerIcon);
        }
    }
}
