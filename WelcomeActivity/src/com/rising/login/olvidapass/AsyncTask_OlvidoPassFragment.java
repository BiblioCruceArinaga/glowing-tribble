package com.rising.login.olvidapass;

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


public class AsyncTask_OlvidoPassFragment extends Fragment {
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
        String Language = "";
               
        Bundle args = getArguments();
        if (args  != null && args.containsKey("mail")){
        	Mail = args.getString("mail");
        	Language = args.getString("language");
        }
        
        // Create and execute the background task.
        mTask = new Task();
        mTask.execute(Mail, Language);
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
    	private String URL_connect = "http://www.scores.rising.es/recuperar-clave-mobile";
    	private String URL_connect_en = "http://www.scores.rising.es/en/recuperar-clave-mobile";
    	
    	
        @Override
        protected void onPreExecute() {
            mCallbacks.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer i) {
            mCallbacks.onPostExecute(i);
        }
        
        public int MailStatus(String mail, String language) {
        	int status=-1;

        	Log.d("Data", "Mail: " + mail + ", Language: " + language);
        	
        	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
    		postparameters2send.add(new BasicNameValuePair("mail", mail));
    		JSONArray jData = null;
    		
    		if(language != null && language != ""){
    			if(language.equals("español")){
    				jData = HPA.getServerData(postparameters2send, URL_connect);
    			}else{
    				jData = HPA.getServerData(postparameters2send, URL_connect_en);
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
		protected void onCancelled() {
			mCallbacks.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return MailStatus(params[0],params[1]);  
		}
    }

    public static interface TaskCallbacks {
        void onPreExecute();
        void onPostExecute(int Result);
        void onCancelled();
    }
}