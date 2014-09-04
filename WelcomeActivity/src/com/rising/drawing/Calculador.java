package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.Clave;
import com.rising.drawing.figurasgraficas.Compas;
import com.rising.drawing.figurasgraficas.Intensidad;
import com.rising.drawing.figurasgraficas.Nota;
import com.rising.drawing.figurasgraficas.Partitura;
import com.rising.drawing.figurasgraficas.Pedal;
import com.rising.drawing.figurasgraficas.Quintas;
import com.rising.drawing.figurasgraficas.Tempo;
import com.rising.drawing.figurasgraficas.Texto;
import com.rising.drawing.figurasgraficas.Vista;
import com.rising.drawing.figurasgraficas.Wedge;

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
		clave.imagenClave = obtenerImagenDeClave(claveByte);
		clave.x = compasMarginX + posicionX;
		clave.y = marginY + obtenerPosicionYDeClave(claveByte);
		clave.valorClave = claveByte;
		clave.pentagrama = pentagrama;
		clave.position = clef.getPosition();
		
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
			quintas.notaQuintas = notaQuintasByte;
			quintas.valorQuintas = valorQuintasByte;
			quintas.x = compasMarginX + posicion;
			quintas.margenY = compasMarginY;
	
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
			texto.texto = compas.getWordsString(i);
			texto.x = compasMarginX + posicionX;
			texto.y = obtenerPosicionYDeElementoGrafico(compas.getWordsLocation(i));
			
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
			intensidad.imagen = obtenerImagenDeIntensidad(intensidadByte);
			intensidad.x = compasMarginX + posicion;
			intensidad.y = obtenerPosicionYDeElementoGrafico(location);
	
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
		final Object[] pedals = compas.getPedals();
		ElementoGrafico pedalElement;
		
		for (int i=0; i<pedals.length; i++) 
		{
			pedalElement = (ElementoGrafico) pedals[i];
			final byte location = pedalElement.getValue(0);
			final int posicion = calcularPosicionX(pedalElement.getPosition());

			final Pedal pedal = new Pedal();
			pedal.x = compasMarginX + posicion;
			pedal.y = obtenerPosicionYDeElementoGrafico(location);
			
			if (pedalElement.getValue(1) == 25) {
				pedal.imagen = bitmapManager.getPedalStart();
				compas.addPedalInicio(pedal);
			} else {
				pedal.imagen = bitmapManager.getPedalStop();
				compas.addPedalFin(pedal);
			}
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
			final byte clave = compas.getClaveDeNota(nota);
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
		final YPositionCalculator yPositionCalculator = new YPositionCalculator();
		int cooY = 0;
		
		final int margenY = compasMarginY + 
				(config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas) * (nota.getPentagrama() - 1);
		
		byte octava = yPositionCalculator.prepararOctava(nota.getOctava());
		
		if (!nota.silencio()) 
		{
			switch (instrumento) 
			{
				case 1: 
				{
					switch (clave) 
					{
						case 1: 
						{
							switch (octava) 
							{
								case 3:
									cooY = yPositionCalculator.guitarG2Octave3(nota.getStep(), margenY);
									break;

								case 4:
									cooY = yPositionCalculator.guitarG2Octave4(nota.getStep(), margenY);
									break;

								case 5:
									cooY = yPositionCalculator.guitarG2Octave5(nota.getStep(), margenY);
									break;

								case 6:
									cooY = yPositionCalculator.guitarG2Octave6(nota.getStep(), margenY);
									break;
							}
							
							break;
						}
					}	
					break;
				}
				case 2: 
				{
					switch (clave) 
					{
						case 1: 
						{
							switch (octava) 
							{
								case 2:
									cooY = yPositionCalculator.pianoG2Octave2(nota.getStep(), margenY);
									break;

								case 3:
									cooY = yPositionCalculator.pianoG2Octave3(nota.getStep(), margenY);
									break;

								case 4:
									cooY = yPositionCalculator.pianoG2Octave4(nota.getStep(), margenY);
									break;

								case 5:
									cooY = yPositionCalculator.pianoG2Octave5(nota.getStep(), margenY);
									break;

								case 6:
									cooY = yPositionCalculator.pianoG2Octave6(nota.getStep(), margenY);
									break;
							}
							
							break;
						}
						
						case 2: 
						{
							switch (octava) 
							{
								case 1:
									cooY = yPositionCalculator.pianoF4Octave1(nota.getStep(), margenY);
									break;

								case 2:
									cooY = yPositionCalculator.pianoF4Octave2(nota.getStep(), margenY);
									break;

								case 3:
									cooY = yPositionCalculator.pianoF4Octave3(nota.getStep(), margenY);
									break;

								case 4:
									cooY = yPositionCalculator.pianoF4Octave4(nota.getStep(), margenY);
									break;
							}
						}
					}
					
					break;
				}
			}
		} else {
			cooY = yPositionCalculator.silence(nota.getFiguracion(), margenY);
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
				claveActual[i] = clave.valorClave;
			}
		}
	}
	
	private void recolocarCompases(final Compas compas)
	{
		if (hayQueReajustar(compas.getXFin())) 
		{
			MeasureReadjuster measureReadjuster = new MeasureReadjuster(compasMarginY);
			measureReadjuster.moverCompasAlSiguienteRenglon(compas, partitura.getStaves());
			
			compasMarginX = compas.getXFin();
			compasMarginY = measureReadjuster.getUpdatedCompasMarginY();
			
			ultimoCompas = compasActual - 1;
			measureReadjuster.reajustarCompases(partitura, primerCompas, ultimoCompas);
			primerCompas = compasActual;
		}
	}
	
	private boolean hayQueReajustar(final int xFin)
	{
		return vista == Vista.VERTICAL && xFin > config.xFinalPentagramas;
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
