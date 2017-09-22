package com.example.heshammostafa.mykeystrok.biokeyboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */
public class DialogConfirmation extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_OK_BUTTON = "ok_button";
    private static final String KEY_CANCEL_BUTTON = "cancel_button";

    public static <T extends Fragment & ConfirmationListener> DialogConfirmation newInstance(T fragment, int requestId,
                                                                                             CharSequence message,
                                                                                             CharSequence okButtonText,
                                                                                             CharSequence cancelButtonText)
    {
        DialogConfirmation dialog = new DialogConfirmation();
        dialog.setTargetFragment(fragment, requestId);

        Bundle args = new Bundle();
        args.putCharSequence(KEY_MESSAGE, message);
        args.putCharSequence(KEY_OK_BUTTON, okButtonText);
        args.putCharSequence(KEY_CANCEL_BUTTON, cancelButtonText);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        return new AlertDialog.Builder(getActivity())
                .setMessage(args.getCharSequence(KEY_MESSAGE))
                .setPositiveButton(args.getCharSequence(KEY_OK_BUTTON), this)
                .setNegativeButton(args.getCharSequence(KEY_CANCEL_BUTTON), this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int button) {
        ConfirmationListener listener = (ConfirmationListener) getTargetFragment();
        boolean confirmed = button == DialogInterface.BUTTON_POSITIVE;
        listener.onResponseReceived(getTargetRequestCode(), confirmed);
    }

    public interface ConfirmationListener {
        void onResponseReceived(int requestId, boolean confirmed);
    }
}
