package com.rising.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

public class Scroll {
	
	//  Gestión del scroll vertical
	private float altoPantalla = 0;
	private float divY = 0;
	private float finalScrollY = 0;
	private float limiteVisibleArriba = 0;
    private float limiteVisibleAbajo = 0;
    private int margenFinalScrollY = 0;
    private boolean mostrarBarraVertical = false;
    private float offsetBarraVertical = 0;
    private float porcentajeAltura = 0;
    private int tamanoBarraVertical = 0;
    private static float yOffset = 0;
    private float yPrevious = 0;
    private float yDown = 0;
    private boolean y_initialized = false;
    
    //  Gestión del scroll horizontal
    private float anchoPantalla = 0;
	private float divX = 0;
	private float finalScrollX = 0;
	private float limiteVisibleDerecha = 0;
    private float limiteVisibleIzquierda = 0;
    private int margenFinalScrollX = 0;
    private boolean mostrarBarraHorizontal = false;
    private float offsetBarraHorizontal = 0;
    private float porcentajeAnchura = 0;
    private int tamanoBarraHorizontal = 0;
    private static float xOffset = 0;
    private float xPrevious = 0;
    private float xDown = 0;
    private boolean x_initialized = false;
	
	public Scroll(Config config) {;
		margenFinalScrollY = config.getDistanciaPentagramas();
		margenFinalScrollX = config.getXInicialPentagramas();
	}
	
	public void down(MotionEvent e) {
		yPrevious = e.getY();
		xPrevious = e.getX();
		yDown = yPrevious;
		xDown = xPrevious;
        mostrarBarraVertical = true;
        mostrarBarraHorizontal = true;
	}
	
	public void move(MotionEvent e, Vista vista) {
		if (vista == Vista.VERTICAL)
			moveVertical(e.getY());
		else
			moveHorizontal(e.getX());
	}
	
	private void moveHorizontal(float x) {
		xOffset += x - xPrevious;
        porcentajeAnchura = - xOffset / finalScrollX;
        offsetBarraHorizontal = -anchoPantalla * porcentajeAnchura;
        divX = x - xPrevious;
        xPrevious = x;
        limiteVisibleIzquierda += divX;
        limiteVisibleDerecha += divX;
        
        Log.i("XOFFSET", xOffset + "");
        Log.i("PORCENTAJE ANCHURA", porcentajeAnchura + "");
        Log.i("OFFSET BARRA HORIZONTAL", offsetBarraHorizontal + "");
        Log.i("DIVX", divX + "");
        Log.i("XPREVIOUS", xPrevious + "");
        Log.i("LIMITE VISIBLE IZQUIERDA", limiteVisibleIzquierda + "");
        Log.i("LIMITE VISIBLE DERECHA", limiteVisibleDerecha + "");

        if (limiteVisibleIzquierda > 0) {
        	Log.i("ENTRÉ EN LÍMITE VISIBLE IZQUIERDA < 0", "");
        	
        	xOffset = 0;
        	divX = 0;
        	limiteVisibleIzquierda = 0;
        	limiteVisibleDerecha = anchoPantalla;
        	
        	Log.i("XOFFSET", xOffset + "");
        	Log.i("DIVX", divX + "");
        	Log.i("LIMITE VISIBLE IZQUIERDA", limiteVisibleIzquierda + "");
            Log.i("LIMITE VISIBLE DERECHA", limiteVisibleDerecha + "");
        }

        if (limiteVisibleDerecha < -finalScrollX) {
        	Log.i("ENTRÉ EN LÍMITE VISIBLE DERECHA < - FINAL SCROLL X", "");
        	
        	xOffset = -finalScrollX - anchoPantalla;
        	divX = 0;
        	limiteVisibleIzquierda = -finalScrollX - anchoPantalla;
        	limiteVisibleDerecha = -finalScrollX;
        	
        	Log.i("XOFFSET", xOffset + "");
        	Log.i("DIVX", divX + "");
        	Log.i("LIMITE VISIBLE IZQUIERDA", limiteVisibleIzquierda + "");
            Log.i("LIMITE VISIBLE DERECHA", limiteVisibleDerecha + "");
        }

        mostrarBarraHorizontal = true;
	}
	
	private void moveVertical(float y) {
		yOffset += y - yPrevious;
        porcentajeAltura = - yOffset / finalScrollY;
        offsetBarraVertical = -altoPantalla * porcentajeAltura;
        divY = y - yPrevious;
        yPrevious = y;
        limiteVisibleArriba += divY;
        limiteVisibleAbajo += divY;

        if (limiteVisibleArriba > 0) {
        	yOffset = 0;
        	divY = 0;
        	limiteVisibleArriba = 0;
        	limiteVisibleAbajo = altoPantalla;
        }

        if (limiteVisibleAbajo < -finalScrollY) {
        	yOffset = -finalScrollY - altoPantalla;
        	divY = 0;
        	limiteVisibleArriba = -finalScrollY - altoPantalla;
        	limiteVisibleAbajo = -finalScrollY;
        }

        mostrarBarraVertical = true;
	}
	
	public boolean up(MotionEvent e) {
		mostrarBarraVertical = false;
    	mostrarBarraHorizontal = false;
    	
    	return (yDown == e.getY());
	}
	
	public float getXOffset() {
		return xOffset;
	}
	
	public float getYOffset() {
		return yOffset;
	}
	
	public void inicializarHorizontal(int width, int xFin) {
		if (!x_initialized) {
    		
    		limiteVisibleDerecha = - width;
    		anchoPantalla = limiteVisibleDerecha;
    		finalScrollX = xFin + margenFinalScrollX;
    		tamanoBarraHorizontal = (int) ( (anchoPantalla / finalScrollX) * anchoPantalla);

    		Log.i("LIMITE VISIBLE DERECHA", limiteVisibleDerecha + "");
    		Log.i("ANCHO PANTALLA", anchoPantalla + "");
    		Log.i("FINAL SCROLL X", finalScrollX + "");
    		Log.i("TAMANO BARRA HORIZONTAL", tamanoBarraHorizontal + "");
    		
    		x_initialized = true;
		}
	}
	
	public void inicializarVertical(int height, int lastMarginY) {
		if (!y_initialized) {
    		
    		limiteVisibleAbajo = - height;
    		altoPantalla = limiteVisibleAbajo;
    		finalScrollY = lastMarginY + margenFinalScrollY;
    		tamanoBarraVertical = (int) ( (altoPantalla / finalScrollY) * altoPantalla);
			
    		y_initialized = true;
		}
	}
	
	public void back(Vista vista){
		if (vista == Vista.VERTICAL) {
			yOffset = 0;
			limiteVisibleArriba = 0;
			limiteVisibleAbajo = altoPantalla;
		}
		else {
			xOffset = 0;
	    	limiteVisibleIzquierda = 0;
	    	limiteVisibleDerecha = anchoPantalla;
		}
	}

	public void forward(Vista vista) {
		if (vista == Vista.VERTICAL) {
			yOffset = -finalScrollY - altoPantalla;
	    	limiteVisibleArriba = -finalScrollY - altoPantalla;
	    	limiteVisibleAbajo = -finalScrollY;
		}
		else {
			xOffset = -finalScrollX - anchoPantalla;
	    	limiteVisibleIzquierda = -finalScrollX - anchoPantalla;
	    	limiteVisibleDerecha = -finalScrollX;
		}
	}
	
	public void dibujarBarra(Canvas canvas, Vista vista) {
		if (vista == Vista.VERTICAL) {
			if (mostrarBarraVertical) {
				int x_end = canvas.getWidth() - 30;
				
				Paint paint = new Paint();
		    	paint.setStrokeWidth(5);
		    	paint.setARGB(255, 0, 0, 0);
		    	canvas.drawLine(x_end, offsetBarraVertical - yOffset, 
		    			x_end, offsetBarraVertical + tamanoBarraVertical - yOffset, paint);
			}
		}
		else {
			if (mostrarBarraHorizontal) {
				int y_end = canvas.getHeight() - 30;
				
				Paint paint = new Paint();
		    	paint.setStrokeWidth(5);
		    	paint.setARGB(255, 0, 0, 0);
		    	canvas.drawLine(offsetBarraHorizontal - xOffset, y_end, 
		    			offsetBarraHorizontal + tamanoBarraHorizontal - xOffset, y_end, paint);
			}
		}
    }
}
