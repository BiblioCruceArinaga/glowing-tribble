package com.rising.store;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.conexiones.HttpPostAux;
import com.rising.drawing.MainActivity;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.store.BuyNetworkConnection.OnBuyCompleted;
import com.rising.store.BuyNetworkConnection.OnBuyFailed;
import com.rising.store.DownloadScores.OnDownloadCompleted;
import com.rising.store.DownloadScores.OnDownloadFailed;

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
	private float price;
	private boolean comprado;
	private String urlD;
	static String selectedURL = "";
	private String path = "/RisingScores/scores/";//Implementar sistema anti piratería
	Dialog BDialog, NMDialog;
	Button Confirm_Buy, Cancel_Buy, Buy_Money;
		
	String Id_User = "";
	String Id_Score = "";
	
	//  private String style -> Dato para el futuro. Estilo musical
	//  Al final del perfil de la partitura se recomienda al usuario más del mismo estilo

	//private ShareActionProvider share;
	private DownloadScores download;
	BuyNetworkConnection bnc;
	String URL_Buy = "http://www.scores.rising.es/store-buyscore";
	
	private HttpPostAux HPA =  new HttpPostAux();
	
	// declare the dialog as a member field of your activity
	ProgressDialog mProgressDialog;
	
	private OnBuyCompleted buyComplete = new OnBuyCompleted(){

		@Override
		public void onBuyCompleted() {
			
			//Hay que poner algo aquí para que cuando falle la aplicación no se cierre     				
			download.execute(selectedURL);
			comprado = true;
		}
	};
	
	private OnBuyFailed failedBuy = new OnBuyFailed(){

		@Override
		public void onBuyFailed() {
			Toast.makeText(ctx, "Falló", Toast.LENGTH_LONG).show();
		}
		
	};
	
	private OnDownloadCompleted listenerDownload = new OnDownloadCompleted(){
		@Override
		public void onDownloadCompleted() {
			Intent i = new Intent(ScoreProfile.this, MainActivityStore.class);
			startActivity(i);
			finish();
		}
		
	};
	
	private OnDownloadFailed failedDownload = new OnDownloadFailed(){
		@Override
		public void onDownloadFailed() {
			//Acciones a ejecutar cuando la descarga fall�(Dialog o  Activity????)
		}
	};	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.score_profile_layout);
		ctx = this;
		download = new DownloadScores(listenerDownload, failedDownload, this);
		bnc = new BuyNetworkConnection(buyComplete, failedBuy, this);		
		
		mProgressDialog = new ProgressDialog(ScoreProfile.this);
		mProgressDialog.setMessage(getString(R.string.downloading));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		
		Bundle b = getIntent().getExtras();
		Id = b.getInt("id");
		name = b.getString("name");
		author = b.getString("author");
		year = String.valueOf(b.getInt("year"));
		instrument = b.getString("instrument");
		price = b.getFloat("price");
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
		//ImageView IV_Partitura = (ImageView) findViewById(R.id.imagenPartitura_profile);
				
		//  Cambiamos el texto de los TextView por el de la partitura seleccionada 
		TV_Name.setText(name);
		TV_Author.setText(author);
		TV_Year.setText(year);
		TV_Instrument.setText(instrument);
		TV_Description.setText(description);
			
		if(comprado){
			if(buscarArchivos(FileNameString(urlD))){
				B_Price.setText(R.string.open);
			}else{
				B_Price.setText(R.string.download);
	    	}
		}else{
			if(price == 0.0){
				B_Price.setText(R.string.free);
				B_Price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money_ico, 0);
			}else{
				B_Price.setText(price + "");
	        	B_Price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money_ico, 0);
			}
		}
				
		//Dialog que pregunta al usuario si quiere comprar la partitura
 		BDialog = new Dialog(this, R.style.cust_dialog);
 		BDialog.setContentView(R.layout.buy_dialog);
		BDialog.setTitle(R.string.confirm_buy);
										
		Confirm_Buy = (Button)BDialog.findViewById(R.id.b_confirm_buy);
		Cancel_Buy = (Button)BDialog.findViewById(R.id.b_cancel_buy);
			
		B_Price.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				conf = new Configuration(ctx);
        		Id_User = conf.getUserId();
        		Id_Score = String.valueOf(Id);
                
                Id_User = conf.getUserId();
        		Id_Score = String.valueOf(Id);
                
        		//  Si la partitura ya está comprada lanza la descarga 
        		//  sin registrar la compra en la base de datos.
        		if(comprado){
        			
        			//Si la partitura ya est� en el dispositivo la abre
        			if(buscarArchivos(FileNameString(urlD))){
        							
        				AbrirFichero(ctx, FileNameString(urlD));				
        			}else{
        				     				
	     				download.execute(urlD);
        			}  				
        			     				
     				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
     				    @Override
     				    public void onCancel(DialogInterface dialog) {
     				    	download.cancel(true);
     				    }
     				});
     				
        		}else{
        			
					//Se le pregunta al usuario si realmente desea comprar la partitura	
					BDialog.show();
					
					Confirm_Buy.setOnClickListener(new OnClickListener(){
							
						@Override
						public void onClick(View arg0) {
										
							selectedURL = urlD;
							
							//Aquí tiene lugar la descarga y la compra, y el registro de la compra en la base de datos
							if(price == 0.0){	
									     							     							     							     					
								bnc.execute(Id_User, Id_Score);
				 			
				 				BDialog.dismiss();
										     						     								     							     							     				
							}else{
												 								 														 			
				     			if(price < conf.getUserMoney()){		 					
				 							     								     				
					     			bnc.execute(Id_User, Id_Score);
					     			
					     			BDialog.dismiss();					     							     			
								}else{
										
									NMDialog = new Dialog(ctx, R.style.cust_dialog);
										
									NMDialog.setContentView(R.layout.no_money_dialog);
									NMDialog.setTitle(R.string.not_enough_credit);
										
									Buy_Money = (Button)NMDialog.findViewById(R.id.b_buy_credit);
										
									Buy_Money.setOnClickListener(new OnClickListener(){
				
										@Override
										public void onClick(View v) {
											/************************************************************************/
											/*======================================================================
										 			Terminar cuando se implemente el growth hacking
										  	=====================================================================*/
											/***********************************************************************/
											
										}
											
									});
										
									NMDialog.show();			 						
								}
									
							}
						}
							
						});
					
					Cancel_Buy.setOnClickListener(new OnClickListener(){
				
						@Override
						public void onClick(View v) {
							BDialog.dismiss();							
						}
							
					});
						
				}
			}
        });
	} 
	
	public String FileNameString(String urlComplete){
		
		String urlCompleto = urlComplete.toString();
		int position = urlCompleto.lastIndexOf('/');
		
		String name = urlCompleto.substring(position + 1, urlCompleto.length());
		
		return name;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_score_profile, menu); 
		
		//Hacer un menú con el botón de compartir en todos sitios
		
		// Para cuando tengamos la tienda de partituras web se inplementará esto, no antes
		// Set up ShareActionProvider's default share intent
	   /* MenuItem shareItem = menu.findItem(R.id.action_share);
	    share = (ShareActionProvider) shareItem.getActionProvider();
        share.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

        share.setShareIntent(createShareIntent());
		*/
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		finish();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	//Busca en el dispositivo archivos con el mismo nombre que el que se le pasa
	public boolean buscarArchivos(String name){
			//String[] ficheros = new MainScreenActivity().leeFicheros();
			File f = new File(Environment.getExternalStorageDirectory() + path + name);
			
			if(f.exists()){
				return true;
			}else{
				return false;
			}
		}
		
	public void AbrirFichero(Context ctx, String path){
			Intent in = new Intent(ctx, MainActivity.class);
			in.putExtra("score", path);
			
			startActivity(in);
		}
		
	/*
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