package com.rising.money;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.money.Invitations.OnInvitationFail;
import com.rising.money.Invitations.OnInvitationOk;

public class FreeMoneyActivity extends Activity{
	
	Dialog friends, social; 
	Button B_SFriendsDialog;
	EditText ET_SFriendsDialog;
	Context ctx = this;
	Invitations invitacion;
	
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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.freemoney_layout);
								
    	ActionBar ABar = getActionBar();
    	
    	ABar.setTitle(R.string.money);
    	ABar.setDisplayHomeAsUpEnabled(true); 
    	
    	invitacion = new Invitations(listenerInvitation, failInvitation, this);
    	
    	Button SFriends = (Button) findViewById(R.id.b_share_friends);
    	Button SSocial = (Button) findViewById(R.id.b_share_social);
    	    	
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
						
						Intent i = new Intent(ctx, Facebook_Publish.class);
						startActivity(i);
					}
						
				});
				
				B_TWT_SocialShareDialog.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Intent i = new Intent(ctx, Twitter_Publish.class);
						startActivity(i);						
					}
					
				});
				
				social.show();
			}
    		
    	});

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