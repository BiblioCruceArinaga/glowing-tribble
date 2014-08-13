package com.rising.store.purchases;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.store.CustomAdapter;
import com.rising.store.MainActivityStore;
import com.rising.store.PartituraTienda;
import com.rising.store.purchases.PurchasesNetworkConnection.OnTaskCompleted;

//Clase que muestra una lista de todas las partituras que ha comprado el usuario
public class MyPurchases extends Activity{

	//Variables
	private List<PartituraTienda> infoPart = new ArrayList<PartituraTienda>();
	private ArrayList<InfoCompra> ICompra = new ArrayList<InfoCompra>();
	private ProgressDialog PDialog;
	private ActionBar ABar;
	private TextView result;
	
	//Clases usadas
	private Configuration CONF;
	private InfoBuyNetworkConnection INFO_ASYNCTASK;
	private PurchasesNetworkConnection PURCHASES_ASYNCTASK;
	
	private OnTaskCompleted Purchases = new OnTaskCompleted() {
	    public void onTaskCompleted() {     	    		
		    	
	    	infoPart = PURCHASES_ASYNCTASK.devolverPartituras();
	    	ICompra = INFO_ASYNCTASK.devolverCompra();
	    	
	    	if(infoPart.size() == 0){
	    		result.setVisibility(View.VISIBLE);
	    	}else{ 
	    	
		    	for(int i = 0; i < infoPart.size(); i++){
			    	for(int j = 0; j < ICompra.size(); j++){	
			    		if(infoPart.get(i).getId() == ICompra.get(j).getId_S()){
			    			infoPart.get(i).setComprado(true);
			    		}
			    	}
		    	}		    	
	    	}
	    	GridView GV_Purchases = (GridView) findViewById(R.id.gV_purchases);
					
	    	GV_Purchases.setAdapter(new CustomAdapter(MyPurchases.this, infoPart));	  
	    	
	    	PDialog.dismiss();
	    }	
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_purchases_mypurchaseslayout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.CONF = new Configuration(this);
		this.PURCHASES_ASYNCTASK = new PurchasesNetworkConnection(Purchases);
		this.INFO_ASYNCTASK = new InfoBuyNetworkConnection();
				
		result = (TextView)findViewById(R.id.empty_result_purchases);
		result.setVisibility(View.INVISIBLE);
		
		PDialog = new ProgressDialog(this);
		PDialog.setMessage(getString(R.string.searching));
		PDialog.setIndeterminate(false);
		PDialog.setCancelable(false);
		PDialog.show();
				
		ABar = getActionBar();
		ABar.setTitle(R.string.my_purchases);
		ABar.show();
		ABar.setDisplayHomeAsUpEnabled(true);
	
		INFO_ASYNCTASK.execute(new Configuration(this).getUserId());				
		PURCHASES_ASYNCTASK.execute(CONF.getUserId(), Locale.getDefault().getDisplayLanguage());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		Intent in = new Intent(this, MainActivityStore.class);
	    		startActivity(in);
	    		finish();
	    		return true;
	    		
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}