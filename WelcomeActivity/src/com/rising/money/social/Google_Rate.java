package com.rising.money.social;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.money.SocialBonificationNetworkConnection;
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;
import com.rising.money.SocialBonificationNetworkConnection.OnSuccessBonification;

//Clase que da opción a hacer una crítica en Google Play y da la bonificación correspondiente
public class Google_Rate extends Activity{

	//Variables
	private Context ctx;
	private String ID_BONIFICATION = "11";
		
	//Clases usadas
	private EnableButtonsData ENABLE_BUTTONS;
	private SocialBonificationNetworkConnection SOCIALBONIFICATION_ASYNCTASK;
	
	private OnSuccessBonification SuccessBonification = new OnSuccessBonification(){
	
		@Override
		public void onSuccessBonification() {
			Toast.makeText(ctx, R.string.win_social, Toast.LENGTH_LONG).show();
			ENABLE_BUTTONS.setEnable_Rate(false);	
			finish();
		}		
	};
	
	private OnFailBonification FailBonification = new OnFailBonification(){
	
		@Override
		public void onFailBonification() {
			Toast.makeText(ctx, R.string.fail_social, Toast.LENGTH_LONG).show();
			finish();
		}		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.ctx = this;
		SOCIALBONIFICATION_ASYNCTASK = new SocialBonificationNetworkConnection(SuccessBonification, FailBonification, ctx);
		ENABLE_BUTTONS = new EnableButtonsData(ctx);
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
		if(ENABLE_BUTTONS.getEnable_Rate()){
			SOCIALBONIFICATION_ASYNCTASK.execute(ID_BONIFICATION);
		}
	}
	
}