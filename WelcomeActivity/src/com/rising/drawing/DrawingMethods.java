package com.rising.drawing;

import java.util.ArrayList;
import java.util.Collections;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.RectF;

public class DrawingMethods {
	
	private boolean isValid = false;

	private ArrayList<OrdenDibujo> ordenesDibujo = new ArrayList<OrdenDibujo>();
	private Partitura partitura;
	private Config config;
	private Calculador calculador;
	private BitmapManager bitmapManager;

	//  Variables para la gestión y el tratamiento dinámico de los múltiples compases
	private int compas_margin_y = 0;
	private int compasActual = 0;
	private int notaActual = 0;
	
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
	
	public DrawingMethods(Partitura partitura, Resources resources) 
	{
		this.config = Config.getInstance();
		
		if (config.supported()) 
		{
			this.partitura = partitura;
			compas_margin_y = config.margenSuperior;

			bitmapManager = BitmapManager.getInstance(resources);

			isValid = true;
		}
	}

	public ArrayList<OrdenDibujo> crearOrdenesDeDibujo(Vista vista) 
	{
		ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraObra, 
				true, partitura.getWork(), config.width / 2, 
				compas_margin_y + config.margenObra));
		ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraAutor, 
				true, partitura.getCreator(), config.width / 2, 
				compas_margin_y + config.margenAutor));
		compas_margin_y += config.margenInferiorAutor;
		
		clearMeasures();
		
		calculador = new Calculador(partitura, compas_margin_y, bitmapManager, vista);
		calculador.numerarCompases();
		calculador.calcularPosicionesDeCompases();
		
		dibujarCompases();
		return ordenesDibujo;
	}
	
	//  Each time we want to rewrite the score, we have to get rid of the
	//  previous values, so they don't interfiere with the new calculations
	private void clearMeasures()
	{
		int numMeasures = partitura.getNumeroDeCompases();
		for (int i=0; i<numMeasures; i++)
			partitura.getCompas(i).clear();
	}

	public boolean isValid() {
		return isValid;
	}

	
	/*
	 * 
	 * FUNCIONES DE DIBUJO
	 * 
	 */

	private int colocarBeamsALaMismaAltura(int beamId) {
		int numBeams = beams.size();
		int y_beams = -1;
		int pentagrama = -1;
		
		//  Nota que no es de gracia
		boolean notaNormal = false;

		for (int i=0; i<numBeams; i++) {
			if (beamId == beams.get(i).getBeamId()) {
				
				int indCompas = beams.get(i).getCompas();
				int indNota = beams.get(i).getNota();
				Nota nota = partitura.getCompas(indCompas).getNota(indNota);
				if (y_beams == -1) {
					y_beams = nota.haciaArriba() ? Integer.MAX_VALUE : 0;
					pentagrama = nota.getPentagrama();
				}
				
				//  No todas las notas están en el mismo pentagrama
				if (pentagrama != nota.getPentagrama()) {
					y_beams = partitura.getCompas(indCompas).getYIni() +
							config.distanciaLineasPentagrama * 4 + config.distanciaPentagramas / 2;
					break;
				}
				
				//  Previene que puedan haber notas normales y de gracia
				//  mezcladas en un mismo beam, en cuyo caso la altura
				//  la impondría la nota normal por ocupar más espacio
				if (!notaNormal)
					if (!nota.notaDeGracia()) 
						notaNormal = true;
				
				if (nota.haciaArriba()) {
					if (y_beams > nota.getY())
						y_beams = nota.getY();
				}
				else {
					if (y_beams < nota.getY())
						y_beams = nota.getY();
				}
				
				if (i == numBeams - 1) {
					int longitudPlica = notaNormal ? 
							config.longitudPlica : config.longitudPlicaNotaGracia;
					
					if (nota.haciaArriba()) y_beams -= longitudPlica;
					else y_beams += longitudPlica;
				}
			}
		}

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
	private void dibujarBeams(int y_beams, int beamId) {
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
			Nota notaAnt = partitura.getCompas(indCompasAnt).getNota(indNotaAnt);
			
			distancia_beams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ? 
					config.distanciaEntreBeamsNotaGracia : config.distanciaEntreBeams;
			if (!notaAnt.haciaArriba()) distancia_beams *= -1;
			
			ancho_beams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ?
					config.anchoBeamsNotaGracia : config.anchoBeams;

			if ( (i == numBeams - 1) || (beams.get(i + 1).getBeamId() != beamId) ) {
				
				int x_last_beam = 0;
				
				int offset = notaAnt.haciaArriba() ? config.anchoCabezaNota : 0;
				
				//  Gestión de hooks en la última nota
				switch (notaAnt.getBeam()) {
						
					case 4:
						x_last_beam = notaAnt.getX();
						
						ordenesDibujo.add( new OrdenDibujo(
								ancho_beams, x_last_beam + offset, y_beams + distancia_beams * 2,
								x_last_beam + offset - config.anchoHooks, y_beams + distancia_beams * 2));
						break;
						
					case 6:
						x_last_beam = notaAnt.getX();
						
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
				
				int x_ant_beams = notaAnt.getX();
				Nota notaSig = partitura.getCompas(indCompasSig).getNota(indNotaSig);
				int x_sig_beams = notaSig.getX();
				
				if (notaAnt.haciaArriba()) {
					int anchoCabezaNota = 
						partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ? 
							config.anchoCabezaNotaGracia : config.anchoCabezaNota;
					x_ant_beams += anchoCabezaNota;
				}
				
				if (notaSig.haciaArriba()) {
					int anchoCabezaNota = 
							partitura.getCompas(indCompasSig).getNota(indNotaSig).notaDeGracia() ? 
							config.anchoCabezaNotaGracia : config.anchoCabezaNota;
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

		ordenesDibujo.add( new OrdenDibujo(
				calculador.obtenerImagenDeCabezaDeNota(nota), 
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
				calculador.obtenerImagenDeCorcheteDeNota(nota), x, y + distanciaCorchete));
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
				dibujarAlteraciones(nota, bitmapManager.getSharp(), Xsillo);
				break;

			case 13:
				dibujarAlteraciones(nota, bitmapManager.getFlat(), Xsillo);
				break;

			case 14:
				dibujarAlteraciones(nota, bitmapManager.getNatural(), Xsillo);
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
				ordenesDibujo.add( new OrdenDibujo(
						bitmapManager.getBendRelease(), nota.getX(), nota.getY() + config.yBend));
				break;
				
			case 26:
				ordenesDibujo.add( new OrdenDibujo(
						bitmapManager.getVibrato(), nota.getX(), nota.getY() + config.yBend));
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
				
				ordenesDibujo.add( new OrdenDibujo(
						bitmapManager.getAccent(), x, nota.getY() - config.yAccentUp));
				break;
				
			case 31:
				ordenesDibujo.add( new OrdenDibujo(bitmapManager.getAccent(), nota.getX(), 
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
				ordenesDibujo.add( new OrdenDibujo(bitmapManager.getMarcato(), nota.getX(), 
						nota.getY() - config.yAccentUp));
				break;
				
			case 36:
				ordenesDibujo.add( new OrdenDibujo(
						bitmapManager.getFermata(), nota.getX() - config.xFermata, 
						nota.getY() - config.yFermata));
				break;
				
			case 37:
				ordenesDibujo.add( new OrdenDibujo(
						bitmapManager.getFermataInverted(), nota.getX() - config.xFermata, 
						nota.getY() + config.yFermata));
				break;
				
			case 38:				
				ordenesDibujo.add( new OrdenDibujo(
						bitmapManager.getTrill(), nota.getX(), nota.getY() - config.yTrill));
				break;
				
			case 39:				
				ordenesDibujo.add( new OrdenDibujo(
						bitmapManager.getTrill(), nota.getX(), nota.getY() + config.yTrill));
				break;
				
			case 40:
				ordenesDibujo.add( new OrdenDibujo(bitmapManager.getArpegio(), 
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
				gestionarAlteracion(nota, figurasGraficas, i, y_beams, xAlteraciones);

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
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getOctavarium(), x_ini_octavarium, 
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
					dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX(), 
							quintas.getMargenY() + config.distanciaLineasPentagrama);
					dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX() + 20, 
							quintas.getMargenY() - config.distanciaLineasPentagramaMitad);
					dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX() + 40, 
							quintas.getMargenY() + config.distanciaLineasPentagramaMitad +
							config.distanciaLineasPentagrama);
					dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX() + 60, quintas.getMargenY());
					dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX() + 80, 
							quintas.getMargenY() + config.distanciaLineasPentagrama * 2);
					
					if (partitura.getStaves() == 2) {
						int offset = config.distanciaLineasPentagrama * 4 +
								config.distanciaPentagramas + config.distanciaLineasPentagrama;
						
						dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX(), 
								quintas.getMargenY() + offset + config.distanciaLineasPentagrama);
						dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX() + 20, 
								quintas.getMargenY() + offset - config.distanciaLineasPentagramaMitad);
						dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX() + 40, 
								quintas.getMargenY() + offset + config.distanciaLineasPentagramaMitad +
								config.distanciaLineasPentagrama);
						dibujarQuintasAlteraciones(
								bitmapManager.getFlat(), quintas.getX() + 60, quintas.getMargenY() + offset);
						dibujarQuintasAlteraciones(bitmapManager.getFlat(), quintas.getX() + 80, 
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

	private void gestionarAlteracion(Nota nota, ArrayList<Byte> figurasGraficas, 
			int ind, int y_beams, int xAlteraciones) {
		
		if (nota.desplazadaALaIzquierda())
			nota.setX(nota.getX() - config.anchoCabezaNota);
		else if (nota.desplazadaALaDerecha())
			nota.setX(nota.getX() + config.anchoCabezaNota);
		
		dibujarFiguraGrafica(nota, figurasGraficas.get(ind), y_beams, xAlteraciones);
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
			y_beams = colocarBeamsALaMismaAltura(beamId);
			dibujarBeams(y_beams, beamId);
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