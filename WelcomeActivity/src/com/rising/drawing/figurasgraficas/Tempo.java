package com.rising.drawing.figurasgraficas;

public class Tempo 
{
	private int numerador;
	private int denominador;
	private int x;
	private transient int yNumerador;
	private transient int yDenominador;
	private transient boolean hayQueDibujar;
	
	public Tempo() 
	{
		numerador = 0;
		denominador = 0;
		x = -1;
		yNumerador = -1;
		yDenominador = -1;
		hayQueDibujar = false;
	}
	
	public boolean dibujar() 
	{
		return hayQueDibujar;
	}
	
	public int getDenominador() 
	{
		return denominador;
	}
	
	public String getDenominadorString() 
	{
		return Integer.toString(denominador);
	}
	
	public int getNumerador() 
	{
		return numerador;
	}
	
	public String getNumeradorString() 
	{
		return Integer.toString(numerador);
	}
	
	public int getX() {
		return x;
	}
	
	public int getYDenominador() 
	{
		return yDenominador;
	}
	
	public int getYNumerador() 
	{
		return yNumerador;
	}
	
	public int numeroDePulsos() 
	{
		switch (denominador) {
			case 4:
				return numerador;
			case 8:
				return numerador == 6 ? 2 : 3;
			default:
				return 0;
		}
	}
	
	public void setDenominador(final int denominador) 
	{
		this.denominador = denominador;
	}
	
	public void setDibujar(final boolean dibujar) 
	{
		this.hayQueDibujar = dibujar;
	}
	
	public void setNumerador(final int numerador) 
	{
		this.numerador = numerador;
	}
	
	public void setX(final int x) 
	{
		this.x = x;
	}
	
	public void setYDenominador(final int yDenominador) 
	{
		this.yDenominador = yDenominador;
	}
	
	public void setYNumerador(final int yNumerador) 
	{
		this.yNumerador = yNumerador;
	}
}