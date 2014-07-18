package com.rising.mainscreen.preferencies;

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

public class EraseAccount extends AsyncTask<String, String, Integer> {

	//  Enviar una señal cuando el proceso haya terminado
	public interface OnTaskCompleted{
        void onTaskCompleted(int details);
    }
	
	private Context ctx;
	private OnTaskCompleted listener;
	
	private HttpPostAux HPA;
	private ProgressDialog PDialog;
	private final String URL_Erase_Account = "http://scores.rising.es/eliminar-cuenta-mobile";
	
	public EraseAccount(Context ctx, OnTaskCompleted listener) {
		this.ctx = ctx;
		this.listener = listener;
	}
	
	protected void onPreExecute() {
        PDialog = new ProgressDialog(ctx);
        PDialog.setMessage("Intentando eliminar cuenta. Por favor, espera...");
        PDialog.setIndeterminate(false);
        PDialog.setCancelable(false);
        PDialog.show();
        HPA =  new HttpPostAux();
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		int status = -1;

		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		postparameters2send.add(new BasicNameValuePair("mail", params[0]));
		postparameters2send.add(new BasicNameValuePair("pass", params[1]));
		
      	JSONArray jData = HPA.getServerData(postparameters2send, URL_Erase_Account);

		if (jData!=null && jData.length() > 0) {

			JSONObject json_data;
			
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("eraseAccount");
				
				Log.e("eraseAccount","eraseAccount= " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		            

		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
	}
	
	protected void onPostExecute(Integer result) {
		Log.e("Erase Account: onPostExecute=", "" + result);
		PDialog.dismiss();

		if (result == 1) Log.e("resultado ", "ok");
		else Log.e("resultado ", "error");
		
		listener.onTaskCompleted(result);
	}

}
