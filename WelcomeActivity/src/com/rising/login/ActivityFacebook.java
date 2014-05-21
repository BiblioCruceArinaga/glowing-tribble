package com.rising.login;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;

public class ActivityFacebook {

	//Se inicializa un string con la dirección en la base de datos del archivo a consultar y la clase HttpPostAux.	
		String URL_connect = "http://www.smartscores.es/database/facebookAddUser.php";
		HttpPostAux postAux = new HttpPostAux();
		
	    //Este método valida el estado del logueo. Solamente necesita como parametros el usuario y passw
	    public boolean regStatus(String token) {
	    	int status=-1;
	    	
	    	/*Creamos un ArrayList del tipo nombre-valor para agregar los datos recibidos por los parametros 
	    	 *anteriores (Mail) y enviarlo mediante POST a nuestro sistema para relizar la validacion*/
	    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	     		
			postparameters2send.add(new BasicNameValuePair("token", token));
					  
			//Se realiza una peticion, y como respuesta se obtiene un array JSON
	      	JSONArray jData = postAux.getServerData(postparameters2send, URL_connect);

	      	//Si lo que obtuvimos y guardamos en el jData no es null
			if(jData!=null && jData.length() > 0){

				JSONObject json_data; //Se crea un objeto JSON
				try{
					json_data = jData.getJSONObject(0); //Se lee el primer segmento, en nuestro caso el único
					status = json_data.getInt("regstatus"); //Se accede al valor 
					Log.e("regisstatus","regstatus= " + status); //Se muestra por log que se obtiene
				}catch (JSONException e) {
					e.printStackTrace();
				}		            
			             
				//Aquí se valida el valor obtenido. Si es 0 será invalido, y si es 1 será valido
			    if(status == 0 || status == 2){  // [{"logstatus":"0"}] 
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
	    
		/*		CLASE ASYNCTASK
		 * 
		 * usaremos esta para poder mostrar el dialogo de progreso mientras enviamos y obtenemos los datos
		 * podria hacerse lo mismo sin usar esto pero si el tiempo de respuesta es demasiado lo que podria ocurrir    
		 * si la conexion es lenta o el servidor tarda en responder la aplicacion sera inestable.
		 * ademas observariamos el mensaje de que la app no responde.     
		 */
	    class asyncreg extends AsyncTask< String, String, String > {
	    	String res = "";	
	    	String token;
	    	
		    protected String doInBackground(String... params) {
			
		    	//Se obtiene el parametro dentro de la variable que se le pasa y lo guardamos en la variable 
		    	//creada
		    	token=params[0];

		            
		    	//Se envian y se reciben los datos, para luego analizarlos en segundo plano.
		    	if(regStatus(token) == true){  
		    		return "ok"; //login valido
		    	}else{    		
		    		return "err"; //login invalido     	          	  
		    	}   	
		    }
		    
		    protected void onPostExecute(String result) {
		    	res = result;
		    	Log.e("onPostExecute=", "" + result);
		        
		    	//Si todo ha salido bien y la variable result ha devuelto "OK" se volverá a la pantalla de Login,
		    	//si no mostrará un error
		        if(result.equals("ok")){
		        	res = "ok";
		        }else{
		        	res = "err";
		        }
		    }
	    }
}
