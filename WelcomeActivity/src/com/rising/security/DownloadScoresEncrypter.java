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

//Clase que se encarga de la linea de seguridad de las partituras que se descargan
public class DownloadScoresEncrypter {

	//Variables
	private String User_Token;
	
	//Folder
	private String path = "/.RisingScores/scores/";
	
	public DownloadScoresEncrypter(Context ctx, String token){
		this.User_Token = token.toLowerCase();
	}
    
	public void CreateAndInsertSecurityLine(OutputStream f){
		try{
			 Log.i("UserToken", User_Token);
			 byte[] securityLine = Base64.encode(User_Token.getBytes(Charset.forName("UTF-8")));			 
			 
			 byte[] turnTheLine = new byte[securityLine.length];
			 for(int i = 0; i < securityLine.length; i++){
				 turnTheLine[i] = securityLine[turnTheLine.length - (i + 1)];
			 }	 
						 
			 String SecurityLine = new String(securityLine);
			 Log.i("SecurityLine", SecurityLine);
			 String lineTurned= new String(turnTheLine);
			 Log.i("lineTurned", lineTurned);
			 
			 try{
				 f.write(turnTheLine);
			 }catch(Exception e){
				 Log.e("Error Writing DownloadEncrypter", e.getMessage());
			 }		 
		 }catch(Exception e){
			 Log.e("Error DownloadEncrypter", e.toString());
		 }
	 }

	public boolean DescryptAndConfirm(String fichero){
		
		Log.i("Fichero P", ""+fichero);
		String securityLine = getSecurityLine(fichero); 
		
		Log.i("Fichero P", "paso 2");
		
		byte[] decodedLine = Base64.decode(securityLine);
	    String strDecoded = new String(decodedLine).toLowerCase();
	    		
	    Log.d("Comparation", "Decode: " + strDecoded + ", UT: " + User_Token);
		
	    if(strDecoded.equals(User_Token)){
	    	return true;
		}else{
			return false;
		}		 
	 }
	 	 	 
	public String getSecurityLine(String fichero){
		File f = null;
		String line = "";
		FileInputStream fis = null;
		
		Log.i("Fichero S", ""+fichero);
		
		try{
			f = new File(Environment.getExternalStorageDirectory() + path + fichero);
			Log.i("Fichero SS", path+fichero);
			byte[] ArrayLine = new byte[SecurityLineLength(f)];
						
			fis = new FileInputStream(f);
			
			byte b = 0;
			int i = 0;
			Log.i("Fichero SSS", path+" + más + "+fichero);
			while((b = (byte)fis.read())!=-128){
			}
			
			Log.i("LineLength", ""+SecurityLineLength(f));
			
			while((b = (byte)fis.read()) != -1){
				ArrayLine[i] = b;
				i++;
			}
			
			byte[] ArrayLineTurned = new byte[ArrayLine.length];
			for(int j = 0; j < ArrayLine.length; j++){
				ArrayLineTurned[j] = ArrayLine[ArrayLineTurned.length - (j + 1)];
			}
			
			line = new String(ArrayLineTurned);
			Log.i("CompletedLine", line);
					 
		}catch (EOFException EOF){
			Log.e("EOFException Encrypt", EOF.getMessage());
		}catch(IOException IOE){
			Log.e("IOException Encrypt", IOE.getMessage());
		}catch(Exception e){
			Log.e("Exception Encrypt", e.getMessage());
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				Log.e("Finally IOException Encrypt", e.getMessage());
			}
		}
		return line;
	}
	
	public int SecurityLineLength(File f){
		Log.i("Fichero L", path+f);
		FileInputStream fis = null;
		int length = 0;
		@SuppressWarnings("unused")
		byte bb = 0;
		Log.i("Fichero LL", path+f);
		try{
			fis = new FileInputStream(f);
			while((bb = (byte)fis.read())!=-128){
			}
			
			while((bb = (byte)fis.read()) != -1){
				length++;
			}
			
		}catch(Exception e){
			Log.e("ExceptionLineLength Encrypt", e.getMessage());
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				Log.e("Finally ExceptionLineLength Encrypt", e.getMessage());
			}
		}
		
		return length;
	}
	
}