package com.rising.drawing;

/*
 * Esta clase almacena una posición de una nota
 * que contiene beams, para posteriormente, tras
 * los cálculos, dibujarlos
 */

public class Beam {

	private int compas;
	private int nota;
	
	public Beam(int compas, int nota) {
		this.compas = compas;
		this.nota = nota;
	}
	
	public int getCompas() {
		return compas;
	}
	
	public int getNota() {
		return nota;
	}
}
