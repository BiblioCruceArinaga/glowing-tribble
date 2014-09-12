package com.rising.drawing;

import com.rising.drawing.figurasgraficas.Compas;

public class VerticalAutoScrollCalculator extends AbstractAutoScrollCalculator
{
	private transient final Config config;
	
	public VerticalAutoScrollCalculator()
	{
		super();
		
		config = Config.getInstance();
	}
	
	@Override
	protected int getCoordinate(final Compas compas) 
	{
		return compas.getYIni();
	}

	@Override
	protected void addScrollsToArray(final int currentCoordinate, final int index) 
	{
		final int scrollPorCompas = canMove ? 
				scrollPorCompas(golpesSonido, coordinate, currentCoordinate) : 0;
		
		for (int j=primerCompas; j<=index; j++) {
			scrolls[j] = scrollPorCompas;
		}
	}

	private int scrollPorCompas(final int golpesSonido, final int previousY, final int currentY)
	{
		final int yIni = previousY - config.distanciaPentagramas;
		final int yFin = currentY - config.distanciaPentagramas;
		
		return (yFin - yIni) / golpesSonido;
	}
}
