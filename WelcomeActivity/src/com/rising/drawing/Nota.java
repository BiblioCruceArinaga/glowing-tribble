package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Nota {

	private byte step;
	private byte octava;
	private byte figuracion;
	private byte union;
	private byte plica;
	private byte voz;
	private byte pentagrama;
	
	ArrayList<Byte> figurasGraficas;
	ArrayList<Byte> posicionEjeX;
	
	public Nota(byte step, byte octava, byte figuracion, byte union,
			byte plica, byte voz, byte pentagrama,
			ArrayList<Byte> figurasGraficas, ArrayList<Byte> posicionEjeX) {
		
		this.step = step;
		this.octava = octava;
		this.figuracion = figuracion;
		this.union = union;
		this.plica = plica;
		this.voz = voz;
		this.pentagrama = pentagrama;
		
		this.figurasGraficas = figurasGraficas;
		this.posicionEjeX = posicionEjeX;
	}
	
	public byte getFiguracion() {
		return figuracion;
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
	
	public int getPosicion() {
		byte[] bytesArray = new byte[posicionEjeX.size()];
        int len = bytesArray.length;
        for (int i=0; i<len; i++)
            bytesArray[i] = posicionEjeX.get(i);
        
        try {
            String bytesString = new String(bytesArray, "UTF-8");
            return Integer.parseInt(bytesString);
        }
        catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            return -1;
        }
	}
	
	public boolean notaDeGracia() {
		return figurasGraficas.contains((byte) 19) || figurasGraficas.contains((byte) 20);
	}
}
