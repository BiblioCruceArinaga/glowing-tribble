package com.example.connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class NetworkConnectionTest extends AsyncTask<String, Integer, String> {

	//  Comunicación HTTP con el servidor
	HttpPost httppost;
	HttpClient httpclient;
	
	//  Contexto
	Context context;
	
	//  Información obtenida de la base de datos
	ArrayList<PartituraTienda> resultado = new ArrayList<PartituraTienda>();
	
	public interface OnTaskCompleted{
        void onTaskCompleted();
    }

    private OnTaskCompleted listener;
	
	public NetworkConnectionTest(OnTaskCompleted listener, Context ctx) {
		this.listener = listener;
		context = ctx;
	}
	
	// Do the long-running work in here
    protected String doInBackground(String... urls) {
        
        JSONArray jArray = null;
        String result = null;
        StringBuilder sb = null;
        InputStream is = null;
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        // Http post
        try{
        	 httpclient = new DefaultHttpClient();
             httppost = new HttpPost("http://10.0.2.2/android_api/index.php");
             httppost.setEntity(new UrlEncodedFormEntity(params));
             HttpResponse response = httpclient.execute(httppost);
             HttpEntity entity = response.getEntity();
             is = entity.getContent();
        }catch(Exception e){
        	Log.e("log_tag", "Error in http connection"+e.toString());
        }
	        
        // Convert response to string
	    try{
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	        sb = new StringBuilder();
	        sb.append(reader.readLine() + "\n");

	        String line="0";
	        while ((line = reader.readLine()) != null) {
	        	sb.append(line + "\n");
	        }
	        is.close();
	        result=sb.toString();
	    }catch(Exception e){
	        Log.e("log_tag", "Error converting result "+e.toString());
	    }

		try{

			int id;
			String nombre;
			String autor;
			String instrumento;
			float precio;

			jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
		    	
		    	id = json_data.getInt("id");
		    	nombre = json_data.getString("nombre");
		    	autor = json_data.getString("autor");
		    	instrumento = json_data.getString("instrumento");
		    	precio = (float) json_data.getDouble("precio");
		    	
		        resultado.add(new PartituraTienda(id,nombre,autor,instrumento,precio));
		    }
		}
		catch(JSONException e1){
			Toast.makeText(context, "No Data Found" ,Toast.LENGTH_LONG).show();
		} 
	    catch (ParseException e1) {
	        e1.printStackTrace();
	    }
        
		// Making HTTP Request
		try {
			HttpResponse response = httpclient.execute(httppost);
 
            // writing response to log
            Log.d("Http Response:", response.toString());
        } catch (ClientProtocolException e0) {
            // writing exception to log
            e0.printStackTrace();
        } catch (IOException e1) {
            // writing exception to log
            e1.printStackTrace();
        }
    	return "";
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
        
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(String result) {
    	if (listener != null) listener.onTaskCompleted();
    	//Toast.makeText(context, result ,Toast.LENGTH_LONG).show();
    }
    
    // Devolver la información leída de la base de datos
    public ArrayList<PartituraTienda> devolverPartituras() {
    	return resultado;
    }
}
