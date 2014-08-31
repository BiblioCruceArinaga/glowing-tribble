package com.rising.drawing;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class OrdenDibujo 
{	
	private transient final Paint paint = new Paint();
	private String texto = "";
	private transient Bitmap imagen;
	private transient DrawOrder orden;
	private transient RectF rectf;
	private transient float angulo;
	private transient boolean isClockwiseAngle;
	
	private transient int x1 = -1;
	private transient int y1 = -1;
	private transient int x2 = -1;
	private transient int y2 = -1;
	private transient int radius;
	
	/**
	 * Dibujar líneas
	 */
	public OrdenDibujo(final int strokeWidth, final int x1, 
			final int y1, final int x2, final int y2) 
	{
		orden = DrawOrder.DRAW_LINE;
		
		paint.setStrokeWidth(strokeWidth);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/**
	 * Dibujar imágenes
	 */
	public OrdenDibujo(final Bitmap bitmap, final int x, final int y) 
	{
		orden = DrawOrder.DRAW_BITMAP;
		
		imagen = bitmap;
		x1 = x;
		y1 = y;
	}
	
	/**
	 * Dibujar círculos
	 */
	public OrdenDibujo(final int radius, final int x, final int y) 
	{
		orden = DrawOrder.DRAW_CIRCLE;
		
		this.radius = radius;
		x1 = x;
		y1 = y;
	}
	
	/**
	 * Dibujar texto
	 */
	public OrdenDibujo(final int textSize, final boolean textAlign, 
			final String text, final int x, final int y) 
	{
		orden = DrawOrder.DRAW_TEXT;
		
		paint.setTextSize(textSize);
		if (textAlign) { 
			paint.setTextAlign(Align.CENTER);
		}
		
		texto = text;
		x1 = x;
		y1 = y;
	}
	
	/**
	 * Dibujar arcos
	 */
	public OrdenDibujo(final int strokeWidth, final RectF rectf, 
			final float angulo, final boolean clockwise) 
	{
		orden = DrawOrder.DRAW_ARC;
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(strokeWidth);
		this.rectf = rectf;
		this.angulo = angulo;
		isClockwiseAngle = clockwise;
	}
	
	public boolean clockwiseAngle() {
		return isClockwiseAngle;
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
	
	public void setTexto(final String texto) {
		this.texto = texto;
	}
}
