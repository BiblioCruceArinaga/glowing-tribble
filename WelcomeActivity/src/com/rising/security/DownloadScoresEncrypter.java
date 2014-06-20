package com.rising.security;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.bouncycastle.util.encoders.Base64;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DownloadScoresEncrypter {

	private String User_Token;
	private String path = "/.RisingScores/scores/";
	
	//Constructor 
	public DownloadScoresEncrypter(Context ctx, String token){
		this.User_Token = token.toLowerCase();
	}
    
	//Crea un archivo e inserta en él la linea de seguridad
	public void CreateAndInsert(OutputStream f){	
		 		 
		 try{
			 Log.i("UserToken", User_Token);
			 byte[] coded = Base64.encode(User_Token.getBytes(Charset.forName("UTF-8")));			 
			 
			 //Dar la vuelta al array para que el signo = esté al principio y se pueda colocar la cadena al final
			 byte[] aux = new byte[coded.length];
			 for(int i = 0; i < coded.length; i++){
				 aux[i] = coded[aux.length - (i + 1)];
			 }	 
						 
			 String codid = new String(coded);
			 Log.i("Coded", codid);
			 String auxi = new String(aux);
			 Log.i("Aux", auxi);
			 
			 f.write(aux);
			 			 
		 }catch(Exception e){
			 Log.e("Error DownloadEncrypter", e.getMessage());
		 }
	 }

	//Desencripta la linea de seguridad en el archivo que se le pase y devuelve true o false si es igual o no
	public boolean DescryptAndConfirm(String fichero){
		
		String tempp = getSecurityLine(fichero); 
		
		//Devuelve un array de bytes y convierte estos en una string
		byte[] decoded = Base64.decode(tempp);
	    String strDecoded = new String(decoded).toLowerCase();
	    		
	    Log.d("Comparation", "Decode: " + strDecoded + ", UT: " + User_Token);
		
	    if(strDecoded.equals(User_Token)){
	    	return true;
		}else{
			return false;
		}		 
	 }
	 	 
	//Esto extrae el nombre del fichero a partir de la URL. Está en varias clases, debería estar solo en una común a todas. 
	public String FileNameURL(String urlCompleto){
			
		int position = urlCompleto.lastIndexOf('/');
			
		String name = urlCompleto.substring(position, urlCompleto.length());
			
		return name;
	}	
	 
	//Coge la linea de seguridad del archivo que se le pasa
	public String getSecurityLine(String fichero){
		File f = null;
		String line = "";
		FileInputStream fis = null;
				
		try{
			f = new File(Environment.getExternalStorageDirectory() + path + fichero);
			
			byte[] ArrayLine = new byte[SecurityLineLength(f)];
						
			fis = new FileInputStream(f);
			
			byte b = 0;
			int i = 0;
			
			while((b = (byte)fis.read())!=-128){
			}
			Log.i("LineLength", ""+SecurityLineLength(f));
			while((b = (byte)fis.read()) != -1){
				ArrayLine[i] = b;
				i++;
			}
			
			byte[] ArrayAux = new byte[ArrayLine.length];
			for(int j = 0; j < ArrayLine.length; j++){
				ArrayAux[j] = ArrayLine[ArrayAux.length - (j + 1)];
			}
			
			line = new String(ArrayAux);
			Log.i("Complettempp", line);
					 
		}catch (EOFException EOF){
		}catch(IOException IOE){
			IOE.printStackTrace(); 
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return line;
	}
	
	//Mide la longitud de la linea de seguridad del archivo que se le pasa
	public int SecurityLineLength(File f){
		FileInputStream fis = null;
		int length = 0;
		byte bb = 0;
		try{
			fis = new FileInputStream(f);
			while((bb = (byte)fis.read())!=-128){
			}
			
			while((bb = (byte)fis.read()) != -1){
				length++;
			}
			
		}catch(Exception e){
			Log.e("ExceptionLineLength", e.getMessage());
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return length;
	}
	
}