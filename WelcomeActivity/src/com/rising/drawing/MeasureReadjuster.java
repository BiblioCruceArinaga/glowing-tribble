package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.Compas;
import com.rising.drawing.figurasgraficas.Nota;
import com.rising.drawing.figurasgraficas.Partitura;

public class MeasureReadjuster 
{
	private transient final Config config;
	private transient int compasMarginY;
	
	public MeasureReadjuster(final int compasMarginY)
	{
		config = Config.getInstance();
		
		this.compasMarginY = compasMarginY;
	}
	
	public void moverCompasAlSiguienteRenglon(final Compas compas, final int staves) 
	{
		final int distanciaX = compas.getXIni() - config.xInicialPentagramas;
		establecerXsDeCompas(compas, distanciaX);
		
		final int distanciaY = (config.distanciaLineasPentagrama * 4 + 
				config.distanciaPentagramas) * staves;
		moverFigurasGraficasDeCompas(compas, distanciaX, distanciaY);

		compasMarginY = compasMarginY + distanciaY;

		establecerYsDeCompas(compas, staves);
		
		moverNotasDeCompas(compas, distanciaX, distanciaY);
	}
	
	private void establecerXsDeCompas(final Compas compas, final int distanciaX)
	{
		compas.setXIni(config.xInicialPentagramas);
		compas.setXFin(compas.getXFin() - distanciaX);
		
		if (compas.getXFin() > config.xFinalPentagramas) {
			compas.setXFin(config.xFinalPentagramas);
		}
		
		compas.setXIniNotas(compas.getXIniNotas() - distanciaX);
	}
	
	private void moverFigurasGraficasDeCompas(final Compas compas, final int distanciaX, final int distanciaY)
	{
		for (int i=0; i<compas.numClaves(); i++) {
			compas.getClave(i).x = compas.getClave(i).x - distanciaX;
			compas.getClave(i).y = compas.getClave(i).y + distanciaY;
		}

		for (int i=0; i<compas.numIntensidades(); i++) {
			compas.getIntensidad(i).x = compas.getIntensidad(i).x - distanciaX;
			compas.getIntensidad(i).y = compas.getIntensidad(i).y + distanciaY;
		}
		
		for (int i=0; i<compas.numPedalesInicio(); i++) {
			compas.getPedalInicio(i).x = compas.getPedalInicio(i).x - distanciaX;
			compas.getPedalInicio(i).y = compas.getPedalInicio(i).y + distanciaY;
		}

		for (int i=0; i<compas.numPedalesFin(); i++) {
			compas.getPedalFin(i).x = compas.getPedalFin(i).x - distanciaX;
			compas.getPedalFin(i).y = compas.getPedalFin(i).y + distanciaY;
		}
		
		if (compas.hayTempo()) {
			compas.getTempo().setX(compas.getTempo().getX() - distanciaX);
			compas.getTempo().setYNumerador(compas.getTempo().getYNumerador() + distanciaY);
			compas.getTempo().setYDenominador(compas.getTempo().getYDenominador() + distanciaY);
		}
		
		for (int i=0; i<compas.numTextos(); i++) {
			compas.getTexto(i).x = compas.getTexto(i).x - distanciaX;
			compas.getTexto(i).y = compas.getTexto(i).y + distanciaY;
		}
		
		for (int i=0; i<compas.numCrescendos(); i++) {
			compas.getCrescendo(i).setXIni(compas.getCrescendo(i).getXIni() - distanciaX);
			compas.getCrescendo(i).setXFin(compas.getCrescendo(i).getXFin() - distanciaX);
			compas.getCrescendo(i).setYIni(compas.getCrescendo(i).getYIni() + distanciaY);
		}
		
		for (int i=0; i<compas.numDiminuendos(); i++) {
			compas.getDiminuendo(i).setXIni(compas.getDiminuendo(i).getXIni() - distanciaX);
			compas.getDiminuendo(i).setXFin(compas.getDiminuendo(i).getXFin() - distanciaX);
			compas.getDiminuendo(i).setYIni(compas.getDiminuendo(i).getYIni() + distanciaY);
		}
	}
	
	private void establecerYsDeCompas(final Compas compas, final int staves)
	{
		compas.setYIni(compasMarginY);
		compas.setYFin(compasMarginY + 
				config.distanciaLineasPentagrama * 4 + 
				(config.distanciaPentagramas + 
						config.distanciaLineasPentagrama * 4) * (staves - 1));
	}
	
	private void moverNotasDeCompas(final Compas compas, final int distanciaX, final int distanciaY)
	{
		final int numNotas = compas.numNotas();
		for (int i=0; i<numNotas; i++) 
		{
			compas.getNota(i).setX(compas.getNota(i).getX() - distanciaX);
			compas.getNota(i).setY(compas.getNota(i).getY() + distanciaY);
		}
	}
	
	public int getUpdatedCompasMarginY()
	{
		return compasMarginY;
	}
	
	/*
	 * Reajustar compases es un proceso en dos pasos.
	 * 
	 * El primer paso consiste en calcular el ancho sobrante del "renglón"
	 * y añadir a cada compás el ancho que le corresponde. Para añadir
	 * este ancho no basta con mover xFin, ya que las notas y el resto
	 * de elementos gráficos quedarían fuera de los límites del compás.
	 * Por tanto, hay que mover todas las figuras gráficas del compás
	 * y dejarlas en su posición correspondiente con respecto al inicio.
	 * El resultado de esta operación será un compás con sus figuras
	 * gráficas al principio y con un ancho sobrante a la derecha.
	 * Haciendo una analogía con el Word, las figuras estarían
	 * "alineadas a la izquierda"
	 * 
	 * El segundo paso consiste en, siguiendo con la analogía del word,
	 * "justificar" las figuras gráficas. Para ello hay que calcular,
	 * del ancho sobrante de la derecha, cuánto le corresponde a cada
	 * una y asignárselo. A cada elemento se le suma una distancia cada 
	 * vez mayor, ya que de lo contrario sólo estaríamos desplazándolos 
	 * todos pero manteniéndolos a la misma distancia entre sí mismos 
	 * que antes
	 */
	
	public void reajustarCompases(final Partitura partitura, final int primerCompas, final int ultimoCompas) 
	{
		reajustarAnchoYPosicionDeCompases(partitura, primerCompas, ultimoCompas);
        reajustarPosicionNotasYFigurasGraficas(partitura, primerCompas, ultimoCompas);
	}
	
	private void reajustarAnchoYPosicionDeCompases(final Partitura partitura, 
			final int primerCompas, final int ultimoCompas)
	{
		final int espacioADistribuir = config.xFinalPentagramas - partitura.getCompas(ultimoCompas).getXFin();
    	final int numCompases = ultimoCompas - primerCompas + 1;
        final int anchoParaCadaCompas = espacioADistribuir / numCompases;
		int posicionX = partitura.getCompas(primerCompas).getXFin() + anchoParaCadaCompas;
		
		Compas compas;
		for (int i=primerCompas; i<=ultimoCompas; i++) 
        {
        	compas = partitura.getCompas(i);
        	
        	if (i == primerCompas) {
        		compas.setXFin(posicionX);
        	}
        	else {
	        	final int distanciaXIni = compas.getXIniNotas() - compas.getXIni();
	        	
	        	compas.setXIni(posicionX);
	        	compas.setXIniNotas(posicionX + distanciaXIni);
	            
	            posicionX = i == ultimoCompas ? config.xFinalPentagramas : 
	            	compas.getXFin() + anchoParaCadaCompas;
	            
	            compas.setXFin(posicionX);
	            
	            adaptarElementosDeCompasAlNuevoAncho(compas, anchoParaCadaCompas);
        	}
        }
	}
	
	private void adaptarElementosDeCompasAlNuevoAncho(final Compas compas, final int anchoParaCadaCompas)
	{
		final int numNotas = compas.numNotas();
        for (int j=0; j<numNotas; j++) {
        	compas.getNota(j).setX(compas.getNota(j).getX() + anchoParaCadaCompas);
        }

		for (int j=0; j<compas.numClaves(); j++) {
			if (compas.getClave(j) != null) {
				compas.getClave(j).x = compas.getClave(j).x + anchoParaCadaCompas;
			}
		}
        
		for (int j=0; j<compas.numIntensidades(); j++) {
    		compas.getIntensidad(j).x = compas.getIntensidad(j).x + anchoParaCadaCompas;
		}
        
    	for (int j=0; j<compas.numPedalesInicio(); j++) {
    		compas.getPedalInicio(j).x = compas.getPedalInicio(j).x + anchoParaCadaCompas;
    	}
        
    	for (int j=0; j<compas.numPedalesFin(); j++) {
    		compas.getPedalFin(j).x = compas.getPedalFin(j).x + anchoParaCadaCompas;
    	}
        
        if (compas.hayTempo()) {
        	compas.getTempo().setX(compas.getTempo().getX() + anchoParaCadaCompas);
        }
        
        for (int j=0; j<compas.numTextos(); j++) {
        	compas.getTexto(j).x = compas.getTexto(j).x + anchoParaCadaCompas;
        }
        
        for (int j=0; j<compas.numCrescendos(); j++) {
        	compas.getCrescendo(j).setXIni(compas.getCrescendo(j).getXIni() + anchoParaCadaCompas);
        	compas.getCrescendo(j).setXFin(compas.getCrescendo(j).getXFin() + anchoParaCadaCompas);
        }
        
        for (int j=0; j<compas.numDiminuendos(); j++) {
        	compas.getDiminuendo(j).setXIni(compas.getDiminuendo(j).getXIni() + anchoParaCadaCompas);
        	compas.getDiminuendo(j).setXFin(compas.getDiminuendo(j).getXFin() + anchoParaCadaCompas);
        }
	}
	
	private void reajustarPosicionNotasYFigurasGraficas(final Partitura partitura, 
			final int primerCompas, final int ultimoCompas)
	{
		Compas compas;
		
		for (int i=primerCompas; i<=ultimoCompas; i++) 
		{
        	compas = partitura.getCompas(i);
        	
        	final ArrayList<Integer> xsDeNotasYClaves = compas.xsDeCompas(false);
        	final int lastX = xsDeNotasYClaves.get(xsDeNotasYClaves.size() - 1);
        	final int anchoADistribuir = compas.getXFin() - config.margenDerechoCompases - lastX;
        	
        	//  El primer elemento no lo vamos a mover, de ahí el -1
        	final int numElementos = xsDeNotasYClaves.size() - 1;
        	final int anchoPorElemento = numElementos > 0 ? anchoADistribuir / numElementos : 0;
        	
        	reajustarFigurasGraficas(compas, anchoPorElemento);
        	reajustarNotasYClaves(compas, xsDeNotasYClaves, anchoPorElemento);
        }
	}

	private void reajustarFigurasGraficas(final Compas compas, final int anchoPorNota) 
	{
		final ArrayList<Integer> xsDelCompas = compas.xsDeCompas(true);
		final int xPrimeraNota = compas.saberXPrimeraNota();
		
		for (int i=0; i<compas.numIntensidades(); i++) 
		{
    		if (compas.getIntensidad(i).x != xPrimeraNota) {
	    		final int multiplicador = xsDelCompas.indexOf(compas.getIntensidad(i).x);
	        	compas.getIntensidad(i).x = compas.getIntensidad(i).x + anchoPorNota * multiplicador;
    		}
		}
        
		for (int i=0; i<compas.numPedalesInicio(); i++) 
		{
    		if (compas.getPedalInicio(i).x != xPrimeraNota) {
    			final int multiplicador = xsDelCompas.indexOf(compas.getPedalInicio(i).x);
	        	compas.getPedalInicio(i).x = compas.getPedalInicio(i).x + anchoPorNota * multiplicador;
    		}
		}
        
    	for (int i=0; i<compas.numPedalesFin(); i++) 
    	{
        	if (compas.getPedalFin(i).x != xPrimeraNota) {
        		final int multiplicador = xsDelCompas.indexOf(compas.getPedalFin(i).x);
	        	compas.getPedalFin(i).x = compas.getPedalFin(i).x + anchoPorNota * multiplicador;
        	}
    	}

    	for (int i=0; i<compas.numTextos(); i++) 
    	{
    		if (compas.getTexto(i).x != xPrimeraNota) {
	    		final int multiplicador = xsDelCompas.indexOf(compas.getTexto(i).x);
	        	compas.getTexto(i).x = compas.getTexto(i).x + anchoPorNota * multiplicador;
    		}
    	}
        
        for (int i=0; i<compas.numCrescendos(); i++) 
        {
        	int multiplicador = xsDelCompas.indexOf(compas.getCrescendo(i).getXIni());
    		compas.getCrescendo(i).setXIni( 
        			compas.getCrescendo(i).getXIni() + anchoPorNota * multiplicador);
    		
    		multiplicador = xsDelCompas.indexOf(compas.getCrescendo(i).getXFin());
    		compas.getCrescendo(i).setXFin( 
        			compas.getCrescendo(i).getXFin() + anchoPorNota * multiplicador);
        }
        
        for (int i=0; i<compas.numDiminuendos(); i++) 
        {
    		int multiplicador = xsDelCompas.indexOf(compas.getDiminuendo(i).getXIni());
    		compas.getDiminuendo(i).setXIni( 
        			compas.getDiminuendo(i).getXIni() + anchoPorNota * multiplicador);
    		
    		multiplicador = xsDelCompas.indexOf(compas.getDiminuendo(i).getXFin());
    		compas.getDiminuendo(i).setXFin( 
        			compas.getDiminuendo(i).getXFin() + anchoPorNota * multiplicador);
        }
	}
	
	private void reajustarNotasYClaves(final Compas compas, 
			final ArrayList<Integer> xsDeElementos, final int anchoPorNota) 
	{
		final int xPrimeraNota = compas.saberXPrimeraNota();
    	final ArrayList<Nota> notas = compas.getNotas();
    	
    	for (int j=0;j<notas.size();j++) 
    	{
    		if (notas.get(j).getX() != xPrimeraNota) {
				final int multiplicador = xsDeElementos.indexOf(notas.get(j).getX());
				notas.get(j).setX(notas.get(j).getX() + anchoPorNota * multiplicador);
    		}
    	}
    	
    	for (int j=0; j<compas.numClaves(); j++) 
    	{
			final int multiplicador = xsDeElementos.indexOf(compas.getClave(j).x);
			compas.getClave(j).x = compas.getClave(j).x + anchoPorNota * multiplicador;
		}
	}
}
