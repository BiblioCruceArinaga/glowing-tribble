package com.rising.store;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.money.MoneyActivity;
import com.rising.money.MoneyUpdateConnectionNetwork;
import com.rising.money.MoneyUpdateConnectionNetwork.OnFailUpdateMoney;
import com.rising.money.MoneyUpdateConnectionNetwork.OnSuccessUpdateMoney;
import com.rising.store.instruments.FreeFragment;
import com.rising.store.instruments.GuitarFragment;
import com.rising.store.instruments.PianoFragment;
import com.rising.store.purchases.MyPurchases;
import com.rising.store.search.SearchStoreActivity;

//Clase principal de la tienda. Sirve de contenedor para el fragment de los instrumentos
public class MainActivityStore extends FragmentActivity implements OnQueryTextListener{

	//Variables
	public Context ctx;
	private ActionBar ABar;
	public MenuItem item;	
	
	//Clases usadas
	private MoneyUpdateConnectionNetwork MONEY_ASYNCTASK;
	private Configuration CONF;
			
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
			Toast.makeText(ctx, getString(R.string.errcredit), Toast.LENGTH_LONG).show();
		}		
	};
				
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);				
		setContentView(R.layout.store_storeactivity);		
				
		ctx = this;
		this.CONF = new Configuration(this);
		
		StartMoneyUpdate(CONF.getUserEmail());
    	
		ABar = getActionBar();
    	ABar.setIcon(R.drawable.ic_menu);
    	ABar.setTitle(R.string.store);
    	ABar.setDisplayHomeAsUpEnabled(true);
    	ABar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
    	     	    	
       	ABar.addTab(ABar.newTab().setText(R.string.piano).setTabListener(new TabListener(new PianoFragment())));
    	ABar.addTab(ABar.newTab().setText(R.string.guitar).setTabListener(new TabListener(new GuitarFragment())));
    	ABar.addTab(ABar.newTab().setText(R.string.free).setTabListener(new TabListener(new FreeFragment())));
		    	
		ImageOptions();
   	}
	
	public void StartMoneyUpdate(String user){
		MONEY_ASYNCTASK = new MoneyUpdateConnectionNetwork(MoneyUpdateSuccess, MoneyUpdateFail, ctx);	
		MONEY_ASYNCTASK.execute(user);
	}
	
	@Override
	public void onBackPressed() {
	   Intent setIntent = new Intent(this, MainScreenActivity.class);
	   startActivity(setIntent);
	   finish();
	}
	
	public void ImageOptions(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.cover)
        .showImageForEmptyUri(R.drawable.cover)
        .showImageOnFail(R.drawable.cover)
        .cacheInMemory(true).considerExifParams(true)
        .displayer(new RoundedBitmapDisplayer(10)).build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.
		Builder(this).defaultDisplayImageOptions(options).build();
		ImageLoader.getInstance().init(config);
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }   
	    		
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		item = menu.findItem(R.id.money);
		String s = String.valueOf(CONF.getUserMoney());
		item.setTitle(s);
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_store, menu);
		
		//Crea el buscador
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search_store).getActionView();
	    searchView.setOnQueryTextListener(this);
	    	  	
	    //Crea y muestra el saldo del usuario
	    item = menu.findItem(R.id.money);    
	    item.setTitle("" + CONF.getUserMoney());
	    item.setIcon(R.drawable.money);
	    
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.money:
	    		if(new Login_Utils(ctx).isOnline()){
		    		Intent i = new Intent(this, MoneyActivity.class);
		    	    startActivity(i);
	    		}else{
	    			new Login_Errors(ctx).errLogin(4);
	    		}
	    	    return true;
       	    
	    	case R.id.update_store:
	    		
	    		switch(ABar.getSelectedNavigationIndex()){
	    			case 0:
	    				getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PianoFragment()).commit();
	    				break;
	    			case 1:
	    				getFragmentManager().beginTransaction().replace(R.id.fragment_container, new GuitarFragment()).commit();
	    				break;
	    			case 2:
	    				getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FreeFragment()).commit();
	    				break;
	    			default:
	    				super.onOptionsItemSelected(item);
	    		
	    		}
	    			    		
	    		return true;
	    		
	    	case R.id.my_purchases:
		    	if(new Login_Utils(ctx).isOnline()){
		    		Intent intent = new Intent(this, MyPurchases.class);
		    		startActivity(intent);
		    		finish();
		    	}else{
	    			new Login_Errors(ctx).errLogin(4);
	    		}
	    		return true;
	        
	    	case android.R.id.home:
	    		Intent in = new Intent(this, MainScreenActivity.class);
	    		startActivity(in);
	    		finish();
	    	
	    	default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/*************************Bloque de métodos de busqueda************************/
	@Override
	public boolean onQueryTextSubmit(String text) {
		if(new Login_Utils(ctx).isOnline()){	
			Intent i = new Intent(this, SearchStoreActivity.class);
			Bundle b = new Bundle();
			b.putString("SearchText", text);
			i.putExtras(b);
			startActivity(i);	
		}else{
			new Login_Errors(ctx).errLogin(4);
		}
		return false;
	}
		
	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}
	/*****************************************************************************/	
}