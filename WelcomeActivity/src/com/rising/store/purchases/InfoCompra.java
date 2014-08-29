package com.rising.store.purchases;

import android.util.Log;


public class InfoCompra {
		
	private int Id_Score;
	private static boolean comprado;

	public InfoCompra(int Id_S){
		this.Id_Score = Id_S;
	}
	
	public int getId_S() {
		return Id_Score;
	}
	public void setId_S(int id_S) {
		Id_Score = id_S;
	}

	public boolean isComprado() {
		Log.d("Entró", "En isComprado: " + comprado);
		return comprado;
	}

	public void setComprado(boolean comprado) {
		Log.d("Entró", "En setComprado: " + comprado);
		InfoCompra.comprado = comprado;
	}
	
}