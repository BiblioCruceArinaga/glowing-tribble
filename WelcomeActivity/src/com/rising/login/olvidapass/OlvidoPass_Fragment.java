package com.rising.login.olvidapass;

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
import com.rising.login.Login;
import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;
import com.rising.login.login.ProgressDialogFragment;

public class OlvidoPass_Fragment extends Activity implements AsyncTask_OlvidoPassFragment.TaskCallbacks {
    private AsyncTask_OlvidoPassFragment task;
	private Button Confirm_OlvidoPass;
	private EditText Mail_OlvidoPass;
	 
	private Context ctx;
	
	//Clases utilizadas
	private Login_Utils UTILS;
	private Login_Errors ERRORS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_olvidopass_olvidopassfragment);
        
        this.ctx = this;
        this.UTILS = new Login_Utils(ctx);
        this.ERRORS = new Login_Errors(ctx);
        
		ActionBar ABar = getActionBar();
		
		ABar = getActionBar();
    	ABar.setDisplayHomeAsUpEnabled(true);
		
	    Confirm_OlvidoPass = (Button)findViewById(R.id.b_confirm_olpass);
		Mail_OlvidoPass = (EditText)findViewById(R.id.et_mail_olvidopass);
		
		Confirm_OlvidoPass.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				
				String mail = Mail_OlvidoPass.getText().toString();
				String Language = Locale.getDefault().getISO3Language();
				
				if (mail.equals("")) {
					ERRORS.errLogin(0);
				} else {
					UTILS.hideKeyboard();
					
					final Bundle bundle = new Bundle();
					bundle.putString("mail", Mail_OlvidoPass.getText().toString());
					bundle.putString("language", Language);
									
			        FragmentManager fm = getFragmentManager();
	
			        if(task == null){
			            task = new AsyncTask_OlvidoPassFragment();
			            task.setArguments(bundle);
			            fm.beginTransaction().add(task, "myTask").commit();
			        }else{
			        	ERRORS.errLogin(6);
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

        ProgressDialogFragment dialog = ProgressDialogFragment.newInstance(getString(R.string.auth));
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
    	
    	ERRORS.errOlvidaPass(result);  	
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