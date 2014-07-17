package com.rising.drawing;

public class Wedge {

	//  True = crescendo, false = diminuendo
	private boolean crescendo = true;
	
	private int xIni = 0;
	private int yIni = 0;
	private int xFin = 0;
	
	public Wedge(byte value, int position) {
		switch (value) {
			case 33:
				crescendo = true;
				xIni = position;
				break;
			case 34:
				crescendo = true;
				xFin = position;
				break;
			case 35:
				crescendo = false;
				xIni = position;
				break;
			case 36:
				crescendo = false;
				xFin = position;
				break;
			default:
				break;
		}
	}
	
	public boolean crescendo() {
		return crescendo;
	}
	
	public int getXIni() {
		return xIni;
	}
	
	public int getYIni() {
		return yIni;
	}
	
	public int getXFin() {
		return xFin;
	}
	
	public void setXIni(int xIni) {
		this.xIni = xIni;
	}
	
	public void setXFin(int xFin) {
		this.xFin = xFin;
	}
	
	public void setYIni(int yIni) {
		this.yIni = yIni;
	}
}