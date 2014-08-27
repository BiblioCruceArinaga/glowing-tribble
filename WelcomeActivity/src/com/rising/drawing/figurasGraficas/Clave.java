package com.rising.drawing.figurasGraficas;

import android.graphics.Bitmap;

public class Clave {
	
	private Bitmap imagenClave;
	private int x;
	private int y;
	private byte pentagrama;
	private byte clave;
	private int position;
	
	public Clave() {
		imagenClave = null;
		x = -1;
		y = -1;
		
		pentagrama = -1;
		clave = -1;
		position = -1;
	}
	
	public byte getByteClave() {
		return clave;
	}
	
	public Bitmap getImagenClave() {
		return imagenClave;
	}
	
	public byte getPentagrama() {
		return pentagrama;
	}
	
	public int getPosition() {
		return position;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setClave(byte clave) {
		this.clave = clave;
	}
	
	public void setImagenClave(Bitmap imagenClave) {
		this.imagenClave = imagenClave;
	}
	
	public void setPentagrama(byte pentagrama) {
		this.pentagrama = pentagrama;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
}
