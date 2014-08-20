package com.rising.money;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.login.Configuration;

//Clase que devuelve el saldo actual del usuario cuyo mail se le pasa
public class MoneyUpdateConnectionNetwork extends AsyncTask<String, Integer, String>{

	//Variables
	private Context ctx;
	private double money;
	
	//URL
	private String URL_Money = "http://www.scores.rising.es/user-info";
	
	//Clases usadas
	private Configuration CONF;
	private HttpPostAux HPA = new HttpPostAux();
		
	
	private OnSuccessUpdateMoney SuccessUpdateMoney;
	private OnFailUpdateMoney FailUpdateMoney;
	
	public interface OnSuccessUpdateMoney{
        void onSuccessUpdateMoney();
    }
		
	public interface OnFailUpdateMoney{
		void onFailUpdateMoney();
	}
	
	public MoneyUpdateConnectionNetwork(OnSuccessUpdateMoney success, OnFailUpdateMoney fail, Context context) {
		this.ctx = context;
		this.SuccessUpdateMoney = success;
		this.FailUpdateMoney = fail;
		CONF = new Configuration(ctx);
	}
		
	private String MoneyUpdate_Status(String Mail){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mail", Mail));
		
		try{
	    	JSONArray jArray = HPA.getServerData(params, URL_Money); 
		   	
			if (jArray!=null && jArray.length() > 0){
			    
				JSONObject json_data=null;
				    			
				try{														
				    for(int i=0;i<jArray.length();i++){ 
				    	json_data = jArray.getJSONObject(i);
				    	this.setMoney(json_data.getDouble("Money"));		  
				    }
				}catch(JSONException e1){
					Log.e("JSONException MoneyUpdateAsynctask", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + jArray.toString());
					this.cancel(true);
				}catch (ParseException e1) {
			        Log.e("ParseException MoneyUpdateAsynctask", "" + e1.getMessage());
			        this.cancel(true);
			    }	
				
			}else{	
				Log.e("JSON MoneyUpdateAsynctask", "ERROR");
				this.cancel(true);
			}
	    
	    }catch(Exception e){
	    	this.cancel(true);
	    	Log.e("Gran Try MoneyUpdateAsynctask", e.getMessage());
	    }
		return "";
	}
	
    protected String doInBackground(String... urls) {
        return MoneyUpdate_Status(urls[0]); 
    }

    protected void onPostExecute(String result) {
    	if(result != null){
    		if (SuccessUpdateMoney != null) SuccessUpdateMoney.onSuccessUpdateMoney();
    		CONF.setUserMoney(this.getMoney());
    	}else{
    		if (FailUpdateMoney != null) FailUpdateMoney.onFailUpdateMoney();
    	}
    }
 
    public double devolverDatos() {
    	return this.getMoney();
    }
    
    @Override
	protected void onCancelled() {
		if (FailUpdateMoney != null) FailUpdateMoney.onFailUpdateMoney();
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

}