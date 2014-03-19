package com.rising.store;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.store.GuitarNetworkConnection.OnTaskCompleted;
import com.rising.store.GuitarNetworkConnection.OnTaskUncomplete;

public class GuitarFragment extends Fragment{

	private static ArrayList<PartituraTienda> partiturasGuitar = new ArrayList<PartituraTienda>();
	private static ArrayList<InfoCompra> ICompra = new ArrayList<InfoCompra>();
	View rootView;
	Configuration conf;
	static GuitarNetworkConnection gnc;
	static InfoBuyNetworkConnection ibnc;
	static ProgressDialog progressDialog;
	FragmentManager fm;
			
	//Esto cargará todo aquello que dependa del hilo para ejecutarse, y que de no ser así no interesa que se ejecute
	private OnTaskCompleted listener = new OnTaskCompleted() {
	    public void onTaskCompleted() {     

	    	partiturasGuitar = gnc.devolverPartituras();
	    	ICompra = ibnc.devolverCompra();
	    		    		    	
	    	//Trozo de código dónde se ve si la partitura ha sido comprada por el usuario. En tal caso se pone a true el valor "Comprado"
	    	for(int i = 0; i < partiturasGuitar.size(); i++){
		    	for(int j = 0; j < ICompra.size(); j++){	
		    		if(partiturasGuitar.get(i).getId() == ICompra.get(j).getId_S()){
		    			partiturasGuitar.get(i).setComprado(true);
		    		}
		    	}
	    	}
	    	
	    	GridView guitarView = (GridView) rootView.findViewById(R.id.gV_guitar_fragment);
	    				        
	    	Log.i("Guitar", partiturasGuitar + "");
	    	
		    guitarView.setAdapter(new CustomAdapter(getActivity(), partiturasGuitar));
		      		    
		    onDestroyProgress();
	    } 	
	};
	
	//Esta interface cargará el método que cierra el hilo y da error
	private OnTaskUncomplete listen = new OnTaskUncomplete(){
		public void onTaskUncomplete(){
			ConnectionExceptionHandle();
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return main(inflater, container, savedInstanceState);
	}
	
	//Método que infla y hace todo lo de el onCreateView, devolviendo la View que se le pasa a este método
	public View main(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
				
		rootView = inflater.inflate(R.layout.guitar_fragment, container, false);
		
		conf = new Configuration(rootView.getContext());
		
		ibnc = new InfoBuyNetworkConnection(rootView.getContext());
		
		ibnc.execute(new Configuration(rootView.getContext()).getUserId());
		
		progressDialog = ProgressDialog.show(rootView.getContext(), "", "Por favor, espere...");
				
		gnc = new GuitarNetworkConnection(listen, listener, rootView.getContext());
		
		gnc.execute(Locale.getDefault().getDisplayLanguage());
						
		fm = getFragmentManager();
		
		return rootView;
	}

	
	//Cierra el ProgressDialog en el caso de que lo haya. 
	public void onDestroyProgress() {				
		if(progressDialog != null)
	        progressDialog.dismiss();
	    progressDialog = null;
	}
		
	//Método que cierra el hilo, cancela el ProgressDialog y abre el Dialog de error.
	public void ConnectionExceptionHandle(){
		
		//Cancelo el hilo y destruyo el ProgressDialog 
		gnc.cancel(true);
		onDestroyProgress();		
		Log.i("Cancelled", "Guitarra: " + gnc.isCancelled());
		
		ErrorFragment EF = new ErrorFragment();
		
		EF.getFragment("Guitar");
		
		getFragmentManager().beginTransaction().replace(R.id.fragment_container, EF).commit();
	}
}