package com.rising.login;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.R;

//Clase que gestiona todo lo relacionado con el botón de Login
public class Login_Actions {

	private Dialog LDialog, EDialog;
	private Button Confirm_Login, Cancel_Login;
	private EditText Mail, Pass;
	private Context ctx;
	
	//Clases usadas
	private Login_Utils UTILS;
	private AsyncTask_Login ASYNCTASK;
	
	public Login_Actions(Context context){
		this.ctx = context;
		UTILS = new Login_Utils(ctx);
		ASYNCTASK = new AsyncTask_Login(ctx);
	}
			
	public void LoginButton_Actions(){
		if(UTILS.isOnline()){	
			LDialog = new Dialog(ctx, R.style.cust_dialog);
			
			LDialog.setContentView(R.layout.login_dialog);
			LDialog.setTitle(R.string.login_title);
			
			Confirm_Login = (Button)LDialog.findViewById(R.id.b_confirm_login);
			Cancel_Login = (Button)LDialog.findViewById(R.id.b_cancel_login);
			Mail = (EditText)LDialog.findViewById(R.id.et_mail);
			Pass = (EditText)LDialog.findViewById(R.id.et_pass);
			
			Confirm_Login.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
		        	LoginConfirm_Actions();
				}
				
			});
			
			Cancel_Login.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					LDialog.dismiss();					
				}
				
			});
			
			LDialog.show();
		}else{
			Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
		}	

	}

	private void LoginConfirm_Actions(){
		    	    	    					        	
		if (checkLoginData(Mail.getText().toString(), Pass.getText().toString())==true) {
			
			ASYNCTASK.execute(Mail.getText().toString(),Pass.getText().toString());    

			Pass.setText("");
		}else{
			errLogin(0);
		} 
		
		LDialog.dismiss();
	}
	
	// Este método valida que no haya ningun campo en blanco, devolviendo false si lo hay y true si no.
    private boolean checkLoginData(String username ,String password){
    	
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