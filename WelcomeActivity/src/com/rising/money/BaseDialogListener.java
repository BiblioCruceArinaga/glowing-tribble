package com.rising.money;

import android.os.Bundle;
import android.util.Log;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class BaseDialogListener implements DialogListener{

	@Override
	public void onComplete(Bundle values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFacebookError(FacebookError e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(DialogError e) {
		Log.e("Facebook", e.getMessage());
        e.printStackTrace();
		
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}

}
