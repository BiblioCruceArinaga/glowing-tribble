package com.rising.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.money.MoneyActivity;
import com.rising.money.SocialBonificationNetworkConnection;
import com.rising.money.SocialBonificationNetworkConnection.OnBonificationDone;
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;
import com.rising.store.BuyNetworkConnection.OnBuyCompleted;
import com.rising.store.BuyNetworkConnection.OnBuyFailed;
import com.rising.store.downloads.DownloadScores;
import com.rising.store.downloads.DownloadScores.OnDownloadCompleted;
import com.rising.store.downloads.DownloadScores.OnDownloadFailed;
import com.rising.store.instruments.InstrumentFragment;

//Adaptador de la vista de la tienda de partituras. Alberga toda la lógica de las descargas
public class CustomAdapter extends BaseAdapter {

	//Variables  
	private Context ctx;
	private ViewHolder holder;
	private LayoutInflater inflater;
	private String Id_User = "";
	private String Id_Score = "";
	private List<PartituraTienda> lista;	
    private ArrayList<PartituraTienda> infoPartituras;   
	private static String selectedURL = "";
	private static String imagenURL = "";
	private static int selected = -1; 
	private String ID_BONIFICATION = "6";
	private Button Confirm_Buy, Cancel_Buy, Buy_Money;
	private Dialog BDialog, NMDialog;
		
	//Folder
	private String path = "/.RisingScores/scores/";
	
	//Clases usadas
	private Configuration CONF;
	private BuyNetworkConnection BUY_ASYNCTASK;	
	private DownloadScores DOWNLOAD;
	private SocialBonificationNetworkConnection BONIFICATION_ASYNCTASK;
	private ImageLoader IML;
	private Store_Utils UTILS;
	
	private OnBonificationDone SuccessBonification = new OnBonificationDone(){

		@Override
		public void onBonificationDone() {
			Toast.makeText(ctx, R.string.win_buy, Toast.LENGTH_LONG).show();
		}		
	};
	
	private OnFailBonification FailBonification = new OnFailBonification(){

		@Override
		public void onFailBonification() {
			Toast.makeText(ctx, R.string.fail_social, Toast.LENGTH_LONG).show();
		}		
	};
	
	private OnBuyCompleted RegisterCompletedBuyAndDownload = new OnBuyCompleted(){

		@Override
		public void onBuyCompleted() {
			((MainActivityStore) ctx).StartMoneyUpdate(CONF.getUserEmail());
			BONIFICATION_ASYNCTASK.execute(ID_BONIFICATION);
			
			if(UTILS.spaceOnDisc()){
				DOWNLOAD.execute(selectedURL, imagenURL, String.valueOf(lista.get(selected).getNombre())+CONF.getUserId());
			}else{
				new AlertDialog.Builder(ctx).setMessage(ctx.getString(R.string.no_space)).show();
			}
									
			lista.get(selected).setComprado(true);	
		}
	};
	
	private OnBuyFailed FailedBuy = new OnBuyFailed(){

		@Override
		public void onBuyFailed() {
			Toast.makeText(ctx, ctx.getString(R.string.errbuy), Toast.LENGTH_LONG).show();
		}
	};
		
	private OnDownloadCompleted SuccessedDownload = new OnDownloadCompleted(){
		
		@Override
		public void onDownloadCompleted() {

			holder.botonCompra.setText(R.string.open);
			holder.botonCompra.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			notifyDataSetChanged();				
            Toast.makeText(ctx,R.string.okdownload, Toast.LENGTH_SHORT).show();            
            Log.i("Custom", "Archivo descargado");
            Log.i("Space", Environment.getExternalStorageDirectory().getFreeSpace()+"");
		}
	};
			
	private OnDownloadFailed FailedDownload = new OnDownloadFailed(){
		
		@Override
		public void onDownloadFailed() {
			
			holder.botonCompra.setText(R.string.download);	
        	
			Toast.makeText(ctx,R.string.errordownload, Toast.LENGTH_LONG).show();
		}
	};	
		
	public CustomAdapter(Context context, List<PartituraTienda> partituras) {
		this.ctx = context;
		this.CONF = new Configuration(ctx);
		this.BUY_ASYNCTASK = new BuyNetworkConnection(RegisterCompletedBuyAndDownload, FailedBuy);	
		this.DOWNLOAD = new DownloadScores(SuccessedDownload, FailedDownload, ctx);
		this.BONIFICATION_ASYNCTASK = new SocialBonificationNetworkConnection(SuccessBonification, FailBonification, ctx);
		this.UTILS = new Store_Utils(ctx);
		this.lista = partituras;
		this.infoPartituras = new ArrayList<PartituraTienda>();
		this.infoPartituras.addAll(partituras);
		this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
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
	
	public class ViewHolder {
        TextView Author;
        TextView Title;
        ImageView image;
        TextView intrumento;
        Button botonCompra;
        Button botonInfo;
    }
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		selected = position;
		IML = ImageLoader.getInstance();
        if (view == null) {
        	holder = new ViewHolder();
        	view = inflater.inflate(R.layout.grid_element, parent, false);
        	
            holder.Title = (TextView) view.findViewById(R.id.nombrePartitura);
            holder.Author = (TextView) view.findViewById(R.id.autorPartitura);
            holder.intrumento = (TextView) view.findViewById(R.id.instrumentoPartitura);
            holder.botonCompra = (Button) view.findViewById(R.id.comprar);
            holder.botonInfo = (Button) view.findViewById(R.id.masInfo);
            holder.image = (ImageView) view.findViewById(R.id.imagenPartitura);
        
            view.setTag(holder);
        }else{
        	holder = (ViewHolder) view.getTag();
        }

        holder.Title.setText(lista.get(position).getNombre());
        holder.Author.setText(lista.get(position).getAutor());
        holder.intrumento.setText(lista.get(position).getInstrumento());
                        
         final DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.cover)
        .showImageForEmptyUri(R.drawable.cover)
        .showImageOnFail(R.drawable.cover)
        .cacheInMemory(true).considerExifParams(true)
        .displayer(new RoundedBitmapDisplayer(10)).build();
                  
        IML.displayImage(lista.get(position).getImagen(), holder.image, options, new SimpleImageLoadingListener(){
        	 boolean cacheFound;

             @Override
             public void onLoadingStarted(String url, View view) {

                 List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(url, IML.getMemoryCache());
                 cacheFound = !memCache.isEmpty();
                 if (!cacheFound) {
                     File discCache = DiskCacheUtils.findInCache(url, IML.getDiskCache());
                     if (discCache != null) {
                         cacheFound = discCache.exists();
                     }
                 }            	
             }
            
             @Override
             public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                 if (cacheFound) {
                     MemoryCacheUtils.removeFromCache(imageUri, IML.getMemoryCache());
                     DiskCacheUtils.removeFromCache(imageUri, IML.getDiskCache());

                     IML.displayImage(imageUri, (ImageView) view, options);
                 }
                 
                 new InstrumentFragment(0).onDestroyProgress();
                 new InstrumentFragment(1).onDestroyProgress();
                 new InstrumentFragment(2).onDestroyProgress();
             }
        });
                         
	    if(lista.get(position).getComprado()){    
	    	if(UTILS.buscarArchivos(UTILS.FileNameString(lista.get(position).getUrl()), path)){
				holder.botonCompra.setText(R.string.open);
			}else{
				holder.botonCompra.setText(R.string.download);
	    	}
	    }else{
	    	if(lista.get(position).getPrecio() == 0.0){
	        	holder.botonCompra.setText(R.string.free);	        	
	        	holder.botonCompra.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money_ico, 0);
	        }else{
	        	holder.botonCompra.setText(lista.get(position).getPrecio() + "");
	        	holder.botonCompra.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money_ico, 0);
	        }
	    }        
 		
 		//Dialog que pregunta al usuario si quiere comprar la partitura
 		BDialog = new Dialog(ctx, R.style.cust_dialog);
 		BDialog.setContentView(R.layout.buy_dialog);
		BDialog.setTitle(R.string.confirm_buy);
										
		Confirm_Buy = (Button)BDialog.findViewById(R.id.b_confirm_buy);
		Cancel_Buy = (Button)BDialog.findViewById(R.id.b_cancel_buy);
 			
		holder.image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx, ImageActivity.class);
				i.putExtra("imagen", lista.get(position).getImagen());
				Log.i("Imagen", "" + lista.get(position).getImagen());
				ctx.startActivity(i);
			}
			
		});
		
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
				i.putExtra("url_imagen", lista.get(position).getImagen());
				Log.i("Datos", "Id: " + lista.get(position).getId() + ", name: " + lista.get(position).getNombre());
				ctx.startActivity(i);
			}
        	
         });
        
        holder.botonCompra.setOnClickListener(new OnClickListener(){
        	
        	 @Override
 			public void onClick(View v) {
 		        		 
        		Id_User = CONF.getUserId();
        		Id_Score = String.valueOf(lista.get(position).getId());
        		        		
        		//Si la partitura ya está comprada lanza la descarga sin registrar la compra en la base de datos.
        		if(lista.get(position).getComprado()){
        			
        			//Si la partitura ya est� en el dispositivo la abre
        			if(UTILS.buscarArchivos(UTILS.FileNameString(lista.get(position).getUrl()), path)){       	
        				UTILS.AbrirFichero(lista.get(position).getNombre(), UTILS.FileNameString(lista.get(position).getUrl()));	
        			}else{
        				if(UTILS.spaceOnDisc()){
	     					DOWNLOAD.execute(lista.get(position).getUrl(), lista.get(position).getImagen(), 
	     							String.valueOf(lista.get(position).getNombre())+CONF.getUserId());
        				}else{
        					new AlertDialog.Builder(ctx).setMessage(ctx.getString(R.string.no_space)).show();
        				}
        			}
        		}else{
        		        			
        			//Se le pregunta al usuario si realmente desea comprar la partitura	
        			BDialog.show();
        			
        			Confirm_Buy.setOnClickListener(new OnClickListener(){
     					
						@Override
						public void onClick(View arg0) {
									
							selectedURL = lista.get(position).getUrl();
							imagenURL = lista.get(position).getImagen();
							
							//Aquí tiene lugar la descarga y la compra, y el registro de la compra en la base de datos
			 				if(lista.get(position).getPrecio() == 0.0){	
			 						     							     							     							     					
			     				BUY_ASYNCTASK.execute(Id_User, Id_Score);
			     				
			     				BDialog.dismiss();
			    							     						     								     							     							     				
			 				}else{
			 								 								 					
				     			if(lista.get(position).getPrecio() < CONF.getUserMoney()){		 					
			     							     								     				
					     			BUY_ASYNCTASK.execute(Id_User, Id_Score);
					     			
					     			BDialog.dismiss();					     							     			
			 					}else{
			 						
			 						NMDialog = new Dialog(ctx, R.style.cust_dialog);
			 						
			 						NMDialog.setContentView(R.layout.no_money_dialog);
			 						NMDialog.setTitle(R.string.not_enough_credit);
			 						
			 						Buy_Money = (Button)NMDialog.findViewById(R.id.b_buy_credit);
			 						
			 						Buy_Money.setOnClickListener(new OnClickListener(){

										@Override
										public void onClick(View v) {
											Intent i = new Intent(ctx, MoneyActivity.class);
											ctx.startActivity(i);
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