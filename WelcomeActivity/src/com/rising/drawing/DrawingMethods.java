package com.rising.drawing;

import java.util.ArrayList;
import java.util.Collections;

import com.rising.drawing.figurasGraficas.Clave;
import com.rising.drawing.figurasGraficas.Compas;
import com.rising.drawing.figurasGraficas.IndiceNota;
import com.rising.drawing.figurasGraficas.Intensidad;
import com.rising.drawing.figurasGraficas.Nota;
import com.rising.drawing.figurasGraficas.Partitura;
import com.rising.drawing.figurasGraficas.Pedal;
import com.rising.drawing.figurasGraficas.Quintas;
import com.rising.drawing.figurasGraficas.Tempo;
import com.rising.drawing.figurasGraficas.Texto;
import com.rising.drawing.figurasGraficas.Wedge;

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
	private int compasActual = 0;
	private int notaActual = 0;
	
	//  Variables para la gestión de las múltiples notas
	private ArrayList<IndiceNota> beams = new ArrayList<IndiceNota>();
	private ArrayList<IndiceNota> ligaduras = new ArrayList<IndiceNota>();
	private boolean buscandoLigaduraExpresion = false;
	private int ligaduraExpresionYArriba = Integer.MAX_VALUE;
	private int ligaduraExpresionYAbajo = 0;
	private int octavarium = 0;
	private int xIniOctavarium = 0;
	private int yIniOctavarium = 0;
	private int xFinOctavarium = 0;
	private int yFinOctavarium = 0;
	private int xIniSlide = 0;
	private int xIniTresillo = 0;
	private int yAnterior = 0;
	private int yIniSlide = 0;
	
	public DrawingMethods(Partitura partitura, Resources resources) 
	{
		this.config = Config.getInstance();
		
		if (config.supported()) 
		{
			this.partitura = partitura;
			bitmapManager = BitmapManager.getInstance(resources);
			isValid = true;
		}
	}
	
	public boolean isValid() 
	{
		return isValid;
	}

	public ArrayList<OrdenDibujo> crearOrdenesDeDibujo(Vista vista) 
	{
		clearMeasures();
		
		calculador = new Calculador(partitura, bitmapManager, vista);
		calculador.calcularPosicionesDeCompases();
		
		dibujarPartitura();
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
	
	private void dibujarPartitura()
	{
		dibujarTituloYAutor();
		dibujarCompases();
	}
	
	private void dibujarTituloYAutor()
	{
		ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraObra, 
				true, partitura.getWork(), config.width / 2, 
				config.margenSuperior + config.margenObra));
		ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraAutor, 
				true, partitura.getCreator(), config.width / 2, 
				config.margenSuperior + config.margenAutor));
	}
	
	private void dibujarCompases() 
	{
		ArrayList<Compas> compases = partitura.getCompases();
		int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++) {
			compasActual = i;
			dibujarCompas(compases.get(i));
		}
	}
	
	private void dibujarCompas(Compas compas)
	{
		dibujarNumeroDeCompas(compas);
		dibujarLineasDePentagramaDeCompas(compas);
		dibujarClaves(compas);
		dibujarQuintas(compas);
		dibujarIntensidades(compas);
		dibujarPedales(compas);
		dibujarTempo(compas);
		dibujarTextos(compas);
		dibujarCrescendosYDiminuendos(compas);
		
		dibujarNotas(compas);
		
		dibujarBarlines(compas);
	}
	
	private void dibujarNumeroDeCompas(Compas compas) 
	{
		if (compas.getXIni() == config.xInicialPentagramas)
			ordenesDibujo.add( new OrdenDibujo(
				config.tamanoLetraNumeroCompas, false, Integer.toString(compas.getNumeroCompas()), 
					compas.getXIni() - config.xNumeroCompas, compas.getYIni() - config.yNumeroCompas));
	}
	
	private void dibujarLineasDePentagramaDeCompas(Compas compas) 
	{
		dibujarLineasLaterales(compas);
		dibujarLineasHorizontales(compas);
	}
	
	private void dibujarLineasLaterales(Compas compas)
	{
		ordenesDibujo.add( new OrdenDibujo(
				1, compas.getXIni(), compas.getYIni(), compas.getXIni(), compas.getYFin()));
		ordenesDibujo.add( new OrdenDibujo(
				1, compas.getXFin(), compas.getYIni(), compas.getXFin(), compas.getYFin()));
	}
	
	private void dibujarLineasHorizontales(Compas compas)
	{
		int yLinea = compas.getYIni();
		int pentagramasPendientes = partitura.getStaves();
		do {
			for (int i=0; i<5; i++) {
				ordenesDibujo.add( new OrdenDibujo(
						1, compas.getXIni(), yLinea, compas.getXFin(), yLinea));

				yLinea += config.distanciaLineasPentagrama;
			}

			yLinea += config.distanciaPentagramas - config.distanciaLineasPentagrama;
			pentagramasPendientes--;

		} while (pentagramasPendientes > 0);
	}
	
	private void dibujarClaves(Compas compas) 
	{
		for (int i=0; i<compas.numClaves(); i++) 
		{
			Clave clave = compas.getClave(i);
			ordenesDibujo.add( new OrdenDibujo(clave.getImagenClave(), clave.getX(), clave.getY()));
		}
	}
	
	private void dibujarQuintas(Compas compas) 
	{
		if (compas.hayQuintas()) 
			dibujarArmaduraReBemol(compas.getQuintas());
	}

	private void dibujarArmaduraReBemol(Quintas quintas)
	{
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.getX(), 
				quintas.getMargenY() + config.distanciaLineasPentagrama));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.getX() + 20, 
				quintas.getMargenY() - config.distanciaLineasPentagramaMitad));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.getX() + 40, 
				quintas.getMargenY() + config.distanciaLineasPentagramaMitad +
				config.distanciaLineasPentagrama));
		ordenesDibujo.add( new OrdenDibujo(
				bitmapManager.getFlat(), quintas.getX() + 60, quintas.getMargenY()));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.getX() + 80, 
				quintas.getMargenY() + config.distanciaLineasPentagrama * 2));

		int offset = config.distanciaLineasPentagrama * 4 +
				config.distanciaPentagramas + config.distanciaLineasPentagrama;
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.getX(), 
				quintas.getMargenY() + offset + config.distanciaLineasPentagrama));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.getX() + 20, 
				quintas.getMargenY() + offset - config.distanciaLineasPentagramaMitad));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.getX() + 40, 
				quintas.getMargenY() + offset + config.distanciaLineasPentagramaMitad +
				config.distanciaLineasPentagrama));
		ordenesDibujo.add( new OrdenDibujo(
				bitmapManager.getFlat(), quintas.getX() + 60, quintas.getMargenY() + offset));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.getX() + 80, 
				quintas.getMargenY() + offset + config.distanciaLineasPentagrama * 2));
	}
	
	private void dibujarIntensidades(Compas compas) 
	{
		for (int i=0; i<compas.numIntensidades(); i++) 
		{
			Intensidad intensidad = compas.getIntensidad(i);
			ordenesDibujo.add( new OrdenDibujo(intensidad.getImagen(), 
					intensidad.getX(), intensidad.getY()));
		}
	}
	
	private void dibujarPedales(Compas compas) 
	{
		for (int i=0; i<compas.numPedalesInicio(); i++) 
		{
			Pedal pedalInicio = compas.getPedalInicio(i);
			ordenesDibujo.add( new OrdenDibujo(pedalInicio.getImagen(), 
					pedalInicio.getX(), pedalInicio.getY()));
		}

		for (int i=0; i<compas.numPedalesFin(); i++) 
		{
			Pedal pedalFin = compas.getPedalFin(i);
			ordenesDibujo.add( new OrdenDibujo(pedalFin.getImagen(), 
					pedalFin.getX(), pedalFin.getY()));
		}
	}
	
	private void dibujarTempo(Compas compas) 
	{
		if (compas.hayTempo())
		{
			if (compas.getTempo().dibujar()) 
			{
				Tempo tempo = compas.getTempo();
							
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTempo, 
						false, tempo.getNumeradorString(), tempo.getX(), tempo.getYNumerador()));
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTempo, 
						false, tempo.getDenominadorString(), tempo.getX(), tempo.getYDenominador()));
		
				if (partitura.getStaves() == 2) 
				{
					int margenY = config.distanciaLineasPentagrama * 4 + config.distanciaPentagramas;
					
					ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTempo, 
							false, tempo.getNumeradorString(), tempo.getX(), tempo.getYNumerador() + margenY));
					ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTempo, 
							false, tempo.getDenominadorString(), tempo.getX(), tempo.getYDenominador() + margenY));
				}
			}
		}
	}
	
	private void dibujarTextos(Compas compas) 
	{
		int numTextos = compas.numTextos();
		
		for (int i=0; i<numTextos; i++) 
		{
			Texto texto = compas.getTexto(i);
			ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraWords, 
					false, texto.getTexto(), texto.getX(), texto.getY()));
		}
	}
	
	private void dibujarCrescendosYDiminuendos(Compas compas) 
	{
		int num = compas.numCrescendos();
		for (int i=0; i<num; i++) 
		{
			Wedge wedge = compas.getCrescendo(i);
			
			ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
					wedge.getYIni() + config.alturaCrescendos / 2,
					wedge.getXFin(), wedge.getYIni()));
			ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
					wedge.getYIni() + config.alturaCrescendos / 2,
					wedge.getXFin(), wedge.getYIni() + config.alturaCrescendos));
		}
		
		num = compas.numDiminuendos();
		for (int i=0; i<num; i++) 
		{
			Wedge wedge = compas.getDiminuendo(i);
			
			ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
					wedge.getYIni(), wedge.getXFin(), wedge.getYIni() + config.alturaCrescendos / 2));
			ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
					wedge.getYIni() + config.alturaCrescendos, 
					wedge.getXFin(), wedge.getYIni() + config.alturaCrescendos / 2));
		}
	}
	
	private void dibujarNotas(Compas compas) 
	{
		ArrayList<Nota> notas = compas.getNotas();
		int numNotas = notas.size();
		
		for (int i=0; i<numNotas; i++) 
		{
			notaActual = i;
			dibujarNota(notas.get(i), compas.getYIni());
		}
	}
	
	private void dibujarNota(Nota nota, int yIni)
	{
		actualizarLimitesDeLigaduraDeExpresion(nota.getY());
		actualizarYAnterior(nota);
		
		dibujarCabezaDeNota(nota);
		int yBeams = dibujarCuerpoDeNota(nota);

		dibujarFigurasGraficasDeNota(nota, yBeams);
		dibujarLineasFueraDelPentagrama(nota, yIni);
		
		gestionarOctavarium(nota, yIni - config.distanciaLineasPentagrama * 6);
	}
	
	private void actualizarLimitesDeLigaduraDeExpresion(int y)
	{
		if (buscandoLigaduraExpresion) {
			if (ligaduraExpresionYArriba > y)
				ligaduraExpresionYArriba = y;
			if (ligaduraExpresionYAbajo < y)
				ligaduraExpresionYAbajo = y;
		}
	}
	
	private void actualizarYAnterior(Nota nota)
	{
		if (!nota.acorde()) yAnterior = nota.getY();
	}
	
	private void dibujarCabezaDeNota(Nota nota) 
	{
		int desplazamiento = obtenerDesplazamientoDeNota(nota);
		ordenesDibujo.add( new OrdenDibujo(
				calculador.obtenerImagenDeCabezaDeNota(nota), 
				nota.getX() + desplazamiento, nota.getY()));
	}
	
	private int obtenerDesplazamientoDeNota(Nota nota)
	{
		int desplazamiento = 0;
		if (nota.desplazadaALaIzquierda()) desplazamiento -= config.anchoCabezaNota;
		if (nota.desplazadaALaDerecha()) desplazamiento += config.anchoCabezaNota;
		
		return desplazamiento;
	}
	
	private int dibujarCuerpoDeNota(Nota nota)
	{
		int yBeams = 0;
		
		if (nota.tieneBeams()) 
			yBeams = gestionarBeams(nota);
		else
		{
			if (nota.tienePlica())
			{
				dibujarPlicaDeNota(nota, 0);
				dibujarCorcheteDeNota(nota);
			}
		}

		return yBeams;
	}
	
	private int gestionarBeams(Nota nota)
	{
		int yBeams = 0;
		
		int beamId = guardarBeamDeNota(nota);
		if (nota.beamFinal())
		{
			yBeams = colocarBeamsALaMismaAltura(beamId);
			dibujarBeams(yBeams, beamId);
		}
		
		return yBeams;
	}
	
	private int guardarBeamDeNota(Nota nota)
	{
		IndiceNota beam = new IndiceNota(compasActual, notaActual, (byte) 0, nota.getBeamId());
		beams.add(beam);
		
		return beam.getBeamId();
	}

	private int colocarBeamsALaMismaAltura(int beamId) 
	{
		int numBeams = beams.size();
		int yBeams = -1;
		int pentagrama = -1;
		
		//  Nota que no es de gracia
		boolean notaNormal = false;

		for (int i=0; i<numBeams; i++) {
			if (beamId == beams.get(i).getBeamId()) {
				
				int indCompas = beams.get(i).getCompas();
				int indNota = beams.get(i).getNota();
				Nota nota = partitura.getCompas(indCompas).getNota(indNota);
				if (yBeams == -1) {
					yBeams = nota.haciaArriba() ? Integer.MAX_VALUE : 0;
					pentagrama = nota.getPentagrama();
				}
				
				//  No todas las notas están en el mismo pentagrama
				if (pentagrama != nota.getPentagrama()) {
					yBeams = partitura.getCompas(indCompas).getYIni() +
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
					if (yBeams > nota.getY())
						yBeams = nota.getY();
				}
				else {
					if (yBeams < nota.getY())
						yBeams = nota.getY();
				}
				
				if (i == numBeams - 1) {
					int longitudPlica = notaNormal ? 
							config.longitudPlica : config.longitudPlicaNotaGracia;
					
					if (nota.haciaArriba()) yBeams -= longitudPlica;
					else yBeams += longitudPlica;
				}
			}
		}

		return yBeams;
	}
	
	//  Esta implementación está ignorando las plicas dobles
	private void dibujarBeams(int yBeams, int beamId) 
	{
		int numBeams = beams.size();
		
		int indCompasAnt = 0;
		int indNotaAnt = 0;
		int distanciaBeams = 0;
		int anchoBeams = 0;
		
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
			
			distanciaBeams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ? 
					config.distanciaEntreBeamsNotaGracia : config.distanciaEntreBeams;
			if (!notaAnt.haciaArriba()) distanciaBeams *= -1;
			
			anchoBeams = partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ?
					config.anchoBeamsNotaGracia : config.anchoBeams;

			if ( i == numBeams - 1 || beams.get(i + 1).getBeamId() != beamId ) {
				
				int xLastBeam = 0;
				
				int offset = notaAnt.haciaArriba() ? config.anchoCabezaNota : 0;
				
				//  Gestión de hooks en la última nota
				switch (notaAnt.getBeam()) {
						
					case 4:
						xLastBeam = notaAnt.getX();
						
						ordenesDibujo.add( new OrdenDibujo(
								anchoBeams, xLastBeam + offset, yBeams + distanciaBeams * 2,
								xLastBeam + offset - config.anchoHooks, yBeams + distanciaBeams * 2));
						break;
						
					case 6:
						xLastBeam = notaAnt.getX();
						
						ordenesDibujo.add( new OrdenDibujo(
								anchoBeams, xLastBeam + offset, yBeams + distanciaBeams,
								xLastBeam + offset - config.anchoHooks, yBeams + distanciaBeams));
						break;
						
					default:
						break;
				}
			}
			else {
				int indCompasSig = beams.get(i + 1).getCompas();
				int indNotaSig = beams.get(i + 1).getNota();
				
				int xAntBeams = notaAnt.getX();
				Nota notaSig = partitura.getCompas(indCompasSig).getNota(indNotaSig);
				int xSigBeams = notaSig.getX();
				
				if (notaAnt.haciaArriba()) {
					int anchoCabezaNota = 
						partitura.getCompas(indCompasAnt).getNota(indNotaAnt).notaDeGracia() ? 
							config.anchoCabezaNotaGracia : config.anchoCabezaNota;
					xAntBeams += anchoCabezaNota;
				}
				
				if (notaSig.haciaArriba()) {
					int anchoCabezaNota = 
							partitura.getCompas(indCompasSig).getNota(indNotaSig).notaDeGracia() ? 
							config.anchoCabezaNotaGracia : config.anchoCabezaNota;
					xSigBeams += anchoCabezaNota;
				}
				
				switch (partitura.getCompas(indCompasAnt).getNota(indNotaAnt).getBeam()) {
					
					case 2:
						ordenesDibujo.add( new OrdenDibujo(
								anchoBeams, xAntBeams, yBeams, xSigBeams, yBeams));
						ordenesDibujo.add( new OrdenDibujo(
								anchoBeams, xAntBeams, yBeams + distanciaBeams, 
								xSigBeams, yBeams + distanciaBeams));
						ordenesDibujo.add( new OrdenDibujo(
								1, xAntBeams, yBeams, xAntBeams, yBeams + distanciaBeams));
						ordenesDibujo.add( new OrdenDibujo(
								1, xSigBeams, yBeams, xSigBeams, yBeams + distanciaBeams));
						break;
	
					case 3:
						ordenesDibujo.add( new OrdenDibujo(
								anchoBeams, xAntBeams, yBeams, xSigBeams, yBeams));
						break;
	
					case 5:
						ordenesDibujo.add( new OrdenDibujo(
								anchoBeams, xAntBeams, yBeams, xSigBeams, yBeams));
						ordenesDibujo.add( new OrdenDibujo(
								anchoBeams, xAntBeams, yBeams + distanciaBeams, 
								xSigBeams, yBeams + distanciaBeams));
						ordenesDibujo.add( new OrdenDibujo(
								anchoBeams, xAntBeams, yBeams + distanciaBeams * 2, 
								xSigBeams, yBeams + distanciaBeams * 2));
						ordenesDibujo.add( new OrdenDibujo(
								1, xAntBeams, yBeams, xAntBeams, yBeams + distanciaBeams * 2));
						ordenesDibujo.add( new OrdenDibujo(
								1, xSigBeams, yBeams, xSigBeams, yBeams + distanciaBeams * 2));
						break;
	
					default: 
						break;
				}
			}
			
			dibujarPlicaDeNota(partitura.getCompas(indCompasAnt).getNota(indNotaAnt), yBeams);
			if (++i == numBeams) break;
		}
		
		for (int j = primerBeam; j<i; j++)
			beams.remove(primerBeam);
	}
	
	private void dibujarPlicaDeNota(Nota nota, int yBeams) 
	{
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
				
		if (nota.haciaArriba())
		{
			x1 = nota.getX() + anchoCabezaNota;
			x2 = nota.getX() + anchoCabezaNota;
			y2 = nota.acorde() ? yAnterior : nota.getY() - longitudPlica;
		}
		else 
		{
			x1 = nota.getX();
			x2 = nota.getX();
			y2 = nota.acorde() ? yAnterior + longitudPlica : 
				nota.getY() + mitadCabezaNota + longitudPlica;
		}
		
		if (yBeams != 0) y2 = yBeams;
		ordenesDibujo.add( new OrdenDibujo(2, x1, y1, x2, y2));
	}
	
	private void dibujarCorcheteDeNota(Nota nota) 
	{
		//  Si la nota forma parte de un acorde, su corchete ya fue dibujado, 
		//  por lo que no hay que dibujarlo de nuevo
		if (!nota.acorde() && !nota.silencio()) 
		{
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
			
			if (nota.tieneSlash()) 
				dibujarSlash(x, y);
		}
	}
	
	private void dibujarCorcheteDeNotaCrearOrden(Nota nota, int x, int y, int distanciaCorchete) 
	{
		ordenesDibujo.add( new OrdenDibujo(
				calculador.obtenerImagenDeCorcheteDeNota(nota), x, y + distanciaCorchete));
	}
	
	private void dibujarSlash(int x, int y) 
	{
		ordenesDibujo.add( new OrdenDibujo(2, x + config.xInicioSlash, 
				y + config.yInicioSlash, x - config.xFinSlash, y + config.yFinSlash));
	}
	
	private void dibujarFigurasGraficasDeNota(Nota nota, int yBeams) 
	{
		ArrayList<Byte> figurasGraficas = nota.getFigurasGraficas();
		int numFiguras = figurasGraficas.size();
		int xAlteraciones = 0;
		int numAlteraciones = 0;
		
		for (int i=0; i<numFiguras; i++) 
		{
			if (nota.esLigadura(i)) {
				i = gestionarLigaduras(nota, figurasGraficas, i, yBeams);
			} else if (nota.esAlteracion(i)) {
				xAlteraciones = config.xAccidental2 * numAlteraciones++;
				gestionarAlteracion(nota, figurasGraficas, i, yBeams, xAlteraciones);
			} else if (nota.finDeTresillo(i)) {
				dibujarFiguraGrafica(nota, figurasGraficas.get(i), yBeams, figurasGraficas.get(i + 1));
				i++;
			} else {
				dibujarFiguraGrafica(nota, figurasGraficas.get(i), yBeams, 0);
			}
		}
	}
	
	private void dibujarFiguraGrafica(Nota nota, byte figura, int yBeams, int xSillo) 
	{
		int x = 0;

		switch (figura) {
			case 3:
				xIniTresillo = nota.getX();
				break;

			case 4:
				int margenTresillo = nota.haciaArriba() ? 
						- config.yTresilloArriba : config.yTresilloAbajo;
				int x_tresillo = (nota.getX() + xIniTresillo) / 2;
				if (nota.haciaArriba()) x_tresillo += config.xTresillo;
				
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTresillo, 
						false, Integer.toString(xSillo), x_tresillo, yBeams + margenTresillo));
				break;

			case 6:
				xIniSlide = nota.getX();
				yIniSlide = nota.getY();
				break;
				
			case 7:			
				dibujarSlide(nota, xIniSlide, yIniSlide);
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
				dibujarAlteraciones(nota, bitmapManager.getSharp(), xSillo);
				break;

			case 13:
				dibujarAlteraciones(nota, bitmapManager.getFlat(), xSillo);
				break;

			case 14:
				dibujarAlteraciones(nota, bitmapManager.getNatural(), xSillo);
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
	
	private void dibujarSlide(Nota nota, int xIniSlide, int yIniSlide) 
	{
		if (xIniSlide < nota.getX()) {
			ordenesDibujo.add( new OrdenDibujo(
				1, xIniSlide + config.anchoCabezaNota, 
					yIniSlide + config.mitadCabezaVertical, nota.getX(), 
						nota.getY() + config.mitadCabezaVertical));
		}
		else {
			ordenesDibujo.add( new OrdenDibujo(
				1, xIniSlide + config.anchoCabezaNota, 
					yIniSlide + config.mitadCabezaVertical, 
						config.xFinalPentagramas, 
								yIniSlide - config.ySlideTruncado));
			
			ordenesDibujo.add( new OrdenDibujo(
				1, config.xInicialPentagramas, 
					nota.getY() + config.mitadCabezaVertical + config.ySlideTruncado, 
						nota.getX(), nota.getY() + config.mitadCabezaVertical));
		}
	}
	
	private void dibujarLigadura(int indLigadura, Nota notaFinal, boolean union) 
	{
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
	
	private void dibujarLigaduraExpresion(Nota notaInicial, Nota notaFinal, boolean clockwise) 
	{
		if (notaInicial.getX() < notaFinal.getX())
			dibujarLigaduraExpresionNormal(notaInicial, notaFinal, clockwise);
		else
			dibujarLigaduraExpresionPartida(notaInicial, notaFinal, clockwise);
	}
	
	private void dibujarLigaduraExpresionNormal(Nota notaInicial, Nota notaFinal, boolean clockwise) 
	{
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
	
	private void dibujarLigaduraExpresionPartida(Nota notaInicial, Nota notaFinal, boolean clockwise) 
	{
		int anchoCabezaNota = notaInicial.notaDeGracia() ? 
				config.anchoCabezaNotaGracia : config.anchoCabezaNota;
		
		RectF rectf = new RectF(notaInicial.getX(), notaInicial.getY() - config.yLigadurasExpresion, 
				config.xFinalPentagramas, notaInicial.getY() + config.alturaArcoLigadurasExpresion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, clockwise));

		rectf = new RectF(config.xInicialPentagramas, notaFinal.getY() - config.yLigadurasExpresion, 
				notaFinal.getX() + anchoCabezaNota, notaFinal.getY() + config.alturaArcoLigadurasExpresion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, clockwise));
	}
	
	private void dibujarLigaduraUnion(Nota notaInicial, Nota notaFinal, boolean clockwise) 
	{
		if (notaInicial.getX() < notaFinal.getX())
			dibujarLigaduraUnionNormal(notaInicial, notaFinal, clockwise);
		else
			dibujarLigaduraUnionPartida(notaInicial, notaFinal, clockwise);
	}
	
	private void dibujarLigaduraUnionNormal(Nota notaInicial, Nota notaFinal, boolean clockwise) 
	{
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
	
	private void dibujarLigaduraUnionPartida(Nota notaInicial, Nota notaFinal, boolean clockwise) 
	{
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
	
	private int gestionarLigaduras(Nota nota, ArrayList<Byte> figurasGraficas, int ind, int yBeams) 
	{
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
		
		dibujarFiguraGrafica(nota, figurasGraficas.get(ind), yBeams, 0);
		
		return ind + 2;
	}
	
	private void dibujarAlteraciones(Nota nota, Bitmap alteracion, int offset) 
	{
		int xAccidental = nota.notaDeGracia() ? 
				config.xAccidentalNotaGracia : config.xAccidental;
		int x = nota.getX() - xAccidental - offset;
		if (nota.desplazadaALaIzquierda()) x -= config.anchoCabezaNota - offset;
		
		ordenesDibujo.add( new OrdenDibujo(alteracion, x, nota.getY() - config.yAccidental));
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
			int ind, int yBeams, int xAlteraciones) 
	{
		if (nota.desplazadaALaIzquierda())
			nota.setX(nota.getX() - config.anchoCabezaNota);
		else if (nota.desplazadaALaDerecha())
			nota.setX(nota.getX() + config.anchoCabezaNota);
		
		dibujarFiguraGrafica(nota, figurasGraficas.get(ind), yBeams, xAlteraciones);
	}

	private void dibujarLineasFueraDelPentagrama(Nota nota, int yIniCompas) 
	{
		int yMarginCustom = yIniCompas + 
				(config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas) * (nota.getPentagrama() - 1);
		
		if (nota.getY() < yMarginCustom)
			dibujarLineasEncimaDelPentagrama(nota, yMarginCustom);
		else
			dibujarLineasDebajoDelPentagrama(nota, yMarginCustom);
	}
	
	private void dibujarLineasEncimaDelPentagrama(Nota nota, int yMarginCustom)
	{
		int currentYPosition = yMarginCustom;
		int yNota = nota.notaDeGracia() ? nota.getY() - config.margenNotaGracia : nota.getY();
		int threshold = currentYPosition - 
				config.distanciaLineasPentagrama - config.distanciaLineasPentagramaMitad;
		
		if (yNota <= threshold) {
			while (currentYPosition > yNota + config.distanciaLineasPentagrama) {
				currentYPosition -= config.distanciaLineasPentagrama;
				
				ordenesDibujo.add( new OrdenDibujo(
						1, nota.getX() - config.margenAnchoCabezaNota, currentYPosition, 
						nota.getX() + config.anchoCabezaNota + config.margenAnchoCabezaNota, 
						currentYPosition));
			}
		}
	}
	
	private void dibujarLineasDebajoDelPentagrama(Nota nota, int yMarginCustom)
	{
		int currentYPosition = yMarginCustom + config.distanciaLineasPentagrama * 4;
		int yNota = nota.notaDeGracia() ? nota.getY() - config.margenNotaGracia : nota.getY();
		int threshold = currentYPosition + config.distanciaLineasPentagramaMitad;
		
		if (yNota >= threshold) {
			while (currentYPosition < yNota) {
				currentYPosition += config.distanciaLineasPentagrama;
				
				ordenesDibujo.add( new OrdenDibujo(
						1, nota.getX() - config.margenAnchoCabezaNota, currentYPosition, 
						nota.getX() + config.anchoCabezaNota + config.margenAnchoCabezaNota, 
						currentYPosition));
			}
		}
	}

	private void gestionarOctavarium(Nota nota, int marginY) 
	{
		if (nota.octavada()) 
			prepararValoresOctavarium(nota.getX(), marginY);
		else 
			if (octavarium > 0) {
				dibujarOctavarium();
				inicializarValoresOctavarium();
			}
	}
	
	private void prepararValoresOctavarium(int x, int marginY)
	{
		if (octavarium == 0) {
			xIniOctavarium = x;
			yIniOctavarium = marginY;
			octavarium++;
		}
		else {
			xFinOctavarium = x;
			yFinOctavarium = marginY;
		}
	}
	
	private void inicializarValoresOctavarium()
	{
		octavarium = 0;
		xIniOctavarium = 0;
		xFinOctavarium = 0;
		yIniOctavarium = 0;
		yFinOctavarium = 0;
	}
	
	private void dibujarOctavarium() 
	{		
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getOctavarium(), xIniOctavarium, 
				yIniOctavarium + 5));
		
		if (xIniOctavarium < xFinOctavarium) {
			ordenesDibujo.add( new OrdenDibujo(
					2, xIniOctavarium, yIniOctavarium, 
					xFinOctavarium + config.anchoCabezaNota, yFinOctavarium));
		}
		else {
			ordenesDibujo.add( new OrdenDibujo(
					2, xIniOctavarium, yIniOctavarium, 
					config.xFinalPentagramas, yIniOctavarium));
			ordenesDibujo.add( new OrdenDibujo(
					2, config.xInicialPentagramas, yFinOctavarium, 
					xFinOctavarium + config.anchoCabezaNota, yFinOctavarium));
		}
		
		ordenesDibujo.add( new OrdenDibujo(
				2, xFinOctavarium + config.anchoCabezaNota, yFinOctavarium, 
				xFinOctavarium + config.anchoCabezaNota, yFinOctavarium + config.yOctavarium));
	}

	//  Esta implementación por ahora sólo considera el barline de fin de partitura
	//  Si en el futuro se añadieran más barlines, habría que usar un switch en el bucle
	private void dibujarBarlines(Compas compas) 
	{
		ArrayList<ElementoGrafico> barlines = compas.getBarlines();
		int numBarlines = barlines.size();

		for (int i=0; i<numBarlines; i++) {
			ordenesDibujo.add( new OrdenDibujo(
				4, compas.getXFin(), compas.getYIni(), compas.getXFin(), compas.getYFin()));
			ordenesDibujo.add( new OrdenDibujo(
					2, compas.getXFin() - config.margenBarlines, 
					compas.getYIni(), compas.getXFin() - config.margenBarlines, compas.getYFin()));
		}
	}
}