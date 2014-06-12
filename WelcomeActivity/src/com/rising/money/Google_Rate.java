package com.rising.money;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.money.SocialBonificationNetworkConnection.OnBonificationDone;
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;

public class Google_Rate extends Activity{

	private EnableButtonsData EBD;
	private String ID_BONIFICATION = "11";
	private SocialBonificationNetworkConnection sbnc;
	private Context ctx = this;
	
	private OnBonificationDone successbonification = new OnBonificationDone(){
	
		@Override
		public void onBonificationDone() {
			Toast.makeText(ctx, R.string.win_social, Toast.LENGTH_LONG).show();
			EBD.setEnable_Rate(false);	
			finish();
		}		
	};
	
	private OnFailBonification failbonification = new OnFailBonification(){
	
		@Override
		public void onFailBonification() {
			Toast.makeText(ctx, R.string.fail_social, Toast.LENGTH_LONG).show();
			finish();
		}		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sbnc = new SocialBonificationNetworkConnection(successbonification, failbonification, ctx);
		EBD = new EnableButtonsData(ctx);
		launchMarket();
	}

	private void launchMarket() {
	    Uri uri = Uri.parse("market://details?id=" + getPackageName());
	    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
	    try {
	        startActivity(myAppLinkToMarket);
	    } catch (ActivityNotFoundException e) {
	        Toast.makeText(this, R.string.fail_market_app, Toast.LENGTH_LONG).show();
	    }
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(EBD.getEnable_Rate()){
			sbnc.execute(ID_BONIFICATION);
		}
	}
	
}
