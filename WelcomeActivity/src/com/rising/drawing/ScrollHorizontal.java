package com.rising.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.rising.drawing.figurasGraficas.Partitura;

public class ScrollHorizontal extends AbstractScroll 
{
	public ScrollHorizontal()
	{
		super();
		final Config config = Config.getInstance();
		
		this.margenFinalScroll = config.xInicialPentagramas;
		this.margenPrimerCompas = config.xInicialPentagramas;
	}
	
	@Override
	protected int calcularDistancia(final Partitura partitura, 
			final int primerCompas, final int ultimoCompas) 
	{
		int distancia = 0;
		
		for (int i=primerCompas; i<ultimoCompas; i++) {
			distancia += partitura.getCompas(i).getXFin() - partitura.getCompas(i).getXIni();
		}
		
		return distancia;
	}

	@Override
	public void dibujarBarra(final Canvas canvas) {
		if (this.mostrarBarra) {
			final int yEnd = canvas.getHeight() - 30;
			
			final Paint paint = new Paint();
	    	paint.setStrokeWidth(5);
	    	paint.setARGB(255, 0, 0, 0);
	    	canvas.drawLine(offsetBarra - cooOffset, yEnd, 
	    			offsetBarra + tamanoBarra - cooOffset, yEnd, paint);
		}
	}
}
