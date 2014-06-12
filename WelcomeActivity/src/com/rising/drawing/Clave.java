package com.rising.drawing;

import android.graphics.Bitmap;

public class Clave {
	
	private Bitmap imagenClave;
	private int x;
	private int y;
	private byte pentagrama;
	
	public Clave() {
		imagenClave = null;
		x = -1;
		y = -1;
		pentagrama = -1;
	}
	
	public Bitmap getImagenClave() {
		return imagenClave;
	}
	
	public byte getPentagrama() {
		return pentagrama;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setImagenClave(Bitmap imagenClave) {
		this.imagenClave = imagenClave;
	}
	
	public void setPentagrama(byte pentagrama) {
		this.pentagrama = pentagrama;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
}
