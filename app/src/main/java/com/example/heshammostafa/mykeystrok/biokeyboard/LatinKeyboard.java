package com.example.heshammostafa.mykeystrok.biokeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

import com.example.heshammostafa.mykeystrok.R;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */

public class LatinKeyboard extends Keyboard {
    private Type mType;

    private Key mEnterKey;
    private Key mSpaceKey;

    public LatinKeyboard(Context context, Type type) {
        this(context, type.xmlLayoutResId, type.id);
        mType = type;
    }

    private LatinKeyboard(Context context, int xmlLayoutResId, String name) {
        super(context, xmlLayoutResId);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
                                   XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);
        if (key.codes[0] == 10) {
            mEnterKey = key;
        } else if (key.codes[0] == ' ') {
            mSpaceKey = key;
        }
        return key;
    }

    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    void setImeOptions(Resources res, int options) {
        if (mEnterKey == null) {
            return;
        }

        switch (options&(EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_next_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
                mEnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_send_key);
                break;
            default:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
                mEnterKey.label = null;
                break;
        }
    }

    void setSpaceIcon(final Drawable icon) {
        if (mSpaceKey != null) {
            mSpaceKey.icon = icon;
        }
    }

    public Type getType() {
        return mType;
    }

    static class LatinKey extends Keyboard.Key {
        LatinKey(Resources res, Keyboard.Row parent, int x, int y, XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }

        /**
         * Overriding this method so that we can reduce the target area for the key that
         * closes the keyboard.
         */
        @Override
        public boolean isInside(int x, int y) {
            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
        }
    }

    public enum Type {
        QWERTY(R.xml.qwerty, "qwerty"),
        SYMBOLS(R.xml.symbols, "symbols"),
        SYMBOLS_SHIFTED(R.xml.symbols_shift, "symbols_shift"),
        NUMBERS(R.xml.numbers, "numbers");

        public final int xmlLayoutResId;
        public final String id;

        private Type(int xmlLayoutResId, String id) {
            this.xmlLayoutResId = xmlLayoutResId;
            this.id = id;
        }

        public static Type fromId(String id) {
            for (Type type : values()) {
                if (type.id.equals(id)) return type;
            }
            return null;
        }
    }
}
