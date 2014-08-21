package com.rising.store;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class ProgressiveDialogFragment extends DialogFragment{
	
	 private String message;
	 private int progress;
	 private boolean indeterminate;

	 public static ProgressiveDialogFragment newInstance(String message, int progress, boolean indeterminate) {
		ProgressiveDialogFragment dialog = new ProgressiveDialogFragment();
	    Bundle args = new Bundle();
	    args.putString("message", message);
	    args.putInt("progress", progress);
	    args.putBoolean("indeterminate", indeterminate);
	    dialog.setArguments(args);
	  	    
	    return dialog;
	 }

	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
	    message = getArguments().getString("message");
	    indeterminate = getArguments().getBoolean("indeterminate");
	    progress = getArguments().getInt("progress");
	    
	    dialog.setMessage(message);
	 	dialog.setIndeterminate(indeterminate);
	 	dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	 	dialog.setCancelable(true);
		dialog.setMax(100);
		dialog.setProgress(progress);
	    return dialog;
	 }
	 
}