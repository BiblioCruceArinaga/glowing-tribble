package com.rising.drawing.figurasgraficas;

import android.graphics.Bitmap;

public class Intensidad {

	private Bitmap imagen;
	private int x;
	private int y;
	
	public Intensidad() {
		imagen = null;
		x = -1;
		y = -1;
	}
	
	public Bitmap getImagen() {
		return imagen;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setImagen(final Bitmap imagen) {
		this.imagen = imagen;
	}
	
	public void setX(final int x) {
		this.x = x;
	}
	
	public void setY(final int y) {
		this.y = y;
	}
	
}
