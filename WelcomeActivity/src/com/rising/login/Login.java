package com.rising.login;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.rising.drawing.R;
import com.rising.login.facebook.Facebook_Fragment;
import com.rising.login.login.Login_Fragment;
import com.rising.login.olvidapass.OlvidoPass_Fragment;
import com.rising.login.registro.Registro_Fragment;

//Clase login. Permite al usuario loguearse y registrarse, con la aplicación o con Facebook, y cambiar la contraseña
public class Login extends FragmentActivity {
	
	//Necesario para el botón de Facebook
	public static String FId;
	public static String FName;
	public static String FMail;
		
	//Elementos usados
	private LoginButton Facebook_Button;
	private Button Login, Registro;
	private TextView OlvidaPass;
		
	private Context ctx;
	private Login_Utils UTILS;
	private Login_Errors ERRORS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
		
        this.ctx = this;
        this.UTILS = new Login_Utils(ctx);
        this.ERRORS = new Login_Errors(ctx);
        
		Login = (Button) findViewById(R.id.button_login);
		Facebook_Button = (LoginButton) findViewById(R.id.button_login_f);
		OlvidaPass = (TextView)findViewById(R.id.tv_olvido_pass);
		Registro = (Button) findViewById(R.id.b_registro);
		
		if(Session.getActiveSession() != null){
			Session.getActiveSession().closeAndClearTokenInformation();
		}
		
		Login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				UTILS.Open_Fragment(Login_Fragment.class);
			}  
		});
		
		OlvidaPass.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				UTILS.Open_Fragment(OlvidoPass_Fragment.class);
			}
		});
	 
		Registro.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				UTILS.Open_Fragment(Registro_Fragment.class);	
			}
		});
			
		Facebook_Button.setReadPermissions(Arrays.asList("email"));
		
		Facebook_Button.setSessionStatusCallback(new Session.StatusCallback() {
		    			
			@Override
			public void call(Session session, SessionState state, Exception exception) {
								
				if(UTILS.isOnline()){
																				
					if(session.isOpened()){
												
						Request.newMeRequest(session, new Request.GraphUserCallback() {
							
							@Override
							public void onCompleted(GraphUser user, Response response) {
								if (user != null) {
																				
										FId = user.getId();
										FName = user.getFirstName() + " " + user.getLastName();
										FMail = user.getProperty("email").toString();
										
										final Bundle bundle = new Bundle();
										bundle.putString("fmail", FMail);
										bundle.putString("fname", FName);
										bundle.putString("fid", FId);
									
									if(UTILS.isOnline()){	
										Intent i = new Intent(ctx, Facebook_Fragment.class);
										i.putExtras(bundle);
										startActivity(i);
										finish();
									}else{
										
										if(Session.getActiveSession() != null){
											Session.getActiveSession().closeAndClearTokenInformation();
										}
										ERRORS.errLogin(4);
									}
								}	
							}
						}).executeAsync();
					}
					
				}else{
					Facebook_Button.setOnErrorListener(new OnErrorListener() {
					       
						@Override
					    public void onError(FacebookException error) {
							if(Session.getActiveSession() != null){
								Session.getActiveSession().closeAndClearTokenInformation();
							}
							ERRORS.errLogin(4);
					    }
					});
				}	
			}         
		
		}); 
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

}