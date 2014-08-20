package com.rising.money.social;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;
import com.rising.money.MoneyActivity;
import com.rising.money.social.Invitations.OnInvitationFail;
import com.rising.money.social.Invitations.OnInvitationOk;

//Clase que muestra las opciones para conseguir saldo gratis
public class FreeMoneyActivity extends Activity{
	
	//Variables
	private Context ctx;
	private Dialog friends, social; 
	private Button B_SFriendsDialog, GRate;
	private TextView TV_Rate;
	private EditText ET_SFriendsDialog;
	
	//Clases usadas
	private Invitations INVITATIONS;
	private EnableButtonsData ENABLE_BUTTONS;
	
	private OnInvitationOk SuccessInvitation = new OnInvitationOk(){

		@Override
		public void onInvitationOk() {
			Toast.makeText(ctx, INVITATIONS.getMensaje(), Toast.LENGTH_SHORT).show();
		}	
	};
	
	private OnInvitationFail FailInvitation = new OnInvitationFail(){

		@Override
		public void onInvitationFail() {
			Toast.makeText(ctx, INVITATIONS.getMensaje(), Toast.LENGTH_SHORT).show();	
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.money_freemoneylayout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.ctx = this;
		this.ENABLE_BUTTONS = new EnableButtonsData(ctx);
		this.INVITATIONS = new Invitations(SuccessInvitation, FailInvitation, ctx);
		
    	ActionBar ABar = getActionBar();
    	
    	ABar.setTitle(R.string.money);
    	ABar.setDisplayHomeAsUpEnabled(true); 
    	    	    	
    	Button SFriends = (Button) findViewById(R.id.b_share_friends);
    	Button SSocial = (Button) findViewById(R.id.b_share_social);
    	GRate = (Button) findViewById(R.id.b_rate);
    	TV_Rate = (TextView) findViewById(R.id.tV_rate);   	
    	    	
    	SFriends.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(new Login_Utils(ctx).isOnline()){
					friends = new Dialog(FreeMoneyActivity.this, R.style.cust_dialog);
					
					friends.setContentView(R.layout.money_social_friendssharedialog);
					friends.setTitle(R.string.friendmail);
					B_SFriendsDialog = (Button) friends.findViewById(R.id.b_sharefriend_dialog);
					ET_SFriendsDialog = (EditText) friends.findViewById(R.id.eT_sharefriend_dialog);
									
					B_SFriendsDialog.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {			
							
							if(ET_SFriendsDialog.getText().toString().equals("")){
								Toast.makeText(ctx, R.string.friendmail_empty, Toast.LENGTH_LONG).show();
							}else{
							
								INVITATIONS.execute(ET_SFriendsDialog.getText().toString());
								friends.dismiss(); 
							}
						}
						
					});
					
					friends.show();
				}else{
					new Login_Errors(ctx).errLogin(4);
				}
			}
		});
    	
    	SSocial.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(new Login_Utils(ctx).isOnline()){
					social = new Dialog(FreeMoneyActivity.this, R.style.cust_dialog);
					social.setContentView(R.layout.money_social_socialsharedialog);
					social.setTitle(R.string.share_social);
					ImageButton B_FB_SocialShareDialog = (ImageButton) social.findViewById(R.id.ib_fb_social);
					ImageButton B_TWT_SocialShareDialog = (ImageButton) social.findViewById(R.id.ib_twt_social);
													
					B_FB_SocialShareDialog.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {
							
							Log.i("Dialog_Enable", "" + ENABLE_BUTTONS.getEnable_FB());
							Calendar c = Calendar.getInstance();
							long time = c.getTimeInMillis();
							
							if(ENABLE_BUTTONS.getEnable_FB()){
								Intent i = new Intent(ctx, Facebook_Publish.class);
								startActivity(i);
	
								ENABLE_BUTTONS.setEnable_FB(false);
								ENABLE_BUTTONS.setTime_FB(c.getTimeInMillis());
							}else{
																						
								if(ENABLE_BUTTONS.getTime_FB() != -1 && cantidadTotalHoras(ENABLE_BUTTONS.getTime_FB(), time) >= 12){
									ENABLE_BUTTONS.setEnable_FB(true);
								}else{
									Toast.makeText(ctx, getString(R.string.wait) + " " +(12-cantidadTotalHoras(ENABLE_BUTTONS.getTime_FB(), time)) + " " + getString(R.string.hours), Toast.LENGTH_LONG).show();
								}
								
							}
							Log.i("Time", "Time: "+time+ ", Diferencias: " + (12 - cantidadTotalHoras(ENABLE_BUTTONS.getTime_FB(), time)) + ", Horas: " + ENABLE_BUTTONS.getTime_FB());
						} 
							
					});
					
					B_TWT_SocialShareDialog.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {
													
							Log.i("Dialog_Enable", ""+ENABLE_BUTTONS.getEnable_TW());
							Calendar c = Calendar.getInstance();
							long time = c.getTimeInMillis();
							
							if(ENABLE_BUTTONS.getEnable_TW()){
								Intent i = new Intent(ctx, Twitter_Publish.class);
								startActivity(i);	
	
								ENABLE_BUTTONS.setEnable_TW(false);
								ENABLE_BUTTONS.setTime_TW(c.getTimeInMillis());
							}else{
																						
								if(ENABLE_BUTTONS.getTime_TW() != -1 && cantidadTotalHoras(ENABLE_BUTTONS.getTime_TW(), time) >= 12){
									ENABLE_BUTTONS.setEnable_TW(true);
								}else{
									Toast.makeText(ctx, getString(R.string.wait) + " " +(12-cantidadTotalHoras(ENABLE_BUTTONS.getTime_TW(), time)) + " " + getString(R.string.hours), Toast.LENGTH_LONG).show();
								}
								
							}
							Log.i("Time", "Time: "+time+ ", Diferencias: " + (12 - cantidadTotalHoras(ENABLE_BUTTONS.getTime_TW(), time)) + ", Horas: " + ENABLE_BUTTONS.getTime_TW());		
						}
						
					});
					
					social.show();
				}else{
					new Login_Errors(ctx).errLogin(4);
				}
			}
    		
    	});

    	GRate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(new Login_Utils(ctx).isOnline()){
					Intent i = new Intent(ctx, Google_Rate.class);
					startActivity(i);
					onPause();
				}else{
					new Login_Errors(ctx).errLogin(4);
				}
			}
    	});
    	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(ENABLE_BUTTONS.getEnable_Rate()){
    		GRate.setVisibility(View.VISIBLE);
    		TV_Rate.setVisibility(View.VISIBLE);
    	}else{
    		GRate.setVisibility(View.INVISIBLE);
    		GRate.setEnabled(false);
    		TV_Rate.setVisibility(View.INVISIBLE);
    	}
	}
 
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