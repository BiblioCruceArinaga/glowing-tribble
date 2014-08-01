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

import com.rising.drawing.R;
import com.rising.login.Login_Utils;
import com.rising.login.SessionManager;
import com.rising.login.login.ProgressDialogFragment;

//Clase que maneja el envio de feedback
public class SendFeedback_Fragment extends Activity implements AsyncTask_SendFeedbackFragment.TaskCallbacks {
    private AsyncTask_SendFeedbackFragment task;
	
	private EditText mensaje;
    private Button FeedBoton;
    
	private Context ctx;
	
	//Clases utilizadas
	private Login_Utils UTILS;
	private Preferencies_Errors ERRORS;
	private SessionManager SESSION;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.preferencies_feedback);
        
        this.ctx = this;
        this.UTILS = new Login_Utils(ctx);
        this.ERRORS = new Preferencies_Errors(ctx);
        this.SESSION = new SessionManager(ctx);
        
		ActionBar ABar = getActionBar();
		
		ABar = getActionBar();
    	ABar.setDisplayHomeAsUpEnabled(true);
    	
    	mensaje = (EditText) findViewById(R.id.feedbackCajaTexto);
    	FeedBoton = (Button) findViewById(R.id.feedbackBoton);
    	
		FeedBoton.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				
				if(UTILS.isOnline()){	
						
					if (mensaje.getText().length() == 0) {
						ERRORS.errFeedback(0);
					} else {
								
						UTILS.HideKeyboard();
												
						final Bundle bundle = new Bundle();
						bundle.putString("mail", SESSION.getMail());
						bundle.putString("mensaje", mensaje.getText().toString());
										
				        FragmentManager fm = getFragmentManager();
			
				        if(task == null){
				        	task = new AsyncTask_SendFeedbackFragment();
				        	task.setArguments(bundle);
					        fm.beginTransaction().add(task, "myTask").commit();       
				        }else{
				        	ERRORS.errFeedback(10);
					    }
					}				
					
				}else{
					ERRORS.errFeedback(2);
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

        ProgressDialogFragment dialog = ProgressDialogFragment.newInstance(getString(R.string.sending_feedback));
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
        ERRORS.errFeedback(result);
    }

	@Override
	public void onCancelled() {
    	task = null;
    	
    	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
     	
        if (dialog!=null) {
            dialog.dismiss();
        }
		
		ERRORS.errFeedback(10);		
	}

}