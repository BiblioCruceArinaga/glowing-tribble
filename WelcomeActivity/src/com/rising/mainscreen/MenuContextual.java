package com.rising.mainscreen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.widget.GridView;
import android.widget.Toast;

import com.rising.drawing.R;

public class MenuContextual {

	private Context ctx;
	private File f_toDelete;
	private File f_image_toDelete;
	private boolean delete;
	private HashMap<Integer, Boolean> mSelected;
	
	//URLs que quiero quitar de aquí
	public String path = "/.RisingScores/scores/";
	public String image_path = "/.RisingScores/scores_images/";
	
	//Clases usadas
	private MainScreen_Utils MSUTILS;
	private RecopilarInfoFicheros INFO_FICHEROS;
	private InterfazNoPartituras INTERFACES;
	
	public MenuContextual(Context context, GridView scores_gallery){
		this.ctx = context;
		this.mSelected = new HashMap<Integer, Boolean>();
		this.MSUTILS = new MainScreen_Utils();
		this.INFO_FICHEROS = new RecopilarInfoFicheros();
		this.INTERFACES = new InterfazNoPartituras(ctx, scores_gallery);
	}	
	
	
	public void borrarElementos(String[] ficheros2, ScoresAdapter s_adapter){
		List<Score> elementosAEliminar = new ArrayList<Score>();
	   		
	   		for(int i = 0; i < INFO_FICHEROS.ficherosLength(); i++){
	   					   	   			
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
	   		
	   		if(delete){
	   			Toast.makeText(ctx, R.string.successDelete, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(ctx, R.string.failDelete, Toast.LENGTH_LONG).show();
			}
				
   		if (s_adapter.isEmpty()) {
   			INTERFACES.interfazCuandoNoHayPartituras();
   		}
   		mSelected.clear();
	}
}