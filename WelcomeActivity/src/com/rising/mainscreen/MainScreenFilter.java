package com.rising.mainscreen;

import android.widget.SearchView.OnQueryTextListener;

public class MainScreenFilter implements OnQueryTextListener{

	private ScoresAdapter s_adapter;
	
	public MainScreenFilter(ScoresAdapter s_adapter){
		this.s_adapter = s_adapter;
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		if (s_adapter != null) s_adapter.filter(newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (s_adapter != null) s_adapter.filter(query); 
		return false;
	}

}
