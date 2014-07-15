package com.rising.login;

import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;

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
	
}