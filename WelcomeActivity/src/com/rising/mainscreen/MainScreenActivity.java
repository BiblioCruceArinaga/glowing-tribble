package com.rising.mainscreen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.MainActivity;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.Login_Utils;
import com.rising.login.SessionManager;
import com.rising.mainscreen.preferencies.PreferenciesActivity;
import com.rising.mainscreen.preferencies.SendFeedback;
import com.rising.mainscreen.preferencies.SendFeedback.OnSendingFeedback;
import com.rising.money.MoneyUpdateConnectionNetwork;
import com.rising.money.MoneyUpdateConnectionNetwork.OnFailMoney;
import com.rising.money.MoneyUpdateConnectionNetwork.OnUpdateMoney;
import com.rising.security.DownloadScoresEncrypter;
import com.rising.store.MainActivityStore;

public class MainScreenActivity extends Activity implements OnQueryTextListener{

	SessionManager session;
	Configuration conf;
		
	String[] ficheros;
	String[][] infoFicheros;
	private File f_toDelete;
	private File f_image_toDelete;
	private boolean delete;
	HashMap<Integer, Boolean> mSelected;	
	private ScoresAdapter s_adapter;
	private GridView scores_gallery;
	private int numScores = 0;
	ArrayList<Score> arraylist = new ArrayList<Score>();
	public MoneyUpdateConnectionNetwork mucn;
	private Dialog MDialog;
	private int fid;
	private Context ctx;
	private Dialog Incorrect_User;
	
	//URLs que quiero quitar de aquí
	public String path = "/.RisingScores/scores/";
	public String image_path = "/.RisingScores/scores_images/";
	
	
	//Clases usadas
	private Ordenar_Partituras ORDENAR;
	private MainScreen_Utils MSUTILS;
	private CreateFolders CREATE;
	private PDF_Methods PDF;
	
	//  Recibir la señal del proceso que envía Feedback
	private OnSendingFeedback listenerFeedback = new OnSendingFeedback() {
	    public void onFeedbackSent(int details) {       
			if (details == 1)
				Toast.makeText(getApplicationContext(), 
					R.string.feedback_enviado, Toast.LENGTH_LONG).show();
	    	else
				Toast.makeText(getApplicationContext(), 
					R.string.err_login_unknown, Toast.LENGTH_LONG).show();
			
			MDialog.dismiss();
	    }
	};
	 
	private OnUpdateMoney moneyUpdate = new OnUpdateMoney(){

		@Override
		public void onUpdateMoney() {
												
			conf.setUserMoney(mucn.devolverDatos());
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
				
		mSelected = new HashMap<Integer, Boolean>();
		conf = new Configuration(this);
		session = new SessionManager(getApplicationContext());
		ficheros = leeFicheros();
		ctx = this;
		session.checkLogin();
		fid = session.getFacebookId();
		
		this.ORDENAR = new Ordenar_Partituras(ctx, s_adapter);
		this.MSUTILS = new MainScreen_Utils();
		this.CREATE = new CreateFolders(ctx);

		
		CREATE.createScoreFolder();
		CREATE.createImageFolder();		

		UpdateMoney(conf.getUserEmail());
		
		ActionBar action = getActionBar();
		action.setTitle(R.string.titulo_coleccion);
		action.setIcon(R.drawable.ic_menu);
		
		//  Si no hay partituras, mostramos un mensaje al usuario. Si hay partituras cargamos la galería de partituras
		if (ficherosLength() == 0) {
			interfazCuandoNoHayPartituras();
		} else {
			interfazCuandoHayPartituras(ficheros);
		} 
		
	}

	public void UpdateMoney(String user){
		mucn = new MoneyUpdateConnectionNetwork(moneyUpdate, failMoney, this);
		mucn.execute(user);
	}
	
	public int ficherosLength(){
		int ficherosLength;
		if (ficheros != null) ficherosLength = ficheros.length;
		else ficherosLength = 0;
		return ficherosLength;
	}	
	
	public String ficheroAImagen(String fichero){
		String imagenFichero = fichero.substring(0, fichero.lastIndexOf("."));
		return imagenFichero + ".jpg";
	}
		
	public void interfazCuandoHayPartituras(final String[] ficheros){
		
		this.PDF = new PDF_Methods(ctx, ficheros);
		
		infoFicheros = darInfoFicheros(ficheros);
				
		Incorrect_User = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
		
		Incorrect_User.setContentView(R.layout.incorrect_user_dialog);
		Incorrect_User.setTitle(R.string.incorrect_user);
		
		for (int i = 0; i < ficherosLength(); i++){
			 Score ss = new Score(infoFicheros[1][i], infoFicheros[0][i], infoFicheros[3][i], infoFicheros[2][i], infoFicheros[4][i]);
			 arraylist.add(ss);
		}
		
		s_adapter = new ScoresAdapter(this, arraylist);
		
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
					 				    
				}else{
					if(new DownloadScoresEncrypter(ctx, infoFicheros[0][position]+conf.getUserId()).DescryptAndConfirm(ficheros[position])){
						Intent i = new Intent(MainScreenActivity.this, MainActivity.class);
						i.putExtra("score", ficheros[position]);
		
						startActivity(i);
					}else{
						Incorrect_User.show();
					}
				}
			} 
		});
	
	}
			
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    
	    searchView.setOnQueryTextListener(this);
		return true;			
	}
	
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
				ORDENAR.listarAutores();
				return true;
				
			case R.id.sort_instrument:
				ORDENAR.listarInstrumentos();
				return true;
				
			case R.id.sort_name:
				ORDENAR.ordenarPorNombre();
				return true;
				
			case R.id.show_all:
				ORDENAR.mostrarTodas();
				return true;
            
	        case R.id.about:
	        	MDialog = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
	    		MDialog.setContentView(R.layout.about);
	    		MDialog.setTitle(R.string.about);
	    		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    		TextView Link_Web = (TextView) MDialog.findViewById(R.id.link);
	    		Link_Web.setLinkTextColor(Color.BLACK);
	    		Linkify.addLinks(Link_Web, Linkify.ALL);
	    		TextView Link_Metronome_Icon = (TextView) MDialog.findViewById(R.id.metronome_link);
	    		Link_Metronome_Icon.setLinkTextColor(Color.BLACK);
	    		Linkify.addLinks(Link_Metronome_Icon, Linkify.ALL);
	    		MDialog.show();
	    		return true;
	                
	        case R.id.feedback:
	        	MDialog = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
	    		MDialog.setContentView(R.layout.feedback);
	    		MDialog.setTitle(R.string.feedback);
	    		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    		MDialog.show();

	    		Button feedbackBoton = (Button)MDialog.findViewById(R.id.feedbackBoton);
	    		feedbackBoton.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if(new Login_Utils(ctx).isOnline()){	
							EditText feedbackCajaTexto = (EditText) MDialog.findViewById(R.id.feedbackCajaTexto);
				    		String message = feedbackCajaTexto.getText().toString();
							
							if (message.equals("")) {
								Toast.makeText(getApplicationContext(), 
									R.string.err_campos_vacios, Toast.LENGTH_LONG).show();
							} else {
								new SendFeedback(MainScreenActivity.this, 
										listenerFeedback).execute(session.getMail(), message);
							}
						
						}else{
							Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();	
						}
					}
	    	    });
	    		
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
		
	public String[] leeFicheros(){
		Log.i("Eh", ""+path);
		File f = new File(Environment.getExternalStorageDirectory() + path);
		String[] lista = f.list();
		return lista;
	}
	
	//  Extrae el autor, el nombre, la imagen y el instrumento de
	//  todas las partituras existentes en el dispositivo
	private String[][] darInfoFicheros(String[] ArrayScores) {
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
				res[3][i] = ficheroAImagen(ArrayScores[i]);	// Imagen
				res[4][i] = ArrayScores[i].substring(ArrayScores[i].lastIndexOf(".") + 1, ArrayScores[i].length());
			}else{
				String[] dataSplit = ArrayScores[i].split("_");
				//String imagenFichero = ArrayScores[i].substring(0, ArrayScores[i].lastIndexOf("."));
						
				res[0][i] = dataSplit[0].replace("-", " ");	//  Nombre de la obra
				res[1][i] = dataSplit[1].replace("-", " ");	//  Autor
				res[2][i] = dataSplit[2].substring(0, dataSplit[2].indexOf("."));	//  Instrumento
				res[3][i] = ficheroAImagen(ArrayScores[i]);	// Imagen
				res[4][i] = ArrayScores[i].substring(ArrayScores[i].lastIndexOf(".") + 1, ArrayScores[i].length());
			}
		}
		
		return res;
	}
		
	private void interfazCuandoNoHayPartituras() {
		TextView textoColeccionVacia = (TextView) findViewById(R.id.textoColeccionVacia);
		textoColeccionVacia.setVisibility(0);
		Button tienda = (Button) findViewById(R.id.tienda);
		tienda.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(new Login_Utils(ctx).isOnline()){
					Intent i = new Intent(MainScreenActivity.this, MainActivityStore.class);
					startActivity(i);
				}else{
					Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
				}				
			}	
		});
		
		tienda.setVisibility(0);
		scores_gallery = (GridView) findViewById(R.id.gV_scores);
		scores_gallery.setVisibility(8);
	}
		
	//  Método que coge los archivos de las partituras en el 
	//  dispositivo y los muestra en la pantalla principal
	public void ColocarFicheros(){ 
		ficheros = leeFicheros();
		infoFicheros = darInfoFicheros(ficheros);
				
		for (int i = 0; i < ficheros.length; i++){
			Score ss = new Score(infoFicheros[1][i], infoFicheros[0][i], null, infoFicheros[2][i], infoFicheros[4][i]);
			
			// Binds all strings into an array
			arraylist.add(ss);
		}
	}

	public void borrarElementos(String[] ficheros2){
		List<Score> elementosAEliminar = new ArrayList<Score>();
	   		
	   		for(int i = 0; i < ficherosLength(); i++){
	   					   	   			
	   			if(mSelected.containsKey(i)){
	   				f_toDelete = new File(Environment.getExternalStorageDirectory() + path + ficheros2[i]);	
	   				f_image_toDelete = new File(Environment.getExternalStorageDirectory() + image_path + ficheroAImagen(ficheros2[i]));
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
   			interfazCuandoNoHayPartituras();
   		}
   		mSelected.clear();
	}
		
}