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
import com.rising.money.EnableButtonsData;
import com.rising.money.SocialBonificationNetworkConnection;
import com.rising.money.SocialBonificationNetworkConnection.OnBonificationDone;
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;


public class Twitter_Publish extends Activity {
	
	Context ctx = this;	 
	private String CONSUMER_KEY = "9pCGWCrRzrnABt41uaSy2FjuJ";
	private String CONSUMER_SECRET = "DMLUcLrC33UEmZ67t6KOpGNUyhia9I0F2qLBuDJiKF3KMKpfV8";
	private String TOKEN_ACCESS = "470327856-kn5dIpwYQ2df6W5enBKIbd3a3QMRmzzu9X7fKSpZ";
	private String TOKEN_SECRET = "HfOX04yB2FQBq6KI1CQvfzPQJAZYcDaqVx1EAjoFYBTyN";
	private String MESSAGE = "Estoy usando Scores. La aplicación para partituras en formato digital. ¡PRUEBALA! http://scores.rising.es #rising #scores";
	private String MESSAGE_EN = "I'm using Scores. The app of the scores in digital format. TEST IT!! http://scores.rising.es/en/ #rising #scores";
	//Mensaje alternativo "He conseguido 0,05 créditos en Scores, la aplicación de partituras en formato digital."";
	
	private String Language;
	private String ID_BONIFICATION = "4";
	private SocialBonificationNetworkConnection sbnc;
	private EnableButtonsData EBD;
	
	private OnBonificationDone successbonification = new OnBonificationDone(){

		@Override
		public void onBonificationDone() {
			Toast.makeText(ctx, R.string.win_social, Toast.LENGTH_LONG).show();
			EBD.setEnable_TW(false);
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
		
		//Necesario para lanzar el Tweet
		if (android.os.Build.VERSION.SDK_INT > 11) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }
		
		EBD = new EnableButtonsData(ctx);
	    sbnc = new SocialBonificationNetworkConnection(successbonification, failbonification, ctx);
		
		try {
			publish();
			Toast.makeText(ctx, "El tweet se envió correctamente", Toast.LENGTH_LONG).show();
		} catch (TwitterException e) {
			Toast.makeText(ctx, "Hubo un error y no se pudo enviar el tweet", Toast.LENGTH_LONG).show();
			Log.e("TwitterError", e.getMessage());
			e.printStackTrace();
			finish();
		}
	}
	 	 
	 public void publish() throws TwitterException{
	 		 
		 Language = Locale.getDefault().getDisplayLanguage();
		 
		//Instantiate a re-usable and thread-safe factory
	    TwitterFactory twitterFactory = new TwitterFactory();
	 
	    //Instantiate a new Twitter instance
	    Twitter twitter = twitterFactory.getInstance();
	    	 
	    //setup OAuth Consumer Credentials
	    twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	 
	    //setup OAuth Access Token
	    twitter.setOAuthAccessToken(new AccessToken(TOKEN_ACCESS, TOKEN_SECRET));
	 
	    //Instantiate and initialize a new twitter status update
	    //Message
	    StatusUpdate statusUpdate;
	    if(Language.equals("spanish")){
	    	statusUpdate = new StatusUpdate(MESSAGE);
	    }else{
	    	statusUpdate = new StatusUpdate(MESSAGE_EN);
	    }
	       
	    //attach any media, if you want to
	    //statusUpdate.setMedia("http://h1b-work-visa-usa.blogspot.com", new URL("http://lh6.ggpht.com/-NiYLR6SkOmc/Uen_M8CpB7I/AAAAAAAAEQ8/tO7fufmK0Zg/h-1b%252520transfer%252520jobs%25255B4%25255D.png?imgmax=800").openStream());
	 
	    //tweet or update status
	    Status status = twitter.updateStatus(statusUpdate);	
	    
	    sbnc.execute(ID_BONIFICATION);
	    
	    Log.i("TwitterStatus", "Status: " + status);
	 }
	 
}
