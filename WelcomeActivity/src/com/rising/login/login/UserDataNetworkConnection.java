package com.rising.login.login;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.store.DatosUsuario;

/**Clase que devuelve los datos del usuario (Id, Name, Mail y Money)
* 
* @author Ayo
* @version 2.0
* 
*/
public class UserDataNetworkConnection extends AsyncTask<String, Integer, String>{
			
	//Variables usadas
	private ArrayList<DatosUsuario> resultado = new ArrayList<DatosUsuario>();
    
    //URL
    private String URL_getUserInfo = "http://www.scores.rising.es/user-info";
    
	//Clases utilizadas
	private HttpPostAux HPA =  new HttpPostAux();
    
	
	public interface OnLoginCompleted{
        void onLoginCompleted();
    }
	
	public interface OnNetworkDown{
		void onNetworkDown();
	}
	
	private OnLoginCompleted listenerUser;
	private OnNetworkDown NetworkDown;
	
	public UserDataNetworkConnection(OnLoginCompleted listener2, OnNetworkDown networkfail) {
		this.listenerUser = listener2;
		this.NetworkDown = networkfail;
	}
		
	private String UserDataStatus(String Mail, String Fid){
		
		try{
			ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
			postparameters2send.add(new BasicNameValuePair("mail", Mail));
			postparameters2send.add(new BasicNameValuePair("fid", Fid));
							
			JSONArray jData = HPA.getServerData(postparameters2send, URL_getUserInfo);
			
			if(jData!=null && jData.length() > 0){
	
				JSONObject json_data;
				try{
					String id;
					String nombre;
					String mail; 
					double money;
									    
				    for(int i=0;i<jData.length();i++){ 
				    	json_data = jData.getJSONObject(i);
				    	id = json_data.getString("Id_U");
				    	nombre = json_data.getString("Name");
				    	mail = json_data.getString("Mail");
				    	money = json_data.getDouble("Money");
				    	Log.i("Money", "" + money);
				        resultado.add(new DatosUsuario(id,nombre,money, mail));
				    }
				}catch(JSONException e1){
					Log.e("JSONException UserDataNetwork", "Pues eso, JSONException: " + e1.getMessage());
					this.cancel(true);
				}catch (ParseException e1) {
			        Log.e("ParseException UserDataNetwork", "Pues eso, JSONException: " + e1.getMessage());
			        this.cancel(true);
			    }	            
			}else{	
				Log.e("JSON UserDataNetwork", "ERROR");
				this.cancel(true);
			}
		}catch(Exception e){
			Log.e("BigTry UserDataNetwork", "" + e.getMessage());
			this.cancel(true);
		}
		
		return "";
		
	}
	
    protected String doInBackground(String... urls) {
    	return UserDataStatus(urls[0], urls[1]);
    }
    
    protected void onPostExecute(String result) {
    	if (listenerUser != null) listenerUser.onLoginCompleted();
    }
    
    @Override
	protected void onCancelled(String result) {
		Log.d("Cancelled UDNC", "El hilo UDNC fue cancelado");	
		if(NetworkDown != null) NetworkDown.onNetworkDown();
	}

    public ArrayList<DatosUsuario> devolverDatos() {
    	return resultado;
    }
    
}