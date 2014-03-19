package com.rising.login;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.conexiones.HttpPostAux;

public class ActivityOlvidarPass {

	//Se inicializa un string con la dirección en la base de datos del archivo a consultar y la clase HttpPostAux.	
	String URL_connect = "http://10.0.2.2/recuperar-clave-mobile";
	HttpPostAux postAux = new HttpPostAux();
	
    public int mailStatus(String mail) {
    	int status=-1;

    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		postparameters2send.add(new BasicNameValuePair("mail", mail));
		  
      	JSONArray jData = postAux.getServerData(postparameters2send, URL_connect);
      	
		if (jData!=null && jData.length() > 0){
			JSONObject json_data;
			
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("mailstatus");
				Log.e("mailinstatus","mailstatus= " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		            
		             
		    if (status == 1) Log.e("mailstatus ", "valido");
		    else Log.e("mailstatus ", "invalido");

		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
    } 
    
	//  Gestión de la recuperación de la contraseña
    class asyncmail extends AsyncTask< String, String, Integer > {
    	Context ctx;
    	int res = -1;	
    	String mail;
    	
    	public asyncmail(Context ctx) {
    		this.ctx = ctx;
    	}
    	
	    protected Integer doInBackground(String... params) {
	    	mail=params[0];
	    	return mailStatus(mail);
	    }
	    
	    protected void onPostExecute(Integer result) {
	    	res = result;
	    	Log.e("onPostExecute=", "" + result);
	    	
	        switch (result) {
		        case 0:
		        	Toast.makeText(ctx, R.string.err_campos_vacios, Toast.LENGTH_SHORT).show();
		        	break;
		        case 1:
		        	Toast.makeText(ctx, R.string.olvidopass_ok, Toast.LENGTH_SHORT).show();
		        	break;
		        case 2:
		        	Toast.makeText(ctx, R.string.err_olvidopass_mail, Toast.LENGTH_SHORT).show();
		        	break;
		        case 3:
		        	Toast.makeText(ctx, R.string.err_not_active, Toast.LENGTH_SHORT).show();
		        	break;
		        case 4:
		        	Toast.makeText(ctx, R.string.err_olvidopass_mail_not_sent, Toast.LENGTH_LONG).show();
		        	break;
	        	default:
	        		Toast.makeText(ctx, R.string.err_olvidopass_unknown, Toast.LENGTH_LONG).show();
	        }
	    }
    }
 
}