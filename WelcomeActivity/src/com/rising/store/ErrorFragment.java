package com.rising.store;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.rising.drawing.R;
import com.rising.store.instruments.FreeFragment;
import com.rising.store.instruments.GuitarFragment;
import com.rising.store.instruments.PianoFragment;

public class ErrorFragment extends Fragment{

	//Variables
	private View view; 
	private String fragment;
			
	public void getFragment(String fragment){
		this.fragment = fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.store_errorlayout, container, false);
			
		Button errButton = (Button) view.findViewById(R.id.b_retry_store);
				
		errButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				
				//Identifica dónde está el fallo y vuelve a cargar ese fragment
				if(fragment.equals("Piano")){
					Log.i("Fragment", fragment);
					getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PianoFragment()).commit();
										
				}else{
					if(fragment.equals("Guitar")){
						Log.i("Fragment", fragment);						
						getFragmentManager().beginTransaction().replace(R.id.fragment_container, new GuitarFragment()).commit();
												
					}else{
						if(fragment.equals("Free")){
							Log.i("Fragment", fragment);
							getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FreeFragment()).commit();
						}
					}
				}			
			}
			
		});
		
		return view;
	}
	
}