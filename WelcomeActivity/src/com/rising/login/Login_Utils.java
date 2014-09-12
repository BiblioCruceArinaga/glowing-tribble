package com.rising.login;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.inputmethod.InputMethodManager;

/**Clase que contiene métodos útiles en varias clases del paquete Login
* 
* @author Ayo
* @version 2.0
* 
*/
public class Login_Utils{

	private final transient Context ctx;
	public String language;

	public Login_Utils(final Context context){
		this.ctx = context;
	}
	
	public boolean isOnline() {
		final ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch(NullPointerException n) {
			return false;
		}
	}	
	
	public String getLanguage() {
		return Locale.getDefault().getDisplayLanguage();
	}
	
    public boolean checkPass(final String pass, final String confipass){
    	
    	if(confipass.equals(pass)){
    		return true;
    	}else{
    		return false;
    	}
    }
	
    public void hideKeyboard(){
    	final InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE); 

    	inputManager.hideSoftInputFromWindow(((Activity) ctx).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    public void openFragment(@SuppressWarnings("rawtypes") Class clase){
		if(isOnline()){	
			final Intent intent = new Intent(ctx, clase);
			ctx.startActivity(intent);
			((Activity)ctx).finish();
		}else{
			new Login_Errors(ctx).errLogin(4);
		}	
    }
    
 // Este método valida que no haya ningun campo en blanco, 
    //devolviendo false si lo hay y true si no.
    public boolean checkLoginData(final String username, final String password){
    	
	    if(username.equals("") || password.equals("")){
	    	return false;
	    }else{
	    	return true;
	    }
    }
}