package com.rising.login;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.rising.conexiones.HttpPostAux;

public class FacebookMethods {
	
	//Accesos a los archivos php que se necesitarán
	private String URL_Facebook_Signup = "http://smartscores.es/database/facebookSignUser.php";
	private String URL_Check_Facebook_Data = "http://smartscores.es/database/facebookCheck.php";
	
	//Clases necesarias
	private HttpPostAux HPA = new HttpPostAux();	
	
 //Este método comprueba si existen los datos de la cuenta de Facebook en la base de datos   
 public boolean checkFacebookData(String mail){
 	
 	//Si está en la base de datos devuelve true, si no, false
 	int checkStatus=-1;
 	
 	/*Creamos un ArrayList del tipo nombre-valor para agregar los datos recibidos por los parametros 
 	 *anteriores (Mail) y enviarlo mediante POST a nuestro sistema para relizar la validacion*/
 	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
  		
		postparameters2send.add(new BasicNameValuePair("mail", mail));
		  
		//Se realiza una peticion, y como respuesta se obtiene un array JSON
   	JSONArray jData = HPA.getServerData(postparameters2send, URL_Check_Facebook_Data);

   	//Si lo que obtuvimos y guardamos en el jData no es null
		if(jData!=null && jData.length() > 0){

			JSONObject json_data; //Se crea un objeto JSON
			try{
				json_data = jData.getJSONObject(0); //Se lee el primer segmento, en nuestro caso el único
				checkStatus = json_data.getInt("checkstatus"); //Se accede al valor 
				Log.e("checkinstatus","checkstatus= " + checkStatus); //Se muestra por log que se obtiene
			}catch (JSONException e) {
				e.printStackTrace();
			}		            
		             
			//Aquí se valida el valor obtenido. Si es 0 será invalido, y si es 1 será valido
		    if(checkStatus == 0 || checkStatus == 2){  // [{"logstatus":"0"}] 
		    	Log.e("checkstatus ", "invalido");
		    	return false;
		    }else{  // [{"logstatus":"1"}]
		    	Log.e("checkstatus ", "valido");
		    	return true;
		    } 
		}else{	
			
			//Si el JSON obtenido es null se mostrará el error en Log.
			Log.e("JSON", "ERROR");
			return false;
		}
		
 }
 
 //Este método registra datos del usuario en la base de datos. 
 
 //Este método introduce los datos del usuario de Facebook en la base de datos. 
 public boolean facebookSignUp(String mail, String name){
 	
 	int SignStatus=-1;
 	
 	/*Creamos un ArrayList del tipo nombre-valor para agregar los datos recibidos por los parametros 
 	 *anteriores (Mail) y enviarlo mediante POST a nuestro sistema para relizar la validacion*/
 	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
  		
		postparameters2send.add(new BasicNameValuePair("mail", mail));
		postparameters2send.add(new BasicNameValuePair("name", name));
		
		//Se realiza una peticion, y como respuesta se obtiene un array JSON
   	JSONArray jData = HPA.getServerData(postparameters2send, URL_Facebook_Signup);

   	//Si lo que obtuvimos y guardamos en el jData no es null
		if(jData!=null && jData.length() > 0){

			JSONObject json_data; //Se crea un objeto JSON
			try{
				json_data = jData.getJSONObject(0); //Se lee el primer segmento, en nuestro caso el único
				SignStatus = json_data.getInt("regstatus"); //Se accede al valor 
				Log.e("regisstatus","regstatus= " + SignStatus); //Se muestra por log que se obtiene
			}catch (JSONException e) {
				e.printStackTrace();
			}		            
		             
			//Aquí se valida el valor obtenido. Si es 0 será invalido, y si es 1 será valido
		    if(SignStatus == 0 || SignStatus == 2){  // [{"logstatus":"0"}] 
		    	Log.e("regstatus ", "invalido");
		    	return false;
		    }else{  // [{"logstatus":"1"}]
		    	Log.e("regstatus ", "valido");
		    	return true;
		    } 
		}else{	
			
			//Si el JSON obtenido es null se mostrará el error en Log.
			Log.e("JSON", "ERROR");
			return false;
		}
 	
 }


 
}
