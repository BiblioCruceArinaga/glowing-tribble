package com.rising.login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import com.rising.drawing.R;

public class WelcomeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_layout);
		
		new AsyncWelcome().execute();
	}
	
	class AsyncWelcome extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... secods) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		 
		@Override
		protected void onPostExecute(Void arg0) {
			Intent i = new Intent(WelcomeActivity.this, Login.class);
			startActivity(i);
			finish();
			super.onPostExecute(arg0);
		}
		
	}
}
