package com.rising.drawing;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class OrdenDibujo {
	
	private Paint paint;
	private String texto;
	private Bitmap imagen;
	private DrawOrder orden;
	private RectF rectf;
	
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int radius;
	
	public OrdenDibujo() {
		paint = new Paint();
		texto = "";
		imagen = null;
		orden = null;
		rectf = null;
		
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
	
	public RectF getRectF() {
		return rectf;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public int getX1() {
		return x1;
	}
	
	public int getX2() {
		return x2;
	}
	
	public int getY1() {
		return y1;
	}
	
	public int getY2() {
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
			case SET_STYLE_STROKE:
				paint.setStyle(Paint.Style.STROKE);
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
	
	public void setRectF(RectF rectf) {
		this.rectf = rectf;
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public void setOrden(DrawOrder orden) {
		this.orden = orden;
	}
	
	public void setX1(int x1) {
		this.x1 = x1;
	}
	
	public void setY1(int y1) {
		this.y1 = y1;
	}
	
	public void setX2(int x2) {
		this.x2 = x2;
	}
	
	public void setY2(int y2) {
		this.y2 = y2;
	}
}
