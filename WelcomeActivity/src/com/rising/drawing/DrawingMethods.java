package com.rising.drawing;

import java.util.ArrayList;
import java.util.Collections;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class DrawingMethods {
	
	private boolean isValid = false;

	private ArrayList<OrdenDibujo> ordenesDibujo = new ArrayList<OrdenDibujo>();
	
	//  Partitura y sus datos "físicos" (límites, densidad de pantalla, etc.)
	private Partitura partitura;
	private Config config;
	private Vista vista;
	
	//  Variables para la gestión y el tratamiento dinámico de los múltiples compases
	private int compas_margin_x = 0;
	private int compas_margin_y = 0;
	private byte[] claveActual = {0, 0};
	private Tempo tempoActual = null;
	
	private int compasActual = 0;
	private int notaActual = 0;
	private int primerCompas = 0;
	private int ultimoCompas = 0;

	//  Variables para la gestión de las múltiples notas
	private ArrayList<IndiceNota> beams = new ArrayList<IndiceNota>();
	private ArrayList<IndiceNota> ligaduras = new ArrayList<IndiceNota>();
	private boolean buscandoLigaduraExpresion = false;
	private int ligaduraExpresionYArriba = Integer.MAX_VALUE;
	private int ligaduraExpresionYAbajo = 0;
	private int octavarium = 0;
	private int x_ini_octavarium = 0;
	private int y_ini_octavarium = 0;
	private int x_fin_octavarium = 0;
	private int y_fin_octavarium = 0;
	private int x_ini_slide = 0;
	private int x_ini_tresillo = 0;
	private int y_anterior = 0;
	private int y_ini_slide = 0;

	//  Bitmaps
	private Bitmap accent = null;
	private Bitmap arpegioImage = null;
	private Bitmap bassclef = null;
	private Bitmap bendrelease = null;
	private Bitmap blackheadlittle = null;
	private Bitmap blackhead = null;
	private Bitmap eighthrest = null;
	private Bitmap fermata = null;
	private Bitmap fermata_inverted = null;
	private Bitmap flat = null;
	private Bitmap forte = null;
	private Bitmap fortissimo = null;
	private Bitmap forzando = null;
	private Bitmap forzandop = null;
	private Bitmap head = null;
	private Bitmap headlittle = null;
	private Bitmap headinv = null;
	private Bitmap headinvlittle = null;
	private Bitmap marcato = null;
	private Bitmap mezzoforte = null;
	private Bitmap natural = null;
	private Bitmap noterest16 = null;
	private Bitmap noterest32 = null;
	private Bitmap noterest64 = null;
	private Bitmap octavariumImage = null;
	private Bitmap pedalStart = null;
	private Bitmap pedalStop = null;
	private Bitmap piano = null;
	private Bitmap pianissimo = null;
	private Bitmap pianississimo = null;
	private Bitmap quarterrest = null;
	private Bitmap rectangle = null;
	private Bitmap sharp = null;
	private Bitmap trebleclef = null;
	private Bitmap trill = null;
	private Bitmap vibrato = null;
	private Bitmap whitehead = null;

	
	public DrawingMethods(Partitura partitura, Config config, Resources resources, Vista vista) {
		if (config.supported()) {
			
			this.partitura = partitura;
			this.config = config;
			this.vista = vista;
			
			compas_margin_x = config.xInicialPentagramas;
			compas_margin_y = config.margenSuperior;

			accent = BitmapFactory.decodeResource(resources, R.drawable.accent);
			arpegioImage = BitmapFactory.decodeResource(resources, R.drawable.arpegio);
			bassclef = BitmapFactory.decodeResource(resources, R.drawable.bassclef);
			bendrelease = BitmapFactory.decodeResource(resources, R.drawable.bendrelease);
			blackheadlittle = BitmapFactory.decodeResource(resources, R.drawable.blackheadlittle);
			blackhead = BitmapFactory.decodeResource(resources, R.drawable.blackhead);
			eighthrest = BitmapFactory.decodeResource(resources, R.drawable.eighthrest);
			fermata = BitmapFactory.decodeResource(resources, R.drawable.fermata);
			fermata_inverted = BitmapFactory.decodeResource(resources, R.drawable.fermata_inverted);
			flat = BitmapFactory.decodeResource(resources, R.drawable.flat);
			forte = BitmapFactory.decodeResource(resources, R.drawable.forte);
			fortissimo = BitmapFactory.decodeResource(resources, R.drawable.fortissimo);
			forzando = BitmapFactory.decodeResource(resources, R.drawable.forzando);
			forzandop = BitmapFactory.decodeResource(resources, R.drawable.forzandop);
			head = BitmapFactory.decodeResource(resources, R.drawable.head);
			headinv = BitmapFactory.decodeResource(resources, R.drawable.headinv);
			headinvlittle = BitmapFactory.decodeResource(resources, R.drawable.headinvlittle);
			headlittle = BitmapFactory.decodeResource(resources, R.drawable.headlittle);
			marcato = BitmapFactory.decodeResource(resources, R.drawable.marcato);
			mezzoforte = BitmapFactory.decodeResource(resources, R.drawable.mezzoforte);
			natural = BitmapFactory.decodeResource(resources, R.drawable.natural);
			noterest16 = BitmapFactory.decodeResource(resources, R.drawable.noterest16);
			noterest32 = BitmapFactory.decodeResource(resources, R.drawable.noterest32);
			noterest64 = BitmapFactory.decodeResource(resources, R.drawable.noterest64);
			octavariumImage = BitmapFactory.decodeResource(resources, R.drawable.octavarium);
			pedalStart = BitmapFactory.decodeResource(resources, R.drawable.pedalstart);
			pedalStop = BitmapFactory.decodeResource(resources, R.drawable.pedalstop);
			piano = BitmapFactory.decodeResource(resources, R.drawable.piano);
			pianissimo = BitmapFactory.decodeResource(resources, R.drawable.pianissimo);
			pianississimo = BitmapFactory.decodeResource(resources, R.drawable.pianississimo);
			quarterrest = BitmapFactory.decodeResource(resources, R.drawable.quarterrest);
			rectangle = BitmapFactory.decodeResource(resources, R.drawable.rectangle);
			sharp = BitmapFactory.decodeResource(resources, R.drawable.sharp);
			trebleclef = BitmapFactory.decodeResource(resources, R.drawable.trebleclef);
			trill = BitmapFactory.decodeResource(resources, R.drawable.trill);
			vibrato = BitmapFactory.decodeResource(resources, R.drawable.vibrato);
			whitehead = BitmapFactory.decodeResource(resources, R.drawable.whitehead);
			
			isValid = true;
		}
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
			pedalInicio.setImagen(pedalStart);
			pedalInicio.setX(compas_margin_x + posicion);
			pedalInicio.setY(obtenerPosicionYDeElementoGrafico(location));
			compas.addPedalInicio(pedalInicio);
		}


		for (int i=0; i<compas.numPedalStops(); i++) {
			ElementoGrafico pedal = compas.getPedalStop(i);
			byte location = pedal.getValue(0);
			int posicion = calcularPosicionX(pedal.getPosition());

			Pedal pedalFin = new Pedal();
			pedalFin.setImagen(pedalStop);
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
	
	private void calcularPosicionesDeCompases() {
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
		
		for (int i=0; i<numNotas; i++) {
			notaActual = i;
			
			distanciaActualX = calcularPosicionesDeNota(compas, notas.get(i));

			if (distanciaActualX > mayorDistanciaX) 
				mayorDistanciaX = distanciaActualX;
		}
		
		return mayorDistanciaX;
	}
	
	private int calcularPosicionX(int position) {
		return position * config.unidadDesplazamiento / partitura.getDivisions();
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
	
	public ArrayList<OrdenDibujo> crearOrdenesDeDibujo() {

		numerarCompases();

		ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraObra, 
				true, partitura.getWork(), config.width / 2, 
				compas_margin_y + config.margenObra));
		ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraAutor, 
				true, partitura.getCreator(), config.width / 2, 
				compas_margin_y + config.margenAutor));
		
		compas_margin_y += config.margenInferiorAutor;
		
		calcularPosicionesDeCompases();
		dibujarCompases();
		return ordenesDibujo;
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
	
	public boolean isValid() {
		return isValid;
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
	
	private void numerarCompases() {
		final int numCompases = partitura.getCompases().size();
		int numeroCompas = partitura.getFirstNumber();
		
		for (int i=0; i<numCompases; i++) 
			partitura.getCompas(i).setNumeroCompas(numeroCompas++);
	}
	
	private Bitmap obtenerImagenDeCabezaDeNota(Nota nota) {
		if (nota.getStep() == 0) {

			switch (nota.getFiguracion()) {
				case 5:
					return noterest64;
				case 6:
					return noterest32;
				case 7:
					return noterest16;
				case 8:
					return eighthrest;
				case 9:
					return quarterrest;
				case 10:
				case 11:
					return rectangle;
				default:
					return null;
			}
		}
		
		else {
			if (nota.getFiguracion() > 9) return whitehead;
			else
				if (nota.notaDeGracia()) return blackheadlittle;
				else return blackhead;
		}
	}
	
	private Bitmap obtenerImagenDeClave(byte clave) {
		switch (clave) {
			case 1:
				return trebleclef;
			case 2:
				return bassclef;
			default: 
				return null;
		}
	}
	
	private Bitmap obtenerImagenDeCorcheteDeNota(Nota nota) {
		if (nota.notaDeGracia()) {
			if (nota.haciaArriba()) return headlittle;
			else return headinvlittle;
		}
		else {
			if (nota.haciaArriba()) return head;
			else return headinv;
		}
	}
	
	private Bitmap obtenerImagenDeIntensidad(byte intensidad) {
		switch (intensidad) {
			case 1:
				return forte;
			case 2:
				return mezzoforte;
			case 3:
				return piano;
			case 4:
				return pianissimo;
			case 5:
				return forzandop;
			case 6:
				return pianississimo;
			case 7:
				return fortissimo;
			case 8:
				return forzando;
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
	
	
	/*
	 * 
	 * FUNCIONES DE DIBUJO
	 * 
	 */

	//  NOTA: Esta implementación está ignorando las plicas dobles
	private int colocarBeamsALaMismaAltura(boolean haciaArriba, int beamId) {
		int numBeams = beams.size();
		int y_beams = haciaArriba ? Integer.MAX_VALUE : 0;
		
		//  Nota que no es de gracia
		boolean notaNormal = false;

		for (int i=0; i<numBeams; i++) {
			if (beamId == beams.get(i).getBeamId()) {
				
				int indCompas = beams.get(i).getCompas();
				int indNota = beams.get(i).getNota();
				Nota nota = partitura.getCompas(indCompas).getNota(indNota);
				
				//  Previene que puedan haber notas normales y de gracia
				//  mezcladas en un mismo beam, en cuyo caso la altura
				//  la impondría la nota normal por ocupar más espacio
				if (!notaNormal)
					if (!nota.notaDeGracia()) 
						notaNormal = true;
				
				if (haciaArriba) {
					if (y_beams > nota.getY())
						y_beams = nota.getY();
				}
				else {
					if (y_beams < nota.getY())
						y_beams = nota.getY();
				}
			}
		}
		
		int longitudPlica = notaNormal ? 
				config.longitudPlica : config.longitudPlicaNotaGracia;
		
		if (haciaArriba) y_beams -= longitudPlica;
		else y_beams += longitudPlica;
		
		return y_beams;
	}
	
	private void dibujarAlteraciones(Nota nota, Bitmap alteracion, int offset) {
		int xAccidental = nota.notaDeGracia() ? 
				config.xAccidentalNotaGracia : config.xAccidental;
		int x = nota.getX() - xAccidental - offset;
		if (nota.desplazadaALaIzquierda()) x -= config.anchoCabezaNota - offset;
		
		ordenesDibujo.add( new OrdenDibujo(alteracion, x, nota.getY() - config.yAccidental));
	}
	
	//  Esta implementación por ahora sólo considera el barline de fin de partitura
	//  Si en el futuro se añadieran más barlines, habría que usar un switch en el bucle
	private void dibujarBarlines(Compas compas) {
		ArrayList<ElementoGrafico> barlines = compas.getBarlines();
		int numBarlines = barlines.size();

		for (int i=0; i<numBarlines; i++) {
			if (barlines.get(i).getValue(1) == 2) {
				ordenesDibujo.add( new OrdenDibujo(
					4, compas.getXFin(), compas.getYIni(), compas.getXFin(), compas.getYFin()));
				ordenesDibujo.add( new OrdenDibujo(
						2, compas.getXFin() - config.margenBarlines, 
						compas.getYIni(), compas.getXFin() - config.margenBarlines, compas.getYFin()));
			}
		}
	}
	
	//  Esta implementación está ignorando las plicas dobles
	private void dibujarBeams(int y_beams, boolean haciaArriba, int beamId) {
		int numBeams = beams.size();
		
		int indCompasAnt = 0;
		int indNotaAnt = 0;
		int distancia_beams = 0;
		int ancho_beams = 0;
		
		Collections.sort(beams);
		int i = -1;
		for (int j=0; j<numBeams; j++) {
			if (beams.get(j).getBeamId() == beamId) {
				i = j;
				break;
			}
		}
		int primerBeam = i;

		while (beams.get(i).getBeamId() == beamId) {
			
			indCompasAnt = beams.get(i).getCompas();
			indNotaAnt = beams.get(i).getNota();
			
			distancia_beams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ? 
					config.distanciaEntreBeamsNotaGracia : config.distanciaEntreBeams;
			if (!haciaArriba) distancia_beams *= -1;
			
			ancho_beams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ?
					config.anchoBeamsNotaGracia : config.anchoBeams;

			if ( (i == numBeams - 1) || (beams.get(i + 1).getBeamId() != beamId) ) {
				
				int x_last_beam = 0;
				Nota nota = partitura.getCompas(indCompasAnt).getNota(indNotaAnt);
				int offset = nota.haciaArriba() ? config.anchoCabezaNota : 0;
				
				//  Gestión de hooks en la última nota
				switch (nota.getBeam()) {
						
					case 4:
						x_last_beam = nota.getX();
						
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_last_beam + offset, y_beams + distancia_beams * 2,
								x_last_beam + offset - config.anchoHooks, y_beams + distancia_beams * 2));
						break;
						
					case 6:
						x_last_beam = nota.getX();
						
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_last_beam + offset, y_beams + distancia_beams,
								x_last_beam + offset - config.anchoHooks, y_beams + distancia_beams));
						break;
						
					default:
						break;
				}
			}
			else {
				int indCompasSig = beams.get(i + 1).getCompas();
				int indNotaSig = beams.get(i + 1).getNota();
				
				int x_ant_beams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).getX();
				int x_sig_beams = partitura.getCompas(indCompasSig).getNota(indNotaSig).getX();
				
				if (haciaArriba) {
					int anchoCabezaNota = 
							partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ? 
							config.anchoCabezaNotaGracia : config.anchoCabezaNota;
					x_ant_beams += anchoCabezaNota;
					x_sig_beams += anchoCabezaNota;
				}
				
				switch (partitura.getCompas(indCompasAnt).getNota(indNotaAnt).getBeam()) {
					
					case 2:
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_ant_beams, y_beams, x_sig_beams, y_beams));
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_ant_beams, y_beams + distancia_beams, 
								x_sig_beams, y_beams + distancia_beams));
						ordenesDibujo.add( new OrdenDibujo(
								1, x_ant_beams, y_beams, x_ant_beams, y_beams + distancia_beams));
						ordenesDibujo.add( new OrdenDibujo(
								1, x_sig_beams, y_beams, x_sig_beams, y_beams + distancia_beams));
						break;
	
					case 3:
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_ant_beams, y_beams, x_sig_beams, y_beams));
						break;
	
					case 5:
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_ant_beams, y_beams, x_sig_beams, y_beams));
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_ant_beams, y_beams + distancia_beams, 
								x_sig_beams, y_beams + distancia_beams));
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_ant_beams, y_beams + distancia_beams * 2, 
								x_sig_beams, y_beams + distancia_beams * 2));
						ordenesDibujo.add( new OrdenDibujo(
								1, x_ant_beams, y_beams, x_ant_beams, y_beams + distancia_beams * 2));
						ordenesDibujo.add( new OrdenDibujo(
								1, x_sig_beams, y_beams, x_sig_beams, y_beams + distancia_beams * 2));
						break;
	
					default: 
						break;
				}
			}
			
			dibujarPlicaDeNota(partitura.getCompas(indCompasAnt).getNota(indNotaAnt), y_beams);
			if (++i == numBeams) break;
		}
		
		for (int j = primerBeam; j<i; j++)
			beams.remove(primerBeam);
	}

	private void dibujarCabezaDeNota(Nota nota) {
		int desplazamiento = 0;
		if (nota.desplazadaALaIzquierda()) desplazamiento -= config.anchoCabezaNota;
		if (nota.desplazadaALaDerecha()) desplazamiento += config.anchoCabezaNota;
		
		//  La y de la ligadura de expresión debe colocarse tan arriba como
		//  obligue a ello la nota más alta en medio de las notas ligadas
		if (buscandoLigaduraExpresion) {
			if (ligaduraExpresionYArriba > nota.getY())
				ligaduraExpresionYArriba = nota.getY();
			if (ligaduraExpresionYAbajo < nota.getY())
				ligaduraExpresionYAbajo = nota.getY();
		}
			
		if (!nota.acorde()) y_anterior = nota.getY();

		ordenesDibujo.add( new OrdenDibujo(obtenerImagenDeCabezaDeNota(nota), 
				nota.getX() + desplazamiento, nota.getY()));
	}
	
	private void dibujarClaves(Compas compas) {
		Clave clave;
		
		for (int i=0; i<compas.numClaves(); i++) {
			clave = compas.getClave(i);

			ordenesDibujo.add( new OrdenDibujo(clave.getImagenClave(), clave.getX(), clave.getY()));
		}
	}
	
	private void dibujarCompases() {
		ArrayList<Compas> compases = partitura.getCompases();
		int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++) {
			compasActual = i;
			
			//  Este compás da inicio al pentagrama, por tanto dibujamos su número
			if (compases.get(i).getXIni() == config.xInicialPentagramas)
				dibujarNumeroDeCompas(compases.get(i));
			
			dibujarLineasDePentagramaDeCompas(compases.get(i));
			dibujarClaves(compases.get(i));
			
			if (compases.get(i).hayQuintas()) dibujarQuintas(compases.get(i));
			dibujarIntensidades(compases.get(i));
			dibujarPedales(compases.get(i));
			if (compases.get(i).hayTempo()) dibujarTempo(compases.get(i));
			dibujarTextos(compases.get(i));
			dibujarCrescendosYDiminuendos(compases.get(i));
			
			dibujarNotasDeCompas(compases.get(i));
			
			dibujarBarlines(compases.get(i));
		}
	}

	private void dibujarCorcheteDeNota(Nota nota) {
		
		//  En este caso no significa que la nota no pueda formar parte de un acorde. 
		//  Significa que si forma parte de un acorde, su corchete ya fue dibujado, 
		//  por lo que no hay que dibujarlo de nuevo
		if (!nota.acorde() && !nota.silencio()) {
			int anchoCabezaNota = nota.notaDeGracia() ? 
					config.anchoCabezaNotaGracia : config.anchoCabezaNota;
			int longitudPlica = nota.notaDeGracia() ? 
					config.longitudPlicaNotaGracia : config.longitudPlica;
			int anchoCorchete = nota.notaDeGracia() ? 
					config.largoImagenCorcheteNotaGracia : config.largoImagenCorchete;
			int distanciaCorchete = nota.haciaArriba() ?
					config.distanciaCorchetes : - config.distanciaCorchetes;

			int x;
			int y;
			if (nota.haciaArriba()) {
				x = nota.getX() + anchoCabezaNota;
				y = nota.getY() - longitudPlica;
			}
			else {
				x = nota.getX();
				y = nota.getY() + longitudPlica - anchoCorchete;
			}		

			switch (nota.getFiguracion()) {
				case 8:
					dibujarCorcheteDeNotaCrearOrden(nota, x, y, 0);
					break;
					
				case 7:				
					dibujarCorcheteDeNotaCrearOrden(nota, x, y, 0);
					dibujarCorcheteDeNotaCrearOrden(nota, x, y, distanciaCorchete);
					break;
					
				case 6:
					break;
				case 5:
					break;
				default:
					break;
			}
			
			if (nota.tieneSlash()) dibujarSlash(x, y);
		}
	}
	
	private void dibujarCorcheteDeNotaCrearOrden(Nota nota, int x, int y, int distanciaCorchete) {
		ordenesDibujo.add( new OrdenDibujo(
				obtenerImagenDeCorcheteDeNota(nota), x, y + distanciaCorchete));
	}
	
	private void dibujarFiguraGrafica(Nota nota, byte figura, int y_beams, int Xsillo) {
		
		int x = 0;

		switch (figura) {
			case 3:
				x_ini_tresillo = nota.getX();
				break;

			case 4:
				int margenTresillo = nota.haciaArriba() ? 
						- config.yTresilloArriba : config.yTresilloAbajo;
				int x_tresillo = (nota.getX() + x_ini_tresillo) / 2;
				if (nota.haciaArriba()) x_tresillo += config.xTresillo;
				
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTresillo, 
						false, Xsillo + "", x_tresillo, y_beams + margenTresillo));
				break;

			case 6:
				x_ini_slide = nota.getX();
				y_ini_slide = nota.getY();
				break;
				
			case 7:			
				dibujarSlide(nota, x_ini_slide, y_ini_slide);
				break;
				
			case 8:
				if (nota.haciaArriba()) 
					ordenesDibujo.add( new OrdenDibujo(config.radioStaccatos, 
							nota.getX() + config.xStaccato, nota.getY() + config.yStaccatoArriba));
				else 
					ordenesDibujo.add( new OrdenDibujo(config.radioStaccatos, 
							nota.getX() + config.xStaccato, nota.getY() - config.yStaccatoAbajo));
				break;

			case 9:
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTapping, 
						false, "T", nota.getX(), nota.getY() - config.yTapping));
				break;

			case 10:
				ligaduras.add(new IndiceNota(compasActual, notaActual, 
						nota.getLigaduraUnion(), (byte) 0));
				break;
				
			case 11:
				int indLigaduraUnion = encontrarIndiceLigadura(nota.getLigaduraUnion());
				dibujarLigadura(indLigaduraUnion, nota, true);
				break;
			
			case 12:
				dibujarAlteraciones(nota, sharp, Xsillo);
				break;

			case 13:
				dibujarAlteraciones(nota, flat, Xsillo);
				break;

			case 14:
				dibujarAlteraciones(nota, natural, Xsillo);
				break;

			case 15:
				ordenesDibujo.add( new OrdenDibujo(config.radioPuntillos, 
						nota.getX() + config.xPuntillo, 
						nota.getY() + config.mitadCabezaVertical));
				break;

			case 16:
				ordenesDibujo.add( new OrdenDibujo(config.radioPuntillos, 
						nota.getX() + config.xPuntillo, 
						nota.getY() + config.yPuntilloArriba));
				break;

			case 17:
				ordenesDibujo.add( new OrdenDibujo(config.radioPuntillos, 
						nota.getX() + config.xPuntillo, 
						nota.getY() + config.yPuntilloAbajo));
				break;

			case 22:
				ordenesDibujo.add( new OrdenDibujo(bendrelease, nota.getX(), nota.getY() + config.yBend));
				break;
				
			case 26:
				ordenesDibujo.add( new OrdenDibujo(vibrato, nota.getX(), nota.getY() + config.yBend));
				break;
				
			case 27:
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraPalmMute, 
						false, "P.M.", nota.getX(), nota.getY() - config.yPalmMute));
				break;
				
			case 28:
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraPalmMute, 
						false, "P.M.", nota.getX(), nota.getY() + config.yPalmMute));
				break;
			
			case 29:
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTapping, 
						false, "T", nota.getX(), nota.getY() + config.yTapping));
				
			case 30:
				if (nota.haciaArriba()) 
					x = nota.getX() - config.offsetAccent;
				else
					x = nota.getX();
				
				ordenesDibujo.add( new OrdenDibujo(accent, x, nota.getY() - config.yAccentUp));
				break;
				
			case 31:
				ordenesDibujo.add( new OrdenDibujo(accent, nota.getX(), 
						nota.getY() + config.longitudPlica + config.yAccentUp));
				break;
				
			case 32:
				ligaduras.add(new IndiceNota(compasActual, notaActual, 
						nota.getLigaduraExpresion(), (byte) 0));
				buscandoLigaduraExpresion = true;
				break;
				
			case 33:
				int indLigaduraExpresion = encontrarIndiceLigadura(nota.getLigaduraExpresion());
				dibujarLigadura(indLigaduraExpresion, nota, false);
				
				ligaduraExpresionYArriba = Integer.MAX_VALUE;
				ligaduraExpresionYAbajo = 0;
				buscandoLigaduraExpresion = false;
				break;
				
			case 34:
				ordenesDibujo.add( new OrdenDibujo(marcato, nota.getX(), 
						nota.getY() - config.yAccentUp));
				break;
				
			case 36:
				ordenesDibujo.add( new OrdenDibujo(fermata, nota.getX() - config.xFermata, 
						nota.getY() - config.yFermata));
				break;
				
			case 37:
				ordenesDibujo.add( new OrdenDibujo(fermata_inverted, nota.getX() - config.xFermata, 
						nota.getY() + config.yFermata));
				break;
				
			case 38:				
				ordenesDibujo.add( new OrdenDibujo(trill, nota.getX(), nota.getY() - config.yTrill));
				break;
				
			case 39:				
				ordenesDibujo.add( new OrdenDibujo(trill, nota.getX(), nota.getY() + config.yTrill));
				break;
				
			case 40:
				ordenesDibujo.add( new OrdenDibujo(arpegioImage, 
						nota.getX() - config.xArpegio, nota.getY()));
				break;
				
			default:
				break;
		}
	}
	
	private void dibujarFigurasGraficasDeNota(Nota nota, int y_beams) {
		ArrayList<Byte> figurasGraficas = nota.getFigurasGraficas();
		int numFiguras = figurasGraficas.size();
		int xAlteraciones = 0;
		int numAlteraciones = 0;
		
		for (int i=0; i<numFiguras; i++) {
			
			if (nota.esLigadura(i)) {
				i = gestionarLigaduras(nota, figurasGraficas, i, y_beams);

			} else if (nota.esAlteracion(i)) {
				xAlteraciones = config.xAccidental2 * numAlteraciones++;
				i = gestionarAlteracion(nota, figurasGraficas, i, y_beams, xAlteraciones);

			} else if (nota.finDeTresillo(i)) {
				dibujarFiguraGrafica(nota, figurasGraficas.get(i), y_beams, figurasGraficas.get(i + 1));
				i++;

			} else {
				dibujarFiguraGrafica(nota, figurasGraficas.get(i), y_beams, 0);
			}
		}
	}
	
	private void dibujarIntensidades(Compas compas) {
		for (int i=0; i<compas.numIntensidades(); i++) {
			Intensidad intensidad = compas.getIntensidad(i);
			
			ordenesDibujo.add( new OrdenDibujo(intensidad.getImagen(), 
					intensidad.getX(), intensidad.getY()));
		}
	}
	
	private void dibujarLigadura(int indLigadura, Nota notaFinal, boolean union) {
		int compasNotaInicio = ligaduras.get(indLigadura).getCompas();
		int notaInicio = ligaduras.get(indLigadura).getNota();
		Nota notaInicial = partitura.getCompas(compasNotaInicio).getNota(notaInicio);

		if (union) {
			boolean clockwise = notaInicial.ligaduraUnionEncima();
			dibujarLigaduraUnion(notaInicial, notaFinal, clockwise);
		}
		else {
			boolean clockwise = notaInicial.ligaduraExpresionEncima();
			dibujarLigaduraExpresion(notaInicial, notaFinal, clockwise);
		}
		
		ligaduras.remove(indLigadura);
	}
	
	private void dibujarLigaduraExpresion(Nota notaInicial, Nota notaFinal, boolean clockwise) {
		if (notaInicial.getX() < notaFinal.getX())
			dibujarLigaduraExpresionNormal(notaInicial, notaFinal, clockwise);
		else
			dibujarLigaduraExpresionPartida(notaInicial, notaFinal, clockwise);
	}
	
	private void dibujarLigaduraExpresionNormal(Nota notaInicial, Nota notaFinal, boolean clockwise) {
		int anchoCabezaNota = notaInicial.notaDeGracia() ? 
				config.anchoCabezaNotaGracia : config.anchoCabezaNota;
		RectF rectf = null;
		int y = Math.min(notaInicial.getY(), notaFinal.getY());
		
		if (notaInicial.ligaduraExpresionEncima())
			rectf = new RectF(notaInicial.getX(), y - config.yLigadurasExpresion, 
				notaFinal.getX() + anchoCabezaNota, y + config.alturaArcoLigadurasExpresion);
		else 
			rectf = new RectF(notaInicial.getX(), y + config.yLigadurasExpresion / 2, 
				notaFinal.getX() + anchoCabezaNota, 
				y + config.yLigadurasExpresion / 2 + config.alturaArcoLigadurasExpresion);
		
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 
				notaFinal.getAnguloRotacionLigaduraExpresion(), clockwise));
	}
	
	private void dibujarLigaduraExpresionPartida(Nota notaInicial, Nota notaFinal, boolean clockwise) {
		int anchoCabezaNota = notaInicial.notaDeGracia() ? 
				config.anchoCabezaNotaGracia : config.anchoCabezaNota;
		
		RectF rectf = new RectF(notaInicial.getX(), notaInicial.getY() - config.yLigadurasExpresion, 
				config.xFinalPentagramas, notaInicial.getY() + config.alturaArcoLigadurasExpresion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, clockwise));

		rectf = new RectF(config.xInicialPentagramas, notaFinal.getY() - config.yLigadurasExpresion, 
				notaFinal.getX() + anchoCabezaNota, notaFinal.getY() + config.alturaArcoLigadurasExpresion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, clockwise));
	}
	
	private void dibujarLigaduraUnion(Nota notaInicial, Nota notaFinal, boolean clockwise) {
		if (notaInicial.getX() < notaFinal.getX())
			dibujarLigaduraUnionNormal(notaInicial, notaFinal, clockwise);
		else
			dibujarLigaduraUnionPartida(notaInicial, notaFinal, clockwise);
	}
	
	private void dibujarLigaduraUnionNormal(Nota notaInicial, Nota notaFinal, boolean clockwise) {
		int anchoCabezaNota = notaInicial.notaDeGracia() ? 
				config.anchoCabezaNotaGracia : config.anchoCabezaNota;
		RectF rectf = null;
		
		if (notaInicial.ligaduraUnionEncima())
			rectf = new RectF(notaInicial.getX() + anchoCabezaNota +
				config.xLigadurasUnion, notaFinal.getY() - config.yLigadurasUnion, 
					notaFinal.getX() - config.xLigadurasUnion, 
						notaFinal.getY() + config.alturaArcoLigadurasUnion);
		else
			rectf = new RectF(notaInicial.getX() + anchoCabezaNota +
				config.xLigadurasUnion, notaFinal.getY(), 
					notaFinal.getX() - config.xLigadurasUnion, 
						notaFinal.getY() + config.alturaArcoLigadurasUnion);
		
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, clockwise));
	}
	
	private void dibujarLigaduraUnionPartida(Nota notaInicial, Nota notaFinal, boolean clockwise) {
		RectF rectf = new RectF(notaInicial.getX() + config.anchoCabezaNota +
				config.xLigadurasUnion, notaInicial.getY() - config.yLigadurasUnion, 
				config.xFinalPentagramas, notaInicial.getY() + config.alturaArcoLigadurasUnion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, clockwise));

		int yFinal = notaInicial.getY() +
				(config.distanciaLineasPentagrama * 4 + config.distanciaPentagramas) * 
				(partitura.getStaves());
		rectf = new RectF(config.xInicialPentagramas, yFinal - config.yLigadurasUnion, 
				notaFinal.getX() - config.xLigadurasUnion, yFinal + config.alturaArcoLigadurasUnion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, clockwise));
	}
	
	private void dibujarLineasDePentagramaDeCompas(Compas compas) {
		
		//  Líneas laterales
		ordenesDibujo.add( new OrdenDibujo(
				1, compas.getXIni(), compas.getYIni(), compas.getXIni(), compas.getYFin()));
		ordenesDibujo.add( new OrdenDibujo(
				1, compas.getXFin(), compas.getYIni(), compas.getXFin(), compas.getYFin()));

		//  Líneas horizontales
		int y_linea = compas.getYIni();
		int pentagramas_pendientes = partitura.getStaves();
		do {
			for (int i=0; i<5; i++) {
				ordenesDibujo.add( new OrdenDibujo(
						1, compas.getXIni(), y_linea, compas.getXFin(), y_linea));

				y_linea += config.distanciaLineasPentagrama;
			}

			y_linea += config.distanciaPentagramas - config.distanciaLineasPentagrama;
			pentagramas_pendientes--;

		} while (pentagramas_pendientes > 0);
	}
	
	//  Las notas que se dibujan fuera del pentagrama requieren que se dibujen 
	//  unas pequeñas líneas debajo (o encima) que sirvan de orientación
	private void dibujarLineasFueraDelPentagrama(Nota nota, int yIniCompas) {
		int y_margin_custom = yIniCompas + 
				(config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas) * (nota.getPentagrama() - 1);
		
		int yNota = nota.notaDeGracia() ? nota.getY() - config.margenNotaGracia : nota.getY();

		if (yNota == y_margin_custom + config.distanciaLineasPentagrama * 4 + 
				config.distanciaLineasPentagramaMitad) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 5);
		}
		
		if (yNota == y_margin_custom + config.distanciaLineasPentagrama * 5) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 5);
		}
		
		if (yNota == y_margin_custom + config.distanciaLineasPentagrama * 5 + 
				config.distanciaLineasPentagramaMitad) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 5);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 6);
		}
		
		if (yNota == y_margin_custom + config.distanciaLineasPentagrama * 6) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 5);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 6);
		}
		
		if (yNota == y_margin_custom + config.distanciaLineasPentagrama * 6 + 
				config.distanciaLineasPentagramaMitad) {

			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 5);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 6);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 7);
		}
		
		if (yNota == y_margin_custom + config.distanciaLineasPentagrama * 7) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 5);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 6);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.distanciaLineasPentagrama * 7);
		}
		
		if (yNota == y_margin_custom + config.distanciaLineasPentagrama * 7 + 
				config.distanciaLineasPentagramaMitad) {

		}
		
		if (yNota == y_margin_custom + config.distanciaLineasPentagrama * 8) {

		}
		
		if (yNota == y_margin_custom - config.distanciaLineasPentagrama - 
				config.distanciaLineasPentagramaMitad) {

			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama);
		}
		
		if (yNota == y_margin_custom - config.distanciaLineasPentagrama * 2) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama);
		}
		
		if (yNota == y_margin_custom - config.distanciaLineasPentagrama * 2 - 
				config.distanciaLineasPentagramaMitad) {

			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 2);
		}
		
		if (yNota == y_margin_custom - config.distanciaLineasPentagrama * 3) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 2);
		}
		
		if (yNota == y_margin_custom - config.distanciaLineasPentagrama * 3 - 
				config.distanciaLineasPentagramaMitad) {

			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 2);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 3);
		}
		
		if (yNota == y_margin_custom - config.distanciaLineasPentagrama * 4) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 2);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 3);
		}
		
		if (yNota == y_margin_custom - config.distanciaLineasPentagrama * 4 - 
				config.distanciaLineasPentagramaMitad) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 2);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 3);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.distanciaLineasPentagrama * 4);
		}
	}
	
	//  Función auxiliar para las líneas de fuera del pentagrama
	private void dibujarLineasFueraDelPentagramaAuxiliar(int x, int y) {
		ordenesDibujo.add( new OrdenDibujo(
				1, x - config.margenAnchoCabezaNota, y, 
				x + config.anchoCabezaNota + config.margenAnchoCabezaNota, y));
	}
	
	private void dibujarNotasDeCompas(Compas compas) {
		ArrayList<Nota> notas = compas.getNotas();
		int numNotas = notas.size();
		
		for (int i=0; i<numNotas; i++) {
			notaActual = i;
			
			dibujarCabezaDeNota(notas.get(i));

			int y_beams = 0;
			if (notas.get(i).tieneBeams())
				y_beams = gestionarBeams(notas.get(i));
			else
				if (dibujarPlicaDeNota(notas.get(i), 0))
					dibujarCorcheteDeNota(notas.get(i));
			
			dibujarFigurasGraficasDeNota(notas.get(i), y_beams);
			dibujarLineasFueraDelPentagrama(notas.get(i), compas.getYIni());
			
			gestionarOctavarium(notas.get(i), compas.getYIni() - 
					config.distanciaLineasPentagrama * 6);
		}
	}
	
	private void dibujarNumeroDeCompas(Compas compas) {
		ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraNumeroCompas, 
				false, compas.getNumeroCompas() + "", 
				compas.getXIni() - config.xNumeroCompas, compas.getYIni() - config.yNumeroCompas));
	}
	
	private void dibujarOctavarium() {		
		ordenesDibujo.add( new OrdenDibujo(octavariumImage, x_ini_octavarium, 
				y_ini_octavarium + 5));
		
		if (x_ini_octavarium < x_fin_octavarium) {
			ordenesDibujo.add( new OrdenDibujo(
					2, x_ini_octavarium, y_ini_octavarium, 
					x_fin_octavarium + config.anchoCabezaNota, y_fin_octavarium));
		}
		else {
			ordenesDibujo.add( new OrdenDibujo(
					2, x_ini_octavarium, y_ini_octavarium, 
					config.xFinalPentagramas, y_ini_octavarium));
			ordenesDibujo.add( new OrdenDibujo(
					2, config.xInicialPentagramas, y_fin_octavarium, 
					x_fin_octavarium + config.anchoCabezaNota, y_fin_octavarium));
		}
		
		ordenesDibujo.add( new OrdenDibujo(
				2, x_fin_octavarium + config.anchoCabezaNota, y_fin_octavarium, 
				x_fin_octavarium + config.anchoCabezaNota, y_fin_octavarium + config.yOctavarium));
	}
	
	private void gestionarOctavarium(Nota nota, int margin_y) {
		if (nota.octavada()) {
			
			switch (octavarium) {

				case 0:
					x_ini_octavarium = nota.getX();
					y_ini_octavarium = margin_y;
					octavarium++;
					break;
					
				case 1:
					x_fin_octavarium = nota.getX();
					y_fin_octavarium = margin_y;
					break;
			}
		}
		else {
			if (octavarium > 0) {
				dibujarOctavarium();
				
				octavarium = 0;
				x_ini_octavarium = 0;
				x_fin_octavarium = 0;
				y_ini_octavarium = 0;
				y_fin_octavarium = 0;
			}
		}
	}
	
	private void dibujarPedales(Compas compas) {
		for (int i=0; i<compas.numPedalesInicio(); i++) {
			Pedal pedalInicio = compas.getPedalInicio(i);
			
			ordenesDibujo.add( new OrdenDibujo(pedalInicio.getImagen(), 
					pedalInicio.getX(), pedalInicio.getY()));
		}

		for (int i=0; i<compas.numPedalesFin(); i++) {
			Pedal pedalFin = compas.getPedalFin(i);
			
			ordenesDibujo.add( new OrdenDibujo(pedalFin.getImagen(), 
					pedalFin.getX(), pedalFin.getY()));
		}
	}
	
	private boolean dibujarPlicaDeNota(Nota nota, int y_beams) {
		if (nota.tienePlica()) {
			int mitadCabezaNota = nota.notaDeGracia() ? 
					config.mitadCabezaVerticalNotaGracia : config.mitadCabezaVertical;
			int anchoCabezaNota = nota.notaDeGracia() ? 
					config.anchoCabezaNotaGracia : config.anchoCabezaNota;
			int longitudPlica = nota.notaDeGracia() ? 
					config.longitudPlicaNotaGracia : config.longitudPlica;
			
			int x1 = 0;
			int y1 = nota.getY() + mitadCabezaNota;
			int x2 = 0;
			int y2 = 0;
					
			switch (nota.getPlica()) {
				case 1:
					x1 = nota.getX() + anchoCabezaNota;
					x2 = nota.getX() + anchoCabezaNota;
					
					if (nota.acorde()) 
						y2 = y_anterior;
					else 
						y2 = nota.getY() - longitudPlica;
					break;
				case 2:
					x1 = nota.getX();
					x2 = nota.getX();
					
					if (nota.acorde()) 
						y2 = y_anterior + longitudPlica;
					else 
						y2 = nota.getY() + mitadCabezaNota + longitudPlica;
					break;
				default:
					break;
			}
			
			if (y_beams != 0) y2 = y_beams;
					
			ordenesDibujo.add( new OrdenDibujo(2, x1, y1, x2, y2));
		}

		return !nota.tieneBeams();
	}
	
	private void dibujarQuintas(Compas compas) {
		Quintas quintas = compas.getQuintas();
		
		switch (quintas.getValorQuintas()) {
			case -5:
				if (quintas.getNotaQuintas() == 3) {
					dibujarQuintasAlteraciones(flat, quintas.getX(), 
							quintas.getMargenY() + config.distanciaLineasPentagrama);
					dibujarQuintasAlteraciones(flat, quintas.getX() + 20, 
							quintas.getMargenY() - config.distanciaLineasPentagramaMitad);
					dibujarQuintasAlteraciones(flat, quintas.getX() + 40, 
							quintas.getMargenY() + config.distanciaLineasPentagramaMitad +
							config.distanciaLineasPentagrama);
					dibujarQuintasAlteraciones(flat, quintas.getX() + 60, quintas.getMargenY());
					dibujarQuintasAlteraciones(flat, quintas.getX() + 80, 
							quintas.getMargenY() + config.distanciaLineasPentagrama * 2);
					
					if (partitura.getStaves() == 2) {
						int offset = config.distanciaLineasPentagrama * 4 +
								config.distanciaPentagramas + config.distanciaLineasPentagrama;
						
						dibujarQuintasAlteraciones(flat, quintas.getX(), 
								quintas.getMargenY() + offset + config.distanciaLineasPentagrama);
						dibujarQuintasAlteraciones(flat, quintas.getX() + 20, 
								quintas.getMargenY() + offset - config.distanciaLineasPentagramaMitad);
						dibujarQuintasAlteraciones(flat, quintas.getX() + 40, 
								quintas.getMargenY() + offset + config.distanciaLineasPentagramaMitad +
								config.distanciaLineasPentagrama);
						dibujarQuintasAlteraciones(flat, quintas.getX() + 60, quintas.getMargenY() + offset);
						dibujarQuintasAlteraciones(flat, quintas.getX() + 80, 
								quintas.getMargenY() + offset + config.distanciaLineasPentagrama * 2);
					}
				}
				break;
				
			default:
				break;
		}
	}
	
	private void dibujarQuintasAlteraciones(Bitmap image, int x, int y) {
		ordenesDibujo.add( new OrdenDibujo(image, x, y));
	}

	private void dibujarSlash(int x, int y) {
		ordenesDibujo.add( new OrdenDibujo(2, x + config.xInicioSlash, 
				y + config.yInicioSlash, x - config.xFinSlash, y + config.yFinSlash));
	}
	
	private void dibujarSlide(Nota nota, int x_ini_slide, int y_ini_slide) {
		if (x_ini_slide < nota.getX()) {
			ordenesDibujo.add( new OrdenDibujo(
				1, x_ini_slide + config.anchoCabezaNota, 
					y_ini_slide + config.mitadCabezaVertical, nota.getX(), 
						nota.getY() + config.mitadCabezaVertical));
		}
		else {
			ordenesDibujo.add( new OrdenDibujo(
				1, x_ini_slide + config.anchoCabezaNota, 
					y_ini_slide + config.mitadCabezaVertical, 
						config.xFinalPentagramas, 
								y_ini_slide - config.ySlideTruncado));
			
			ordenesDibujo.add( new OrdenDibujo(
				1, config.xInicialPentagramas, 
					nota.getY() + config.mitadCabezaVertical + config.ySlideTruncado, 
						nota.getX(), nota.getY() + config.mitadCabezaVertical));
		}
	}
	
	private void dibujarTempo(Compas compas) {
		if (compas.getTempo().dibujar()) {
			Tempo tempo = compas.getTempo();
						
			ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTempo, 
					false, tempo.getNumeradorString(), tempo.getX(), tempo.getYNumerador()));
			ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTempo, 
					false, tempo.getDenominadorString(), tempo.getX(), tempo.getYDenominador()));
	
			if (partitura.getStaves() == 2) {
				int margenY = config.distanciaLineasPentagrama * 4 + config.distanciaPentagramas;
				
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTempo, 
						false, tempo.getNumeradorString(), tempo.getX(), tempo.getYNumerador() + margenY));
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTempo, 
						false, tempo.getDenominadorString(), tempo.getX(), tempo.getYDenominador() + margenY));
			}
		}
	}
	
	private void dibujarTextos(Compas compas) {
		int numTextos = compas.numTextos();
		
		for (int i=0; i<numTextos; i++) {
			Texto texto = compas.getTexto(i);
			
			ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraWords, 
					false, texto.getTexto(), texto.getX(), texto.getY()));
		}
	}
	
	private void dibujarCrescendosYDiminuendos(Compas compas) {
		int num = compas.numCrescendos();
		Wedge wedge;
		
		for (int i=0; i<num; i++) {
			wedge = compas.getCrescendo(i);
			
			ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
					wedge.getYIni() + config.alturaCrescendos / 2,
					wedge.getXFin(), wedge.getYIni()));
			ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
					wedge.getYIni() + config.alturaCrescendos / 2,
					wedge.getXFin(), wedge.getYIni() + config.alturaCrescendos));
		}
		
		num = compas.numDiminuendos();
		
		for (int i=0; i<num; i++) {
			wedge = compas.getDiminuendo(i);
			
			ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
					wedge.getYIni(), wedge.getXFin(), wedge.getYIni() + config.alturaCrescendos / 2));
			ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
					wedge.getYIni() + config.alturaCrescendos, 
					wedge.getXFin(), wedge.getYIni() + config.alturaCrescendos / 2));
		}
	}
	
	//  Busca, en el array de ligaduras de unión, el índice
	//  del elemento que contiene el inicio de esta ligadura
	private int encontrarIndiceLigadura(byte ligadura) {
		int numLigaduras = ligaduras.size();
		int indice = -1;
		
		for (int i=0; i<numLigaduras; i++) {
			if (ligaduras.get(i).getLigadura() == ligadura) {
				indice = i;
				break;
			}
		}
		
		return indice;
	}

	private int gestionarAlteracion(Nota nota, ArrayList<Byte> figurasGraficas, 
			int ind, int y_beams, int xAlteraciones) {
		
		if (figurasGraficas.get(ind + 1) == 1)
			nota.setX(nota.getX() - config.anchoCabezaNota);
		
		dibujarFiguraGrafica(nota, figurasGraficas.get(ind++), y_beams, xAlteraciones);
		return ind;
	}
	
	//  Guarda las posiciones de las notas que tienen beams para,
	//  más adelante, dibujar sus plicas a la altura del beam
	private int gestionarBeams(Nota nota) {
		boolean dibujarBeams = false;
		int beamId = 0;
		
		if (nota.tieneBeams()) {
			IndiceNota beam = new IndiceNota(compasActual, notaActual, (byte) 0, nota.getBeamId());
			beams.add(beam);
			beamId = beam.getBeamId();
			
			if (nota.beamFinal())
				dibujarBeams = true;
		}
		
		int y_beams = 0;
		if (dibujarBeams) {
			y_beams = colocarBeamsALaMismaAltura(nota.haciaArriba(), beamId);
			dibujarBeams(y_beams, nota.haciaArriba(), beamId);
		}
		
		return y_beams;
	}
	
	private int gestionarLigaduras(Nota nota, ArrayList<Byte> figurasGraficas, int ind, int y_beams) {
		
		if (figurasGraficas.get(ind + 1) == 0) {
			if (nota.esLigaduraUnion(ind)) {
				nota.setLigaduraUnionOrientacion(true);
				nota.setLigaduraUnion(figurasGraficas.get(ind + 2));
			}
			else {
				nota.setLigaduraExpresionOrientacion(true);
				nota.setAnguloRotacionLigaduraExpresion(figurasGraficas.get(ind + 2));
				nota.setLigaduraExpresion(figurasGraficas.get(ind + 3));
			}
		}
		
		dibujarFiguraGrafica(nota, figurasGraficas.get(ind), y_beams, 0);
		ind += 2;
		
		return ind;
	}
}