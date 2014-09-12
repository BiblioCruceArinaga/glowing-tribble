package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.Nota;
import com.rising.drawing.figurasgraficas.OrdenDibujo;

public abstract class AbstractOutOfStaveLineDrawer 
{
	protected transient Config config;
	protected transient ArrayList<OrdenDibujo> ordenesDibujo;
	
	public AbstractOutOfStaveLineDrawer(final ArrayList<OrdenDibujo> ordenesDibujo)
	{
		config = Config.getInstance();
		
		this.ordenesDibujo = ordenesDibujo;
	}
	
	public final void drawOutOfStaveLines(final Nota nota, int currentYPosition,
			final int yNota)
	{		
		final int threshold = getThreshold(currentYPosition);
		
		if (noteUnderThreshold(yNota, threshold)) 
		{
			while (notEndOfLoop(currentYPosition, yNota)) 
			{
				currentYPosition += updateCurrentYPosition();
				
				ordenesDibujo.add( new OrdenDibujo(
						1, nota.getX() - config.margenAnchoCabezaNota, currentYPosition, 
						nota.getX() + config.anchoCabezaNota + config.margenAnchoCabezaNota, 
						currentYPosition));
			}
		}
	}
	
	public abstract int getThreshold(final int currentYPosition);
	public abstract boolean noteUnderThreshold(final int yNota, final int threshold);
	public abstract boolean notEndOfLoop(final int currentYPosition, final int yNota);
	public abstract int updateCurrentYPosition();
}
