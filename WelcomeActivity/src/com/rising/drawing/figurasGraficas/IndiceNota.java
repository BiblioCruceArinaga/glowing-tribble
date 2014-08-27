package com.rising.drawing.figurasGraficas;

/*
 * Esta clase almacena una posición de una nota
 * para, más tarde, dibujar las figuras gráficas
 * o beams asociados a ella.
 */

public class IndiceNota implements Comparable<IndiceNota> {
	private int compas;
	private int nota;
	
	//  Este campo es de uso exclusivo cuando
	//  estemos manipulando ligaduras
	private byte ligadura;
	
	//  Este campo es de uso exclusivo
	//  cuando estemos manipulando beams
	private byte beamId;
	
	public IndiceNota(int compas, int nota, byte ligadura, byte beamId) {
		this.compas = compas;
		this.nota = nota;
		this.ligadura = ligadura;
		this.beamId = beamId;
	}
	
	public byte getBeamId() {
		return beamId;
	}
	
	public int getCompas() {
		return compas;
	}
	
	public byte getLigadura() {
		return ligadura;
	}
	
	public int getNota() {
		return nota;
	}

	@Override
	public int compareTo(IndiceNota another) {
		if (beamId < another.getBeamId())
			return -1;
		else if (beamId == another.getBeamId())
			return 0;
		else
			return 1;
	}
}
