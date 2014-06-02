package com.rising.store;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.nostra13.universalimageloader.core.ImageLoader;

public class DownloadImages extends AsyncTask<String, Integer, String>{
	
//  Comunicaci�n HTTP con el servidor
	HttpPost httppost;
	HttpClient httpcliente;
	private String path = "/RisingScores/scores_images/";
	ImageLoader iml;
	
	//  Contexto
	Context context;
	
	//  Informaci�n obtenida de la base de datos
	String res;
	
	public interface OnDownloadICompleted{
        void onDownloadICompleted();
    }
	
	public interface OnDownloadIFailed{
		void onDownloadIFailed();
	}
	
	private OnDownloadIFailed failedDownloadImage;
	
	private OnDownloadICompleted listenerDownloadImage;
	
	public DownloadImages(OnDownloadICompleted listener, OnDownloadIFailed failed, Context ctx) {
		this.context = ctx;
		this.listenerDownloadImage = listener;
		this.failedDownloadImage = failed;
	}
	
	public String FileNameURL(URL urlComplete){
		
		String urlCompleto = urlComplete.toString();
		int position = urlCompleto.lastIndexOf('/');
		
		String name = urlCompleto.substring(position, urlCompleto.length());
		
		return name;
	}	
			
	@Override
    protected String doInBackground(String... sUrl) {
    	
        try {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report 
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                     return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory() + path + FileNameURL(url));

                byte data[] = new byte[fileLength];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                	
                    // allow canceling with back button
                    if (isCancelled())
                        return null;
                    total += count;
                    
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } 
                catch (IOException ignored) { }

                if (connection != null)
                    connection.disconnect();
            }
            
        } finally {
        	
        }
        return null;
    }
		
	@Override
	protected void onPostExecute(String result) {	
		
        if (result != null){
        	if(failedDownloadImage != null) failedDownloadImage.onDownloadIFailed();
        }else{ 
        	if (listenerDownloadImage != null) listenerDownloadImage.onDownloadICompleted();	            
        }
	}

}