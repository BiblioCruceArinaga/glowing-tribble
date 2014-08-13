package com.rising.mainscreen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.rising.drawing.MainActivity;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.Login_Utils;
import com.rising.login.SessionManager;
import com.rising.mainscreen.preferencies.PreferenciesActivity;
import com.rising.mainscreen.preferencies.Preferencies_Utils;
import com.rising.mainscreen.preferencies.SendFeedback_Fragment;
import com.rising.money.MoneyUpdateConnectionNetwork;
import com.rising.money.MoneyUpdateConnectionNetwork.OnFailUpdateMoney;
import com.rising.money.MoneyUpdateConnectionNetwork.OnSuccessUpdateMoney;
import com.rising.pdf.PDF_Methods;
import com.rising.security.DownloadScoresEncrypter;
import com.rising.store.MainActivityStore;

public class MainScreenActivity extends Activity implements OnQueryTextListener{

	//Variables
	private Context ctx;
	private String[] ficheros;
	private String[][] infoFicheros;
	private File f_toDelete;
	private File f_image_toDelete;
	private boolean delete;
	private HashMap<Integer, Boolean> mSelected;	
	private ScoresAdapter s_adapter;
	private GridView scores_gallery;
	private int numScores = 0;
	private ArrayList<Score> arraylist = new ArrayList<Score>();
	public MoneyUpdateConnectionNetwork mucn;

	//URLs que quiero quitar de aquí
	public String path = "/.RisingScores/scores/";
	public String image_path = "/.RisingScores/scores_images/";


	//Clases usadas
	private Ordenar_Partituras ORDENAR;
	private MainScreen_Utils MSUTILS;
	private CreateFolders CREATE;
	private PDF_Methods PDF;
	private SessionManager SESSION;
	private Configuration CONF;
	private MainScreen_Errors ERRORS;


	private OnSuccessUpdateMoney moneyUpdate = new OnSuccessUpdateMoney(){

		@Override
		public void onSuccessUpdateMoney() {

			CONF.setUserMoney(mucn.devolverDatos());
		}
	};

	private OnFailUpdateMoney failMoney = new OnFailUpdateMoney(){

		@Override
		public void onFailUpdateMoney() {
			Toast.makeText(ctx, getString(R.string.errcredit), Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.mainscreen_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mSelected = new HashMap<Integer, Boolean>();
		CONF = new Configuration(this);
		SESSION = new SessionManager(getApplicationContext());
		ficheros = leeFicheros();
		ctx = this;
		SESSION.checkLogin();

		this.ERRORS = new MainScreen_Errors(ctx);
		this.MSUTILS = new MainScreen_Utils();
		this.CREATE = new CreateFolders(ctx);
		this.PDF = new PDF_Methods(ctx, null);


		CREATE.createScoreFolder();
		CREATE.createImageFolder();		

		UpdateMoney(CONF.getUserEmail());

		ActionBar action = getActionBar();
		action.setTitle(R.string.titulo_coleccion);
		action.setIcon(R.drawable.ic_menu);

		//  Si no hay partituras, mostramos un mensaje al usuario. Si hay partituras cargamos la galería de partituras
		if (ficherosLength() == 0) {
			new InterfazNoPartituras(ctx, scores_gallery);
		} else {
			interfazCuandoHayPartituras(ficheros);
		} 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_screen_activity, menu);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

	    searchView.setOnQueryTextListener(this);
		return true;			
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
			case R.id.store_button:
				if(new Login_Utils(ctx).isOnline()){
					Intent i = new Intent(MainScreenActivity.this, MainActivityStore.class);
					startActivity(i);
					finish();
				}else{
					Toast.makeText(this, R.string.connection_err, Toast.LENGTH_LONG).show();	
				}
				return true;

			case R.id.subir_archivo:
				PDF.subirArchivo();
				return true;

			case R.id.sort_author:
				if(ORDENAR != null){
					ORDENAR.listarAutores();
				}else{
					ERRORS.ErrOrdenar(0);
				}
				return true;

			case R.id.sort_instrument:
				if(ORDENAR != null){
					ORDENAR.listarInstrumentos();
				}else{
					ERRORS.ErrOrdenar(0);
				}
				return true;

			case R.id.sort_name:
				if(ORDENAR != null){
					ORDENAR.ordenarPorNombre();
				}else{
					ERRORS.ErrOrdenar(0);
				}					
				return true;

			case R.id.sort_format:
				if(ORDENAR != null){
					ORDENAR.listarFormatos();
				}else{
					ERRORS.ErrOrdenar(0);
				}
				return true;
				
			case R.id.show_all:
				if(ORDENAR != null){
					ORDENAR.mostrarTodas();
				}else{
					ERRORS.ErrOrdenar(0);
				}
				return true;
            
	        case R.id.about:
	        	new Preferencies_Utils(ctx).AboutDialog();
	    		return true;

	        case R.id.feedback:
	        	Intent i = new Intent(this, SendFeedback_Fragment.class);
	        	startActivity(i);
	    		finish();
	            return true;

	        case R.id.mis_datos:
	        	Intent in = new Intent(MainScreenActivity.this, PreferenciesActivity.class);
	        	startActivity(in);
	        	finish();
	        	return true;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void UpdateMoney(String user){
		mucn = new MoneyUpdateConnectionNetwork(moneyUpdate, failMoney, this);
		mucn.execute(user);
	}
	
	public void interfazCuandoHayPartituras(final String[] ficheros){

		this.PDF = new PDF_Methods(ctx, ficheros);

		infoFicheros = darInfoNombreImagenInstrumentoFormatoFicheros(ficheros);

		for (int i = 0; i < ficherosLength(); i++){
			 Score ss = new Score(infoFicheros[1][i], infoFicheros[0][i], infoFicheros[3][i], infoFicheros[2][i], infoFicheros[4][i]);
			 arraylist.add(ss);
		}

		s_adapter = new ScoresAdapter(this, arraylist);
		this.ORDENAR = new Ordenar_Partituras(ctx, s_adapter);

		scores_gallery = (GridView) findViewById(R.id.gV_scores);
		scores_gallery.setAdapter(s_adapter);
		scores_gallery.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		numScores = scores_gallery.getCount();

		scores_gallery.setMultiChoiceModeListener(new MultiChoiceModeListener(){

			String[] ficheros2 = ficheros;

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

				// Este método dirige las acciones de los botones de la barra superior
				switch(item.getItemId()){
			   	   	case R.id.discard:
			   	   		borrarElementos(ficheros2);
			       		mode.finish();
				        return true; 

			   	   	case R.id.s_all:
			   	   		for(int i = 0; i < numScores; i++) {
			   	   			scores_gallery.setItemChecked(i, true);
			    	   	}
				        return true;

			   	   	case R.id.s_none:
			    		for(int i = 0; i < numScores; i++) {
			    			scores_gallery.setItemChecked(i, false);
			    		}
			    	   	return true;     
				}

		        return false;
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {

				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.modal_details, menu);
				mode.setTitle(R.string.title);
		        mode.setSubtitle(R.string.subtitle);
		        ficheros2 = leeFicheros();

		        return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {			
				return true;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

				//Este método dirige lo que pasa en la pantalla del menú contextual 
				int selectCount = scores_gallery.getCheckedItemCount();

		        switch (selectCount) {
		        	case 1:
		        		mode.setSubtitle(R.string.subtitle);
			            break;
		        	default:
		        		mode.setSubtitle(selectCount + " " + getString(R.string.subtitle2));
			            break; 
		        }

		        if(checked){
		           	mSelected.put(position, checked);
		        }else{
		           	mSelected.remove(position);
		           	mSelected.put(position, false);
		        }

		        Log.i("Estado", position + ": " + mSelected.get(position));
			}
	});

		scores_gallery.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("Position", ficheros[position]);

				//Si es un PDF abre el PDF, si no, el otro. 
				if(MSUTILS.ComprobarExtensionFichero(ficheros[position])){

					PDF.AbrirPDFExterno(position);
					//PDF.AbrirPDFInterno(position);

				}else{
					//Intentar usar el método de AbrirFicheros en Store_Utils
					if(new DownloadScoresEncrypter(ctx, infoFicheros[0][position]+CONF.getUserId()).DescryptAndConfirm(ficheros[position])){
						Intent i = new Intent(MainScreenActivity.this, MainActivity.class);
						i.putExtra("score", ficheros[position]);

						startActivity(i);
					}else{
						ERRORS.Incorrect_User();
					}
				}
			} 
		});

	}

	public void borrarElementos(String[] ficheros2){
		List<Score> elementosAEliminar = new ArrayList<Score>();

	   		for(int i = 0; i < ficherosLength(); i++){

	   			if(mSelected.containsKey(i)){
	   				f_toDelete = new File(Environment.getExternalStorageDirectory() + path + ficheros2[i]);	
	   				f_image_toDelete = new File(Environment.getExternalStorageDirectory() + image_path + MSUTILS.ficheroAImagen(ficheros2[i]));
	   				if(f_toDelete.exists() && f_image_toDelete.exists()){
		   				if(f_toDelete.delete() && f_image_toDelete.delete()){
		   					elementosAEliminar.add(s_adapter.getItem(i));
		   					delete = true;
		   				}else{
		   					delete = false;
		   					break;
		   				}
	   				}		
	   			}
	   		}

	   		s_adapter.removeAllSelected(elementosAEliminar);
	   		numScores = scores_gallery.getCount();

	   		if(delete){
	   			Toast.makeText(getApplicationContext(), R.string.successDelete, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), R.string.failDelete, Toast.LENGTH_LONG).show();
			}

   		if (s_adapter.isEmpty()) {
   			new InterfazNoPartituras(ctx, scores_gallery);
   		}
   		mSelected.clear();
	}

	
	/**************************************Recopilación de información de los ficheros*********************************/
	
	public String[] leeFicheros(){
		Log.i("Eh", ""+path);
		File f = new File(Environment.getExternalStorageDirectory() + path);
		String[] lista = f.list();
		return lista;
	}

	public int ficherosLength(){
		int ficherosLength;
		if (ficheros != null) ficherosLength = ficheros.length;
		else ficherosLength = 0;
		return ficherosLength;
	}	

	private String[][] darInfoNombreImagenInstrumentoFormatoFicheros(String[] ArrayScores) {
		String[][] res;

		int len;
		if (ArrayScores != null) len = ArrayScores.length;
		else len = 0;

		res = new String[5][len];

		for(int i=0; i < len; i++){
			if(MSUTILS.ComprobarExtensionFichero(ArrayScores[i])){
				res[0][i] = ArrayScores[i].substring(0, ArrayScores[i].indexOf(".")); 
				res[1][i] = "";	//  Autor
				res[2][i] = "";	//  Instrumento
				res[3][i] = MSUTILS.ficheroAImagen(ArrayScores[i]);	// Imagen
				res[4][i] = ArrayScores[i].substring(ArrayScores[i].lastIndexOf(".") + 1, ArrayScores[i].length());
			}else{
				String[] dataSplit = ArrayScores[i].split("_");
				//String imagenFichero = ArrayScores[i].substring(0, ArrayScores[i].lastIndexOf("."));

				res[0][i] = dataSplit[0].replace("-", " ");	//  Nombre de la obra
				res[1][i] = dataSplit[1].replace("-", " ");	//  Autor
				res[2][i] = dataSplit[2].substring(0, dataSplit[2].indexOf("."));	//  Instrumento
				res[3][i] = MSUTILS.ficheroAImagen(ArrayScores[i]);	// Imagen
				res[4][i] = ArrayScores[i].substring(ArrayScores[i].lastIndexOf(".") + 1, ArrayScores[i].length());
			}
		}

		return res;
	}
	
	/*********************************************Fin de bloque recopliación******************************************/

	
	
	/***************************************************Bloque filtro************************************/
	@Override
	public boolean onQueryTextChange(String newText) {
		if (s_adapter != null) s_adapter.filter(newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		if (s_adapter != null) s_adapter.filter(text); 
		return false;
	}
	/*************************************************Fin bloque filtro*********************************/
}