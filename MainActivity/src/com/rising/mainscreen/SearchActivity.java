package com.rising.mainscreen;

import java.io.File;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.rising.drawing.R;

public class SearchActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.search_layout);
				
		// Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      Log.i("Query", query);
	      Log.d("Query", query);
	      Log.e("Query", query);
	    }
			
	}
	
	private void SearchIt(String query){
		File file = new File(Environment.getExternalStorageDirectory() + "/SmartScores/scores/");
		String[] f = file.list();
		
		for(int i = 0; i < f.length; i++){
			if(f[i].equals(query)){
				Toast.makeText(getApplicationContext(), "Pues sí", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), "Pues no", Toast.LENGTH_LONG).show();
			}		
		}
		
	}
	

}
