package com.rising.mainscreen;

import java.io.File;

import android.os.Environment;

public class RecopilarInfoFicheros {

	private String[] ficheros;
	
	//URLs
	public String path = "/.RisingScores/scores/";
	public String image_path = "/.RisingScores/scores_images/";
	
	//Clases usadas
	private MainScreen_Utils MSUTILS;
	
	
	public RecopilarInfoFicheros(){
		this.ficheros = leeFicheros();
		this.MSUTILS = new MainScreen_Utils();
	}
	
	//  Extrae el autor, el nombre, la imagen, el instrumento y el formato de todas las partituras existentes en el dispositivo
	public String[][] darInfoFicheros(String[] ArrayScores) {
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
				res[3][i] = MSUTILS.ficheroAImagen(ArrayScores[i]);	// Imagen
				res[4][i] = ArrayScores[i].substring(ArrayScores[i].lastIndexOf(".") + 1, ArrayScores[i].length());
			}else{
				String[] dataSplit = ArrayScores[i].split("_");
				//String imagenFichero = ArrayScores[i].substring(0, ArrayScores[i].lastIndexOf("."));
						
				res[0][i] = dataSplit[0].replace("-", " ");	//  Nombre de la obra
				res[1][i] = dataSplit[1].replace("-", " ");	//  Autor
				res[2][i] = dataSplit[2].substring(0, dataSplit[2].indexOf(".")); //  Instrumento
				res[3][i] = MSUTILS.ficheroAImagen(ArrayScores[i]);	// Imagen
				res[4][i] = ArrayScores[i].substring(ArrayScores[i].lastIndexOf(".") + 1, ArrayScores[i].length()); //Formato
			}
		}
		
		return res;
	}
		
	public int ficherosLength(){
		int ficherosLength;
		if (ficheros != null) ficherosLength = ficheros.length;
		else ficherosLength = 0;
		return ficherosLength;
	}
	
	public String[] leeFicheros(){
		File f = new File(Environment.getExternalStorageDirectory() + path);
		String[] lista = f.list();
		return lista;
	}
	
}