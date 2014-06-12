package com.rising.money;

import android.content.Context;
import android.content.SharedPreferences;

public class EnableButtonsData {
	private final String SHARED_PREFS_FILE = "EnablePrefs";
	private final String KEY_ENABLE_TW = "Enable_tw";
	private final String KEY_TIME_TW = "Time_tw";
	private final String KEY_ENABLE_FB = "Enable_fb";
	private final String KEY_TIME_FB = "Time_fb";
	private final String KEY_ENABLE_RATE = "Enable_rate";
	
	private Context mContext;
	
	public EnableButtonsData(Context context){
		this.mContext = context;
	}
	
	private SharedPreferences getSettings(){
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}
	
	public boolean getEnable_FB(){
		return getSettings().getBoolean(KEY_ENABLE_FB, true);  
	}
	
	public long getTime_FB(){
		return getSettings().getLong(KEY_TIME_FB, -1);
	}
		
	public void setEnable_FB(boolean enable){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putBoolean(KEY_ENABLE_FB, enable);
	    editor.commit();
	}
	
	public void setTime_FB(long time){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putLong(KEY_TIME_FB, time);
	    editor.commit();
	}
	
	public boolean getEnable_TW(){
		return getSettings().getBoolean(KEY_ENABLE_TW, true);  
	}
	
	public long getTime_TW(){
		return getSettings().getLong(KEY_TIME_TW, -1);
	}
		
	public void setEnable_TW(boolean enable){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putBoolean(KEY_ENABLE_TW, enable);
	    editor.commit();
	}
	
	public void setTime_TW(long time){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putLong(KEY_TIME_TW, time);
	    editor.commit();
	}
	
	public boolean getEnable_Rate(){
		return getSettings().getBoolean(KEY_ENABLE_RATE, true);
	}
		
	public void setEnable_Rate(boolean enable){
	    SharedPreferences.Editor editor = getSettings().edit();
	    editor.putBoolean(KEY_ENABLE_RATE, enable);
	    editor.commit();
	}
	
}