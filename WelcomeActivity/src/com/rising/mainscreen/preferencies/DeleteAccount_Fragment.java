package com.rising.mainscreen.preferencies;

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
import com.rising.login.Login_Utils;
import com.rising.login.SessionManager;
import com.rising.login.login.ProgressDialogFragment;

public class DeleteAccount_Fragment extends Activity implements AsyncTask_DeleteAccount.TaskCallbacks {
    private AsyncTask_DeleteAccount task;
	
	private EditText Clave;
    private Button BotonEliminarCuenta;
    
	private Context ctx;
	
	//Clases utilizadas
	private Login_Utils UTILS;
	private Preferencies_Errors ERRORS;
	private SessionManager SESSION;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.preferencies_deleteaccount);
        
        this.ctx = this;
        this.UTILS = new Login_Utils(ctx);
        this.ERRORS = new Preferencies_Errors(ctx);
        this.SESSION = new SessionManager(ctx);
        
		ActionBar ABar = getActionBar();
		
		ABar = getActionBar();
    	ABar.setDisplayHomeAsUpEnabled(true);
    	
    	Clave = (EditText) findViewById(R.id.claveEliminarCuenta);
    	BotonEliminarCuenta = (Button) findViewById(R.id.botonEliminarCuenta);
    	
    	if (SESSION.getFacebookId() > -1) {
			Clave.setVisibility(View.INVISIBLE);
			
			TextView texto = (TextView) findViewById(R.id.textoEliminarCuenta);
			texto.setText(R.string.esta_seguro_facebook);
		}
    	    	
		BotonEliminarCuenta.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				
				if(UTILS.isOnline()){	
										
					final Bundle bundle = new Bundle();
					
					if (Clave.getText().length() > 0) {
						UTILS.HideKeyboard();						
						
						bundle.putString("mail", SESSION.getMail());
						bundle.putString("pass", Clave.getText().toString());
						
						InicioFragment(bundle);
					}else{
						if (SESSION.getFacebookId() > -1) {
							bundle.putString("mail", SESSION.getMail());
							bundle.putString("pass", String.valueOf(SESSION.getFacebookId()));
							
							InicioFragment(bundle);
						}else{
							ERRORS.errDeleteAccount(0);
						}
					}
															
				}else{
					ERRORS.errDeleteAccount(4);	
				}
			}
			    	
		});
	}

	private void InicioFragment(Bundle bundle){
		FragmentManager fm = getFragmentManager();
		
        if(task == null){
            task = new AsyncTask_DeleteAccount();
            task.setArguments(bundle);
            fm.beginTransaction().add(task, "myTask").commit();
			Clave.setText("");
        }else{
        	ERRORS.errDeleteAccount(10);
        }
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

        ProgressDialogFragment dialog = ProgressDialogFragment.newInstance(getString(R.string.eliminando_cuenta));
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
        ERRORS.errDeleteAccount(result);
    }

	@Override
	public void onCancelled() {
    	task = null;
    	
    	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
     	
        if (dialog!=null) {
            dialog.dismiss();
        }
		
		ERRORS.errChangePass(1);		
	}

}