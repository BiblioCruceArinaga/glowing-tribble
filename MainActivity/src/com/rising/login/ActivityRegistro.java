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

public class ActivityRegistro {

	//  Enviar una señal cuando el proceso haya terminado
	public interface OnTaskCompleted{
        void onTaskCompleted(int details);
        void onTaskFailed(int details);
    }
	
	//  Se inicializa un string con la dirección en la base de datos del archivo a consultar y la clase HttpPostAux.	
	String URL_connect = "http://10.0.2.2/registro-mobile";
	HttpPostAux postAux = new HttpPostAux();
	
    //  Este método valida el estado del logueo. Solamente necesita como parametros el usuario y passw
    public int regStatus(String mail, String name, String pass) {
    	int status=-1;

    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		
		postparameters2send.add(new BasicNameValuePair("mail", mail));
		postparameters2send.add(new BasicNameValuePair("name", name));
		postparameters2send.add(new BasicNameValuePair("pass", pass));
				  
		//  Se realiza una peticion, y como respuesta se obtiene un array JSON
      	JSONArray jData = postAux.getServerData(postparameters2send, URL_connect);

		if(jData!=null && jData.length() > 0){

			JSONObject json_data;
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("regstatus"); 
				Log.e("regisstatus","regstatus= " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		            
		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
    } 
    
    //  Gestión del proceso de registro
    class asyncreg extends AsyncTask< String, String, Integer > {
    	int res = -1;	
    	String mail;
    	String user;
    	String pass;
    	
    	private OnTaskCompleted listener;
    	public asyncreg(OnTaskCompleted listener) {
    		this.listener = listener;
    	}
    	
	    protected Integer doInBackground(String... params) {
	    	user=params[0];
	    	mail=params[1];
	    	pass=params[2];

	    	return regStatus(mail, user, pass);	
	    }
	    
	    protected void onPostExecute(Integer result) {
	    	res = result;
	    	Log.e("onPostExecute=", "" + result);
	    	
	    	if (listener != null) {
		    	switch (result) {
				    case 0:
				    	Log.e("regstatus ", "invalido");
				    	listener.onTaskFailed(result);
				    	break;
				    case 1:
				    	Log.e("regstatus ", "valido");
				    	listener.onTaskCompleted(result);
				    	break;
				    case 2:
				    	Log.e("regstatus ", "mail repetido");
				    	listener.onTaskFailed(result);
				    	break;
				    case 3:
				    	Log.e("regstatus ", "registrado, mail no enviado");
				    	listener.onTaskCompleted(result);
				    	break;
				    case 4:
				    	Log.e("regstatus", "falló el envio de datos");
				    	listener.onTaskFailed(result);
				    	break;
				    default:
				    	Log.e("regstatus ", "invalido");
				    	listener.onTaskFailed(0);
				    	break;
			    }
	    	}
	    }
    }
}
