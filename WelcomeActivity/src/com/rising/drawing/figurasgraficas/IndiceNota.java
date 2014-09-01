package com.rising.drawing.figurasgraficas;

/*
 * Esta clase almacena una posición de una nota
 * para, más tarde, dibujar las figuras gráficas
 * o beams asociados a ella.
 */

public class IndiceNota implements Comparable<IndiceNota> 
{
	private transient final int compas;
	private transient final int nota;
	
	//  Este campo es de uso exclusivo cuando
	//  estemos manipulando ligaduras
	private transient final byte ligadura;
	
	//  Este campo es de uso exclusivo
	//  cuando estemos manipulando beams
	private transient final byte beamId;
	
	public IndiceNota(final int compas, final int nota, final byte ligadura, final byte beamId) 
	{
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
	public int compareTo(final IndiceNota another) 
	{
		if (beamId < another.getBeamId()) {
			return -1;
		} else if (beamId == another.getBeamId()) {
			return 0;
		} else {
			return 1;
		}
	}
}
