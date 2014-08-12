package com.rising.store.downloads;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.rising.drawing.R;

public class DownloadImages extends AsyncTask<String, Integer, String>{
	
	// Variables
	private Context ctx;
	
	//Folders
	private String path = "/.RisingScores/scores_images/";
	
	//Clases usadas
	private Download_Utils UTILS;
		
	public interface OnDownloadICompleted{
        void onDownloadICompleted();
    }
	
	public interface OnDownloadIFailed{
		void onDownloadIFailed();
	}
	
	private OnDownloadIFailed FailedDownloadImage;
	
	private OnDownloadICompleted SuccessedDownloadImage;
	
	public DownloadImages(OnDownloadICompleted success, OnDownloadIFailed fail, Context context) {
		this.ctx = context;
		this.UTILS = new Download_Utils();
		this.SuccessedDownloadImage = success;
		this.FailedDownloadImage = fail;
	}
	
	private String DownloadImageStatus(String URL){
		try {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            
            try {
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report 
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                     return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

                int fileLength = connection.getContentLength();
                
                input = connection.getInputStream();
                output = new FileOutputStream(Environment.getExternalStorageDirectory() + path + UTILS.FileNameURL(url));

                byte data[] = new byte[fileLength];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
            } catch (IOException IOE) {
                //resourceToBitmap(sUrl[0]);
            } catch(Exception e){
            	return e.toString(); 
            }finally {
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
            
        }catch(Exception e){
        	Log.e("Exception BigTry DownloadImages", "" + e.getMessage());
        }
        return null;
	}
				
	@Override
    protected String doInBackground(String... sUrl) {
    	return DownloadImageStatus(sUrl[0]);        
    }
		
	@Override
	protected void onPostExecute(String result) {	
		
        if (result != null){        	
        	if(FailedDownloadImage != null) FailedDownloadImage.onDownloadIFailed();
        }else{ 
        	if(SuccessedDownloadImage != null) SuccessedDownloadImage.onDownloadICompleted();	            
        }
	}

	public void resourceToBitmap(String url){
		Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.cover);
		
		URL urll = null;
		try {
			urll = new URL(url);
		} catch (MalformedURLException e1) {	
			e1.printStackTrace();
			Log.e("Falló", "Falló conversión de Resource a Bitmap " + e1.getMessage());
		}
		
		FileOutputStream out = null;
		try {
		       out = new FileOutputStream(Environment.getExternalStorageDirectory() + path + UTILS.FileNameURL(urll));
		       bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		       try{
		           out.close();
		       } catch(Throwable ignore) {}
		}
	}
	
}