package com.rising.login;

import android.content.Context;
import android.net.ConnectivityManager;

//Clase que contiene métodos útiles en varias clases del paquete Login
public class Login_Utils{

	private Context ctx;
	
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
}
