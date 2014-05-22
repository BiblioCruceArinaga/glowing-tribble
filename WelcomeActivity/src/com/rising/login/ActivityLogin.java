package com.rising.login;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.rising.conexiones.HttpPostAux;

public class ActivityLogin {

	private HttpPostAux HPA =  new HttpPostAux();
	
	//Esta es la ruta donde estan los archivos PHP
	private String URL_connect = "http://www.scores.rising.es/login-mobile";
	
	//Este método valida el estado del logueo. Solamente necesita los parametros Mail y Pass
    public boolean loginStatus(String username, String password) {
    	int logStatus=-1;
    	
    	/*Creamos un ArrayList del tipo nombre-valor para agregar los datos recibidos por los parametros 
    	 *anteriores (Mail y Pass) y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		
		postparameters2send.add(new BasicNameValuePair("usuario", username));
		postparameters2send.add(new BasicNameValuePair("password", password));
		
		//Se realiza una peticion, y como respuesta se obtiene un array JSON
      	JSONArray jData = HPA.getServerData(postparameters2send, URL_connect);
      	
		//Si lo que obtuvimos  y guardamos en el jData no es null
		if (jData!=null && jData.length() > 0){

			JSONObject json_Data; //Se crea un objeto JSON
			try {
				json_Data = jData.getJSONObject(0); //Se lee el primer segmento, en nuestro caso el único
				logStatus=json_Data.getInt("logstatus"); //Se accede al valor 
				Log.e("LoginStatus","LogStatus= "+logStatus);//Se muestra por log que obtuvimos
			} catch (JSONException e) {
				e.printStackTrace();
			}		            
		             
			//Aquí se valida el valor obtenido. Si es 0 será invalido, y si es 1 será valido
		    if (logStatus==0){// [{"logstatus":"0"}] 
		    	Log.e("LoginStatus ", "Invalido");
		    	return false;
		    }else{// [{"logstatus":"1"}]
		    	 Log.e("LoginStatus ", "Valido");
		    	 return true;
		    } 
		}else{	
				  
			//Si el JSON obtenido es null se mostrará el error en Log.
			Log.e("JSON", "ERROR");
			return false;
		}
    }   
	
}
