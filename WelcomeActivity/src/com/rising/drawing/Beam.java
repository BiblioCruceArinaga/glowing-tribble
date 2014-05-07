package com.rising.drawing;

/*
 * Esta clase almacena una posición de una nota
 * que contiene beams, para posteriormente, tras
 * los cálculos, dibujarlos
 */

public class Beam {

	private int compas;
	private int nota;
	private int y;
	
	public Beam(int compas, int nota) {
		this.compas = compas;
		this.nota = nota;
		this.y = -1;
	}
	
	public int getCompas() {
		return compas;
	}
	
	public int getNota() {
		return nota;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
