package com.rising.mainscreen;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.rising.drawing.MainActivity;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.Login_Errors;
import com.rising.login.Login_Utils;
import com.rising.mainscreen.preferencies.PreferenciesActivity;
import com.rising.mainscreen.preferencies.SendFeedback_Fragment;
import com.rising.security.DownloadScoresEncrypter;
import com.rising.store.MainActivityStore;

public class InterfazConPartituras {

	private Context ctx;
	private String[] ficheros;
	private Dialog Incorrect_User;
	private GridView scores_gallery;
	private String[][] infoFicheros;
	private ScoresAdapter s_adapter;
	private ArrayList<Score> arraylist = new ArrayList<Score>();
	
	//Clases usadas
	private PDF_Methods PDF; 
	private RecopilarInfoFicheros INFOFICHEROS;
	private MainScreen_Utils MSUTILS;
	private Configuration CONF;
	private Ordenar_Partituras ORDENAR;
	
	//URLs que quiero quitar de aquí
	public String path = "/.RisingScores/scores/";
	public String image_path = "/.RisingScores/scores_images/";
	
	public InterfazConPartituras(Context context, GridView Scores_gallery){
		this.ctx = context;
		this.scores_gallery = Scores_gallery;
		this.INFOFICHEROS = new RecopilarInfoFicheros();
		this.MSUTILS = new MainScreen_Utils();
		this.CONF = new Configuration(ctx);
		this.ORDENAR = new Ordenar_Partituras(ctx, s_adapter);
	}
	
	public void interfazCuandoHayPartituras(){
		
		this.ficheros = INFOFICHEROS.leeFicheros();
		
		this.PDF = new PDF_Methods(ctx, ficheros);
		
		infoFicheros = INFOFICHEROS.darInfoFicheros(ficheros);
				
		Incorrect_User = new Dialog(ctx, R.style.cust_dialog);
		Incorrect_User.setContentView(R.layout.incorrect_user_dialog);
		Incorrect_User.setTitle(R.string.incorrect_user);
		
		for (int i = 0; i < INFOFICHEROS.ficherosLength(); i++){
			 Score ss = new Score(infoFicheros[1][i], infoFicheros[0][i], infoFicheros[3][i], infoFicheros[2][i], infoFicheros[4][i]);
			 arraylist.add(ss);
		}
		
		s_adapter = new ScoresAdapter(ctx, arraylist);
		
		scores_gallery = (GridView) ((Activity)ctx).findViewById(R.id.gV_scores);
		scores_gallery.setAdapter(s_adapter);
		scores_gallery.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
							
		/*scores_gallery.setMultiChoiceModeListener(new MultiChoiceModeListener(){

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
			   	   		for(int i = 0; i < scores_gallery.getCount(); i++) {
			   	   			scores_gallery.setItemChecked(i, true);
			    	   	}
				        return true;
				             
			   	   	case R.id.s_none:
			    		for(int i = 0; i < scores_gallery.getCount(); i++) {
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
		        ficheros2 = INFOFICHEROS.leeFicheros();
		        
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
	});*/
		
		scores_gallery.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("Position", ficheros[position]);
				
				//Si es un PDF abre el PDF, si no, el otro. 
				if(MSUTILS.ComprobarExtensionFichero(ficheros[position])){
	
					PDF.AbrirPDFExterno(position);
					 				    
				}else{
					if(new DownloadScoresEncrypter(ctx, infoFicheros[0][position] + CONF.getUserId()).DescryptAndConfirm(ficheros[position])){
						Intent i = new Intent(ctx, MainActivity.class);
						i.putExtra("score", ficheros[position]);
		
						ctx.startActivity(i);
					}else{
						Incorrect_User.show();
					}
				}
			} 
		});
	
	}
	
	public void MainScreenMenu(int item){
		switch (item) {
			case R.id.store_button:
				if(new Login_Utils(ctx).isOnline()){
					Intent i = new Intent(ctx, MainActivityStore.class);
					ctx.startActivity(i);
					((Activity)ctx).finish();
				}else{
					new Login_Errors(ctx).errLogin(4);
				}
				break;
				
			case R.id.subir_archivo:
				PDF.subirArchivo();
				break;
				
			case R.id.sort_author:
				ORDENAR.listarAutores();
				break;
				
			case R.id.sort_instrument:
				ORDENAR.listarInstrumentos();
				break;
				
			case R.id.sort_name:
				ORDENAR.ordenarPorNombre();
				break;
			
			case R.id.sort_format:
				ORDENAR.listarFormatos();
				break;
				
			case R.id.show_all:
				ORDENAR.mostrarTodas();
				break;
	        
	        case R.id.about:
	        	((MainScreenActivity)ctx).AboutDialog();
	    		break;
	                
	        case R.id.feedback:
	        	Intent i = new Intent(ctx, SendFeedback_Fragment.class);
	        	ctx.startActivity(i);
	    		((Activity)ctx).finish();
	            break;
	            
	        case R.id.mis_datos:
	        	
	        	Intent in = new Intent(ctx, PreferenciesActivity.class);
	        	ctx.startActivity(in);
	        	((Activity)ctx).finish();
	        	break;
		}
	}
}