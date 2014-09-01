package com.rising.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.rising.drawing.figurasgraficas.Partitura;

public class ScrollVertical extends AbstractScroll 
{
	public ScrollVertical()
	{
		super();
		final Config config = Config.getInstance();
		
		this.margenFinalScroll = config.distanciaPentagramas;
		this.margenPrimerCompas = config.margenInferiorAutor;
	}
	
	@Override
	protected int calcularDistancia(final Partitura partitura, 
			final int primerCompas, final int ultimoCompas) 
	{
		return partitura.getCompas(ultimoCompas).getYIni() - 
			   partitura.getCompas(primerCompas).getYIni();
	}

	@Override
	public void dibujarBarra(final Canvas canvas) {
		if (mostrarBarra) {
			final int xEnd = canvas.getWidth() - 30;
			
			final Paint paint = new Paint();
	    	paint.setStrokeWidth(5);
	    	paint.setARGB(255, 0, 0, 0);
	    	canvas.drawLine(xEnd, offsetBarra - cooOffset, 
	    			xEnd, offsetBarra + tamanoBarra - cooOffset, paint);
		}
	}
}