package com.rising.login;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.inputmethod.InputMethodManager;

//Clase que contiene métodos útiles en varias clases del paquete Login
public class Login_Utils{

	private Context ctx;
	public String Language;

	public Login_Utils(Context context){
		this.ctx = context;
	}
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch(NullPointerException n) {
			return false;
		}
	}	
	
	public String getLanguage() {
		return Locale.getDefault().getDisplayLanguage();
	}
	
    public boolean checkPass(String pass, String confipass){
    	
    	if(confipass.equals(pass)){
    		return true;
    	}else{
    		return false;
    	}
    }
	
    public void HideKeyboard(){
    	InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE); 

    	inputManager.hideSoftInputFromWindow(((Activity) ctx).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}