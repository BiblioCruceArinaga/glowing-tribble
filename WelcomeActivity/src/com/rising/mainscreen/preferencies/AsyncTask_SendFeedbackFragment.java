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

public class AsyncTask_SendFeedbackFragment extends Fragment {
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
        String Mensaje = "";
               
        Bundle args = getArguments();
        if (args  != null && args.containsKey("mail")){
        	Mail = args.getString("mail");
        	Mensaje = args.getString("mensaje");
        }
        
        // Create and execute the background task.
        mTask = new Task();
        mTask.execute(Mail, Mensaje);
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
    	private String URL_Send_Feedback = "http://scores.rising.es/enviar-feedback-mobile";
    	
    	
        @Override
        protected void onPreExecute() {
            mCallbacks.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer i) {
            mCallbacks.onPostExecute(i);
        }
        
        public int FeedStatus(String mail, String Mensaje) {
        	int status=-1;
        
			ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
			postparameters2send.add(new BasicNameValuePair("mail", mail));
			postparameters2send.add(new BasicNameValuePair("message", Mensaje));
			JSONArray jData = null;

			jData = HPA.getServerData(postparameters2send, URL_Send_Feedback);    		

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

		@Override
		protected void onCancelled() {
			mCallbacks.onPreExecute();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			return FeedStatus(params[0],params[1]);
		}
    }

    public static interface TaskCallbacks {
        void onPreExecute();
        void onPostExecute(int Result);
        void onCancelled();
    }
}