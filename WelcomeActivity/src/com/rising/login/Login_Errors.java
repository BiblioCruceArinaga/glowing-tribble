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

/**Clase que muestra los errores e informaciones 
 * en Dialog de todos los botones y Dialogs de la pantalla de Login
* 
* @author Ayo
* @version 2.0
* 
*/
public class Login_Errors {

	private final transient Context ctx;
	private transient Dialog eDialog;
	
	public Login_Errors(final Context context){
		this.ctx = context;
	}
	
    public void errLogin(final int code){
    	    	
    	eDialog = new Dialog(ctx, R.style.cust_dialog);
    	eDialog.getWindow();
        eDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		eDialog.setContentView(R.layout.error_errordialog);
		eDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		final TextView tv_E = (TextView)eDialog.findViewById(R.id.error_tV);
		
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
		
		final Button  Login_Error_CloseButton = (Button)eDialog.findViewById(R.id.error_button);
		
		Login_Error_CloseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				eDialog.dismiss();				
			}
		});
    	
		eDialog.show();	
    }

    public void errFacebook(final int code){
    	
    	if(Session.getActiveSession() != null){
			Session.getActiveSession().closeAndClearTokenInformation();
		}
    	
    	final Intent intent = new Intent(ctx, Login.class);
    	ctx.startActivity(intent);
    	((Activity)ctx).finish();  	

    	errLogin(code); 
    	
    } 
    
    public void errRegistro(final int code){

		eDialog = new Dialog(ctx, R.style.cust_dialog);
    	eDialog.getWindow();
        eDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		eDialog.setContentView(R.layout.error_errordialog);
		eDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		final TextView tv_E = (TextView)eDialog.findViewById(R.id.error_tV);
		
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
		
		final Button  Login_Error_CloseButton = (Button)eDialog.findViewById(R.id.error_button);
		
		Login_Error_CloseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {	
				if(code == 1 || code == 3){
					final Intent intent = new Intent(ctx, Login.class);
					ctx.startActivity(intent);
					((Activity)ctx).finish();
				}else{
					eDialog.dismiss();
				}
			}
		});
    	
		eDialog.show();	
	}

    public void errOlvidaPass(final int code){
    	
    	eDialog = new Dialog(ctx, R.style.cust_dialog);
    	eDialog.getWindow();
        eDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		eDialog.setContentView(R.layout.error_errordialog);
		eDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		final TextView tv_E = (TextView)eDialog.findViewById(R.id.error_tV);
		
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
		
		final Button OlvidaPass_Error_CloseButton = (Button)eDialog.findViewById(R.id.error_button);
		
		OlvidaPass_Error_CloseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				eDialog.dismiss();				
			}
		});
    	
		eDialog.show();	
    }

    public void errSession(int code){
    	eDialog = new Dialog(ctx, R.style.cust_dialog);
    	eDialog.getWindow();
        eDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		eDialog.setContentView(R.layout.error_errordialog);
		eDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		final TextView tv_E = (TextView)eDialog.findViewById(R.id.error_tV);
		
		switch (code) {
	        case 0:
	        	tv_E.setText(R.string.err_close_session);
	        	break;
	        case 1:
	        	tv_E.setText(R.string.connection_err);
	        	break;
	    	default:
	    		tv_E.setText(R.string.err_olvidopass_unknown);
	    }
		
		final Button OlvidaPassError_CloseButton = (Button)eDialog.findViewById(R.id.error_button);
		
		OlvidaPassError_CloseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				eDialog.dismiss();				
			}
		});
    	
		eDialog.show();	
    }
}