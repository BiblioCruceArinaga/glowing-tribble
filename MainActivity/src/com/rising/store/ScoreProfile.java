package com.rising.store;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.conexiones.HttpPostAux;
import com.rising.login.Configuration;

public class ScoreProfile extends Activity{
	
	//Declaro variables  
	Context ctx;
	Configuration conf;
	private int Id;
	private String name;
	private String author;
	private String year;
	private String instrument;
	private String description;
	private String price;
	private boolean comprado;
	private String urlD;
	
	String Id_User = "";
	String Id_Score = "";
	
	//  private String style -> Dato para el futuro. 
	//  Al final del perfil de la partitura se recomienda al usuario más del mismo estilo

	//private ShareActionProvider share;
	private DownloadScores download = new DownloadScores();
	
	String URL_Buy = "http://www.scores.rising.es/store-buyscore";
	
	private HttpPostAux HPA =  new HttpPostAux();
	
	// declare the dialog as a member field of your activity
	ProgressDialog mProgressDialog;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.score_profile_layout);
		
		ctx = getApplicationContext();
		
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(ScoreProfile.this);
		mProgressDialog.setMessage("Descargando");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		
		Bundle b = getIntent().getExtras();
		Id = b.getInt("id");
		name = b.getString("name");
		author = b.getString("author");
		year = String.valueOf(b.getInt("year"));
		instrument = b.getString("instrument");
		price = String.valueOf(b.getFloat("price"));
		description = b.getString("description");
		comprado = b.getBoolean("comprado");
		urlD = b.getString("url");
		
		ActionBar ABar = getActionBar();
    	
    	ABar.setTitle(R.string.store);
    	ABar.setIcon(R.drawable.ic_menu);
    	ABar.setDisplayHomeAsUpEnabled(true);
		
		TextView TV_Name = (TextView) findViewById(R.id.nombrePartitura_profile);
		TextView TV_Author = (TextView) findViewById(R.id.autorPartitura_profile);
		TextView TV_Year = (TextView) findViewById(R.id.anoPartitura_profile);
		TextView TV_Instrument = (TextView) findViewById(R.id.instrumentoPartitura_profile);
		Button B_Price = (Button) findViewById(R.id.comprar_profile);
		TextView TV_Description = (TextView) findViewById(R.id.tv_description_profile);
				
		//  Cambiamos el texto de los TextView por el de la partitura seleccionada 
		TV_Name.setText(name);
		TV_Author.setText(author);
		TV_Year.setText(year);
		TV_Instrument.setText(instrument);
		TV_Description.setText(description);
		
		if(comprado){
			B_Price.setText(R.string.download);
		}else{
			if(price.equals("0.0")){
				B_Price.setText(R.string.free);
			}else{
				B_Price.setText(price + "€");
			}
		}
		
		B_Price.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				conf = new Configuration(ctx);
        		Id_User = conf.getUserId();
        		Id_Score = String.valueOf(Id);

                // Lanza la descarga 
                final AsyncDownload downloadTask = new AsyncDownload(ctx);
                
                Id_User = conf.getUserId();
        		Id_Score = String.valueOf(Id);
                
        		//  Si la partitura ya está comprada lanza la descarga 
        		//  sin registrar la compra en la base de datos.
        		if(comprado){
        			     				
     				//  Hay que poner algo aquí para que cuando 
        			//  falle la aplicación no se cierre     				
     				downloadTask.execute(urlD);
     				Log.i("URL", urlD);
     				
     				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
     				    @Override
     				    public void onCancel(DialogInterface dialog) {
     				    	downloadTask.cancel(true);
     				    }
     				});
     				
        		}else{
        		
	        		//  Aquí tiene lugar la descarga y la compra, y el 
        			//  registro de la compra en la base de datos
	 				if(price.equals("0.0")){	
	 						     				
	     				//  Log.i("BuyScore", "" + new CustomAdapter(ctx, null).new 
	 					//  AsyncBuyScore().execute(Id_User, Id_Score));
		     			
	     				//  Primero usuario y luego partitura
	     				if(new AsyncBuyScore().execute(Id_User, Id_Score).equals("Valido") ){
	     					
		     				//  Hay que poner algo aquí para que cuando 
	     					//  falle la aplicación no se cierre     				
		     				downloadTask.execute(urlD);
		     				Log.i("URL", urlD);
		     				
		     				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		     				    @Override
		     				    public void onCancel(DialogInterface dialog) {
		     				    	downloadTask.cancel(true);
		     				    }
		     				});
	     				}	
	     				
	 				}else{
	 					Toast.makeText(ctx, "¡Paga!", Toast.LENGTH_LONG).show();				
	 				}
        		}
			}
        });
	} 
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_score_profile, menu); 
		//Hacer un menú con el botón de compartir en todos sitios
		
		// Para cuando tengamos la tienda de partituras web se inplementará esto, no antes
		// Set up ShareActionProvider's default share intent
	    MenuItem shareItem = menu.findItem(R.id.action_share);
	    share = (ShareActionProvider) shareItem.getActionProvider();
        share.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

        share.setShareIntent(createShareIntent());
		
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		finish();
	    		return true;
	    		
	        case R.id.action_settings:
	            Toast.makeText(getApplicationContext(), "Preferencias. Está por hacer", Toast.LENGTH_LONG).show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	//  Este método se utiliza en el menú. ShareActionProvider
	private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/plain");
        Uri uri = Uri.fromFile(getFileStreamPath("shared.png"));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TITLE, "This is an android icon");
        return shareIntent;
    }
    */
	
	class AsyncDownload extends AsyncTask<String, Integer, String>{

		private Context context;

	    public AsyncDownload(Context context) {
	        this.context = context;
	    }

	    @Override
	    protected String doInBackground(String... sUrl) {
	    	
	        // take CPU lock to prevent CPU from going off if the user 
	        // presses the power button during download
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
	        wl.acquire();

	        try {
	            InputStream input = null;
	            OutputStream output = null;
	            HttpURLConnection connection = null;
	            try {
	                URL url = new URL(sUrl[0]);
	                connection = (HttpURLConnection) url.openConnection();
	                connection.connect();

	                // expect HTTP 200 OK, so we don't mistakenly save error report 
	                // instead of the file
	                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
	                     return "Server returned HTTP " + connection.getResponseCode() 
	                    		 + " " + connection.getResponseMessage();

	                // this will be useful to display download percentage
	                // might be -1: server did not report the length
	                int fileLength = connection.getContentLength();

	                // download the file
	                input = connection.getInputStream();
	                output = new FileOutputStream(Environment.getExternalStorageDirectory() 
	                		+ "/RisingScores/scores/" + download.FileName(url));

	                byte data[] = new byte[4096];
	                long total = 0;
	                int count;
	                while ((count = input.read(data)) != -1) {
	                	
	                    // allow canceling with back button
	                    if (isCancelled())
	                        return null;
	                    total += count;
	                    
	                    // publishing the progress....
	                    if (fileLength > 0) // only if total length is known
	                        publishProgress((int) (total * 100 / fileLength));
	                    output.write(data, 0, count);
	                }
	            } catch (Exception e) {
	                return e.toString();
	            } finally {
	                try {
	                    if (output != null)
	                        output.close();
	                    if (input != null)
	                        input.close();
	                } 
	                catch (IOException ignored) { }

	                if (connection != null)
	                    connection.disconnect();
	            }
	        } finally {
	            wl.release();
	        }
	        return null;
	    }
		
		 @Override
		    protected void onPreExecute() {
		        super.onPreExecute();
		        mProgressDialog.show();
		    }

		    @Override
		    protected void onProgressUpdate(Integer... progress) {
		        super.onProgressUpdate(progress);
		        // if we get here, length is known, now set indeterminate to false
		        mProgressDialog.setIndeterminate(false);
		        mProgressDialog.setMax(100);
		        mProgressDialog.setProgress(progress[0]);
		    }

		    @Override
		    protected void onPostExecute(String result) {
		        mProgressDialog.dismiss();
		        if (result != null){
		            Toast.makeText(context,R.string.errordownload + result, Toast.LENGTH_LONG).show();
		        	Log.e("Error descarga", "Error descarga: " + result);
		        }else{ 
		            Toast.makeText(context,R.string.okdownload, Toast.LENGTH_SHORT).show();
		            Log.i("Descarga", "Archivo descargado");		            
		        }
		    }
		
		
	}
	
	class AsyncBuyScore extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			int buyStatus=-1;
	    	
	    	/*Creamos un ArrayList del tipo nombre-valor para agregar los datos recibidos por los parametros 
	    	 *anteriores (Mail y Pass) y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
	    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	     		
			postparameters2send.add(new BasicNameValuePair("id_u", params[0]));
			postparameters2send.add(new BasicNameValuePair("id_s", params[1]));
			
			//Se realiza una peticion, y como respuesta se obtiene un array JSON
	      	JSONArray jData = HPA.getServerData(postparameters2send, URL_Buy);
	      	
			//Si lo que obtuvimos  y guardamos en el jData no es null
			if (jData!=null && jData.length() > 0){

				JSONObject json_Data; //Se crea un objeto JSON
				try {
					json_Data = jData.getJSONObject(0); //Se lee el primer segmento, en nuestro caso el único
					buyStatus=json_Data.getInt("buystatus"); //Se accede al valor 
					Log.e("LoginStatus","buyStatus= "+buyStatus);//Se muestra por log que obtuvimos
				} catch (JSONException e) {
					e.printStackTrace();
				}		            
			             
				//Aquí se valida el valor obtenido. Si es 0 será invalido, y si es 1 será valido
			    if (buyStatus==0){// [{"logstatus":"0"}] 
			    	Log.e("BuyStatus ", "Invalido");
			    	
			    	return "Inval";
			    	
			    }else{// [{"logstatus":"1"}]
			    	 Log.e("BuyStatus ", "Valido");
			    	return "Val";
			    } 
			}else{	
					  
				//Si el JSON obtenido es null se mostrará el error en Log.
				Log.e("JSON", "ERROR");
				
			}
			return null;
		}
		
	}
}