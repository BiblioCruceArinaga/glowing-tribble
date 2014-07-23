package com.rising.login.login;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class ProgressDialogFragment extends DialogFragment {
    private String message;

    public static ProgressDialogFragment newInstance(String message) {
        ProgressDialogFragment dialog = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        message = getArguments().getString("message");
        dialog.setMessage(message);
        return dialog;
    }
}