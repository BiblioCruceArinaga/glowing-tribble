package com.rising.mainscreen.preferencies;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;

//Hilo que envía los datos y recibe la respuesta del cambio de contraseña
public class AsyncTask_ChangePassword extends Fragment {
    private TaskCallbacks mCallbacks;
    private Task mTask;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        String Mail = "";
        String OldPass = "";
        String NewPass = "";
               
        Bundle args = getArguments();
        if (args  != null && args.containsKey("mail")){
        	Mail = args.getString("mail");
        	OldPass = args.getString("oldpass");
        	NewPass = args.getString("newpass");
        }
        
        // Create and execute the background task.
        mTask = new Task();
        mTask.execute(Mail, OldPass, NewPass);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
 
    private class Task extends AsyncTask<String, String, Integer> {
      	
    	//Clases utilizadas
    	private HttpPostAux HPA =  new HttpPostAux();
    	
    	//URLs
    	private final String URL_Password_Change = "http://scores.rising.es/cambiar-clave-mobile";
    	
    	
        @Override
        protected void onPreExecute() {
            mCallbacks.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer i) {
            mCallbacks.onPostExecute(i);
        }
        
        public int ChangeStatus(String mail, String OldPass, String NewPass) {
        	int status=-1;
        
			ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
			postparameters2send.add(new BasicNameValuePair("mail", mail));
			postparameters2send.add(new BasicNameValuePair("passOld", OldPass));
			postparameters2send.add(new BasicNameValuePair("passNew", NewPass));
			JSONArray jData = null;

			jData = HPA.getServerData(postparameters2send, URL_Password_Change);    		

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

		@Override
		protected void onCancelled() {
			mCallbacks.onPreExecute();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			return ChangeStatus(params[0],params[1], params[2]);
		}
    }

    public static interface TaskCallbacks {
        void onPreExecute();
        void onPostExecute(int Result);
        void onCancelled();
    }
}