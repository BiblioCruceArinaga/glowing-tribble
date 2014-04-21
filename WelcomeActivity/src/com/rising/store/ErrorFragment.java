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

	View view; 
	String f;
	static GuitarFragment GF = new GuitarFragment();
		
	public ErrorFragment() {
        //El constructor debe estar vacío para el DialogFragment
    }
	
	public void getFragment(String fragment){
		this.f = fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.store_error_layout, container, false);
		
		Log.d("Error", "Entró en el fragment error");
		
		Button eButton = (Button) view.findViewById(R.id.b_retry_store);
				
		eButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				
				//Identifica dónde está el fallo y vuelve a cargar ese fragment
				if(f.equals("Piano")){
					
					Log.i("Fragment", f);
					getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PianoFragment()).commit();
										
				}else{
					if(f.equals("Guitar")){
						Log.i("Fragment", f);						
						getFragmentManager().beginTransaction().replace(R.id.fragment_container, new GuitarFragment()).commit();
												
					}else{
						if(f.equals("Free")){
							Log.i("Fragment", f);
							getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FreeFragment()).commit();
						}
					}
				}			
			}
			
		});
		
		return view;
	}
	
}