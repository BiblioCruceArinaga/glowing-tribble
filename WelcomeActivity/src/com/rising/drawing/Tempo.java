package com.rising.drawing;

public class Tempo {

	private int numerador;
	private int denominador;
	private int x;
	private int y_numerador;
	private int y_denominador;
	
	public Tempo() {
		numerador = 0;
		denominador = 0;
		x = -1;
		y_numerador = -1;
		y_denominador = -1;
	}
	
	public String getDenominador() {
		return denominador + "";
	}
	
	public String getNumerador() {
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
	
	public void setDenominador(int denominador) {
		this.denominador = denominador;
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
