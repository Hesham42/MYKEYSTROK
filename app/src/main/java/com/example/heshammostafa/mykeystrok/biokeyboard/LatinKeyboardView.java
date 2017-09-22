package com.example.heshammostafa.mykeystrok.biokeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodSubtype;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */

public class LatinKeyboardView
        extends KeyboardView
{
    static final int KEYCODE_OPTIONS = -100;
    static final boolean PREVIEW_ENABLED = false; // TODO: This should probably be a setting

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPreviewEnabled(PREVIEW_ENABLED);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPreviewEnabled(PREVIEW_ENABLED);
    }

    @Override
    protected boolean onLongPress(Keyboard.Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        }

        return super.onLongPress(key);
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }
}