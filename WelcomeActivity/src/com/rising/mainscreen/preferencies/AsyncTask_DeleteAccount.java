package com.rising.mainscreen.preferencies;

import java.util.ArrayList;
import java.util.Locale;

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

//Hilo que envía los datos y recibe la repuesta del borrado de cuentas 
public class AsyncTask_DeleteAccount extends Fragment {
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
        String FId = "";
               
        Bundle args = getArguments();
        if (args  != null && args.containsKey("mail")){
        	Mail = args.getString("mail");
        	Pass = args.getString("pass");
        	FId = args.getString("fid");
        }
        
        // Create and execute the background task.
        mTask = new Task();
        mTask.execute(Mail, Pass, FId);
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
    	private String URL_Delete_Account = "http://scores.rising.es/eliminar-cuenta-mobile";
    	
    	
        @Override
        protected void onPreExecute() {
            mCallbacks.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer i) {
            mCallbacks.onPostExecute(i);
        }
        
        public int DeleteStatus(String mail, String Pass, String fid) {
        	int status=-1;
        
        	Log.i("Data", mail + ", " + Pass);
        	
			ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
			postparameters2send.add(new BasicNameValuePair("mail", mail));
			postparameters2send.add(new BasicNameValuePair("pass", Pass));
			postparameters2send.add(new BasicNameValuePair("fid", fid));
			postparameters2send.add(new BasicNameValuePair("language", Locale.getDefault().getISO3Language()));
			
			JSONArray jData = HPA.getServerData(postparameters2send, URL_Delete_Account);    		
			
			if (jData!=null && jData.length() > 0) {

				JSONObject json_data;
				
				try{
					json_data = jData.getJSONObject(0);
					status = json_data.getInt("eraseAccount");
					
					Log.e("DeleteAccount","DeleteAccount = " + status);
				}catch (JSONException e) {
					Log.e("BigTry DeleteAccount", e.getMessage());
					this.cancel(true);
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
			return DeleteStatus(params[0],params[1], params[2]);
		}
    }

    public static interface TaskCallbacks {
        void onPreExecute();
        void onPostExecute(int Result);
        void onCancelled();
    }
}