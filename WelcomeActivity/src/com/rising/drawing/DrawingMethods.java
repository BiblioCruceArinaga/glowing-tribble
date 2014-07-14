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
	private byte[] clavesActuales = {0, 0};
	private Tempo tempoActual = null;
	
	private int compasActual = 0;
	private int notaActual = 0;
	private int primerCompas = 0;
	private int ultimoCompas = 0;

	//  Variables para la gestión de las múltiples notas
	private ArrayList<IndiceNota> beams = new ArrayList<IndiceNota>();
	private ArrayList<IndiceNota> ligaduras = new ArrayList<IndiceNota>();
	private int ligaduraExpresionY = Integer.MAX_VALUE;
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
	private Bitmap bassclef = null;
	private Bitmap bendrelease = null;
	private Bitmap blackheadlittle = null;
	private Bitmap blackhead = null;
	private Bitmap eighthrest = null;
	private Bitmap fermata = null;
	private Bitmap fermata_inverted = null;
	private Bitmap flat = null;
	private Bitmap forte = null;
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
	private Bitmap quarterrest = null;
	private Bitmap rectangle = null;
	private Bitmap sharp = null;
	private Bitmap trebleclef = null;
	private Bitmap vibrato = null;
	private Bitmap whitehead = null;

	
	public DrawingMethods(Partitura partitura, Config config, Resources resources, Vista vista) {
		if (config.supported()) {
			
			this.partitura = partitura;
			this.config = config;
			this.vista = vista;
			
			compas_margin_x = config.getXInicialPentagramas();
			compas_margin_y = config.getMargenSuperior();

			accent = BitmapFactory.decodeResource(resources, R.drawable.accent);
			bassclef = BitmapFactory.decodeResource(resources, R.drawable.bassclef);
			bendrelease = BitmapFactory.decodeResource(resources, R.drawable.bendrelease);
			blackheadlittle = BitmapFactory.decodeResource(resources, R.drawable.blackheadlittle);
			blackhead = BitmapFactory.decodeResource(resources, R.drawable.blackhead);
			eighthrest = BitmapFactory.decodeResource(resources, R.drawable.eighthrest);
			fermata = BitmapFactory.decodeResource(resources, R.drawable.fermata);
			fermata_inverted = BitmapFactory.decodeResource(resources, R.drawable.fermata_inverted);
			flat = BitmapFactory.decodeResource(resources, R.drawable.flat);
			forte = BitmapFactory.decodeResource(resources, R.drawable.forte);
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
			quarterrest = BitmapFactory.decodeResource(resources, R.drawable.quarterrest);
			rectangle = BitmapFactory.decodeResource(resources, R.drawable.rectangle);
			sharp = BitmapFactory.decodeResource(resources, R.drawable.sharp);
			trebleclef = BitmapFactory.decodeResource(resources, R.drawable.trebleclef);
			vibrato = BitmapFactory.decodeResource(resources, R.drawable.vibrato);
			whitehead = BitmapFactory.decodeResource(resources, R.drawable.whitehead);
			
			isValid = true;
		}
	}
	
	private int calcularCabezaDeNota(Nota nota, int posicion) {
		int y = obtenerPosicionYDeNota(nota, 
				clavesActuales[nota.getPentagrama() - 1], partitura.getInstrument());
		if (nota.notaDeGracia()) y += config.getMargenNotaGracia();

		return y;
	}

	private void calcularClefs(Compas compas) {
		ElementoGrafico[] clefs = compas.getClefs();
		
		int x_position = -1;
		int numClavesEnElemento = -1;
		
		for (int i=0; i<clefs.length; i++) {

			if (clefs[i] != null) {
				x_position = calcularPosicionX(compas.getPositions(), clefs[i].getPosition());
				numClavesEnElemento = clefs[i].getValue(1);
	
				for (int j=0; j<numClavesEnElemento; j++) {
					byte pentagrama = clefs[i].getValue(2 + 2 * j);
					byte claveByte = clefs[i].getValue(3 + 2 * j);
	
					//  El margen Y depende del pentagrama al que pertenezca el compás
					int marginY = compas_margin_y + 
							(config.getDistanciaLineasPentagrama() * 4 + 
									config.getDistanciaPentagramas()) * (pentagrama - 1);

					Clave clave = new Clave();
					clave.setImagenClave(obtenerImagenDeClave(claveByte));
					clave.setX(compas_margin_x + x_position);
					clave.setY(marginY + obtenerPosicionYDeClave(claveByte));
					clave.setClave(claveByte);
					clave.setPentagrama(pentagrama);
					clave.setPosition(clefs[i].getPosition());
					
					compas.setClave(clave, pentagrama);
				}
			}
		}
	}
	
	private int calcularDesplazamientoExtraNotaDeGracia(Nota nota, int posicionX) {
		int desplazamientoExtra = 0;
		
		if (nota.notaDeGracia()) {
			desplazamientoExtra = config.getDesplazamientoExtraNotaGracia();
			
			if (!nota.tieneBeams())
				desplazamientoExtra -= config.getOffsetUltimaNotaGracia();
			else
				if (nota.beamFinal()) 
					desplazamientoExtra -= config.getOffsetUltimaNotaGracia();
		}
		
		return desplazamientoExtra;
	}
	
	private void calcularDynamics(Compas compas) {
		ElementoGrafico dynamics = compas.getDynamics();
		byte location = dynamics.getValue(0);
		byte intensidadByte = dynamics.getValue(1);
		int posicion = calcularPosicionX(compas.getPositions(), dynamics.getPosition());

		Intensidad intensidad = new Intensidad();
		intensidad.setImagen(obtenerImagenDeIntensidad(intensidadByte));
		intensidad.setX(compas_margin_x + posicion);
		intensidad.setY(obtenerPosicionYDeElementoGrafico(1, location));

		compas.setIntensidad(intensidad);
	}
	
	private void calcularFifths(Compas compas) {
		ElementoGrafico fifths = compas.getFifths();
		byte notaQuintasByte = fifths.getValue(1);
		byte valorQuintasByte = fifths.getValue(2);
		int posicion = calcularPosicionX(compas.getPositions(), fifths.getPosition());

		Quintas quintas = new Quintas();
		quintas.setNotaQuintas(notaQuintasByte);
		quintas.setValorQuintas(valorQuintasByte);
		quintas.setX(compas_margin_x + posicion);
		quintas.setMargenY(compas_margin_y);

		compas.setQuintas(quintas);
	}
	
	private void calcularPedals(Compas compas) {
		if (compas.hayPedalStart()) {
			for (int i=0; i<compas.getNumPedalStarts(); i++) {
				ElementoGrafico pedal = compas.getPedalStart(i);
				byte location = pedal.getValue(0);
				int posicion = calcularPosicionX(compas.getPositions(), pedal.getPosition());
	
				Pedal pedalInicio = new Pedal();
				pedalInicio.setImagen(pedalStart);
				pedalInicio.setX(compas_margin_x + posicion);
				pedalInicio.setY(obtenerPosicionYDeElementoGrafico(2, location));
				compas.addPedalInicio(pedalInicio);
			}
		}
		
		if (compas.hayPedalStop()) {
			for (int i=0; i<compas.getNumPedalStops(); i++) {
				ElementoGrafico pedal = compas.getPedalStop(i);
				byte location = pedal.getValue(0);
				int posicion = calcularPosicionX(compas.getPositions(), pedal.getPosition());
	
				Pedal pedalFin = new Pedal();
				pedalFin.setImagen(pedalStop);
				pedalFin.setX(compas_margin_x + posicion);
				pedalFin.setY(obtenerPosicionYDeElementoGrafico(2, location));
				compas.addPedalFin(pedalFin);
			}
		}
	}
	
	private void calcularPosicionesDeCompas(Compas compas) {
		compas.setXIni(compas_margin_x);
		compas.setYIni(compas_margin_y);
		
		compas_margin_x += config.getMargenIzquierdoCompases();

		if (compas.hayClefs()) calcularClefs(compas);
		if (compas.hayFifths()) calcularFifths(compas);
		calcularTime(compas);
		if (compas.hayWords()) calcularWords(compas);
		if (compas.hayDynamics()) calcularDynamics(compas);
		if (compas.hayPedals()) calcularPedals(compas);
		if (compas.hayWedges()) calcularWedges(compas);
		
		compas.setXIniNotas(compas_margin_x);
		
		int distanciaX = calcularPosicionesDeNotas(compas);
		compas_margin_x += distanciaX;
		compas_margin_x += config.getMargenDerechoCompases();
		
		compas.setXFin(compas_margin_x);
		compas.setYFin(compas_margin_y + 
				config.getDistanciaLineasPentagrama() * 4 + 
				(config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama() * 4) * 
				(partitura.getStaves() - 1));
		
		if (vista == Vista.VERTICAL) {
			if (compas.getXFin() > config.getXFinalPentagramas()) {
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

	private int calcularPosicionesDeNota(ArrayList<Integer> posiciones, 
			Compas compas, Nota nota) {
		int posicionX = nota.getPosition();
		int posicionY = 0;
		
		if (posicionX != -1) {
			posicionX = calcularPosicionX(posiciones, posicionX);
			posicionX += calcularDesplazamientoExtraNotaDeGracia(nota, posicionX);
			nota.setX(compas_margin_x + posicionX);
			
			//  Si se coloca una clave en el compás, las notas anteriores
			//  a esta clave deben colocarse según la clave vieja, y las
			//  posteriores según la clave nueva
			Clave clave = compas.getClavePorPentagrama(nota.getPentagrama());
			if (clave != null) {
				if (clave.getX() <= nota.getX())
					clavesActuales[nota.getPentagrama() - 1] = clave.getByteClave();
				else {
					
					//  Si no hay notas después de esta clave en este compás,
					//  las notas del siguiente compás deben calcularse con esta clave
					if (compas.noHayNotasDelanteDeClave(clave))
						clavesActuales[nota.getPentagrama() - 1] = clave.getByteClave();
				}
			}
			
			posicionY = calcularCabezaDeNota(nota, posicionX);
			nota.setY(posicionY);
		}

		return posicionX;
	}
	
	private int calcularPosicionesDeNotas(Compas compas) {
		ArrayList<Nota> notas = compas.getNotas();
		int numNotas = notas.size();
		
		ArrayList<Integer> posiciones = compas.getPositions();
		int mayorDistanciaX = 0;
		int distanciaActualX = 0;
		
		for (int i=0; i<numNotas; i++) {
			distanciaActualX = calcularPosicionesDeNota(posiciones, compas, notas.get(i));

			if (distanciaActualX > mayorDistanciaX) 
				mayorDistanciaX = distanciaActualX;
		}
		
		return mayorDistanciaX;
	}
	
	private int calcularPosicionX(ArrayList<Integer> posiciones, int position) {
		return position * config.getUnidadDesplazamiento() / partitura.getDivisions();
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
		int numWedges = compas.getNumWedges();
		int posicionX = 0;
		
		//  Asumimos que los crescendos o diminuendos estarán siempre
		//  contenidos dentro del mismo compás. También estamos
		//  asumiendo que, en la partitura, siempre que se indique el
		//  comienzo de un crescendo o diminuendo, su posición
		//  final vendrá justo después
		
		for (int i=0; i<numWedges; i++) {
			posicionX = calcularPosicionX(compas.getPositions(), compas.getWedge(i).getPosition());
			Wedge wedge = new Wedge(compas.getWedge(i).getValue(1), compas_margin_x + posicionX);
			wedge.setYIni(obtenerPosicionYDeElementoGrafico(4, compas.getWedge(i).getValue(0)));

			i++;
			
			posicionX = calcularPosicionX(compas.getPositions(), compas.getWedge(i).getPosition());
			wedge.setXFin(compas_margin_x + posicionX);
			
			if (wedge.crescendo())
				compas.addCrescendo(wedge);
			else
				compas.addDiminuendo(wedge);
		}
	}
	
	private void calcularWords(Compas compas) {
		int numWords = compas.getNumWords();
		
		for (int i=0; i<numWords; i++) {
			Texto texto = new Texto();
			int posicionX = calcularPosicionX(compas.getPositions(), compas.getWordsPosition(i));
			
			texto.setTexto(compas.getWordsString(i));
			texto.setX(compas_margin_x + posicionX);
			texto.setY(obtenerPosicionYDeElementoGrafico(3, compas.getWordsLocation(i)));
			
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
		
		//  Obra
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraObra());
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_ALIGN, -1);
		ordenDibujo.setTexto(partitura.getWork());
		ordenDibujo.setX1(config.getWidth() / 2);
		ordenDibujo.setY1(compas_margin_y + config.getMargenObra());
		ordenesDibujo.add(ordenDibujo);
		
		//  Autor
		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraAutor());
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_ALIGN, -1);
		ordenDibujo.setTexto(partitura.getCreator());
		ordenDibujo.setX1(config.getWidth() / 2);
		ordenDibujo.setY1(compas_margin_y + config.getMargenAutor());
		ordenesDibujo.add(ordenDibujo);
		
		compas_margin_y += config.getMargenInferiorAutor();
		
		calcularPosicionesDeCompases();
		dibujarCompases();
		return ordenesDibujo;
	}

	private void inicializarTempo(Tempo tempo, Compas compas, int numerador, int denominador) {
		int x_position = calcularPosicionX(compas.getPositions(), compas.getTime().getPosition());
		
		tempo.setDibujar(true);
		tempo.setNumerador(numerador);
		tempo.setDenominador(denominador);
		tempo.setX(compas_margin_x + x_position);
		tempo.setYNumerador(compas_margin_y + config.getDistanciaLineasPentagrama() * 2);
		tempo.setYDenominador(compas_margin_y + config.getDistanciaLineasPentagrama() * 4);
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	private void moverCompasAlSiguienteRenglon(Compas compas) {
		int distancia_x = compas.getXIni() - config.getXInicialPentagramas();
		int distancia_y = (config.getDistanciaLineasPentagrama() * 4 + 
				config.getDistanciaPentagramas()) * partitura.getStaves();

		compas.setXIni(config.getXInicialPentagramas());
		compas.setXFin(compas.getXFin() - distancia_x);
		if (compas.getXFin() > config.getXFinalPentagramas())
			compas.setXFin(config.getXFinalPentagramas());
		compas.setXIniNotas(compas.getXIniNotas() - distancia_x);
		
		if (compas.hayClaves()) {
			Clave[] claves = compas.getClaves();
 
			for (int i=0; i<claves.length; i++) {
				if (compas.getClave(i) != null) {
					compas.getClave(i).setX(compas.getClave(i).getX() - distancia_x);
					compas.getClave(i).setY(compas.getClave(i).getY() + distancia_y);
				}
			}
		}
 		
		if (compas.hayIntensidad()) {
			compas.getIntensidad().setX(compas.getIntensidad().getX() - distancia_x);
			compas.getIntensidad().setY(compas.getIntensidad().getY() + distancia_y);
		}
		
		if (compas.hayPedalInicio()) {
			for (int i=0; i<compas.numeroDePedalesInicio(); i++) {
				compas.getPedalInicio(i).setX(compas.getPedalInicio(i).getX() - distancia_x);
				compas.getPedalInicio(i).setY(compas.getPedalInicio(i).getY() + distancia_y);
			}
		}
		
		if (compas.hayPedalFin()) {
			for (int i=0; i<compas.numeroDePedalesFin(); i++) {
				compas.getPedalFin(i).setX(compas.getPedalFin(i).getX() - distancia_x);
				compas.getPedalFin(i).setY(compas.getPedalFin(i).getY() + distancia_y);
			}
		}
		
		if (compas.hayTempo()) {
			compas.getTempo().setX(compas.getTempo().getX() - distancia_x);
			compas.getTempo().setYNumerador(compas.getTempo().getYNumerador() + distancia_y);
			compas.getTempo().setYDenominador(compas.getTempo().getYDenominador() + distancia_y);
		}
		
		if (compas.hayTextos()) {
			for (int i=0; i<compas.numeroDeTextos(); i++) {
				compas.getTexto(i).setX(compas.getTexto(i).getX() - distancia_x);
				compas.getTexto(i).setY(compas.getTexto(i).getY() + distancia_y);
			}
		}
		
		for (int i=0; i<compas.numeroDeCrescendos(); i++) {
			compas.getCrescendo(i).setXIni(compas.getCrescendo(i).getXIni() - distancia_x);
			compas.getCrescendo(i).setXFin(compas.getCrescendo(i).getXFin() - distancia_x);
			compas.getCrescendo(i).setYIni(compas.getCrescendo(i).getYIni() + distancia_y);
		}
		
		for (int i=0; i<compas.numeroDeDiminuendos(); i++) {
			compas.getDiminuendo(i).setXIni(compas.getDiminuendo(i).getXIni() - distancia_x);
			compas.getDiminuendo(i).setXFin(compas.getDiminuendo(i).getXFin() - distancia_x);
			compas.getDiminuendo(i).setYIni(compas.getDiminuendo(i).getYIni() + distancia_y);
		}
		
		compas_margin_x = compas.getXFin();
		compas_margin_y = compas_margin_y + distancia_y;

		compas.setYIni(compas_margin_y);
		compas.setYFin(compas_margin_y + 
				config.getDistanciaLineasPentagrama() * 4 + 
				(config.getDistanciaPentagramas() + 
						config.getDistanciaLineasPentagrama() * 4) * (partitura.getStaves() - 1));
		
		int numNotas = compas.numeroDeNotas();
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
			default:
				return null;
		}
	}
	
	private int obtenerPosicionYDeClave(byte clave) {
		switch (clave) {
			case 1: return - config.getYClaveSolSegunda();
			default: return 0;
		}
	}
	
	private int obtenerPosicionYDeElementoGrafico(int tipoElemento, int location) {
		switch (tipoElemento) {
	
			//  Intensidad
			case 1:
				switch (location) {
					case 2:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 6;
					case 3:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 + 
								config.getDistanciaPentagramas() - config.getDistanciaLineasPentagrama() * 4;
					case 5:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 +
								config.getDistanciaPentagramas() / 2 - config.getYIntensidadMiddle();
					default:
						return compas_margin_y - config.getDistanciaLineasPentagrama() * 6;
				}
	
			//  Pedales
			case 2:
				switch (location) {
					case 4:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 + 
							config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama() * 8;
					case 5:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 +
								config.getDistanciaPentagramas() / 2;
					default:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 + 
							config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama() * 8;
				}
	
			//  Texto
			case 3:
				switch (location) {
					case 1:
						return compas_margin_y - config.getDistanciaLineasPentagrama() * 4;
					case 2:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 6;
					case 4:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 + 
								config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama() * 8;
					case 5:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 +
								config.getDistanciaPentagramas() / 2;
					default:
						return compas_margin_y - config.getDistanciaLineasPentagrama();
				}
				
			//  Crescendos y diminuendos
			case 4:
				switch (location) {
					case 1:
						return 0;
					case 2:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 6;
					case 4:
						return 0;
					case 5:
						return 0;
					default:
						return 0;
				}
				
			default:
				return 0;
		}
	}
	
	private int obtenerPosicionYDeNota(Nota nota, byte clave, byte instrumento){
		int coo_y = 0;
		int margenY = compas_margin_y + 
				(config.getDistanciaLineasPentagrama() * 4 + 
						config.getDistanciaPentagramas()) * (nota.getPentagrama() - 1);
		
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
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 5 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 5;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 8;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 7 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 7;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 6 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 6;
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
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 2;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.getDistanciaLineasPentagrama() + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 4 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 4;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 3 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 3;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 2 + 
												config.getDistanciaLineasPentagramaMitad();
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
											coo_y = margenY - config.getDistanciaLineasPentagrama() - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 2;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagramaMitad();
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.getDistanciaLineasPentagramaMitad();
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.getDistanciaLineasPentagrama();
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
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 5;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 5 - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 2 - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 3;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 3 - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 4;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 4 - 
												config.getDistanciaLineasPentagramaMitad();
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
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 9;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 8 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 11 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 11;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 10 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 10;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 9 + 
												config.getDistanciaLineasPentagramaMitad();
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
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 5 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.getDistanciaLineasPentagrama() + 5;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 8;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 7 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 7;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 6 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 6;
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
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 2;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.getDistanciaLineasPentagrama() + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 4 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 4;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 3 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 3;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 2 + 
												config.getDistanciaLineasPentagramaMitad();
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
											coo_y = margenY - config.getDistanciaLineasPentagrama() - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 2;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagramaMitad();
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.getDistanciaLineasPentagramaMitad();
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.getDistanciaLineasPentagrama();
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
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 5;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 5 - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 2 - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 3;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 3 - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 4;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 4 - 
												config.getDistanciaLineasPentagramaMitad();
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
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 6 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 6;
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 9;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 8 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 8;
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 7 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 7;
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
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 3;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 2 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 5 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 5;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 4 + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 4;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 3 + 
												config.getDistanciaLineasPentagramaMitad();
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
											coo_y = margenY - config.getDistanciaLineasPentagramaMitad();
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.getDistanciaLineasPentagrama();
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY + config.getDistanciaLineasPentagrama() * 2;
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY + config.getDistanciaLineasPentagrama() + 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY + config.getDistanciaLineasPentagrama();
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY + config.getDistanciaLineasPentagrama() - 
												config.getDistanciaLineasPentagramaMitad();
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
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 4;
											break;

										case 2:
										case 9:
										case 16:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 4 - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 3:
										case 10:
										case 17:
											coo_y = margenY - config.getDistanciaLineasPentagrama() - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 4:
										case 11:
										case 18:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 2;
											break;

										case 5:
										case 12:
										case 19:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 2 - 
												config.getDistanciaLineasPentagramaMitad();
											break;

										case 6:
										case 13:
										case 20:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 3;
											break;

										case 7:
										case 14:
										case 21:
											coo_y = margenY - config.getDistanciaLineasPentagrama() * 3 - 
												config.getDistanciaLineasPentagramaMitad();
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
					coo_y = margenY + config.getDistanciaLineasPentagrama() + 
						config.getDistanciaLineasPentagramaMitad() + config.getYSilencioBlanca();
					break;
					
				case 11:
					coo_y = margenY + config.getDistanciaLineasPentagrama();
					break;

				default: break;
			}
		}

		return coo_y;
	}
	
	private void reajustarCompases() {
		int espacioADistribuir = config.getXFinalPentagramas() - partitura.getCompas(ultimoCompas).getXFin();

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
	            if (i == ultimoCompas) posicionX = config.getXFinalPentagramas();
	            compas.setXFin(posicionX);
	            
	            int numNotas = compas.numeroDeNotas();
	            for (int j=0; j<numNotas; j++)
	            	compas.getNota(j).setX(compas.getNota(j).getX() + anchoParaCadaCompas);

	            if (compas.hayClaves()) {
	            	Clave[] claves = compas.getClaves();
	            	 
	    			for (int j=0; j<claves.length; j++)
	    				if (compas.getClave(j) != null)
	    					compas.getClave(j).setX(compas.getClave(j).getX() + anchoParaCadaCompas);
	            }
	            
	            if (compas.hayIntensidad())
	            	compas.getIntensidad().setX(compas.getIntensidad().getX() + anchoParaCadaCompas);
	            
	            if (compas.hayPedalInicio())
	            	for (int j=0; j<compas.numeroDePedalesInicio(); j++)
	            		compas.getPedalInicio(j).setX(compas.getPedalInicio(j).getX() + anchoParaCadaCompas);
	            
	            if (compas.hayPedalFin())
	            	for (int j=0; j<compas.numeroDePedalesFin(); j++)
	            		compas.getPedalFin(j).setX(compas.getPedalFin(j).getX() + anchoParaCadaCompas);
	            
	            if (compas.hayTempo())
	            	compas.getTempo().setX(compas.getTempo().getX() + anchoParaCadaCompas);
	            
	            for (int j=0; j<compas.numeroDeTextos(); j++)
	            	compas.getTexto(j).setX(compas.getTexto(j).getX() + anchoParaCadaCompas);
	            
	            for (int j=0; j<compas.numeroDeCrescendos(); j++) {
	            	compas.getCrescendo(j).setXIni(compas.getCrescendo(j).getXIni() + anchoParaCadaCompas);
	            	compas.getCrescendo(j).setXFin(compas.getCrescendo(j).getXFin() + anchoParaCadaCompas);
	            }
	            
	            for (int j=0; j<compas.numeroDeDiminuendos(); j++) {
	            	compas.getDiminuendo(j).setXIni(compas.getDiminuendo(j).getXIni() + anchoParaCadaCompas);
	            	compas.getDiminuendo(j).setXFin(compas.getDiminuendo(j).getXFin() + anchoParaCadaCompas);
	            }
        	}
        }
        
        //  Segundo paso: reajustar posición de las notas
        for (int i=primerCompas; i<=ultimoCompas; i++) {
        	Compas compas = partitura.getCompas(i);
        	ArrayList<Integer> xsDeNotas = compas.saberXsDeNotas();
        	
        	int lastX = compas.saberXUltimaNota();
        	int anchoADistribuir = compas.getXFin() - config.getMargenDerechoCompases() - lastX;
        	
        	//  El primer elemento no lo vamos a mover, de ahí el -1
        	int numElementos = xsDeNotas.size() - 1;
        	int anchoPorNota = 0;
        	if (numElementos > 0)
        		anchoPorNota = anchoADistribuir / numElementos;
        	
        	reajustarNotas(compas, xsDeNotas, anchoPorNota);
        	reajustarFigurasGraficas(compas, anchoPorNota);
        }
	}

	private void reajustarFigurasGraficas(Compas compas, int anchoPorNota) {
		
		int multiplicador = 0;
		int xPrimeraNota = compas.saberXPrimeraNota();
		
		ArrayList<Integer> xsDelCompas = compas.saberXsDelCompas();

    	if (compas.hayClaves()) {
        	Clave[] claves = compas.getClaves();
        	
			for (int j=0; j<claves.length; j++) {
				if (compas.getClave(j) != null) {
    				multiplicador = xsDelCompas.indexOf(compas.getClave(j).getX());
    				compas.getClave(j).setX(compas.getClave(j).getX() + anchoPorNota * multiplicador);
				}
			}
        }
		
		if (compas.hayIntensidad()) {
			if (compas.getIntensidad().getX() != xPrimeraNota) {
				multiplicador = xsDelCompas.indexOf(compas.getIntensidad().getX());
	        	compas.getIntensidad().setX( 
	        			compas.getIntensidad().getX() + anchoPorNota * multiplicador);
			}
    	}
        
    	if (compas.hayPedalInicio()) {
    		for (int i=0; i<compas.numeroDePedalesInicio(); i++) {
	    		if (compas.getPedalInicio(i).getX() != xPrimeraNota) {
		    		multiplicador = xsDelCompas.indexOf(compas.getPedalInicio(i).getX());
		        	compas.getPedalInicio(i).setX( 
		        			compas.getPedalInicio(i).getX() + anchoPorNota * multiplicador);
	    		}
    		}
    	}
        
        if (compas.hayPedalFin()) {
        	for (int i=0; i<compas.numeroDePedalesInicio(); i++) {
	        	if (compas.getPedalFin(i).getX() != xPrimeraNota) {
		        	multiplicador = xsDelCompas.indexOf(compas.getPedalFin(i).getX());
		        	compas.getPedalFin(i).setX( 
		        			compas.getPedalFin(i).getX() + anchoPorNota * multiplicador);
	        	}
        	}
        }

    	for (int i=0; i<compas.numeroDeTextos(); i++) {
        	//if (compas.getTexto(i).getX() != xPrimeraNota) {
        		multiplicador = xsDelCompas.indexOf(compas.getTexto(i).getX());
	        	compas.getTexto(i).setX( 
	        			compas.getTexto(i).getX() + anchoPorNota * multiplicador);
        	//}
    	}
        
        for (int i=0; i<compas.numeroDeCrescendos(); i++) {
    		multiplicador = xsDelCompas.indexOf(compas.getCrescendo(i).getXIni());
    		compas.getCrescendo(i).setXIni( 
        			compas.getCrescendo(i).getXIni() + anchoPorNota * multiplicador);
    		
    		multiplicador = xsDelCompas.indexOf(compas.getCrescendo(i).getXFin());
    		compas.getCrescendo(i).setXFin( 
        			compas.getCrescendo(i).getXFin() + anchoPorNota * multiplicador);
        }
        
        for (int i=0; i<compas.numeroDeDiminuendos(); i++) {
    		multiplicador = xsDelCompas.indexOf(compas.getDiminuendo(i).getXIni());
    		compas.getDiminuendo(i).setXIni( 
        			compas.getDiminuendo(i).getXIni() + anchoPorNota * multiplicador);
    		
    		multiplicador = xsDelCompas.indexOf(compas.getDiminuendo(i).getXFin());
    		compas.getDiminuendo(i).setXFin( 
        			compas.getDiminuendo(i).getXFin() + anchoPorNota * multiplicador);
        }
	}
	
	private void reajustarNotas(Compas compas, ArrayList<Integer> xsDeNotas, int anchoPorNota) {
		
		//  A cada elemento se le suma una distancia cada vez
    	//  mayor, ya que de lo contrario sólo estaríamos
    	//  desplazándolos todos pero manteniéndolos a la misma
    	//  distancia entre sí mismos que antes
    	ArrayList<Nota> notas = compas.getNotas();
    	int numNotas = notas.size();
    	int multiplicador = 0;
    	for (int j=0;j<numNotas;j++) {
			multiplicador = xsDeNotas.indexOf(notas.get(j).getX());
			notas.get(j).setX(notas.get(j).getX() + anchoPorNota * multiplicador);
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
				config.getLongitudPlica() : config.getLongitudPlicaNotaGracia();
		
		if (haciaArriba) y_beams -= longitudPlica;
		else y_beams += longitudPlica;
		
		return y_beams;
	}
	
	//  Esta implementación por ahora sólo considera el barline de fin de partitura
	//  Si en el futuro se añadieran más barlines, habría que usar un switch en el bucle
	private void dibujarBarlines(Compas compas) {
		ArrayList<ElementoGrafico> barlines = compas.getBarlines();
		int numBarlines = barlines.size();
		OrdenDibujo ordenDibujo = new OrdenDibujo();

		for (int i=0; i<numBarlines; i++) {
			if (barlines.get(i).getValue(1) == 2) {
				ordenDibujo = new OrdenDibujo();
				ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
				ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 4);
				ordenDibujo.setX1(compas.getXFin());
				ordenDibujo.setY1(compas.getYIni());
				ordenDibujo.setX2(compas.getXFin());
				ordenDibujo.setY2(compas.getYFin());
				ordenesDibujo.add(ordenDibujo);

				ordenDibujo = new OrdenDibujo();
				ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
				ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
				ordenDibujo.setX1(compas.getXFin() - config.getMargenBarlines());
				ordenDibujo.setY1(compas.getYIni());
				ordenDibujo.setX2(compas.getXFin() - config.getMargenBarlines());
				ordenDibujo.setY2(compas.getYFin());
				ordenesDibujo.add(ordenDibujo);
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
			
			OrdenDibujo ordenDibujo;
			
			indCompasAnt = beams.get(i).getCompas();
			indNotaAnt = beams.get(i).getNota();
			
			distancia_beams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ? 
					config.getDistanciaEntreBeamsNotasGracia() : config.getDistanciaEntreBeams();
			if (!haciaArriba) distancia_beams *= -1;
			
			ancho_beams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ?
					config.getAnchoBeamsNotaGracia() : config.getAnchoBeams();

			if ( (i == numBeams - 1) || (beams.get(i + 1).getBeamId() != beamId) ) {
				
				int x_last_beam = 0;
				Nota nota = partitura.getCompas(indCompasAnt).getNota(indNotaAnt);
				int offset = nota.haciaArriba() ? config.getAnchoCabezaNota() : 0;
				
				//  Gestión de hooks en la última nota
				switch (nota.getBeam()) {
						
					case 4:
						x_last_beam = nota.getX();
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, ancho_beams);
						ordenDibujo.setX1(x_last_beam + offset);
						ordenDibujo.setY1(y_beams + distancia_beams * 2);
						ordenDibujo.setX2(x_last_beam + offset - config.getAnchoHooks());
						ordenDibujo.setY2(y_beams + distancia_beams * 2);
						ordenesDibujo.add(ordenDibujo);
						break;
						
					case 6:
						x_last_beam = nota.getX();
						
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, ancho_beams);
						ordenDibujo.setX1(x_last_beam + offset);
						ordenDibujo.setY1(y_beams + distancia_beams);
						ordenDibujo.setX2(x_last_beam + offset - config.getAnchoHooks());
						ordenDibujo.setY2(y_beams + distancia_beams);
						ordenesDibujo.add(ordenDibujo);
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
							config.getAnchoCabezaNotaGracia() : config.getAnchoCabezaNota();
					x_ant_beams += anchoCabezaNota;
					x_sig_beams += anchoCabezaNota;
				}
				
				switch (partitura.getCompas(indCompasAnt).getNota(indNotaAnt).getBeam()) {
					case 2:
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, ancho_beams);
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, ancho_beams);
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams + distancia_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams + distancia_beams);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_ant_beams);
						ordenDibujo.setY2(y_beams + distancia_beams);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
						ordenDibujo.setX1(x_sig_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams + distancia_beams);
						ordenesDibujo.add(ordenDibujo);
						break;
	
					case 3:
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, ancho_beams);
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams);
						ordenesDibujo.add(ordenDibujo);
						break;
	
					case 5:
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, ancho_beams);
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, ancho_beams);
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams + distancia_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams + distancia_beams);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, ancho_beams);
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams + distancia_beams * 2);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams + distancia_beams * 2);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_ant_beams);
						ordenDibujo.setY2(y_beams + distancia_beams * 2);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
						ordenDibujo.setX1(x_sig_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams + distancia_beams * 2);
						ordenesDibujo.add(ordenDibujo);
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
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
		ordenDibujo.setImagen(obtenerImagenDeCabezaDeNota(nota));

		int desplazamiento = 0;
		if (nota.desplazadaALaIzquierda()) desplazamiento -= config.getAnchoCabezaNota();
		if (nota.desplazadaALaDerecha()) desplazamiento += config.getAnchoCabezaNota();
		
		ordenDibujo.setX1(nota.getX() + desplazamiento);
		ordenDibujo.setY1(nota.getY());
		
		//  La y de la ligadura de expresión debe colocarse tan arriba como
		//  obligue a ello la nota más alta en medio de las notas ligadas
		if (ligaduraExpresionY > nota.getY())
			ligaduraExpresionY = nota.getY();
			
		if (!nota.acorde()) y_anterior = nota.getY();

		ordenesDibujo.add(ordenDibujo);
	}
	
	private void dibujarClaves(Compas compas) {
		Clave[] claves = compas.getClaves();
		int numClaves = claves.length;
		
		for (int i=0; i<numClaves; i++) {
			if (claves[i] != null) {
				OrdenDibujo ordenDibujo = new OrdenDibujo();
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(claves[i].getImagenClave());
				ordenDibujo.setX1(claves[i].getX());
				ordenDibujo.setY1(claves[i].getY());
				ordenesDibujo.add(ordenDibujo);
			}
		}
	}
	
	private void dibujarCompases() {
		ArrayList<Compas> compases = partitura.getCompases();
		int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++) {
			compasActual = i;
			
			//  Este compás da inicio al pentagrama, por tanto dibujamos su número
			if (compases.get(i).getXIni() == config.getXInicialPentagramas())
				dibujarNumeroDeCompas(compases.get(i));
			
			dibujarLineasDePentagramaDeCompas(compases.get(i));
			dibujarClaves(compases.get(i));
			
			if (compases.get(i).hayQuintas()) dibujarQuintas(compases.get(i));
			if (compases.get(i).hayIntensidad()) dibujarIntensidad(compases.get(i));
			if (compases.get(i).hayPedales()) dibujarPedales(compases.get(i));
			if (compases.get(i).hayTempo()) dibujarTempo(compases.get(i));
			if (compases.get(i).hayTextos()) dibujarTextos(compases.get(i));
			dibujarCrescendosYDiminuendos(compases.get(i));
			
			dibujarNotasDeCompas(compases.get(i));
			
			if (compases.get(i).hayBarlines()) dibujarBarlines(compases.get(i));
		}
	}

	private void dibujarCorcheteDeNota(Nota nota) {
		
		//  En este caso no significa que la nota no pueda formar parte de un acorde. 
		//  Significa que si forma parte de un acorde, su corchete ya fue dibujado, 
		//  por lo que no hay que dibujarlo de nuevo
		if (!nota.acorde() && !nota.silencio()) {
			int anchoCabezaNota = nota.notaDeGracia() ? 
					config.getAnchoCabezaNotaGracia() : config.getAnchoCabezaNota();
			int longitudPlica = nota.notaDeGracia() ? 
					config.getLongitudPlicaNotaGracia() : config.getLongitudPlica();
			int anchoCorchete = nota.notaDeGracia() ? 
					config.getLargoImagenCorcheteGracia() : config.getLargoImagenCorchete();
			int distanciaCorchete = nota.haciaArriba() ?
					config.getDistanciaCorchetes() : - config.getDistanciaCorchetes();

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
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
		ordenDibujo.setImagen(obtenerImagenDeCorcheteDeNota(nota));
		ordenDibujo.setX1(x);
		ordenDibujo.setY1(y + distanciaCorchete);
		ordenesDibujo.add(ordenDibujo);
	}
	
	private void dibujarFiguraGrafica(Nota nota, byte figura, int y_beams, int Xsillo) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();

		switch (figura) {
			case 3:
				x_ini_tresillo = nota.getX();
				break;

			case 4:
				int margenTresillo = nota.haciaArriba() ? 
						- config.getYTresilloArriba() : config.getYTresilloAbajo();
				int x_tresillo = (nota.getX() + x_ini_tresillo) / 2;
				if (nota.haciaArriba()) x_tresillo += config.getXTresillo();

				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTresillo());
				ordenDibujo.setTexto(Xsillo + "");
				ordenDibujo.setX1(x_tresillo);
				ordenDibujo.setY1(y_beams + margenTresillo);
				ordenesDibujo.add(ordenDibujo);
				break;

			case 6:
				x_ini_slide = nota.getX();
				y_ini_slide = nota.getY();
				break;
				
			case 7:
				ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
				ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
				ordenDibujo.setX1(x_ini_slide + config.getAnchoCabezaNota());
				ordenDibujo.setY1(y_ini_slide + config.getMitadCabezaNotaVertical());
				ordenDibujo.setX2(nota.getX());
				ordenDibujo.setY2(nota.getY() + config.getMitadCabezaNotaVertical());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 8:
				if (nota.haciaArriba()) {
					ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
					ordenDibujo.setRadius(config.getRadioStaccatos());
					ordenDibujo.setX1(nota.getX() + config.getXStaccato());
					ordenDibujo.setY1(nota.getY() + config.getYStaccatoArriba());
					ordenesDibujo.add(ordenDibujo);
				}
				else {
					ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
					ordenDibujo.setRadius(config.getRadioStaccatos());
					ordenDibujo.setX1(nota.getX() + config.getXStaccato());
					ordenDibujo.setY1(nota.getY() - config.getYStaccatoAbajo());
					ordenesDibujo.add(ordenDibujo);
				}
				break;

			case 9:
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTapping());
				ordenDibujo.setTexto("T");
				ordenDibujo.setX1(nota.getX());
				ordenDibujo.setY1(nota.getY() - config.getYTapping());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 10:
				ligaduras.add(new IndiceNota(compasActual, notaActual, 
						nota.getLigaduraUnion(), (byte) 0));
				break;
				
			case 11:
				int indLigaduraUnion = encontrarIndiceLigadura(nota.getLigaduraUnion());
				dibujarLigaduraUnion(indLigaduraUnion, nota.getX());
				break;
			
			case 12:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(sharp);

				ordenDibujo.setX1(nota.getX() - config.getXAccidental());
				if (nota.desplazadaALaIzquierda()) 
					ordenDibujo.setX1(ordenDibujo.getX1() - config.getAnchoCabezaNota());

				ordenDibujo.setY1(nota.getY() - config.getYAccidental());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 13:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(flat);

				ordenDibujo.setX1(nota.getX() - config.getXAccidental());
				if (nota.desplazadaALaIzquierda()) 
					ordenDibujo.setX1(ordenDibujo.getX1() - config.getAnchoCabezaNota());

				ordenDibujo.setY1(nota.getY() - config.getYAccidentalFlat());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 14:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(natural);

				ordenDibujo.setX1(nota.getX() - config.getXAccidental());
				if (nota.desplazadaALaIzquierda()) 
					ordenDibujo.setX1(ordenDibujo.getX1() - config.getAnchoCabezaNota());

				ordenDibujo.setY1(nota.getY() - config.getYAccidental());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 15:
				ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
				ordenDibujo.setRadius(config.getRadioPuntillos());
				ordenDibujo.setX1(nota.getX() + config.getXPuntillo());
				ordenDibujo.setY1(nota.getY() + config.getMitadCabezaNotaVertical());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 16:
				ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
				ordenDibujo.setRadius(config.getRadioPuntillos());
				ordenDibujo.setX1(nota.getX() + config.getXPuntillo());
				ordenDibujo.setY1(nota.getY() + config.getYPuntilloArriba());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 17:
				ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
				ordenDibujo.setRadius(config.getRadioPuntillos());
				ordenDibujo.setX1(nota.getX() + config.getXPuntillo());
				ordenDibujo.setY1(nota.getY() + config.getYPuntilloAbajo());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 22:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(bendrelease);
				ordenDibujo.setX1(nota.getX());
				ordenDibujo.setY1(nota.getY() + config.getYBend());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 26:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(vibrato);
				ordenDibujo.setX1(nota.getX());
				ordenDibujo.setY1(nota.getY() + config.getYBend());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 27:
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraPalmMute());
				ordenDibujo.setTexto("P.M.");
				ordenDibujo.setX1(nota.getX());
				ordenDibujo.setY1(nota.getY() - config.getYPalmMute());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 28:
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraPalmMute());
				ordenDibujo.setTexto("P.M.");
				ordenDibujo.setX1(nota.getX());
				ordenDibujo.setY1(nota.getY() + config.getYPalmMute());
				ordenesDibujo.add(ordenDibujo);
				break;
			
			case 29:
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTapping());
				ordenDibujo.setTexto("T");
				ordenDibujo.setX1(nota.getX());
				ordenDibujo.setY1(nota.getY() + config.getYTapping());
				ordenesDibujo.add(ordenDibujo);
				
			case 30:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(accent);
				
				if (nota.haciaArriba()) 
					ordenDibujo.setX1(nota.getX() - config.getOffsetAccent());
				else
					ordenDibujo.setX1(nota.getX());
				
				ordenDibujo.setY1(nota.getY() - config.getYAccentUp());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 32:
				ligaduras.add(new IndiceNota(compasActual, notaActual, 
						nota.getLigaduraExpresion(), (byte) 0));
				break;
				
			case 33:
				int indLigaduraExpresion = encontrarIndiceLigadura(nota.getLigaduraExpresion());
				dibujarLigaduraExpresion(indLigaduraExpresion, nota.getX(), nota.getY());
				ligaduraExpresionY = Integer.MAX_VALUE;
				break;
				
			case 34:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(marcato);
				ordenDibujo.setX1(nota.getX());
				ordenDibujo.setY1(nota.getY() - config.getYAccentUp());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 36:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(fermata);
				ordenDibujo.setX1(nota.getX() - config.getXFermata());
				ordenDibujo.setY1(nota.getY() - config.getYFermata());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 37:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(fermata_inverted);
				ordenDibujo.setX1(nota.getX() - config.getXFermata());
				ordenDibujo.setY1(nota.getY() + config.getYFermata());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			default:
				break;
		}
	}
	
	private void dibujarFigurasGraficasDeNota(Nota nota, int y_beams) {
		ArrayList<Byte> figurasGraficas = nota.getFigurasGraficas();
		int numFiguras = figurasGraficas.size();
		
		for (int i=0; i<numFiguras; i++) {

			//  Gestión de ligaduras, que llevan bytes extra
			if (nota.esLigadura(i)) {
				i = gestionarLigaduras(nota, figurasGraficas, i, y_beams);
			
			//  Gestión de alteraciones, que llevan un byte extra
			} else if (nota.esAlteracion(i)) {
				i = gestionarAlteracion(nota, figurasGraficas, i, y_beams);
				
			//  Gestión de finales de XSillo, que llevan un byte extra
			} else if (nota.finDeTresillo(i)) {
				dibujarFiguraGrafica(nota, figurasGraficas.get(i), y_beams, figurasGraficas.get(i + 1));
				i++;
				
			//  Resto de figuras gráficas
			} else {
				dibujarFiguraGrafica(nota, figurasGraficas.get(i), y_beams, 0);
			}
		}
	}
	
	private void dibujarIntensidad(Compas compas) {
		Intensidad intensidad = compas.getIntensidad();
		
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
		ordenDibujo.setImagen(intensidad.getImagen());
		ordenDibujo.setX1(intensidad.getX());
		ordenDibujo.setY1(intensidad.getY());
		ordenesDibujo.add(ordenDibujo);
	}
	
	private void dibujarLigaduraExpresion(int indLigadura, int xFinal, int yFinal) {
		int compasNotaInicio = ligaduras.get(indLigadura).getCompas();
		int notaInicio = ligaduras.get(indLigadura).getNota();
		
		Nota nota = partitura.getCompas(compasNotaInicio).getNota(notaInicio);
		int xInicio = nota.getX();
		int yInicio = nota.getY();

		if (xInicio < xFinal) {
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_ARC);
			ordenDibujo.setPaint(PaintOptions.SET_STYLE_STROKE, 0);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			
			RectF rectf = null;
			int notasEnMedio = notaActual - notaInicio;
			
			if (nota.ligaduraExpresionEncima()) {
				
				//  No son notas contiguas
				if (notasEnMedio > 1) {
					ordenDibujo.setAngulo(0);
					
					rectf = new RectF(xInicio, ligaduraExpresionY - config.getYLigadurasExpresion(), 
						xFinal + config.getAnchoCabezaNota(), ligaduraExpresionY);
				}
				
				//  Son notas contiguas
				else {
					ordenDibujo.setAngulo(hallarAngulo(yInicio, yFinal));
					
					int y = Math.min(yInicio, yFinal);
					rectf = new RectF(xInicio, y - config.getYLigadurasExpresion(), 
						xFinal + config.getAnchoCabezaNota(), 
						y + config.getAlturaArcoLigadurasExpresion());
				}
			}
			
			//  Else en el futuro para las ligaduras dibujadas por debajo
			
			ordenDibujo.setRectF(rectf, config.getAnchoLigaduraUnionMax(), config.getOffsetLigaduraExpresion());
			ordenesDibujo.add(ordenDibujo);
		}
		
		//  En el futuro se añadirá un else para controlar
		//  las ligaduras de expresión que terminan en un
		//  compás inferior
		
		ligaduras.remove(indLigadura);
	}
	
	
	private void dibujarLigaduraUnion(int indLigadura, int xFinal) {
		int compasNotaInicio = ligaduras.get(indLigadura).getCompas();
		int notaInicio = ligaduras.get(indLigadura).getNota();
		
		Nota nota = partitura.getCompas(compasNotaInicio).getNota(notaInicio);
		int xInicio = nota.getX();
		int y = nota.getY();

		if (xInicio < xFinal) {
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_ARC);
			ordenDibujo.setPaint(PaintOptions.SET_STYLE_STROKE, 0);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			
			RectF rectf = new RectF(xInicio + config.getAnchoCabezaNota() +
					config.getXLigadurasUnion(), y - config.getYLigadurasUnion(), 
					xFinal - config.getXLigadurasUnion(), y + config.getAlturaArcoLigadurasUnion());
			ordenDibujo.setRectF(rectf, config.getAnchoLigaduraUnionMax(), config.getOffsetLigaduraUnion());
			
			ordenesDibujo.add(ordenDibujo);
		}
		
		//  La ligadura es con una nota de un compás
		//  que fue desplazado hacia abajo porque no cabía,
		//  así que la dibujamos al frente
		else {
			dibujarLigaduraDeNotasEnDiferentesRenglones(xInicio, y, xFinal);
		}
		
		
		ligaduras.remove(indLigadura);
	}
	
	private void dibujarLigaduraDeNotasEnDiferentesRenglones(int xInicio, int yInicio, int xFinal) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_ARC);
		ordenDibujo.setPaint(PaintOptions.SET_STYLE_STROKE, 0);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
		
		RectF rectf = new RectF(xInicio + config.getAnchoCabezaNota() +
				config.getXLigadurasUnion(), yInicio - config.getYLigadurasUnion(), 
				config.getXFinalPentagramas(), yInicio + config.getAlturaArcoLigadurasUnion());
		ordenDibujo.setRectF(rectf, config.getAnchoLigaduraUnionMax(), config.getOffsetLigaduraUnion());
		
		ordenesDibujo.add(ordenDibujo);
		
	    ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_ARC);
		ordenDibujo.setPaint(PaintOptions.SET_STYLE_STROKE, 0);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
		
		int yFinal = yInicio +
				(config.getDistanciaLineasPentagrama() * 4 + config.getDistanciaPentagramas()) * 
				(partitura.getStaves());
		
		rectf = new RectF(config.getXInicialPentagramas(), yFinal - config.getYLigadurasUnion(), 
				xFinal - config.getXLigadurasUnion(), yFinal + config.getAlturaArcoLigadurasUnion());
		ordenDibujo.setRectF(rectf, config.getAnchoLigaduraUnionMax(), config.getOffsetLigaduraUnion());
		
		ordenesDibujo.add(ordenDibujo);
	}
	
	private void dibujarLineasDePentagramaDeCompas(Compas compas) {
		
		//  Líneas laterales
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
		ordenDibujo.setX1(compas.getXIni());
		ordenDibujo.setY1(compas.getYIni());
		ordenDibujo.setX2(compas.getXIni());
		ordenDibujo.setY2(compas.getYFin());
		ordenesDibujo.add(ordenDibujo);

		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
		ordenDibujo.setX1(compas.getXFin());
		ordenDibujo.setY1(compas.getYIni());
		ordenDibujo.setX2(compas.getXFin());
		ordenDibujo.setY2(compas.getYFin());
		ordenesDibujo.add(ordenDibujo);

		//  Líneas horizontales
		int y_linea = compas.getYIni();
		int pentagramas_pendientes = partitura.getStaves();
		do {
			for (int i=0; i<5; i++) {
				ordenDibujo = new OrdenDibujo();
				ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
				ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
				ordenDibujo.setX1(compas.getXIni());
				ordenDibujo.setY1(y_linea);
				ordenDibujo.setX2(compas.getXFin());
				ordenDibujo.setY2(y_linea);
				ordenesDibujo.add(ordenDibujo);

				y_linea += config.getDistanciaLineasPentagrama();
			}

			y_linea += config.getDistanciaPentagramas() - config.getDistanciaLineasPentagrama();
			pentagramas_pendientes--;

		} while (pentagramas_pendientes > 0);
	}
	
	//  Las notas que se dibujan fuera del pentagrama requieren que se dibujen 
	//  unas pequeñas líneas debajo (o encima) que sirvan de orientación
	private void dibujarLineasFueraDelPentagrama(Nota nota, int yIniCompas) {
		int y_margin_custom = yIniCompas + 
				(config.getDistanciaLineasPentagrama() * 4 + 
						config.getDistanciaPentagramas()) * (nota.getPentagrama() - 1);			

		if (nota.getY() == y_margin_custom + config.getDistanciaLineasPentagrama() * 4 + 
				config.getDistanciaLineasPentagramaMitad()) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
		}
		
		if (nota.getY()== y_margin_custom + config.getDistanciaLineasPentagrama() * 5) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
		}
		
		if (nota.getY() == y_margin_custom + config.getDistanciaLineasPentagrama() * 5 + 
				config.getDistanciaLineasPentagramaMitad()) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 6);
		}
		
		if (nota.getY() == y_margin_custom + config.getDistanciaLineasPentagrama() * 6) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 6);
		}
		
		if (nota.getY() == y_margin_custom + config.getDistanciaLineasPentagrama() * 6 + 
				config.getDistanciaLineasPentagramaMitad()) {

			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 6);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 7);
		}
		
		if (nota.getY() == y_margin_custom + config.getDistanciaLineasPentagrama() * 7) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 6);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom +
					config.getDistanciaLineasPentagrama() * 7);
		}
		
		if (nota.getY() == y_margin_custom + config.getDistanciaLineasPentagrama() * 7 + 
				config.getDistanciaLineasPentagramaMitad()) {

		}
		
		if (nota.getY() == y_margin_custom + config.getDistanciaLineasPentagrama() * 8) {

		}
		
		if (nota.getY() == y_margin_custom - config.getDistanciaLineasPentagrama() - 
				config.getDistanciaLineasPentagramaMitad()) {

			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama());
		}
		
		if (nota.getY() == y_margin_custom - config.getDistanciaLineasPentagrama() * 2) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama());
		}
		
		if (nota.getY() == y_margin_custom - config.getDistanciaLineasPentagrama() * 2 - 
				config.getDistanciaLineasPentagramaMitad()) {

			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama());
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 2);
		}
		
		if (nota.getY() == y_margin_custom - config.getDistanciaLineasPentagrama() * 3) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama());
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 2);
		}
		
		if (nota.getY() == y_margin_custom - config.getDistanciaLineasPentagrama() * 3 - 
				config.getDistanciaLineasPentagramaMitad()) {

			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama());
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 2);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 3);
		}
		
		if (nota.getY() == y_margin_custom - config.getDistanciaLineasPentagrama() * 4) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama());
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 2);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 3);
		}
		
		if (nota.getY() == y_margin_custom - config.getDistanciaLineasPentagrama() * 4 - 
				config.getDistanciaLineasPentagramaMitad()) {
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama());
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 2);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 3);
			dibujarLineasFueraDelPentagramaAuxiliar(nota.getX(), y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 4);
		}
	}
	
	//  Función auxiliar para las líneas de fuera del pentagrama
	private void dibujarLineasFueraDelPentagramaAuxiliar(int x, int y) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
		ordenDibujo.setX1(x - config.getMargenAnchoCabezaNota());
		ordenDibujo.setY1(y);
		ordenDibujo.setX2(x + config.getAnchoCabezaNota() + config.getMargenAnchoCabezaNota());
		ordenDibujo.setY2(y);
		ordenesDibujo.add(ordenDibujo);
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
					config.getDistanciaLineasPentagrama() * 6);
		}
	}
	
	private void dibujarNumeroDeCompas(Compas compas) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraNumeroCompas());
		ordenDibujo.setTexto(compas.getNumeroCompas() + "");
		ordenDibujo.setX1(compas.getXIni() - config.getXNumeroCompas());
		ordenDibujo.setY1(compas.getYIni() - config.getYNumeroCompas());
		ordenesDibujo.add(ordenDibujo);
	}
	
	private void dibujarOctavarium() {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
		ordenDibujo.setImagen(octavariumImage);
		ordenDibujo.setX1(x_ini_octavarium);
		ordenDibujo.setY1(y_ini_octavarium + config.getYOctavarium() / 2);
		ordenesDibujo.add(ordenDibujo);
		
		if (x_ini_octavarium < x_fin_octavarium) {
			ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			ordenDibujo.setX1(x_ini_octavarium);
			ordenDibujo.setY1(y_ini_octavarium);
			ordenDibujo.setX2(x_fin_octavarium + config.getAnchoCabezaNota());
			ordenDibujo.setY2(y_fin_octavarium);
			ordenesDibujo.add(ordenDibujo);
		}
		else {
			ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			ordenDibujo.setX1(x_ini_octavarium);
			ordenDibujo.setY1(y_ini_octavarium);
			ordenDibujo.setX2(config.getXFinalPentagramas());
			ordenDibujo.setY2(y_ini_octavarium);
			ordenesDibujo.add(ordenDibujo);
			
			ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			ordenDibujo.setX1(config.getXInicialPentagramas());
			ordenDibujo.setY1(y_fin_octavarium);
			ordenDibujo.setX2(x_fin_octavarium + config.getAnchoCabezaNota());
			ordenDibujo.setY2(y_fin_octavarium);
			ordenesDibujo.add(ordenDibujo);
		}
		
		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
		ordenDibujo.setX1(x_fin_octavarium + config.getAnchoCabezaNota());
		ordenDibujo.setY1(y_fin_octavarium);
		ordenDibujo.setX2(x_fin_octavarium + config.getAnchoCabezaNota());
		ordenDibujo.setY2(y_fin_octavarium + config.getYOctavarium());
		ordenesDibujo.add(ordenDibujo);
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
		if (compas.hayPedalInicio()) {
			for (int i=0; i<compas.numeroDePedalesInicio(); i++) {
				Pedal pedalInicio = compas.getPedalInicio(i);
				
				OrdenDibujo ordenDibujo = new OrdenDibujo();
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(pedalInicio.getImagen());
				ordenDibujo.setX1(pedalInicio.getX());
				ordenDibujo.setY1(pedalInicio.getY());
				ordenesDibujo.add(ordenDibujo);
			}
		}
		
		if (compas.hayPedalFin()) {
			for (int i=0; i<compas.numeroDePedalesFin(); i++) {
				Pedal pedalFin = compas.getPedalFin(i);
				
				OrdenDibujo ordenDibujo = new OrdenDibujo();
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(pedalFin.getImagen());
				ordenDibujo.setX1(pedalFin.getX());
				ordenDibujo.setY1(pedalFin.getY());
				ordenesDibujo.add(ordenDibujo);
			}
		}
	}
	
	private boolean dibujarPlicaDeNota(Nota nota, int y_beams) {
		if (nota.tienePlica()) {
			int mitadCabezaNota = nota.notaDeGracia() ? 
					config.getMitadCabezaNotaGraciaVertical() : config.getMitadCabezaNotaVertical();
			int anchoCabezaNota = nota.notaDeGracia() ? 
					config.getAnchoCabezaNotaGracia() : config.getAnchoCabezaNota();
			int longitudPlica = nota.notaDeGracia() ? 
					config.getLongitudPlicaNotaGracia() : config.getLongitudPlica();

			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
			ordenDibujo.setY1(nota.getY() + mitadCabezaNota);

			if (nota.getPlica() == 1) {
				ordenDibujo.setX1(nota.getX() + anchoCabezaNota);
				ordenDibujo.setX2(nota.getX() + anchoCabezaNota);

				if (nota.acorde()) ordenDibujo.setY2(y_anterior);
				else ordenDibujo.setY2(nota.getY() - longitudPlica);
			}
			if (nota.getPlica() == 2) {
				ordenDibujo.setX1(nota.getX());
				ordenDibujo.setX2(nota.getX());

				if (nota.acorde()) ordenDibujo.setY2(y_anterior + longitudPlica);
				else ordenDibujo.setY2(nota.getY() + mitadCabezaNota + longitudPlica);
			}

			if (y_beams != 0) ordenDibujo.setY2(y_beams);
			
			ordenesDibujo.add(ordenDibujo);
		}

		return !nota.tieneBeams();
	}
	
	private void dibujarQuintas(Compas compas) {
		Quintas quintas = compas.getQuintas();
		
		switch (quintas.getValorQuintas()) {
			case -5:
				if (quintas.getNotaQuintas() == 3) {
					dibujarQuintasAlteraciones(flat, quintas.getX(), 
							quintas.getMargenY() + config.getDistanciaLineasPentagrama());
					dibujarQuintasAlteraciones(flat, quintas.getX() + 20, 
							quintas.getMargenY() - config.getDistanciaLineasPentagramaMitad());
					dibujarQuintasAlteraciones(flat, quintas.getX() + 40, 
							quintas.getMargenY() + config.getDistanciaLineasPentagramaMitad() +
							config.getDistanciaLineasPentagrama());
					dibujarQuintasAlteraciones(flat, quintas.getX() + 60, quintas.getMargenY());
					dibujarQuintasAlteraciones(flat, quintas.getX() + 80, 
							quintas.getMargenY() + config.getDistanciaLineasPentagrama() * 2);
					
					if (partitura.getStaves() == 2) {
						int offset = config.getDistanciaLineasPentagrama() * 4 +
								config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama();
						
						dibujarQuintasAlteraciones(flat, quintas.getX(), 
								quintas.getMargenY() + offset + config.getDistanciaLineasPentagrama());
						dibujarQuintasAlteraciones(flat, quintas.getX() + 20, 
								quintas.getMargenY() + offset - config.getDistanciaLineasPentagramaMitad());
						dibujarQuintasAlteraciones(flat, quintas.getX() + 40, 
								quintas.getMargenY() + offset + config.getDistanciaLineasPentagramaMitad() +
								config.getDistanciaLineasPentagrama());
						dibujarQuintasAlteraciones(flat, quintas.getX() + 60, quintas.getMargenY() + offset);
						dibujarQuintasAlteraciones(flat, quintas.getX() + 80, 
								quintas.getMargenY() + offset + config.getDistanciaLineasPentagrama() * 2);
					}
				}
				break;
				
			default:
				break;
		}
	}
	
	private void dibujarQuintasAlteraciones(Bitmap image, int x, int y) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
		ordenDibujo.setImagen(image);
		ordenDibujo.setX1(x);
		ordenDibujo.setY1(y);
		ordenesDibujo.add(ordenDibujo);
	}

	private void dibujarSlash(int x, int y) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
		ordenDibujo.setX1(x + config.getXInicioSlash());
		ordenDibujo.setY1(y + config.getYInicioSlash());
		ordenDibujo.setX2(x - config.getXFinSlash());
		ordenDibujo.setY2(y + config.getYFinSlash());
		ordenesDibujo.add(ordenDibujo);
	}
	
	private void dibujarTempo(Compas compas) {
		if (compas.getTempo().dibujar()) {
			Tempo tempo = compas.getTempo();
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
			ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTempo());
			ordenDibujo.setTexto(tempo.getNumeradorString());
			ordenDibujo.setX1(tempo.getX());
			ordenDibujo.setY1(tempo.getYNumerador());
			ordenesDibujo.add(ordenDibujo);
			
			ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
			ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTempo());
			ordenDibujo.setTexto(tempo.getDenominadorString());
			ordenDibujo.setX1(tempo.getX());
			ordenDibujo.setY1(tempo.getYDenominador());
			ordenesDibujo.add(ordenDibujo);
	
			if (partitura.getStaves() == 2) {
				int margenY = config.getDistanciaLineasPentagrama() * 4 + config.getDistanciaPentagramas();
	
				ordenDibujo = new OrdenDibujo();
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTempo());
				ordenDibujo.setTexto(tempo.getNumeradorString());
				ordenDibujo.setX1(tempo.getX());
				ordenDibujo.setY1(tempo.getYNumerador() + margenY);
				ordenesDibujo.add(ordenDibujo);
				
				ordenDibujo = new OrdenDibujo();
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTempo());
				ordenDibujo.setTexto(tempo.getDenominadorString());
				ordenDibujo.setX1(tempo.getX());
				ordenDibujo.setY1(tempo.getYDenominador() + margenY);
				ordenesDibujo.add(ordenDibujo);
			}
		}
	}
	
	private void dibujarTextos(Compas compas) {
		int numTextos = compas.numeroDeTextos();
		
		for (int i=0; i<numTextos; i++) {
			Texto texto = compas.getTexto(i);
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
			ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraWords());
			ordenDibujo.setTexto(texto.getTexto());
			ordenDibujo.setX1(texto.getX());
			ordenDibujo.setY1(texto.getY());
			ordenesDibujo.add(ordenDibujo);
		}
	}
	
	private void dibujarCrescendosYDiminuendos(Compas compas) {
		int num = compas.numeroDeCrescendos();
		Wedge wedge;
		
		for (int i=0; i<num; i++) {
			wedge = compas.getCrescendo(i);
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			ordenDibujo.setX1(wedge.getXIni());
			ordenDibujo.setY1(wedge.getYIni() + config.getAlturaCrescendos() / 2);
			ordenDibujo.setX2(wedge.getXFin());
			ordenDibujo.setY2(wedge.getYIni());
			ordenesDibujo.add(ordenDibujo);
			
			ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			ordenDibujo.setX1(wedge.getXIni());
			ordenDibujo.setY1(wedge.getYIni() + config.getAlturaCrescendos() / 2);
			ordenDibujo.setX2(wedge.getXFin());
			ordenDibujo.setY2(wedge.getYIni() + config.getAlturaCrescendos());
			ordenesDibujo.add(ordenDibujo);
		}
		
		num = compas.numeroDeDiminuendos();
		
		for (int i=0; i<num; i++) {
			wedge = compas.getDiminuendo(i);
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			ordenDibujo.setX1(wedge.getXIni());
			ordenDibujo.setY1(wedge.getYIni());
			ordenDibujo.setX2(wedge.getXFin());
			ordenDibujo.setY2(wedge.getYIni() + config.getAlturaCrescendos() / 2);
			ordenesDibujo.add(ordenDibujo);
			
		    ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
			ordenDibujo.setX1(wedge.getXIni());
			ordenDibujo.setY1(wedge.getYIni() + config.getAlturaCrescendos());
			ordenDibujo.setX2(wedge.getXFin());
			ordenDibujo.setY2(wedge.getYIni() + config.getAlturaCrescendos() / 2);
			ordenesDibujo.add(ordenDibujo);
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

	private int gestionarAlteracion(Nota nota, ArrayList<Byte> figurasGraficas, int ind, int y_beams) {
		if (figurasGraficas.get(ind + 1) == 1)
			nota.setX(nota.getX() - config.getAnchoCabezaNota());
		
		dibujarFiguraGrafica(nota, figurasGraficas.get(ind++), y_beams, 0);
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
		if (nota.esLigaduraUnion(ind)) {
			nota.setLigaduraUnion(figurasGraficas.get(ind + 1));	
			dibujarFiguraGrafica(nota, figurasGraficas.get(ind++), y_beams, 0);
		}
		else {
			if (figurasGraficas.get(ind + 1) == 1) 
				nota.setLigaduraExpresionOrientacion(true);
			
			nota.setLigaduraExpresion(figurasGraficas.get(ind + 2));
			dibujarFiguraGrafica(nota, figurasGraficas.get(ind), y_beams, 0);
			ind += 2;
		}
		
		return ind;
	}
	
	//  Halla el ángulo de rotación de la ligadura de expresión
	private float hallarAngulo(int yInicio, int yFinal) {
		float angulo = 0;
		
		int signo = -1;
		if (yFinal > yInicio) signo = 1;
		
		int distancia = Math.abs(yFinal - yInicio);
		
		if (distancia == config.getDistanciaLineasPentagramaMitad())
			angulo = 0;
		
		if (distancia == config.getDistanciaLineasPentagrama() +
				config.getDistanciaLineasPentagramaMitad())
			angulo = 15;
		
		if (distancia == config.getDistanciaLineasPentagrama() * 2 +
				config.getDistanciaLineasPentagramaMitad())
			angulo = 8;
		
		if (distancia == config.getDistanciaLineasPentagrama() * 3 +
				config.getDistanciaLineasPentagramaMitad())
			angulo = 25;
		
		return angulo * signo;
	}
}