package com.rising.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
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

import com.rising.drawing.MainActivity;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.store.BuyNetworkConnection.OnBuyCompleted;
import com.rising.store.BuyNetworkConnection.OnBuyFailed;
import com.rising.store.DownloadScores.OnDownloadCompleted;
import com.rising.store.DownloadScores.OnDownloadFailed;

public class CustomAdapter extends BaseAdapter {

	//Declaro variables  
	Context ctx;
	ViewHolder holder;
	LayoutInflater inflater;
	String Id_User = "";
	String Id_Score = "";
	String path = "/RisingScores/scores/";
	private List<PartituraTienda> lista;
	Configuration conf;
	Dialog BDialog, NMDialog;
	Button Confirm_Buy, Cancel_Buy, Buy_Money;
    private ArrayList<PartituraTienda> infoPartituras;
	ProgressDialog mProgressDialog;
	String URL_Buy = "http://www.scores.rising.es/store-buyscore";
	public static DownloadScores download;
	static String selectedURL = "";
	
	BuyNetworkConnection bnc;
   	
	private OnBuyCompleted buyComplete = new OnBuyCompleted(){

		@Override
		public void onBuyCompleted() {
			BDialog.dismiss();
				
			//Hay que poner algo aquÃ­ para que cuando falle la aplicaciÃ³n no se cierre     				
			download.execute(selectedURL);
			//Log.i("URL", lista.get(position).getUrl());
			
			/**
			Esto es provicional. No debe hacerse asÃ­, debe actualizarse desde la base de datos
			**/
			//conf.setUserMoney(conf.getUserMoney() - lista.get(position).getPrecio());
		}
		
	};
	
	private OnBuyFailed failedBuy = new OnBuyFailed(){

		@Override
		public void onBuyFailed() {
			Toast.makeText(ctx, "Falló el registro de la compra", Toast.LENGTH_LONG).show();
		}
		
	};
	
	private OnDownloadCompleted listenerDownload = new OnDownloadCompleted(){
		@Override
		public void onDownloadCompleted() {
			//Acciones a ejecutar cuando la descarga está completa
			
			holder.botonCompra.setText(R.string.open);	
		}
	};
		
	
	private OnDownloadFailed failedDownload = new OnDownloadFailed(){
		@Override
		public void onDownloadFailed() {
			//Acciones a ejecutar cuando la descarga falló
			
			//Aquí va un Dialog
			Toast.makeText(ctx, "Falló la descarga", Toast.LENGTH_LONG).show();
		}
		
	};	
		
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

	public String FileNameString(String urlComplete){
		
		String urlCompleto = urlComplete.toString();
		int position = urlCompleto.lastIndexOf('/');
		
		String name = urlCompleto.substring(position + 1, urlCompleto.length());
		
		return name;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		conf = new Configuration(ctx);
		bnc = new BuyNetworkConnection(buyComplete, failedBuy, ctx);		
		
        // Hilo que recoge datos
        //final AsyncBuyScore buyScore = new AsyncBuyScore();
		 
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
	    	if(buscarArchivos(FileNameString(lista.get(position).getUrl()))){
				holder.botonCompra.setText(R.string.open);
			}else{
				holder.botonCompra.setText(R.string.download);
	    	}
	    }else{
	    	if(lista.get(position).getPrecio() == 0.0){
	        	holder.botonCompra.setText(R.string.free);	        	
	        }else{
	        	holder.botonCompra.setText(lista.get(position).getPrecio() + "");
	        }
	    	
	    }
	         
        //ProgressDialog de la descarga
	    mProgressDialog.setMessage("Descargando");
 		mProgressDialog.setIndeterminate(true);
 		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
 		mProgressDialog.setCancelable(true);
 		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			    @Override
			    public void onCancel(DialogInterface dialog) {
			    	download.cancel(true);
			    }
			}); 
 		
 		
 		//Dialog que pregunta al usuario si quiere comprar la partitura
 		BDialog = new Dialog(ctx, R.style.cust_dialog);
 		BDialog.setContentView(R.layout.buy_dialog);
		BDialog.setTitle(R.string.confirm_buy);
										
		Confirm_Buy = (Button)BDialog.findViewById(R.id.b_confirm_buy);
		Cancel_Buy = (Button)BDialog.findViewById(R.id.b_cancel_buy);
 		
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

        	/*
        	 * -Si la partitura ya está comprada, lanza la descarga sin registrarla en la base de datos*
        	 * 		-Si la partitura ya está en el dispositivo, no la descarga, da la opción para abrirla directamente*
        	 * 		-Si la descarga falla que no se cierre la aplicación. (Para esto hay que meterle mano a las clase HTTPPost y JSON, y controlar de manera diferente los catch en estas)
        	 * 		-Cuando se ha descargado que el botón cambie de "Descargar" a "Abrir"*
        	 * -Si no está comprada, en el botón aparece el precio, y al pulsarlo se abrirá un dialog.*
        	 * 		-El dialog pregunta si desea comprar la partitura o no.* 
        	 * 			-En caso negativo se cierra el dialog*
        	 * 			-En caso afirmativo
        	 * 				-Si el precio es 0
        	 * 					-Se registra la compra
        	 * 					-Se descarga la partitura. 
        	 *				-Si el precio es menor que el dinero que tiene el usuario
        	 *					-Se registra la compra
        	 *					-Se descargar la partitura
        	 *					-Se resta al dinero que ya se tenía el precio de la partitura
        	 *				-Si el precio es mayor al que tiene un usuario 
        	 *					-Se abre un dialogo con un botón a la pantalla de compra de saldo
        	 * 
        	 * */
        	
        	
        	 @Override
 			public void onClick(View v) {
 		        		 
        		Id_User = conf.getUserId();
        		Id_Score = String.valueOf(lista.get(position).getId());
        		 
        		download = new DownloadScores(listenerDownload, failedDownload, ctx);
        		
        		//Si la partitura ya estÃ¡ comprada lanza la descarga sin registrar la compra en la base de datos.
        		if(lista.get(position).getComprado()){
        			
        			//Si la partitura ya está en el dispositivo la abre
        			if(buscarArchivos(FileNameString(lista.get(position).getUrl()))){
        				
        				//Log.i("Filename", FileNameString(lista.get(position).getUrl()) + ", Context: " + ctx);      				
        				AbrirFichero(ctx, FileNameString(lista.get(position).getUrl()));				
        			}else{
        				     				
	     				download.execute(lista.get(position).getUrl());
	     				//Log.i("URL", lista.get(position).getUrl());
	     				
	     				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	     				    @Override
	     				    public void onCancel(DialogInterface dialog) {
	     				    	download.cancel(true);
	     				    }
	     				});
        			}
        		}else{
        		        			
        			//Se le pregunta al usuario si realmente desea comprar la partitura	
        			BDialog.show();
        			
        			Confirm_Buy.setOnClickListener(new OnClickListener(){
     					
						@Override
						public void onClick(View arg0) {
									
							selectedURL = lista.get(position).getUrl();
							
							//AquÃ­ tiene lugar la descarga y la compra, y el registro de la compra en la base de datos
			 				if(lista.get(position).getPrecio() == 0.0){	
			 						     							     							     							     					
			     				bnc.execute(Id_User, Id_Score);
			     						     								     							     							     				
			 				}else{
			 								 								 					
			 					//Log.i("Prices", "Partitura: " + lista.get(position).getPrecio() + ", User: " + conf.getUserMoney());
			 					 			
				     			if(lista.get(position).getPrecio() < conf.getUserMoney()){		 					
			     							     								     				
					     			bnc.execute(Id_User, Id_Score);
					     							     			
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
                  
         return view;
	}
	
	public void AbrirFichero(Context ctx, String path){
		Intent in = new Intent(ctx, MainActivity.class);
		in.putExtra("score", path);
		
		ctx.startActivity(in);
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
	
	 // MÃ©todo de filtrado
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

}
