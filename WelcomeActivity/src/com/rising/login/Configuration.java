package com.rising.login;

import android.content.Context;
import android.content.SharedPreferences;

public class Configuration {

	private final String SHARED_PREFS_FILE = "RSPrefs";
	private final String KEY_EMAIL = "Mail";
	private final String KEY_ID = "Id";
	private final String KEY_NAME = "Name";
	private final String KEY_MONEY = "Money";

	private Context mContext;
	
	public Configuration(Context context){
		this.mContext = context;
	}
	
	private SharedPreferences getSettings(){
	 return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}
	
	public String getUserEmail(){
	 return getSettings().getString(KEY_EMAIL, null);  
	}
	
	public String getUserId(){
		return getSettings().getString(KEY_ID, null);
	}
	
	public String getUserName(){
		return getSettings().getString(KEY_NAME, null);  
	}
		
	public float getUserMoney(){		
		return Math.round(getSettings().getFloat(KEY_MONEY, 0));	
	}
	
	public void setUserEmail(String email){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putString(KEY_EMAIL, email );
	    editor.commit();
	}
	
	public void setUserId(String id){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putString(KEY_ID, id );
	    editor.commit();
	}
	
	public void setUserName(String name){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putString(KEY_NAME, name );
	    editor.commit();
	}
	
	public void setUserMoney(double money){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putFloat(KEY_MONEY, (float)money);
	    editor.commit();
	}
	
}
