package com.rising.drawing;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class OrdenDibujo {
	
	private Paint paint;
	private String texto;
	private Bitmap imagen;
	private DrawOrder orden;
	
	private float x1;
	private float y1;
	private float x2;
	private float y2;
	private int radius;
	
	public OrdenDibujo() {
		paint = new Paint();
		texto = "";
		imagen = null;
		orden = null;
		
		x1 = -1;
		x2 = -1;
		y1 = -1;
		y2 = -1;
		radius = 0;
	}
	
	public Bitmap getImagen() {
		return imagen;
	}
	
	public DrawOrder getOrden() {
		return orden;
	}
	
	public Paint getPaint() {
		return paint;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public float getX1() {
		return x1;
	}
	
	public float getX2() {
		return x2;
	}
	
	public float getY1() {
		return y1;
	}
	
	public float getY2() {
		return y2;
	}
	
	public void setImagen(Bitmap imagen) {
		this.imagen = imagen;
	}
	
	public void setPaint(PaintOptions option, float value) {
		switch (option) {
			case SET_STROKE_WIDTH:
				paint.setStrokeWidth(value);
				break;
			case SET_TEXT_SIZE:
				paint.setTextSize(value);
				break;
			case SET_STYLE:
				paint.setStyle(Paint.Style.FILL);
				break;
			case SET_ARGB:
				paint.setARGB(255, (int) value, (int) value, (int) value);
				break;
			case SET_TEXT_ALIGN:
				paint.setTextAlign(Align.CENTER);
				break;
			default: 
				break;
		}
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public void setOrden(DrawOrder orden) {
		this.orden = orden;
	}
	
	public void setX1(float x1) {
		this.x1 = x1;
	}
	
	public void setY1(float y1) {
		this.y1 = y1;
	}
	
	public void setX2(float x2) {
		this.x2 = x2;
	}
	
	public void setY2(float y2) {
		this.y2 = y2;
	}
}
