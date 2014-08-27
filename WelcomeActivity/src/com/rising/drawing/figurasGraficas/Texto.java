package com.rising.drawing.figurasGraficas;

public class Texto {

	private String texto;
	private int x;
	private int y;
	
	public Texto() {
		texto = "";
		x = -1;
		y = -1;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
