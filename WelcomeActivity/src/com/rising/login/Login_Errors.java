package com.rising.login;

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

import com.facebook.Session;
import com.rising.drawing.R;

//Clase que muestra los errores e informaciones en Dialog de todos los botones y Dialogs de la pantalla de Login
public class Login_Errors {

	private Context ctx;
	private Dialog EDialog;
	
	public Login_Errors(Context context){
		this.ctx = context;
	}
	
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
			case 5: 
				tv_E.setText(R.string.err_session);
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

    public void errFacebook(int code){
    	
    	if(Session.getActiveSession() != null){
			Session.getActiveSession().closeAndClearTokenInformation();
		}
    	
    	Intent i = new Intent(ctx, Login.class);
    	ctx.startActivity(i);
    	((Activity)ctx).finish();  	

    	errLogin(code); 
    	
    } 
    
    public void errRegistro(int code){

		EDialog = new Dialog(ctx, R.style.cust_dialog);
    	EDialog.getWindow();
        EDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		EDialog.setContentView(R.layout.login_error_dialog);
		EDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		TextView tv_E = (TextView)EDialog.findViewById(R.id.error_tV);
		
		switch (code) {
			case 0:
				tv_E.setText(R.string.err_reg);
				break;
			case 1:
				tv_E.setText(R.string.ok_reg);
				break;
			case 2:
				tv_E.setText(R.string.err_reg_mail);
				break;
			case 3:
				tv_E.setText(R.string.ok_reg_mail);
				break;
			case 4:
				tv_E.setText(R.string.err_net);
				break;
			case 5: 
				tv_E.setText(R.string.err_campos_vacios);
				break;
			case 6:
				tv_E.setText(R.string.err_pass);
				break;
			default:
				tv_E.setText(R.string.err_reg);
				break;
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

    public void errOlvidaPass(int code){
    	
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
	        case 1:
	        	tv_E.setText(R.string.olvidopass_ok);
	        	break;
	        case 2:
	        	tv_E.setText(R.string.err_olvidopass_mail);
	        	break;
	        case 3:
	        	tv_E.setText(R.string.err_not_active);
	        	break;
	        case 4:
	        	tv_E.setText(R.string.err_olvidopass_mail_not_sent);
	        	break;
	    	default:
	    		tv_E.setText(R.string.err_olvidopass_unknown);
	    }
		
		Button OlvidaPass_Error_Close_Button = (Button)EDialog.findViewById(R.id.error_button);
		
		OlvidaPass_Error_Close_Button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				EDialog.dismiss();				
			}
		});
    	
		EDialog.show();	
    }

}