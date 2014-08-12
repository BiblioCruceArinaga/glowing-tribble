package com.rising.store.instruments;

import java.util.ArrayList;

import android.app.FragmentManager;

import com.rising.drawing.R;
import com.rising.store.ErrorFragment;
import com.rising.store.PartituraTienda;
import com.rising.store.purchases.InfoCompra;

public class Store_Instruments_Utils {
	
	public void MarcaPartituraComoComprada(ArrayList<PartituraTienda> partiturasPiano, ArrayList<InfoCompra> ICompra){
    	for(int i = 0; i < partiturasPiano.size(); i++){
	    	for(int j = 0; j < ICompra.size(); j++){	
	    		if(partiturasPiano.get(i).getId() == ICompra.get(j).getId_S()){
	    			partiturasPiano.get(i).setComprado(true);
	    		}
	    	}
    	}
	}

	public void OpenErrorFragment(FragmentManager fm, String fromFragment){
		ErrorFragment EF = new ErrorFragment();		
		EF.getFragment(fromFragment);
		fm.beginTransaction().replace(R.id.fragment_container, EF).commit();
	}

}