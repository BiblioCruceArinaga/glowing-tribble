package com.example.connections;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CustomAdapter implements ListAdapter {

    ArrayList<Integer> elements;
    ArrayList<PartituraTienda> infoPartituras;
	Context ctx;
	
	public CustomAdapter(MainActivity mainActivity, ArrayList<Integer> elements, ArrayList<PartituraTienda> partituras) {
		// TODO Auto-generated constructor stub
		ctx = mainActivity;
		this.elements = elements;
		infoPartituras = partituras;
	}

	public void addView(Integer element) {
		elements.add(element);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return elements.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return elements.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemViewType(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		 View row = view;
         
         if (row == null) {
             LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
             row = inflater.inflate(R.layout.grid_element, parent, false);
             
             PartituraTienda partitura = infoPartituras.get(position);
             TextView info = (TextView) row.findViewById(R.id.nombrePartitura);
             info.setText(partitura.nombre());
             info = (TextView) row.findViewById(R.id.autorPartitura);
             info.setText(partitura.autor());
             info = (TextView) row.findViewById(R.id.instrumentoPartitura);
             info.setText(partitura.instrumento());
             Button botonCompra = (Button) row.findViewById(R.id.comprar);
             botonCompra.setText(partitura.precio() + " €");
         } 

         return row;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

}
