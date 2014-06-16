package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Nota {

	private byte step;
	private byte octava;
	private byte figuracion;
	private byte beam;
	private byte plica;
	private byte voz;
	private byte pentagrama;
	
	//  La posición x del octavarium depende de la de
	//  la nota, y por tanto no necesitamos guardarla
	private int octavarium;
	private int y_octavarium;

	private byte ligaduraUnion;
	private byte ligaduraExpresion;
	private boolean ligaduraExpresionEncima;
	
	ArrayList<Byte> figurasGraficas;
	ArrayList<Byte> posicion;
	
	private int x;
	private int y;
	
	public Nota(byte step, byte octava, byte figuracion, byte beam,
			byte plica, byte voz, byte pentagrama,
			ArrayList<Byte> figurasGraficas, ArrayList<Byte> posicion) {
		
		this.step = step;
		this.octava = octava;
		this.figuracion = figuracion;
		this.beam = beam;
		this.plica = plica;
		this.voz = voz;
		this.pentagrama = pentagrama;
		
		octavarium = 0;
		y_octavarium = -1;
		
		ligaduraUnion = 0;
		ligaduraExpresion = 0;
		ligaduraExpresionEncima = false;
		
		this.figurasGraficas = figurasGraficas;
		this.posicion = posicion;
		
		x = 0;
		y = 0;
	}
	
	public boolean acorde() {
		return figurasGraficas.contains((byte) 2);
	}
	
	public boolean beamFinal() {
		return beam == 1 || beam == 4;
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
	
	public byte getBeam() {
		return beam;
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
	
	public int getOctavarium() {
		return octavarium;
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
	
	public int getYOctavarium() {
		return y_octavarium;
	}
	
	public boolean haciaArriba() {
		return plica == 1;
	}
	
	public boolean ligaduraExpresionEncima() {
		return ligaduraExpresionEncima;
	}
	
	public boolean notaDeGracia() {
		return figurasGraficas.contains((byte) 18) || figurasGraficas.contains((byte) 19);
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
	
	public void setOctavarium(int octavarium) {
		this.octavarium = octavarium;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setYOctavarium(int y_octavarium) {
		this.y_octavarium = y_octavarium;
	}
	
	public boolean silencio() {
		return step == 0;
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
}
