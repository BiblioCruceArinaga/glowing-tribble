package com.rising.money;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.rising.drawing.R;
import com.rising.money.SocialBonificationNetworkConnection.OnBonificationDone;
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;

public class Facebook_Publish extends Activity{
	
	Context ctx = this;
	private UiLifecycleHelper uiHelper;
	private String LINK = "http://scores.rising.es/";
	private String NAME = "Scores";
	private String DESCRIPTION = "Todo el poder de las partituras en la palma de tu mano";
	private String SUBTITLE = "Las partituras del futuro";
	private String PICTURE = "";
	private String ID_BONIFICATION = "3";
	private SocialBonificationNetworkConnection sbnc;
		
	private OnBonificationDone successbonification = new OnBonificationDone(){

		@Override
		public void onBonificationDone() {
			Toast.makeText(ctx, R.string.win_social, Toast.LENGTH_LONG).show();
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
		
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
	    sbnc = new SocialBonificationNetworkConnection(successbonification, failbonification, ctx);
	    publish();
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        
	    	@Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("Activity", String.format("Error: %s", error.toString()));
	            finish();
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("Activity", "Success!");	     
	            sbnc.execute(ID_BONIFICATION);
	        }
	    });
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	private void publish(){
		
		//Si la aplicación de Facebook está instalada lanzará la primera parte, sino se apoyará en la web para lanzar la segunda parte
		if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			publishApp();		
		} else {
			publishFeedDialog();
		}
		
	}
	
	private void publishApp(){
		// Publish the post using the Share Dialog
		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
		.setLink(LINK).setName(NAME).setDescription(DESCRIPTION).setCaption(SUBTITLE).setPicture(PICTURE)
		.build();
		uiHelper.trackPendingDialogCall(shareDialog.present());
	}
	
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", NAME);
	    params.putString("caption", SUBTITLE);
	    params.putString("description", DESCRIPTION);
	    params.putString("link", LINK);
	    params.putString("picture", PICTURE);

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(ctx,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(ctx,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(ctx.getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(ctx.getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(ctx.getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	
}