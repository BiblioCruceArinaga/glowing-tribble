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
	
	//  Variables para la gestión y el tratamiento dinámico de los múltiples compases
	private int compas_margin_x = 0;
	private int compas_margin_y = 0;
	private ArrayList<Byte> clavesActuales = new ArrayList<Byte>();
	private Tempo tempoActual = null;
	
	private int compasActual = 0;
	private int notaActual = 0;
	private int primerCompas = 0;
	private int ultimoCompas = 0;

	//  Variables para la gestión de las múltiples notas
	private boolean buscandoOctavarium = false;
	private int octavarium = 0;
	private int[] posicionesOctavarium = {0,0};
	private ArrayList<IndiceNota> beams = new ArrayList<IndiceNota>();
	private int y_anterior = 0;
	private int x_ini_tresillo = 0;
	private int x_ini_slide = 0;
	private int y_ini_slide = 0;
	private ArrayList<IndiceNota> ligadurasInicio = new ArrayList<IndiceNota>();
	
	//  Bitmaps
	private Bitmap trebleclef = null;
	private Bitmap bassclef = null;
	private Bitmap mezzoforte = null;
	private Bitmap forte = null;
	private Bitmap piano = null;
	private Bitmap pianissimo = null;
	private Bitmap rectangle = null;
	private Bitmap quarterrest = null;
	private Bitmap eighthrest = null;
	private Bitmap noterest16 = null;
	private Bitmap noterest32 = null;
	private Bitmap noterest64 = null;
	private Bitmap whitehead = null;
	private Bitmap blackheadlittle = null;
	private Bitmap blackhead = null;
	private Bitmap head = null;
	private Bitmap headlittle = null;
	private Bitmap headinv = null;
	private Bitmap headinvlittle = null;
	private Bitmap sharp = null;
	private Bitmap flat = null;
	private Bitmap natural = null;
	private Bitmap ligato = null;
	private Bitmap vibrato = null;
	private Bitmap tremolobar = null;
	private Bitmap hammeron = null;
	private Bitmap bendrelease = null;
	private Bitmap octavariumImage = null;
	private Bitmap pedalStart = null;
	private Bitmap pedalStop = null;
	
	public DrawingMethods(Partitura partitura, Config config, Resources resources) {
		if (config.supported()) {
		
			this.partitura = partitura;
			this.config = config;
			
			compas_margin_x = config.getXInicialPentagramas();
			compas_margin_y = config.getMargenSuperior();

			trebleclef = BitmapFactory.decodeResource(resources, R.drawable.trebleclef);
			bassclef = BitmapFactory.decodeResource(resources, R.drawable.bassclef);
			mezzoforte = BitmapFactory.decodeResource(resources, R.drawable.mezzoforte);
			forte = BitmapFactory.decodeResource(resources, R.drawable.forte);
			piano = BitmapFactory.decodeResource(resources, R.drawable.piano);
			pianissimo = BitmapFactory.decodeResource(resources, R.drawable.pianissimo);
			rectangle = BitmapFactory.decodeResource(resources, R.drawable.rectangle);
			quarterrest = BitmapFactory.decodeResource(resources, R.drawable.quarterrest);
			eighthrest = BitmapFactory.decodeResource(resources, R.drawable.eighthrest);
			noterest16 = BitmapFactory.decodeResource(resources, R.drawable.noterest16);
			noterest32 = BitmapFactory.decodeResource(resources, R.drawable.noterest32);
			noterest64 = BitmapFactory.decodeResource(resources, R.drawable.noterest64);
			whitehead = BitmapFactory.decodeResource(resources, R.drawable.whitehead);
			blackheadlittle = BitmapFactory.decodeResource(resources, R.drawable.blackheadlittle);
			blackhead = BitmapFactory.decodeResource(resources, R.drawable.blackhead);
			head = BitmapFactory.decodeResource(resources, R.drawable.head);
			headlittle = BitmapFactory.decodeResource(resources, R.drawable.headlittle);
			headinv = BitmapFactory.decodeResource(resources, R.drawable.headinv);
			headinvlittle = BitmapFactory.decodeResource(resources, R.drawable.headinvlittle);
			sharp = BitmapFactory.decodeResource(resources, R.drawable.sharp);
			flat = BitmapFactory.decodeResource(resources, R.drawable.flat);
			natural = BitmapFactory.decodeResource(resources, R.drawable.natural);
			ligato = BitmapFactory.decodeResource(resources, R.drawable.ligato);
			vibrato = BitmapFactory.decodeResource(resources, R.drawable.vibrato);
			tremolobar = BitmapFactory.decodeResource(resources, R.drawable.tremolobar);
			hammeron = BitmapFactory.decodeResource(resources, R.drawable.hammeron);
			bendrelease = BitmapFactory.decodeResource(resources, R.drawable.bendrelease);
			octavariumImage = BitmapFactory.decodeResource(resources, R.drawable.octavarium);
			pedalStart = BitmapFactory.decodeResource(resources, R.drawable.pedalstart);
			pedalStop = BitmapFactory.decodeResource(resources, R.drawable.pedalstop);
			
			isValid = true;
		}
	}
	
	private int calcularCabezaDeNota(Nota nota, int posicion) {
		if (buscandoOctavarium)
			if (compas_margin_x + posicion == posicionesOctavarium[0]) 
				octavarium = 1;

		int y = obtenerPosicionYDeNota(nota, clavesActuales.get(nota.getPentagrama() - 1), partitura.getInstrument());
		if (nota.notaDeGracia()) y += config.getMargenNotaGracia();
		
		if (octavarium > 0) {
			nota.setOctavarium(octavarium);
			
			if (octavarium == 1) {
				nota.setYOctavarium(compas_margin_y - 
						config.getDistanciaLineasPentagrama() * 6 - config.getYOctavarium());
				octavarium++;
			}
			else {
				nota.setYOctavarium(compas_margin_y - config.getDistanciaLineasPentagrama() * 6);
			}
		}

		if (buscandoOctavarium) {
			if (compas_margin_x + posicion == posicionesOctavarium[1]) {
				octavarium = 0;
				buscandoOctavarium = false;

				posicionesOctavarium[0] = 0;
				posicionesOctavarium[1] = 0;
			}
		}

		return y;
	}

	private void calcularClefs(Compas compas) {
		ArrayList<ElementoGrafico> clefs = compas.getClefs();
		
		int numClefs = clefs.size();
		int x_position = -1;
		int numClavesEnElemento = -1;
		boolean claveNormalTratada = false;

		for (int i=0; i<numClefs; i++) {
			claveNormalTratada = false;

			x_position = calcularPosicionX(clefs.get(i).getPosition());
			numClavesEnElemento = clefs.get(i).getValue(1);

			for (int j=0; j<numClavesEnElemento; j++) {
				byte pentagrama = clefs.get(i).getValue(2 + 3 * j);
				byte claveByte = clefs.get(i).getValue(3 + 3 * j);
				byte alteracion = clefs.get(i).getValue(4 + 3 * j);

				//  El margen Y depende del pentagrama al que pertenezca el compás
				int marginY = compas_margin_y + 
						(config.getDistanciaLineasPentagrama() * 4 + 
								config.getDistanciaPentagramas()) * (pentagrama - 1);

				switch (alteracion) {
					case 0:
						Clave clave = new Clave();
						clave.setImagenClave(obtenerImagenDeClave(claveByte));
						clave.setX(compas_margin_x);
						clave.setY(marginY + obtenerPosicionYDeClave(claveByte));
						compas.addClave(clave);
						
						clavesActuales.add(claveByte);
						break;

					case 1:
						posicionesOctavarium[0] = compas_margin_x + x_position;
						buscandoOctavarium = true;
						break;

					case -1:
						posicionesOctavarium[1] = compas_margin_x + x_position;
						break;

					default: 
						break;
				}

				//  Se asume lo de abajo
				if (alteracion == 0) claveNormalTratada = true;
			}

			//  Se asume que en un mismo compás nunca habrá una clave
			//  y un octavarium al mismo tiempo. Si eso ocurriera, las
			//  claves normales deberían mover la variable, pero los
			//  octavarium no
			if (claveNormalTratada) compas_margin_x += config.getAnchoClaves();
		}
	}
	
	private void calcularDynamics(Compas compas) {
		ElementoGrafico dynamics = compas.getDynamics();
		byte location = dynamics.getValue(0);
		byte intensidadByte = dynamics.getValue(1);
		int posicion = calcularPosicionX(dynamics.getPosition());

		Intensidad intensidad = new Intensidad();
		intensidad.setImagen(obtenerImagenDeIntensidad(intensidadByte));
		intensidad.setX(compas_margin_x + posicion);
		intensidad.setY(obtenerPosicionYDeElementoGrafico(1, location));

		compas.setIntensidad(intensidad);
	}
	
	private void calcularPedals(Compas compas) {
		if (compas.hayPedalStart()) {
			ElementoGrafico dynamics = compas.getPedalStart();
			byte location = dynamics.getValue(0);
			int posicion = calcularPosicionX(dynamics.getPosition());

			Pedal pedalInicio = new Pedal();
			pedalInicio.setImagen(pedalStart);
			pedalInicio.setX(compas_margin_x + posicion);
			pedalInicio.setY(obtenerPosicionYDeElementoGrafico(2, location));
			compas.setPedalInicio(pedalInicio);
		}
		
		if (compas.hayPedalStop()) {
			ElementoGrafico dynamics = compas.getPedalStop();
			byte location = dynamics.getValue(0);
			int posicion = calcularPosicionX(dynamics.getPosition());

			Pedal pedalFin = new Pedal();
			pedalFin.setImagen(pedalStop);
			pedalFin.setX(compas_margin_x + posicion);
			pedalFin.setY(obtenerPosicionYDeElementoGrafico(2, location));
			compas.setPedalFin(pedalFin);
		}
	}
	
	private void calcularPosicionesDeCompas(Compas compas) {
		compas.setXIni(compas_margin_x);
		compas.setYIni(compas_margin_y);
		
		compas_margin_x += config.getMargenIzquierdoCompases();

		if (compas.hayClefs()) calcularClefs(compas);
		calcularTime(compas);
		if (compas.hayWords()) calcularWords(compas);
		if (compas.hayDynamics()) calcularDynamics(compas);
		if (compas.hayPedals()) calcularPedals(compas);
		
		compas.setXIniNotas(compas_margin_x);
		
		int distanciaX = calcularPosicionesDeNotas(compas);
		
		compas_margin_x += distanciaX;
		compas_margin_x += config.getMargenDerechoCompases();
		
		compas.setXFin(compas_margin_x);
		compas.setYFin(compas_margin_y + 
				config.getDistanciaLineasPentagrama() * 4 + 
				(config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama() * 4) * 
				(partitura.getStaves() - 1));
		
		if (compas.getXFin() > config.getXFinalPentagramas()) {
			moverCompasAlSiguienteRenglon(compas);
			
			ultimoCompas = compasActual - 1;
			reajustarCompases();
			primerCompas = compasActual;
		}
	}
	
	private void calcularPosicionesDeCompases() {
		int numCompases = partitura.getCompases().size();
		
		for (int i=0; i<numCompases; i++) {
			compasActual = i;
			calcularPosicionesDeCompas(partitura.getCompas(i));
		}
	}

	private int calcularPosicionesDeNota(Nota nota) {
		int posicionX = nota.getPosicion();
		int posicionY = 0;

		if (posicionX != -1) {
			posicionX = calcularPosicionX(posicionX);
			posicionY = calcularCabezaDeNota(nota, posicionX);
			
			nota.setX(compas_margin_x + posicionX);
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
			distanciaActualX = calcularPosicionesDeNota(notas.get(i));

			if (distanciaActualX > mayorDistanciaX) 
				mayorDistanciaX = distanciaActualX;
		}
		
		return mayorDistanciaX;
	}
	
	private int calcularPosicionX(int position) {
		return position * config.getUnidadDesplazamiento() / partitura.getDivisions();
	}
	
	private void calcularTime(Compas compas) {
		if (compas.hayTime()) {
			Tempo tempo = new Tempo();
			
			switch (compas.getTime().getValue(1)) {
				case 1:
					inicializarTempo(tempo, 3, 8);
					config.unidadDesplazamientoCorcheas();
					break;
				case 2:
					inicializarTempo(tempo, 4, 4);
					config.unidadDesplazamientoNegras();
					break;
				case 3:
					inicializarTempo(tempo, 2, 4);
					config.unidadDesplazamientoNegras();
					break;
				case 4:
					inicializarTempo(tempo, 7, 4);
					config.unidadDesplazamientoNegras();
					break;
				default:
					break;
			}
			
			compas.setTempo(tempo);
			tempoActual = tempo;
			compas_margin_x += config.getAnchoTempo();
		}
		else {
			compas.setTempo(clonarTempo(tempoActual));
		}
	}
	
	private void calcularWords(Compas compas) {
		Texto texto = new Texto();
		int posicionX = calcularPosicionX(compas.getWordsPosition());
		
		texto.setTexto(compas.getWordsString());
		texto.setX(compas_margin_x + posicionX);
		texto.setY(obtenerPosicionYDeElementoGrafico(3, compas.getWordsLocation()));
		
		compas.setTexto(texto);
	}

	//  Cada elemento de un ArrayList debe ser un objeto independiente. Esto
	//  significa que no se puede asignar un compás a un compás viejo directamente,
	//  ya que en tal caso el array lo considerará como el mismo objeto antiguo.
	//  Por eso hace falta esta función, que crea un nuevo objeto compás y le
	//  pasa todos los datos contenidos en el compás viejo
	private Compas clonarCompas(Compas compasViejo) {
		Compas compasNuevo = new Compas();
		
		ArrayList<Nota> notas = compasViejo.getNotas();
		int num = notas.size();
		for (int i=0; i<num; i++) {
			Nota nota = new Nota(
					notas.get(i).getStep(), notas.get(i).getOctava(), notas.get(i).getFiguracion(),
					notas.get(i).getBeam(), notas.get(i).getPlica(), notas.get(i).getVoz(),
					notas.get(i).getPentagrama(), notas.get(i).getFigurasGraficas(),
					notas.get(i).getPosicionArray()
					);
			compasNuevo.addNote(nota);
		}
		
		ArrayList<ElementoGrafico> barlines = compasViejo.getBarlines();
		num = barlines.size();
		for (int i=0; i<num; i++) {
			compasNuevo.addBarline(clonarElementoGrafico(barlines.get(i)));
		}
		
		ArrayList<ElementoGrafico> clefs = compasViejo.getClefs();
		num = clefs.size();
		for (int i=0; i<num; i++) {
			compasNuevo.addClef(clonarElementoGrafico(clefs.get(i)));
		}
		
		compasNuevo.setRepeatBegin(compasViejo.getRepeatBegin());
		compasNuevo.setRepeatEnd(compasViejo.getRepeatEnd());
		compasNuevo.setEndingBegin(compasViejo.getEndingBegin());
		compasNuevo.setEndingEnd(compasViejo.getEndingEnd());
		compasNuevo.setEndingDis(compasViejo.getEndingDis());
		
		compasNuevo.setDynamics(clonarElementoGrafico(compasViejo.getDynamics()));
		compasNuevo.setPedalStart(clonarElementoGrafico(compasViejo.getPedalStart()));
		compasNuevo.setPedalStop(clonarElementoGrafico(compasViejo.getPedalStop()));
		compasNuevo.setTime(clonarElementoGrafico(compasViejo.getTime()));
		compasNuevo.setWords(clonarElementoGrafico(compasViejo.getWords()));
		
		compasNuevo.setXIni(compasViejo.getXIni());
		compasNuevo.setXFin(compasViejo.getXFin());
		compasNuevo.setYIni(compasViejo.getYIni());
		compasNuevo.setYFin(compasViejo.getYFin());
		
		return compasNuevo;
	}
	
	private ElementoGrafico clonarElementoGrafico(ElementoGrafico elementoViejo) {
		if (elementoViejo == null) 
			return null;
		
		else {
			ElementoGrafico elementoNuevo = new ElementoGrafico();
			
			ArrayList<Byte> values = elementoViejo.getValues();
			int numValues = values.size();
			for (int i=0; i<numValues; i++) {
				byte value = elementoViejo.getValue(i);
				elementoNuevo.addValue(value);
			}
			
			elementoNuevo.setPosition(elementoViejo.getPosition());
			elementoNuevo.setX(elementoViejo.getX());
			
			return elementoNuevo;
		}
	}

	private Tempo clonarTempo(Tempo tempoViejo) {
		Tempo tempoNuevo = new Tempo();
		
		tempoNuevo.setDibujar(false);
		tempoNuevo.setNumerador(tempoViejo.getNumerador());
		tempoNuevo.setDenominador(tempoViejo.getDenominador());
		tempoNuevo.setX(tempoViejo.getX());
		tempoNuevo.setYNumerador(tempoViejo.getYNumerador());
		tempoNuevo.setYDenominador(tempoViejo.getYDenominador());
		
		return tempoNuevo;
	}
	
	public ArrayList<OrdenDibujo> crearOrdenesDeDibujo() {
		
		desenrollarRepeticiones();
		
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
	
	//  Cambiamos la estructura de la partitura para que las repeticiones y endings
	//  se muestren de corrido, de forma que no haya que dar saltos para leer la partitura
	private void desenrollarRepeticiones() {
		ArrayList<Compas> compases = partitura.getCompases();
		ArrayList<Compas> nuevosCompases = new ArrayList<Compas>();
		
		int repeticionInicio = 0;
		int repeticionFinal = 0;
		int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++) {
			nuevosCompases.add(clonarCompas(compases.get(i)));
			
			if (compases.get(i).getRepeatBegin()) {
				repeticionInicio = i;
				nuevosCompases.get(nuevosCompases.size() - 1).setRepeatBegin(false);
			}
			
			if (compases.get(i).getRepeatEnd()) {
				nuevosCompases.get(nuevosCompases.size() - 1).setRepeatEnd(false);
				
				repeticionFinal = i;
				if (compases.get(i).hayEnding1()) {
					repeticionFinal--;
					nuevosCompases.get(nuevosCompases.size() - 1).setEndingBegin(false);
					nuevosCompases.get(nuevosCompases.size() - 1).setEndingEnd(false);
				}
				
				for (int j=repeticionInicio; j<=repeticionFinal; j++) {
					nuevosCompases.add(clonarCompas(compases.get(j)));
					
					nuevosCompases.get(nuevosCompases.size() - 1).clearClefs();
					nuevosCompases.get(nuevosCompases.size() - 1).setTime(null);
				}
			}
		}
		
		partitura.setCompases(nuevosCompases);
	}

	private void inicializarTempo(Tempo tempo, int numerador, int denominador) {
		tempo.setDibujar(true);
		tempo.setNumerador(numerador);
		tempo.setDenominador(denominador);
		tempo.setX(compas_margin_x);
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
		
		if (compas.hayIntensidad()) {
			compas.getIntensidad().setX(compas.getIntensidad().getX() - distancia_x);
			compas.getIntensidad().setY(compas.getIntensidad().getY() + distancia_y);
		}
		
		if (compas.hayPedalInicio()) {
			compas.getPedalInicio().setX(compas.getPedalInicio().getX() - distancia_x);
			compas.getPedalInicio().setY(compas.getPedalInicio().getY() + distancia_y);
		}
		
		if (compas.hayPedalFin()) {
			compas.getPedalFin().setX(compas.getPedalFin().getX() - distancia_x);
			compas.getPedalFin().setY(compas.getPedalFin().getY() + distancia_y);
		}
		
		if (compas.hayTempo()) {
			compas.getTempo().setX(compas.getTempo().getX() - distancia_x);
			compas.getTempo().setYNumerador(compas.getTempo().getYNumerador() + distancia_y);
			compas.getTempo().setYDenominador(compas.getTempo().getYDenominador() + distancia_y);
		}
		
		if (compas.hayTexto()) {
			compas.getTexto().setX(compas.getTexto().getX() - distancia_x);
			compas.getTexto().setY(compas.getTexto().getY() + distancia_y);
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
			compas.getNota(i).setYOctavarium(compas.getNota(i).getYOctavarium() + distancia_y);
		}
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
					default:
						return compas_margin_y - config.getDistanciaLineasPentagrama() * 6;
				}
	
			//  Pedales
			case 2:
				switch (location) {
					case 4:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 + 
							config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama() * 8;
					default:
						return compas_margin_y + config.getDistanciaLineasPentagrama() * 4 + 
							config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama() * 8;
				}
	
			//  Texto
			case 3:
				switch (location) {
					case 1:
						return compas_margin_y - config.getDistanciaLineasPentagrama();
					default:
						return compas_margin_y - config.getDistanciaLineasPentagrama();
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
		if (octavarium > 0) octava--;
		
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
										case 24:
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
						config.getDistanciaLineasPentagramaMitad();
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
	            for (int j=0; j<numNotas; j++) {
	            	compas.getNota(j).setX(compas.getNota(j).getX() + anchoParaCadaCompas);
	            }
        	}
        }

        //  Segundo paso: reajustar posición de las notas
        for (int i=primerCompas; i<=ultimoCompas; i++) {
        	Compas compas = partitura.getCompas(i);
        	ArrayList<Integer> xDelCompas = saberNumeroDeElementosDeCompas(compas);
        	
        	int lastX = saberXMasGrande(compas);
        	int anchoADistribuir = compas.getXFin() - config.getMargenDerechoCompases() - lastX;
        	
        	//  El primer elemento no lo vamos a mover, de ahí el -1
        	int numElementos = xDelCompas.size() - 1;
        	
        	int anchoPorNota = 0;
        	if (numElementos > 0)
        		anchoPorNota = anchoADistribuir / numElementos;
        	
        	//  A cada elemento se le suma una distancia cada vez
        	//  mayor, ya que de lo contrario sólo estaríamos
        	//  desplazándolos todos pero manteniéndolos a la misma
        	//  distancia entre sí mismos que antes
        	ArrayList<Nota> notas = compas.getNotas();
        	int numNotas = notas.size();
        	int multiplicador = 0;
        	for (int j=0;j<numNotas;j++) {
        		
        		//  Las X contenidas en el array xDelCompas están en orden
        		//  de menor a mayor. Esto permite asociar automáticamente
        		//  el índice de cada posición X con el multiplicador
        		//  necesario para reajustar el elemento con ese valor de x
        		multiplicador = xDelCompas.indexOf(notas.get(j).getX());
    			notas.get(j).setX(notas.get(j).getX() + anchoPorNota * multiplicador);
        	}
        }
	}
	
	//  Devuelve un array con cada valor de X de cada elemento
	//  del compás. Por elemento se entiende cualquier nota o
	//  acorde que ocupe una posición X única en el compás
	private ArrayList<Integer> saberNumeroDeElementosDeCompas(Compas compas) {
		ArrayList<Integer> xEncontradas = new ArrayList<Integer>();
		
		ArrayList<Nota> notas = compas.getNotas();
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			if (!xEncontradas.contains(notas.get(i).getX()))
				xEncontradas.add(notas.get(i).getX());
		
		Collections.sort(xEncontradas);
		return xEncontradas;
	}
	
	//  Devuelve la posición X de la nota más cercana al margen derecho
	private int saberXMasGrande(Compas compas) {
		int xMasGrande = 0;
		
		ArrayList<Nota> notas = compas.getNotas();
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++) {
			if (xMasGrande < notas.get(i).getX())
				xMasGrande = notas.get(i).getX();
		}
		
		return xMasGrande;
	}
	
	/*
	 * 
	 * FUNCIONES DE DIBUJO
	 * 
	 */

	//  Esta implementación está ignorando las plicas dobles
	private int colocarBeamsALaMismaAltura(boolean haciaArriba) {
		int numBeams = beams.size();
		int y_beams = haciaArriba ? Integer.MAX_VALUE : 0;
		
		//  Nota que no es de gracia
		boolean notaNormal = false;

		for (int i=0; i<numBeams; i++) {
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
	private void dibujarBeams(int y_beams, boolean haciaArriba) {
		int numBeams = beams.size();
		int distancia_beams = haciaArriba ? config.getDistanciaEntreBeams() : 
			- config.getDistanciaEntreBeams();

		for (int i=0; i<numBeams; i++) {
			OrdenDibujo ordenDibujo;
			
			int indCompasAnt = beams.get(i).getCompas();
			int indNotaAnt = beams.get(i).getNota();

			if (i == numBeams - 1) {
				
				//  Gestión de hooks en la última nota. Por ahora sólo se está controlando un caso
				if (partitura.getCompas(i).getNota(i).getBeam() == 4) {
					
					int x_last_beam = partitura.getCompas(numBeams - 1).getNota(numBeams - 1).getX();

					ordenDibujo = new OrdenDibujo();
					ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
					ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, config.getAnchoBeams());
					ordenDibujo.setX1(x_last_beam);
					ordenDibujo.setY1(y_beams + distancia_beams * 2);
					ordenDibujo.setX2(x_last_beam - config.getAnchoHooks());
					ordenDibujo.setY2(y_beams + distancia_beams * 2);
					ordenesDibujo.add(ordenDibujo);
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
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, config.getAnchoBeams());
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, config.getAnchoBeams());
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
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, config.getAnchoBeams());
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams);
						ordenesDibujo.add(ordenDibujo);
						break;
	
					case 5:
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, config.getAnchoBeams());
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, config.getAnchoBeams());
						ordenDibujo.setX1(x_ant_beams);
						ordenDibujo.setY1(y_beams + distancia_beams);
						ordenDibujo.setX2(x_sig_beams);
						ordenDibujo.setY2(y_beams + distancia_beams);
						ordenesDibujo.add(ordenDibujo);
	
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, config.getAnchoBeams());
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
		}
		
		beams.clear();
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
		
		if (!nota.acorde()) y_anterior = nota.getY();

		ordenesDibujo.add(ordenDibujo);
		
		dibujarOctavarium(nota);
	}
	
	private void dibujarClaves(Compas compas) {
		ArrayList<Clave> claves = compas.getClaves();
		int numClaves = claves.size();
		
		for (int i=0; i<numClaves; i++) {
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
			ordenDibujo.setImagen(claves.get(i).getImagenClave());
			ordenDibujo.setX1(claves.get(i).getX());
			ordenDibujo.setY1(claves.get(i).getY());
			ordenesDibujo.add(ordenDibujo);
		}
	}
	
	private void dibujarCompases() {
		ArrayList<Compas> compases = partitura.getCompases();
		int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++) {
			compasActual = i;
			
			dibujarLineasDePentagramaDeCompas(compases.get(i));
			dibujarClaves(compases.get(i));
			
			if (compases.get(i).hayIntensidad()) dibujarIntensidad(compases.get(i));
			if (compases.get(i).hayPedales()) dibujarPedales(compases.get(i));
			if (compases.get(i).hayTempo()) dibujarTempo(compases.get(i));
			if (compases.get(i).hayTexto()) dibujarTexto(compases.get(i));
			
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
	
	private void dibujarFiguraGrafica(Nota nota, byte figura, int posicionX, int posicionY, int y_beams) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();

		switch (figura) {
			case 3:
				x_ini_tresillo = posicionX;
				break;

			case 4:
				int margenTresillo = nota.haciaArriba() ? 
						- config.getYTresilloArriba() : config.getYTresilloAbajo();
				int x_tresillo = (posicionX + x_ini_tresillo) / 2;
				if (nota.haciaArriba()) x_tresillo += config.getXTresillo();

				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTresillo());
				ordenDibujo.setTexto("3");
				ordenDibujo.setX1(x_tresillo);
				ordenDibujo.setY1(y_beams + margenTresillo);
				ordenesDibujo.add(ordenDibujo);
				break;

			case 6:
				x_ini_slide = posicionX;
				y_ini_slide = posicionY;
				break;
				
			case 7:
				ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
				ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
				ordenDibujo.setX1(x_ini_slide + config.getAnchoCabezaNota());
				ordenDibujo.setY1(y_ini_slide + config.getMitadCabezaNotaVertical());
				ordenDibujo.setX2(posicionX);
				ordenDibujo.setY2(posicionY + config.getMitadCabezaNotaVertical());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 8:
				if (nota.haciaArriba()) {
					ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
					ordenDibujo.setRadius(config.getRadioStaccatos());
					ordenDibujo.setX1(posicionX + config.getXStaccato());
					ordenDibujo.setY1(posicionY + config.getYStaccatoArriba());
					ordenesDibujo.add(ordenDibujo);
				}
				else {
					ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
					ordenDibujo.setRadius(config.getRadioStaccatos());
					ordenDibujo.setX1(posicionX + config.getXStaccato());
					ordenDibujo.setY1(posicionY - config.getYStaccatoAbajo());
					ordenesDibujo.add(ordenDibujo);
				}
				break;

			case 9:
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTapping());
				ordenDibujo.setTexto("T");
				ordenDibujo.setX1(posicionX);
				ordenDibujo.setY1(posicionY - config.getYTapping());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 10:
				ligadurasInicio.add(new IndiceNota(compasActual, notaActual, nota.getLigadura()));
				break;
				
			case 11:
				int indLigadura = encontrarIndiceLigadura(nota.getLigadura());
				dibujarLigadura(indLigadura, posicionX);
				break;
			
			case 12:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(sharp);

				ordenDibujo.setX1(posicionX - config.getXAccidental());
				if (nota.desplazadaALaIzquierda()) 
					ordenDibujo.setX1(ordenDibujo.getX1() - config.getAnchoCabezaNota());
				if (nota.desplazadaALaDerecha())
					ordenDibujo.setX1(ordenDibujo.getX1() + config.getAnchoCabezaNota());

				ordenDibujo.setY1(posicionY - config.getYAccidental());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 13:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(flat);

				ordenDibujo.setX1(posicionX - config.getXAccidental());
				if (nota.desplazadaALaIzquierda()) 
					ordenDibujo.setX1(ordenDibujo.getX1() - config.getAnchoCabezaNota());
				if (nota.desplazadaALaDerecha())
					ordenDibujo.setX1(ordenDibujo.getX1() + config.getAnchoCabezaNota());

				ordenDibujo.setY1(posicionY - config.getYAccidentalFlat());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 14:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(natural);

				ordenDibujo.setX1(posicionX - config.getXAccidental());
				if (nota.desplazadaALaIzquierda()) 
					ordenDibujo.setX1(ordenDibujo.getX1() - config.getAnchoCabezaNota());
				if (nota.desplazadaALaDerecha())
					ordenDibujo.setX1(ordenDibujo.getX1() + config.getAnchoCabezaNota());

				ordenDibujo.setY1(posicionY - config.getYAccidental());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 15:
				ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
				ordenDibujo.setRadius(config.getRadioPuntillos());
				ordenDibujo.setX1(posicionX + config.getXPuntillo());
				ordenDibujo.setY1(posicionY + config.getMitadCabezaNotaVertical());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 16:
				ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
				ordenDibujo.setRadius(config.getRadioPuntillos());
				ordenDibujo.setX1(posicionX + config.getXPuntillo());
				ordenDibujo.setY1(posicionY + config.getYPuntilloArriba());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 17:
				ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
				ordenDibujo.setRadius(config.getRadioPuntillos());
				ordenDibujo.setX1(posicionX + config.getXPuntillo());
				ordenDibujo.setY1(posicionY + config.getYPuntilloAbajo());
				ordenesDibujo.add(ordenDibujo);
				break;

			case 22:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(bendrelease);
				ordenDibujo.setX1(posicionX);
				ordenDibujo.setY1(posicionY + config.getYBend());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 26:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(vibrato);
				ordenDibujo.setX1(posicionX);
				ordenDibujo.setY1(posicionY + config.getYBend());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 27:
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraPalmMute());
				ordenDibujo.setTexto("P.M.");
				ordenDibujo.setX1(posicionX);
				ordenDibujo.setY1(posicionY - config.getYPalmMute());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 28:
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraPalmMute());
				ordenDibujo.setTexto("P.M.");
				ordenDibujo.setX1(posicionX);
				ordenDibujo.setY1(posicionY + config.getYPalmMute());
				ordenesDibujo.add(ordenDibujo);
				break;
			
			case 29:
				ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
				ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraTapping());
				ordenDibujo.setTexto("T");
				ordenDibujo.setX1(posicionX);
				ordenDibujo.setY1(posicionY + config.getYTapping());
				ordenesDibujo.add(ordenDibujo);
				
			default:
				break;
		}
	}
	
	private void dibujarFigurasGraficasDeNota(Nota nota, int y_beams) {
		ArrayList<Byte> figurasGraficas = nota.getFigurasGraficas();
		int numFiguras = figurasGraficas.size();

		for (int i=0; i<numFiguras; i++) {
			
			//  Las ligaduras requieren un byte extra que indica su número
			if (esLigadura(figurasGraficas, i)) {
				nota.setLigadura(figurasGraficas.get(i + 1));	
				dibujarFiguraGrafica(nota, figurasGraficas.get(i++), nota.getX(), nota.getY(), y_beams);
			}
			
			else {
				dibujarFiguraGrafica(nota, figurasGraficas.get(i), nota.getX(), nota.getY(), y_beams);
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
	
	private void dibujarLigadura(int indLigadura, int xFinal) {
		int compasNotaInicio = ligadurasInicio.get(indLigadura).getCompas();
		int notaInicio = ligadurasInicio.get(indLigadura).getNota();
		
		Nota nota = partitura.getCompas(compasNotaInicio).getNota(notaInicio);
		int xInicio = nota.getX();
		int y = nota.getY();
		
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_ARC);
		ordenDibujo.setPaint(PaintOptions.SET_STYLE_STROKE, 0);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
		
		if (xInicio < xFinal) {
			RectF rectf = new RectF(xInicio + config.getAnchoCabezaNota() +
					config.getXLigaduras(), y - config.getYLigaduras(), 
					xFinal - config.getXLigaduras(), y + config.getAlturaArcoLigaduras());
			ordenDibujo.setRectF(rectf);
		}
		
		//  La ligadura es con una nota de un compás
		//  que fue desplazado hacia abajo porque no cabía,
		//  así que la dibujamos al frente
		else {
			Compas compas = partitura.getCompas(compasNotaInicio);

			RectF rectf = new RectF(xInicio + config.getAnchoCabezaNota() + 
					config.getXLigaduras(), y - config.getYLigaduras(), 
					compas.getXFin(), y + config.getAlturaArcoLigaduras());
			ordenDibujo.setRectF(rectf);
		}
		
		ordenesDibujo.add(ordenDibujo);
		ligadurasInicio.remove(indLigadura);
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
			if (notas.get(i).getBeam() > 0) 
				y_beams = gestionarBeams(notas.get(i));
			else
				if (dibujarPlicaDeNota(notas.get(i), 0))
					dibujarCorcheteDeNota(notas.get(i));
			
			dibujarFigurasGraficasDeNota(notas.get(i), y_beams);
			
			dibujarLineasFueraDelPentagrama(notas.get(i), compas.getYIni());
		}
	}
	
	private void dibujarOctavarium(Nota nota) {
		if (nota.getOctavarium() == 1) {
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
			ordenDibujo.setImagen(octavariumImage);
			ordenDibujo.setX1(nota.getX());
			ordenDibujo.setY1(nota.getYOctavarium());
			ordenesDibujo.add(ordenDibujo);
		}
		
		if (nota.getOctavarium() == 2) {
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
			ordenDibujo.setRadius(config.getRadioOctavarium());
			ordenDibujo.setX1(nota.getX() + config.getXOctavarium());
			ordenDibujo.setY1(nota.getYOctavarium());
			ordenesDibujo.add(ordenDibujo);
		}
	}
	
	private void dibujarPedales(Compas compas) {
		if (compas.hayPedalInicio()) {
			Pedal pedalInicio = compas.getPedalInicio();
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
			ordenDibujo.setImagen(pedalInicio.getImagen());
			ordenDibujo.setX1(pedalInicio.getX());
			ordenDibujo.setY1(pedalInicio.getY());
			ordenesDibujo.add(ordenDibujo);
		}
		
		if (compas.hayPedalFin()) {
			Pedal pedalFin = compas.getPedalFin();
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
			ordenDibujo.setImagen(pedalFin.getImagen());
			ordenDibujo.setX1(pedalFin.getX());
			ordenDibujo.setY1(pedalFin.getY());
			ordenesDibujo.add(ordenDibujo);
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

		return nota.getBeam() == 0;
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
	
	private void dibujarTexto(Compas compas) {
		Texto texto = compas.getTexto();
		
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraWords());
		ordenDibujo.setTexto(texto.getTexto());
		ordenDibujo.setX1(texto.getX());
		ordenDibujo.setY1(texto.getY());
		ordenesDibujo.add(ordenDibujo);
	}
	
	//  Busca, en el array de ligaduras de inicio, el índice
	//  del elemento que contiene el inicio de esta ligadura
	private int encontrarIndiceLigadura(byte ligadura) {
		int numLigaduras = ligadurasInicio.size();
		int indice = -1;
		
		for (int i=0; i<numLigaduras; i++) {
			if (ligadurasInicio.get(i).getLigadura() == ligadura) {
				indice = i;
				break;
			}
		}
		
		return indice;
	}
	
	private boolean esLigadura(ArrayList<Byte> figuras, int indFigura) {
		return (figuras.get(indFigura) == 10) || (figuras.get(indFigura) == 11);
	}
	
	//  Guarda las posiciones de las notas que tienen beams para,
	//  más adelante, dibujar sus plicas a la altura del beam
	private int gestionarBeams(Nota nota) {
		boolean dibujarBeams = false;
		
		if (nota.getBeam() > 0) {
			IndiceNota beam = new IndiceNota(compasActual, notaActual, (byte) 0);
			beams.add(beam);
			if ((nota.getBeam() == 1) || (nota.getBeam() == 4)) 
				dibujarBeams = true;
		}
		
		int y_beams = 0;
		if (dibujarBeams) {
			boolean haciaArriba = nota.haciaArriba();
			y_beams = colocarBeamsALaMismaAltura(haciaArriba);
			dibujarBeams(y_beams, haciaArriba);
		}
		
		return y_beams;
	}
}