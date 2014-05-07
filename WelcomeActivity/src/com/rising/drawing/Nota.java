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
	private boolean octavarium;
	
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
		octavarium = false;
		
		this.figurasGraficas = figurasGraficas;
		this.posicion = posicion;
		
		x = 0;
		y = 0;
	}
	
	public boolean acorde() {
		return figurasGraficas.contains((byte) 2);
	}
	
	public boolean desplazadaALaIzquierda() {
		return figurasGraficas.contains((byte) 24);
	}
	
	public boolean desplazadaALaDerecha() {
		return figurasGraficas.contains((byte) 25);
	}
	
	public byte getFiguracion() {
		return figuracion;
	}
	
	public ArrayList<Byte> getFigurasGraficas() {
		return figurasGraficas;
	}
	
	public boolean getOctavarium() {
		return octavarium;
	}
	
	public byte getStep() {
		return step;
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
	
	public byte getBeam() {
		return beam;
	}
	
	public byte getVoz() {
		return voz;
	}
	
	public ArrayList<Byte> getPosicionArray() {
		return posicion;
	}

	public int getPosicion() {
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
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
		
	public boolean finDeTresillo() {
		return figurasGraficas.contains((byte) 4);
	}
	
	public boolean haciaArriba() {
		return plica == 1;
	}
	
	public boolean notaDeGracia() {
		return figurasGraficas.contains((byte) 18) || figurasGraficas.contains((byte) 19);
	}
	
	public void setOctavarium(boolean octavarium) {
		this.octavarium = octavarium;
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
	
	public boolean tienePlica() {
		return plica > 0;
	}

	public boolean tieneSlash() {
		return figurasGraficas.contains((byte) 18);
	}
}
