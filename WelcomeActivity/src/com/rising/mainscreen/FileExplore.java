package com.rising.mainscreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.WeakReference;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.RectF;
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

import com.rising.drawing.R;
import com.sun.pdfview.Cache;
import com.sun.pdfview.ImageInfo;
import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

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
	Context context = this;
	ListAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		loadFileList();
		
		showDialog(DIALOG_LOAD_FILE);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
        Intent i = new Intent(this, MainScreenActivity.class);
        startActivity(i);
		finish();
	}

	private void loadFileList() {
		try {
			path.mkdirs();
		} catch (SecurityException e) {
			Log.e("Error List", e.getMessage());
			Toast.makeText(this, getString(R.string.no_space), Toast.LENGTH_LONG).show();
		}

		FilesPathExist();

		FileAdapter();

	}

	private void FilesPathExist(){
		
		// Checks whether path exists
		if (path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
				
					// Filters based on whether the file is hidden or not
					return (sel.isFile() || sel.isDirectory())	&& !sel.isHidden();
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
			Toast.makeText(this, getString(R.string.path_error), Toast.LENGTH_LONG).show();
		}
	}

	private void FileAdapter(){
		adapter = new ArrayAdapter<Item>(this, android.R.layout.select_dialog_item, android.R.id.text1,	fileList) {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// creates view
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view.findViewById(android.R.id.text1);

				// put the image on the text view
				textView.setCompoundDrawablesWithIntrinsicBounds(fileList[position].icon, 0, 0, 0);

				// add margin between image and text (support various screen densities)
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
		final AlertDialog.Builder builder = new Builder(this);

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
							path = new File(path.toString().substring(0, path.toString().lastIndexOf(s)));
							fileList = null;
	
							// if there are no more directories in the list, then its the first level
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
 	               	
 	              getRenderImagen();
 	              
 	              Intent i = new Intent(this, MainScreenActivity.class);
 	              startActivity(i);
 	              finish();
 	              
 	        } catch(Exception e){
 	            Log.e("Error copiar", e.getMessage());
 	        }
		}else{
			Toast.makeText(this, getString(R.string.pdf_error), Toast.LENGTH_LONG).show();
		}        

	}
	
	public void getRenderImagen(){
		byte[] bytes;
		
	    try {

	        File file = new File(Environment.getExternalStorageDirectory() + scores_path + chosenFile);
	        FileInputStream is = new FileInputStream(file);

	        // Get the size of the file
	        long length = file.length();
	        bytes = new byte[(int) length];
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        
	        is.close();

	        ByteBuffer buffer = ByteBuffer.NEW(bytes);
	        PDFFile pdf_file = new PDFFile(buffer);
	        PDFPage page = pdf_file.getPage(0, true);
	        
	        RectF rect = new RectF(0, 0, (int)page.getWidth(), (int)page.getHeight());
	        
	        Bitmap image = page.getImage((int)page.getWidth(), (int)page.getHeight(), rect);
	        FileOutputStream os = new FileOutputStream(Environment.getExternalStorageDirectory() + img_path + ChangeExtention(chosenFile, ".jpg"));
	        image.compress(Bitmap.CompressFormat.JPEG, 80, os);

	        os.close();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.e("Error", "Error con el PDF");
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