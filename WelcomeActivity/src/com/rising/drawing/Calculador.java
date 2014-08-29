package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasGraficas.Clave;
import com.rising.drawing.figurasGraficas.Compas;
import com.rising.drawing.figurasGraficas.Intensidad;
import com.rising.drawing.figurasGraficas.Nota;
import com.rising.drawing.figurasGraficas.Partitura;
import com.rising.drawing.figurasGraficas.Pedal;
import com.rising.drawing.figurasGraficas.Quintas;
import com.rising.drawing.figurasGraficas.Tempo;
import com.rising.drawing.figurasGraficas.Texto;
import com.rising.drawing.figurasGraficas.Wedge;

import android.graphics.Bitmap;

public class Calculador {
	
	private transient final Vista vista;
	private transient final Config config;
	private transient final Partitura partitura;
	private transient final BitmapManager bitmapManager;
	private transient int compasMarginX;
	private transient int compasMarginY;
	private transient int compasActual;
	private transient byte[] claveActual = {0, 0};
	private transient Tempo tempoActual;
	private transient int primerCompas;
	private transient int ultimoCompas;
	
	public Calculador(final Partitura partitura, final BitmapManager bitmapManager, final Vista vista)
	{
		this.partitura = partitura;
		this.bitmapManager = bitmapManager;
		this.vista = vista;
		
		config = Config.getInstance();
		compasMarginX = config.xInicialPentagramas;
		compasMarginY = config.margenSuperior + config.margenInferiorAutor;
	}

	public void calcularPosicionesDeCompases() 
	{
		int numeroCompas = partitura.getFirstNumber();
		
		final int numeroCompases = partitura.getCompases().size();
		for (int i=0; i<numeroCompases; i++) 
		{
			compasActual = i;
			
			final Compas compas = partitura.getCompas(compasActual);
			compas.setNumeroCompas(numeroCompas++);
			
			calcularPosicionesDeCompas(compas);
		}
	}
	
	private void calcularPosicionesDeCompas(final Compas compas) 
	{
		establecerPosicionInicialDeCompas(compas);
		calcularFigurasGraficasDeCompas(compas);
		calcularNotasDeCompas(compas);
		establecerPosicionFinalDeCompas(compas);		
		actualizarClavesParaElCompasSiguiente(compas);
		recolocarCompases(compas);
	}
	
	private void establecerPosicionInicialDeCompas(final Compas compas)
	{
		compas.setXIni(compasMarginX);
		compas.setYIni(compasMarginY);
		
		compasMarginX += config.margenIzquierdoCompases;
	}
	
	private void calcularFigurasGraficasDeCompas(final Compas compas)
	{
		calcularClefs(compas);
		calcularFifths(compas);
		calcularTime(compas);
		calcularWords(compas);
		calcularDynamics(compas);
		calcularPedals(compas);
		calcularWedges(compas);
	}
	
	private void calcularClefs(final Compas compas) 
	{
		final int numClefs = compas.numClefs();
		
		for (int i=0; i<numClefs; i++) 
		{
			final Clave clave = obtenerClave(compas.getClef(i));
			compas.addClave(clave);
		}
	}
	
	private Clave obtenerClave(final ElementoGrafico clef)
	{		
		final int posicionX = calcularPosicionX(clef.getPosition());
		final byte pentagrama = clef.getValue(1);
		final byte claveByte = clef.getValue(2);
		final int marginY = compasMarginY + 
				(config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas) * (pentagrama - 1);

		final Clave clave = new Clave();
		clave.setImagenClave(obtenerImagenDeClave(claveByte));
		clave.setX(compasMarginX + posicionX);
		clave.setY(marginY + obtenerPosicionYDeClave(claveByte));
		clave.setClave(claveByte);
		clave.setPentagrama(pentagrama);
		clave.setPosition(clef.getPosition());
		
		return clave;
	}
	
	private int calcularPosicionX(final int position) 
	{
		return position * config.unidadDesplazamiento / config.divisions;
	}
	
	private Bitmap obtenerImagenDeClave(final byte clave) 
	{
		switch (clave) {
			case 1:
				return bitmapManager.getTrebleClef();
			case 2:
				return bitmapManager.getBassClef();
			default: 
				return null;
		}
	}
	
	private int obtenerPosicionYDeClave(final byte clave)
	{
		return clave == 1 ? - config.yClaveSolSegunda : 0;
	}
	
	private void calcularFifths(final Compas compas) 
	{
		if (compas.hayFifths())
		{
			final ElementoGrafico fifths = compas.getFifths();
			final byte notaQuintasByte = fifths.getValue(1);
			final byte valorQuintasByte = fifths.getValue(2);
			final int posicion = calcularPosicionX(fifths.getPosition());
	
			final Quintas quintas = new Quintas();
			quintas.setNotaQuintas(notaQuintasByte);
			quintas.setValorQuintas(valorQuintasByte);
			quintas.setX(compasMarginX + posicion);
			quintas.setMargenY(compasMarginY);
	
			compas.setQuintas(quintas);
		}
	}
	
	private void calcularTime(final Compas compas) 
	{
		if (compas.hayTime()) 
		{
			final Tempo tempo = obtenerTempo(compas.getTime());
			compas.setTempo(tempo);
			tempoActual = tempo;
		}
		else {
			compas.setTempo(clonarTempo(tempoActual));
		}
	}
	
	private Tempo obtenerTempo(final ElementoGrafico time)
	{
		final Tempo tempo = new Tempo();

		switch (time.getValue(1)) 
		{
			case 1:
				inicializarTempo(tempo, time.getPosition(), 3, 8);
				break;
			case 2:
				inicializarTempo(tempo, time.getPosition(), 4, 4);
				break;
			case 3:
				inicializarTempo(tempo, time.getPosition(), 2, 4);
				break;
			case 4:
				inicializarTempo(tempo, time.getPosition(), 7, 4);
				break;
			case 5:
				inicializarTempo(tempo, time.getPosition(), 6, 8);
				break;
			case 6:
				inicializarTempo(tempo, time.getPosition(), 3, 4);
				break;
			case 7:
				inicializarTempo(tempo, time.getPosition(), 6, 4);
				break;
			default:
				break;
		}
		
		return tempo;
	}
	
	private void inicializarTempo(final Tempo tempo, final int xPosition, 
			final int numerador, final int denominador) 
	{
		final int posicionX = calcularPosicionX(xPosition);
		
		tempo.setDibujar(true);
		tempo.setNumerador(numerador);
		tempo.setDenominador(denominador);
		tempo.setX(compasMarginX + posicionX);
		tempo.setYNumerador(compasMarginY + config.distanciaLineasPentagrama * 2);
		tempo.setYDenominador(compasMarginY + config.distanciaLineasPentagrama * 4);
	}

	//  Los tempos clonados no se dibujan, ya que comparten tempo con un compás anterior
	private Tempo clonarTempo(final Tempo tempoViejo) 
	{
		final Tempo tempoNuevo = new Tempo();

		tempoNuevo.setNumerador(tempoViejo.getNumerador());
		tempoNuevo.setDenominador(tempoViejo.getDenominador());
		tempoNuevo.setDibujar(false);
		tempoNuevo.setX(-1);
		tempoNuevo.setYNumerador(-1);
		tempoNuevo.setYDenominador(-1);
		
		return tempoNuevo;
	}

	private void calcularWords(final Compas compas) 
	{
		final int numWords = compas.numWords();
		
		for (int i=0; i<numWords; i++) 
		{
			final int posicionX = calcularPosicionX(compas.getWordsPosition(i));
			
			final Texto texto = new Texto();
			texto.setTexto(compas.getWordsString(i));
			texto.setX(compasMarginX + posicionX);
			texto.setY(obtenerPosicionYDeElementoGrafico(compas.getWordsLocation(i)));
			
			compas.addTexto(texto);
		}
	}
	
	private int obtenerPosicionYDeElementoGrafico(final int location) 
	{
		switch (location) {
			case 1:
				return compasMarginY - config.distanciaLineasPentagrama * 2;
			case 2:
				return compasMarginY + config.distanciaLineasPentagrama * 6;
			case 3:
				return compasMarginY + config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas - config.distanciaLineasPentagrama * 2;
			case 4:
				return compasMarginY + config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas + config.distanciaLineasPentagrama * 6;
			case 5:
				return compasMarginY + config.distanciaLineasPentagrama * 4 +
						config.distanciaPentagramas / 2;
			default:
				return 0;
		}
	}
	
	private void calcularDynamics(final Compas compas) 
	{
		final int numDynamics = compas.numDynamics();
		
		for (int i=0; i<numDynamics; i++) 
		{
			final ElementoGrafico dynamics = compas.getDynamics(i);
			final byte location = dynamics.getValue(0);
			final byte intensidadByte = dynamics.getValue(1);
			final int posicion = calcularPosicionX(dynamics.getPosition());
	
			final Intensidad intensidad = new Intensidad();
			intensidad.setImagen(obtenerImagenDeIntensidad(intensidadByte));
			intensidad.setX(compasMarginX + posicion);
			intensidad.setY(obtenerPosicionYDeElementoGrafico(location));
	
			compas.addIntensidad(intensidad);
		}
	}
	
	private Bitmap obtenerImagenDeIntensidad(final byte intensidad) 
	{
		switch (intensidad) {
			case 1:
				return bitmapManager.getForte();
			case 2:
				return bitmapManager.getMezzoforte();
			case 3:
				return bitmapManager.getPiano();
			case 4:
				return bitmapManager.getPianissimo();
			case 5:
				return bitmapManager.getForzandoP();
			case 6:
				return bitmapManager.getPianississimo();
			case 7:
				return bitmapManager.getFortissimo();
			case 8:
				return bitmapManager.getForzando();
			default:
				return null;
		}
	}
	
	private void calcularPedals(final Compas compas) 
	{
		int numPedals = compas.numPedalStarts();
		for (int i=0; i<numPedals; i++) 
		{
			final ElementoGrafico pedal = compas.getPedalStart(i);
			final byte location = pedal.getValue(0);
			final int posicion = calcularPosicionX(pedal.getPosition());

			final Pedal pedalInicio = new Pedal();
			pedalInicio.setImagen(bitmapManager.getPedalStart());
			pedalInicio.setX(compasMarginX + posicion);
			pedalInicio.setY(obtenerPosicionYDeElementoGrafico(location));
			
			compas.addPedalInicio(pedalInicio);
		}

		numPedals = compas.numPedalStops();
		for (int i=0; i<numPedals; i++) 
		{
			final ElementoGrafico pedal = compas.getPedalStop(i);
			final byte location = pedal.getValue(0);
			final int posicion = calcularPosicionX(pedal.getPosition());

			final Pedal pedalFin = new Pedal();
			pedalFin.setImagen(bitmapManager.getPedalStop());
			pedalFin.setX(compasMarginX + posicion);
			pedalFin.setY(obtenerPosicionYDeElementoGrafico(location));
			
			compas.addPedalFin(pedalFin);
		}
	}
	
	private void calcularWedges(final Compas compas) 
	{
		final int numWedges = compas.numWedges();
		
		//  Asumimos que los crescendos o diminuendos estarán siempre
		//  contenidos dentro del mismo compás. También estamos
		//  asumiendo que, en la partitura, siempre que se indique el
		//  comienzo de un crescendo o diminuendo, su posición
		//  final vendrá justo después
		
		for (int i=0; i<numWedges; i++) 
		{
			int posicionX = calcularPosicionX(compas.getWedge(i).getPosition());
			final Wedge wedge = new Wedge(compas.getWedge(i).getValue(1), compasMarginX + posicionX);
			wedge.setYIni(obtenerPosicionYDeElementoGrafico(compas.getWedge(i).getValue(0)));

			i++;
			
			posicionX = calcularPosicionX(compas.getWedge(i).getPosition());
			wedge.setXFin(compasMarginX + posicionX);
			
			if (wedge.crescendo()) {
				compas.addCrescendo(wedge);
			} else {
				compas.addDiminuendo(wedge);
			}
		}
	}
	
	private void calcularNotasDeCompas(final Compas compas)
	{
		compas.setXIniNotas(compasMarginX);
		
		final int xOfLastNote = calcularPosicionesDeNotas(compas);
		
		//  Actualizamos la posición x del final del compás
		//  si la nota calculada sobrepasa este valor
		if (compasMarginX + xOfLastNote > compas.getXFin()) {
			compas.setXFin(compasMarginX + xOfLastNote);
		}
	}
	
	private int calcularPosicionesDeNotas(final Compas compas) 
	{
		final ArrayList<Nota> notas = compas.getNotas();
		final int numNotas = notas.size();

		int mayorDistanciaX = 0;
		
		for (int i=0; i<numNotas; i++) 
		{
			final int xDeNota = calcularPosicionesDeNota(compas, notas.get(i));

			if (xDeNota > mayorDistanciaX) {
				mayorDistanciaX = xDeNota;
			}
		}
		
		return mayorDistanciaX;
	}
	
	private int calcularPosicionesDeNota(final Compas compas, final Nota nota) 
	{
		int posicionX = nota.getPosition();
		if (posicionX != -1) 
		{
			posicionX = calcularPosicionX(posicionX);
			nota.setX(compasMarginX + posicionX);
			
			//  Si se coloca una clave en el compás, las notas anteriores
			//  a esta clave deben colocarse según la clave vieja, y las
			//  posteriores según la clave nueva
			final byte clave = compas.getClavePorPentagrama(nota);
			if (clave > -1) {
				claveActual[nota.getPentagrama() - 1] = clave;
			}
			
			final int posicionY = calcularCabezaDeNota(nota);
			nota.setY(posicionY);
		}

		return posicionX;
	}

	private int calcularCabezaDeNota(final Nota nota) 
	{
		final int y = obtenerPosicionYDeNota(nota, 
				claveActual[nota.getPentagrama() - 1], partitura.getInstrument());
		
		return nota.notaDeGracia() ? y + config.margenNotaGracia : y;
	}
	
	private int obtenerPosicionYDeNota(final Nota nota, final byte clave, final byte instrumento)
	{
		int cooY = 0;
		final int margenY = compasMarginY + 
				(config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas) * (nota.getPentagrama() - 1);
		
		byte octava = nota.getOctava();
		if (octava > 10) {
			octava -= 12;
		}
		
		if (!nota.silencio()) {

			switch (instrumento) {

				case 1: {

					switch (clave) {

						case 1: {

							switch (octava) {

								case 3: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY + config.distanciaLineasPentagrama * 5 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY + config.distanciaLineasPentagrama * 5;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama * 8;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagrama * 7 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY + config.distanciaLineasPentagrama * 7;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY + config.distanciaLineasPentagrama * 6 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY + config.distanciaLineasPentagrama * 6;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 4: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY + config.distanciaLineasPentagrama * 2;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY + config.distanciaLineasPentagrama + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama * 4 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagrama * 4;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY + config.distanciaLineasPentagrama * 3 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY + config.distanciaLineasPentagrama * 3;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY + config.distanciaLineasPentagrama * 2 + 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 5: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY - config.distanciaLineasPentagrama - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY - config.distanciaLineasPentagrama * 2;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY - config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY - config.distanciaLineasPentagrama;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 6: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY - config.distanciaLineasPentagrama * 5;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY - config.distanciaLineasPentagrama * 5 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY - config.distanciaLineasPentagrama * 2 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY - config.distanciaLineasPentagrama * 3;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY - config.distanciaLineasPentagrama * 3 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY - config.distanciaLineasPentagrama * 4;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY - config.distanciaLineasPentagrama * 4 - 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}
								default: break;
							}
						}
						default: break;
					}	
					break;
				}

				case 2: {

					switch (clave) {

						case 1: {

							switch (octava) {

								case 2: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY + config.distanciaLineasPentagrama * 9;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY + config.distanciaLineasPentagrama * 8 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama * 11 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagrama * 11;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY + config.distanciaLineasPentagrama * 10 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY + config.distanciaLineasPentagrama * 10;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY + config.distanciaLineasPentagrama * 9 + 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 3: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY + config.distanciaLineasPentagrama * 5 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY + config.distanciaLineasPentagrama * 5;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama * 8;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagrama * 7 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY + config.distanciaLineasPentagrama * 7;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY + config.distanciaLineasPentagrama * 6 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY + config.distanciaLineasPentagrama * 6;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 4: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY + config.distanciaLineasPentagrama * 2;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY + config.distanciaLineasPentagrama + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama * 4 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagrama * 4;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY + config.distanciaLineasPentagrama * 3 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY + config.distanciaLineasPentagrama * 3;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY + config.distanciaLineasPentagrama * 2 + 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 5: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY - config.distanciaLineasPentagrama - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY - config.distanciaLineasPentagrama * 2;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY - config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY - config.distanciaLineasPentagrama;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 6: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY - config.distanciaLineasPentagrama * 5;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY - config.distanciaLineasPentagrama * 5 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY - config.distanciaLineasPentagrama * 2 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY - config.distanciaLineasPentagrama * 3;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY - config.distanciaLineasPentagrama * 3 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY - config.distanciaLineasPentagrama * 4;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY - config.distanciaLineasPentagrama * 4 - 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}
								default: break;
							}
							break;
						}

						case 2: {

							switch (octava) {

								case 1: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY + config.distanciaLineasPentagrama * 6 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY + config.distanciaLineasPentagrama * 6;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama * 9;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagrama * 8 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY + config.distanciaLineasPentagrama * 8;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY + config.distanciaLineasPentagrama * 7 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY + config.distanciaLineasPentagrama * 7;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 2: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY + config.distanciaLineasPentagrama * 3;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY + config.distanciaLineasPentagrama * 2 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama * 5 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagrama * 5;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY + config.distanciaLineasPentagrama * 4 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY + config.distanciaLineasPentagrama * 4;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY + config.distanciaLineasPentagrama * 3 + 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 3: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY - config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY - config.distanciaLineasPentagrama;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY + config.distanciaLineasPentagrama * 2;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY + config.distanciaLineasPentagrama + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY + config.distanciaLineasPentagrama;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY + config.distanciaLineasPentagrama - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}

								case 4: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											cooY = margenY - config.distanciaLineasPentagrama * 4;
											break;

										case 2:
										case 9:
										case 16:
											cooY = margenY - config.distanciaLineasPentagrama * 4 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											cooY = margenY - config.distanciaLineasPentagrama - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											cooY = margenY - config.distanciaLineasPentagrama * 2;
											break;

										case 5:
										case 12:
										case 19:
											cooY = margenY - config.distanciaLineasPentagrama * 2 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											cooY = margenY - config.distanciaLineasPentagrama * 3;
											break;

										case 7:
										case 14:
										case 21:
											cooY = margenY - config.distanciaLineasPentagrama * 3 - 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											cooY = 0;
											break;
									}
									break;
								}
								default: break;
							}
						}
						default: break;
					}
					break;
				}
				default: break;
			}
		}

		else {

			switch (nota.getFiguracion()) {

				case 5:		
				case 6:
				case 7:
				case 8:
				case 9:
					cooY = margenY;
					break;
					
				case 10:
					cooY = margenY + config.distanciaLineasPentagrama + 
						config.distanciaLineasPentagramaMitad + config.ySilencioBlanca;
					break;
					
				case 11:
					cooY = margenY + config.distanciaLineasPentagrama;
					break;

				default: break;
			}
		}

		return cooY;
	}
	
	private void establecerPosicionFinalDeCompas(final Compas compas)
	{
		compas.setXFin(compas.getXFin() + config.margenDerechoCompases);
		compas.setYFin(compasMarginY + 
				config.distanciaLineasPentagrama * 4 + 
				(config.distanciaPentagramas + config.distanciaLineasPentagrama * 4) * 
				(partitura.getStaves() - 1));
		
		compasMarginX = compas.getXFin();
	}

	//  Si hay claves al final de un compás, las notas del siguiente compás
	//  deben regirse por dichas claves. Aquí nos aseguramos de eso
	private void actualizarClavesParaElCompasSiguiente(final Compas compas)
	{
		final int[] clavesAlFinalDelCompas = compas.clavesAlFinalDelCompas(partitura.getStaves());
		for (int i=0; i<clavesAlFinalDelCompas.length; i++) {
			if (clavesAlFinalDelCompas[i] > -1) {
				final Clave clave = compas.getClave(clavesAlFinalDelCompas[i]);
				claveActual[i] = clave.getByteClave();
			}
		}
	}
	
	private void recolocarCompases(final Compas compas)
	{
		if ( vista == Vista.VERTICAL && compas.getXFin() > config.xFinalPentagramas ) {
			moverCompasAlSiguienteRenglon(compas);
			
			ultimoCompas = compasActual - 1;
			reajustarCompases();
			primerCompas = compasActual;
		}
	}
	
	private void moverCompasAlSiguienteRenglon(final Compas compas) 
	{
		final int distanciaX = compas.getXIni() - config.xInicialPentagramas;
		final int distanciaY = (config.distanciaLineasPentagrama * 4 + 
				config.distanciaPentagramas) * partitura.getStaves();

		compas.setXIni(config.xInicialPentagramas);
		compas.setXFin(compas.getXFin() - distanciaX);
		if (compas.getXFin() > config.xFinalPentagramas) {
			compas.setXFin(config.xFinalPentagramas);
		}
		compas.setXIniNotas(compas.getXIniNotas() - distanciaX);

		for (int i=0; i<compas.numClaves(); i++) {
			compas.getClave(i).setX(compas.getClave(i).getX() - distanciaX);
			compas.getClave(i).setY(compas.getClave(i).getY() + distanciaY);
		}

		for (int i=0; i<compas.numIntensidades(); i++) {
			compas.getIntensidad(i).setX(compas.getIntensidad(i).getX() - distanciaX);
			compas.getIntensidad(i).setY(compas.getIntensidad(i).getY() + distanciaY);
		}
		
		for (int i=0; i<compas.numPedalesInicio(); i++) {
			compas.getPedalInicio(i).setX(compas.getPedalInicio(i).getX() - distanciaX);
			compas.getPedalInicio(i).setY(compas.getPedalInicio(i).getY() + distanciaY);
		}

		for (int i=0; i<compas.numPedalesFin(); i++) {
			compas.getPedalFin(i).setX(compas.getPedalFin(i).getX() - distanciaX);
			compas.getPedalFin(i).setY(compas.getPedalFin(i).getY() + distanciaY);
		}
		
		if (compas.hayTempo()) {
			compas.getTempo().setX(compas.getTempo().getX() - distanciaX);
			compas.getTempo().setYNumerador(compas.getTempo().getYNumerador() + distanciaY);
			compas.getTempo().setYDenominador(compas.getTempo().getYDenominador() + distanciaY);
		}
		
		for (int i=0; i<compas.numTextos(); i++) {
			compas.getTexto(i).setX(compas.getTexto(i).getX() - distanciaX);
			compas.getTexto(i).setY(compas.getTexto(i).getY() + distanciaY);
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
		
		compasMarginX = compas.getXFin();
		compasMarginY = compasMarginY + distanciaY;

		compas.setYIni(compasMarginY);
		compas.setYFin(compasMarginY + 
				config.distanciaLineasPentagrama * 4 + 
				(config.distanciaPentagramas + 
						config.distanciaLineasPentagrama * 4) * (partitura.getStaves() - 1));
		
		final int numNotas = compas.numNotas();
		for (int i=0; i<numNotas; i++) {
			compas.getNota(i).setX(compas.getNota(i).getX() - distanciaX);
			compas.getNota(i).setY(compas.getNota(i).getY() + distanciaY);
		}
	}
	
	private void reajustarCompases() {
		final int espacioADistribuir = config.xFinalPentagramas - partitura.getCompas(ultimoCompas).getXFin();

    	final int numCompases = ultimoCompas - primerCompas + 1;
        final int anchoParaCadaCompas = espacioADistribuir / numCompases;
        int posicionX = partitura.getCompas(primerCompas).getXFin() + anchoParaCadaCompas;
        
        //  Primer paso: reajustar ancho y posición de los compases
        for (int i=primerCompas; i<=ultimoCompas; i++) {
        	final Compas compas = partitura.getCompas(i);
        	
        	if (i == primerCompas) {
        		compas.setXFin(posicionX);
        	}
        	else {
	        	final int distanciaXIni = compas.getXIniNotas() - compas.getXIni();
	        	
	        	compas.setXIni(posicionX);
	        	compas.setXIniNotas(posicionX + distanciaXIni);
	            
	        	posicionX = compas.getXFin() + anchoParaCadaCompas;
	            if (i == ultimoCompas) {
	            	posicionX = config.xFinalPentagramas;
	            }
	            compas.setXFin(posicionX);
	            
	            final int numNotas = compas.numNotas();
	            for (int j=0; j<numNotas; j++) {
	            	compas.getNota(j).setX(compas.getNota(j).getX() + anchoParaCadaCompas);
	            }

    			for (int j=0; j<compas.numClaves(); j++) {
    				if (compas.getClave(j) != null) {
    					compas.getClave(j).setX(compas.getClave(j).getX() + anchoParaCadaCompas);
    				}
    			}
	            
    			for (int j=0; j<compas.numIntensidades(); j++) {
            		compas.getIntensidad(j).setX(compas.getIntensidad(j).getX() + anchoParaCadaCompas);
    			}
	            
            	for (int j=0; j<compas.numPedalesInicio(); j++) {
            		compas.getPedalInicio(j).setX(compas.getPedalInicio(j).getX() + anchoParaCadaCompas);
            	}
	            
            	for (int j=0; j<compas.numPedalesFin(); j++) {
            		compas.getPedalFin(j).setX(compas.getPedalFin(j).getX() + anchoParaCadaCompas);
            	}
	            
	            if (compas.hayTempo()) {
	            	compas.getTempo().setX(compas.getTempo().getX() + anchoParaCadaCompas);
	            }
	            
	            for (int j=0; j<compas.numTextos(); j++) {
	            	compas.getTexto(j).setX(compas.getTexto(j).getX() + anchoParaCadaCompas);
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
        }
        
        //  Segundo paso: reajustar posición de las notas y figuras gráficas
        for (int i=primerCompas; i<=ultimoCompas; i++) {
        	final Compas compas = partitura.getCompas(i);
        	final ArrayList<Integer> xsDeElementos = compas.saberXsDeElementos();
        	
        	final int lastX = xsDeElementos.get(xsDeElementos.size() - 1);
        	final int anchoADistribuir = compas.getXFin() - config.margenDerechoCompases - lastX;
        	
        	//  El primer elemento no lo vamos a mover, de ahí el -1
        	final int numElementos = xsDeElementos.size() - 1;
        	int anchoPorElemento = 0;
        	if (numElementos > 0) {
        		anchoPorElemento = anchoADistribuir / numElementos;
        	}
        	
        	reajustarNotasYClaves(compas, xsDeElementos, anchoPorElemento);
        	reajustarFigurasGraficas(compas, anchoPorElemento);
        }
	}

	private void reajustarFigurasGraficas(final Compas compas, final int anchoPorNota) {
		
		int multiplicador = 0;
		final int xPrimeraNota = compas.saberXPrimeraNota();
		
		final ArrayList<Integer> xsDelCompas = compas.saberXsDelCompas();
		
		for (int i=0; i<compas.numIntensidades(); i++) {
    		if (compas.getIntensidad(i).getX() != xPrimeraNota) {
	    		multiplicador = xsDelCompas.indexOf(compas.getIntensidad(i).getX());
	        	compas.getIntensidad(i).setX( 
	        			compas.getIntensidad(i).getX() + anchoPorNota * multiplicador);
    		}
		}
        
		for (int i=0; i<compas.numPedalesInicio(); i++) {
    		if (compas.getPedalInicio(i).getX() != xPrimeraNota) {
	    		multiplicador = xsDelCompas.indexOf(compas.getPedalInicio(i).getX());
	        	compas.getPedalInicio(i).setX( 
	        			compas.getPedalInicio(i).getX() + anchoPorNota * multiplicador);
    		}
		}
        
    	for (int i=0; i<compas.numPedalesFin(); i++) {
        	if (compas.getPedalFin(i).getX() != xPrimeraNota) {
	        	multiplicador = xsDelCompas.indexOf(compas.getPedalFin(i).getX());
	        	compas.getPedalFin(i).setX( 
	        			compas.getPedalFin(i).getX() + anchoPorNota * multiplicador);
        	}
    	}

    	for (int i=0; i<compas.numTextos(); i++) {
    		multiplicador = xsDelCompas.indexOf(compas.getTexto(i).getX());
        	compas.getTexto(i).setX( 
        		compas.getTexto(i).getX() + anchoPorNota * multiplicador);
    	}
        
        for (int i=0; i<compas.numCrescendos(); i++) {
    		multiplicador = xsDelCompas.indexOf(compas.getCrescendo(i).getXIni());
    		compas.getCrescendo(i).setXIni( 
        			compas.getCrescendo(i).getXIni() + anchoPorNota * multiplicador);
    		
    		multiplicador = xsDelCompas.indexOf(compas.getCrescendo(i).getXFin());
    		compas.getCrescendo(i).setXFin( 
        			compas.getCrescendo(i).getXFin() + anchoPorNota * multiplicador);
        }
        
        for (int i=0; i<compas.numDiminuendos(); i++) {
    		multiplicador = xsDelCompas.indexOf(compas.getDiminuendo(i).getXIni());
    		compas.getDiminuendo(i).setXIni( 
        			compas.getDiminuendo(i).getXIni() + anchoPorNota * multiplicador);
    		
    		multiplicador = xsDelCompas.indexOf(compas.getDiminuendo(i).getXFin());
    		compas.getDiminuendo(i).setXFin( 
        			compas.getDiminuendo(i).getXFin() + anchoPorNota * multiplicador);
        }
	}
	
	private void reajustarNotasYClaves(final Compas compas, 
			final ArrayList<Integer> xsDeElementos, final int anchoPorNota) {
		
		//  A cada elemento se le suma una distancia cada vez
    	//  mayor, ya que de lo contrario sólo estaríamos
    	//  desplazándolos todos pero manteniéndolos a la misma
    	//  distancia entre sí mismos que antes
    	final ArrayList<Nota> notas = compas.getNotas();
    	final int numNotas = notas.size();
    	int multiplicador = 0;
    	for (int j=0;j<numNotas;j++) {
			multiplicador = xsDeElementos.indexOf(notas.get(j).getX());
			notas.get(j).setX(notas.get(j).getX() + anchoPorNota * multiplicador);
    	}
    	
    	for (int j=0; j<compas.numClaves(); j++) {
			multiplicador = xsDeElementos.indexOf(compas.getClave(j).getX());
			compas.getClave(j).setX(compas.getClave(j).getX() + anchoPorNota * multiplicador);
		}
    	
	}
	
	public Bitmap obtenerImagenDeCabezaDeNota(final Nota nota) 
	{
		if (nota.silencio()) {

			switch (nota.getFiguracion()) {
				case 5:
					return bitmapManager.getNoteRest64();
				case 6:
					return bitmapManager.getNoteRest32();
				case 7:
					return bitmapManager.getNoteRest16();
				case 8:
					return bitmapManager.getEighthRest();
				case 9:
					return bitmapManager.getQuarterRest();
				case 10:
				case 11:
					return bitmapManager.getRectangle();
				default:
					return null;
			}
		}
		
		else {
			if (nota.getFiguracion() > 9) {
				return bitmapManager.getWhiteHead();
			}
			else {
				return nota.notaDeGracia() ? 
						bitmapManager.getBlackHeadLittle() : bitmapManager.getBlackHead();
			}
		}
	}

	public Bitmap obtenerImagenDeCorcheteDeNota(final Nota nota) 
	{
		if (nota.notaDeGracia()) {
			return nota.haciaArriba() ? 
					bitmapManager.getHeadLittle() : bitmapManager.getHeadInvLittle();
		}
		else {
			return nota.haciaArriba() ? bitmapManager.getHead() : bitmapManager.getHeadInv();
		}
	}
}
