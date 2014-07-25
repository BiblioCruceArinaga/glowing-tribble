package com.rising.login.facebook;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.Session;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.Login_Errors;
import com.rising.login.SessionManager;
import com.rising.login.login.ProgressDialogFragment;
import com.rising.login.login.UserDataNetworkConnection;
import com.rising.login.login.UserDataNetworkConnection.OnLoginCompleted;
import com.rising.login.login.UserDataNetworkConnection.OnNetworkDown;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.store.DatosUsuario;

public class Facebook_Fragment extends Activity implements AsyncTask_FacebookFragment.TaskCallbacks {
    private AsyncTask_FacebookFragment task;
	private String FMail, FName, FId;
	 
	private Context ctx;
	private static ArrayList<DatosUsuario> userData;
	
	//Clases utilizadas
	public Configuration conf;
	private static UserDataNetworkConnection dunc;
	private SessionManager session;
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
								
    		session.createLoginSession(FMail, FName, FId);
           		    			    	
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
			
	        ERRORS.errFacebook(5);		
		}
		
	};
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.facebook_fragment);
        
        this.ctx = this;
        this.ERRORS = new Login_Errors(ctx);
        this.conf = new Configuration(this);
		this.session = new SessionManager(getApplicationContext());

		Bundle bundlein = getIntent().getExtras();
		FMail = bundlein.getString("fmail");
		FName = bundlein.getString("fname");
		FId = bundlein.getString("fid");
		
		final Bundle bundleout = new Bundle();
		bundleout.putString("fmail", FMail);
		bundleout.putString("fname", FName);
		bundleout.putString("fid", FId);
						
	    FragmentManager fm = getFragmentManager();
	    if(task == null){
	    	task = new AsyncTask_FacebookFragment();
	        task.setArguments(bundleout);
	        fm.beginTransaction().add(task, "myTask").commit();
	    }else{
	      	ERRORS.errLogin(6);
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
    	         
    	dunc = new UserDataNetworkConnection(listenerUser, NetworkDown);
    	
    	if(result == 1 || result == 3) {
    		dunc.execute(FMail);      		
        }else{
         	                	
         	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
 	     	
 	        if (dialog!=null) {
 	            dialog.dismiss();
 	        }
 	         	        
 	        ERRORS.errFacebook(result);
        }    	
    }

	@Override
	public void onCancelled() {

		if(Session.getActiveSession() != null){
			Session.getActiveSession().closeAndClearTokenInformation();
		}
		
		ERRORS.errFacebook(6);		
	}

}