package com.rising.drawing.figurasgraficas;

/*
 * Esta clase almacena una posición de una nota
 * para, más tarde, dibujar las figuras gráficas
 * o beams asociados a ella.
 */

public class IndiceNota implements Comparable<IndiceNota> 
{
	public int compas;
	public int nota;
	
	//  Este campo es de uso exclusivo cuando
	//  estemos manipulando ligaduras
	public byte ligadura = -1;
	
	//  Este campo es de uso exclusivo
	//  cuando estemos manipulando beams
	public byte beamId = -1;
	
	@Override
	public int compareTo(final IndiceNota another) 
	{
		if (beamId < another.beamId) {
			return -1;
		} else if (beamId == another.beamId) {
			return 0;
		} else {
			return 1;
		}
	}
}
