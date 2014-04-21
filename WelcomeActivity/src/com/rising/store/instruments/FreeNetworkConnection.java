package com.rising.store.instruments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rising.store.PartituraTienda;

import android.content.Context;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

public class FreeNetworkConnection extends AsyncTask<String, Integer, String>{

	//  Comunicaci�n HTTP con el servidor
	HttpPost httppost;
	HttpClient httpclient;
	HttpParams httpParams = new BasicHttpParams();
	final int CONN_WAIT_TIME = 30000;
	final int CONN_DATA_WAIT_TIME = 20000;
	
	//  Contexto
	Context context;
	
	//  Informaci�n obtenida de la base de datos
	ArrayList<PartituraTienda> resultado = new ArrayList<PartituraTienda>();
	
	public interface OnTaskCompleted{
        void onTaskCompleted();
    }
	
	public interface OnTaskUncomplete{
		void onTaskUncomplete();
	}

    private OnTaskCompleted listener;
    private OnTaskUncomplete listen;
	
	public FreeNetworkConnection(OnTaskUncomplete listen, OnTaskCompleted listener, Context ctx) {
		this.listener = listener;
		this.listen = listen;
		context = ctx;
	}
		
	// Do the long-running work in here
    protected String doInBackground(String... urls) {
        
        JSONArray jArray = null;
        String result = null;
        StringBuilder sb = null;
        InputStream is = null;
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();      
        
        // Http post
        try{
        	
        	//params.add(user);
        	params.add(new BasicNameValuePair("Lenguaje", urls[0]));
        	        	
        	HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
        	HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);
        	httpclient = new DefaultHttpClient(httpParams);
            httppost = new HttpPost("http://www.scores.rising.es/store-free");
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.i("Params", "Parametros enviados: " + params.toString());
        }catch(Exception e){
        	Log.e("Log_Tag_Connection", "Error in http connection: " + e.toString());
        	httppost.abort();
        	this.cancel(true); 
        	if(listen != null) listen.onTaskUncomplete();
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
	        Log.e("log_tag_convert", "Error converting result " + e.toString());	        
	    }
	    
		try{
			
			int id;
			String nombre;
			String autor; 
			String instrumento;
			String description;
			int year;
			float precio;
			boolean comprado;
			String URL;
			
			jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
		    	
		    	id = json_data.getInt("Id_S");
		    	nombre = json_data.getString("Name_Song");
		    	autor = json_data.getString("Author");
		    	instrumento = json_data.getString("instrument");
		    	precio = (float) json_data.getDouble("Price");
		    	description = json_data.getString("Description");		    	
		    	year = json_data.getInt("Year");
		    	comprado = false;
		    	URL = json_data.getString("URL");
		    	
		        resultado.add(new PartituraTienda(id,nombre,autor,instrumento,precio, description, year, comprado, URL));
		    }
		}catch(JSONException e1){
			
			Log.d("JSONException", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + result.toString());
			if(listen != null) listen.onTaskUncomplete();
		}catch (ParseException e1) {
			if(listen != null) listen.onTaskUncomplete();
	        e1.printStackTrace();
	    }
        
		// Making HTTP Request
		try {
			HttpResponse response = httpclient.execute(httppost);
 
            // writing response to log
            Log.d("Http Response:", response.toString());
        }catch (ClientProtocolException e0) {
        	
            // writing exception to log 
            e0.printStackTrace();
            if(listen != null) listen.onTaskUncomplete();
        }catch (IOException e1) {
        	
            // writing exception to log
            e1.printStackTrace();
            if(listen != null) listen.onTaskUncomplete();
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
    public ArrayList<PartituraTienda> devolverPartituras() {
    	return resultado;
    }
    
}