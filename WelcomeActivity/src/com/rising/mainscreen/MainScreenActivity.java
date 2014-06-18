package com.rising.mainscreen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import com.rising.login.SessionManager;
import com.rising.mainscreen.ChangePassword.OnPasswordChanging;
import com.rising.mainscreen.EraseAccount.OnTaskCompleted;
import com.rising.mainscreen.SendFeedback.OnSendingFeedback;
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
	String path = "/RisingScores/scores/";
	String image_path = "/RisingScores/scores_images/";
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
	Context context;
	Dialog Incorrect_User;
		
	//  Recibir la señal del proceso que elimina la cuenta
	private OnTaskCompleted listener = new OnTaskCompleted() {
	    public void onTaskCompleted(int details) {       
			switch (details) {
				case 1: {
					Toast.makeText(getApplicationContext(), 
							R.string.cuenta_eliminada, Toast.LENGTH_LONG).show();
					
					session.logoutUser();
					finish();
					break;
				}
				case 2: {
					Toast.makeText(getApplicationContext(), 
							R.string.error_eliminar_cuenta_fallo_verif, Toast.LENGTH_LONG).show();
					break;
				}
				case 3: {
					Toast.makeText(getApplicationContext(), 
							R.string.error_eliminar_cuenta_identidad, Toast.LENGTH_LONG).show();
					break;
				}
				default:
					Toast.makeText(getApplicationContext(), 
						R.string.error_eliminar_cuenta, Toast.LENGTH_LONG).show();
			}
			
			MDialog.dismiss();
	    }
	};
	
	//  Recibir la señal del proceso que cambia la contraseña
	private OnPasswordChanging listenerPass = new OnPasswordChanging() {
	    public void onPasswordChanged(int details) {       
			switch (details) {
				case 1: {
					Toast.makeText(getApplicationContext(), 
							R.string.mis_datos_clave_cambiada, Toast.LENGTH_LONG).show();
					break;
				}
				case 2: {
					Toast.makeText(getApplicationContext(), 
							R.string.mis_datos_error_verif, Toast.LENGTH_LONG).show();
					break;
				}
				case 3: {
					Toast.makeText(getApplicationContext(), 
							R.string.mis_datos_clave_erronea, Toast.LENGTH_LONG).show();
					break;
				}
				case 4: {
					Toast.makeText(getApplicationContext(), 
							R.string.err_login_unknown, Toast.LENGTH_LONG).show();
					break;
				}
				default:
					Toast.makeText(getApplicationContext(), 
							R.string.err_login_unknown, Toast.LENGTH_LONG).show();
			}		
			MDialog.dismiss();
	    }
	};
	
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
			Toast.makeText(context, "Falló al actualizar el saldo", Toast.LENGTH_LONG).show();
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		
		setContentView(R.layout.mainscreen_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
		mSelected = new HashMap<Integer, Boolean>();
		conf = new Configuration(this);
		session = new SessionManager(getApplicationContext());
		ficheros = leeFicheros();
		context = this;
		session.checkLogin();
		fid = session.getFacebookId();
		
		createScoreFolder();
		createImageFolder();
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
		infoFicheros = darInfoFicheros(ficheros);
				
		Incorrect_User = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
		
		Incorrect_User.setContentView(R.layout.incorrect_user_dialog);
		Incorrect_User.setTitle(R.string.incorrect_user);
		
		for (int i = 0; i < ficherosLength(); i++){
			 Score ss = new Score(infoFicheros[1][i], infoFicheros[0][i], infoFicheros[3][i], infoFicheros[2][i]);
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
				Log.i("Eso", "Aquí1");
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.modal_details, menu);
				mode.setTitle(R.string.title);
		        mode.setSubtitle(R.string.subtitle);
		        ficheros2 = leeFicheros();
		        Log.i("Eso", "Aquí2");
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
						/*
			if(new DownloadScoresEncrypter(context, infoFicheros[0][position]+conf.getUserId()).DescryptAndConfirm(ficheros[position])){
				Intent i = new Intent(MainScreenActivity.this, MainActivity.class);
				i.putExtra("score", ficheros[position]);
						
				startActivity(i);
			}else{
				Incorrect_User.show();
			}
			*/
			
			Intent i = new Intent(MainScreenActivity.this, MainActivity.class);
			i.putExtra("score", ficheros[position]);
					
			startActivity(i);
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
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch(NullPointerException n) {
			return false;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
			case R.id.store_button:
				if(isOnline()){
					Intent i = new Intent(MainScreenActivity.this, MainActivityStore.class);
					startActivity(i);
					finish();
				}else{
					Toast.makeText(this, R.string.connection_err, Toast.LENGTH_LONG).show();	
				}
				return true;
	    	
			case R.id.sort_author:
				listarAutores();
				return true;
				
			case R.id.sort_instrument:
				listarInstrumentos();
				return true;
				
			case R.id.sort_name:
				ordenarPorNombre();
				return true;
				
			case R.id.show_all:
				mostrarTodas();
				return true;

	    	case R.id.session_button:
	    		if(fid > -1){
	    			session.LogOutFacebook();	    			
	    		}else{
	    			session.logoutUser();
	    		}
	    		conf.setUserEmail("");
	    		conf.setUserId("");
	    		conf.setUserMoney(0);
	    		conf.setUserName("");
	        	finish();
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
	            
	        case R.id.terminos_condiciones:
	        	MDialog = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
	    		MDialog.setContentView(R.layout.terminos_condiciones);
	    		MDialog.setTitle(R.string.terminos_condiciones);
	    		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    		MDialog.show();
	            return true;
	            
	        case R.id.condiciones_compra:
	        	MDialog = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
	    		MDialog.setContentView(R.layout.condiciones_compra);
	    		MDialog.setTitle(R.string.condiciones_compra);
	    		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
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
						if(isOnline()){	
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
							Toast.makeText(context, R.string.connection_err, Toast.LENGTH_LONG).show();	
						}
					}
	    	    });
	    		
	            return true;
	            
	        case R.id.mis_datos:
	        		        	        	
	        	MDialog = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
	        	MDialog.setTitle(R.string.mis_datos);
	    		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);	        	
	        	
	        	if(fid > -1){
	        		MDialog.setContentView(R.layout.mis_datos_facebook);
	        		final TextView nombreF = (TextView) MDialog.findViewById(R.id.misDatosFacebookNombre);
		    		final TextView emailF = (TextView) MDialog.findViewById(R.id.misDatosFacebookEmail);
		    		final TextView saldoF = (TextView) MDialog.findViewById(R.id.misDatosFacebookSaldo);
		    		
		    		nombreF.setText(nombreF.getText() + " " + session.getName());
		    		emailF.setText(emailF.getText() + " " + session.getMail()); 
		    		saldoF.setText(saldoF.getText() + " " + conf.getUserMoney());
		    		saldoF.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money_ico, 0);
	        		
	        	}else{
	        		MDialog.setContentView(R.layout.mis_datos);
		        	
		        	final TextView nombre = (TextView) MDialog.findViewById(R.id.misDatosNombre);
		    		final TextView email = (TextView) MDialog.findViewById(R.id.misDatosEmail);
		    		final TextView saldo = (TextView) MDialog.findViewById(R.id.misDatosSaldo);
		    		final EditText claveVieja = (EditText) MDialog.findViewById(R.id.misDatosClaveVieja);
		    	    final EditText claveNueva = (EditText) MDialog.findViewById(R.id.misDatosClaveNueva);
		    	    final EditText claveRepetir = (EditText) MDialog.findViewById(R.id.misDatosClaveRepetir);
		    	    
		    		nombre.setText(nombre.getText() + " " + conf.getUserName());
		    		email.setText(email.getText() + " " + conf.getUserEmail()); 
		    		saldo.setText(saldo.getText() + " " + conf.getUserMoney());
		    		saldo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money_ico, 0);
		    				    		
		    		Button misDatosBoton = (Button)MDialog.findViewById(R.id.misDatosBoton);
		    	    misDatosBoton.setOnClickListener(new Button.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if(isOnline()){	
								//  Usuario normal
								if (fid == -1) {
									
									if ( 
										( claveVieja.getText().length() == 0 ) ||
										( claveNueva.getText().length() == 0 ) ||
										( claveRepetir.getText().length() == 0 )
									) {
										Toast.makeText(getApplicationContext(), 
											R.string.err_campos_vacios, Toast.LENGTH_LONG).show();
									} else {
										if (!claveNueva.getText().toString().equals(
												claveRepetir.getText().toString())) {
											Toast.makeText(getApplicationContext(), 
												R.string.err_pass, Toast.LENGTH_LONG).show();
										}
										else {
											new ChangePassword(MainScreenActivity.this, listenerPass).execute(
												session.getMail(), 
												claveVieja.getText().toString(), 
												claveNueva.getText().toString());    
						        			
											claveVieja.setText("");
											claveNueva.setText("");
											claveRepetir.setText("");
										}
									}
								}
							}else{
								Toast.makeText(context, R.string.connection_err, Toast.LENGTH_LONG).show();	
							}
						}
		    	    	
		    	    });
	        	}

	    	    MDialog.show();
	            return true;
	            
	        case R.id.eliminar_cuenta:
	        	MDialog = new Dialog(MainScreenActivity.this, R.style.cust_dialog);
	    		MDialog.setContentView(R.layout.eliminar_cuenta);
	    		MDialog.setTitle(R.string.eliminar_cuenta);
	    		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    		MDialog.show();
	    		
	    		final EditText clave = (EditText) MDialog.findViewById(R.id.claveEliminarCuenta);
	    		if (fid > -1) {
	    			clave.setVisibility(View.INVISIBLE);
	    			
	    			TextView texto = (TextView) MDialog.findViewById(R.id.textoEliminarCuenta);
	    			texto.setText(R.string.esta_seguro_facebook);
	    		}
	    		
	    		Button botonEliminarCuenta = (Button)MDialog.findViewById(R.id.botonEliminarCuenta);
	    	    botonEliminarCuenta.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if(isOnline()){
							String mail = session.getMail();
							
							//  Usuario normal
							if (clave.getText().length() > 0) {
								new EraseAccount(MainScreenActivity.this, listener).execute(
										mail, clave.getText().toString());    
			        			clave.setText("");
							}
							else {
								
								//  Usuario de facebook
								if (fid > -1) {
									new EraseAccount(MainScreenActivity.this, listener).execute(
											mail, fid + "");    
				        			clave.setText("");
								}
								
								else {
									Toast.makeText(getApplicationContext(), 
										R.string.err_campos_vacios, Toast.LENGTH_LONG).show();
								}
							}
						}else{
							Toast.makeText(context, R.string.connection_err, Toast.LENGTH_LONG).show();	
						}
					}
	    	    	
	    	    });
	            
	            return true;
	        	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void createScoreFolder(){
		File file=new File(Environment.getExternalStorageDirectory() + path);
        if(!file.exists()) {
            boolean res = file.mkdirs();
            if (!res) {
            	if (!file.isDirectory()) {
            		createScoreFolderInternal();
            	}
            }
        }
	}
	

	public void createScoreFolderInternal(){
		File file=new File(Environment.getRootDirectory() + path);
        if(!file.exists()) {
            boolean res = file.mkdirs();
            
            if (!res) {
            	if (!file.isDirectory()) {
            		
            		//  No se pudo crear el directorio, muy probablemente por los permisos
            		Toast.makeText(getApplicationContext(), R.string.error_folder, Toast.LENGTH_LONG).show();
            	}
            }
        }
	} 
	
	public void createImageFolder(){
		File file=new File(Environment.getExternalStorageDirectory() + image_path);
        if(!file.exists()) {
            boolean res = file.mkdirs();
            if (!res) {
            	if (!file.isDirectory()) {
            		createImageFolderInternal();
            	}
            }
        }
	}
	
	//Si falla la creación en el directorio externo
	public void createImageFolderInternal(){
		File file=new File(Environment.getRootDirectory() + image_path);
        if(!file.exists()) {
            boolean res = file.mkdirs();
            if (!res) {
            	if (!file.isDirectory()) {
            		
            		//  No se pudo crear el directorio, muy probablemente por los permisos
            		Toast.makeText(getApplicationContext(), R.string.error_folder, Toast.LENGTH_LONG).show();
            	}
            }
        }
	}
	
	public String[] leeFicheros(){
		File f = new File(Environment.getExternalStorageDirectory() + path);
		String[] lista = f.list();
		//Habría que poner algo de seguridad y que solo muestre los archivos acabados en smts
		return lista;
	}
	
	//  Extrae el autor, el nombre, la imagen y el instrumento de
	//  todas las partituras existentes en el dispositivo
	private String[][] darInfoFicheros(String[] ArrayScores) {
		String[][] res;
		
		int len;
		if (ArrayScores != null) len = ArrayScores.length;
		else len = 0;
		
		res = new String[4][len];
		
		for(int i=0; i < len; i++){
			String[] dataSplit = ArrayScores[i].split("_");
			//String imagenFichero = ArrayScores[i].substring(0, ArrayScores[i].lastIndexOf("."));
					
			res[0][i] = dataSplit[0].replace("-", " ");	//  Nombre de la obra
			res[1][i] = dataSplit[1].replace("-", " ");	//  Autor
			res[2][i] = dataSplit[2].substring(0, dataSplit[2].indexOf("."));	//  Instrumento
			res[3][i] = ficheroAImagen(ArrayScores[i]);	// Imagen
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
				if(isOnline()){
					Intent i = new Intent(MainScreenActivity.this, MainActivityStore.class);
					startActivity(i);
				}else{
					Toast.makeText(context, R.string.connection_err, Toast.LENGTH_LONG).show();
				}				
			}	
		});
		
		tienda.setVisibility(0);
		scores_gallery = (GridView) findViewById(R.id.gV_scores);
		scores_gallery.setVisibility(8);
	}
	
	private void listarAutores() {
		if (s_adapter != null) {
			int size = s_adapter.getCount();
			final CharSequence[] items = new CharSequence[size];
			for (int i=0; i<size; i++) items[i] = s_adapter.getItemAuthor(i);
	
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle(R.string.author_dialog_title);
		    builder.setItems(items, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int item) {
		            s_adapter.filter(s_adapter.getItemAuthor(item));
		        }
		    }).show();
		}
	}
	
	private void listarInstrumentos() {	
		if (s_adapter != null) {
			int size = s_adapter.getCount();
			
			//  Evitar repeticiones
			LinkedHashSet<String> hs = new LinkedHashSet<String>();
			for (int i=0; i<size; i++) {
				hs.add(s_adapter.getItemInstrument(i));
			}
			
			size = hs.size();
			final CharSequence[] items = new CharSequence[size];
			ArrayList<String> al = new ArrayList<String>();
			al.addAll(hs);
			for (int i=0; i<size; i++) items[i] = al.get(i);
	
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle(R.string.instrument_dialog_title);
		    
		    builder.setItems(items, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int item) {
		            s_adapter.filter(s_adapter.getItemInstrument(item));
		        }
		    }).show();
		}
	}
	
	private void ordenarPorNombre() {
		if (s_adapter != null) s_adapter.sortByName();
	}
	
	private void mostrarTodas() {
		if (s_adapter != null) s_adapter.showAll();
	}
	
	//  Método que coge los archivos de las partituras en el 
	//  dispositivo y los muestra en la pantalla principal
	public void ColocarFicheros(){ 
		ficheros = leeFicheros();
		infoFicheros = darInfoFicheros(ficheros);
				
		for (int i = 0; i < ficheros.length; i++){
			Score ss = new Score(infoFicheros[1][i], infoFicheros[0][i], null, infoFicheros[2][i]);
			
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