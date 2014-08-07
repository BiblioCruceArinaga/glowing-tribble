package com.rising.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import net.sf.andpdf.nio.ByteBuffer;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.mainscreen.MainScreen_Errors;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class FileExplore extends Activity {

	private Context ctx = this;
	private ListAdapter adapter;
	private ArrayList<String> str = new ArrayList<String>();
	private Boolean firstLvl = true;
	private static final String TAG = "F_PATH";
	private Item[] fileList;
	private File path = new File(Environment.getExternalStorageDirectory() + "");
	private String chosenFile;
	private ListView LV_FileExplorer;
	private ActionBar ABar;
	
	//Folders
	private String scores_path = "/.RisingScores/scores/";
	private String img_path = "/.RisingScores/scores_images/";
		
	//Clases usadas
	private MainScreen_Errors ERRORS;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.mainscreen_pdf_fileexplorer);
		
		this.ERRORS = new MainScreen_Errors(ctx);
		
		LV_FileExplorer = (ListView) findViewById(R.id.lv_fileexplorer);
				
		ABar = getActionBar();
		ABar.setDisplayHomeAsUpEnabled(true);
		
		loadFileList();
				
		if (fileList == null) {
			new MainScreen_Errors(this).ErrPDF(0);
		}
				
		LV_FileExplorer.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				
				chosenFile = fileList[position].file;
				File sel = new File(path + "/" + chosenFile);
				
				if (sel.isDirectory()) {
					firstLvl = false;

					// Adds chosen directory to list
					str.add(chosenFile);
					fileList = null;
					path = new File(sel + "");

					loadFileList();
				} else {
					CopiarArchivos(path.getAbsolutePath().toString()+"/"+chosenFile);
				}
			}
			
		});
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		if(firstLvl){
	    			Intent i = new Intent(ctx, MainScreenActivity.class);
	    			startActivity(i);
	    			finish();
	    		}else{
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

					Log.d(TAG, path.getAbsolutePath());
	    		}
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
	    }
		
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
				if(!firstLvl){
					ABar.setTitle("Up");
				}else{
					ABar.setTitle(R.string.app_name);
				}
			}
		} else {
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
		
		LV_FileExplorer.setAdapter(adapter);
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

	public void CopiarArchivos(String path) {
                        	
		File origen = new File(path);
        File destino = new File(Environment.getExternalStorageDirectory() + scores_path, chosenFile);
        
        if(ComprobarFichero(origen)){
        	       	
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
 	            ERRORS.ErrPDF(0);
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