package com.rising.login;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.rising.drawing.R;
import com.rising.login.login.Login_Actions;
import com.rising.login.login.UserDataNetworkConnection;
import com.rising.login.olvidapass.OlvidaPass_Actions;
import com.rising.login.registro.Registro_Actions;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.store.DatosUsuario;

//Clase login. Permite al usuario loguearse y registrarse, con la aplicación o con Facebook, y cambiar la contraseña
public class Login extends FragmentActivity {
	
	SessionManager session; //Habría que separarlo también
	LoginButton Facebook_Button;
	public static String FId;
	public static String FName;
	public static String FMail;

	private Button Login, Registro;
	private TextView OlvidaPass;
		
	private Context ctx;
	public static UserDataNetworkConnection dunc;
	public static ArrayList<DatosUsuario> userData;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
				
		session = new SessionManager(getApplicationContext());
		ctx = this;
				
		if (session.isLoggedIn()) {
			Intent i = new Intent(Login.this, MainScreenActivity.class);
			startActivity(i);
			finish();
		}
	
		Login = (Button) findViewById(R.id.button_login);
		Facebook_Button = (LoginButton) findViewById(R.id.button_login_f);
		OlvidaPass = (TextView)findViewById(R.id.tv_olvido_pass);
		Registro = (Button) findViewById(R.id.b_registro);
		
		
		//  Acciones al presionar el botón de nombre Login
		Login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new Login_Actions(ctx).LoginButton_Actions();
			}  
		});
		
		//  Acciones al presionar sobre la frase de nombre OlvidaPass
		OlvidaPass.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				new OlvidaPass_Actions(ctx).OlvidaPassButton_Actions();
			}
		});
	 
		//  Acciones al presionar sobre la palabra de nombre Registro
		Registro.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new Registro_Actions(ctx).RegistroButton_Actions();
			}
		});
		
	
		//Acciones al presionar sobre el botón de Facebook
		Facebook_Button.setOnErrorListener(new OnErrorListener() {
	       
	       @Override
	       public void onError(FacebookException error) {
	         Log.e("FacebookError", error.toString());
	       }
	    });
	    
	    Facebook_Button.setReadPermissions(Arrays.asList("email"));
	    Facebook_Button.setSessionStatusCallback(new Session.StatusCallback() {
	    	
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if(new Login_Utils(ctx).isOnline()){
					if(session.isOpened()){
						Request.newMeRequest(session, new Request.GraphUserCallback() {
							
							@Override
							public void onCompleted(GraphUser user, Response response) {
								if (user != null) {
									
									FId = user.getId();
									FName = user.getFirstName() + " " + user.getLastName();
									FMail = user.getProperty("email").toString();
									new AsyncTask_Facebook(ctx).execute(FMail, FName, FId);								
								}
								
							}
						}).executeAsync(); 
					}
				}else{
					Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
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