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

public class SendFeedback extends AsyncTask<String, String, Integer> {

	//  Enviar una señal cuando el proceso haya terminado
	public interface OnSendingFeedback {
        void onFeedbackSent(int details);
    }
	
	private Context ctx;
	private OnSendingFeedback listener;
	
	private HttpPostAux HPA =  new HttpPostAux();
	private ProgressDialog PDialog;
	private final String URL_Send_Feedback = "http://scores.rising.es/enviar-feedback-mobile";
	
	public SendFeedback(Context ctx, OnSendingFeedback listener) {
		this.ctx = ctx;
		this.listener = listener;
	}
	
	protected void onPreExecute() {
        PDialog = new ProgressDialog(ctx);
        PDialog.setMessage("Enviando feedback...");
        PDialog.setIndeterminate(false);
        PDialog.setCancelable(false);
        PDialog.show();
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		int status = -1;

		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		postparameters2send.add(new BasicNameValuePair("mail", params[0]));
		postparameters2send.add(new BasicNameValuePair("message", params[1]));
		
      	JSONArray jData = HPA.getServerData(postparameters2send, URL_Send_Feedback);

		if (jData!=null && jData.length() > 0) {

			JSONObject json_data;
			
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("feedbackSent");
				
				Log.e("feedbackSent","feedbackSent= " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		            

		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
	}

	protected void onPostExecute(Integer result) {
		Log.e("Feedback Sent: onPostExecute=", "" + result);
		PDialog.dismiss();

		if (result == 1) Log.e("resultado ", "ok");
		else Log.e("resultado ", "error");
		
		listener.onFeedbackSent(result);
	}
}
