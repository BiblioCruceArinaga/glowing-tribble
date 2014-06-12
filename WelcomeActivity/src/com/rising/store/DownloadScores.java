package com.rising.store;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.security.DownloadScoresEncrypter;
import com.rising.store.DownloadImages.OnDownloadICompleted;
import com.rising.store.DownloadImages.OnDownloadIFailed;

public class DownloadScores extends AsyncTask<String, Integer, String>{
	
	// declare the dialog as a member field of your activity
	ProgressDialog mProgressDialog;
	
	//  Comunicaci�n HTTP con el servidor
	HttpPost httppost;
	HttpClient httpcliente;
	private String path = "/.RisingScores/scores/";
	String URL_connect = "http://www.scores.rising.es/store-buyscore";
	ImageLoader iml;
	DownloadImages downloadimage;
	String urlI;
	Configuration conf;
		
	//  Contexto
	Context context;
	
	//  Informaci�n obtenida de la base de datos
	String res;
	
	private OnDownloadICompleted successdownloadimages = new OnDownloadICompleted(){

		@Override
		public void onDownloadICompleted() {
			Log.i("Éxito", "Descarga correcta de la imagen");
		}		
	};
	
	private OnDownloadIFailed faildownloadimages = new OnDownloadIFailed(){

		@Override
		public void onDownloadIFailed() {
			downloadimage.resourceToBitmap(urlI);
			Log.i("Fracaso", "Descarga incorrecta de la imagen");
		}		
	};
	
	public interface OnDownloadCompleted{
        void onDownloadCompleted();
    }
	
	public interface OnDownloadFailed{
		void onDownloadFailed();
	}
	
	private OnDownloadFailed failedDownload;
	
	private OnDownloadCompleted listenerDownload;
	
	public DownloadScores(OnDownloadCompleted listener, OnDownloadFailed failed, Context ctx) {
		this.context = ctx;
		this.listenerDownload = listener;
		this.failedDownload = failed;
		downloadimage = new DownloadImages(successdownloadimages, faildownloadimages, context);
		mProgressDialog = new ProgressDialog(ctx);
		mProgressDialog.setMessage(ctx.getString(R.string.downloading));
 		mProgressDialog.setIndeterminate(true);
 		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
 		mProgressDialog.setCancelable(true);
 		conf = new Configuration(ctx);
	}
	
	public String FileNameURL(URL urlComplete){
		
		String urlCompleto = urlComplete.toString();
		int position = urlCompleto.lastIndexOf('/');
		
		String name = urlCompleto.substring(position, urlCompleto.length());
		
		return name;
	}	
			
	@Override
    protected String doInBackground(String... sUrl) {
    	
        // take CPU lock to prevent CPU from going off if the user 
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wl.acquire();
        
        new DownloadScoresEncrypter(context, sUrl[2]).CreateAndInsert(sUrl[0]);
        
        try {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            
            try {
                URL url = new URL(sUrl[0]);
                urlI = sUrl[1];
                downloadimage.execute(urlI);
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
                output = new FileOutputStream(Environment.getExternalStorageDirectory() + path + FileNameURL(url), true);
                
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                                
                while ((count = input.read(data)) != -1) {
                                	 
                    // allow canceling with back button
                    if (isCancelled()){
                    	input.close();
                        return null;
                    }
                    total += count;
                    
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    
                    //Aquí iría el método de introducción del código de seguridad. Habría que pasarle a ese método el Id de la 
                    //partitura y el Token del usuario 
                     
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
            	e.getMessage();
            	Log.e("Error descargar", e.getMessage());	
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
            wl.release();
        }
        return null;
    }
	
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    mProgressDialog.show();
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
	    super.onProgressUpdate(progress);
	    
	    // if we get here, length is known, now set indeterminate to false
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgress(progress[0]);
	}
	
	@Override
	protected void onPostExecute(String result) {
	    mProgressDialog.dismiss();
	    Log.i("Download", "Se acaba de cerrar el dialog");
	    
	    //Podr�an sustituirse por Dialogs. 
        if (result != null){
        	if(failedDownload != null) failedDownload.onDownloadFailed();
        	Log.e("Error descarga", "Error descarga: " + result);
        }else{ 
        	Log.i("Download", "Listener Good");
        	if (listenerDownload != null) listenerDownload.onDownloadCompleted();
        }
	}

}