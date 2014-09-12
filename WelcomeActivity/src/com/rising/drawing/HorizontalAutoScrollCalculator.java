package com.rising.drawing;

import com.rising.drawing.figurasgraficas.Compas;

public class HorizontalAutoScrollCalculator extends AbstractAutoScrollCalculator // NOPMD by joel on 12/09/14 16:21
{
	@Override
	protected int getCoordinate(final Compas compas) 
	{
		return compas.getXIni();
	}

	@Override
	protected void addScrollsToArray(final int currentCoordinate, final int index) 
	{
		scrolls[index - 1] = canMove ? (currentCoordinate - coordinate) / golpesSonido : 0;
	}
}
