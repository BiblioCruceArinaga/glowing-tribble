package com.rising.pdf;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.rising.drawing.R;
import com.rising.mainscreen.MainScreen_Utils;

//Clase con los métodos necesarios para hacer uso de archivos PDF
public class PDF_Methods {

	//Variables
	private Context ctx;
	private Dialog MDialog;
	private String[] ficheros;
	
	//Clases usadas
	private MainScreen_Utils MSUTILS;
	
	public PDF_Methods(Context context, String[] ficheros_datos){
		this.ctx = context;
		this.MSUTILS = new MainScreen_Utils();
		this.ficheros = ficheros_datos;
	}
		
	public void subirArchivo(){
		MDialog = new Dialog(ctx, R.style.cust_dialog);
		MDialog.setContentView(R.layout.mainscreen_pdf_upload);
		MDialog.setTitle(R.string.upload_pdf);
		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		MDialog.show();
		
		Button B_Upload = (Button) MDialog.findViewById(R.id.upload_pdf_button);
				
		B_Upload.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(ctx, FileExplore.class);
				ctx.startActivity(i);
				((Activity)ctx).finish();
			}
		});	
	}
	
	public void AbrirPDFExterno(int position){
		
		//Abrir con lector pdf del sistema
		File file = new File(Environment.getExternalStorageDirectory() + MSUTILS.path + ficheros[position]);
		Intent target = new Intent(Intent.ACTION_VIEW);
		target.setDataAndType(Uri.fromFile(file), "application/pdf");
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		Intent intent = Intent.createChooser(target, ctx.getString(R.string.open_file));
		ctx.startActivity(intent);
	}
	
	//No se usa actualmente, pero no lo descarto para el futuro
	public void AbrirPDFInterno(int position){
		
		//Abrir con lector pdf de la aplicación
		Intent intent_scores = new Intent(ctx, PDFReaderActivity.class);
	    intent_scores.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, Environment.getExternalStorageDirectory() + MSUTILS.path + ficheros[position]);	    
		
	    ctx.startActivity(intent_scores);
	} 
	
}