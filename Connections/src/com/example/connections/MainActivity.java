package com.example.connections;

import java.util.ArrayList;

import com.example.connections.NetworkConnectionTest.OnTaskCompleted;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	MainActivity activity = this;
	ArrayList<PartituraTienda> infoPartituras = new ArrayList<PartituraTienda>();
	NetworkConnectionTest nct;
	ProgressDialog progressDialog;
	
	private OnTaskCompleted listener = new OnTaskCompleted() {
	    public void onTaskCompleted() {       
	        infoPartituras = nct.devolverPartituras();
			
			ArrayList<Integer> elements = new ArrayList<Integer>();
			int numPartituras = infoPartituras.size();
			for (int i=0; i<numPartituras; i++) elements.add(R.layout.grid_element);
			GridView gridview = (GridView) findViewById(R.id.gridView1);
		    gridview.setAdapter(new CustomAdapter(activity, elements, infoPartituras));

		    gridview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Toast.makeText(MainActivity.this, "" + arg2, Toast.LENGTH_LONG).show();
				}
		    });
		    
		    progressDialog.dismiss();
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		activity = this;
		nct = new NetworkConnectionTest(listener, getBaseContext());
		nct.execute("");
    	progressDialog = ProgressDialog.show(MainActivity.this, "", "Por favor, espere...");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
