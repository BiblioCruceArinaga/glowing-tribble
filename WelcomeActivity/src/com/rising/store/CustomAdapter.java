package com.rising.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;
import com.rising.money.MoneyActivity;
import com.rising.money.MoneyUpdateConnectionNetwork;
import com.rising.money.MoneyUpdateConnectionNetwork.OnFailUpdateMoney;
import com.rising.money.MoneyUpdateConnectionNetwork.OnSuccessUpdateMoney;
import com.rising.store.BuyNetworkConnection.OnBuyCompleted;
import com.rising.store.BuyNetworkConnection.OnBuyFailed;
import com.rising.store.downloads.DownloadScores;
import com.rising.store.downloads.DownloadScores.OnDownloadCompleted;
import com.rising.store.downloads.DownloadScores.OnDownloadFailed;
import com.rising.store.purchases.InfoCompra;

/**Adaptador de la vista de la tienda de partituras. Alberga toda la lógica de las descargas
 * 
 * @author Ayo
 * @version 2.0
 * 
 */
public class CustomAdapter extends BaseAdapter {

	//Variables  
	private final transient Context ctx;
	private transient ViewHolder holder;
	private final transient LayoutInflater inflater;
	private transient String idUser = "";
	private transient String idScore = "";
	private final transient List<PartituraTienda> lista;	
    private final transient List<PartituraTienda> infoPartituras;   
	private static String selectedURL = "";
	private static String imagenURL = "";
	private static int selected = -1; 
	private transient Button buyMoney;
	private transient Dialog noMoneyDialog;
	private transient AlertDialog.Builder buyDialog;
		
	//Folder
	private final transient String path = "/.RisingScores/scores/";
	
	//Clases usadas
	private final transient Configuration mCONF; 
	private transient BuyNetworkConnection mBUYASYNCTASK;	
	private transient DownloadScores mDOWNLOAD;
	private transient MoneyUpdateConnectionNetwork mMONEYASYNCTASK;
	private transient ImageLoader mIML;
	private final transient Store_Utils mUTILS;
	
	private final transient OnSuccessUpdateMoney moneyUpdateSuccess = new OnSuccessUpdateMoney(){

		@Override
		public void onSuccessUpdateMoney() {							
			mCONF.setUserMoney(mMONEYASYNCTASK.devolverDatos());
			notifyDataSetChanged();
		}
	};
	
	private final OnFailUpdateMoney moneyUpdateFail = new OnFailUpdateMoney(){

		@Override
		public void onFailUpdateMoney() {		
			mCONF.setUserMoney(mCONF.getUserMoney());
		}		
	};
		
	private OnBuyCompleted registerCompletedBuyAndDownload = new OnBuyCompleted(){

		@Override
		public void onBuyCompleted() {
			try{
				((MainActivityStore)ctx).StartMoneyUpdate(mCONF.getUserEmail());
			}catch(Exception e){
				mMONEYASYNCTASK = new MoneyUpdateConnectionNetwork(moneyUpdateSuccess, moneyUpdateFail, ctx);	
				mMONEYASYNCTASK.execute(mCONF.getUserEmail());
			}
			
			if(mBUYASYNCTASK.Resultado().equals("2")){
				Toast.makeText(ctx, R.string.win_buy, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(ctx, R.string.fail_social, Toast.LENGTH_LONG).show();
			}
												
			if(mUTILS.spaceOnDisc()){
				mDOWNLOAD = new DownloadScores(successedDownload, failedDownload, ctx);
				mDOWNLOAD.execute(selectedURL, imagenURL, lista.get(selected).getNombre() + mCONF.getUserId());
			}else{
				new AlertDialog.Builder(ctx).setMessage(ctx.getString(R.string.no_space)).show();
			}
									
			lista.get(selected).setComprado(true);
			
			buttonText(selected);
			
			notifyDataSetChanged();
		}
	};
	
	private final transient OnBuyFailed failedBuy = new OnBuyFailed(){

		@Override
		public void onBuyFailed() {
			Toast.makeText(ctx, ctx.getString(R.string.errbuy), Toast.LENGTH_LONG).show();
		}
	};
			
	private final transient OnDownloadCompleted successedDownload = new OnDownloadCompleted(){
		
		@Override
		public void onDownloadCompleted() {
						
			buttonText(selected);
			
			notifyDataSetChanged();
			
			Toast.makeText(ctx,R.string.okdownload, Toast.LENGTH_SHORT).show();            
            Log.i("Custom", "Archivo descargado");
            Log.i("Space", Environment.getExternalStorageDirectory().getFreeSpace() + "");
		}
	};
			
	private final transient OnDownloadFailed failedDownload = new OnDownloadFailed(){
		
		@Override
		public void onDownloadFailed() {

			holder.botonCompra.setText(R.string.download);	
			holder.botonCompra.refreshDrawableState();
			notifyDataSetChanged();
			Toast.makeText(ctx,R.string.errordownload, Toast.LENGTH_LONG).show();
		}
	};	
		
	
	public CustomAdapter(final Context context, final List<PartituraTienda> partituras) {
		super();
		this.ctx = context;
		this.mCONF = new Configuration(ctx);	
		this.mUTILS = new Store_Utils(ctx);
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
        private transient TextView author;
        private transient TextView title;
        private transient ImageView image;
        private transient TextView intrumento;
        private transient Button botonCompra;
        private transient Button botonInfo;
    }
		
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
						
		if (view == null) {
        	holder = new ViewHolder();
        	view = inflater.inflate(R.layout.store_gridelement, parent, false);
        	
            holder.title = (TextView) view.findViewById(R.id.nombrePartitura);
            holder.author = (TextView) view.findViewById(R.id.autorPartitura);
            holder.intrumento = (TextView) view.findViewById(R.id.instrumentoPartitura);
            holder.botonCompra = (Button) view.findViewById(R.id.comprar);
            holder.botonInfo = (Button) view.findViewById(R.id.masInfo);
            holder.image = (ImageView) view.findViewById(R.id.imagenPartitura);
        
            view.setTag(holder);
        }else{
        	holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(lista.get(position).getNombre());
        holder.author.setText(lista.get(position).getAutor());
        holder.intrumento.setText(lista.get(position).getInstrumento());
                        
        showImage(position);
                         
	    buttonText(position);
	    	     			
		holder.image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				loadImageActivity(position);
			}
			
		});
		
        holder.botonInfo.setOnClickListener(new OnClickListener(){
        	 
			@Override
			public void onClick(View v) {
				loadScoreProfileActivity(position);
			}
        	
         });
        
        holder.botonCompra.setOnClickListener(new OnClickListener(){
        	
        	 @Override
 			public void onClick(View v) {
        		accionesBotonCompra(position);
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
    
    private void buttonText(int position){
    	
    	if(lista.get(position).getComprado()){  

	    	if(mUTILS.buscarArchivos(mUTILS.FileNameString(lista.get(position).getUrl()), path)){
				holder.botonCompra.setText(R.string.open);
				holder.botonCompra.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}else{
				holder.botonCompra.setText(R.string.download);
				holder.botonCompra.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	    	}
	    }else{

	    	if(lista.get(position).getPrecio() == 0.0){	    		
	        	holder.botonCompra.setText(R.string.free);	        	
	        	holder.botonCompra.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money, 0);
	        }else{
	        	holder.botonCompra.setText(lista.get(position).getPrecio()+"");
	        	holder.botonCompra.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money, 0);
	        }
	    } 
    }
    
    private void showImage(int position){
    	mIML = ImageLoader.getInstance();
    	
    	 final DisplayImageOptions options = new DisplayImageOptions.Builder()
         .showImageOnLoading(R.drawable.cover)
         .showImageForEmptyUri(R.drawable.cover)
         .showImageOnFail(R.drawable.cover)
         .cacheInMemory(true).considerExifParams(true)
         .displayer(new RoundedBitmapDisplayer(10)).build();
                   
         mIML.displayImage(lista.get(position).getImagen(), holder.image, options, new SimpleImageLoadingListener(){
         	 boolean cacheFound;

              @Override
              public void onLoadingStarted(String url, View view) {
                  List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(url, mIML.getMemoryCache());
                  cacheFound = !memCache.isEmpty();
                  if (!cacheFound) {
                      File discCache = DiskCacheUtils.findInCache(url, mIML.getDiskCache());
                      if (discCache != null) {
                          cacheFound = discCache.exists();
                      }
                  }            	
              }
             
              @Override
              public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                  if (cacheFound) {
                      MemoryCacheUtils.removeFromCache(imageUri, mIML.getMemoryCache());
                      DiskCacheUtils.removeFromCache(imageUri, mIML.getDiskCache());

                      mIML.displayImage(imageUri, (ImageView) view, options);
                  }
                  
                  //Por si quiero que el ProgressDialog se cierre después de que se hayan cargado las imagenes
                  /*new PianoFragment().onDestroyProgress();
                  new GuitarFragment().onDestroyProgress();
                  new FreeFragment().onDestroyProgress();*/
              }
         });
    }
    
    private void confirmCompraDialog(final int position){

    	buyDialog = new AlertDialog.Builder(ctx);  
        buyDialog.setTitle(R.string.confirm_buy);  
        buyDialog.setMessage(R.string.buy_agree);            
        buyDialog.setCancelable(false);  
        buyDialog.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface BuyDialog, int id) { 
        		accionesConfirmaCompra(position, BuyDialog);
        	}
        });  
         
        buyDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface BuyDialog, int id) {  
        		BuyDialog.cancel();
            }  
        });  
    }
    
    private void loadImageActivity(int position){
    	if(new Login_Utils(ctx).isOnline()){
			Intent i = new Intent(ctx, ImageActivity.class);
			i.putExtra("imagen", lista.get(position).getImagen());
			Log.i("Imagen", "" + lista.get(position).getImagen());
			ctx.startActivity(i);
		}else{
			new Login_Errors(ctx).errLogin(4);
		}
    } 

    private void loadScoreProfileActivity(int position){
    	if(new Login_Utils(ctx).isOnline()){
			
			if(lista.get(position).getComprado()){
				new InfoCompra(lista.get(position).getId()).setComprado(true);
			}else{
				new InfoCompra(lista.get(position).getId()).setComprado(false);
			}
			
			Intent i = new Intent(ctx, ScoreProfile.class);
			i.putExtra("id", lista.get(position).getId());
			i.putExtra("name", lista.get(position).getNombre());
			i.putExtra("year", lista.get(position).getYear());
			i.putExtra("author", lista.get(position).getAutor());
			i.putExtra("instrument", lista.get(position).getInstrumento());
			i.putExtra("price", lista.get(position).getPrecio());
			i.putExtra("description", lista.get(position).getDescription());
			i.putExtra("url", lista.get(position).getUrl());
			i.putExtra("url_imagen", lista.get(position).getImagen());
			Log.i("Datos", "Id: " + lista.get(position).getId() + ", name: " + lista.get(position).getNombre());
			ctx.startActivity(i);
		}else{
			new Login_Errors(ctx).errLogin(4);
		}
    }

    private void accionesBotonCompra(int position){
    	
    	idUser = mCONF.getUserId();
		idScore = String.valueOf(lista.get(position).getId());
		selected = position;
				
		confirmCompraDialog(position);	
		
		if(lista.get(position).getComprado()){
			
			if(mUTILS.buscarArchivos(mUTILS.FileNameString(lista.get(position).getUrl()), path)){ 
				mUTILS.AbrirFichero(lista.get(position).getNombre(), mUTILS.FileNameString(lista.get(position).getUrl()));	
			}else{
				if(new Login_Utils(ctx).isOnline()){
    				if(mUTILS.spaceOnDisc()){
    					mDOWNLOAD = new DownloadScores(successedDownload, failedDownload, ctx);
     					mDOWNLOAD.execute(lista.get(position).getUrl(), lista.get(position).getImagen(), 
     							String.valueOf(lista.get(position).getNombre())+mCONF.getUserId());
    				}else{
    					new AlertDialog.Builder(ctx).setMessage(ctx.getString(R.string.no_space)).show();
    				}
				}else{
					new Login_Errors(ctx).errLogin(4);
				}
			}
		}else{
    			
			if(new Login_Utils(ctx).isOnline()){
				buyDialog.show();
   			}else{
				new Login_Errors(ctx).errLogin(4);
			}	
		}
    }

    private void accionesConfirmaCompra(int position, DialogInterface BuyDialog){
    	    	
    	if(new Login_Utils(ctx).isOnline()){
        	selectedURL = lista.get(position).getUrl();
			imagenURL = lista.get(position).getImagen();
			
				if(lista.get(position).getPrecio() == 0.0){	
					mBUYASYNCTASK = new BuyNetworkConnection(registerCompletedBuyAndDownload, failedBuy);					     							     							     					
	 				mBUYASYNCTASK.execute(idUser, idScore, Locale.getDefault().getISO3Language());
	 				
	 				BuyDialog.dismiss();
							     						     								     							     							     				
				}else{
								 								 					
     			if(lista.get(position).getPrecio() < mCONF.getUserMoney()){		 					
     				mBUYASYNCTASK = new BuyNetworkConnection(registerCompletedBuyAndDownload, failedBuy);   								     				
	     			mBUYASYNCTASK.execute(idUser, idScore, Locale.getDefault().getISO3Language());
	     			
	     			BuyDialog.dismiss();					     							     			
					}else{
						
						noMoneyDialog = new Dialog(ctx, R.style.cust_dialog);
						
						noMoneyDialog.setContentView(R.layout.store_nomoneydialog);
						noMoneyDialog.setTitle(R.string.not_enough_credit);
						
						buyMoney = (Button)noMoneyDialog.findViewById(R.id.b_buy_credit);
						
						buyMoney.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								Intent i = new Intent(ctx, MoneyActivity.class);
								ctx.startActivity(i);
							}						
						});
						
						noMoneyDialog.show();			 						
					}
			}            	
        }else{
			new Login_Errors(ctx).errLogin(4);
		}
    }

}