package com.rising.money;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.money.Invitations.OnInvitationFail;
import com.rising.money.Invitations.OnInvitationOk;

public class FreeMoneyActivity extends Activity{
	
	Dialog friends, social; 
	Button B_SFriendsDialog, GRate;
	TextView TV_Rate;
	EditText ET_SFriendsDialog;
	Context ctx = this;
	Invitations invitacion;
	private EnableButtonsData EBD;
	
	private OnInvitationOk listenerInvitation = new OnInvitationOk(){

		@Override
		public void onInvitationOk() {
			Toast.makeText(ctx, invitacion.mensaje, Toast.LENGTH_SHORT).show();
		}
		
	};
	
	private OnInvitationFail failInvitation = new OnInvitationFail(){

		@Override
		public void onInvitationFail() {
			Toast.makeText(ctx, invitacion.mensaje, Toast.LENGTH_SHORT).show();	
		}
	
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.freemoney_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		EBD = new EnableButtonsData(ctx);
						
    	ActionBar ABar = getActionBar();
    	
    	ABar.setTitle(R.string.money);
    	ABar.setDisplayHomeAsUpEnabled(true); 
    	
    	invitacion = new Invitations(listenerInvitation, failInvitation, this);
    	    	
    	Button SFriends = (Button) findViewById(R.id.b_share_friends);
    	Button SSocial = (Button) findViewById(R.id.b_share_social);
    	GRate = (Button) findViewById(R.id.b_rate);
    	TV_Rate = (TextView) findViewById(R.id.tV_rate);   	
    	    	
    	SFriends.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				friends = new Dialog(FreeMoneyActivity.this, R.style.cust_dialog);
				
				friends.setContentView(R.layout.friends_share_dialog);
				friends.setTitle(R.string.friendmail);
				B_SFriendsDialog = (Button) friends.findViewById(R.id.b_sharefriend_dialog);
				ET_SFriendsDialog = (EditText) friends.findViewById(R.id.eT_sharefriend_dialog);
								
				B_SFriendsDialog.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {			
						
						if(ET_SFriendsDialog.getText().toString().equals("")){
							Toast.makeText(ctx, R.string.friendmail_empty, Toast.LENGTH_LONG).show();
						}else{
						
							invitacion.execute(ET_SFriendsDialog.getText().toString());
							friends.dismiss(); 
						}
					}
					
				});
				
				friends.show();
			}
		});
    	
    	SSocial.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				social = new Dialog(FreeMoneyActivity.this, R.style.cust_dialog);
				
				social.setContentView(R.layout.social_share_dialog);
				social.setTitle(R.string.share_social);
				ImageButton B_FB_SocialShareDialog = (ImageButton) social.findViewById(R.id.ib_fb_social);
				ImageButton B_TWT_SocialShareDialog = (ImageButton) social.findViewById(R.id.ib_twt_social);
												
				//Debería poner un progressdialog que avise de que está cargando la ventana.
				B_FB_SocialShareDialog.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
						Log.i("Dialog_Enable", ""+EBD.getEnable_FB());
						Calendar c = Calendar.getInstance();
						long time = c.getTimeInMillis();
						
						if(EBD.getEnable_FB()){
							Intent i = new Intent(ctx, Facebook_Publish.class);
							startActivity(i);

							EBD.setEnable_FB(false);
							EBD.setTime_FB(c.getTimeInMillis());
						}else{
																					
							if(EBD.getTime_FB() != -1 && cantidadTotalHoras(EBD.getTime_FB(), time) >= 12){
								EBD.setEnable_FB(true);
							}else{
								Toast.makeText(ctx, getString(R.string.wait) + " " +(12-cantidadTotalHoras(EBD.getTime_FB(), time)) + " " + getString(R.string.hours), Toast.LENGTH_LONG).show();
							}
							
						}
						Log.i("Time", "Time: "+time+ ", Diferencias: " + (12 - cantidadTotalHoras(EBD.getTime_FB(), time)) + ", Horas: " + EBD.getTime_FB());
					} 
						
				});
				
				B_TWT_SocialShareDialog.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
						
						Log.i("Dialog_Enable", ""+EBD.getEnable_TW());
						Calendar c = Calendar.getInstance();
						long time = c.getTimeInMillis();
						
						if(EBD.getEnable_TW()){
							Intent i = new Intent(ctx, Twitter_Publish.class);
							startActivity(i);	

							EBD.setEnable_TW(false);
							EBD.setTime_TW(c.getTimeInMillis());
						}else{
																					
							if(EBD.getTime_TW() != -1 && cantidadTotalHoras(EBD.getTime_TW(), time) >= 12){
								EBD.setEnable_TW(true);
							}else{
								Toast.makeText(ctx, getString(R.string.wait) + " " +(12-cantidadTotalHoras(EBD.getTime_TW(), time)) + " " + getString(R.string.hours), Toast.LENGTH_LONG).show();
							}
							
						}
						Log.i("Time", "Time: "+time+ ", Diferencias: " + (12 - cantidadTotalHoras(EBD.getTime_TW(), time)) + ", Horas: " + EBD.getTime_TW());		
					}
					
				});
				
				social.show();
			}
    		
    	});

    	GRate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx, Google_Rate.class);
				startActivity(i);
				onPause();
			}
    	});
    	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(EBD.getEnable_Rate()){
    		GRate.setVisibility(View.VISIBLE);
    		TV_Rate.setVisibility(View.VISIBLE);
    	}else{
    		GRate.setVisibility(View.INVISIBLE);
    		GRate.setEnabled(false);
    		TV_Rate.setVisibility(View.INVISIBLE);
    	}
	}

	/*Metodo que devuelve el Numero total de horas que hay entre las dos Fechas */ 
	public static long cantidadTotalHoras(long fechaInicial, long fechaFinal){  
		long totalMinutos=0; 
		totalMinutos=((fechaFinal-fechaInicial)/1000/60/60); 
		return totalMinutos; 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    		        
	    	case android.R.id.home:
	    		Intent in = new Intent(this, MoneyActivity.class);
	    		startActivity(in);
	    		finish();
	    		
	    	default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}