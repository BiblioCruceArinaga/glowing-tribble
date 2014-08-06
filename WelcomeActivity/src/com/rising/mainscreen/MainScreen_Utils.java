package com.rising.mainscreen;


public class MainScreen_Utils {
	
	public String path = "/.RisingScores/scores/";
	public String image_path = "/.RisingScores/scores_images/";

	public boolean ComprobarExtensionFichero(String nombre){
									
		int i = nombre.lastIndexOf('.');
			
		String pdf = nombre.substring(i+1, nombre.length()).toLowerCase();
			
		if(pdf.equals("pdf")){
			return true;
		}else{
			return false;
		}		
	}
	
	public String ficheroAImagen(String fichero){
		String imagenFichero = fichero.substring(0, fichero.lastIndexOf("."));
		return imagenFichero + ".jpg";
	}
	
}
