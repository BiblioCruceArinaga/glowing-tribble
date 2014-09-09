package com.rising.store;

import java.io.File;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;
import com.rising.login.login.ProgressDialogFragment;
import com.rising.money.MoneyActivity;
import com.rising.money.MoneyUpdateConnectionNetwork;
import com.rising.money.MoneyUpdateConnectionNetwork.OnFailUpdateMoney;
import com.rising.money.MoneyUpdateConnectionNetwork.OnSuccessUpdateMoney;
import com.rising.money.SocialBonificationNetworkConnection;
import com.rising.money.SocialBonificationNetworkConnection.OnFailBonification;
import com.rising.money.SocialBonificationNetworkConnection.OnSuccessBonification;
import com.rising.store.BuyNetworkConnection.OnBuyCompleted;
import com.rising.store.BuyNetworkConnection.OnBuyFailed;
import com.rising.store.downloads.DownloadScores;
import com.rising.store.downloads.DownloadScores.OnDownloadCompleted;
import com.rising.store.downloads.DownloadScores.OnDownloadFailed;
import com.rising.store.purchases.InfoCompra;

/**Clase que muestra toda la información de la partitura seleccionada
* 
* @author Ayo
* @version 2.0
* 
*/
public class ScoreProfile extends Activity{
	
	//Variables  
	private Context ctx;
	private Bundle b;
	private String ID_BONIFICATION = "6";
	private int Id;
	private String name, author, year, instrument, description, URL, URL_Image, Id_User, Id_Score;
	private float price;
	//private boolean comprado;
	private Dialog NoMoneyDialog;
	private Button B_Price, Buy_Money;
	private ProgressDialog mProgressDialog;
	private static boolean ButtonClicked;

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
	private MoneyUpdateConnectionNetwork MONEY_ASYNCTASK;
	private Store_Utils UTILS;
	private InfoCompra INFO;
	
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
			
			INFO.setComprado(true);
						
			MONEY_ASYNCTASK = new MoneyUpdateConnectionNetwork(MoneyUpdateSuccess, MoneyUpdateFail, ctx);	
			MONEY_ASYNCTASK.execute(CONF.getUserEmail());
						
			BONIFICATION_ASYNCTASK.execute(ID_BONIFICATION);
			if(UTILS.spaceOnDisc()){
				DOWNLOAD = new DownloadScores(SuccessedDownload, FailedDownload, ctx);
				DOWNLOAD.execute(URL, URL_Image, name + CONF.getUserId());
			}else{
				new AlertDialog.Builder(ctx).setMessage(ctx.getString(R.string.no_space)).show();
			}			
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
			
			onResume();
			
			Toast.makeText(ctx,R.string.okdownload, Toast.LENGTH_SHORT).show();            
            Log.i("Custom", "Archivo descargado");
		}		
	};
		
	private OnDownloadFailed FailedDownload = new OnDownloadFailed(){
		@Override
		public void onDownloadFailed() {
			B_Price.setText(R.string.download);	
			Toast.makeText(ctx,R.string.errordownload, Toast.LENGTH_LONG).show();
		}
	};	
	
	private OnSuccessUpdateMoney MoneyUpdateSuccess = new OnSuccessUpdateMoney(){

		@Override
		public void onSuccessUpdateMoney() {							
			CONF.setUserMoney(MONEY_ASYNCTASK.devolverDatos());
			invalidateOptionsMenu();
		}
	};
	
	private OnFailUpdateMoney MoneyUpdateFail = new OnFailUpdateMoney(){

		@Override
		public void onFailUpdateMoney() {		
			CONF.setUserMoney(CONF.getUserMoney());
		}		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_scoreprofilelayout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.ctx = this;
		this.BUY_ASYNCTASK = new BuyNetworkConnection(RegisterCompletedBuyAndDownload, FailedBuy);
		this.BONIFICATION_ASYNCTASK = new SocialBonificationNetworkConnection(SuccessBonification, FailBonification, ctx);
		this.UTILS = new Store_Utils(ctx);
		this.IML = ImageLoader.getInstance();
						
		mProgressDialog = new ProgressDialog(ScoreProfile.this);
		mProgressDialog.setMessage(getString(R.string.downloading));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
		b = getIntent().getExtras();
		Id = b.getInt("id");	
		
		this.INFO = new InfoCompra(Id);
		
		name = b.getString("name");
		author = b.getString("author");
		year = String.valueOf(b.getInt("year"));
		instrument = b.getString("instrument");
		price = b.getFloat("price");
		description = b.getString("description");
		
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
               
		//Que al abrir las imagenes se abran desde el caché, no desde internet
		IML.displayImage(URL_Image, IV_Partitura, options, new SimpleImageLoadingListener(){
		   	 boolean cacheFound;
	
	         @Override
	         public void onLoadingStarted(String url, View view) {
	        	 if(new Login_Utils(ctx).isOnline()){
	        		 List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(url, ImageLoader.getInstance().getMemoryCache());
		             cacheFound = !memCache.isEmpty();
		             
		             if (!cacheFound) {
		            	 File discCache = DiskCacheUtils.findInCache(url, ImageLoader.getInstance().getDiskCache());
		            
		                 if (discCache != null) {
		                	 cacheFound = discCache.exists();
		                 }
		             }
	        	 }else{
	            	new Login_Errors(ctx).errLogin(4);
	            }
	         }
	
	         @Override
	         public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	           	if(new Login_Utils(ctx).isOnline()){
	           		if (cacheFound) {

	           			MemoryCacheUtils.removeFromCache(imageUri, ImageLoader.getInstance().getMemoryCache());
		                DiskCacheUtils.removeFromCache(imageUri, ImageLoader.getInstance().getDiskCache());
		               
		                ImageLoader.getInstance().displayImage(imageUri, (ImageView) view);
	           		}
	           		
		        	TV_Name.setText(name);
		        	TV_Author.setText(author);
		        	TV_Year.setText(year);
		        	TV_Instrument.setText(instrument);
		        	TV_Description.setText(description);
		        	
		        	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
			     	
			        if (dialog!=null) { 
			            dialog.dismiss();
			        }
		        	
	            }else{
	            	new Login_Errors(ctx).errLogin(4);
	            }
	         }
	    });
				
		IV_Partitura.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(new Login_Utils(ctx).isOnline()){
					Intent i = new Intent(ctx, ImageActivity.class);
					i.putExtra("imagen", URL_Image);
					ctx.startActivity(i);
				}else{
					new Login_Errors(ctx).errLogin(4);
				}
			}
			
		});
						
		final AlertDialog.Builder BuyDialog = new AlertDialog.Builder(ctx);  
        BuyDialog.setTitle(R.string.confirm_buy);  
        BuyDialog.setMessage(R.string.buy_agree);            
        BuyDialog.setCancelable(false);  
        BuyDialog.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface BuyDialog, int id) {  
            	if(new Login_Utils(ctx).isOnline()){
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
	            }else{
					new Login_Errors(ctx).errLogin(4);
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
				ButtonClicked = true;
				
				CONF = new Configuration(ctx);
        		Id_User = CONF.getUserId();
        		Id_Score = String.valueOf(Id);
                
        		if(INFO.isComprado()){
        			
        			if(UTILS.buscarArchivos(UTILS.FileNameString(URL), path)){
        				UTILS.AbrirFichero(name , UTILS.FileNameString(URL));				
        			}else{
        				if(new Login_Utils(ctx).isOnline()){	
        					DOWNLOAD = new DownloadScores(SuccessedDownload, FailedDownload, ctx);
        					DOWNLOAD.execute(URL, URL_Image, name + CONF.getUserId());
        				}else{
        					new Login_Errors(ctx).errLogin(4);
        				}
        			}  				     				
        		}else{
        			if(new Login_Utils(ctx).isOnline()){
        				BuyDialog.show();
        			}else{
    					new Login_Errors(ctx).errLogin(4);
    				}
				}
			}
        });
	
		ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
     	
        if (dialog!=null) { 
            dialog.dismiss();
        }
	} 
			
	@Override
	protected void onStart() {
		super.onStart();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("myDialog");
	    if (prev != null) {
	      	ft.remove(prev);
	    }
	    ft.addToBackStack(null);  
	            
	    ProgressDialogFragment dialog = ProgressDialogFragment.newInstance(getString(R.string.pleasewait));
	    dialog.setCancelable(false);
	    dialog.show(ft, "myDialog");	
	}

	@Override
	protected void onResume() {
		super.onResume();
	    if(INFO.isComprado()){
			if(UTILS.buscarArchivos(UTILS.FileNameString(URL), path)){
				B_Price.setText(R.string.open);
				B_Price.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}else{
				B_Price.setText(R.string.download);
				B_Price.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
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
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(ButtonClicked){
			new MainActivityStore().finish();
			Intent i = new Intent(ctx, MainActivityStore.class);
    		startActivity(i);
    		finish();
		}else{
			finish();
		}
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
				Log.d("Entró", "ButtonClicked: " + ButtonClicked);
	    		if(ButtonClicked){
	    			new MainActivityStore().finish();
	    			Intent i = new Intent(ctx, MainActivityStore.class);
		    		startActivity(i);
		    		finish();
	    		}else{
	    			finish();
	    		}
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
    }*/
}