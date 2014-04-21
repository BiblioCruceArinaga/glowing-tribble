package com.rising.store;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.store.MoneyUpdateConnectionNetwork.OnUpdateMoney;
import com.rising.store.instruments.FreeFragment;
import com.rising.store.instruments.GuitarFragment;
import com.rising.store.instruments.PianoFragment;

public class MainActivityStore extends FragmentActivity implements OnQueryTextListener{

	private ActionBar ABar;
	Bundle b = new Bundle();
	String f;
	static String s;
	public MenuItem item;	
	MoneyUpdateConnectionNetwork mucn;
	
	//  Poner tiempo máximo de conexión. Si no se conecta pasado ese tiempo, sale algo.
	Bundle savedInstanceState;
	MainActivityStore activity;
	double d; 
	//  Con esta clase se guardan los datos del usuario 
	//  en el dispositivo para posteriores consultas
	Configuration conf = new Configuration(this);
		
	public static Context context;
			
	private OnUpdateMoney moneyUpdate = new OnUpdateMoney(){

		@Override
		public void onUpdateMoney() {
			
			//Log.i("UpdateMoney", "Aqu� 1: " + conf.getUserMoney());
			//conf.setUserMoney(d);
			/**
			Esto es provicional. No debe hacerse así, debe actualizarse desde la base de datos
			**/
			//conf.setUserMoney();
			//item.setTitle(""+conf.getUserMoney());
			
			invalidateOptionsMenu();			
			Log.i("UpdateMoney", "Aqu� 2");
		}
	};
	
	public void StartMoneyUpdate(String user){
		
		Log.i("User", "Mail: " + user);
		new MoneyUpdateConnectionNetwork(moneyUpdate, context).execute(user);
		//d = new MoneyUpdateConnectionNetwork(moneyUpdate, context).getMoney();	
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main_store);		
		
		mucn = new MoneyUpdateConnectionNetwork(moneyUpdate, context);		
		
    	ABar = getActionBar();
    	ABar.setIcon(R.drawable.ic_menu);
    	ABar.setTitle(R.string.store);
    	ABar.setDisplayHomeAsUpEnabled(true); 
    	ABar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
    	     	    	
    	ABar.addTab(ABar.newTab().setText(R.string.piano).setTabListener(new TabListener(new PianoFragment())));
    	ABar.addTab(ABar.newTab().setText(R.string.guitar).setTabListener(new TabListener(new GuitarFragment())));
    	ABar.addTab(ABar.newTab().setText(R.string.free).setTabListener(new TabListener(new FreeFragment())));
   	}
	
	//Vuelve (o lo pretende) a colocar los archivos que encuentre en el dispositivo en la pantalla principal
	@Override
	public void onBackPressed() {
	   //super.onBackPressed();
	   Log.d("CDA", "onBackPressed Called");
	   Intent setIntent = new Intent(this, MainScreenActivity.class);
	   new MainScreenActivity().ColocarFicheros();
	   startActivity(setIntent);
	   finish();
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }   
	
	public void getFragment(Fragment fragment){
		this.f = fragment.toString();
		int i = f.indexOf('{');
		s = f.substring(0, i);
	}
    	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_store, menu);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search_store).getActionView();
	    searchView.setOnQueryTextListener(this);
	    	  	
	    item = menu.findItem(R.id.money);
		item.setTitle("" + conf.getUserMoney());
	    Log.i("Conf", "Conf User: " + conf.getUserName() + ", Conf Money: " + conf.getUserMoney());
	    	    
		return true;
	}
		
	//Al pulsar en el botón intro una vez escrita la palabra entra en este método, que envia la palabra a otro Activity para que este muestre 
	//los resultados a partir de la palabra
	@Override
	public boolean onQueryTextSubmit(String text) {
		 
		Intent i = new Intent(this, SearchStoreActivity.class);
		Bundle b = new Bundle();
		b.putString("SearchText", text);
		i.putExtras(b);
		startActivity(i);
		
		//c_adapter.filter(text); 
		
		return false;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.money:
	    		//Intent i = new Intent(this, MoneyActivity.class);
	    	   // startActivity(i);
	    	    return true;
    	    
	    	case R.id.update_store:
	    		    		
	    		if(s.equals("PianoFragment")){
	    			getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PianoFragment()).commit();
	    		}else{
	    			if(s.equals("GuitarFragment")){
	    				getFragmentManager().beginTransaction().replace(R.id.fragment_container, new GuitarFragment()).commit();
	    			}else{
	    				if(s.equals("FreeFragment")){
	    					getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FreeFragment()).commit();
	    				}else{
	    					Log.e("Error actualizar", "Falló al actualizar");
	    					Toast.makeText(getApplicationContext(), "Hubo un error en la actualización", Toast.LENGTH_SHORT).show();
	    				}
	    			}
	    		}
	    			    		
	    		Log.i("Update", "Entró en actualizar ");
	    		return true;
	        
	    	case android.R.id.home:
	    		Intent in = new Intent(this, MainScreenActivity.class);
	    		startActivity(in);
	    		finish();
	    		
	    	default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

}

//Clase necesaria para implementar las pestañas en la aplicación. Los métodos en esta ejecutan lo que pasa cuando 
//se selecciona, se re selecciona, o se deselecciona una pestaña
class TabListener implements ActionBar.TabListener {
	public Fragment fragment;
	
	public TabListener(Fragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.fragment_container, fragment);
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.fragment_container, fragment);
		new MainActivityStore().getFragment(fragment);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}	
}