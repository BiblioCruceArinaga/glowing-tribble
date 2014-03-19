package com.rising.store;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.rising.drawing.R;
import com.rising.store.SearchNetworkConnection.OnTaskCompleted;

public class SearchStoreActivity extends Activity{
			
	public String word; 
	public CustomAdapter CAdapter;
	public List<PartituraTienda> infoPart = new ArrayList<PartituraTienda>();
	public SearchNetworkConnection snc;
	
	public ProgressDialog PDialog;
	
	//Esto cargará todo aquello que dependa del hilo para ejecutarse, y que de no ser así no interesa que se ejecute
	private OnTaskCompleted listener = new OnTaskCompleted() {
	    public void onTaskCompleted() {     	    		
	    	
	    	infoPart = snc.devolverPartituras();
		   		
	    	GridView GV_Search = (GridView) findViewById(R.id.gV_search);
				
	    	GV_Search.setAdapter(new CustomAdapter(SearchStoreActivity.this, infoPart));	  
	    	
	    	PDialog.dismiss();
	    }	
	};
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.searchstore_layout);
			
		//Recibo la palabra de la activity anterior
		Bundle b = this.getIntent().getExtras();
				
		//La paso a una variable String
		word = b.getString("SearchText");
		
		PDialog = new ProgressDialog(this);
		PDialog.setMessage("Buscando...");
        PDialog.setIndeterminate(false);
        PDialog.setCancelable(false);
        PDialog.show();
		
		snc = new SearchNetworkConnection(listener, this);
		
		snc.execute(word);
	}   
	
	/*private void ListMode(){
		View.inflate(getApplicationContext(), R.layout.searchstorelist_layout, null);
		
		infoPart = snc.devolverPartituras();
   		
		ListView LV_Search = (ListView) findViewById(R.id.lista);
    	
    	LV_Search.setAdapter(new CustomListAdapter(SearchStoreActivity.this, infoPart));
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_search, menu);

		return true;			
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		Intent in = new Intent(this, MainActivityStore.class);
	    		startActivity(in);
	    		finish();
	    		return true;
	    	
	    	//case R.id.lista:
	    		//ListMode();
	    		//return true;
	    		
	        case R.id.action_settings:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
}