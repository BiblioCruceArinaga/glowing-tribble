package com.rising.login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import com.rising.drawing.R;
import com.rising.mainscreen.MainScreenActivity;

public class WelcomeActivity extends Activity{

	private SessionManager session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_welcomelayout);
		
		session = new SessionManager(getApplicationContext());
		
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
			if (session.isLoggedIn()) {
				Intent i = new Intent(WelcomeActivity.this, MainScreenActivity.class);
				startActivity(i);
				finish();
			}else{
				Intent i = new Intent(WelcomeActivity.this, Login.class);
				startActivity(i);
				finish();
			}
			super.onPostExecute(arg0);
		}
		
	}
}
