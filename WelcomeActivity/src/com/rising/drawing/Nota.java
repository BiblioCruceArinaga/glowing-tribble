package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Nota implements Comparable<Nota> {

	private byte step;
	private byte octava;
	private byte figuracion;
	private byte pulsos;
	private byte beam;
	private byte beamId;
	private byte plica;
	private byte voz;
	private byte pentagrama;

	private byte ligaduraUnion;
	private byte ligaduraExpresion;
	private boolean ligaduraUnionEncima;
	private boolean ligaduraExpresionEncima;
	private float anguloRotacionLigaduraExpresion;
	
	ArrayList<Byte> figurasGraficas;
	ArrayList<Byte> posicion;
	
	private int x;
	private int y;
	
	public Nota(byte step, byte octava, byte figuracion, byte pulsos, byte beam,
			byte beamId, byte plica, byte voz, byte pentagrama,
			ArrayList<Byte> figurasGraficas, ArrayList<Byte> posicion) {
		
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
		ligaduraUnionEncima = false;
		ligaduraExpresionEncima = false;
		anguloRotacionLigaduraExpresion = 0;

		x = 0;
		y = 0;
	}
	
	public boolean acorde() {
		return figurasGraficas.contains((byte) 2);
	}
	
	public boolean beamFinal() {
		return beam == 1 || beam == 4 || beam == 6;
	}
	
	public boolean desplazadaALaIzquierda() {
		return figurasGraficas.contains((byte) 24);
	}
	
	public boolean desplazadaALaDerecha() {
		return figurasGraficas.contains((byte) 25);
	}
	
	public boolean esAlteracion(int indFigura) {
		return (figurasGraficas.get(indFigura) == 12) ||
			   (figurasGraficas.get(indFigura) == 13) ||
			   (figurasGraficas.get(indFigura) == 14);
	}
	
	public boolean esLigadura(int indFigura) {
		return esLigaduraUnion(indFigura) || esLigaduraExpresion(indFigura);
	}
	
	public boolean esLigaduraExpresion(int indFigura) {
		return (figurasGraficas.get(indFigura) == 32) || 
			   (figurasGraficas.get(indFigura) == 33);
	}
	
	public boolean esLigaduraUnion(int indFigura) {
		return (figurasGraficas.get(indFigura) == 10) || 
			   (figurasGraficas.get(indFigura) == 11);
	}

	public boolean finDeTresillo() {
		return figurasGraficas.contains((byte) 4);
	}
	
	public boolean finDeTresillo(int indFigura) {
		return figurasGraficas.get(indFigura) == 4;
	}
	
	public float getAnguloRotacionLigaduraExpresion() {
		return anguloRotacionLigaduraExpresion;
	}
	
	public byte getBeam() {
		return beam;
	}
	
	public byte getBeamId() {
		return beamId;
	}
	
	public byte getFiguracion() {
		return figuracion;
	}
	
	public ArrayList<Byte> getFigurasGraficas() {
		return figurasGraficas;
	}
	
	public byte getLigaduraExpresion() {
		return ligaduraExpresion;
	}
	
	public byte getLigaduraUnion() {
		return ligaduraUnion;
	}
	
	public byte getOctava() {
		return octava;
	}

	public byte getPentagrama() {
		return pentagrama;
	}
	
	public byte getPlica() {
		return plica;
	}
	
	public int getPosition() {
		byte[] bytesArray = new byte[posicion.size()];
        int len = bytesArray.length;
        for (int i=0; i<len; i++)
            bytesArray[i] = posicion.get(i);
        
        try {
            String bytesString = new String(bytesArray, "UTF-8");
            return Integer.parseInt(bytesString);
        }
        catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            return -1;
        }
	}
	
	public ArrayList<Byte> getPosicionArray() {
		return posicion;
	}
	
	public byte getPulsos() {
		return pulsos;
	}
	
	public byte getStep() {
		return step;
	}

	public byte getVoz() {
		return voz;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean haciaArriba() {
		return plica == 1;
	}
	
	public boolean ligaduraExpresionEncima() {
		return ligaduraExpresionEncima;
	}
	
	public boolean ligaduraUnionEncima() {
		return ligaduraUnionEncima;
	}
	
	public boolean notaDeGracia() {
		return figurasGraficas.contains((byte) 18) || figurasGraficas.contains((byte) 19);
	}
	
	public boolean octavada() {
		return octava > 10;
	}
	
	public void setLigaduraUnion(byte ligaduraUnion) {
		this.ligaduraUnion = ligaduraUnion;
	}
	
	public void setLigaduraExpresion(byte ligaduraExpresion) {
		this.ligaduraExpresion = ligaduraExpresion;
	}
	
	public void setLigaduraExpresionOrientacion(boolean orientacion) {
		ligaduraExpresionEncima = orientacion;
	}
	
	public void setLigaduraUnionOrientacion(boolean orientacion)  {
		ligaduraUnionEncima = orientacion;
	}
	
	public void setAnguloRotacionLigaduraExpresion(byte angulo) {
		this.anguloRotacionLigaduraExpresion = (float) angulo;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean silencio() {
		return step == 0;
	}

	public boolean tieneBeams() {
		return beam > 0;
	}
	
	public boolean tienePlica() {
		return plica > 0;
	}
	
	public boolean tienePuntillo() {
		return figurasGraficas.contains((byte) 15) ||
			   figurasGraficas.contains((byte) 16) ||
			   figurasGraficas.contains((byte) 17);
	}

	public boolean tieneSlash() {
		return figurasGraficas.contains((byte) 18);
	}

	@Override
	public int compareTo(Nota arg0) {
		if (x < arg0.getX())
			return -1;
		else if (x == arg0.getX())
			return 0;
		else
			return 1;
	}
}