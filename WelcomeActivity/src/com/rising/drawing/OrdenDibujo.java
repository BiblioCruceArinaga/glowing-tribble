package com.rising.drawing;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class OrdenDibujo {
	
	private Paint paint = new Paint();
	private String texto = "";
	private Bitmap imagen = null;
	private DrawOrder orden = null;
	private RectF rectf = null;
	private float angulo = 0;
	private boolean clockwiseAngle = false;
	
	private int x1 = -1;
	private int y1 = -1;
	private int x2 = -1;
	private int y2 = -1;
	private int radius = 0;
	
	//  Líneas
	public OrdenDibujo(int strokeWidth, int x1, int y1, int x2, int y2) {
		orden = DrawOrder.DRAW_LINE;
		
		paint.setStrokeWidth(strokeWidth);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	//  Imágenes
	public OrdenDibujo(Bitmap bitmap, int x, int y) {
		orden = DrawOrder.DRAW_BITMAP;
		
		imagen = bitmap;
		x1 = x;
		y1 = y;
	}
	
	//  Círculos
	public OrdenDibujo(int radius, int x, int y) {
		orden = DrawOrder.DRAW_CIRCLE;
		
		this.radius = radius;
		x1 = x;
		y1 = y;
	}
	
	//  Textos
	public OrdenDibujo(int textSize, boolean textAlign, String text, int x, int y) {
		orden = DrawOrder.DRAW_TEXT;
		
		paint.setTextSize(textSize);
		if (textAlign) paint.setTextAlign(Align.CENTER);
		texto = text;
		x1 = x;
		y1 = y;
	}
	
	//  Arcos
	public OrdenDibujo(int strokeWidth, RectF rectf, float angulo, boolean clockwise) {
		orden = DrawOrder.DRAW_ARC;
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(strokeWidth);
		this.rectf = rectf;
		this.angulo = angulo;
		clockwiseAngle = clockwise;
	}
	
	public boolean clockwiseAngle() {
		return clockwiseAngle;
	}
	
	public float getAngulo() {
		return angulo;
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
	
	public void setARGBRed() {
		paint.setARGB(255, 255, 0, 0);
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}
}
