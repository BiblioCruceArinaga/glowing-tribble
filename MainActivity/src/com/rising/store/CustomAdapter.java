package com.rising.store;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.conexiones.HttpPostAux;
import com.rising.login.Configuration;

public class CustomAdapter extends BaseAdapter {

	//Declaro variables  
	Context ctx;
	LayoutInflater inflater;
	String Id_User = "";
	String Id_Score = "";
	
	private List<PartituraTienda> lista;
	Configuration conf;
	Dialog BDialog;
	Button Confirm_Buy, Cancel_Buy;
    private ArrayList<PartituraTienda> infoPartituras;
	ProgressDialog mProgressDialog;
	String URL_Buy = "http://www.scores.rising.es/store-buyscore";
	
	private HttpPostAux HPA =  new HttpPostAux();
    
    //Variables usadas en el hilo
	private DownloadScores download = new DownloadScores();
	
	public CustomAdapter(Context context, List<PartituraTienda> partituras) {
		ctx = context;
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		this.lista = partituras;
		this.infoPartituras = new ArrayList<PartituraTienda>();
		this.infoPartituras.addAll(partituras);
		mProgressDialog = new ProgressDialog(ctx);
	}
	
	public class ViewHolder {
        TextView Author;
        TextView Title;
        ImageView image;
        TextView intrumento;
        Button botonCompra;
        Button botonInfo;
    }
	
	@Override
	public int getCount() {
		return lista.size();
	}

	@Override
	public PartituraTienda getItem(int position) {
		return lista.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		conf = new Configuration(ctx);
		final ViewHolder holder;
		        
        // Lanza la descarga 
        final AsyncDownload downloadTask = new AsyncDownload(ctx);
        final AsyncBuyScore buyScore = new AsyncBuyScore();
		 
        if (view == null) {
        	holder = new ViewHolder();
        	view = inflater.inflate(R.layout.grid_element, parent, false);
            
            holder.Title = (TextView) view.findViewById(R.id.nombrePartitura);
            holder.Author = (TextView) view.findViewById(R.id.autorPartitura);
            holder.intrumento = (TextView) view.findViewById(R.id.instrumentoPartitura);
            holder.botonCompra = (Button) view.findViewById(R.id.comprar);
            holder.botonInfo = (Button) view.findViewById(R.id.masInfo);         
             
            view.setTag(holder);
        }else{
        	holder = (ViewHolder) view.getTag();
        }

        holder.Title.setText(lista.get(position).getNombre());
        holder.Author.setText(lista.get(position).getAutor());
        holder.intrumento.setText(lista.get(position).getInstrumento());
        
	    if(lista.get(position).getComprado()){    
	    	holder.botonCompra.setText(R.string.download);
	    }else{
	    	if(lista.get(position).getPrecio() == 0.0){
	        	holder.botonCompra.setText(R.string.free);
	        }else{
	        	holder.botonCompra.setText(lista.get(position).getPrecio() + "€");
	        }
	    }
	         
        // instantiate it within the onCreate method
	    mProgressDialog.setMessage("Descargando");
 		mProgressDialog.setIndeterminate(true);
 		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
 		mProgressDialog.setCancelable(true);
         
        holder.botonInfo.setOnClickListener(new OnClickListener(){
        	 
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx, ScoreProfile.class);
				i.putExtra("id", lista.get(position).getId());
				i.putExtra("name", lista.get(position).getNombre());
				i.putExtra("year", lista.get(position).getYear());
				i.putExtra("author", lista.get(position).getAutor());
				i.putExtra("instrument", lista.get(position).getInstrumento());
				i.putExtra("price", lista.get(position).getPrecio());
				i.putExtra("description", lista.get(position).getDescription());
				i.putExtra("url", lista.get(position).getUrl());
				i.putExtra("comprado", lista.get(position).getComprado());
				ctx.startActivity(i);
			}
        	
         });
         
        holder.botonCompra.setOnClickListener(new OnClickListener(){

        	 @Override
 			public void onClick(View v) {
 		        		 
        		Id_User = conf.getUserId();
        		Id_Score = String.valueOf(lista.get(position).getId());
        		 
        		//Si la partitura ya está comprada lanza la descarga sin registrar la compra en la base de datos.
        		if(lista.get(position).getComprado()){
        			     				
     				//Hay que poner algo aquí para que cuando falle la aplicación no se cierre     				
     				downloadTask.execute(lista.get(position).getUrl());
     				Log.i("URL", lista.get(position).getUrl());
     				
     				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
     				    @Override
     				    public void onCancel(DialogInterface dialog) {
     				    	downloadTask.cancel(true);
     				    }
     				});
     				
        		}else{
        		        			
        			//Se le pregunta al usuario si realmente desea comprar la partitura
     				BDialog = new Dialog(ctx, R.style.cust_dialog);
     				BDialog.setContentView(R.layout.buy_dialog);
     				BDialog.setTitle(R.string.confirm_buy);
     											
     				Confirm_Buy = (Button)BDialog.findViewById(R.id.b_confirm_buy);
     				Cancel_Buy = (Button)BDialog.findViewById(R.id.b_cancel_buy);
     				
     				Confirm_Buy.setOnClickListener(new OnClickListener(){
     					
						@Override
						public void onClick(View arg0) {
							
							//Aquí tiene lugar la descarga y la compra, y el registro de la compra en la base de datos
			 				if(lista.get(position).getPrecio() == 0.0){	
			 						     							     							     				
			     				//Primero usuario y luego partitura
			     				try {
									if(buyScore.execute(Id_User, Id_Score).get().equals("Val")){
										Log.i("Registro compra", "Registro compra gratis");
										BDialog.dismiss();
										
										//Hay que poner algo aquí para que cuando falle la aplicación no se cierre     				
										downloadTask.execute(lista.get(position).getUrl());
										Log.i("URL", lista.get(position).getUrl());
										
										mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
										    @Override
										    public void onCancel(DialogInterface dialog) {
										    	downloadTask.cancel(true);
										    }
										});
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								} catch (ExecutionException e) {
									e.printStackTrace();
								}	
			     				
			 				}else{
			 								 								 					
			 					Log.i("Prices", "Partitura: " + lista.get(position).getPrecio() + ", User: " + conf.getUserMoney());
			 					 			
				     			if(lista.get(position).getPrecio() < conf.getUserMoney()){		 					
			     							     								     				
					     			//Primero usuario y luego partitura
				     				try {
										if(buyScore.execute(Id_User, Id_Score).get().equals("Val")){
											Log.i("Registro compra", "Registro compra con dinero");
											BDialog.dismiss();
											
											//Hay que poner algo aquí para que cuando falle la aplicación no se cierre     				
											downloadTask.execute(lista.get(position).getUrl());
											Log.i("URL", lista.get(position).getUrl());
											
											/**
											Esto es provicional. No debe hacerse así, debe actualizarse desde la base de datos
											**/
											conf.setUserMoney(conf.getUserMoney() - lista.get(position).getPrecio());				  
											
											mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
											    @Override
											    public void onCancel(DialogInterface dialog) {
											    	downloadTask.cancel(true);
											    }
											});	
										}
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (ExecutionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
			 					}else{
			 						
			 						//Debería ser un dialog con un botón que acceda a la tienda de saldo
			 						Toast.makeText(ctx, R.string.no_money, Toast.LENGTH_LONG).show();
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
     				
        			BDialog.show();
        		}
 			}
        	 
         });
                  
         return view;
	}

	 // Método de filtrado
    public void filter(String charText){
    	
    	charText = charText.toLowerCase(Locale.getDefault());
    	
       	lista.clear();
        String Author;
        String Title;
        if(charText.length() == 0){
        	lista.addAll(infoPartituras);
        }else{
            for(PartituraTienda pt : infoPartituras){
            	Author = pt.getAutor().toLowerCase();
            	Title = pt.getNombre().toLowerCase();
                if(Author.contains(charText) || Title.contains(charText)){
                	lista.add(pt);
                }
            }
        }
        
        notifyDataSetChanged();
    }
	
	//Clase/Hilo de descarga de partituras
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
	                     return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

	                // this will be useful to display download percentage
	                // might be -1: server did not report the length
	                int fileLength = connection.getContentLength();

	                // download the file
	                input = connection.getInputStream();
	                output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/RisingScores/scores/" + download.FileName(url));

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
		            Toast.makeText(context,R.string.errordownload, Toast.LENGTH_LONG).show();
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
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
		}
		
	}
}
