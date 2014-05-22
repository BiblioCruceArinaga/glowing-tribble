package com.rising.money;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.login.Configuration;

public class SocialBonificationNetworkConnection extends AsyncTask<String, Integer, Integer>{

	//Esta por probar. 
	
		//Comunicaci�n HTTP con el servidor
		HttpPost httppost;
		HttpClient httpcliente;
		String URL_connect = "http://www.scores.rising.es/store-bonification-social";
		HttpPostAux postAux = new HttpPostAux();
		int status = -1;
		String Id_U;
		String Id_B;
		Context context;
		Configuration conf;
		
		public interface OnBonificationDone{
	        void onBonificationDone();
	    }
		
		private OnBonificationDone listenerBonification;
			
		public interface OnFailBonification{
			void onFailBonification();
		}
		
		private OnFailBonification failMoney;
		
		public SocialBonificationNetworkConnection(OnBonificationDone listener, OnFailBonification failMoney, Context ctx) {
			this.context = ctx;
			this.listenerBonification = listener;
			conf = new Configuration(ctx);
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			
			//Recojo la id de la bonificación a través de params
			Id_U = conf.getUserId();
			Id_B = params[0];
						
			ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
			postparameters2send.add(new BasicNameValuePair("id_u", Id_U));
			postparameters2send.add(new BasicNameValuePair("id_b", Id_B));
					
			JSONArray jData = postAux.getServerData(postparameters2send, URL_connect);
			
			if(jData!=null && jData.length() > 0){

				JSONObject json_data;
				try{
					json_data = jData.getJSONObject(0);
					status = json_data.getInt("bonificationstatus"); 
					Log.e("Bonification","bonificationstatus= " + status);
				}catch (JSONException e) {
					e.printStackTrace();
				}		            
			}else{	
				Log.e("JSON", "ERROR");
			}
			
			return status;
		}
		
		// This is called when doInBackground() is finished
	    @Override
		protected void onPostExecute(Integer result) {
	    	
	    	//Si es 1 está bien, si es 2 hubo un problema al registrar la compra en la base de datos. 
	    	if(status == 1){
	    		if (listenerBonification != null) listenerBonification.onBonificationDone();
	    	}else{
	    		if (failMoney != null) failMoney.onFailBonification();
	    	}
	    }
	
}
