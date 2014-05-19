package com.rising.drawing;

import android.graphics.Bitmap;

public class Pedal {

	private Bitmap imagen;
	private int x;
	private int y;
	
	public Pedal() {
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
	
	public void setImagen(Bitmap imagen) {
		this.imagen = imagen;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
