package com.rising.store.purchases;

//Hilo que devuelve las partituras que han sido compradas por el usuario
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

public class InfoBuyNetworkConnection extends AsyncTask<String, Integer, String>{

	//Variables
	private ArrayList<InfoCompra> S_Bought = new ArrayList<InfoCompra>();
	private int Id_S;
	
	//URL
	private String URL_Info = "http://www.scores.rising.es/store-infobuy"; 
		
	//Clases utilizadas
	private HttpPostAux HPA =  new HttpPostAux();
		
	private OnTaskNoInfo NoInfo;
	
	public interface OnTaskNoInfo{
		void onTaskNoInfo();
	}
	
	public InfoBuyNetworkConnection(OnTaskNoInfo fail) {
		this.NoInfo = fail;
	}
	
	private String InfoStatus(String Id_U){
		try{
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();  
            params.add(new BasicNameValuePair("id_u", Id_U));
        	
        	JSONArray jArray = HPA.getServerData(params, URL_Info); 
        	   	
    		if (jArray!=null && jArray.length() > 0){
			    
    			JSONObject json_data=null;
    			    			
    			try{
    				for(int i=0;i<jArray.length();i++){
    			    	json_data = jArray.getJSONObject(i);

    			    	Id_S = json_data.getInt("Id_S");
    			    	
    			    	S_Bought.add(new InfoCompra(Id_S));
    			    }
    			}catch(JSONException e1){
    				Log.d("JSONException InfoNetwork", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + Id_S);
    				this.cancel(true);
    			}catch (ParseException e1) {
    				Log.e("Parse Error InfoNetwork", e1.getMessage());
    				this.cancel(true); 
    		    }	
    			
    		}else{	
    			Log.e("JSON InfoNetwork", "ERROR");
    			this.cancel(true);
    		}
	    
        }catch(Exception e){
        	this.cancel(true);
        	Log.e("Gran Try InfoNetwork", e.getMessage());
        }
		return "";
	}
		
    protected String doInBackground(String... urls) {
    	return InfoStatus(urls[0]);
    }
       
    @Override
	protected void onCancelled() {
    	if(NoInfo != null) NoInfo.onTaskNoInfo();
    }

	public ArrayList<InfoCompra> devolverCompra() {
    	return S_Bought;
    }
    
}