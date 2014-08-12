package com.rising.store.downloads;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import com.rising.drawing.R;
import com.rising.security.DownloadScoresEncrypter;
import com.rising.store.downloads.DownloadImages.OnDownloadICompleted;
import com.rising.store.downloads.DownloadImages.OnDownloadIFailed;

public class DownloadScores extends AsyncTask<String, Integer, String>{
	
	//Variables
	private Context ctx;
	private ProgressDialog mProgressDialog;
	private String urlImages;
		
	//Folder
	private String path = "/.RisingScores/scores/";

	//CLases usadas
	private DownloadImages DOWNLOADIMAGES;
	private Download_Utils UTILS;

	
	private OnDownloadICompleted SuccessDownloadImages = new OnDownloadICompleted(){

		@Override
		public void onDownloadICompleted() {
			Log.i("Éxito", "Descarga correcta de la imagen");
		}		
	};
	
	private OnDownloadIFailed FailDownloadImages = new OnDownloadIFailed(){

		@Override
		public void onDownloadIFailed() {
			DOWNLOADIMAGES.resourceToBitmap(urlImages);
			Log.i("Fracaso", "Descarga incorrecta de la imagen");
		}		
	};
	
	public interface OnDownloadCompleted{
        void onDownloadCompleted();
    }
	
	public interface OnDownloadFailed{
		void onDownloadFailed();
	}
	
	private OnDownloadFailed FailedDownload;
	
	private OnDownloadCompleted SuccessedDownload;
	
	public DownloadScores(OnDownloadCompleted success, OnDownloadFailed failed, Context context) {
		this.ctx = context;
		this.UTILS = new Download_Utils();
		this.SuccessedDownload = success;
		this.FailedDownload = failed;
		this.DOWNLOADIMAGES = new DownloadImages(SuccessDownloadImages, FailDownloadImages, ctx);
		this.mProgressDialog = new ProgressDialog(ctx);
		mProgressDialog.setMessage(ctx.getString(R.string.downloading));
 		mProgressDialog.setIndeterminate(true);
 		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
 		mProgressDialog.setCancelable(true);

	}
	
	
			
	@SuppressLint("Wakelock")
	private String DownloadStatus(String URL_Score, String URL_Image, String NameAndNumberForEncrypt){
		   	PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
	        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
	        wl.acquire();
	        
	        try {
	            InputStream input = null;
	            OutputStream output = null;
	            HttpURLConnection connection = null;
	            
	            try {
	                URL url = new URL(URL_Score);
	                urlImages = URL_Image;
	                DOWNLOADIMAGES.execute(urlImages);
	                connection = (HttpURLConnection) url.openConnection();
	                connection.connect();
	                
					// expect HTTP 200 OK, so we don't mistakenly save error report 
	                // instead of the file
	                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
	                     return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

	                int fileLength = connection.getContentLength();
	                
	                // download the file
	                input = connection.getInputStream();
	                output = new FileOutputStream(Environment.getExternalStorageDirectory() + path + UTILS.FileNameURL(url), true);
	                                                
	                byte data[] = new byte[fileLength];
	                long total = 0;
	                int count;
	                                
	                while ((count = input.read(data)) != -1) {
	                                	 
	                    total += count;
	                   
	                    if (fileLength > 0) 
	                        publishProgress((int) (total * 100 / fileLength));
	                    
	                    output.write(data, 0, count);
	                }
	            } catch (Exception e) {
	            	e.getMessage();
	            	Log.e("Error descargar partitura", e.getMessage());	
	            } finally {
	                try {
	                	
	                	//Crea el fichero e incluye en él la linea de seguridad
	                    new DownloadScoresEncrypter(ctx, NameAndNumberForEncrypt).CreateAndInsert(output);
	                    Log.w("User_Token_Download", ""+NameAndNumberForEncrypt);
	                    if (output != null)
	                        output.close();
	                    if (input != null)
	                        input.close();
	                } 
	                catch (IOException ignored) { 
	                	Log.e("IOException DownloadEncrypt", "" + ignored.getMessage());
	                }

	                if (connection != null)
	                    connection.disconnect();
	            }
	             
	        }catch(Exception e){
	        	Log.e("Exception BigTry Download", "" + e.getMessage());
	        }finally{
	        	wl.release();
	        }
		return null;
	}
	
	@Override
    protected String doInBackground(String... sUrl) {
    	return DownloadStatus(sUrl[0], sUrl[1], sUrl[2]);               
    }
	
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    mProgressDialog.show();
	}
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
	    super.onProgressUpdate(progress);
	    
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgress(progress[0]);
	}
	
	@Override
	protected void onPostExecute(String result) {
	    mProgressDialog.dismiss();
	    
        if (result != null){
        	if(FailedDownload != null) FailedDownload.onDownloadFailed();
        	Log.e("Error descarga partitura", "Error descarga: " + result);
        }else{ 
        	Log.i("Download", "Listener Good");
        	if (SuccessedDownload != null) SuccessedDownload.onDownloadCompleted();
        }
	}

}