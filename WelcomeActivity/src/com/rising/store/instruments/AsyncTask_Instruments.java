package com.rising.store.instruments;

import java.util.ArrayList;

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

public class AsyncTask_Instruments extends AsyncTask<String, Integer, String>{

	//Variables
	private String result = null;
	private int id, year;
	private String nombre, autor, instrumento, description, URL, URL_Imagen;
	private float precio;
	private boolean comprado;
	private ArrayList<PartituraTienda> resultado = new ArrayList<PartituraTienda>();
	private int instrument;
	
	//URL
	private String URL_Piano = "http://www.scores.rising.es/store-piano";
	private String URL_Guitar = "http://www.scores.rising.es/store-guitar";
	private String URL_Free = "http://www.scores.rising.es/store-free";
		
	//Clases utilizadas
	private HttpPostAux HPA =  new HttpPostAux();
			
	public interface OnTaskCompleted{
        void onTaskCompleted();
    }
	
	public interface OnTaskUncomplete{
		void onTaskUncomplete();
	}

    private OnTaskCompleted listener;
    private OnTaskUncomplete listen;
	
	public AsyncTask_Instruments(OnTaskUncomplete listen, OnTaskCompleted listener, int instrument) {
		this.listener = listener;
		this.listen = listen;
		this.instrument = instrument;
	}
			
    protected String doInBackground(String... urls) {
       
        try{
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();  
        	params.add(new BasicNameValuePair("Lenguaje", urls[0]));
        	
        	JSONArray jArray = null; 
        	
        	switch(instrument){
        		case 0:
        			jArray = HPA.getServerData(params, URL_Piano);
        			break;
        		case 1: 
        			jArray = HPA.getServerData(params, URL_Guitar);
        			break;
        		case 2:
        			jArray = HPA.getServerData(params, URL_Free);
        			break;
        		default:
        			this.cancel(true);
        	}   	
        	
    		if (jArray!=null && jArray.length() > 0){
			    
    			JSONObject json_data=null;
    			
    			try{
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
    			    	
    			        resultado.add(new PartituraTienda(id,nombre,autor,instrumento,precio, description, year, comprado, URL, URL_Imagen));
    			    }
    			}catch(JSONException e1){
    				Log.d("JSONException AsyncTask_Instrument", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + result.toString());
    				this.cancel(true);
    			}catch (ParseException e1) {
    				Log.e("Parse Error Instrument", e1.getMessage());
    				this.cancel(true);
    		    }	
    			
    		}else{	
    			Log.e("JSON_AsyncTask_Instrument", "ERROR");
    		}
	    
        }catch(Exception e){
        	this.cancel(true);
        	Log.e("Gran Try AsyncTask_Instrument", e.getMessage());
        }
      
    	return "";
    }
    
    protected void onPostExecute(String result) {
    	if (listener != null) listener.onTaskCompleted();
    }
    
    public ArrayList<PartituraTienda> devolverPartituras() {
    	return resultado;
    }
        
    @Override
	protected void onCancelled(String result) {
    	if(listen != null) listen.onTaskUncomplete();
	}    
}