package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.OrdenDibujo;

public class AboveStaveLineDrawer extends AbstractOutOfStaveLineDrawer 
{
	public AboveStaveLineDrawer(final ArrayList<OrdenDibujo> ordenesDibujo)
	{
		super(ordenesDibujo);
	}

	@Override
	public int getThreshold(final int currentYPosition) 
	{
		return currentYPosition - config.distanciaLineasPentagrama - 
				config.distanciaLineasPentagramaMitad;
	}
	
	@Override
	public boolean noteUnderThreshold(final int yNota, final int threshold) 
	{
		return yNota <= threshold;
	}

	@Override
	public boolean notEndOfLoop(final int currentYPosition, final int yNota) 
	{
		return currentYPosition > yNota + config.distanciaLineasPentagrama;
	}

	@Override
	public int updateCurrentYPosition() 
	{
		return - config.distanciaLineasPentagrama;
	}
}
