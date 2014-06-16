package com.rising.security;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DownloadScoresEncrypter {

	private String Id_Score = "DC101AB52CF894CEE52F6173";
	private String User_Token;
	private String path = "/.RisingScores/scores/";
	BlockCipher engine;
	
	public DownloadScoresEncrypter(Context ctx, String token){
		this.User_Token = token.toLowerCase();
		Log.w("Token", ""+token);
		engine = new DESedeEngine();
	}
	
	public byte[] Encrypt(byte[] key, String plainText) {
        byte[] ptBytes = plainText.getBytes();
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));
        cipher.init(true, new KeyParameter(key));
        byte[] rv = new byte[cipher.getOutputSize(ptBytes.length)];
        int tam = cipher.processBytes(ptBytes, 0, ptBytes.length, rv, 0);
        Log.d("Dos datos Encrypt", "Tam: "+ tam + ", plainText tamaño: " + plainText.length());
        try {
            cipher.doFinal(rv, tam);
        } catch (Exception ce) {
            ce.printStackTrace();
        }
        return rv;
    }

    public String Decrypt(byte[] key, byte[] cipherText) {
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));
        cipher.init(false, new KeyParameter(key));
        byte[] rv = new byte[cipher.getOutputSize(cipherText.length)];
        int tam = cipher.processBytes(cipherText, 0, cipherText.length, rv, 0);
        Log.d("Dos datos Decrypt", "Tam: "+ tam + ", CipherText tamaño: " + cipherText.length);
        try {
            cipher.doFinal(rv, tam);
        } catch (Exception ce) {
        	ce.printStackTrace();
            Log.i("Error en Decrypt", "Error aquí: " + ce.getMessage());
        }
        return new String(rv).trim();
    }
    	
	public void CreateAndInsert(String url){	
		 File f = null;
		 FileWriter fw = null;
		 PrintWriter pw = null;
		 		 
		 try{
			 f = new File(Environment.getExternalStorageDirectory() + path + FileNameURL(url));
			 fw = new FileWriter(f);
			 pw = new PrintWriter(fw);			 
			 Log.i("UserToken", User_Token);
			 byte[] coded = Base64.encode(User_Token.getBytes());
		     String cipherText = new String(coded);
			 
		     pw.println(cipherText);
		     
		     Log.i("Text", "UserToken: " + User_Token + ", Cipher: " + cipherText);		     
			 			 
		 }catch(Exception e){
			 Log.e("Error", e.getMessage());
		 }finally{
			 try {
				 pw.close();
				 fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			 
		 }
	 }

	public boolean DescryptAndConfirm(String fichero){
		File f = null;
		FileReader fr = null;
		BufferedReader br = null;
		String tempp = "";
		 
		try{
			f = new File(Environment.getExternalStorageDirectory() + path + fichero);
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			tempp = br.readLine();
			Log.i("Complettempp", tempp);
					 
		}catch (EOFException EOF){
		}catch(IOException IOE){
			IOE.printStackTrace(); 
		}finally{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		 
		byte[] decoded = Base64.decode(tempp);
	    String strDecoded = new String(decoded).toLowerCase();
	    		
	    //Este User_Token lo está dando incorrecto
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
	 
	public String getSecurityLine(){
		byte[] rr = null;
		try {
			rr = Encrypt(Id_Score.getBytes(), User_Token);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			Log.i("Resultado Unión", rr.toString());
		}
		return null;
	}
	
}