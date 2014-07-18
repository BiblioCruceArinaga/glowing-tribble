package com.rising.login.registro;

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
import com.rising.login.registro.AsyncTask_Registro.OnTaskCompleted;

public class Registro_Actions {
	
	private Context ctx;
	private Button Confirm_Reg, Cancel_Reg;
	private Dialog RDialog, EDialog;
	private String pass = "";
	private String confipass = "";
	private String mail = "";
	private String name = "";	
	private String language = "";
	private EditText Nombre_Registro, Mail_Registro, Pass_Registro, ConfiPass_Registro;	
	
	//Clases usadas
	private Login_Utils UTILS;
	private AsyncTask_Registro ASYNCTASK;
	
	//  Recibir la señal del proceso que termina el registro
	private OnTaskCompleted listener = new OnTaskCompleted() {

	    public void onTaskCompleted(int details) {
			errRegistro(details);
	    }

		public void onTaskFailed(int details) {			
			errRegistro(details);
		}
	};
	
	public Registro_Actions(Context context){
		this.ctx = context;
		UTILS = new Login_Utils(ctx);
		this.ASYNCTASK = new AsyncTask_Registro(ctx, listener);
	}
	
	public void RegistroButton_Actions(){
		if(new Login_Utils(ctx).isOnline()){	
			RDialog = new Dialog(ctx, R.style.cust_dialog);
			
			RDialog.setContentView(R.layout.registro_dialog);
			RDialog.setTitle(R.string.registro_title);
			
			Nombre_Registro = (EditText)RDialog.findViewById(R.id.et_nombre_registro);
			Mail_Registro = (EditText)RDialog.findViewById(R.id.et_mail_registro);
			Pass_Registro = (EditText)RDialog.findViewById(R.id.et_pass_registro);
			ConfiPass_Registro = (EditText)RDialog.findViewById(R.id.et_confipass_registro);
			Confirm_Reg = (Button)RDialog.findViewById(R.id.b_confirm_reg);
			Cancel_Reg = (Button)RDialog.findViewById(R.id.b_cancel_reg);
							
			Confirm_Reg.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					RegistroConfirm_Actions();					
				}	
			});

			Cancel_Reg.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					RDialog.dismiss();
				}
			});
			
			RDialog.show();
		
		}else{
			Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
		}
	}
	
	public void RegistroConfirm_Actions(){
	
		name = Nombre_Registro.getText().toString();
		mail = Mail_Registro.getText().toString();
		pass = Pass_Registro.getText().toString();
		confipass = ConfiPass_Registro.getText().toString();
		
		if(name.equals("") && mail.toString().equals("") && pass.equals("") && confipass.equals("")) {			
			errRegistro(5);
        }else{
        	
			if(UTILS.checkPass(pass, confipass)){	            
				ASYNCTASK.execute(name, mail, pass, language); 
        	}else{
        		errRegistro(6);
        	}
        }
    	
		RDialog.dismiss();
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

}