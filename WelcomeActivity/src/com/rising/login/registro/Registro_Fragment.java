package com.rising.login.registro;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rising.drawing.R;
import com.rising.login.Login;
import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;
import com.rising.login.login.ProgressDialogFragment;
import com.rising.mainscreen.preferencies.Preferencies_Utils;

public class Registro_Fragment extends Activity implements AsyncTask_RegistroFragment.TaskCallbacks {
    private AsyncTask_RegistroFragment task;
	private Button Confirm_Registro;
	private EditText Mail, Pass, Nombre, ConfiPass;
	private TextView Terminos, Condiciones;
	private String RName, RMail, RPass, RConfipass, RLanguage;
	 
	private Context ctx;
	
	//Clases utilizadas
	private Login_Utils UTILS;
	private Login_Errors ERRORS;
	private Preferencies_Utils PUTILS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_registro_registrofragment);
        
        this.ctx = this;
        this.UTILS = new Login_Utils(ctx);
        this.ERRORS = new Login_Errors(ctx);
        this.PUTILS = new Preferencies_Utils(ctx);
        
		ActionBar ABar = getActionBar();
		
		ABar = getActionBar();
    	ABar.setDisplayHomeAsUpEnabled(true);
		
    	//Aviso
    	Terminos = (TextView) findViewById(R.id.tV_registro_term);
    	Condiciones = (TextView) findViewById(R.id.tV_registro_conds);
    	
    	Terminos.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				PUTILS.Legal_Displays(getString(R.string.terminos));
			}
    		
    	});
    	
    	Condiciones.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				PUTILS.Legal_Displays(getString(R.string.condiciones));
			}
    		
    	});
    	
    	
    	//Datos necesarios
	    Confirm_Registro = (Button)findViewById(R.id.b_confirm_reg);
		Mail = (EditText)findViewById(R.id.et_mail_registro);
		Pass = (EditText)findViewById(R.id.et_pass_registro);
		ConfiPass = (EditText) findViewById(R.id.et_confipass_registro);
		Nombre = (EditText) findViewById(R.id.et_nombre_registro);
		
		Confirm_Registro.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
								
				RName = Nombre.getText().toString();
				RMail = Mail.getText().toString();
				RPass = Pass.getText().toString();
				RConfipass = ConfiPass.getText().toString();
				RLanguage = Locale.getDefault().getDisplayLanguage();
				
				if(RName.equals("") && RMail.toString().equals("") && RPass.equals("") && RConfipass.equals("")) {			
					ERRORS.errRegistro(5);
		        }else{
		        	if(UTILS.isOnline()){
						if(UTILS.checkPass(RPass, RConfipass)){	            
							UTILS.HideKeyboard();
							
							final Bundle bundle = new Bundle();
							bundle.putString("mail", RMail);
							bundle.putString("pass", RPass);
							bundle.putString("name", RName);
							bundle.putString("language", RLanguage);
											
					        FragmentManager fm = getFragmentManager();
			
					        if(task == null){
					            task = new AsyncTask_RegistroFragment();
					            task.setArguments(bundle);
					            fm.beginTransaction().add(task, "myTask").commit();
					        }else{
					        	ERRORS.errLogin(6);
					        } 
			        	}else{
			        		ERRORS.errRegistro(6);
			        	}
		        	}else{
		        		ERRORS.errLogin(4);
		        	}
		        }
				
			}
					
		});
 
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        
	    	case android.R.id.home:
	    		Intent in = new Intent(this, Login.class);
	    		startActivity(in);
	    		finish();
	    	
	    	default:
	            return super.onOptionsItemSelected(item);
	    }
	}
        
    @Override
    public void onPreExecute() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("myDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ProgressDialogFragment dialog = ProgressDialogFragment.newInstance(getString(R.string.creating_account));
        dialog.setCancelable(false);
        dialog.show(ft, "myDialog");
    }

    @Override
    public void onPostExecute(int result) {
    	
    	task = null;
    	
    	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
     	
        if (dialog!=null) {
            dialog.dismiss();
        }
    	
    	ERRORS.errRegistro(result);
    }

	@Override
	public void onCancelled() {
		
		task = null;
    	
    	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
     	
        if (dialog!=null) {
            dialog.dismiss();
        }
        
		ERRORS.errLogin(6);		
	}
}