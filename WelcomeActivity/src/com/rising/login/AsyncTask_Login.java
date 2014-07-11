package com.rising.login;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.drawing.R;
import com.rising.login.UserDataNetworkConnection.OnLoginCompleted;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.store.DatosUsuario;

//Clase de gestión del Login
public class AsyncTask_Login extends AsyncTask< String, String, Integer >{

	private SessionManager session;
	private ProgressDialog PDialog; 
	private String user;
	private Context ctx;
	private static ArrayList<DatosUsuario> userData;
	private String usuario = "";
	
	//Clases utilizadas
	private HttpPostAux HPA =  new HttpPostAux();
	public Configuration conf;
	private static UserDataNetworkConnection dunc;
	
	private String URL_connect = "http://www.scores.rising.es/login-mobile";
	
	//Recibe la señal del proceso que termina el Login e introduce los datos del usuario en Configuration. 
	private OnLoginCompleted listenerUser = new OnLoginCompleted(){
		public void onLoginCompleted(){
				
			userData = new ArrayList<DatosUsuario>();
			
			userData = dunc.devolverDatos();
			//Borrar contraseña después de usarla
			conf.setUserId(userData.get(0).getId());	           
			conf.setUserName(userData.get(0).getName());
			conf.setUserEmail(userData.get(0).getMail());
			conf.setUserMoney(userData.get(0).getMoney());
						
			
			session.createLoginSession(conf.getUserEmail(), conf.getUserName(), "-1");
	    	PDialog.dismiss();
	    	Intent i = new Intent(ctx, MainScreenActivity.class);
	    	ctx.startActivity(i); 
	    	((Activity) ctx).finish();			
		}
	};

	
	public AsyncTask_Login(Context context){
		this.ctx = context;
		this.conf = new Configuration(ctx);
		this.session = new SessionManager(ctx.getApplicationContext());
	}
		
    protected void onPreExecute() {
    	PDialog = new ProgressDialog(ctx);
        PDialog.setMessage(ctx.getString(R.string.auth));
        PDialog.setIndeterminate(false);
        PDialog.setCancelable(false);
        PDialog.show();
    }
     
    //  Validar el estado del login
    public int LoginStatus(String username, String password) {
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
   
	@Override
	protected Integer doInBackground(String... params) {
		return LoginStatus(params[0],params[1]);  
	}
           
    protected void onPostExecute(Integer result) {
    	        
		dunc = new UserDataNetworkConnection(listenerUser, ctx.getApplicationContext());
    	            
        if (result == 1) {
        	dunc.execute(usuario);            		
        }else{
        	PDialog.dismiss();
           	new Login_Actions(ctx).errLogin(result);
        }
    }
	
}