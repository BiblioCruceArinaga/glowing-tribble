package com.rising.store;

import java.util.ArrayList;
import java.util.List;

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
import com.rising.store.SearchNetworkConnection.OnTaskCompleted;

public class SearchStoreActivity extends Activity{
			
	public String word; 
	public CustomAdapter CAdapter;
	public List<PartituraTienda> infoPart = new ArrayList<PartituraTienda>();
	private static ArrayList<InfoCompra> ICompra = new ArrayList<InfoCompra>();
	static InfoBuyNetworkConnection ibnc;
	public SearchNetworkConnection snc;
	public ProgressDialog PDialog;
	ActionBar ABar;
	TextView result;
	
	//Esto cargará todo aquello que dependa del hilo para ejecutarse, y que de no ser así no interesa que se ejecute
	private OnTaskCompleted listener = new OnTaskCompleted() {
	    public void onTaskCompleted() {     	    		
	    	
	    	infoPart = snc.devolverPartituras();
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
	    	GridView GV_Search = (GridView) findViewById(R.id.gV_search);
				
	    	GV_Search.setAdapter(new CustomAdapter(SearchStoreActivity.this, infoPart));	  
	    	
	    	PDialog.dismiss();
	    }	
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.searchstore_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
		//Recibo la palabra de la activity anterior
		Bundle b = this.getIntent().getExtras();
				
		//La paso a una variable String
		word = b.getString("SearchText");
		
		result = (TextView)findViewById(R.id.empty_result);
		result.setVisibility(View.INVISIBLE);
		
		PDialog = new ProgressDialog(this);
		PDialog.setMessage(getString(R.string.searching));
        PDialog.setIndeterminate(false);
        PDialog.setCancelable(false);
        PDialog.show();
		
        ABar = getActionBar();
        ABar.setDisplayHomeAsUpEnabled(true);
        
		snc = new SearchNetworkConnection(listener, this);
		ibnc = new InfoBuyNetworkConnection(this);
		
		ibnc.execute(new Configuration(this).getUserId());
		
		snc.execute(word);
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