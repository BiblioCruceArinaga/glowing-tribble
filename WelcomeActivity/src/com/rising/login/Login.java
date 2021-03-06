package com.rising.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.rising.conexiones.HttpPostAux;
import com.rising.drawing.R;
import com.rising.login.ActivityRegistro.OnTaskCompleted;
import com.rising.login.UserDataNetworkConnection.OnLoginCompleted;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.store.DatosUsuario;

//Clase login. Permite al usuario introducir correo electrónico y contraseña, registrarse o recuperar 
//la contraseña olvidada.

public class Login extends FragmentActivity {
	
	SessionManager session;
	LoginButton authButton;
	public Configuration conf = new Configuration(this);
	public static String FId;
	public static String FName;
	public static String FMail;

	private EditText Mail, Pass;
	private Button Login, Registro;
	private TextView OlvidaPass;
	private EditText Mail_OlvidoPass, Nombre_Registro, Mail_Registro, Pass_Registro, ConfiPass_Registro;	
	private Button Confirm_Login, Cancel_Login;
	private Button Confirm_OlvidoPass, Cancel_OlvidoPass;
	private Button Confirm_Reg, Cancel_Reg;
	private ProgressDialog PDialog;
	private HttpPostAux HPA =  new HttpPostAux();
	private Dialog LPDialog, LDialog, RDialog, EDialog;
	private String usuario = "";
	private String passw = "";
	private String pass = "";
	private String confipass = "";
	private String mail = "";
	private String name = "";	
	private String language = "";
	private Context ctx;
	private String URL_connect = "http://www.scores.rising.es/login-mobile";
	private String URL_Check_Facebook = "http://www.scores.rising.es/login-facebook-mobile";
	public static UserDataNetworkConnection dunc;
	public static ArrayList<DatosUsuario> userData;
	
	private OnLoginCompleted listenerUser = new OnLoginCompleted(){
		public void onLoginCompleted(){
			
			userData = dunc.devolverDatos();

			Log.i("UserData", "" + dunc.devolverDatos().get(0).getMoney());
			
            conf.setUserId(userData.get(0).getId());
            
            Log.i("Conf", userData.get(0).getMoney() + "");
            
            conf.setUserName(userData.get(0).getName());

            conf.setUserEmail(userData.get(0).getMail());
            
            conf.setUserMoney(userData.get(0).getMoney());
		}
	};
	
	//  Recibir la señal del proceso que termina el registro
	private OnTaskCompleted listener = new OnTaskCompleted() {
	    public void onTaskCompleted(int details) {  
	    	PDialog.dismiss();
	    	
	    	Pass_Registro.setText("");
			ConfiPass_Registro.setText("");
			
			if (details == 1) 
				Toast.makeText(getApplicationContext(), R.string.ok_reg, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), R.string.ok_reg_mail, Toast.LENGTH_LONG).show();
	    }

		public void onTaskFailed(int details) {
			PDialog.dismiss();
			
			Pass_Registro.setText("");
			ConfiPass_Registro.setText("");
			
			if(details == 4){
				Toast.makeText(getApplicationContext(), R.string.err_net, Toast.LENGTH_LONG).show();
			}else{
				if (details == 0) 
					Toast.makeText(getApplicationContext(), R.string.err_reg, Toast.LENGTH_LONG).show();
				else
					Toast.makeText(getApplicationContext(), R.string.err_reg_mail, Toast.LENGTH_LONG).show();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		language = Locale.getDefault().getDisplayLanguage();
		
		session = new SessionManager(getApplicationContext());
		dunc = new UserDataNetworkConnection(listenerUser, getApplicationContext());
		ctx = this;
				
		if (session.isLoggedIn()) {
			Intent i = new Intent(Login.this, MainScreenActivity.class);
			startActivity(i);
			finish();
		}
	
		//  Datos login
		Login = (Button) findViewById(R.id.button_login);
				
		//  Datos olvido pass
		OlvidaPass = (TextView)findViewById(R.id.tv_olvido_pass);
		
		//  Datos registro
		Registro = (Button) findViewById(R.id.b_registro);
		
		final ActivityOlvidarPass AOP = new ActivityOlvidarPass();
		final ActivityRegistro AR = new ActivityRegistro();
		
		//  Acciones al presionar el botón de nombre Login
		Login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(isOnline()){	
					LDialog = new Dialog(Login.this, R.style.cust_dialog);
					
					LDialog.setContentView(R.layout.login_dialog);
					LDialog.setTitle(R.string.login_title);
					
					Confirm_Login = (Button)LDialog.findViewById(R.id.b_confirm_login);
					Cancel_Login = (Button)LDialog.findViewById(R.id.b_cancel_login);
					Mail = (EditText)LDialog.findViewById(R.id.et_mail);
					Pass = (EditText)LDialog.findViewById(R.id.et_pass);
					
					Confirm_Login.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {
	
				        	usuario = Mail.getText().toString();
				        	passw = Pass.getText().toString();
				        	
				        	Log.i("Login", "Mail: " + usuario + ", Pass: " + passw);
				        					        	
			        		if (checkLoginData(usuario, passw)==true) {
			        			
			        			new asynclogin().execute(usuario,passw);    
			        			Pass.setText("");		        			
			        		}else{
			        			errLogin(0);
			        		} 
			        		
			        		LDialog.dismiss();
						}
						
					});
					
					Cancel_Login.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {
							LDialog.dismiss();					
						}
						
					});
					
					LDialog.show();
				}else{
					Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
				}	
        	}  
		});
		
		//  Acciones al presionar sobre la frase de nombre OlvidaPass
		OlvidaPass.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				if(isOnline()){
					LPDialog = new Dialog(Login.this, R.style.cust_dialog);
					
					LPDialog.setContentView(R.layout.olvidopass_dialog);
					LPDialog.setTitle(R.string.olvido_title);
												
					Confirm_OlvidoPass = (Button)LPDialog.findViewById(R.id.b_confirm_olpass);
					Cancel_OlvidoPass = (Button)LPDialog.findViewById(R.id.b_cancel_olpass);
					Mail_OlvidoPass = (EditText)LPDialog.findViewById(R.id.et_mail_olvidopass);
									
					Confirm_OlvidoPass.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {
							String mail = Mail_OlvidoPass.getText().toString();
							if (mail.equals("")) {
								Toast.makeText(getApplicationContext(), R.string.err_campos_vacios, Toast.LENGTH_SHORT).show();
							}
							else {
								AOP.new asyncmail(getApplicationContext()).execute(mail, language); 
								LPDialog.dismiss();
							}
						}
						
					});
							
					Cancel_OlvidoPass.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {
							LPDialog.dismiss();
						}
						
					});	
					
					LPDialog.show();
				}else{
					Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
				}
			}
		});
	 
		//  Acciones al presionar sobre la palabra de nombre Registro
		Registro.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(isOnline()){	
					RDialog = new Dialog(Login.this, R.style.cust_dialog);
					
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
							
							if(Nombre_Registro.getText().toString().equals("") && 
									Mail_Registro.getText().toString().equals("") && 
									Pass_Registro.getText().toString().equals("") && 
									ConfiPass_Registro.getText().toString().equals("")) {
	 			        		
								Toast.makeText(getApplicationContext(), 
										R.string.err_campos_vacios, Toast.LENGTH_LONG).show();
																						 							
	 			        	}else{
	 			        		
	 			        		name = Nombre_Registro.getText().toString();
								mail = Mail_Registro.getText().toString();
								pass = Pass_Registro.getText().toString();
								confipass = ConfiPass_Registro.getText().toString();
								
								if(checkPass(pass, confipass)){
						            PDialog = new ProgressDialog(Login.this);
						            PDialog.setMessage(getString(R.string.creating_account));
						            PDialog.setIndeterminate(false);
						            PDialog.setCancelable(false);
						            PDialog.show();
						            
									AR.new asyncreg(listener).execute(name, mail, pass, language); 
	 			        		}else{
	 			        			Toast.makeText(getApplicationContext(), R.string.err_pass, Toast.LENGTH_LONG).show();
	 			        		}
	 			        	}
	 			        	
	 		        		RDialog.dismiss();
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
		});
		
		//Acciones al presionar sobre el botón de Facebook
		authButton = (LoginButton) findViewById(R.id.button_login_f);

		authButton.setOnErrorListener(new OnErrorListener() {
	       
	       @Override
	       public void onError(FacebookException error) {
	         Log.e("FacebookError", error.toString());
	       }
	    });
	    
	    authButton.setReadPermissions(Arrays.asList("email"));
	    authButton.setSessionStatusCallback(new Session.StatusCallback() {
	    	
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if(isOnline()){
					if(session.isOpened()){
						Request.newMeRequest(session, new Request.GraphUserCallback() {
							
							@Override
							public void onCompleted(GraphUser user, Response response) {
								if (user != null) {
									
									FId = user.getId();
									FName = user.getFirstName() + " " + user.getLastName();
									FMail = user.getProperty("email").toString();
									new asyncFacebook_process().execute(FMail, FName, FId);
																
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
	
	//  Mostrar errores
    public void errLogin(int code){
    	
    	EDialog = new Dialog(Login.this, R.style.cust_dialog);
    	EDialog.getWindow();
        EDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		EDialog.setContentView(R.layout.login_error_dialog);
		EDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		TextView tv_E = (TextView)EDialog.findViewById(R.id.error_tV);
		
		switch (code) {
			case 0:
				tv_E.setText(R.string.err_campos_vacios);
				break;
			case 2:
				tv_E.setText(R.string.err_login_unknown_user);
				break;
			case 3:
				tv_E.setText(R.string.err_not_active);
				break;
			default:
				tv_E.setText(R.string.err_login_unknown);
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
	
    //  Este método valida que no haya ningun campo en blanco, 
    //  devolviendo false si false si lo hay y true si no.
    public boolean checkLoginData(String username ,String password ){
    	
	    if(username.equals("") || password.equals("")){
	    	return false;
	    }else{
	    	return true;
	    }
    }

    //  Este método valida que las contraseñas sean iguales
    public boolean checkPass(String pass, String confipass){
    	
    	if(confipass.equals(pass)){
    		return true;
    	}else{
    		return false;
    	}
    } 

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch(NullPointerException n) {
			return false;
		}
	}
    
    //  Validar el estado del login
    public int loginStatus(String username, String password) {
    	int logStatus=-1;
    	
    	ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();
		postparameters2send.add(new BasicNameValuePair("usuario", username));
		postparameters2send.add(new BasicNameValuePair("password", password));

      	JSONArray jData = HPA.getServerData(postparameters2send, URL_connect);

		if (jData!=null && jData.length() > 0){
			JSONObject json_Data;
			
			try {
				json_Data = jData.getJSONObject(0);
				logStatus=json_Data.getInt("logstatus");
				Log.e("LoginStatus","LogStatus= "+logStatus);
			} catch (JSONException e) {
				e.printStackTrace();
			}		            
		             
			//  Aquí se valida el valor obtenido
		    if (logStatus==0) Log.e("LoginStatus ", "Invalido");
		    else Log.e("LoginStatus ", "Valido");

		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return logStatus;
    }   

    //  Gestión del login
    class asynclogin extends AsyncTask< String, String, Integer > {
        	 
    	String user,pass;
        protected void onPreExecute() {
            PDialog = new ProgressDialog(Login.this);
            PDialog.setMessage(getString(R.string.auth));
            PDialog.setIndeterminate(false);
            PDialog.setCancelable(false);
            PDialog.show();
        }
     
    	protected Integer doInBackground(String... params){
    		user=params[0];
    		pass=params[1];
    		
        	return loginStatus(user,pass);  		
    	}
           
        protected void onPostExecute(Integer result) {
        	PDialog.dismiss();
            Log.e("onPostExecute=",""+result);
            
            userData = new ArrayList<DatosUsuario>();
            
            if (result == 1) {
            	dunc.execute(usuario);
            	
            	session.createLoginSession(user, "", "-1");            	
    			Intent i=new Intent(Login.this, MainScreenActivity.class);
    			startActivity(i); 
    			finish();	
            }else{
               	errLogin(result);
            }
        }
    }

    //  Gestión del login / registro mediante Facebook
    class asyncFacebook_process extends AsyncTask<String, String, Integer>{

    	@Override
    	protected void onPreExecute() {
            PDialog = new ProgressDialog(Login.this);
            PDialog.setMessage(getString(R.string.auth));
            PDialog.setIndeterminate(false);
            PDialog.setCancelable(false);
            PDialog.show();
        }
    	
		@Override
		protected Integer doInBackground(String... params) {
			int status = -1;

	    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
			postparameters2send.add(new BasicNameValuePair("mail", params[0]));
			postparameters2send.add(new BasicNameValuePair("name", params[1]));
			postparameters2send.add(new BasicNameValuePair("pass", params[2]));
			
	      	JSONArray jData = HPA.getServerData(postparameters2send, URL_Check_Facebook);

			if (jData!=null && jData.length() > 0) {

				JSONObject json_data;
				
				try{
					json_data = jData.getJSONObject(0);
					status = json_data.getInt("facebookStatus");
					
					Log.e("facebookStatus","facebookStatus= " + status);
				}catch (JSONException e) {
					e.printStackTrace();
				}		            

			}else{	
				Log.e("JSON", "ERROR");
			}
			
			return status;
		} 	
		
		protected void onPostExecute(Integer result) {
			Log.e("onPostExecute=",""+result);
			
        	switch (result) {
	        	case 0: {
	        		Toast.makeText(getApplicationContext(), R.string.err_campos_vacios, Toast.LENGTH_LONG).show();
	        		break;
	        	}
	        	case 1: {
	        		dunc.execute(FMail);
	            	
	        		session.createLoginSession(FMail, FName, FId);
	                Intent i=new Intent(Login.this, MainScreenActivity.class);
	                startActivity(i);
	                finish();
	        		break;
	        	}
	        	case 2: {
	        		Toast.makeText(getApplicationContext(), R.string.err_login_unknown_user, Toast.LENGTH_LONG).show();
	        		break;
	        	}
	        	case 3: {
	        		dunc.execute(FMail);
	        		
	        		session.createLoginSession(FMail, FName, FId);
	        		
	                Intent i=new Intent(Login.this, MainScreenActivity.class);
	                startActivity(i);
	                finish();
	        		break;
	        	}
	        	case 4: {
	        		Toast.makeText(getApplicationContext(), R.string.err_login_unknown, Toast.LENGTH_LONG).show();
	        		break;
	        	}
        		default: 
        			Toast.makeText(getApplicationContext(), R.string.err_login_unknown, Toast.LENGTH_LONG).show();
        	}
        }
    }
}