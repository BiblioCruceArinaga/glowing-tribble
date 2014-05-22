package com.rising.money;

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


public class Twitter_Publish extends Activity {
	
	Context ctx;	 
	 
	private String CONSUMER_KEY = "9pCGWCrRzrnABt41uaSy2FjuJ";
	private String CONSUMER_SECRET = "DMLUcLrC33UEmZ67t6KOpGNUyhia9I0F2qLBuDJiKF3KMKpfV8";
	private String TOKEN_ACCESS = "470327856-kn5dIpwYQ2df6W5enBKIbd3a3QMRmzzu9X7fKSpZ";
	private String TOKEN_SECRET = "HfOX04yB2FQBq6KI1CQvfzPQJAZYcDaqVx1EAjoFYBTyN";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Necesario para lanzar el Tweet
		if (android.os.Build.VERSION.SDK_INT > 11) {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	    }
		
		try {
			publish();
		} catch (TwitterException e) {
			Log.e("TwitterError", e.getMessage());
			e.printStackTrace();
		}
	}
	 	 
	 public void publish() throws TwitterException{
	 
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
	        StatusUpdate statusUpdate = new StatusUpdate("Estoy usando Scores. La aplicación para partituras en formato digital. ¡PRUEBALA! http://scores.rising.es #rising #scores");
	        
	        //attach any media, if you want to
	        //statusUpdate.setMedia("http://h1b-work-visa-usa.blogspot.com", new URL("http://lh6.ggpht.com/-NiYLR6SkOmc/Uen_M8CpB7I/AAAAAAAAEQ8/tO7fufmK0Zg/h-1b%252520transfer%252520jobs%25255B4%25255D.png?imgmax=800").openStream());
	 
	        //tweet or update status
	        Status status = twitter.updateStatus(statusUpdate);	
	        Log.i("TwitterStatus", "Status: " + status);
	 }
	 
}
