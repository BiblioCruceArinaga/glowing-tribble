package com.rising.store;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;

import com.rising.drawing.MainActivity;
import com.rising.login.Configuration;
import com.rising.mainscreen.MainScreen_Errors;
import com.rising.security.DownloadScoresEncrypter;

public class Store_Utils {
	
	//Variables
	private Context ctx;
		
	//Clases usadas
	private Configuration CONF;
	private MainScreen_Errors ERRORS;
	
	public Store_Utils(Context context){
		this.ctx = context;
		this.CONF = new Configuration(ctx);
		this.ERRORS = new MainScreen_Errors(ctx);
	}
	
	public boolean spaceOnDisc(){
		if(Environment.getExternalStorageDirectory().getFreeSpace() < 30000){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean buscarArchivos(String name, String path){
		File f = new File(Environment.getExternalStorageDirectory() + path + name);
		
		if(f.exists()){
			return true;
		}else{
			return false;
		}
	}
	
	public void AbrirFichero(String name, String path){
				
		if(new DownloadScoresEncrypter(ctx, name + CONF.getUserId()).DescryptAndConfirm(path)){
			Intent in = new Intent(ctx, MainActivity.class);
			in.putExtra("score", path);
			
			ctx.startActivity(in);
		}else{
			ERRORS.Incorrect_User();
		}		
	}
	
	public String FileNameString(String urlComplete){
		
		String urlCompleto = urlComplete.toString();
		int position = urlCompleto.lastIndexOf('/');
		
		String name = urlCompleto.substring(position + 1, urlCompleto.length());
		
		return name;
	}

	public void DialogCompra(String title, String message, String PositiveButton, String NegativeButton){
		AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ctx);  
        dialogo1.setTitle(title);  
        dialogo1.setMessage(message);            
        dialogo1.setCancelable(false);  
        dialogo1.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialogo1, int id) {  
                
            }  
        });  
        dialogo1.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialogo1, int id) {  
                dialogo1.cancel();
            }  
        });            
        dialogo1.show();
	}
}