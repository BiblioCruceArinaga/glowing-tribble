package com.rising.money.social;

import java.util.Locale;

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
import com.rising.money.SocialBonificationNetworkConnection;
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;
import com.rising.money.SocialBonificationNetworkConnection.OnSuccessBonification;

//Clase que publica en Facebook y registra la bonificación pertinente
public class Facebook_Publish extends Activity{
	
	//Variables;
	private Context ctx = this;
	private UiLifecycleHelper uiHelper;
	private String ID_BONIFICATION = "3";
	
	//URLs
	private String LINK = "http://scores.rising.es/";
	private String LINK_EN = "http://scores.rising.es/en/";
	
	//Mensajes
	private String NAME = "Scores";
	private String DESCRIPTION = "Todo el poder de las partituras en la palma de tu mano";
	private String DESCRIPTION_EN = "All the power of the scores in your hands";
	private String SUBTITLE = "Las partituras del futuro";
	private String SUBTITLE_EN = "Scores of the future";
	private String PICTURE = "http://www.scores.rising.es/img/facebook_share_image.png";
	
	//Clases usadas
	private SocialBonificationNetworkConnection SOCIALBONIFICATION_ASYNCTASK;
	private EnableButtonsData ENABLE_BUTTONS;
	
	
	private OnSuccessBonification SuccessBonification = new OnSuccessBonification(){

		@Override
		public void onSuccessBonification() {
			Toast.makeText(ctx, R.string.win_social, Toast.LENGTH_LONG).show();
			ENABLE_BUTTONS.setEnable_FB(false);
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
		
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
	    ENABLE_BUTTONS = new EnableButtonsData(ctx);
	    SOCIALBONIFICATION_ASYNCTASK = new SocialBonificationNetworkConnection(SuccessBonification, FailBonification, ctx);
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
	        	if(FacebookDialog.getNativeDialogPostId(data) != null){
	        		Log.i("Activity", "Success!");	     
	        		SOCIALBONIFICATION_ASYNCTASK.execute(ID_BONIFICATION);
	        	}else{
	        		finish();
	        	}
	            
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
	    Log.i("Destroy", "Se destruyó");
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
		FacebookDialog shareDialog;
		if(Locale.getDefault().getDisplayLanguage().toString().equals("español")){
			shareDialog = new FacebookDialog.ShareDialogBuilder(this)
			.setLink(LINK).setName(NAME).setDescription(DESCRIPTION).setCaption(SUBTITLE).setPicture(PICTURE).build();
		}else{
			shareDialog = new FacebookDialog.ShareDialogBuilder(this)
			.setLink(LINK_EN).setName(NAME).setDescription(DESCRIPTION_EN).setCaption(SUBTITLE_EN).setPicture(PICTURE).build();
		}
		
		uiHelper.trackPendingDialogCall(shareDialog.present());
	}
	
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    
	    if(Locale.getDefault().getDisplayLanguage().toString().equals("español")){
		    params.putString("name", NAME);
		    params.putString("caption", SUBTITLE);
		    params.putString("description", DESCRIPTION);
		    params.putString("link", LINK);
		    params.putString("picture", PICTURE);
	    }else{
	    	params.putString("name", NAME);
	 	    params.putString("caption", SUBTITLE_EN);
	 	    params.putString("description", DESCRIPTION_EN);
	 	    params.putString("link", LINK_EN);
	 	    params.putString("picture", PICTURE);
	    }
	    
	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(ctx, Session.getActiveSession(), params)).setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values, FacebookException error) {
	                if (error == null) {
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(ctx, "Posted story, id: "+postId, Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(ctx.getApplicationContext(), "Publish cancelled",Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    Toast.makeText(ctx.getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
	                } else {
	                    Toast.makeText(ctx.getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
	                }
	            }

	        }).build();
	    
	    feedDialog.show();
	}
	
}