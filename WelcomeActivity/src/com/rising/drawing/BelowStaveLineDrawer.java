package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.OrdenDibujo;

public class BelowStaveLineDrawer extends AbstractOutOfStaveLineDrawer 
{
	public BelowStaveLineDrawer(final ArrayList<OrdenDibujo> ordenesDibujo) 
	{
		super(ordenesDibujo);
	}

	@Override
	public int getThreshold(final int currentYPosition) 
	{
		return currentYPosition + config.distanciaLineasPentagramaMitad;
	}

	@Override
	public boolean noteUnderThreshold(final int yNota, final int threshold) 
	{
		return yNota >= threshold;
	}

	@Override
	public boolean notEndOfLoop(final int currentYPosition, final int yNota) 
	{
		return currentYPosition < yNota;
	}

	@Override
	public int updateCurrentYPosition() 
	{
		return config.distanciaLineasPentagrama;
	}
}
