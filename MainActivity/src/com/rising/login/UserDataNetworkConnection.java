package com.rising.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.store.DatosUsuario;

public class UserDataNetworkConnection extends AsyncTask<String, Integer, String>{

	//  Comunicaci�n HTTP con el servidor
	HttpPost httppost;
	HttpClient httpcliente;
	String URL_connect = "http://www.scores.rising.es/user-info";
	
	//  Contexto
	Context context;
	
	//  Informaci�n obtenida de la base de datos
	ArrayList<DatosUsuario> resultado = new ArrayList<DatosUsuario>();
	
	public interface OnTaskCompleted{
        void onTaskCompleted();
    }
	
	private OnTaskCompleted listener;
	
	public UserDataNetworkConnection(OnTaskCompleted listener2, Context ctx) {
		this.context = ctx;
		this.listener = listener2;
	}
		
	// Do the long-running work in here
    protected String doInBackground(String... urls) {
            	
        JSONArray jArray = null;
        String result = null;
        StringBuilder sb = null;
        InputStream is = null;
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();      
        
        // Http post
        try{
        	httpcliente = new DefaultHttpClient();
        	params.add(new BasicNameValuePair("mail", urls[0]));
        	        	
            httppost = new HttpPost(URL_connect);
                        
            httppost.setEntity(new UrlEncodedFormEntity(params));
                        
            HttpResponse responses = httpcliente.execute(httppost);

            HttpEntity entitys = responses.getEntity();
             
            is = entitys.getContent();
            Log.i("Params", "Parametros enviados: " + params.toString());
        }catch(Exception e){
        	Log.e("User_Log_Tag_Connection", "Error in http connection: " + e.toString());
        	httppost.abort();
        	this.cancel(true); 
        }       
        
        // Convert response to string
	    try{
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	        sb = new StringBuilder();
	        sb.append(reader.readLine() + "\n");

	        String line="0";
	        while ((line = reader.readLine()) != null) {
	        	sb.append(line + "\n");
	        }
	        is.close();
	        result=sb.toString();
	        
	    }catch(Exception e){
	        Log.e("user_log_tag_convert", "Error converting result " + e.toString());	        
	    }
	    
		try{
			
			String id;
			String nombre;
			String mail; 
			double money;
			
			jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){ 
		    	json_data = jArray.getJSONObject(i);
		    	id = json_data.getString("Id_U");
		    	nombre = json_data.getString("Name");
		    	mail = json_data.getString("Mail");
		    	money = json_data.getDouble("Money");
		    			    	
		        resultado.add(new DatosUsuario(id,nombre,money, mail));
		        
		    }
		}catch(JSONException e1){
			
			Log.d("JSONException", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + result.toString());
		}catch (ParseException e1) {
	        e1.printStackTrace();
	    }
        
		// Making HTTP Request
		try {
			HttpResponse response = httpcliente.execute(httppost);
 
            // writing response to log
            Log.d("Http Response:", response.toString());
        }catch (ClientProtocolException e0) {
        	
            // writing exception to log 
            e0.printStackTrace();
        }catch (IOException e1) {
        	
            // writing exception to log
            e1.printStackTrace();
        }
		
    	return "";
    }
    
    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
    	
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(String result) {
    	if (listener != null) listener.onTaskCompleted();
    }
    
    // Devolver la informaci�n le�da de la base de datos
    public ArrayList<DatosUsuario> devolverDatos() {
    	return resultado;
    }
    
}