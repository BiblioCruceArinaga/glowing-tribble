package com.rising.drawing;

import android.graphics.Bitmap;

public class Clave {
	
	public Bitmap imagenClave;
	public int x;
	public int y;
	
	public Clave() {
		imagenClave = null;
		x = -1;
		y = -1;
	}
	
	public Bitmap getImagenClave() {
		return imagenClave;
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
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
}
