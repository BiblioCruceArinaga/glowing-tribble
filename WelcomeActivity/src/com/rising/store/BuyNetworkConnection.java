package com.rising.store;

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

//Clase que registra la compra de partituras en la correspondiente tabla de la base de datos
public class BuyNetworkConnection extends AsyncTask<String, Integer, String>{

	//Variables	
	private String res;
	
	//URL
	private String URL_Buy = "http://www.scores.rising.es/store-buyscore"; 
	
	//Clases utilizadas
	private HttpPostAux HPA =  new HttpPostAux();
	
	private OnBuyFailed FailedBuy;
	private OnBuyCompleted SuccessedBuy;
	
	public interface OnBuyCompleted{
        void onBuyCompleted();
    }
	
	public interface OnBuyFailed{
        void onBuyFailed();
    }
	
	
	public BuyNetworkConnection(OnBuyCompleted success, OnBuyFailed fail) {
		this.SuccessedBuy = success;
		this.FailedBuy = fail;
	}
			
	private String BuyStatus(String Id_U, String Id_S, String Language){
		try{
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();  
            params.add(new BasicNameValuePair("id_u", Id_U));
			params.add(new BasicNameValuePair("id_s", Id_S));
        	params.add(new BasicNameValuePair("Lenguaje", Language)); 
        	
        	JSONArray jArray = HPA.getServerData(params, URL_Buy); 
        	   	
    		if (jArray!=null && jArray.length() > 0){
			    
    			JSONObject json_data=null;
    			
    			try{
    			    for(int i=0;i<jArray.length();i++){ 
    			    	json_data = jArray.getJSONObject(i);
        		    	res = json_data.getString("BuyStatus");
    			    }
    			}catch(JSONException e1){
    				Log.d("JSONException BuyNetwork", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + res.toString());
    				this.cancel(true);
    			}catch (ParseException e1) {
    				Log.e("Parse Error BuyNetwork", e1.getMessage());
    				this.cancel(true);
    		    }	
    			
    		}else{	
    			Log.e("JSON BuyNetwork", "ERROR");
    		}
	    
        }catch(Exception e){
        	this.cancel(true);
        	Log.e("Gran Try BuyNetwork", e.getMessage());
        }
		return "";
	}
	
    protected String doInBackground(String... params) {
        return BuyStatus(params[0], params[1], params[2]);    	
    }

    protected void onPostExecute(String result) {
    	if(result != null){
    		if (SuccessedBuy != null) SuccessedBuy.onBuyCompleted();
    	}else{
    		if (FailedBuy != null) FailedBuy.onBuyFailed();
    	}
    }
   
    public String Resultado() {
    	return res;
    }

}