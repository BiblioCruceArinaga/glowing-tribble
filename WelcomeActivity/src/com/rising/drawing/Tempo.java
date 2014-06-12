package com.rising.drawing;

public class Tempo {

	private boolean dibujar;
	
	private int numerador;
	private int denominador;
	private int x;
	private int y_numerador;
	private int y_denominador;
	
	public Tempo() {
		dibujar = false;
		
		numerador = 0;
		denominador = 0;
		x = -1;
		y_numerador = -1;
		y_denominador = -1;
	}
	
	public boolean dibujar() {
		return dibujar;
	}
	
	public int getDenominador() {
		return denominador;
	}
	
	public String getDenominadorString() {
		return denominador + "";
	}
	
	public int getNumerador() {
		return numerador;
	}
	
	public String getNumeradorString() {
		return numerador + "";
	}
	
	public int getX() {
		return x;
	}
	
	public int getYDenominador() {
		return y_denominador;
	}
	
	public int getYNumerador() {
		return y_numerador;
	}
	
	public int numeroDePulsos() {
		switch (denominador) {
			case 4:
				return numerador;
			case 8:
				if (numerador == 6) return 2;
				else return 3;
			default:
				return 0;
		}
	}
	
	public void setDenominador(int denominador) {
		this.denominador = denominador;
	}
	
	public void setDibujar(boolean dibujar) {
		this.dibujar = dibujar;
	}
	
	public void setNumerador(int numerador) {
		this.numerador = numerador;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setYDenominador(int y_denominador) {
		this.y_denominador = y_denominador;
	}
	
	public void setYNumerador(int y_numerador) {
		this.y_numerador = y_numerador;
	}
}
