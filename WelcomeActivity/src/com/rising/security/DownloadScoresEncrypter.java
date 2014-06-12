package com.rising.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DownloadScoresEncrypter {

	private String Id_Score = "DC101AB52CF894CEE52F6173";
	private String User_Token;
	private String path = "/.RisingScores/scores/";
	BlockCipher engine;
	
	public DownloadScoresEncrypter(Context ctx, String token){
		this.User_Token = token;
		 engine = new DESedeEngine();
	}
	
	public byte[] Encrypt(byte[] key, String plainText) {
        byte[] ptBytes = plainText.getBytes();
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));
        cipher.init(true, new KeyParameter(key));
        byte[] rv = new byte[cipher.getOutputSize(ptBytes.length)];
        int tam = cipher.processBytes(ptBytes, 0, ptBytes.length, rv, 0);
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
        Log.d("Dos datos", "Tam: "+ tam + ", CipherText tamaño: " + cipherText.length);
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
		 OutputStream os = null;
		 		 
		 try{
			 f = new File(Environment.getExternalStorageDirectory() + path + FileNameURL(url));
			 os = new FileOutputStream(f);
			 byte[] cipherText = Encrypt(Id_Score.getBytes(), User_Token);
			 		 
			 os.write(cipherText);
			 			 
			 Log.i("Encrypt", cipherText[0] + ", " + cipherText[1] + ", " + cipherText[2]);
		 }catch(Exception e){
			 Log.e("Error", e.getMessage());
		 }finally{
			 try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			 
		 }
	 }

	 public boolean DescryptAndConfirm(String fichero){
		 File f = null;
		 FileReader fr = null;
		 char character= ' ';
		 String line = "";
		 String descrypt = "";
		 
		 try{
			 f = new File(Environment.getExternalStorageDirectory() + path + fichero);
			 fr = new FileReader(f);
			 int i = 0;
			 while(i != 20){
				 				 
				 Log.d("Read", ""+fr.read());
				 i++;
				 Log.e("I", i+"");
			 }
			/* while((character = (char)fr.read()) != 'A'){
				 Log.i("Character", ""+character);
				 line = line + character;
			 }*/
			 
		 }catch(Exception e){
			 e.getMessage();
		 }finally{
			 try {
				 fr.close();
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		 
		 try {
			String decryptText = Decrypt(Id_Score.getBytes(), line.getBytes());
			Log.d("Id_Score", Id_Score.getBytes()+"");
			Log.i("descrypt", decryptText);
		 } catch (Exception e) {
			 e.printStackTrace();
			 Log.e("Fallo Decrypt 2", e.getMessage());
		 } 
		 
		 if(line.equals(descrypt)){
			 return true;
		 }else{
			 return false;
		 }
	 }
	 
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