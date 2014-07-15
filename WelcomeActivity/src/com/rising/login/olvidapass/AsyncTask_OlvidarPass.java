package com.rising.login.olvidapass;

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

//Clase de gestión del cambio de contraseña
public class AsyncTask_OlvidarPass extends AsyncTask< String, String, Integer >{

	//Clases que se utilizan
	private HttpPostAux postAux = new HttpPostAux();
	
	//Variables usadas
	private Context ctx;
	private ProgressDialog PDialog;
	
	//URLs
	private String URL_connect = "http://www.scores.rising.es/recuperar-clave-mobile";
	private String URL_connect_en = "http://www.scores.rising.es/en/recuperar-clave-mobile";
	
	
	public AsyncTask_OlvidarPass(Context context){
		this.ctx = context;
	}
	
    protected void onPreExecute() {
    	PDialog = new ProgressDialog(ctx);
        PDialog.setMessage(ctx.getString(R.string.auth));
        PDialog.setIndeterminate(false);
        PDialog.setCancelable(false);
        PDialog.show();
    }
	
    public int mailStatus(String mail, String language) {
    	int status=-1;

    	Log.d("Data", "Mail: " + mail + ", Language: " + language);
    	
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		postparameters2send.add(new BasicNameValuePair("mail", mail));
		JSONArray jData = null;
		
		if(language != null && language != ""){
			if(language.equals("español")){
				jData = postAux.getServerData(postparameters2send, URL_connect);
			}else{
				jData = postAux.getServerData(postparameters2send, URL_connect_en);
			}
		}
		
		if (jData!=null && jData.length() > 0){
			JSONObject json_data;
			
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("mailstatus");
				Log.e("Mailinstatus","Mailstatus = " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		            
		             
		    if (status == 1) Log.e("Mailstatus ", "valido");
		    else Log.e("Mailstatus ", "invalido");

		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
    } 
    	
    @Override
	protected Integer doInBackground(String... params) {
    	return mailStatus(params[0], params[1]);
	}
    
    protected void onPostExecute(Integer result) {
    	new OlvidaPass_Actions(ctx).errOlvidaPass(result);
    	PDialog.dismiss();
    }
     
}