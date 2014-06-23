package com.rising.store;

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
import com.rising.store.PurchasesNetworkConnection.OnTaskCompleted;

public class MyPurchases extends Activity{

	private Configuration conf;
	public CustomAdapter CAdapter;
	public List<PartituraTienda> infoPart = new ArrayList<PartituraTienda>();
	private static ArrayList<InfoCompra> ICompra = new ArrayList<InfoCompra>();
	static InfoBuyNetworkConnection ibnc;
	public PurchasesNetworkConnection pnc;
	public ProgressDialog PDialog;
	ActionBar ABar;
	TextView result;
	
	//Esto cargará todo aquello que dependa del hilo para ejecutarse, y que de no ser así no interesa que se ejecute
		private OnTaskCompleted listener = new OnTaskCompleted() {
		    public void onTaskCompleted() {     	    		
		    	
		    	infoPart = pnc.devolverPartituras();
		    	ICompra = ibnc.devolverCompra();
		    	
		    	if(infoPart.size() == 0){
		    		result.setVisibility(View.VISIBLE);
		    	}else{ 
		    	
			    	//Trozo de código dónde se ve si la partitura ha sido comprada por el usuario. En tal caso se pone a true el valor "Comprado"
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
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.my_purchases_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		conf = new Configuration(this);
				
		result = (TextView)findViewById(R.id.empty_result_purchases);
		result.setVisibility(View.INVISIBLE);
		
		PDialog = new ProgressDialog(this);
		PDialog.setMessage(getString(R.string.searching));
		PDialog.setIndeterminate(false);
		PDialog.setCancelable(false);
		PDialog.show();
				
		ABar = getActionBar();
		ABar.show();
		ABar.setDisplayHomeAsUpEnabled(true);
		        
		pnc = new PurchasesNetworkConnection(listener, this);
		ibnc = new InfoBuyNetworkConnection(this);
				
		ibnc.execute(new Configuration(this).getUserId());
				
		pnc.execute(conf.getUserId(), Locale.getDefault().getDisplayLanguage());
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