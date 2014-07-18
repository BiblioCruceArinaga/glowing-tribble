package com.rising.login.facebook;

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
import android.widget.Toast;

import com.rising.conexiones.HttpPostAux;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.SessionManager;
import com.rising.login.login.UserDataNetworkConnection;
import com.rising.login.login.UserDataNetworkConnection.OnLoginCompleted;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.store.DatosUsuario;

//Gestión del login / registro mediante Facebook
public class AsyncTask_Facebook extends AsyncTask<String, String, Integer>{

	private Context ctx;
	private String FMail;
	private String FName;
	private String FId;
	private static ArrayList<DatosUsuario> userData;
	private ProgressDialog PDialog;
	
	//Clases utilizadas
	private HttpPostAux HPA =  new HttpPostAux();
	public Configuration conf;
	private static UserDataNetworkConnection dunc;
	private SessionManager session;
	
	//URLs
	private String URL_Check_Facebook = "http://www.scores.rising.es/login-facebook-mobile";
	
	private OnLoginCompleted listenerUser = new OnLoginCompleted(){
		public void onLoginCompleted(){
				
			userData = new ArrayList<DatosUsuario>();
			
			userData = dunc.devolverDatos();
			
			conf.setUserId(userData.get(0).getId());	           
			conf.setUserName(userData.get(0).getName());
			conf.setUserEmail(userData.get(0).getMail());
			conf.setUserMoney(userData.get(0).getMoney());			
								
        	
    		session.createLoginSession(FMail, FName, FId);
            Intent i=new Intent(ctx, MainScreenActivity.class);
            ctx.startActivity(i);
            ((Activity) ctx).finish();
            PDialog.dismiss();
		}
	};
	
	public AsyncTask_Facebook(Context context){
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
	
	private int Facebook_Status(String mail, String name, String id){
		int status = -1;
		
		FMail = mail;
		FName = name;
		FId = id; 
		
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		postparameters2send.add(new BasicNameValuePair("mail", mail));
		postparameters2send.add(new BasicNameValuePair("name", name));
		postparameters2send.add(new BasicNameValuePair("pass", id));
		
      	JSONArray jData = HPA.getServerData(postparameters2send, URL_Check_Facebook);

		if (jData!=null && jData.length() > 0) {

			JSONObject json_data;
			
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("facebookStatus");
				
				Log.e("FacebookStatus","FacebookStatus= " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		             

		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		return Facebook_Status(params[0], params[1], params[2]);
	} 	
	
	protected void onPostExecute(Integer result) {
		Log.e("onPostExecute=",""+result);
		
		dunc = new UserDataNetworkConnection(listenerUser);
		
    	switch (result) {
        	case 0: {
        		Toast.makeText(ctx, R.string.err_campos_vacios, Toast.LENGTH_LONG).show();
        		break;
        	}
        	case 1: {
        		dunc.execute(FMail);
        		break;
        	}
        	case 2: {
        		Toast.makeText(ctx, R.string.err_login_unknown_user, Toast.LENGTH_LONG).show();
        		break;
        	}
        	case 3: {
        		dunc.execute(FMail);
        		break;
        	}
        	case 4: {
        		Toast.makeText(ctx, R.string.err_login_unknown, Toast.LENGTH_LONG).show();
        		break;
        	}
    		default: 
    			Toast.makeText(ctx, R.string.err_login_unknown, Toast.LENGTH_LONG).show();
    	}
    }
}