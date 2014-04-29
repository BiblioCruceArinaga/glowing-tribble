package com.rising.drawing;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DrawingMethods {
	
	private boolean isValid = false;

	private ArrayList<OrdenDibujo> ordenesDibujo = new ArrayList<OrdenDibujo>();
	
	//  Partitura y sus datos "físicos" (límites, densidad de pantalla, etc.)
	private Partitura partitura;
	private Config config;
	
	//  Variables para la gestión y el tratamiento dinámico de los múltiples compases
	private int compas_margin_x = 0;
	private int compas_margin_y = 0;
	private byte[] claves;

	//  Variables para la gestión de las múltiples notas
	private boolean octavarium = false;
	private int y_anterior = 0;
	private ArrayList<Beam> beams = new ArrayList<Beam>();
	private boolean dibujarBeams = false;
	
	//  Bitmaps
	private Bitmap trebleclef = null;
	private Bitmap bassclef = null;
	private Bitmap mezzoforte = null;
	private Bitmap forte = null;
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
	private Bitmap bend = null;
	private Bitmap octavariumImage = null;
	
	public DrawingMethods(Partitura partitura, Config config, Resources resources) {
		if (config.supported()) {
		
			this.partitura = partitura;
			this.config = config;
			
			compas_margin_x = config.getXInicialPentagramas();
			compas_margin_y = config.getMargenSuperior();
			
			//  En el futuro, estos valores irán cambiando conforme se vayan
			//  encontrando claves durante la lectura de la partitura
			claves = new byte[partitura.getStaves()];
			claves[0] = 1;
			claves[1] = 2;

			trebleclef = BitmapFactory.decodeResource(resources, R.drawable.trebleclef);
			bassclef = BitmapFactory.decodeResource(resources, R.drawable.bassclef);
			mezzoforte = BitmapFactory.decodeResource(resources, R.drawable.mezzoforte);
			forte = BitmapFactory.decodeResource(resources, R.drawable.forte);
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
			bend = BitmapFactory.decodeResource(resources, R.drawable.bend);
			octavariumImage = BitmapFactory.decodeResource(resources, R.drawable.octavarium);
			
			isValid = true;
		}
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public ArrayList<OrdenDibujo> crearOrdenesDeDibujo() {
		
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
		
		crearOrdenesDeCompases();
		return ordenesDibujo;
	}
	
	private void crearOrdenesDeCompases() {
		ArrayList<Compas> compases = partitura.getCompases();
		int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++)
			crearOrdenesDeCompas(compases.get(i));
	}
	
	private void crearOrdenesDeCompas(Compas compas) {
		compas.setXIni(compas_margin_x);
		compas.setYIni(compas_margin_y);

		/*
		dibujarBarlines();
		dibujarRepeticiones();
		dibujarClaves(compas.getClaves());
		dibujarTempo();
		*/
		
		compas_margin_x += config.getMargenIzquierdoCompases();
		
		ArrayList<Nota> notas = compas.getNotas();
		int numNotas = notas.size();
		int mayorDistanciaX = 0;
		int distanciaActualX = 0;
		int primeraOrden = ordenesDibujo.size();
		for (int i=0; i<numNotas; i++) {
			distanciaActualX = crearOrdenesDeNota(notas.get(i));
			
			if (distanciaActualX > mayorDistanciaX) 
				mayorDistanciaX = distanciaActualX;
		}
		int ultimaOrden = ordenesDibujo.size();
		
		/*
		dibujarIntensidad();
		dibujarPedales();
		dibujarTexto();
		dibujarEndings();
		*/

		//  Final de este compás (e inicio del siguiente)
		compas_margin_x += mayorDistanciaX;
		compas_margin_x += config.getMargenDerechoCompases();
		
		compas.setXFin(compas_margin_x);
		compas.setYFin(compas_margin_y + 
				config.getDistanciaLineasPentagrama() * 4 + 
				(config.getDistanciaPentagramas() + config.getDistanciaLineasPentagrama() * 4) * 
				(partitura.getStaves() - 1));

		if (compas.getXFin() > config.getXFinalPentagramas()) {
			moverCompasAlSiguienteRenglon(compas, primeraOrden, ultimaOrden);
		}
		
		dibujarLineasDePentagramaDeCompas(compas);
	}
	
	private int crearOrdenesDeNota(Nota nota) {
		int posicionX = nota.getPosicion();
		int posicionY = 0;
		
		if (posicionX != -1) {
			posicionX = distanciaUnidadPosicion(posicionX);
			
			posicionY = dibujarCabezaDeNota(nota, posicionX);
			if (dibujarPlicaDeNota(nota, posicionX, posicionY)) {
				dibujarCorcheteDeNota(nota, posicionX, posicionY);
			}
			dibujarFigurasGraficasDeNota(nota, posicionX, posicionY);
			
			if (dibujarBeams) {
				boolean haciaArriba = nota.haciaArriba();
				int y_beams = colocarBeamsALaMismaAltura(haciaArriba);
				dibujarBeams(y_beams, haciaArriba);
			}
		}
		
		return posicionX;
	}
	
	private void dibujarFigurasGraficasDeNota(Nota nota, int posicionX, int posicionY) {
		ArrayList<Byte> figurasGraficas = nota.getFigurasGraficas();
		int numFiguras = figurasGraficas.size();
		
		for (int i=0; i<numFiguras; i++) 
			dibujarFiguraGrafica(nota, figurasGraficas.get(i), posicionX, posicionY);
	}

	private void dibujarFiguraGrafica(Nota nota, byte figura, int posicionX, int posicionY) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		
		switch (figura) {
			case 5:
				break;
				
			case 8:
				if (nota.haciaArriba()) {
					ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
					ordenDibujo.setRadius(config.getRadioStaccatos());
					ordenDibujo.setX1(compas_margin_x + posicionX + config.getXStaccato());
					ordenDibujo.setY1(posicionY + config.getYStaccatoArriba());
					ordenesDibujo.add(ordenDibujo);
				}
				else {
					ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
					ordenDibujo.setRadius(config.getRadioStaccatos());
					ordenDibujo.setX1(compas_margin_x + posicionX + config.getXStaccato());
					ordenDibujo.setY1(posicionY - config.getYStaccatoAbajo());
					ordenesDibujo.add(ordenDibujo);
				}
				break;
				
			case 9:
				break;
				
			case 12:
				ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
				ordenDibujo.setImagen(sharp);
				
				ordenDibujo.setX1(compas_margin_x + posicionX - config.getXAccidental());
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
				
				ordenDibujo.setX1(compas_margin_x + posicionX - config.getXAccidental());
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
				
				ordenDibujo.setX1(compas_margin_x + posicionX - config.getXAccidental());
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
				ordenDibujo.setX1(compas_margin_x + posicionX + config.getXPuntillo());
				ordenDibujo.setY1(posicionY + config.getMitadCabezaNota());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 16:
				ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
				ordenDibujo.setRadius(config.getRadioPuntillos());
				ordenDibujo.setX1(compas_margin_x + posicionX + config.getXPuntillo());
				ordenDibujo.setY1(posicionY + config.getYPuntilloArriba());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 17:
				ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
				ordenDibujo.setRadius(config.getRadioPuntillos());
				ordenDibujo.setX1(compas_margin_x + posicionX + config.getXPuntillo());
				ordenDibujo.setY1(posicionY + config.getYPuntilloAbajo());
				ordenesDibujo.add(ordenDibujo);
				break;
				
			case 20:
			case 21:
			case 22:
			case 23:
				break;
			case 26:
				break;
			default:
				break;
		}
	}
	
	private void dibujarCorcheteDeNota(Nota nota, int posicionX, int posicionY) {
		
		//  En este caso no significa que la nota no pueda formar
		//  parte de un acorde. Significa que si forma parte de un
		//  acorde, su corchete ya fue dibujado, por lo que no hay
		//  que dibujarlo de nuevo
		if (!nota.acorde() && !nota.silencio()) {
			int anchoCabezaNota = nota.notaDeGracia() ? 
					config.getAnchoCabezaNotaGracia() : config.getAnchoCabezaNota();
			int longitudPlica = nota.notaDeGracia() ? 
					config.getLongitudPlicaNotaGracia() : config.getLongitudPlica();
			int anchoCorchete = nota.notaDeGracia() ? 
					config.getLargoImagenCorcheteGracia() : config.getLargoImagenCorchete();
					
			int x;
			int y;
			if (nota.haciaArriba()) {
				x = compas_margin_x + posicionX + anchoCabezaNota;
				y = posicionY - longitudPlica;
			}
			else {
				x = compas_margin_x + posicionX;
				y = posicionY + longitudPlica - anchoCorchete;
			}		
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			switch (nota.getFiguracion()) {
				case 8:
					ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
					ordenDibujo.setImagen(obtenerCorcheteDeNota(nota));
					ordenDibujo.setX1(x);
					ordenDibujo.setY1(y);
					ordenesDibujo.add(ordenDibujo);
					
					if (nota.tieneSlash()) {
						ordenDibujo = new OrdenDibujo();
						ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
						ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 2);
						ordenDibujo.setX1(x + config.getXInicioSlash());
						ordenDibujo.setY1(y + config.getYInicioSlash());
						ordenDibujo.setX2(x - config.getXFinSlash());
						ordenDibujo.setY2(y + config.getYFinSlash());
						ordenesDibujo.add(ordenDibujo);
					}
					break;
				case 7:
					break;
				case 6:
					break;
				case 5:
					break;
				default:
					break;
			}
		}
	}
	
	//  Esta implementación está ignorando las plicas dobles
	private int colocarBeamsALaMismaAltura(boolean haciaArriba) {
		int numBeams = beams.size();
		int y_beams = haciaArriba ? Integer.MAX_VALUE : 0;
		
		for (int i=0; i<numBeams; i++) {
			if (haciaArriba) {
				if (y_beams > ordenesDibujo.get(beams.get(i).getIndex()).getY2())
					y_beams = ordenesDibujo.get(beams.get(i).getIndex()).getY2();
			}
			else {
				if (y_beams < ordenesDibujo.get(beams.get(i).getIndex()).getY2())
					y_beams = ordenesDibujo.get(beams.get(i).getIndex()).getY2();
			}
		}
		
		for (int i=0; i<numBeams; i++)
			ordenesDibujo.get(beams.get(i).getIndex()).setY2(y_beams);
		
		return y_beams;
	}
	
	//  Esta implementación está ignorando las plicas dobles
	private void dibujarBeams(int y_beams, boolean haciaArriba) {
		int numBeams = beams.size();
		int distancia_beams = haciaArriba ? config.getDistanciaEntreBeams() : 
			- config.getDistanciaEntreBeams();
		
		for (int i=0; i<numBeams - 1; i++) {
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			int x_ant_beams = ordenesDibujo.get(beams.get(i).getIndex()).getX1();
			int x_sig_beams = ordenesDibujo.get(beams.get(i + 1).getIndex()).getX1();

			switch (beams.get(i).getBeam()) {
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

		//  Gestión de hooks en la última nota
		if (beams.get(numBeams - 1).getBeam() == 4) {
			int x_last_beam = ordenesDibujo.get(beams.get(numBeams - 1).getIndex()).getX1();
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, config.getAnchoBeams());
			ordenDibujo.setX1(x_last_beam);
			ordenDibujo.setY1(y_beams + distancia_beams * 2);
			ordenDibujo.setX2(x_last_beam - config.getAnchoHooks());
			ordenDibujo.setY2(y_beams + distancia_beams * 2);
			ordenesDibujo.add(ordenDibujo);
		}
		
		beams.clear();
		dibujarBeams = false;
	}

	private int dibujarCabezaDeNota(Nota nota, int posicion) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
		ordenDibujo.setImagen(obtenerImagenDeNota(nota));
		
		int desplazamiento = 0;
		if (nota.desplazadaALaIzquierda()) desplazamiento -= config.getAnchoCabezaNota();
		if (nota.desplazadaALaDerecha()) desplazamiento += config.getAnchoCabezaNota();
		ordenDibujo.setX1(compas_margin_x + posicion + desplazamiento);
		
		int y = obtenerPosicionYDeNota(nota, claves[nota.getPentagrama() - 1], partitura.getInstrument());
		if (nota.notaDeGracia()) y += config.getMargenNotaGracia();
		ordenDibujo.setY1(y);
		if (!nota.acorde()) y_anterior = y;
		
		ordenesDibujo.add(ordenDibujo);
		dibujarLineasFueraDelPentagrama(compas_margin_x + posicion, y, nota.getPentagrama());
		return y;
	}
	
	//  Las notas que se dibujan fuera del pentagrama requieren que se dibujen 
	//  unas pequeñas líneas debajo (o encima) que sirvan de orientación
	private void dibujarLineasFueraDelPentagrama(int x, int y, int pentagrama) {
		int y_margin_custom = compas_margin_y + 
				(config.getDistanciaLineasPentagrama() * 4 + 
						config.getDistanciaPentagramas()) * (pentagrama - 1);			

		if (y == y_margin_custom + config.getDistanciaLineasPentagrama() * 4 + 
				config.getDistanciaLineasPentagramaMitad()) {
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
		}
		if (y == y_margin_custom + config.getDistanciaLineasPentagrama() * 5) {
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
		}
		if (y == y_margin_custom + config.getDistanciaLineasPentagrama() * 5 + 
				config.getDistanciaLineasPentagramaMitad()) {

		}
		if (y == y_margin_custom + config.getDistanciaLineasPentagrama() * 6) {

		}
		if (y == y_margin_custom + config.getDistanciaLineasPentagrama() * 6 + 
				config.getDistanciaLineasPentagramaMitad()) {

			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom +
					config.getDistanciaLineasPentagrama() * 5);
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom +
					config.getDistanciaLineasPentagrama() * 6);
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom +
					config.getDistanciaLineasPentagrama() * 7);
		}
		if (y == y_margin_custom + config.getDistanciaLineasPentagrama() * 7) {

		}
		if (y == y_margin_custom + config.getDistanciaLineasPentagrama() * 7 + 
				config.getDistanciaLineasPentagramaMitad()) {

		}
		if (y == y_margin_custom + config.getDistanciaLineasPentagrama() * 8) {

		}
		if (y == y_margin_custom - config.getDistanciaLineasPentagrama() - 
				config.getDistanciaLineasPentagramaMitad()) {
			
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama());
		}
		if (y == y_margin_custom - config.getDistanciaLineasPentagrama() * 2) {
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama());
		}
		if (y == y_margin_custom - config.getDistanciaLineasPentagrama() * 2 - 
				config.getDistanciaLineasPentagramaMitad()) {
			
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama());
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 2);
		}
		if (y == y_margin_custom - config.getDistanciaLineasPentagrama() * 3) {
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama());
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 2);
		}
		if (y == y_margin_custom - config.getDistanciaLineasPentagrama() * 3 - 
				config.getDistanciaLineasPentagramaMitad()) {
			
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama());
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 2);
			crearOrdenDeDibujoLineaFueraDePentagrama(x, y_margin_custom - 
					config.getDistanciaLineasPentagrama() * 3);
		}
		if (y == y_margin_custom - config.getDistanciaLineasPentagrama() * 4) {

		}
		if (y == y_margin_custom - config.getDistanciaLineasPentagrama() * 4 - 
				config.getDistanciaLineasPentagramaMitad()) {
			
		}
	}
	
	//  Función auxiliar para las líneas de fuera del pentagrama
	private void crearOrdenDeDibujoLineaFueraDePentagrama(int x, int y) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
		ordenDibujo.setX1(x - config.getMargenAnchoCabezaNota());
		ordenDibujo.setY1(y);
		ordenDibujo.setX2(x + config.getAnchoCabezaNota() + config.getMargenAnchoCabezaNota());
		ordenDibujo.setY2(y);
		ordenesDibujo.add(ordenDibujo);
	}
	
	//  Esta implementación está ignorando las plicas dobles
	private boolean dibujarPlicaDeNota(Nota nota, int posicionX, int posicionY) {
		if (nota.tienePlica()) {
			int mitadCabezaNota = nota.notaDeGracia() ? 
					config.getMitadCabezaNotaGracia() : config.getMitadCabezaNota();
			int anchoCabezaNota = nota.notaDeGracia() ? 
					config.getAnchoCabezaNotaGracia() : config.getAnchoCabezaNota();
			int longitudPlica = nota.notaDeGracia() ? 
					config.getLongitudPlicaNotaGracia() : config.getLongitudPlica();
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
			ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 1);
			ordenDibujo.setY1(posicionY + mitadCabezaNota);
			
			if (nota.getPlica() == 1) {
				ordenDibujo.setX1(compas_margin_x + posicionX + anchoCabezaNota);
				ordenDibujo.setX2(compas_margin_x + posicionX + anchoCabezaNota);
				
				if (nota.acorde()) ordenDibujo.setY2(y_anterior);
				else ordenDibujo.setY2(posicionY - longitudPlica);
			}
			if (nota.getPlica() == 2) {
				ordenDibujo.setX1(compas_margin_x + posicionX);
				ordenDibujo.setX2(compas_margin_x + posicionX);
				
				if (nota.acorde()) ordenDibujo.setY2(y_anterior + longitudPlica);
				else ordenDibujo.setY2(posicionY + mitadCabezaNota + longitudPlica);
			}
			
			ordenesDibujo.add(ordenDibujo);

			if (nota.getBeam() > 0) {
				Beam beam = new Beam(nota.getBeam(), ordenesDibujo.size() - 1);
				beams.add(beam);
				if ((nota.getBeam() == 1) || (nota.getBeam() == 4)) dibujarBeams = true;
			}
		}
		
		return nota.getBeam() == 0;
	}
	
	private void dibujarClaves(ArrayList<ElementoGrafico> claves) {
		int numClefs = claves.size();
		int x_position = -1;
		int numClaves = -1;
		
		for (int i=0; i<numClefs; i++) {
			x_position = distanciaUnidadPosicion(claves.get(i).getPosition());
			numClaves = claves.get(i).getValue(1);
			
			for (int j=0; j<numClaves; j++) {
				byte pentagrama = claves.get(i).getValue(2 + 3 * j);
				byte clave = claves.get(i).getValue(3 + 3 * j);
				byte alteracion = claves.get(i).getValue(4 + 3 * j);
				
				//  El margen Y depende del pentagrama al que pertenezca el compás
				int marginY = compas_margin_y + 
						(config.getDistanciaLineasPentagrama() * 4 + 
								config.getDistanciaPentagramas()) * (pentagrama - 1);
				
				OrdenDibujo ordenDibujo = new OrdenDibujo();
				switch (alteracion) {
					case 0:
						ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
						ordenDibujo.setImagen(obtenerImagenDeClave(clave));
						ordenDibujo.setX1(compas_margin_x + x_position);
						ordenDibujo.setY1(compas_margin_y);
						ordenesDibujo.add(ordenDibujo);
						break;
						
					case 1:
						octavarium = true;
	
						ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
						ordenDibujo.setImagen(octavariumImage);
						ordenDibujo.setX1(x_position);
						ordenDibujo.setY1(marginY - config.getDistanciaLineasPentagrama() * 4);
						ordenesDibujo.add(ordenDibujo);
						break;
					
					case -1:
						octavarium = false;
	
						ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
						ordenDibujo.setRadius(config.getRadioPuntillos());
						ordenDibujo.setX1(x_position);
						ordenDibujo.setY1(marginY - config.getDistanciaLineasPentagrama() * 4);
						ordenesDibujo.add(ordenDibujo);
						break;
						
					default: 
						break;
				}
			}
		}
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
	
	private void moverCompasAlSiguienteRenglon(Compas compas, int primeraOrden, int ultimaOrden) {
		int distancia_x = compas.getXIni() - config.getXInicialPentagramas();
		int distancia_y = (config.getDistanciaLineasPentagrama() * 4 + 
				config.getDistanciaPentagramas()) * partitura.getStaves();
		
		compas.setXIni(config.getXInicialPentagramas());
		compas.setXFin(compas.getXFin() - distancia_x);

		compas_margin_x = compas.getXFin();
		compas_margin_y = compas_margin_y + distancia_y;
		
		compas.setYIni(compas_margin_y);
		compas.setYFin(compas_margin_y + 
				config.getDistanciaLineasPentagrama() * 4 + 
				(config.getDistanciaPentagramas() + 
						config.getDistanciaLineasPentagrama() * 4) * (partitura.getStaves() - 1));

		for (int i=primeraOrden; i<ultimaOrden; i++) {
			ordenesDibujo.get(i).setX1(ordenesDibujo.get(i).getX1() - distancia_x);
			ordenesDibujo.get(i).setX2(ordenesDibujo.get(i).getX2() - distancia_x);
			
			ordenesDibujo.get(i).setY1(ordenesDibujo.get(i).getY1() + distancia_y);
			ordenesDibujo.get(i).setY2(ordenesDibujo.get(i).getY2() + distancia_y);
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
	
	private Bitmap obtenerCorcheteDeNota(Nota nota) {
		if (nota.notaDeGracia()) {
			if (nota.haciaArriba()) return headlittle;
			else return headinvlittle;
		}
		else {
			if (nota.haciaArriba()) return head;
			else return headinv;
		}
	}
	
	private Bitmap obtenerImagenDeNota(Nota nota) {
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
	
	public int obtenerPosicionYDeNota(Nota nota, byte clave, byte instrumento){
		int coo_y = 0;
		int margenY = compas_margin_y + 
				(config.getDistanciaLineasPentagrama() * 4 + 
						config.getDistanciaPentagramas()) * (nota.getPentagrama() - 1);
		
		byte octava = nota.getOctava();
		if (octavarium) octava--;
		
		if (nota.getStep() > 0) {

			switch (instrumento) {

				case 1: {

					switch (clave) {

						case 1: {

							switch (octava) {

								case 5: {
									
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

								case 6: {
									
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

								case 7: {
									
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

								case 8: {
									
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
	
	private int distanciaUnidadPosicion(int position) {
		return position * config.getUnidadDesplazamiento() / partitura.getDivisions();
	}
}