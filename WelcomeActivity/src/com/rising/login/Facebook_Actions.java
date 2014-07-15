package com.rising.login;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.rising.drawing.R;

//Clase que gestiona el login/registro por Facebook
public class Facebook_Actions {

	private Context ctx;
	private String FId, FName, FMail;
	private LoginButton Facebook_Button;
	
	public Facebook_Actions(Context context){
		this.ctx = context;
		this.Facebook_Button = (LoginButton)((Activity) ctx).findViewById(R.id.button_login_f);
		FacebookButton_Actions();
	}	
	
	public void FacebookButton_Actions(){
		
		Facebook_Button.setOnErrorListener(new OnErrorListener() {
	       
	       @Override
	       public void onError(FacebookException error) {
	         Log.e("FacebookError", error.toString());
	       }
	    });
	    
	    Facebook_Button.setReadPermissions(Arrays.asList("email"));
	    Facebook_Button.setSessionStatusCallback(new Session.StatusCallback() {
	    	
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if(new Login_Utils(ctx).isOnline()){
					if(session.isOpened()){
						Request.newMeRequest(session, new Request.GraphUserCallback() {
							
							@Override
							public void onCompleted(GraphUser user, Response response) {
								if (user != null) {
									
									FId = user.getId();
									FName = user.getFirstName() + " " + user.getLastName();
									FMail = user.getProperty("email").toString();
									new AsyncTask_Facebook(ctx).execute(FMail, FName, FId);
																
								}
								
							}
						}).executeAsync(); 
					}
				}else{
					Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
				}	
			}         
	    }); 


	}
	
}