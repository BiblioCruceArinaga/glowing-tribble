package com.rising.login.login;

import java.util.ArrayList;

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
import com.rising.login.Configuration;
import com.rising.login.Login;
import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;
import com.rising.login.SessionManager;
import com.rising.login.login.UserDataNetworkConnection.OnLoginCompleted;
import com.rising.login.login.UserDataNetworkConnection.OnNetworkDown;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.store.DatosUsuario;

public class Login_Fragment extends Activity implements AsyncTask_LoginFragment.TaskCallbacks {
    private AsyncTask_LoginFragment task;
	private Button Confirm_Login;
	private EditText Mail, Pass;
	 
	private Context ctx;
	private static ArrayList<DatosUsuario> userData;
	
	//Clases utilizadas
	public Configuration conf;
	private static UserDataNetworkConnection dunc;
	private SessionManager session;
	private Login_Utils UTILS;
	private Login_Errors ERRORS;

	//Recibe la señal del proceso que termina el Login e introduce los datos del usuario en Configuration. 
	private OnLoginCompleted listenerUser = new OnLoginCompleted(){
		public void onLoginCompleted(){
					
			userData = new ArrayList<DatosUsuario>();
				
			userData = dunc.devolverDatos();
				
			conf.setUserId(userData.get(0).getId());	           
			conf.setUserName(userData.get(0).getName());
			conf.setUserEmail(userData.get(0).getMail());
			conf.setUserMoney(userData.get(0).getMoney());
									
			session.createLoginSession(conf.getUserEmail(), conf.getUserName(), "-1");
		    			    	
		    ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
		        		     	
		    if(dialog!=null) {
		    	dialog.dismiss();
		    }

		    Intent i = new Intent(ctx, MainScreenActivity.class);
		    ctx.startActivity(i); 
		    finish();
		}
	};
	
	private OnNetworkDown NetworkDown = new OnNetworkDown(){

		@Override
		public void onNetworkDown() {
			
			task = null;
        	
        	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
	     	
	        if (dialog!=null) {
	            dialog.dismiss();
	        }
			
	        ERRORS.errLogin(5);		
		}
		
	};
   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_login_loginfragment);
        
        this.ctx = this;
        this.UTILS = new Login_Utils(ctx);
        this.ERRORS = new Login_Errors(ctx);
        this.conf = new Configuration(this);
		this.session = new SessionManager(getApplicationContext());
        
		ActionBar ABar = getActionBar();
		
		ABar = getActionBar();
    	ABar.setDisplayHomeAsUpEnabled(true);
		
	    Confirm_Login = (Button)findViewById(R.id.b_confirm_login);
		Mail = (EditText)findViewById(R.id.et_mail);
		Pass = (EditText)findViewById(R.id.et_pass);
		
		Confirm_Login.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				
				if(UTILS.checkLoginData(Mail.getText().toString(), Pass.getText().toString())==true) {	
				
					UTILS.HideKeyboard();
					
					final Bundle bundle = new Bundle();
					bundle.putString("mail", Mail.getText().toString());
					bundle.putString("pass", Pass.getText().toString());
									
			        FragmentManager fm = getFragmentManager();
	
			        if(task == null){
			            task = new AsyncTask_LoginFragment();
			            task.setArguments(bundle);
			            fm.beginTransaction().add(task, "myTask").commit();
			        }else{
			        	ERRORS.errLogin(6);
			        }
				}else{
					ERRORS.errLogin(0);
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
    	         
        if(result == 1) {
        	dunc = new UserDataNetworkConnection(listenerUser, NetworkDown);
        	
        	dunc.execute(Mail.getText().toString());            		
        }else{
        	   
        	task = null;
        	
        	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
	     	
	        if (dialog!=null) {
	            dialog.dismiss();
	        }
	       
	        ERRORS.errLogin(result);
        }   	
    }

	@Override
	public void onCancelled() {
		ERRORS.errLogin(6);		
	}

}