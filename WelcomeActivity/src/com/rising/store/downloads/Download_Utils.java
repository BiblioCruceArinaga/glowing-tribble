package com.rising.store.downloads;

import java.net.URL;

public class Download_Utils {

	
	public String FileNameURL(URL urlComplete){
		
		String urlCompleto = urlComplete.toString();
		int position = urlCompleto.lastIndexOf('/');
		
		String name = urlCompleto.substring(position, urlCompleto.length());
		
		return name;
	}	
	
}
