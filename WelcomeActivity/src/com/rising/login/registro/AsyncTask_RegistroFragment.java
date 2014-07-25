package com.rising.login.registro;

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


public class AsyncTask_RegistroFragment extends Fragment {
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
        String Pass = "";
        String Name = "";
        String Language = "";
                
        Bundle args = getArguments();
        if (args  != null && args.containsKey("mail")){
        	Mail = args.getString("mail");
        	Pass = args.getString("pass");
        	Name = args.getString("name");
        	Language = args.getString("language");
        }
        
        // Create and execute the background task.
        mTask = new Task();
        mTask.execute(Mail, Pass, Name, Language);
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
    	private String URL_connect = "http://www.scores.rising.es/registro-mobile";
    	private String URL_connect_en = "http://www.scores.rising.es/en/registro-mobile";
    	
        @Override
        protected void onPreExecute() {
            mCallbacks.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer i) {
            mCallbacks.onPostExecute(i);
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
    			jData = HPA.getServerData(postparameters2send, URL_connect);
    		}else{
    			jData = HPA.getServerData(postparameters2send, URL_connect_en);
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
		protected void onCancelled() {
			mCallbacks.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			return Registro_Status(params[0], params[2], params[1], params[3]);	  
		}
    }

    public static interface TaskCallbacks {
        void onPreExecute();
        void onPostExecute(int Result);
        void onCancelled();
    }
}
