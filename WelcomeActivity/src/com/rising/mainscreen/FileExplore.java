package com.rising.mainscreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.qoppa.android.pdf.source.FilePDFSource;
import com.qoppa.android.pdfProcess.PDFDocument;
import com.qoppa.android.pdfProcess.PDFPage;
import com.qoppa.android.pdfViewer.fonts.StandardFontTF;
import com.rising.drawing.R;

public class FileExplore extends Activity {

	// Stores names of traversed directories
	ArrayList<String> str = new ArrayList<String>();

	// Check if the first level of the directory structure is the one showing
	private Boolean firstLvl = true;

	private static final String TAG = "F_PATH";

	private Item[] fileList;
	private File path = new File(Environment.getExternalStorageDirectory() + "");
	private String chosenFile;
	private static final int DIALOG_LOAD_FILE = 1000;
	private String scores_path = "/.RisingScores/scores/";
	private String img_path = "/.RisingScores/scores_images/";

	ListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		loadFileList();
		
		showDialog(DIALOG_LOAD_FILE);
		Log.d(TAG, path.getAbsolutePath());

	}

	private void loadFileList() {
		try {
			path.mkdirs();
		} catch (SecurityException e) {
			Log.e(TAG, "unable to write on the sd card ");
		}

		// Checks whether path exists
		if (path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					// Filters based on whether the file is hidden or not
					return (sel.isFile() || sel.isDirectory())
							&& !sel.isHidden();

				}
			};

			String[] fList = path.list(filter);
			fileList = new Item[fList.length];
			for (int i = 0; i < fList.length; i++) {
				fileList[i] = new Item(fList[i], R.drawable.file_icon);

				// Convert into file path
				File sel = new File(path, fList[i]);

				// Set drawables
				if (sel.isDirectory()) {
					fileList[i].icon = R.drawable.directory_icon;
					//Log.d("DIRECTORY", fileList[i].file);
				} else {
					//Log.d("FILE", fileList[i].file);
				}
			}

			if (!firstLvl) {
				Item temp[] = new Item[fileList.length + 1];
				for (int i = 0; i < fileList.length; i++) {
					temp[i + 1] = fileList[i];
				}
				temp[0] = new Item("Up", R.drawable.directory_up);
				fileList = temp;
			}
		} else {
			Log.e(TAG, "path does not exist");
		}

		adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1,
				fileList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// creates view
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				// put the image on the text view
				textView.setCompoundDrawablesWithIntrinsicBounds(
						fileList[position].icon, 0, 0, 0);

				// add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				textView.setCompoundDrawablePadding(dp5);

				return view;
			}
		};

	}

	private class Item {
		public String file;
		public int icon;

		public Item(String file, Integer icon) {
			this.file = file;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return file;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		if (fileList == null) {
			Log.e(TAG, "No files loaded");
			dialog = builder.create();
			return dialog;
		}

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle(getString(R.string.choose_directory));
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chosenFile = fileList[which].file;
					File sel = new File(path + "/" + chosenFile);
					if (sel.isDirectory()) {
						firstLvl = false;

						// Adds chosen directory to list
						str.add(chosenFile);
						fileList = null;
						path = new File(sel + "");

						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}

					// Checks if 'up' was clicked
					else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

						// present directory removed from list
						String s = str.remove(str.size() - 1);

						// path modified to exclude present directory
						path = new File(path.toString().substring(0,
								path.toString().lastIndexOf(s)));
						fileList = null;

						// if there are no more directories in the list, then
						// its the first level
						if (str.isEmpty()) {
							firstLvl = true;
						}
						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					} else {
						CopiarArchivos(path.getAbsolutePath().toString()+"/"+chosenFile);
						finish();
					}

				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}
	
	//Hacer esto y la extracción de las imagenes en un hilo aparte. 
	public void CopiarArchivos(String path) {
                        	
		File origen = new File(path);
        File destino = new File(Environment.getExternalStorageDirectory() + scores_path, chosenFile);

        if(ComprobarFichero(origen)){
        	
        	//Falta hacer el progress bar e indicarle al usuario el proceso. 
        	   try{
        		  FileInputStream or = new FileInputStream(origen);
                  FileOutputStream des = new FileOutputStream(destino); 
 	              FileChannel in = (or).getChannel();
 	              FileChannel out = (des).getChannel();
 	              
 	              in.transferTo(0, origen.length(), out);
 	              
 	              in.close();
 	              out.close();
 	              or.close();
 	              des.close();
 	              
 	              Log.i("Destino", destino.toString() + ", String: " + Environment.getExternalStorageDirectory() + scores_path + chosenFile);
 	              getPDFImagen();
 	               	        
 	              Intent i = new Intent(this, MainScreenActivity.class);
 	              startActivity(i);
 	              
 	        } catch(Exception e){
 	            Log.e("Error copiar", e.getMessage());
 	        }
		}else{
			Toast.makeText(this, getString(R.string.pdf_error), Toast.LENGTH_LONG).show();
		}        

	}
	
	public void getPDFImagen(){
				
		try{
				
			//this static allows the sdk to access font assets, 
            //it must be set prior to utilizing libraries
            StandardFontTF.mAssetMgr = getAssets();
          
            // Load a document and get the first page
            PDFDocument pdf = new PDFDocument(new FilePDFSource(Environment.getExternalStorageDirectory() + scores_path + chosenFile), null);
            //PDFFile pdf = new PDFFile(PDFSource(Environment.getExternalStorageDirectory() + scores_path + chosenFile));
            
            PDFPage page = pdf.getPage(0);
              
            // Create a bitmap and canvas to draw the page into
            int width = (int)Math.ceil (page.getDisplayWidth());
            int height = (int)Math.ceil(page.getDisplayHeight());
            Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
              
            // Create canvas to draw into the bitmap
            Canvas c = new Canvas (bm);
              
            // Fill the bitmap with a white background
            Paint whiteBgnd = new Paint();
            whiteBgnd.setColor(Color.WHITE);
            whiteBgnd.setStyle(Paint.Style.FILL);
            c.drawRect(0, 0, width, height, whiteBgnd);
                       
            // paint the page into the canvas
            page.paintPage(c);
            
            // Save the bitmap
            OutputStream outStream = new FileOutputStream(Environment.getExternalStorageDirectory() + img_path + ChangeExtention(chosenFile, ".jpg"));
          	bm.compress(CompressFormat.JPEG, 80, outStream);
          	outStream.close();

        }
        catch(Exception e){
              Log.e("error", Log.getStackTraceString(e));
        }
	}
	
	public String ChangeExtention(String s, String extension){		
		String file = s.substring(0, s.lastIndexOf('.'));
		
		return file+extension.toLowerCase();
	}
		
	//Comprueba si es un PDF por la extensión
	public boolean ComprobarFichero(File f){
		
		Log.i("Comprueba ficheros", f.getName());
		String nombre = f.getName();
		
		int i = nombre.lastIndexOf('.');
		
		String pdf = nombre.substring(i+1, nombre.length()).toLowerCase();
		Log.i("PDF", pdf);
		
		if(pdf.equals("pdf")){
			return true;
		}else{
			return false;
		}		
	}

}