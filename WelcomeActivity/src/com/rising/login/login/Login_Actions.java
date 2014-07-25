package com.rising.login.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;

//Clase que gestiona todo lo relacionado con el botón de Login
public class Login_Actions {

	private Context ctx;
	
	//Clases usadas
	private Login_Utils UTILS;
	private Login_Errors ERRORS;
	
	public Login_Actions(Context context){
		this.ctx = context;
		UTILS = new Login_Utils(ctx);
		ERRORS = new Login_Errors(ctx);
	}
			
	public void LoginButton_Actions(){
		if(UTILS.isOnline()){	
			Intent i = new Intent(ctx, Login_Fragment.class);
			ctx.startActivity(i);
			((Activity)ctx).finish();
		}else{
			ERRORS.errLogin(4);
		}	

	}
	
	// Este método valida que no haya ningun campo en blanco, devolviendo false si lo hay y true si no.
    public boolean checkLoginData(String username ,String password){
    	
	    if(username.equals("") || password.equals("")){
	    	return false;
	    }else{
	    	return true;
	    }
    }
	
}