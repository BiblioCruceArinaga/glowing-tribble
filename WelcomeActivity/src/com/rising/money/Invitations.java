package com.rising.money;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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

import com.rising.conexiones.HttpPostAux;
import com.rising.login.Configuration;

public class Invitations extends AsyncTask<String, Integer, String>{
	
	//Esta por probar. 
		
	//Comunicaci�n HTTP con el servidor
	HttpPost httppost;
	HttpClient httpcliente;
	String URL_connect = "http://www.scores.rising.es/invitar_mobile";
	String URL_connect_en = "http://www.scores.rising.es/en/invitar_mobile";
	String language = "";
	
	HttpPostAux postAux = new HttpPostAux();
	
	String Id_U;
	String Mail;
	Context context;
	Configuration conf;
	String mensaje = "";
	
	public interface OnInvitationOk{
        void onInvitationOk();
    }
	
	private OnInvitationOk listenerInvitation;
		
	public interface OnInvitationFail{
		void onInvitationFail();
	}
	
	private OnInvitationFail failInvitation;
	
	public Invitations(OnInvitationOk listener, OnInvitationFail failMoney, Context ctx) {
		this.context = ctx;
		this.listenerInvitation = listener;
		conf = new Configuration(ctx);
		language = Locale.getDefault().getDisplayLanguage();
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		//Hará falta la id del usuario, el método de pago y la cantidad comprada
		Id_U = conf.getUserEmail();
		Mail = params[0];
		
		JSONArray jArray = null;
        String result = null;
        StringBuilder sb = null;
        InputStream is = null;
        ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();      
        
        // Http post
        try{
        	httpcliente = new DefaultHttpClient();
        	values.add(new BasicNameValuePair("usuario", Id_U));
        	values.add(new BasicNameValuePair("email1", params[0]));
        	        	
        	if(language.equals("spanish")){
        		httppost = new HttpPost(URL_connect);
        	}else{
        		httppost = new HttpPost(URL_connect_en);
        	}
        	                        
            httppost.setEntity(new UrlEncodedFormEntity(values));
                        
            HttpResponse responses = httpcliente.execute(httppost);

            HttpEntity entitys = responses.getEntity();
             
            is = entitys.getContent();
            Log.i("Params", "Parametros enviados: " + params.toString());
        }catch(Exception e){
        	Log.e("User_Log_Tag_Connection", "Error in http connection: " + e.toString());
        	if (failInvitation != null) failInvitation.onInvitationFail();
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
	        if (failInvitation != null) failInvitation.onInvitationFail();
	        httppost.abort();
        	this.cancel(true); 
	    }
	    
		try{
			
			jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){ 
		    	json_data = jArray.getJSONObject(i);
		    	mensaje = json_data.getString("invitation");	
		    	Log.i("Mensaje", mensaje);
		    }
		}catch(JSONException e1){
			if (failInvitation != null) failInvitation.onInvitationFail();
			Log.d("JSONException", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + result.toString());
			httppost.abort();
        	this.cancel(true); 
		}catch (ParseException e1) {
			if (failInvitation != null) failInvitation.onInvitationFail();
	        e1.printStackTrace();
	        httppost.abort();
        	this.cancel(true); 
	    } 
        		
    	return "";
	}
	
	// This is called when doInBackground() is finished
    protected void onPostExecute(String result) {
    	
    	if(result != null){
    		if (listenerInvitation != null) listenerInvitation.onInvitationOk();
    	}else{
    		if (failInvitation != null) failInvitation.onInvitationFail();
    	}
    }
}