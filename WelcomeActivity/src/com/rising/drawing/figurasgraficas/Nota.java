package com.rising.drawing.figurasgraficas;

import java.util.ArrayList;

import com.rising.drawing.StaticMethods;

public class Nota implements Comparable<Nota> 
{
	private transient final byte step;
	private transient final byte octava;
	private transient final byte figuracion;
	private transient final byte pulsos;
	private transient final byte beam;
	private transient final byte beamId;
	private transient final byte plica;
	private transient final byte voz;
	private transient final byte pentagrama;

	private byte ligaduraUnion;
	private byte ligaduraExpresion;
	private transient boolean estaligaduraUnionEncima;
	private transient boolean estaligaduraExpresionEncima;
	private float anguloRotacionLigaduraExpresion;
	
	private transient ArrayList<Byte> figurasGraficas;
	private transient ArrayList<Byte> posicion;
	
	private int x;
	private int y;
	
	public Nota(final byte step, final byte octava, final byte figuracion, 
			final byte pulsos, final byte beam,	final byte beamId,
			final byte plica, final byte voz, final byte pentagrama,
			final ArrayList<Byte> figurasGraficas, final ArrayList<Byte> posicion) 
	{	
		this.step = step;
		this.octava = octava;
		this.figuracion = figuracion;
		this.pulsos = pulsos;
		this.beam = beam;
		this.beamId = beamId;
		this.plica = plica;
		this.voz = voz;
		this.pentagrama = pentagrama;
		
		this.figurasGraficas = figurasGraficas;
		this.posicion = posicion;
		
		ligaduraUnion = 0;
		ligaduraExpresion = 0;
		estaligaduraUnionEncima = false;
		estaligaduraExpresionEncima = false;
		anguloRotacionLigaduraExpresion = 0;

		x = 0;
		y = 0;
	}
	
	public boolean acorde() 
	{
		return figurasGraficas.contains((byte) 2);
	}
	
	public boolean beamFinal() 
	{
		return beam == 1 || beam == 4 || beam == 6;
	}
	
	public boolean desplazadaALaIzquierda() 
	{
		return figurasGraficas.contains((byte) 24);
	}
	
	public boolean desplazadaALaDerecha() 
	{
		return figurasGraficas.contains((byte) 25);
	}
	
	public boolean esAlteracion(final int indFigura) 
	{
		return figurasGraficas.get(indFigura) == 12 ||
			   figurasGraficas.get(indFigura) == 13 ||
			   figurasGraficas.get(indFigura) == 14;
	}
	
	public boolean esLigadura(final int indFigura) 
	{
		return esLigaduraUnion(indFigura) || esLigaduraExpresion(indFigura);
	}
	
	public boolean esLigaduraExpresion(final int indFigura) 
	{
		return figurasGraficas.get(indFigura) == 32 || 
			   figurasGraficas.get(indFigura) == 33;
	}
	
	public boolean esLigaduraUnion(final int indFigura) 
	{
		return figurasGraficas.get(indFigura) == 10 || 
			   figurasGraficas.get(indFigura) == 11;
	}

	public boolean finDeTresillo() 
	{
		return figurasGraficas.contains((byte) 4);
	}
	
	public boolean finDeTresillo(final int indFigura) 
	{
		return figurasGraficas.get(indFigura) == 4;
	}
	
	public float getAnguloRotacionLigaduraExpresion() 
	{
		return anguloRotacionLigaduraExpresion;
	}
	
	public byte getBeam() 
	{
		return beam;
	}
	
	public byte getBeamId() 
	{
		return beamId;
	}
	
	public byte getFiguracion() 
	{
		return figuracion;
	}
	
	public ArrayList<Byte> getFigurasGraficas() 
	{
		return figurasGraficas;
	}
	
	public byte getLigaduraExpresion() 
	{
		return ligaduraExpresion;
	}
	
	public byte getLigaduraUnion() 
	{
		return ligaduraUnion;
	}
	
	public byte getOctava() 
	{
		return octava;
	}

	public byte getPentagrama() 
	{
		return pentagrama;
	}
	
	public byte getPlica() 
	{
		return plica;
	}
	
	public int getPosition() 
	{
        String position = StaticMethods.convertByteArrayListToString(posicion, 0);
        
        return position.equals("") ? -1 : Integer.parseInt(position);
	}
	
	public ArrayList<Byte> getPosicionArray() 
	{
		return posicion;
	}
	
	public byte getPulsos() 
	{
		return pulsos;
	}
	
	public byte getStep() 
	{
		return step;
	}

	public byte getVoz() 
	{
		return voz;
	}

	public int getX() 
	{
		return x;
	}
	
	public int getY() 
	{
		return y;
	}
	
	public boolean haciaArriba() 
	{
		return plica == 1;
	}
	
	public boolean ligaduraExpresionEncima() 
	{
		return estaligaduraExpresionEncima;
	}
	
	public boolean ligaduraUnionEncima() 
	{
		return estaligaduraUnionEncima;
	}
	
	public boolean notaDeGracia() 
	{
		return figurasGraficas.contains((byte) 18) || 
			   figurasGraficas.contains((byte) 19);
	}
	
	public boolean octavada() 
	{
		return octava > 10;
	}
	
	public void setLigaduraUnion(final byte ligaduraUnion) 
	{
		this.ligaduraUnion = ligaduraUnion;
	}
	
	public void setLigaduraExpresion(final byte ligaduraExpresion) 
	{
		this.ligaduraExpresion = ligaduraExpresion;
	}
	
	public void setLigaduraExpresionOrientacion(final boolean orientacion) 
	{
		estaligaduraExpresionEncima = orientacion;
	}
	
	public void setLigaduraUnionOrientacion(final boolean orientacion)  
	{
		estaligaduraUnionEncima = orientacion;
	}
	
	public void setAnguloRotacionLigaduraExpresion(final byte angulo) 
	{
		this.anguloRotacionLigaduraExpresion = (float) angulo;
	}
	
	public void setX(final int x) 
	{
		this.x = x;
	}
	
	public void setY(final int y) 
	{
		this.y = y;
	}
	
	public boolean silencio() 
	{
		return step == 0;
	}

	public boolean tieneBeams() 
	{
		return beam > 0;
	}
	
	public boolean tienePlica() 
	{
		return plica > 0;
	}
	
	public boolean tienePuntillo() 
	{
		return figurasGraficas.contains((byte) 15) ||
			   figurasGraficas.contains((byte) 16) ||
			   figurasGraficas.contains((byte) 17);
	}

	public boolean tieneSlash() 
	{
		return figurasGraficas.contains((byte) 18);
	}

	@Override
	public int compareTo(final Nota arg0) 
	{
		if (x < arg0.getX()) {
			return -1;
		} else if (x == arg0.getX()) {
			return 0;
		} else {
			return 1;
		}
	}
}