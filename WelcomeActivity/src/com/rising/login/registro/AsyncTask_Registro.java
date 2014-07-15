package com.rising.login.registro;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.drawing.R;

//Clase de gestión del Registro
public class AsyncTask_Registro extends AsyncTask< String, String, Integer >{

	private ProgressDialog PDialog;
	private Context ctx;
	
	//Clases usadas
	private HttpPostAux postAux = new HttpPostAux();
	
	// URLs	
	private String URL_connect = "http://www.scores.rising.es/registro-mobile";
	private String URL_connect_en = "http://www.scores.rising.es/en/registro-mobile";
	
	//  Enviar una señal cuando el proceso haya terminado
	public interface OnTaskCompleted{
        void onTaskCompleted(int details);
        void onTaskFailed(int details);
    }
	
	private OnTaskCompleted listener;
		
	public AsyncTask_Registro(Context context, OnTaskCompleted listener){
		this.ctx = context;
		this.listener = listener;
	}
	
    protected void onPreExecute() {
        PDialog = new ProgressDialog(ctx);
        PDialog.setMessage(ctx.getString(R.string.creating_account));
        PDialog.setIndeterminate(false);
        PDialog.setCancelable(false);
        PDialog.show();
    }
	
    //  Este método valida el estado del logueo. Solamente necesita como parametros el usuario y passw
    public int Registro_Status(String mail, String name, String pass, String language) {
    	int status=-1;
    	
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		
		postparameters2send.add(new BasicNameValuePair("mail", mail));
		postparameters2send.add(new BasicNameValuePair("name", name));
		postparameters2send.add(new BasicNameValuePair("pass", pass));
		
		JSONArray jData;
		
		if(language.equals("español")){
			
			//  Se realiza una peticion, y como respuesta se obtiene un array JSON
			jData = postAux.getServerData(postparameters2send, URL_connect);
		}else{
			jData = postAux.getServerData(postparameters2send, URL_connect_en);
		}
		
		if(jData!=null && jData.length() > 0){

			JSONObject json_data;
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("regstatus"); 
				Log.e("RegisStatus","RegStatus= " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		            
		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
    } 
    
	@Override
	protected Integer doInBackground(String... params) {
    	return Registro_Status(params[1], params[0], params[2], params[3]);	
	}
	
    protected void onPostExecute(Integer result) {
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
    	PDialog.dismiss();
    }
	
}