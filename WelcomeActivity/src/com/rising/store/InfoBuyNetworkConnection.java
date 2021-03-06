package com.rising.store;

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

import android.content.Context;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

public class InfoBuyNetworkConnection extends AsyncTask<String, Integer, String>{

	//  Comunicaci�n HTTP con el servidor
	HttpPost httppost;
	HttpClient httpclient;
	HttpParams httpParams = new BasicHttpParams();
	final int CONN_WAIT_TIME = 30000;
	final int CONN_DATA_WAIT_TIME = 20000;
	
	//  Contexto
	Context context;
	
	ArrayList<InfoCompra> S_Bought = new ArrayList<InfoCompra>();
	
	public InfoBuyNetworkConnection(Context ctx) {
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
        	params.add(new BasicNameValuePair("id_u", urls[0]));
        	        	
        	HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
        	HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);
        	httpclient = new DefaultHttpClient(httpParams);
            httppost = new HttpPost("http://www.scores.rising.es/store-infobuy");
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.i("Params", "Parametros enviados: " + params.toString());
        }catch(Exception e){
        	Log.e("Log_Tag_Connection", "Error in http connection: " + e.toString());
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
	        Log.e("log_tag_convert", "Error converting result " + e.toString());	        
	    }
	    
		try{
			
			int Id_S;
			
			jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);

		    	Id_S = json_data.getInt("Id_S");
		    	
		    	S_Bought.add(new InfoCompra(Id_S));
		    }
		}catch(JSONException e1){
			
			Log.d("JSONException", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + result.toString());
			
		}catch (ParseException e1) {
			
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
    	
    }
    
    // Devolver la informaci�n le�da de la base de datos
    public ArrayList<InfoCompra> devolverCompra() {
    	return S_Bought;
    }
    
}