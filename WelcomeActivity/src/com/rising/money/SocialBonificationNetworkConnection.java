package com.rising.money;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.login.Configuration;

//Hilo que se encarga de registrar las bonificaciones de los usuarios de los que se pase el id
public class SocialBonificationNetworkConnection extends AsyncTask<String, Integer, Integer>{

	//Variables
	private Context ctx;
	private int status = -1;
	
	//URL
	private String URL_Bonification = "http://www.scores.rising.es/store-bonification-social";
	
	//Clase usadas
	private Configuration CONF;
	private HttpPostAux HPA = new HttpPostAux();
	

	private OnSuccessBonification SuccessBonification;
	private OnFailBonification FailBonification;	
	
	public interface OnSuccessBonification{
		void onSuccessBonification();
	}
					
	public interface OnFailBonification{
		void onFailBonification();
	}
	
	public SocialBonificationNetworkConnection(OnSuccessBonification success, OnFailBonification fail, Context context) {
		this.ctx = context;
		this.SuccessBonification = success;
		this.FailBonification = fail;
		this.CONF = new Configuration(ctx);
	}
	
	private int Bonification_Status(String Id_Bonification, String Id_U){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
 		
		params.add(new BasicNameValuePair("id_u", Id_U));
		params.add(new BasicNameValuePair("id_b", Id_Bonification));
	
		try{
			JSONArray jData = HPA.getServerData(params, URL_Bonification);
			
				if(jData!=null && jData.length() > 0){

					JSONObject json_data;
					try{
						json_data = jData.getJSONObject(0);
						status = json_data.getInt("bonificationstatus"); 
						Log.e("Bonification","bonificationstatus= " + status);
					}catch (JSONException e) {
						Log.e("JSONException Bonification_AsyncTask", e.getMessage().toString());
					}		            
				}else{	
					Log.e("JSON Bonification_AsyncTask", "ERROR");
				}
		}catch(Exception e){
			Log.e("Exception BigTry Bonification_AsyncTask", "" + e.getMessage());
		}
		return status;
	}
		
	@Override
	protected Integer doInBackground(String... params) {
		return Bonification_Status(params[0], CONF.getUserId());
	}
		
	@Override
	protected void onPostExecute(Integer result) {
	    	 
	    if(status == 1){
	    	if (SuccessBonification != null) SuccessBonification.onSuccessBonification();
	    }else{
	    	if (FailBonification != null) FailBonification.onFailBonification();
	    }
	}
	
}