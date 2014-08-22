package com.rising.money.social;

import java.util.Locale;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.money.SocialBonificationNetworkConnection;
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;
import com.rising.money.SocialBonificationNetworkConnection.OnSuccessBonification;

//Clase que publica un mensaje en el Twitter del usuario
public class Twitter_Publish extends Activity {
	
	//Variables
	private Context ctx = this;	
	private String Language;
	private String ID_BONIFICATION = "4";
	
	//Keys
	private String CONSUMER_KEY = "9pCGWCrRzrnABt41uaSy2FjuJ";
	private String CONSUMER_SECRET = "DMLUcLrC33UEmZ67t6KOpGNUyhia9I0F2qLBuDJiKF3KMKpfV8";
	private String TOKEN_ACCESS = "470327856-kn5dIpwYQ2df6W5enBKIbd3a3QMRmzzu9X7fKSpZ";
	private String TOKEN_SECRET = "HfOX04yB2FQBq6KI1CQvfzPQJAZYcDaqVx1EAjoFYBTyN";
	
	//Mensajes
	private String MESSAGE = "Estoy usando Scores. La aplicación para partituras en formato digital. ¡PRUEBALA! http://scores.rising.es #rising #scores";
	private String MESSAGE_EN = "I'm using Scores. The app of the scores in digital format. CHECK IT!! http://scores.rising.es/en/ #rising #scores";
	
	//Clases usadas
	private SocialBonificationNetworkConnection SOCIALBONIFICATION_ASYNCTASK;
	private EnableButtonsData ENABLE_BUTTONS;
	
	private OnSuccessBonification SuccessBonification = new OnSuccessBonification(){

		@Override
		public void onSuccessBonification() {
			Toast.makeText(ctx, R.string.win_social, Toast.LENGTH_LONG).show();
			ENABLE_BUTTONS.setEnable_TW(false);
			finish();	
		}		
	};
	
	private OnFailBonification FailBonification = new OnFailBonification(){

		@Override
		public void onFailBonification() {
			new Social_Utils(ctx).Dialog_Aviso(ctx.getString( R.string.fail_social));
			finish();
		}		
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (android.os.Build.VERSION.SDK_INT > 11) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }
		
		this.ENABLE_BUTTONS = new EnableButtonsData(ctx);
		this.SOCIALBONIFICATION_ASYNCTASK = new SocialBonificationNetworkConnection(SuccessBonification, FailBonification, ctx);
		
		try {
			publish();
			new Social_Utils(ctx).Dialog_Aviso(ctx.getString( R.string.tweet_done));
		} catch (TwitterException e) {
			new Social_Utils(ctx).Dialog_Aviso(ctx.getString( R.string.tweet_fail));
			Toast.makeText(ctx, ctx.getString(R.string.tweet_fail), Toast.LENGTH_LONG).show();
			Log.e("TwitterError", e.getMessage());
			finish();
		}
	}
	 	 
	 public void publish() throws TwitterException{
	 		 
		 Language = Locale.getDefault().getDisplayLanguage();
		 
	    TwitterFactory twitterFactory = new TwitterFactory();
	    Twitter twitter = twitterFactory.getInstance();
	    	 
	    twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	    twitter.setOAuthAccessToken(new AccessToken(TOKEN_ACCESS, TOKEN_SECRET));
	 
	    StatusUpdate statusUpdate;
	    if(Language.equals("español")){
	    	statusUpdate = new StatusUpdate(MESSAGE);
	    }else{
	    	statusUpdate = new StatusUpdate(MESSAGE_EN);
	    }
	    
	    Status status = twitter.updateStatus(statusUpdate);	
	    
	    SOCIALBONIFICATION_ASYNCTASK.execute(ID_BONIFICATION);
	    
	    Log.i("TwitterStatus", "Status: " + status); //<--¿Para que se usa el Status? ¿Qué devuelve?
	 }
	 
}