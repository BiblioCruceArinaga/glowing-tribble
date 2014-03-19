package com.rising.conexiones;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
//Clase para el envio de peticiones y manejo de respuestas

public class HttpPostAux{
	
	//Inicializo los tipos de datos que necesitaré
	private InputStream is = null;
	public String result = "";
	  
	//Este método envia los datos contenidos en parameters a la dirección indicada en urlwebserver
	public JSONArray getServerData(ArrayList<NameValuePair> parameters, String urlwebserver){
	
		//Aquí se conecta via http con la dirección indicada y envia los datos como POST.
		httpPostConnect(parameters,urlwebserver);
		
		//Si se obtiene una respuesta la pasa por el método getPostResponse y devuelve un array de JSON, si no
		//devuelve null
		if(is!=null){  
			getPostResponse();
			return getJSONArray();
		}else{
			return null;
		}
	} 
		  
	//Este método envia la peticion HTTP a la dirección que se le pasa. 
    private void httpPostConnect(ArrayList<NameValuePair> parametros, String urlwebserver){
   	
	  	try{
	  		HttpClient httpclient = new DefaultHttpClient();
	  	    HttpPost httppost = new HttpPost(urlwebserver);
	  	    httppost.setEntity(new UrlEncodedFormEntity(parametros));
	  	    
	  	    //Se ejecuta la peticion enviando los datos por POST
	  	    HttpResponse response = httpclient.execute(httppost); 
	  	    HttpEntity entity = response.getEntity();
	  	    is = entity.getContent();    	  	    
	  	}catch(Exception e){
	  	        Log.e("Log_Tag", "Error en la conexión HTTP: "+e.toString());
	  	}
    }
  
    //Este método envia una petición por POST y recibe del php la respuesta, que es el nombre del 
    //usuario dueño del mail que se le envia.
    public String userName(String user, String urlwebserver){
    	
    	try{
    		HttpClient httpclient = new DefaultHttpClient();
	  	    HttpPost httppost = new HttpPost(urlwebserver);
	  	    
	  	    //Aquí se añaden los parámetros
	  	    List<NameValuePair> usuario = new ArrayList<NameValuePair>();
	  	    usuario.add(new BasicNameValuePair("user", user));
	  	    httppost.setEntity(new UrlEncodedFormEntity(usuario));
	  	    
	  	    //Se ejecuta la peticion enviando los datos por POST
	  	    HttpResponse response = httpclient.execute(httppost); 
	  	    HttpEntity entity = response.getEntity();
	  	    String name_u = EntityUtils.toString(entity);   	
	  	    return name_u;
	  	}catch(Exception e){
	  	        Log.e("Log_Names", "Error al obtener el nombre de usuario: "+e.toString());
	  	}
		return user;
    }
        
    //Este método convierte la respuesta dada por el servidor en un String
    public void getPostResponse(){
   
	  	try{
	  		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
	  	    StringBuilder sb = new StringBuilder();
	  	    String line = null;
	  	    
	  	    while ((line = reader.readLine()) != null) {
	  	    	sb.append(line + "\n");
	  	    }
	  	    
	  	    is.close();
	  	    result = sb.toString();
	  	    Log.e("getPostResponse"," result = " + sb.toString());
	  	    	  	    
	  	}catch(Exception e){
	  	    Log.e("Log_Tag", "Error convirtiendo la respuesta en String: " + e.toString());
	  	}
    }
  
    //Este método parsea los datos en JSON y los devuelve en un Array de JSON
    public JSONArray getJSONArray(){
    	
	  	try{
	  		JSONArray jArray = new JSONArray(result); 
	        return jArray;
	  	}catch(JSONException e){
	  		Log.e("Log_Tag", "Error parseando los datos " + e.toString());
	  	    return null;
	  	}		
	}
    
 }	