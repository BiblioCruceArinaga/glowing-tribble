package com.rising.login.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.rising.drawing.R;
import com.rising.login.Login_Utils;

//Clase que gestiona todo lo relacionado con el botón de Login
public class Login_Actions {

	private Dialog EDialog;
	private Context ctx;
	
	//Clases usadas
	private Login_Utils UTILS;
	
	public Login_Actions(Context context){
		this.ctx = context;
		UTILS = new Login_Utils(ctx);
	}
			
	public void LoginButton_Actions(){
		if(UTILS.isOnline()){	
			Intent i = new Intent(ctx, Login_Fragment.class);
			ctx.startActivity(i);
			((Activity)ctx).finish();
		}else{
			errLogin(4);
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
	
//  Mostrar errores
    public void errLogin(int code){
    	    	
    	EDialog = new Dialog(ctx, R.style.cust_dialog);
    	EDialog.getWindow();
        EDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		EDialog.setContentView(R.layout.login_error_dialog);
		EDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		TextView tv_E = (TextView)EDialog.findViewById(R.id.error_tV);
		
		switch (code) {
			case 0:
				tv_E.setText(R.string.err_campos_vacios);
				break;
			case 2:
				tv_E.setText(R.string.err_login_unknown_user);
				break;
			case 3:
				tv_E.setText(R.string.err_not_active);
				break;
			case 4: 
				tv_E.setText(R.string.connection_err);
				break;
			default:
				tv_E.setText(R.string.err_login_unknown);
		}
		
		Button  Login_Error_Close_Button = (Button)EDialog.findViewById(R.id.error_button);
		
		Login_Error_Close_Button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				EDialog.dismiss();				
			}
		});
    	
		EDialog.show();	
    }
}