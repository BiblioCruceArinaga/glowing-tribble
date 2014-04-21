package com.rising.drawing;

import android.graphics.Bitmap;
import android.graphics.Paint;

public class OrdenDibujo {
	
	private Paint paint;
	private String texto;
	private Bitmap imagen;
	private DrawOrder orden;
	
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	public OrdenDibujo() {
		paint = new Paint();
		texto = "";
		imagen = null;
		orden = null;
		
		x1 = -1;
		x2 = -1;
		y1 = -1;
		y2 = -1;
	}
	
	public DrawOrder getOrden() {
		return orden;
	}
	
	public Paint getPaint() {
		return paint;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public int getX1() {
		return x1;
	}
	
	public int getY1() {
		return y1;
	}
	
	public void setPaint(PaintOptions option, int value) {
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
				paint.setARGB(255, value, value, value);
				break;
			default: 
				break;
		}
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
	
	public void setY1(int x2) {
		this.x2 = x2;
	}
	
	public void setX2(int y1) {
		this.y1 = y1;
	}
	
	public void setY2(int y2) {
		this.y2 = y2;
	}
}
