package com.rising.mainscreen;


public class MainScreen_Utils {
	
	private String path = "/.RisingScores/scores/";
	private String image_path = "/.RisingScores/scores_images/";
		
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	public boolean ComprobarExtensionFichero(String nombre){
									
		int i = nombre.lastIndexOf('.');
			
		String pdf = nombre.substring(i+1, nombre.length()).toLowerCase();
			
		if(pdf.equals("pdf")){
			return true;
		}else{
			return false;
		}		
	}
}
