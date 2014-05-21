package com.rising.money;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rising.conexiones.HttpPostAux;
import com.rising.login.Configuration;

public class BuyMoneyNetworkConnection extends AsyncTask<String, Integer, Integer>{
	
	//Esta por probar. 
		
	//Comunicaci�n HTTP con el servidor
	HttpPost httppost;
	HttpClient httpcliente;
	String URL_connect = "http://www.scores.rising.es/store-buymoney";
	HttpPostAux postAux = new HttpPostAux();
	
	String Id_U;
	String PayMethod;
	String Money;
	Context context;
	Configuration conf;
	
	public interface OnBuyMoney{
        void onBuyMoney();
    }
	
	private OnBuyMoney listenerMoney;
		
	public interface OnFailBuyMoney{
		void onFailBuyMoney();
	}
	
	private OnFailBuyMoney failMoney;
	
	public BuyMoneyNetworkConnection(OnBuyMoney listener, OnFailBuyMoney failMoney, Context ctx) {
		this.context = ctx;
		this.listenerMoney = listener;
		conf = new Configuration(ctx);
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		//Hará falta la id del usuario, el método de pago y la cantidad comprada
		Id_U = conf.getUserId();
		PayMethod = params[1];
		Money = params[2];
		int status = -1;
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
 		
		postparameters2send.add(new BasicNameValuePair("id_u", Id_U));
		postparameters2send.add(new BasicNameValuePair("paymethod", PayMethod));
		postparameters2send.add(new BasicNameValuePair("money", Money));
				
		JSONArray jData = postAux.getServerData(postparameters2send, URL_connect);
		
		if(jData!=null && jData.length() > 0){

			JSONObject json_data;
			try{
				json_data = jData.getJSONObject(0);
				status = json_data.getInt("regstatus"); 
				Log.e("regisstatus","regstatus= " + status);
			}catch (JSONException e) {
				e.printStackTrace();
			}		            
		}else{	
			Log.e("JSON", "ERROR");
		}
		
		return status;
	}
	
	// This is called when doInBackground() is finished
    protected void onPostExecute(String result) {
    	
    	//Si es 1 está bien, si es 2 hubo un problema al registrar la compra en la base de datos. 
    	if(result.equals("1")){
    		if (listenerMoney != null) listenerMoney.onBuyMoney();
    	}else{
    		if (failMoney != null) failMoney.onFailBuyMoney();
    	}
    }
}