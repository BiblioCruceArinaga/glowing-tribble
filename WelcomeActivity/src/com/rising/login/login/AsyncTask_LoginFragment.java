package com.rising.login.login;

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

/** Clase que consulta en la base de datos si son correctos los datos que se le pasan e inicia sesión si lo son
* 
* @author Ayo
* @version 2.0
* 
*/
public class AsyncTask_LoginFragment extends Fragment {
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

        setRetainInstance(true);

        String Mail = "";
        String Pass = "";
        
        Bundle args = getArguments();
        if (args  != null && args.containsKey("mail")){
        	Mail = args.getString("mail");
        	Pass = args.getString("pass");
        }
        
        mTask = new Task();
        mTask.execute(Mail, Pass);
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
    	private String URL_connect = "http://www.scores.rising.es/login-mobile";
    	
        @Override
        protected void onPreExecute() {
            mCallbacks.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer i) {
            mCallbacks.onPostExecute(i);
        }
        
        //  Validar el estado del login
        public int LoginStatus(String username, String password) {
        	int logStatus=-1;
	        
        	try{
	        	ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();
	    		postparameters2send.add(new BasicNameValuePair("usuario", username));
	    		postparameters2send.add(new BasicNameValuePair("password", password));
	 
	          	JSONArray jData = HPA.getServerData(postparameters2send, URL_connect);
	
	    		if (jData!=null && jData.length() > 0){
	    			JSONObject json_Data;
	    			
	    			try {
	    				json_Data = jData.getJSONObject(0);
	    				logStatus=json_Data.getInt("logstatus");
	    				Log.e("LoginStatus","LogStatus= "+logStatus);
	    			} catch (JSONException e) {
	    				this.cancel(true);
	    			}		            
	    		             
	    		    if (logStatus==0) Log.e("LoginStatus ", "Invalido");
	    		    else Log.e("LoginStatus ", "Valido");
	
	    		}else{	
	    			Log.e("JSON", "ERROR");
	    		}
        	}catch(Exception e){
        		this.cancel(true);
        	}
    		return logStatus;
        }  

		@Override
		protected void onCancelled() {
			mCallbacks.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return LoginStatus(params[0],params[1]);  
		}
    }

    public static interface TaskCallbacks {
        void onPreExecute();
        void onPostExecute(int Result);
        void onCancelled();
    }

}