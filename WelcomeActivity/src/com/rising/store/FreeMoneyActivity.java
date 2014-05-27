package com.rising.store;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.rising.drawing.R;

public class FreeMoneyActivity extends Activity{
	
	Dialog friends, social; 
	Button B_SFriendsDialog;
	EditText ET_SFriendDialog;
	String FriendMail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.freemoney_layout);
								
    	ActionBar ABar = getActionBar();
    	
    	ABar.setTitle(R.string.money);
    	ABar.setDisplayHomeAsUpEnabled(true); 
    	
    	Button SFriends = (Button) findViewById(R.id.b_share_friends);
    	Button SSocial = (Button) findViewById(R.id.b_share_social);
    	
    	SFriends.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				friends = new Dialog(FreeMoneyActivity.this);
				
				friends.setContentView(R.layout.friends_share_dialog);
				friends.setTitle(R.string.friendmail);
				Button B_SFriendsDialog = (Button) friends.findViewById(R.id.b_sharefriend_dialog);
				EditText ET_SFriendsDialog = (EditText) friends.findViewById(R.id.eT_sharefriend_dialog);
								
				B_SFriendsDialog.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						//Enviar mail a FriendMail
						//FriendMail = ET_SFriendDialog.getText().toString();
						friends.dismiss();
					}
					
				});
				
				friends.show();
			}
		});
    	
    	SSocial.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				social = new Dialog(FreeMoneyActivity.this);			
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