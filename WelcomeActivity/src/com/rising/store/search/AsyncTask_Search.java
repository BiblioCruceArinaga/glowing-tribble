package com.rising.store.search;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.store.PartituraTienda;

//Hilo que devuelve las partituras en cuyo título o cuyo autor tenga la cadena de texto que se le pasa
public class AsyncTask_Search extends AsyncTask<String, Integer, String>{

	//Variables
	private ArrayList<PartituraTienda> searchinfo = new ArrayList<PartituraTienda>();

	//URL
	private String URL_Search = "http://www.scores.rising.es/store-search";

	//Clases usadas
	private HttpPostAux HPA =  new HttpPostAux();
	
	private OnTaskCompleted Search;
	private OnTaskFailed NoSearch;
	
	public interface OnTaskCompleted{
        void onTaskCompleted();
    }
	
	public interface OnTaskFailed{
		void onTaskFailed();
	}
	
	public AsyncTask_Search(OnTaskCompleted success, OnTaskFailed fail) {
		this.Search = success;
		this.NoSearch = fail;
	}
		
	public String Search_Status(String Word){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Lenguaje", Locale.getDefault().getDisplayLanguage()));
		params.add(new BasicNameValuePair("word", Word));
		
		try{
			JSONArray jArray = HPA.getServerData(params, URL_Search); 
			
			if (jArray!=null && jArray.length() > 0){
			    
				JSONObject json_data=null;
				    			
				try{
					
					int id, year;
					String nombre, autor, instrumento, description, URL, URL_Imagen;
					float precio;
					boolean comprado;
					
				    for(int i=0;i<jArray.length();i++){ 
				    	json_data = jArray.getJSONObject(i);
				    	id = json_data.getInt("Id_S");
				    	nombre = json_data.getString("Name_Song");
				    	autor = json_data.getString("Author");
				    	instrumento = json_data.getString("instrument");
				    	precio = (float) json_data.getDouble("Price");
				    	description = json_data.getString("Description");		    	
				    	year = json_data.getInt("Year");
				    	comprado = false;
				    	URL = json_data.getString("URL");
				    	URL_Imagen = json_data.getString("URL_Image");
				    			    	
				        searchinfo.add(new PartituraTienda(id, nombre, autor, instrumento, precio, description, year, comprado, URL, URL_Imagen));
				    }
				}catch(JSONException e1){
					Log.d("JSONException SearchAsyncTask", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + jArray.toString());
					this.cancel(true);
				}catch (ParseException e1) {
					Log.d("ParseException SearchAsyncTask", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + jArray.toString());
					this.cancel(true);
			    }	
				
			}else{	
				Log.e("JSON SearchAsyncTask", "ERROR JArray == " + jArray.toString());
				this.cancel(true);
			}
	    
	    }catch(Exception e){
	    	this.cancel(true);
	    	Log.e("Gran Try SearchAsyncTask", e.getMessage());
	    } 
		return "";
	}
	
	
    protected String doInBackground(String... urls) {	
		return Search_Status(urls[0]);
    }
  
    protected void onPostExecute(String result) {
    	if (Search != null) Search.onTaskCompleted();
    }
    
    @Override
	protected void onCancelled() {
    	if(NoSearch != null) NoSearch.onTaskFailed();
	}

	public ArrayList<PartituraTienda> devolverPartituras() {
    	return searchinfo;
    }
    
}