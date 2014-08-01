package com.rising.mainscreen.preferencies;

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

import com.rising.drawing.R;
import com.rising.login.Login_Utils;
import com.rising.login.SessionManager;
import com.rising.login.login.ProgressDialogFragment;

//Clase que se encarga del manejo del cambio de contraseñas
public class ChangePassword_Fragment extends Activity implements AsyncTask_ChangePassword.TaskCallbacks {
    private AsyncTask_ChangePassword task;
	
	private EditText claveVieja;
    private EditText claveNueva;
    private EditText claveRepetir; 
    private Button misDatosBoton;
    
	private Context ctx;
	
	//Clases utilizadas
	private Login_Utils UTILS;
	private Preferencies_Errors ERRORS;
	private SessionManager SESSION;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.preferencies_changepass);
        
        this.ctx = this;
        this.UTILS = new Login_Utils(ctx);
        this.ERRORS = new Preferencies_Errors(ctx);
        this.SESSION = new SessionManager(ctx);
        
		ActionBar ABar = getActionBar();
		
		ABar = getActionBar();
    	ABar.setDisplayHomeAsUpEnabled(true);
    	
    	claveVieja = (EditText) findViewById(R.id.misDatosClaveVieja);
    	claveNueva = (EditText) findViewById(R.id.misDatosClaveNueva);
    	claveRepetir = (EditText) findViewById(R.id.misDatosClaveRepetir);
    	misDatosBoton = (Button) findViewById(R.id.misDatosBoton);
    	
		misDatosBoton.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				
				if(UTILS.isOnline()){	
					
				//  Usuario normal
					if (SESSION.getFacebookId() == -1) {
						
						if (( claveVieja.getText().length() == 0 ) || ( claveNueva.getText().length() == 0 ) ||	
								( claveRepetir.getText().length() == 0 )) {
							ERRORS.errChangePass(5);
						} else {
								if (!claveNueva.getText().toString().equals(claveRepetir.getText().toString())) {
									ERRORS.errChangePass(6);
								}else{
									
								UTILS.HideKeyboard();
								
								final Bundle bundle = new Bundle();
								bundle.putString("mail", SESSION.getMail());
								bundle.putString("oldpass", claveVieja.getText().toString());
								bundle.putString("newpass", claveNueva.getText().toString());
								bundle.putString("language", Locale.getDefault().getDisplayLanguage());
												
						        FragmentManager fm = getFragmentManager();
				
						        if(task == null){
						            task = new AsyncTask_ChangePassword();
						            task.setArguments(bundle);
						            fm.beginTransaction().add(task, "myTask").commit();
					        			
									claveVieja.setText("");
									claveNueva.setText("");
									claveRepetir.setText("");          
						        }else{
						        	ERRORS.errChangePass(10);
						        }
							}	
						}				
					}
				}else{
					ERRORS.errChangePass(7);
				}
			}
					
		});

	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        
	    	case android.R.id.home:
	    		Intent in = new Intent(this, PreferenciesActivity.class);
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

        ProgressDialogFragment dialog = ProgressDialogFragment.newInstance(getString(R.string.try_change_pass));
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
        ERRORS.errChangePass(result);
    }

	@Override
	public void onCancelled() {
    	task = null;
    	
    	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
     	
        if (dialog!=null) {
            dialog.dismiss();
        }
		
		ERRORS.errChangePass(10);		
	}

}