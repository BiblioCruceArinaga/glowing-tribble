package com.rising.store.purchases;

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

//Hilo que devuelve los datos de las partituras compradas por el usuario que se le pasa
public class PurchasesNetworkConnection extends AsyncTask<String, Integer, String>{
	
	//Variables
	private ArrayList<PartituraTienda> searchinfo = new ArrayList<PartituraTienda>();
	
	//URL
	private String URL_Purchases = "http://www.scores.rising.es/purchases";
	
	//Clases utilizadas
	private HttpPostAux HPA =  new HttpPostAux();
	
	public interface OnTaskCompleted{
        void onTaskCompleted();
    }
	
	public interface OnTaskUncompleted{
		void onTaskUncompleted();
	}
	
	private OnTaskCompleted Purchases;
	private OnTaskUncompleted NoPurchases;
	
	public PurchasesNetworkConnection(OnTaskCompleted success, OnTaskUncompleted fail) {
		this.Purchases = success;
		this.NoPurchases = fail;
	}
		
	public String Purchases_Status(String Id_U, String Language){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Id_U", Id_U));
    	params.add(new BasicNameValuePair("Lenguaje", Language));
    	
    	try{
	    	JSONArray jArray = HPA.getServerData(params, URL_Purchases); 
		   	
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
					
					Log.e("JSONException PurchasesAsynctask", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + jArray.toString());
				}catch (ParseException e1) {
			        Log.e("ParseException PurchasesAsynctask", "" + e1.getMessage());
			    }	
				
			}else{	
				Log.e("JSON PurchasesAsynctask", "ERROR");
			}
	    
	    }catch(Exception e){
	    	this.cancel(true);
	    	Log.e("Gran Try PurchasesAsynctask", e.getMessage());
	    }
		return "";
	}
		
    protected String doInBackground(String... urls) {
    	return Purchases_Status(urls[0], urls[1]);
    }
 
    protected void onPostExecute(String result) {
    	if (Purchases != null) Purchases.onTaskCompleted();
    }
    
    @Override
	protected void onCancelled() {
    	if (NoPurchases != null) NoPurchases.onTaskUncompleted();
	}

	public ArrayList<PartituraTienda> devolverPartituras() {
    	return searchinfo;
    }
}