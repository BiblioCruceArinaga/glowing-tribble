package com.rising.drawing;

/*
 * Esta clase almacena una posición de una nota
 * para, más tarde, dibujar las figuras gráficas
 * o beams asociados a ella.
 */

public class IndiceNota {
	private int compas;
	private int nota;
	
	//  Este campo es de uso exclusivo cuando
	//  estemos manipulando ligaduras
	private byte ligadura;
	
	public IndiceNota(int compas, int nota, byte ligadura) {
		this.compas = compas;
		this.nota = nota;
		this.ligadura = ligadura;
	}
	
	public int getCompas() {
		return compas;
	}
	
	public int getNota() {
		return nota;
	}
	
	public byte getLigadura() {
		return ligadura;
	}
}
