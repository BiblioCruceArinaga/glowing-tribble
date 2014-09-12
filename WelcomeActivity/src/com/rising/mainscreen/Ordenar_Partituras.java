package com.rising.mainscreen;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.rising.drawing.R;

public class Ordenar_Partituras {

	private Context ctx;
	private ScoresAdapter s_adapter;
		
	public Ordenar_Partituras(Context context, ScoresAdapter SAdapter){
		this.s_adapter = SAdapter;
		this.ctx = context;
		if(s_adapter == null){
			Log.e("S_adapter", "¡¡ES NULL!!");
		}
	}
	
	public void listarAutores() {
		if (s_adapter != null) {
			int size = s_adapter.getCount();
			final CharSequence[] items = new CharSequence[size];
			for (int i=0; i<size; i++) items[i] = s_adapter.getItemAuthor(i);
	
		    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		    builder.setTitle(R.string.author_dialog_title);
		    builder.setItems(items, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int item) {
		            s_adapter.filter(s_adapter.getItemAuthor(item));
		        }
		    }).show();
		}
	}
	
	public void listarInstrumentos() {
		if (s_adapter != null) {
			int size = s_adapter.getCount();
			
			//  Evitar repeticiones
			LinkedHashSet<String> hs = new LinkedHashSet<String>();
			for (int i=0; i<size; i++) {
				hs.add(s_adapter.getItemInstrument(i));
			}
			
			size = hs.size();
			final CharSequence[] items = new CharSequence[size];
			ArrayList<String> al = new ArrayList<String>();
			al.addAll(hs);
			for (int i=0; i<size; i++) items[i] = al.get(i);
	
		    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		    builder.setTitle(R.string.instrument_dialog_title);
		    builder.setItems(items, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int item) {
		            s_adapter.filter(s_adapter.getItemInstrument(item));
		        }
		    }).show();
		}
	}
	
	public void ordenarPorNombre() {
		if (s_adapter != null) s_adapter.sortByName();
	}
	
	public void mostrarTodas() {
		if (s_adapter != null) s_adapter.showAll();
	}

	public void listarFormatos(){
		if (s_adapter != null) {
			int size = s_adapter.getCount();
			
			//  Evitar repeticiones
			LinkedHashSet<String> hs = new LinkedHashSet<String>();
			for (int i=0; i<size; i++) {
				hs.add(s_adapter.getItemFormat(i));
			}
			
			size = hs.size();
			final CharSequence[] items = new CharSequence[size];
			ArrayList<String> al = new ArrayList<String>();
			al.addAll(hs);
			for (int i=0; i<size; i++) items[i] = al.get(i);
	
		    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		    builder.setTitle(R.string.format_dialog_title);
		    builder.setItems(items, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int item) {
		            s_adapter.filter(items[item].toString());
		        }
		    }).show();
		}
	}
	
}