package com.rising.drawing.figurasgraficas;

public class Texto 
{
	private String texto;
	private int x;
	private int y;
	
	public Texto() 
	{
		texto = "";
		x = -1;
		y = -1;
	}
	
	public String getTexto() 
	{
		return texto;
	}
	
	public int getX() 
	{
		return x;
	}
	
	public int getY() 
	{
		return y;
	}
	
	public void setTexto(final String texto) 
	{
		this.texto = texto;
	}
	
	public void setX(final int x) 
	{
		this.x = x;
	}
	
	public void setY(final int y) 
	{
		this.y = y;
	}
}