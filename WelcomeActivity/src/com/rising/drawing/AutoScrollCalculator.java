package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.Compas;
import com.rising.drawing.figurasgraficas.Partitura;
import com.rising.drawing.figurasgraficas.Vista;

public class AutoScrollCalculator 
{
	private Vista vista;
	private Config config;
	
	private int currentX;
	private boolean firstMeasure = true;
	
	private int currentY;
	private int golpesSonido;
	private int primerCompas;
	private int ultimoCompas;
	private boolean primerRenglon = true;
	private int[] scrolls;

	public AutoScrollCalculator(final Vista vista)
	{
		this.vista = vista;
		
		config = Config.getInstance();
	}
	
	public int[] calculateScrolls(final Partitura partitura)
	{
		if (vista == Vista.VERTICAL)
		{
			calculateVerticalScrolls(partitura.getCompases());
		} 
		else {
			calculateHorizontalScrolls(partitura.getCompases());
		}
		
		return scrolls;
	}
	
	private void calculateHorizontalScrolls(ArrayList<Compas> compases)
	{
		currentX = compases.get(0).getXIni();
		
		final int numCompases = compases.size();
		scrolls = new int[numCompases];
		
		for (int i=0; i<numCompases; i++)
		{
			calculateScrollForMeasureX(compases.get(i), i);
		}
	}
	
	private void calculateVerticalScrolls(ArrayList<Compas> compases)
	{
		currentY = compases.get(0).getYIni();
		
		final int numCompases = compases.size();
		scrolls = new int[numCompases];

		for (int i=0; i<numCompases; i++)
		{
			calculateScrollForMeasure(compases.get(i), i);
		}
	}
	
	private void calculateScrollForMeasure(final Compas compas, final int index)
	{
		final int compasYIni = compas.getYIni();
		
		if (currentY != compasYIni) 
		{
			addScrollsToArray(compasYIni);
			reassignValues(index, compasYIni);
		}

		golpesSonido += compas.numPulsos();
		ultimoCompas = index;
	}
	
	private void calculateScrollForMeasureX(final Compas compas, final int index)
	{
		final int compasXIni = compas.getXIni();
		
		if (currentX != compas.getXIni())
		{
			addScrollsToArrayX(compasXIni, index);
			reassignValuesX(compasXIni);
		}
		
		golpesSonido += compas.numPulsos();
		ultimoCompas = index;
	}
	
	private void addScrollsToArray(final int compasYIni)
	{
		final int scrollPorCompas = primerRenglon ? 
				0 : scrollPorCompas(golpesSonido, currentY, compasYIni);
		
		for (int j=primerCompas; j<=ultimoCompas; j++) {
			scrolls[j] = scrollPorCompas;
		}
	}
	
	private void addScrollsToArrayX(final int compasXIni, final int index)
	{
		scrolls[index - 1] = firstMeasure ? 
				0 : (compasXIni - currentX) / golpesSonido;
	}
	
	private int scrollPorCompas(final int golpesSonido, final int previousY, final int currentY)
	{
		final int yIni = previousY - config.distanciaPentagramas;
		final int yFin = currentY - config.distanciaPentagramas;
		
		return (yFin - yIni) / golpesSonido;
	}
	
	private void reassignValues(final int index, final int compasYIni)
	{
		primerCompas = index;
		golpesSonido = 0;
		currentY = compasYIni;
		primerRenglon = false;
	}
	
	private void reassignValuesX(final int compasXIni)
	{
		golpesSonido = 0;
		currentX = compasXIni;
		firstMeasure = false;
	}
}