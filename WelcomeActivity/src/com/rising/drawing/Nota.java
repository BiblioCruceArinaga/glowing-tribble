package com.rising.drawing;

import java.util.ArrayList;

public class Nota {

	private byte nota;
	private byte octava;
	private byte figuracion;
	private byte union;
	private byte plica;
	private byte voz;
	private byte pentagrama;
	
	ArrayList<Byte> figurasGraficas;
	ArrayList<Byte> posicionEjeX;
	
	public Nota(byte nota, byte octava, byte figuracion, byte union,
			byte plica, byte voz, byte pentagrama,
			ArrayList<Byte> figurasGraficas, ArrayList<Byte> posicionEjeX) {
		
		this.nota = nota;
		this.octava = octava;
		this.figuracion = figuracion;
		this.union = union;
		this.plica = plica;
		this.voz = voz;
		this.pentagrama = pentagrama;
		
		this.figurasGraficas = figurasGraficas;
		this.posicionEjeX = posicionEjeX;
	}
}
