package com.rising.store;

import java.io.File;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;
import com.rising.money.SocialBonificationNetworkConnection.OnSuccessBonification;
import com.rising.store.BuyNetworkConnection.OnBuyCompleted;
import com.rising.store.BuyNetworkConnection.OnBuyFailed;
import com.rising.store.downloads.DownloadScores;
import com.rising.store.downloads.DownloadScores.OnDownloadCompleted;
import com.rising.store.downloads.DownloadScores.OnDownloadFailed;

//Clase que muestra toda la información de la partitura seleccionada
public class ScoreProfile extends Activity{
	
	//Variables  
	private Context ctx;
	private String ID_BONIFICATION = "6";
	private int Id;
	private String name, author, year, instrument, description, URL, URL_Image, Id_User, Id_Score;
	private float price;
	private boolean comprado;
	private Dialog NoMoneyDialog;
	private Button B_Price, Buy_Money;
	private ProgressDialog mProgressDialog, Image_PDialog;
	//  private String style -> Dato para el futuro. Estilo musical
	//  Al final del perfil de la partitura se recomienda al usuario más del mismo estilo
	
	//URL	
	private String path = "/.RisingScores/scores/";
	
	//Clases usadas
	private Configuration CONF;
	private ImageLoader IML;
	private DownloadScores DOWNLOAD;
	private BuyNetworkConnection BUY_ASYNCTASK;	
	private SocialBonificationNetworkConnection BONIFICATION_ASYNCTASK;
	private Store_Utils UTILS;
	
	private OnSuccessBonification SuccessBonification = new OnSuccessBonification(){

		@Override
		public void onSuccessBonification() {
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
			
			DOWNLOAD.execute(URL, URL_Image, name + CONF.getUserId());
			comprado = true;
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
			
			B_Price.setText(R.string.open);
			Toast.makeText(ctx,R.string.okdownload, Toast.LENGTH_SHORT).show();            
            Log.i("Custom", "Archivo descargado");
            
		}		
	};
	
	
	private OnDownloadFailed FailedDownload = new OnDownloadFailed(){
		@Override
		public void onDownloadFailed() {
			//Acciones a ejecutar cuando la descarga fall�(Dialog o  Activity????)
		}
	};	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_scoreprofilelayout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.ctx = this;
		this.DOWNLOAD = new DownloadScores(SuccessedDownload, FailedDownload, this);
		this.BUY_ASYNCTASK = new BuyNetworkConnection(RegisterCompletedBuyAndDownload, FailedBuy);
		this.BONIFICATION_ASYNCTASK = new SocialBonificationNetworkConnection(SuccessBonification, FailBonification, ctx);
		this.UTILS = new Store_Utils(ctx);
		this.IML = ImageLoader.getInstance();
		
		Image_PDialog = ProgressDialog.show(ctx, "", getString(R.string.pleasewait));
		
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
		URL = b.getString("url");
		URL_Image = b.getString("url_imagen");
		
		ActionBar ABar = getActionBar();
    	
    	ABar.setTitle(R.string.store);
    	ABar.setIcon(R.drawable.ic_menu);
    	ABar.setDisplayHomeAsUpEnabled(true);
		
		final TextView TV_Name = (TextView) findViewById(R.id.nombrePartitura_profile);
		final TextView TV_Author = (TextView) findViewById(R.id.autorPartitura_profile);
		final TextView TV_Year = (TextView) findViewById(R.id.anoPartitura_profile);
		final TextView TV_Instrument = (TextView) findViewById(R.id.instrumentoPartitura_profile);
		B_Price = (Button) findViewById(R.id.comprar_profile);
		final TextView TV_Description = (TextView) findViewById(R.id.tv_description_profile);
		ImageView IV_Partitura = (ImageView) findViewById(R.id.imagenPartitura_profile);
						
		TV_Name.setText("");
		TV_Author.setText("");
		TV_Year.setText("");
		TV_Instrument.setText("");
		TV_Description.setText("");
        		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.cover)
        .showImageForEmptyUri(R.drawable.cover)
        .showImageOnFail(R.drawable.cover)
        .cacheInMemory(true)
        .considerExifParams(true)
        .displayer(new RoundedBitmapDisplayer(10))
        .build();
               
		IML.displayImage(URL_Image, IV_Partitura, options, new SimpleImageLoadingListener(){
	       	 boolean cacheFound;

	            @Override
	            public void onLoadingStarted(String url, View view) {
	                List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(url, ImageLoader.getInstance().getMemoryCache());
	                cacheFound = !memCache.isEmpty();
	                if (!cacheFound) {
	                    File discCache = DiskCacheUtils.findInCache(url, ImageLoader.getInstance().getDiskCache());
	                    if (discCache != null) {
	                        cacheFound = discCache.exists();
	                    }
	                }
	            }

	            @Override
	            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	                if (cacheFound) {
	                    MemoryCacheUtils.removeFromCache(imageUri, ImageLoader.getInstance().getMemoryCache());
	                    DiskCacheUtils.removeFromCache(imageUri, ImageLoader.getInstance().getDiskCache());

	                    ImageLoader.getInstance().displayImage(imageUri, (ImageView) view);
	                }
	            //  Cambiamos el texto de los TextView por el de la partitura seleccionada 
	        		TV_Name.setText(name);
	        		TV_Author.setText(author);
	        		TV_Year.setText(year);
	        		TV_Instrument.setText(instrument);
	        		TV_Description.setText(description);
	                Image_PDialog.dismiss();
	            }
	       });
				
		IV_Partitura.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx, ImageActivity.class);
				i.putExtra("imagen", URL_Image);
				ctx.startActivity(i);
			}
			
		});
				
		if(comprado){
			if(UTILS.buscarArchivos(UTILS.FileNameString(URL), path)){
				B_Price.setText(R.string.open);
			}else{
				B_Price.setText(R.string.download);
	    	}
		}else{
			if(price == 0.0){
				B_Price.setText(R.string.free);
				B_Price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money, 0);
			}else{
				B_Price.setText(price + "");
	        	B_Price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money, 0);
			}
		}
		
		final AlertDialog.Builder BuyDialog = new AlertDialog.Builder(ctx);  
        BuyDialog.setTitle(R.string.confirm_buy);  
        BuyDialog.setMessage(R.string.buy_agree);            
        BuyDialog.setCancelable(false);  
        BuyDialog.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface BuyDialog, int id) {  
                
            	
            	if(price == 0.0){	
    					
					BUY_ASYNCTASK.execute(Id_User, Id_Score, Locale.getDefault().getDisplayLanguage());
	 			
	 				BuyDialog.dismiss();
							     						     								     							     							     				
				}else{
									 								 														 			
	     			if(price < CONF.getUserMoney()){		 					
	 							     								     				
		     			BUY_ASYNCTASK.execute(Id_User, Id_Score, Locale.getDefault().getDisplayLanguage());
		     			
		     			BuyDialog.dismiss();					     							     			
					}else{
							
						NoMoneyDialog = new Dialog(ctx, R.style.cust_dialog);
							
						NoMoneyDialog.setContentView(R.layout.store_nomoneydialog);
						NoMoneyDialog.setTitle(R.string.not_enough_credit);
							
						Buy_Money = (Button)NoMoneyDialog.findViewById(R.id.b_buy_credit);
							
						Buy_Money.setOnClickListener(new OnClickListener(){
	
							@Override
							public void onClick(View v) {
								Intent i = new Intent(ctx, MoneyActivity.class);
								startActivity(i);
							}
								
						});
							
						NoMoneyDialog.show();			 						
					}
						
				}            	
            	
            }  
        });  
        
        BuyDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface BuyDialog, int id) {  
                BuyDialog.cancel();
            }  
        });            
        
		B_Price.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				CONF = new Configuration(ctx);
        		Id_User = CONF.getUserId();
        		Id_Score = String.valueOf(Id);
                
                Id_User = CONF.getUserId();
        		Id_Score = String.valueOf(Id);
                
        		if(comprado){
        			
        			if(UTILS.buscarArchivos(UTILS.FileNameString(URL), path)){
        							
        				UTILS.AbrirFichero(UTILS.FileNameString(URL), path);				
        			}else{
        				     				
	     				DOWNLOAD.execute(URL, URL_Image, name + CONF.getUserId());
        			}  				
        			     				
     				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
     				    @Override
     				    public void onCancel(DialogInterface dialog) {
     				    	DOWNLOAD.cancel(true);
     				    }
     				});
     				
        		}else{
        				
					BuyDialog.show();
										
				}
			}
        });
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
}