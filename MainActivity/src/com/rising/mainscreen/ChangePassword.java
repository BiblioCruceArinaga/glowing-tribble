package com.rising.mainscreen;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rising.conexiones.HttpPostAux;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ChangePassword extends AsyncTask<String, String, Integer> {

	//  Enviar una señal cuando el proceso haya terminado
	public interface OnPasswordChanging {
        void onPasswordChanged(int details);
    }
	
	private Context ctx;
	private OnPasswordChanging listener;
	
	private HttpPostAux HPA =  new HttpPostAux();
	private ProgressDialog PDialog;
	private final String URL_Password_Change = "http://scores.rising.es/cambiar-clave-mobile";
	
	public ChangePassword(Context ctx, OnPasswordChanging listener) {
		this.ctx = ctx;
		this.listener = listener;
	}
	
	protected void onPreExecute() {
        PDialog = new ProgressDialog(ctx);
        PDialog.setMessage("Intentando cambiar tu contraseña. Por favor, espera...");
        PDialog.setIndeterminate(false);
        PDialog.setCancelable(false);
        PDialog.show();
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		int status = -1;

		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		postparameters2send.add(new BasicNameValuePair("mail", params[0]));
		postparameters2send.add(new BasicNameValuePair("passOld", params[1]));
		postparameters2send.add(new BasicNameValuePair("passNew", params[2]));
		
      	JSONArray jData = HPA.getServerData(postparameters2send, URL_Password_Change);

		if (jData!=null && jData.length() > 0) {

			JSONObject json_data;
			
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("passwordChange");
				
				Log.e("passwordChange","passwordChange= " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		            

		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
	}
	
	protected void onPostExecute(Integer result) {
		Log.e("Password Changing: onPostExecute=", "" + result);
		PDialog.dismiss();

		if (result == 1) Log.e("resultado ", "ok");
		else Log.e("resultado ", "error");
		
		listener.onPasswordChanged(result);
	}

}
