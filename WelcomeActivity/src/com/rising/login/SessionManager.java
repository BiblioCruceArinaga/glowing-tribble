package com.rising.login;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.Session;

//Clase que se encarga de la gestión de las sesiones
public class SessionManager {
		
	private SharedPreferences sPref;
	private static Editor editor;
	
	private Context ctx;
	private Session FSesion;
	
	private int PREF_PRIVATE_MODE = 0;
	private static final String PREF_NAME = "Scores";
	private static final String IS_LOGIN = "IsLogIn";
	public static final String KEY_NAME = "name";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_FID = "fid"; 
	
	public SessionManager(Context context){
		this.ctx = context;
		sPref = context.getSharedPreferences(PREF_NAME, PREF_PRIVATE_MODE);
		editor = sPref.edit();
		editor.commit();	
	}
	
	public void createLoginSession(String email, String name, String fid){
		editor = sPref.edit();
		
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		
		// Storing data in pref
		editor.putString(KEY_NAME, name);
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_FID, fid);
		
		if(!fid.equals("-1")){
			FSesion = Session.getActiveSession();
			
			if(FSesion == null){
				FSesion = new Session(ctx);
				Session.setActiveSession(FSesion);
			}
		}
		
		editor.commit();
	}	
	
	/**
	 * Check login method will check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */ 
	public void checkLogin(){
		
		// Check login status
		if(!this.isLoggedIn()){
			
			Intent i = new Intent(ctx, Login.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			ctx.startActivity(i);
		}
	}
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		
		user.put(KEY_NAME, sPref.getString(KEY_NAME, null));
		user.put(KEY_EMAIL, sPref.getString(KEY_EMAIL, null));
		
		return user;
	}
		
	public void LogOutFacebook(){
				
		if (Session.getActiveSession() != null){	
	        Session.getActiveSession().closeAndClearTokenInformation();	
		}else{
			 Session.openActiveSessionFromCache(ctx);
			if(Session.getActiveSession() != null){
				Session.getActiveSession().closeAndClearTokenInformation();
			}	
		}
	    
		editor.clear();
	    editor.commit();
	        
		Intent i = new Intent(ctx, Login.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
		ctx.startActivity(i);
	} 
	
	
	public void LogOutUser(){
		
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		Intent i = new Intent(ctx, Login.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		ctx.startActivity(i);
	}
	
	/**
	 * Quick check for login
	 * **/
	public boolean isLoggedIn(){
		return sPref.getBoolean(IS_LOGIN, false);
	}

	/**
	 * @return Devuelve el nombre de este usuario
	 */
	public String getName() {
		return sPref.getString(KEY_NAME, "");
	}
	
	/**
	 * @return Devuelve el email de este usuario
	 */
	public String getMail() {
		return sPref.getString(KEY_EMAIL, "");
	}
	
	/**
	 * @return Devuelve el id de Facebook de este usuario
	 */
	public int getFacebookId() {
		return Integer.parseInt(sPref.getString(KEY_FID, "-1"));
	}
}