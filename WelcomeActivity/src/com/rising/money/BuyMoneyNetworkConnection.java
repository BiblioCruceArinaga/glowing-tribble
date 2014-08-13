package com.rising.money;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.login.Configuration;

//Clase que registra las compras de saldo
public class BuyMoneyNetworkConnection extends AsyncTask<String, Integer, Integer>{
			
	//Variables
	private Context ctx;
	
	//URL
	private String URL_connect = "http://www.scores.rising.es/store-buymoney";
	
	//Clases usadas
	private HttpPostAux HPA = new HttpPostAux();
	private Configuration CONF;
	
	private OnSuccessBuyMoney SuccessedBuyMoney;
	private OnFailBuyMoney FailedBuyMoney;
	
	public interface OnSuccessBuyMoney{
        void onSuccessBuyMoney();
    }
		
	public interface OnFailBuyMoney{
		void onFailBuyMoney();
	}
	
	
	public BuyMoneyNetworkConnection(OnSuccessBuyMoney listener, OnFailBuyMoney failMoney, Context context) {
		this.ctx = context;
		this.SuccessedBuyMoney = listener;
		this.FailedBuyMoney = failMoney;
		this.CONF = new Configuration(ctx);
	}

	private int BuyMoney_Status(String Id_U, String PayMethod, String Money, String Language){
		int status = -1;
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("id_u", Id_U));
		postparameters2send.add(new BasicNameValuePair("paymethod", PayMethod));
		postparameters2send.add(new BasicNameValuePair("money", Money));
		postparameters2send.add(new BasicNameValuePair("Lenguaje", Language));
				
		try{
		
			JSONArray jData = HPA.getServerData(postparameters2send, URL_connect);
			
			if(jData!=null && jData.length() > 0){
	
				JSONObject json_data;
				try{
					json_data = jData.getJSONObject(0);
					status = json_data.getInt("regstatus"); 
					Log.e("regisstatus","regstatus= " + status);
				}catch (JSONException e) {
					Log.e("JSONException BuyMoney", "" + e.getMessage());
				}		            
			}else{	
				Log.e("JSON BuyMoney", "ERROR");
			}
		}catch(Exception e){
			Log.e("BigTry BuyMoney", "" + e.getMessage());
		}
		
		return status;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		return BuyMoney_Status(CONF.getUserId(), params[1], params[2], params[3]);
	}

    protected void onPostExecute(String result) {
    	
    	if(result.equals("1")){
    		if (SuccessedBuyMoney != null) SuccessedBuyMoney.onSuccessBuyMoney();
    	}else{
    		if (FailedBuyMoney != null) FailedBuyMoney.onFailBuyMoney();
    	}
    }

}