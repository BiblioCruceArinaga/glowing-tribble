package com.rising.drawing;

import android.graphics.Canvas;

import com.rising.drawing.figurasGraficas.Partitura;

public abstract class AbstractScroll 
{
	protected transient int margenFinalScroll;
	protected transient int margenPrimerCompas;
	protected transient boolean mostrarBarra;
	protected transient float offsetBarra;
	protected transient int tamanoBarra;
	protected transient float cooOffset;
	protected transient float limiteVisibleFinal;

	private transient float totalPantallaDisponible;
	private transient float finalScroll;
	private transient float limiteVisibleInicio;
    private transient float cooPrevious;
    private transient float cooDown;
    private transient boolean cooInitialized;
	
	public final void down(final float coo) 
	{
		cooPrevious = coo;
		cooDown = cooPrevious;
        mostrarBarra = true;
	}
	
	public final void move(final float coo) 
	{
		cooOffset += coo - cooPrevious;
        final float porcentajePosicion = - cooOffset / finalScroll;
        offsetBarra = -totalPantallaDisponible * porcentajePosicion;
        final float div = coo - cooPrevious;
        cooPrevious = coo;
        limiteVisibleInicio += div;
        limiteVisibleFinal += div;

        if (limiteVisibleInicio > 0) {
        	cooOffset = 0;
        	limiteVisibleInicio = 0;
        	limiteVisibleFinal = totalPantallaDisponible;
        }

        if (limiteVisibleFinal < -finalScroll) {
        	cooOffset = -finalScroll - totalPantallaDisponible;
        	limiteVisibleInicio = -finalScroll - totalPantallaDisponible;
        	limiteVisibleFinal = -finalScroll;
        }

        mostrarBarra = true;
	}
	
	public final boolean up(final float coo) 
	{
		mostrarBarra = false;
    	
    	return cooDown == coo;
	}
	
	public final float getLimiteVisibleFinal()
	{
		return limiteVisibleFinal;
	}
	
	public final float getCooDown() 
	{
		return cooDown;
	}
	
	public final float getCooOffset() 
	{
		return cooOffset;
	}
	
	public final void hacerScroll(final int distanciaDesplazamiento) 
	{
		limiteVisibleInicio -= distanciaDesplazamiento;
		limiteVisibleFinal -= distanciaDesplazamiento;
		cooOffset -= distanciaDesplazamiento;
	}
	
	public final void inicializar(final int cooLimit, final int lastMargin) 
	{
		if (!cooInitialized) 
		{
    		limiteVisibleFinal = - cooLimit;
    		totalPantallaDisponible = limiteVisibleFinal;
    		finalScroll = lastMargin + margenFinalScroll;
    		tamanoBarra = (int) ( (totalPantallaDisponible / finalScroll) * totalPantallaDisponible);
			
    		cooInitialized = true;
		}
	}

	public abstract void dibujarBarra(Canvas canvas);
	
	public final boolean outOfBoundaries(final int coo) {
		return coo > - limiteVisibleFinal;
	}
	
	public final int distanciaDesplazamiento(final Partitura partitura, 
			final int primerCompas, final int ultimoCompas) 
	{
		int distancia = calcularDistancia(partitura, primerCompas, ultimoCompas);
		
		if (primerCompas == 0) {
			distancia += margenPrimerCompas;
		}
		
		return distancia;
	}
	
	protected abstract int calcularDistancia(Partitura partitura, int primerCompas, int ultimoCompas);
	
	public final void back()
	{
		cooOffset = 0;
		limiteVisibleInicio = 0;
		limiteVisibleFinal = totalPantallaDisponible;
	}

	public final void forward() {
		cooOffset = -finalScroll - totalPantallaDisponible;
    	limiteVisibleInicio = -finalScroll - totalPantallaDisponible;
    	limiteVisibleFinal = -finalScroll;
	}
}
