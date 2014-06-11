package com.rising.store.instruments;

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
import com.rising.store.CustomAdapter;
import com.rising.store.ErrorFragment;
import com.rising.store.InfoBuyNetworkConnection;
import com.rising.store.InfoCompra;
import com.rising.store.PartituraTienda;
import com.rising.store.instruments.PianoNetworkConnection.OnTaskCompleted;
import com.rising.store.instruments.PianoNetworkConnection.OnTaskUncomplete;

public class PianoFragment extends Fragment{

	private static ArrayList<PartituraTienda> partiturasPiano = new ArrayList<PartituraTienda>();
	private static ArrayList<InfoCompra> ICompra = new ArrayList<InfoCompra>();
	
	View rootView;
	static PianoNetworkConnection pnc;
	Configuration conf;
	static InfoBuyNetworkConnection ibnc;
	static ProgressDialog progressDialog;
	FragmentManager fm;
	
	//Esto cargará todo aquello que dependa del hilo para ejecutarse, y que de no ser así no interesa que se ejecute
	private OnTaskCompleted listener = new OnTaskCompleted() {
	    public void onTaskCompleted() {     	    		
	    	
	    	partiturasPiano = pnc.devolverPartituras();
	    	ICompra = ibnc.devolverCompra();
	    	
	    	//Trozo de código dónde se ve si la partitura ha sido comprada por el usuario. En tal caso se pone a true el valor "Comprado"
	    	for(int i = 0; i < partiturasPiano.size(); i++){
		    	for(int j = 0; j < ICompra.size(); j++){	
		    		if(partiturasPiano.get(i).getId() == ICompra.get(j).getId_S()){
		    			partiturasPiano.get(i).setComprado(true);
		    		}
		    	}
	    	}	
	    	
	    	GridView pianoView = (GridView) rootView.findViewById(R.id.gV_piano_fragment);
	    	
		    pianoView.setAdapter(new CustomAdapter(getActivity(), partiturasPiano));
		    		  		    
		    //onDestroyProgress();
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
		
		rootView = inflater.inflate(R.layout.priano_fragment, container, false);
		
		conf = new Configuration(rootView.getContext());
		
		ibnc = new InfoBuyNetworkConnection(rootView.getContext());
		
		ibnc.execute(new Configuration(rootView.getContext()).getUserId());
		fm = getFragmentManager();
					
		return rootView;	
	}
	
	public View errorView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		rootView = inflater.inflate(R.layout.store_error_layout, container, false);
		
		return rootView;
	}
	
	//Cierra el ProgressDialog en el caso de que lo haya. 	
	public void onDestroyProgress() {		
		if(progressDialog != null)
	        progressDialog.dismiss();
	    progressDialog = null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		progressDialog = ProgressDialog.show(rootView.getContext(), "", getString(R.string.pleasewait));
		pnc = new PianoNetworkConnection(listen, listener, rootView.getContext());
		pnc.execute(Locale.getDefault().getDisplayLanguage());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		pnc.cancel(true);
	}

	//Método que cierra el hilo, cancela el ProgressDialog y abre el Dialog de error.
	public void ConnectionExceptionHandle(){	
		
		//Cancelo el hilo y destruyo el ProgressDialog 
		pnc.cancel(true);
		onDestroyProgress();
		Log.i("Cancelled", "Piano: " + pnc.isCancelled());
		ErrorFragment EF = new ErrorFragment();
		
		EF.getFragment("Piano");
		
		getFragmentManager().beginTransaction().replace(R.id.fragment_container, EF).commit();
	
	}
}