package com.rising.mainscreen;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.rising.drawing.R;

public class CreateFolders {
	
	private Context ctx;
	private MainScreen_Utils MSUTILS;
	
	public CreateFolders(Context context){
		this.ctx = context;
		this.MSUTILS = new MainScreen_Utils();
	}
	
	public void createScoreFolder(){
		File file=new File(Environment.getExternalStorageDirectory() + MSUTILS.getPath());
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
		File file=new File(Environment.getRootDirectory() + MSUTILS.getPath());
        if(!file.exists()) {
            boolean res = file.mkdirs();
            
            if (!res) {
            	if (!file.isDirectory()) {
            		
            		//  No se pudo crear el directorio, muy probablemente por los permisos
            		Toast.makeText(ctx, R.string.error_folder, Toast.LENGTH_LONG).show();
            		//finish();
            	}
            }
        }
	} 
	
	public void createImageFolder(){
		File file=new File(Environment.getExternalStorageDirectory() + MSUTILS.getImage_path());
        if(!file.exists()) {
            boolean res = file.mkdirs();
            if (!res) {
            	if (!file.isDirectory()) {
            		createImageFolderInternal();
            	}
            }
        }
	}
	
	public void createImageFolderInternal(){
		File file=new File(Environment.getRootDirectory() + MSUTILS.getImage_path());
        if(!file.exists()) {
            boolean res = file.mkdirs();
            if (!res) {
            	if (!file.isDirectory()) {
            		
            		//  No se pudo crear el directorio, muy probablemente por los permisos
            		Toast.makeText(ctx, R.string.error_folder, Toast.LENGTH_LONG).show();
            		//finish();
            	}
            }
        }
	}

}
