package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.Compas;

public abstract class AbstractAutoScrollCalculator 
{
	protected transient int[] scrolls;

    protected transient int coordinate;
    protected transient int golpesSonido;
    protected transient int primerCompas;
    protected transient boolean canMove;

    public int[] calculate(final ArrayList<Compas> compases)
    {
        coordinate = getCoordinate(compases.get(0));

		final int numCompases = compases.size();
		scrolls = new int[numCompases];

        for (int i=0; i<numCompases; i++) {
            calculateScrollForMeasure(compases.get(i), i);
        }
        
        return scrolls;
    }

    protected abstract int getCoordinate(Compas compas);

    private void calculateScrollForMeasure(final Compas compas, final int index)
    {
        final int currentCoordinate = getCoordinate(compas);

        if (coordinate != currentCoordinate)
        {
            addScrollsToArray(currentCoordinate, index);
            reassignValues(currentCoordinate, index);
        }

        golpesSonido += compas.numPulsos();
    }

    protected abstract void addScrollsToArray(int currentCoordinate, int index);
    
    private void reassignValues(final int currentCoordinate, final int index)
    {
        primerCompas = index;
        golpesSonido = 0;
        coordinate = currentCoordinate;
        canMove = true;
    }
}
