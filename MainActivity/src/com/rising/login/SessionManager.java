package com.rising.login;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.Session;
import com.facebook.internal.SessionTracker;
import com.facebook.widget.LoginButton;

public class SessionManager {
	
	// Shared Preferences
	SharedPreferences sPref;
	LoginButton FLButton;
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "Scores";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLogIn";
	
	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";
	
	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";
	
	//  Id de Facebook (valdrá -1 si el usuario no es de Facebook)
	public static final String KEY_FID = "fid"; 
	
	// Constructor
	public SessionManager(Context context){
		this.context = context;
		sPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		//FLButton = new LoginButton(context, null);
		editor = sPref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String email, String name, String fid){
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		
		// Storing name in pref
		editor.putString(KEY_NAME, name);
		
		// Storing email in pref
		editor.putString(KEY_EMAIL, email);
		
		// Storing Facebook Id in pref
		editor.putString(KEY_FID, fid);
		
		// commit changes
		editor.commit();
	}	
	
	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */
	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(context, Login.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			context.startActivity(i);
		}
		
	}
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, sPref.getString(KEY_NAME, null));
		
		// user email id
		user.put(KEY_EMAIL, sPref.getString(KEY_EMAIL, null));
		
		// return user
		return user;
	}
	
	/**
	 * 
	 * @return Devuelve el nombre de este usuario
	 */
	public String getName() {
		return sPref.getString(KEY_NAME, "");
	}
	
	/**
	 * 
	 * @return Devuelve el email de este usuario
	 */
	public String getMail() {
		return sPref.getString(KEY_EMAIL, "");
	}
	
	/**
	 * 
	 * @return Devuelve el id de Facebook de este usuario
	 */
	public int getFacebookId() {
		return Integer.parseInt(sPref.getString(KEY_FID, "-1"));
	}
	
	public void LogOutFacebook(){
		SessionTracker st = new SessionTracker(context, null);
		Session openSession = st.getOpenSession();
		if (openSession != null) openSession.closeAndClearTokenInformation();
	};
	
	/**
	 * Clear session details
	 * */
	public void logoutUser(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		LogOutFacebook();
		
		// After logout redirect user to Login Activity
		Intent i = new Intent(context, Login.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		context.startActivity(i);
	}
	
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return sPref.getBoolean(IS_LOGIN, false);
	}

}
