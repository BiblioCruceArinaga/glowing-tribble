package com.rising.drawing;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Calculador {
	
	private Vista vista;
	private Config config;
	private Partitura partitura;
	private BitmapManager bitmapManager;
	private int compas_margin_x = 0;
	private int compas_margin_y = 0;
	private int compasActual = 0;
	private byte[] claveActual = {0, 0};
	private Tempo tempoActual = null;
	private int primerCompas = 0;
	private int ultimoCompas = 0;
	
	public Calculador(Partitura partitura, int compas_margin_y, 
			BitmapManager bitmapManager, Vista vista)
	{
		this.partitura = partitura;
		this.compas_margin_y = compas_margin_y;
		this.bitmapManager = bitmapManager;
		this.vista = vista;
		
		config = Config.getInstance();
		compas_margin_x = config.xInicialPentagramas;
	}
	
	private int calcularCabezaDeNota(Nota nota, int posicion) {
		int y = obtenerPosicionYDeNota(nota, 
				claveActual[nota.getPentagrama() - 1], partitura.getInstrument());
		if (nota.notaDeGracia()) y += config.margenNotaGracia;

		return y;
	}

	private void calcularClefs(Compas compas) {
		ArrayList<ElementoGrafico> clefs = compas.getClefs();
		ElementoGrafico clef;
		int x_position = -1;
		
		for (int i=0; i<clefs.size(); i++) {
			clef = clefs.get(i);
			
			x_position = calcularPosicionX(clef.getPosition());

			byte pentagrama = clef.getValue(1);
			byte claveByte = clef.getValue(2);

			//  El margen Y depende del pentagrama al que pertenezca el compás
			int marginY = compas_margin_y + 
					(config.distanciaLineasPentagrama * 4 + 
							config.distanciaPentagramas) * (pentagrama - 1);

			Clave clave = new Clave();
			clave.setImagenClave(obtenerImagenDeClave(claveByte));
			clave.setX(compas_margin_x + x_position);
			clave.setY(marginY + obtenerPosicionYDeClave(claveByte));
			clave.setClave(claveByte);
			clave.setPentagrama(pentagrama);
			clave.setPosition(clef.getPosition());
			
			compas.addClave(clave);
		}
	}
	
	private void calcularDynamics(Compas compas) {
		int numDynamics = compas.numDynamics();
		
		for (int i=0; i<numDynamics; i++) {
			ElementoGrafico dynamics = compas.getDynamics(i);
			byte location = dynamics.getValue(0);
			byte intensidadByte = dynamics.getValue(1);
			int posicion = calcularPosicionX(dynamics.getPosition());
	
			Intensidad intensidad = new Intensidad();
			intensidad.setImagen(obtenerImagenDeIntensidad(intensidadByte));
			intensidad.setX(compas_margin_x + posicion);
			intensidad.setY(obtenerPosicionYDeElementoGrafico(location));
	
			compas.addIntensidad(intensidad);
		}
	}
	
	private void calcularFifths(Compas compas) {
		ElementoGrafico fifths = compas.getFifths();
		byte notaQuintasByte = fifths.getValue(1);
		byte valorQuintasByte = fifths.getValue(2);
		int posicion = calcularPosicionX(fifths.getPosition());

		Quintas quintas = new Quintas();
		quintas.setNotaQuintas(notaQuintasByte);
		quintas.setValorQuintas(valorQuintasByte);
		quintas.setX(compas_margin_x + posicion);
		quintas.setMargenY(compas_margin_y);

		compas.setQuintas(quintas);
	}
	
	private void calcularPedals(Compas compas) {
		for (int i=0; i<compas.numPedalStarts(); i++) {
			ElementoGrafico pedal = compas.getPedalStart(i);
			byte location = pedal.getValue(0);
			int posicion = calcularPosicionX(pedal.getPosition());

			Pedal pedalInicio = new Pedal();
			pedalInicio.setImagen(bitmapManager.getPedalStart());
			pedalInicio.setX(compas_margin_x + posicion);
			pedalInicio.setY(obtenerPosicionYDeElementoGrafico(location));
			compas.addPedalInicio(pedalInicio);
		}


		for (int i=0; i<compas.numPedalStops(); i++) {
			ElementoGrafico pedal = compas.getPedalStop(i);
			byte location = pedal.getValue(0);
			int posicion = calcularPosicionX(pedal.getPosition());

			Pedal pedalFin = new Pedal();
			pedalFin.setImagen(bitmapManager.getPedalStop());
			pedalFin.setX(compas_margin_x + posicion);
			pedalFin.setY(obtenerPosicionYDeElementoGrafico(location));
			compas.addPedalFin(pedalFin);
		}
	}
	
	private void calcularPosicionesDeCompas(Compas compas) {
		compas.setXIni(compas_margin_x);
		compas.setYIni(compas_margin_y);
		
		compas_margin_x += config.margenIzquierdoCompases;

		calcularClefs(compas);
		if (compas.hayFifths()) calcularFifths(compas);
		calcularTime(compas);
		calcularWords(compas);
		calcularDynamics(compas);
		calcularPedals(compas);
		calcularWedges(compas);
		
		compas.setXIniNotas(compas_margin_x);
		
		int lastX = calcularPosicionesDeNotas(compas);
		if (compas_margin_x + lastX > compas.getXFin())
			compas.setXFin(compas_margin_x + lastX);
		
		compas.setXFin(compas.getXFin() + config.margenDerechoCompases);
		compas.setYFin(compas_margin_y + 
				config.distanciaLineasPentagrama * 4 + 
				(config.distanciaPentagramas + config.distanciaLineasPentagrama * 4) * 
				(partitura.getStaves() - 1));
		
		compas_margin_x = compas.getXFin();
		
		//  Si hay claves al final de un compás, las notas del siguiente compás
		//  deben regirse por dichas claves. Aquí nos aseguramos de eso
		int[] clavesAlFinalDelCompas = compas.clavesAlFinalDelCompas(partitura.getStaves());
		for (int i=0; i<clavesAlFinalDelCompas.length; i++) {
			if (clavesAlFinalDelCompas[i] > -1) {
				Clave clave = compas.getClave(clavesAlFinalDelCompas[i]);
				claveActual[i] = clave.getByteClave();
			}
		}
		
		if (vista == Vista.VERTICAL) {
			if (compas.getXFin() > config.xFinalPentagramas) {
				moverCompasAlSiguienteRenglon(compas);
				
				ultimoCompas = compasActual - 1;
				reajustarCompases();
				primerCompas = compasActual;
			}
		}
	}
	
	public void calcularPosicionesDeCompases() {
		int numCompases = partitura.getCompases().size();
		
		for (int i=0; i<numCompases; i++) {
			compasActual = i;
			calcularPosicionesDeCompas(partitura.getCompas(i));
		}
	}

	private int calcularPosicionesDeNota(Compas compas, Nota nota) {
		int posicionX = nota.getPosition();
		int posicionY = 0;
		
		if (posicionX != -1) {
			posicionX = calcularPosicionX(posicionX);
			nota.setX(compas_margin_x + posicionX);
			
			//  Si se coloca una clave en el compás, las notas anteriores
			//  a esta clave deben colocarse según la clave vieja, y las
			//  posteriores según la clave nueva
			byte clave = compas.getClavePorPentagrama(nota);
			if (clave > -1) 
				claveActual[nota.getPentagrama() - 1] = clave;
			
			posicionY = calcularCabezaDeNota(nota, posicionX);
			nota.setY(posicionY);
		}

		return posicionX;
	}
	
	private int calcularPosicionesDeNotas(Compas compas) {
		ArrayList<Nota> notas = compas.getNotas();
		int numNotas = notas.size();

		int mayorDistanciaX = 0;
		int distanciaActualX = 0;
		
		for (int i=0; i<numNotas; i++) 
		{
			distanciaActualX = calcularPosicionesDeNota(compas, notas.get(i));

			if (distanciaActualX > mayorDistanciaX) 
				mayorDistanciaX = distanciaActualX;
		}
		
		return mayorDistanciaX;
	}
	
	private int calcularPosicionX(int position) {
		return position * config.unidadDesplazamiento / config.divisions;
	}
	
	private void calcularTime(Compas compas) {
		if (compas.hayTime()) {
			Tempo tempo = new Tempo();
			
			switch (compas.getTime().getValue(1)) {
				case 1:
					inicializarTempo(tempo, compas, 3, 8);
					break;
				case 2:
					inicializarTempo(tempo, compas, 4, 4);
					break;
				case 3:
					inicializarTempo(tempo, compas, 2, 4);
					break;
				case 4:
					inicializarTempo(tempo, compas, 7, 4);
					break;
				case 5:
					inicializarTempo(tempo, compas, 6, 8);
					break;
				case 6:
					inicializarTempo(tempo, compas, 3, 4);
					break;
				case 7:
					inicializarTempo(tempo, compas, 6, 4);
					break;
				default:
					break;
			}
			
			compas.setTempo(tempo);
			tempoActual = tempo;
		}
		else {
			compas.setTempo(clonarTempo(tempoActual));
		}
	}
	
	private void calcularWedges(Compas compas) {
		int numWedges = compas.numWedges();
		int posicionX = 0;
		
		//  Asumimos que los crescendos o diminuendos estarán siempre
		//  contenidos dentro del mismo compás. También estamos
		//  asumiendo que, en la partitura, siempre que se indique el
		//  comienzo de un crescendo o diminuendo, su posición
		//  final vendrá justo después
		
		for (int i=0; i<numWedges; i++) {
			posicionX = calcularPosicionX(compas.getWedge(i).getPosition());
			Wedge wedge = new Wedge(compas.getWedge(i).getValue(1), compas_margin_x + posicionX);
			wedge.setYIni(obtenerPosicionYDeElementoGrafico(compas.getWedge(i).getValue(0)));

			i++;
			
			posicionX = calcularPosicionX(compas.getWedge(i).getPosition());
			wedge.setXFin(compas_margin_x + posicionX);
			
			if (wedge.crescendo())
				compas.addCrescendo(wedge);
			else
				compas.addDiminuendo(wedge);
		}
	}
	
	private void calcularWords(Compas compas) {
		int numWords = compas.numWords();
		
		for (int i=0; i<numWords; i++) {
			Texto texto = new Texto();
			int posicionX = calcularPosicionX(compas.getWordsPosition(i));
			
			texto.setTexto(compas.getWordsString(i));
			texto.setX(compas_margin_x + posicionX);
			texto.setY(obtenerPosicionYDeElementoGrafico(compas.getWordsLocation(i)));
			
			compas.addTexto(texto);
		}
	}

	private Tempo clonarTempo(Tempo tempoViejo) {
		Tempo tempoNuevo = new Tempo();

		tempoNuevo.setNumerador(tempoViejo.getNumerador());
		tempoNuevo.setDenominador(tempoViejo.getDenominador());
		
		//  Puesto que este tempo no se va a dibujar, y
		//  para evitarnos problemas, colocamos sus
		//  posiciones X e Y a -1
		tempoNuevo.setDibujar(false);
		tempoNuevo.setX(-1);
		tempoNuevo.setYNumerador(-1);
		tempoNuevo.setYDenominador(-1);
		
		return tempoNuevo;
	}
	
	private void inicializarTempo(Tempo tempo, Compas compas, int numerador, int denominador) {
		int x_position = calcularPosicionX(compas.getTime().getPosition());
		
		tempo.setDibujar(true);
		tempo.setNumerador(numerador);
		tempo.setDenominador(denominador);
		tempo.setX(compas_margin_x + x_position);
		tempo.setYNumerador(compas_margin_y + config.distanciaLineasPentagrama * 2);
		tempo.setYDenominador(compas_margin_y + config.distanciaLineasPentagrama * 4);
	}
	
	private void moverCompasAlSiguienteRenglon(Compas compas) {
		int distancia_x = compas.getXIni() - config.xInicialPentagramas;
		int distancia_y = (config.distanciaLineasPentagrama * 4 + 
				config.distanciaPentagramas) * partitura.getStaves();

		compas.setXIni(config.xInicialPentagramas);
		compas.setXFin(compas.getXFin() - distancia_x);
		if (compas.getXFin() > config.xFinalPentagramas)
			compas.setXFin(config.xFinalPentagramas);
		compas.setXIniNotas(compas.getXIniNotas() - distancia_x);

		for (int i=0; i<compas.numClaves(); i++) {
			compas.getClave(i).setX(compas.getClave(i).getX() - distancia_x);
			compas.getClave(i).setY(compas.getClave(i).getY() + distancia_y);
		}

		for (int i=0; i<compas.numIntensidades(); i++) {
			compas.getIntensidad(i).setX(compas.getIntensidad(i).getX() - distancia_x);
			compas.getIntensidad(i).setY(compas.getIntensidad(i).getY() + distancia_y);
		}
		
		for (int i=0; i<compas.numPedalesInicio(); i++) {
			compas.getPedalInicio(i).setX(compas.getPedalInicio(i).getX() - distancia_x);
			compas.getPedalInicio(i).setY(compas.getPedalInicio(i).getY() + distancia_y);
		}

		for (int i=0; i<compas.numPedalesFin(); i++) {
			compas.getPedalFin(i).setX(compas.getPedalFin(i).getX() - distancia_x);
			compas.getPedalFin(i).setY(compas.getPedalFin(i).getY() + distancia_y);
		}
		
		if (compas.hayTempo()) {
			compas.getTempo().setX(compas.getTempo().getX() - distancia_x);
			compas.getTempo().setYNumerador(compas.getTempo().getYNumerador() + distancia_y);
			compas.getTempo().setYDenominador(compas.getTempo().getYDenominador() + distancia_y);
		}
		
		for (int i=0; i<compas.numTextos(); i++) {
			compas.getTexto(i).setX(compas.getTexto(i).getX() - distancia_x);
			compas.getTexto(i).setY(compas.getTexto(i).getY() + distancia_y);
		}
		
		for (int i=0; i<compas.numCrescendos(); i++) {
			compas.getCrescendo(i).setXIni(compas.getCrescendo(i).getXIni() - distancia_x);
			compas.getCrescendo(i).setXFin(compas.getCrescendo(i).getXFin() - distancia_x);
			compas.getCrescendo(i).setYIni(compas.getCrescendo(i).getYIni() + distancia_y);
		}
		
		for (int i=0; i<compas.numDiminuendos(); i++) {
			compas.getDiminuendo(i).setXIni(compas.getDiminuendo(i).getXIni() - distancia_x);
			compas.getDiminuendo(i).setXFin(compas.getDiminuendo(i).getXFin() - distancia_x);
			compas.getDiminuendo(i).setYIni(compas.getDiminuendo(i).getYIni() + distancia_y);
		}
		
		compas_margin_x = compas.getXFin();
		compas_margin_y = compas_margin_y + distancia_y;

		compas.setYIni(compas_margin_y);
		compas.setYFin(compas_margin_y + 
				config.distanciaLineasPentagrama * 4 + 
				(config.distanciaPentagramas + 
						config.distanciaLineasPentagrama * 4) * (partitura.getStaves() - 1));
		
		int numNotas = compas.numNotas();
		for (int i=0; i<numNotas; i++) {
			compas.getNota(i).setX(compas.getNota(i).getX() - distancia_x);
			compas.getNota(i).setY(compas.getNota(i).getY() + distancia_y);
		}
	}
	
	public void numerarCompases() {
		final int numCompases = partitura.getCompases().size();
		int numeroCompas = partitura.getFirstNumber();
		
		for (int i=0; i<numCompases; i++) 
			partitura.getCompas(i).setNumeroCompas(numeroCompas++);
	}
	
	public Bitmap obtenerImagenDeCabezaDeNota(Nota nota) {
		if (nota.getStep() == 0) {

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
			if (nota.getFiguracion() > 9) return bitmapManager.getWhiteHead();
			else
				if (nota.notaDeGracia()) return bitmapManager.getBlackHeadLittle();
				else return bitmapManager.getBlackHead();
		}
	}
	
	private Bitmap obtenerImagenDeClave(byte clave) {
		switch (clave) {
			case 1:
				return bitmapManager.getTrebleClef();
			case 2:
				return bitmapManager.getBassClef();
			default: 
				return null;
		}
	}
	
	public Bitmap obtenerImagenDeCorcheteDeNota(Nota nota) {
		if (nota.notaDeGracia()) {
			if (nota.haciaArriba()) return bitmapManager.getHeadLittle();
			else return bitmapManager.getHeadInvLittle();
		}
		else {
			if (nota.haciaArriba()) return bitmapManager.getHead();
			else return bitmapManager.getHeadInv();
		}
	}
	
	private Bitmap obtenerImagenDeIntensidad(byte intensidad) {
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
	
	private int obtenerPosicionYDeClave(byte clave) {
		switch (clave) {
			case 1: return - config.yClaveSolSegunda;
			default: return 0;
		}
	}
	
	private int obtenerPosicionYDeElementoGrafico(int location) {
		switch (location) {
			case 1:
				return compas_margin_y - config.distanciaLineasPentagrama * 2;
			case 2:
				return compas_margin_y + config.distanciaLineasPentagrama * 6;
			case 3:
				return compas_margin_y + config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas - config.distanciaLineasPentagrama * 2;
			case 4:
				return compas_margin_y + config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas + config.distanciaLineasPentagrama * 6;
			case 5:
				return compas_margin_y + config.distanciaLineasPentagrama * 4 +
						config.distanciaPentagramas / 2;
			default:
				return 0;
		}
	}
	
	private int obtenerPosicionYDeNota(Nota nota, byte clave, byte instrumento){
		int coo_y = 0;
		int margenY = compas_margin_y + 
				(config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas) * (nota.getPentagrama() - 1);
		
		byte octava = nota.getOctava();
		if (octava > 10) octava -= 12;
		
		if (nota.getStep() > 0) {

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
											coo_y = margenY + config.distanciaLineasPentagrama * 5 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.distanciaLineasPentagrama * 5;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama * 8;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagrama * 7 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.distanciaLineasPentagrama * 7;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.distanciaLineasPentagrama * 6 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.distanciaLineasPentagrama * 6;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 4: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY + config.distanciaLineasPentagrama * 2;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.distanciaLineasPentagrama + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama * 4 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagrama * 4;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.distanciaLineasPentagrama * 3 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.distanciaLineasPentagrama * 3;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.distanciaLineasPentagrama * 2 + 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 5: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY - config.distanciaLineasPentagrama - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.distanciaLineasPentagrama * 2;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.distanciaLineasPentagrama;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 6: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY - config.distanciaLineasPentagrama * 5;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.distanciaLineasPentagrama * 5 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY - config.distanciaLineasPentagrama * 2 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY - config.distanciaLineasPentagrama * 3;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY - config.distanciaLineasPentagrama * 3 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.distanciaLineasPentagrama * 4;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.distanciaLineasPentagrama * 4 - 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											coo_y = 0;
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
											coo_y = margenY + config.distanciaLineasPentagrama * 9;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.distanciaLineasPentagrama * 8 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama * 11 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagrama * 11;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.distanciaLineasPentagrama * 10 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.distanciaLineasPentagrama * 10;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.distanciaLineasPentagrama * 9 + 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 3: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY + config.distanciaLineasPentagrama * 5 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.distanciaLineasPentagrama * 5;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama * 8;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagrama * 7 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.distanciaLineasPentagrama * 7;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.distanciaLineasPentagrama * 6 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.distanciaLineasPentagrama * 6;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 4: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY + config.distanciaLineasPentagrama * 2;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.distanciaLineasPentagrama + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama * 4 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagrama * 4;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.distanciaLineasPentagrama * 3 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.distanciaLineasPentagrama * 3;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.distanciaLineasPentagrama * 2 + 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 5: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY - config.distanciaLineasPentagrama - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.distanciaLineasPentagrama * 2;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.distanciaLineasPentagrama;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 6: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY - config.distanciaLineasPentagrama * 5;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.distanciaLineasPentagrama * 5 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY - config.distanciaLineasPentagrama * 2 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY - config.distanciaLineasPentagrama * 3;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY - config.distanciaLineasPentagrama * 3 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.distanciaLineasPentagrama * 4;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.distanciaLineasPentagrama * 4 - 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											coo_y = 0;
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
											coo_y = margenY + config.distanciaLineasPentagrama * 6 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.distanciaLineasPentagrama * 6;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama * 9;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagrama * 8 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.distanciaLineasPentagrama * 8;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.distanciaLineasPentagrama * 7 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.distanciaLineasPentagrama * 7;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 2: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY + config.distanciaLineasPentagrama * 3;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.distanciaLineasPentagrama * 2 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama * 5 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagrama * 5;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.distanciaLineasPentagrama * 4 + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.distanciaLineasPentagrama * 4;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.distanciaLineasPentagrama * 3 + 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 3: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY - config.distanciaLineasPentagramaMitad;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.distanciaLineasPentagrama;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.distanciaLineasPentagrama * 2;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.distanciaLineasPentagrama + 
												config.distanciaLineasPentagramaMitad;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.distanciaLineasPentagrama;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.distanciaLineasPentagrama - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY;
											break;

										default:
											coo_y = 0;
											break;
									}
									break;
								}

								case 4: {
									
									switch(nota.getStep()){
										case 1:
										case 8:
										case 15:
											coo_y = margenY - config.distanciaLineasPentagrama * 4;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.distanciaLineasPentagrama * 4 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY - config.distanciaLineasPentagrama - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY - config.distanciaLineasPentagrama * 2;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY - config.distanciaLineasPentagrama * 2 - 
												config.distanciaLineasPentagramaMitad;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.distanciaLineasPentagrama * 3;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.distanciaLineasPentagrama * 3 - 
												config.distanciaLineasPentagramaMitad;
											break;

										default:
											coo_y = 0;
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
					coo_y = margenY;
					break;
					
				case 10:
					coo_y = margenY + config.distanciaLineasPentagrama + 
						config.distanciaLineasPentagramaMitad + config.ySilencioBlanca;
					break;
					
				case 11:
					coo_y = margenY + config.distanciaLineasPentagrama;
					break;

				default: break;
			}
		}

		return coo_y;
	}
	
	private void reajustarCompases() {
		int espacioADistribuir = config.xFinalPentagramas - partitura.getCompas(ultimoCompas).getXFin();

    	int numCompases = (ultimoCompas - primerCompas) + 1;
        int anchoParaCadaCompas = espacioADistribuir / numCompases;
        int posicionX = partitura.getCompas(primerCompas).getXFin() + anchoParaCadaCompas;
        
        //  Primer paso: reajustar ancho y posición de los compases
        for (int i=primerCompas; i<=ultimoCompas; i++) {
        	Compas compas = partitura.getCompas(i);
        	
        	if (i == primerCompas)
        		compas.setXFin(posicionX);
        	else {
	        	int distanciaXIni = compas.getXIniNotas() - compas.getXIni();
	        	
	        	compas.setXIni(posicionX);
	        	compas.setXIniNotas(posicionX + distanciaXIni);
	            
	        	posicionX = compas.getXFin() + anchoParaCadaCompas;
	            if (i == ultimoCompas) posicionX = config.xFinalPentagramas;
	            compas.setXFin(posicionX);
	            
	            int numNotas = compas.numNotas();
	            for (int j=0; j<numNotas; j++)
	            	compas.getNota(j).setX(compas.getNota(j).getX() + anchoParaCadaCompas);

    			for (int j=0; j<compas.numClaves(); j++)
    				if (compas.getClave(j) != null)
    					compas.getClave(j).setX(compas.getClave(j).getX() + anchoParaCadaCompas);
	            
    			for (int j=0; j<compas.numIntensidades(); j++)
            		compas.getIntensidad(j).setX(compas.getIntensidad(j).getX() + anchoParaCadaCompas);
	            
            	for (int j=0; j<compas.numPedalesInicio(); j++)
            		compas.getPedalInicio(j).setX(compas.getPedalInicio(j).getX() + anchoParaCadaCompas);
	            
            	for (int j=0; j<compas.numPedalesFin(); j++)
            		compas.getPedalFin(j).setX(compas.getPedalFin(j).getX() + anchoParaCadaCompas);
	            
	            if (compas.hayTempo())
	            	compas.getTempo().setX(compas.getTempo().getX() + anchoParaCadaCompas);
	            
	            for (int j=0; j<compas.numTextos(); j++)
	            	compas.getTexto(j).setX(compas.getTexto(j).getX() + anchoParaCadaCompas);
	            
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
        	Compas compas = partitura.getCompas(i);
        	ArrayList<Integer> xsDeElementos = compas.saberXsDeElementos();
        	
        	int lastX = xsDeElementos.get(xsDeElementos.size() - 1);
        	int anchoADistribuir = compas.getXFin() - config.margenDerechoCompases - lastX;
        	
        	//  El primer elemento no lo vamos a mover, de ahí el -1
        	int numElementos = xsDeElementos.size() - 1;
        	int anchoPorElemento = 0;
        	if (numElementos > 0)
        		anchoPorElemento = anchoADistribuir / numElementos;
        	
        	reajustarNotasYClaves(compas, xsDeElementos, anchoPorElemento);
        	reajustarFigurasGraficas(compas, anchoPorElemento);
        }
	}

	private void reajustarFigurasGraficas(Compas compas, int anchoPorNota) {
		
		int multiplicador = 0;
		int xPrimeraNota = compas.saberXPrimeraNota();
		
		ArrayList<Integer> xsDelCompas = compas.saberXsDelCompas();
		
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
	
	private void reajustarNotasYClaves(Compas compas, ArrayList<Integer> xsDeElementos, int anchoPorNota) {
		
		//  A cada elemento se le suma una distancia cada vez
    	//  mayor, ya que de lo contrario sólo estaríamos
    	//  desplazándolos todos pero manteniéndolos a la misma
    	//  distancia entre sí mismos que antes
    	ArrayList<Nota> notas = compas.getNotas();
    	int numNotas = notas.size();
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
}
