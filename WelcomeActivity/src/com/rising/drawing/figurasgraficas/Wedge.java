package com.rising.drawing.figurasgraficas;

public class Wedge 
{
	private transient boolean isCrescendo;
	
	private int xIni;
	private int yIni;
	private int xFin;
	
	public Wedge(final byte value, final int position) 
	{
		switch (value) 
		{
			case 33:
				isCrescendo = true;
				xIni = position;
				break;
			case 34:
				isCrescendo = true;
				xFin = position;
				break;
			case 35:
				isCrescendo = false;
				xIni = position;
				break;
			case 36:
				isCrescendo = false;
				xFin = position;
				break;
			default:
				break;
		}
	}
	
	public boolean crescendo() 
	{
		return isCrescendo;
	}
	
	public int getXIni() 
	{
		return xIni;
	}
	
	public int getYIni() 
	{
		return yIni;
	}
	
	public int getXFin() 
	{
		return xFin;
	}
	
	public void setXIni(final int xIni) 
	{
		this.xIni = xIni;
	}
	
	public void setXFin(final int xFin) 
	{
		this.xFin = xFin;
	}
	
	public void setYIni(final int yIni) 
	{
		this.yIni = yIni;
	}
}