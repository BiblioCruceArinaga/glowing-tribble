package com.rising.login.olvidapass;

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
import com.rising.login.Login_Utils;

public class OlvidaPass_Actions {
	
	private EditText Mail_OlvidoPass;
	private Button Confirm_OlvidoPass, Cancel_OlvidoPass;
	private Dialog LPDialog, EDialog;
	private Context ctx;
	private String Language;
	
	//Clases usadas
	private AsyncTask_OlvidarPass ASYNCTASK;
	
	public OlvidaPass_Actions(Context context){
		this.ctx = context;
		this.Language = new Login_Utils(ctx).getLanguage();
		ASYNCTASK = new AsyncTask_OlvidarPass(ctx);
	}
	
	public void OlvidaPassButton_Actions(){
		if(new Login_Utils(ctx).isOnline()){
			LPDialog = new Dialog(ctx, R.style.cust_dialog);
			LPDialog.setContentView(R.layout.olvidopass_dialog);
			LPDialog.setTitle(R.string.olvido_title);
										
			Confirm_OlvidoPass = (Button)LPDialog.findViewById(R.id.b_confirm_olpass);
			Cancel_OlvidoPass = (Button)LPDialog.findViewById(R.id.b_cancel_olpass);
			Mail_OlvidoPass = (EditText)LPDialog.findViewById(R.id.et_mail_olvidopass);
							
			Confirm_OlvidoPass.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					OlvidoPassConfirm_Actions();
				}
				
			});
					
			Cancel_OlvidoPass.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					LPDialog.dismiss();
				}
				
			});	
			
			LPDialog.show();
		}else{
			Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
		}
	}	
	
	private void OlvidoPassConfirm_Actions(){
		String mail = Mail_OlvidoPass.getText().toString();
		if (mail.equals("")) {
			Toast.makeText(ctx, R.string.err_campos_vacios, Toast.LENGTH_SHORT).show();
		}
		else {
			ASYNCTASK.execute(mail, Language); 
			LPDialog.dismiss();
		}
	}
	
	//  Mostrar errores
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