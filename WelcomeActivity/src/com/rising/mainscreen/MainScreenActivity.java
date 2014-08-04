package com.rising.mainscreen;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.SessionManager;
import com.rising.money.MoneyUpdateConnectionNetwork;
import com.rising.money.MoneyUpdateConnectionNetwork.OnFailMoney;
import com.rising.money.MoneyUpdateConnectionNetwork.OnUpdateMoney;

public class MainScreenActivity extends Activity {
		
	/**Tareas del MainScreenActivity**/
	//Confirmar el login del usuario
	//Crear las carpetas para los archivos si estan no estan creadas. 
	//Actualizar saldo
	//Recolectar información sobre los ficheros en el sistema
	//Mostrar interfaz correspondiente
		//Permite abrir los archivos
		//Permite borrar los archivos
		//Permite buscar los archivos
		//Permite ordenar los archivos por distintos criterios
		//Permite subir archivos PDF
	
	
	private ScoresAdapter s_adapter;
	private GridView scores_gallery;

	public MoneyUpdateConnectionNetwork mucn;
	private Dialog MDialog;
	private Context ctx;
		
	//Clases usadas
	//private Ordenar_Partituras ORDENAR;
	private CreateFolders CREATE;
	//private PDF_Methods PDF;
	private InterfazNoPartituras INTERFACES;
	private RecopilarInfoFicheros INFO_FICHEROS; 
	private SessionManager SESSION;
	private Configuration CONF;
		 
	private OnUpdateMoney moneyUpdate = new OnUpdateMoney(){

		@Override
		public void onUpdateMoney() {
												
			CONF.setUserMoney(mucn.devolverDatos());
		}
	};
	
	private OnFailMoney failMoney = new OnFailMoney(){

		@Override
		public void onFailMoney() {
			Toast.makeText(ctx, "Falló al actualizar el saldo", Toast.LENGTH_LONG).show();
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.mainscreen_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
		ctx = this;
		this.CONF = new Configuration(this);
		this.SESSION = new SessionManager(getApplicationContext());		
		this.CREATE = new CreateFolders(ctx);
		this.INTERFACES = new InterfazNoPartituras(ctx, scores_gallery);
		this.INFO_FICHEROS = new RecopilarInfoFicheros();
		
		SESSION.checkLogin();
		CREATE.createScoreFolder();
		CREATE.createImageFolder();		

		UpdateMoney(CONF.getUserEmail());
		
		ActionBar action = getActionBar();
		action.setTitle(R.string.titulo_coleccion);
		action.setIcon(R.drawable.ic_menu);
		
		if (INFO_FICHEROS.ficherosLength() == 0) {
			INTERFACES.interfazCuandoNoHayPartituras();
		} else {
			new InterfazConPartituras(ctx, scores_gallery).interfazCuandoHayPartituras();
		} 
		
	}

	public void UpdateMoney(String user){
		mucn = new MoneyUpdateConnectionNetwork(moneyUpdate, failMoney, this);
		mucn.execute(user);
	}
							
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_screen_activity, menu);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    
	    searchView.setOnQueryTextListener(new MainScreenFilter(s_adapter));
	    
		return true;			
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    new InterfazConPartituras(ctx,scores_gallery).MainScreenMenu(item.getItemId());
	    return true;
	}
	
	public void AboutDialog(){
    	MDialog = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
		MDialog.setContentView(R.layout.preferencies_about);
		MDialog.setTitle(R.string.about);
		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		TextView Link_Web = (TextView) MDialog.findViewById(R.id.link);
		Link_Web.setLinkTextColor(Color.BLACK);
		Linkify.addLinks(Link_Web, Linkify.ALL);
		TextView Link_Metronome_Icon = (TextView) MDialog.findViewById(R.id.metronome_link);
		Link_Metronome_Icon.setLinkTextColor(Color.BLACK);
		Linkify.addLinks(Link_Metronome_Icon, Linkify.ALL);
		MDialog.show();
	}
					
}