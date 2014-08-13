package com.rising.money.social;

import java.util.ArrayList;
import java.util.Locale;

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

//Clase que envia una invitación del usuario al email que se introduzca
public class Invitations extends AsyncTask<String, Integer, String>{

	//Variables
	private Context ctx;
	private String mensaje = "";
	private String language = "";
		
	//URLS
	private String URL_Invitation = "http://www.scores.rising.es/invitar_mobile";
	private String URL_Invitation_EN = "http://www.scores.rising.es/en/invitar_mobile";	
	
	//Clases usadas
	private HttpPostAux HPA = new HttpPostAux();
	private Configuration CONF;
		
	
	private OnInvitationOk SuccessInvitation;
	private OnInvitationFail FailInvitation;
	
	public interface OnInvitationOk{
        void onInvitationOk();
    }
	
	public interface OnInvitationFail{
		void onInvitationFail();
	}
		
	public Invitations(OnInvitationOk success, OnInvitationFail fail, Context context) {
		this.ctx = context;
		this.SuccessInvitation = success;
		this.FailInvitation = fail;
		this.CONF = new Configuration(ctx);
		language = Locale.getDefault().getDisplayLanguage();
	}
	
	private String Invitation_Status(String Id_U, String Mail){
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("usuario", Id_U));
    	params.add(new BasicNameValuePair("email1", Mail));
    	try{
	    	JSONArray jArray = null;
	    	
	    	if(language.equals("español")){
	    		jArray = HPA.getServerData(params, URL_Invitation);
        	}else{
        		jArray = HPA.getServerData(params, URL_Invitation_EN);
        	}
	    			   	
			if (jArray!=null && jArray.length() > 0){
			    
				JSONObject json_data=null;
				
				try{
					for(int i=0; i<jArray.length(); i++){ 
				    	json_data = jArray.getJSONObject(i);
				    	this.setMensaje(json_data.getString("invitation"));	
				    }
				}catch(JSONException e1){
					Log.e("JSONException Invitations_AsyncTask", "Pues eso, JSONException: " + e1.getMessage() + ", Result: " + mensaje.toString());
				}catch (ParseException e1) {
					Log.e("Parse Error Invitations_AsyncTask", e1.getMessage());
			    }	
				
			}else{	
				Log.e("JSON Invitations_AsyncTask", "ERROR");
			}
	    
	    }catch(Exception e){
	    	this.cancel(true);
	    	Log.e("Gran Try BInvitations_AsyncTask", e.getMessage());
	    }
		return "";
	}
	
	@Override
	protected String doInBackground(String... params) {
		return Invitation_Status(CONF.getUserEmail(), params[0]);
	}
	
    protected void onPostExecute(String result) {
    	
    	if(result != null){
    		if (SuccessInvitation != null) SuccessInvitation.onInvitationOk();
    	}else{
    		if (FailInvitation != null) FailInvitation.onInvitationFail();
    	}
    }

    public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}