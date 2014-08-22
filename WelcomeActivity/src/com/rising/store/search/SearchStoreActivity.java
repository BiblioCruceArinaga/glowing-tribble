package com.rising.store.search;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.login.ProgressDialogFragment;
import com.rising.store.CustomAdapter;
import com.rising.store.MainActivityStore;
import com.rising.store.PartituraTienda;
import com.rising.store.purchases.InfoBuyNetworkConnection;
import com.rising.store.purchases.InfoBuyNetworkConnection.OnTaskNoInfo;
import com.rising.store.purchases.InfoCompra;
import com.rising.store.search.AsyncTask_Search.OnTaskCompleted;
import com.rising.store.search.AsyncTask_Search.OnTaskFailed;

//Clase que muestra el resultado de la búsqueda de partituras a partir de una palabra que se le pasa
public class SearchStoreActivity extends Activity{
			
	//Variables
	private String word;
	private List<PartituraTienda> infoPart = new ArrayList<PartituraTienda>();
	private ArrayList<InfoCompra> ICompra = new ArrayList<InfoCompra>();
	//private ProgressDialog PDialog;
	private ActionBar ABar;
	private TextView result;
	
	//Clases usadas
	private InfoBuyNetworkConnection INFO_ASYNCTASK;
	private AsyncTask_Search SEARCH_ASYNCTASK;
	
	private OnTaskCompleted Search = new OnTaskCompleted() {
	    public void onTaskCompleted() {     	    		
	    	
	    	infoPart = SEARCH_ASYNCTASK.devolverPartituras();
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
	    	GridView GV_Search = (GridView) findViewById(R.id.gV_search);
				
	    	GV_Search.setAdapter(new CustomAdapter(SearchStoreActivity.this, infoPart));	  
	    	
	    	ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
	     	
	        if (dialog!=null) { 
	            dialog.dismiss();
	        }
	    }	
	};
	
	private OnTaskFailed NoSearch = new OnTaskFailed(){

		@Override
		public void onTaskFailed() {
			ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
	     	
	        if (dialog!=null) { 
	            dialog.dismiss();
	        }
			result.setVisibility(View.VISIBLE);
		}		
	}; 
	
	
	private OnTaskNoInfo NoInfo = new OnTaskNoInfo(){

		@Override
		public void onTaskNoInfo() {
			ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
	     	
	        if (dialog!=null) { 
	            dialog.dismiss();
	        }
			result.setText(R.string.connection_err);
			result.setVisibility(View.VISIBLE);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_search_searchstorelayout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.SEARCH_ASYNCTASK = new AsyncTask_Search(Search, NoSearch);
		this.INFO_ASYNCTASK = new InfoBuyNetworkConnection(NoInfo);
		
		Bundle b = this.getIntent().getExtras();
		word = b.getString("SearchText");
		
		result = (TextView)findViewById(R.id.empty_result);
		result.setVisibility(View.INVISIBLE);
		
        ABar = getActionBar();
        ABar.setDisplayHomeAsUpEnabled(true);
        		
		INFO_ASYNCTASK.execute(new Configuration(this).getUserId());
		SEARCH_ASYNCTASK.execute(word);
		
		ProgressDialogFragment dialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("myDialog");
     	
        if (dialog!=null) { 
            dialog.dismiss();
        }
	}   
	
	@Override
	protected void onResume() {
		super.onResume();
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("myDialog");
	    if (prev != null) {
	      	ft.remove(prev);
	    }
	    ft.addToBackStack(null);  
	            
	    ProgressDialogFragment dialog = ProgressDialogFragment.newInstance(getString(R.string.searching));
	    dialog.setCancelable(false);
	    dialog.show(ft, "myDialog");	 
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