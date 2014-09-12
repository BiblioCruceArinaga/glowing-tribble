package com.rising.drawing;

import java.util.ArrayList;
import java.util.Collections;

import com.rising.drawing.figurasgraficas.Clave;
import com.rising.drawing.figurasgraficas.Compas;
import com.rising.drawing.figurasgraficas.IndiceNota;
import com.rising.drawing.figurasgraficas.Intensidad;
import com.rising.drawing.figurasgraficas.Nota;
import com.rising.drawing.figurasgraficas.OrdenDibujo;
import com.rising.drawing.figurasgraficas.Partitura;
import com.rising.drawing.figurasgraficas.Pedal;
import com.rising.drawing.figurasgraficas.Quintas;
import com.rising.drawing.figurasgraficas.Tempo;
import com.rising.drawing.figurasgraficas.Texto;
import com.rising.drawing.figurasgraficas.Vista;
import com.rising.drawing.figurasgraficas.Wedge;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.RectF;

public class DrawingMethods 
{
	private transient boolean drawable;

	private transient final ArrayList<OrdenDibujo> ordenesDibujo = new ArrayList<OrdenDibujo>();
	private transient Partitura partitura;
	private transient final Config config;
	private transient Calculador calculador;
	private transient BitmapManager bitmapManager;

	//  Variables para la gestión y el tratamiento dinámico de los múltiples compases
	private transient int compasActual;
	private transient int notaActual;
	
	//  Variables para la gestión de las múltiples notas
	private transient final ArrayList<IndiceNota> beams = new ArrayList<IndiceNota>();
	private transient final ArrayList<IndiceNota> ligaduras = new ArrayList<IndiceNota>();
	private transient boolean buscandoLigaduraExpresion;
	private transient int ligaduraExpresionYArriba = Integer.MAX_VALUE;
	private transient int ligaduraExpresionYAbajo;
	private transient int octavarium;
	private transient int xIniOctavarium;
	private transient int yIniOctavarium;
	private transient int xFinOctavarium;
	private transient int yFinOctavarium;
	private transient int xIniSlide;
	private transient int xIniTresillo;
	private transient int yAnterior;
	private transient int yIniSlide;
	
	public DrawingMethods(final Partitura partitura, final Resources resources) 
	{
		this.config = Config.getInstance();
		
		if (config.supported()) 
		{
			this.partitura = partitura;
			bitmapManager = BitmapManager.getInstance(resources);
			drawable = true;
		}
	}
	
	public boolean canDraw() 
	{
		return drawable;
	}

	public ArrayList<OrdenDibujo> crearOrdenesDeDibujo(final Vista vista) 
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
		final int numMeasures = partitura.getNumeroDeCompases();
		for (int i=0; i<numMeasures; i++) {
			partitura.getCompas(i).clear();
		}
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
		final ArrayList<Compas> compases = partitura.getCompases();
		final int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++) {
			compasActual = i;
			dibujarCompas(compases.get(i));
		}
	}
	
	private void dibujarCompas(final Compas compas)
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
	
	private void dibujarNumeroDeCompas(final Compas compas) 
	{
		if (compas.getXIni() == config.xInicialPentagramas) 
		{
			ordenesDibujo.add( new OrdenDibujo(
				config.tamanoLetraNumeroCompas, false, Integer.toString(compas.getNumeroCompas()), 
					compas.getXIni() - config.xNumeroCompas, compas.getYIni() - config.yNumeroCompas));
		}
	}
	
	private void dibujarLineasDePentagramaDeCompas(final Compas compas) 
	{
		dibujarLineasLaterales(compas);
		dibujarLineasHorizontales(compas);
	}
	
	private void dibujarLineasLaterales(final Compas compas)
	{
		ordenesDibujo.add( new OrdenDibujo(
				1, compas.getXIni(), compas.getYIni(), compas.getXIni(), compas.getYFin()));
		ordenesDibujo.add( new OrdenDibujo(
				1, compas.getXFin(), compas.getYIni(), compas.getXFin(), compas.getYFin()));
	}
	
	private void dibujarLineasHorizontales(final Compas compas)
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
	
	private void dibujarClaves(final Compas compas) 
	{
		for (int i=0; i<compas.numClaves(); i++) 
		{
			final Clave clave = compas.getClave(i);
			ordenesDibujo.add( new OrdenDibujo( clave.imagenClave, clave.x, clave.y));
		}
	}
	
	private void dibujarQuintas(final Compas compas) 
	{
		if (compas.hayQuintas()) {
			dibujarArmaduraReBemol(compas.getQuintas());
		}
	}

	private void dibujarArmaduraReBemol(final Quintas quintas)
	{
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.x, 
				quintas.margenY + config.distanciaLineasPentagrama));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.x + 20, 
				quintas.margenY - config.distanciaLineasPentagramaMitad));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.x + 40, 
				quintas.margenY + config.distanciaLineasPentagramaMitad +
				config.distanciaLineasPentagrama));
		ordenesDibujo.add( new OrdenDibujo(
				bitmapManager.getFlat(), quintas.x + 60, quintas.margenY));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.x + 80, 
				quintas.margenY + config.distanciaLineasPentagrama * 2));

		final int offset = config.distanciaLineasPentagrama * 4 +
				config.distanciaPentagramas + config.distanciaLineasPentagrama;
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.x, 
				quintas.margenY + offset + config.distanciaLineasPentagrama));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.x + 20, 
				quintas.margenY + offset - config.distanciaLineasPentagramaMitad));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.x + 40, 
				quintas.margenY + offset + config.distanciaLineasPentagramaMitad +
				config.distanciaLineasPentagrama));
		ordenesDibujo.add( new OrdenDibujo(
				bitmapManager.getFlat(), quintas.x + 60, quintas.margenY + offset));
		ordenesDibujo.add( new OrdenDibujo(bitmapManager.getFlat(), quintas.x + 80, 
				quintas.margenY + offset + config.distanciaLineasPentagrama * 2));
	}
	
	private void dibujarIntensidades(final Compas compas) 
	{
		for (int i=0; i<compas.numIntensidades(); i++) 
		{
			final Intensidad intensidad = compas.getIntensidad(i);
			ordenesDibujo.add( new OrdenDibujo(intensidad.imagen, 
					intensidad.x, intensidad.y));
		}
	}
	
	private void dibujarPedales(final Compas compas) 
	{
		final Object[] pedales = compas.getPedales();
		Pedal pedal;
		
		for (int i=0; i<pedales.length; i++) 
		{
			pedal = (Pedal) pedales[i];
			
			ordenesDibujo.add( new OrdenDibujo( pedal.imagen, pedal.x, pedal.y));
		}
	}
	
	private void dibujarTempo(final Compas compas) 
	{
		if (compas.hayTempo() && compas.getTempo().dibujar()) {
			crearOrdenesDeDibujoDeTempo(compas.getTempo());	
		}
	}
	
	private void crearOrdenesDeDibujoDeTempo(Tempo tempo)
	{
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
	
	private void dibujarTextos(final Compas compas) 
	{
		final int numTextos = compas.numTextos();
		
		for (int i=0; i<numTextos; i++) 
		{
			final Texto texto = compas.getTexto(i);
			ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraWords, 
					false, texto.texto, texto.x, texto.y));
		}
	}
	
	private void dibujarCrescendosYDiminuendos(final Compas compas) 
	{		
		final Object[] wedges = compas.getWedges();
		Wedge wedge;
		
		for (int i=0; i<wedges.length; i++) 
		{
			wedge = (Wedge) wedges[i];
			
			if (wedge.crescendo()) {
				ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
						wedge.getYIni() + config.alturaCrescendos / 2,
						wedge.getXFin(), wedge.getYIni()));
				ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
						wedge.getYIni() + config.alturaCrescendos / 2,
						wedge.getXFin(), wedge.getYIni() + config.alturaCrescendos));
			} else {
				ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
						wedge.getYIni(), wedge.getXFin(), 
						wedge.getYIni() + config.alturaCrescendos / 2));
				ordenesDibujo.add( new OrdenDibujo(2, wedge.getXIni(), 
						wedge.getYIni() + config.alturaCrescendos, 
						wedge.getXFin(), wedge.getYIni() + config.alturaCrescendos / 2));
			}
		}
	}
	
	private void dibujarNotas(final Compas compas) 
	{
		final ArrayList<Nota> notas = compas.getNotas();
		final int numNotas = notas.size();
		
		for (int i=0; i<numNotas; i++) 
		{
			notaActual = i;
			dibujarNota(notas.get(i), compas.getYIni());
		}
	}
	
	private void dibujarNota(final Nota nota, final int yIni)
	{
		actualizarLimitesDeLigaduraDeExpresion(nota.getY());
		actualizarYAnterior(nota);
		
		dibujarCabezaDeNota(nota);
		final int yBeams = dibujarCuerpoDeNota(nota);

		dibujarFigurasGraficasDeNota(nota, yBeams);
		dibujarLineasFueraDelPentagrama(nota, yIni);
		
		gestionarOctavarium(nota, yIni - config.distanciaLineasPentagrama * 6);
	}
	
	private void actualizarLimitesDeLigaduraDeExpresion(final int y)
	{
		if (buscandoLigaduraExpresion) {
			if (ligaduraExpresionYArriba > y) {
				ligaduraExpresionYArriba = y;
			}
			if (ligaduraExpresionYAbajo < y) {
				ligaduraExpresionYAbajo = y;
			}
		}
	}
	
	private void actualizarYAnterior(final Nota nota)
	{
		if (!nota.acorde()) {
			yAnterior = nota.getY();
		}
	}
	
	private void dibujarCabezaDeNota(final Nota nota) 
	{
		final int desplazamiento = obtenerDesplazamientoDeNota(nota);
		ordenesDibujo.add( new OrdenDibujo(
				calculador.obtenerImagenDeCabezaDeNota(nota), 
				nota.getX() + desplazamiento, nota.getY()));
	}
	
	private int obtenerDesplazamientoDeNota(final Nota nota)
	{
		int desplazamiento = 0;
		if (nota.desplazadaALaIzquierda()) {
			desplazamiento -= config.anchoCabezaNota;
		}
		if (nota.desplazadaALaDerecha()) {
			desplazamiento += config.anchoCabezaNota;
		}
		
		return desplazamiento;
	}
	
	private int dibujarCuerpoDeNota(final Nota nota)
	{
		int yBeams = 0;
		
		if (nota.tieneBeams()) {
			yBeams = gestionarBeams(nota);
		} else {
			if (nota.tienePlica()) {
				dibujarPlicaDeNota(nota, 0);
				dibujarCorcheteDeNota(nota);
			}
		}

		return yBeams;
	}
	
	private int gestionarBeams(final Nota nota)
	{
		int yBeams = 0;
		final int beamId = guardarBeamDeNota(nota);
		
		if (nota.beamFinal())
		{
			final int numBeams = beams.size();
			
			yBeams = colocarBeamsALaMismaAltura(beamId, numBeams);
			dibujarBeams(yBeams, beamId, numBeams);
		}
		
		return yBeams;
	}
	
	private int guardarBeamDeNota(final Nota nota)
	{
		final IndiceNota beam = new IndiceNota();
		beam.compas = compasActual;
		beam.nota = notaActual;
		beam.ligadura = (byte) 0;
		beam.beamId = nota.getBeamId();
		
		beams.add(beam);
		return beam.beamId;
	}

	private int colocarBeamsALaMismaAltura(final int beamId, final int numBeams) 
	{
		IndiceNota beam;
		Nota nota;
		int yBeams = -1;
		int pentagrama = -1;

		for (int i=0; i<numBeams; i++) 
		{
			beam = beams.get(i);
			
			if (beamId == beam.beamId) 
			{
				nota = partitura.getCompas(beam.compas).getNota(beam.nota);
				
				if (i == 0) {
					yBeams = nota.haciaArriba() ? Integer.MAX_VALUE : 0;
					pentagrama = nota.getPentagrama();
				}
				
				//  No todas las notas están en el mismo pentagrama
				if (pentagrama != nota.getPentagrama()) {
					yBeams = partitura.getCompas(beam.compas).getYIni() +
							config.distanciaLineasPentagrama * 4 + config.distanciaPentagramas / 2;
					break;
				}
				
				yBeams = actualizarYBeams(yBeams, nota);
				
				if (i == numBeams - 1) {
					yBeams = sumarLongitudDePlicaAlBeam(yBeams, nota);
				}
			}
		}

		return yBeams;
	}
	
	private int actualizarYBeams(int yBeams, Nota nota)
	{
		if (nota.haciaArriba()) {
			if (yBeams > nota.getY()) {
				yBeams = nota.getY();
			}
		} else {
			if (yBeams < nota.getY()) {
				yBeams = nota.getY();
			}
		}
		
		return yBeams;
	}
	
	private int sumarLongitudDePlicaAlBeam(int yBeams, Nota nota)
	{
		final int longitudPlica = nota.notaDeGracia() ? 
				config.longitudPlicaNotaGracia : config.longitudPlica;
		
		return nota.haciaArriba() ? yBeams - longitudPlica : yBeams + longitudPlica;
	}
	
	private void dibujarBeams(final int yBeams, final int beamId, final int numBeams) 
	{			
		Collections.sort(beams);
		
		final int primerBeam = obtenerIndicePrimerBeam(numBeams, beamId);
		int indBeam = primerBeam;

		while (beams.get(indBeam).beamId == beamId) 
		{
			final int indCompasAnt = beams.get(indBeam).compas;
			final int indNotaAnt = beams.get(indBeam).nota;
			final Nota notaAnt = partitura.getCompas(indCompasAnt).getNota(indNotaAnt);
			
			final int distanciaBeams = obtenerDistanciaBeams(notaAnt);
			final int anchoBeams = notaAnt.notaDeGracia() ? config.anchoBeamsNotaGracia : config.anchoBeams;

			if (ultimaNota(indBeam, numBeams, beamId)) 
			{
				anadirOrdenesDibujoHooksUltimaNota(notaAnt, anchoBeams, yBeams, distanciaBeams);
			}
			else {
				final int xAntBeams = obtenerXAnterior(notaAnt);
				final int xSigBeams = obtenerXSiguiente(indBeam);
				final byte beam = notaAnt.getBeam();
				
				anadirOrdenesDibujoBeams(beam, xAntBeams, xSigBeams, anchoBeams, yBeams, distanciaBeams);
			}
			
			dibujarPlicaDeNota(notaAnt, yBeams);
			
			if (++indBeam == numBeams) {
				break;
			}
		}
		
		eliminarBeamsDibujados(primerBeam, indBeam);
	}
	
	private int obtenerIndicePrimerBeam(final int numBeams, final int beamId)
	{
		int i = -1;
		
		for (int j=0; j<numBeams; j++) 
		{
			if (beams.get(j).beamId == beamId) {
				i = j;
				break;
			}
		}
		
		return i;
	}
	
	private int obtenerDistanciaBeams(Nota nota)
	{
		int distanciaBeams = nota.notaDeGracia() ? 
				config.distanciaEntreBeamsNotaGracia : config.distanciaEntreBeams;
		
		return nota.haciaArriba() ? distanciaBeams : distanciaBeams * -1;
	}
	
	private boolean ultimaNota(final int indBeam, final int numBeams, final int beamId)
	{
		return indBeam == numBeams - 1 || beams.get(indBeam + 1).beamId != beamId;
	}
	
	private int obtenerXAnterior(Nota nota)
	{
		int xAnteriorBeams = nota.getX();
		
		if (nota.haciaArriba()) 
		{
			final int anchoCabezaNota = 
				nota.notaDeGracia() ? config.anchoCabezaNotaGracia : config.anchoCabezaNota;
			
			xAnteriorBeams += anchoCabezaNota;
		}
		
		return xAnteriorBeams;
	}
	
	private int obtenerXSiguiente(int indiceBeam)
	{
		final int indCompasSig = beams.get(indiceBeam + 1).compas;
		final int indNotaSig = beams.get(indiceBeam + 1).nota;
		final Nota notaSig = partitura.getCompas(indCompasSig).getNota(indNotaSig);
		
		int xSiguienteBeams = notaSig.getX();
		if (notaSig.haciaArriba()) 
		{
			final int anchoCabezaNota = 
				partitura.getCompas(indCompasSig).getNota(indNotaSig).notaDeGracia() ? 
					config.anchoCabezaNotaGracia : config.anchoCabezaNota;
			
			xSiguienteBeams += anchoCabezaNota;
		}
		
		return xSiguienteBeams;
	}
	
	private void anadirOrdenesDibujoHooksUltimaNota(final Nota nota, int anchoBeams,
			int yBeams, int distanciaBeams)
	{
		final int xLastBeam = nota.getX();
		final int offset = nota.haciaArriba() ? config.anchoCabezaNota : 0;
		
		switch (nota.getBeam()) 
		{
			case 4:				
				ordenesDibujo.add( new OrdenDibujo(
						anchoBeams, xLastBeam + offset, yBeams + distanciaBeams * 2,
						xLastBeam + offset - config.anchoHooks, yBeams + distanciaBeams * 2));
				break;
				
			case 6:				
				ordenesDibujo.add( new OrdenDibujo(
						anchoBeams, xLastBeam + offset, yBeams + distanciaBeams,
						xLastBeam + offset - config.anchoHooks, yBeams + distanciaBeams));
				break;
		}
	}
	
	private void anadirOrdenesDibujoBeams(final byte beam, final int xAntBeams, final int xSigBeams,
			final int anchoBeams, final int yBeams, final int distanciaBeams)
	{
		switch (beam) 
		{
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
		}
	}
	
	private void eliminarBeamsDibujados(final int primerBeam, final int indiceBeam)
	{
		for (int j = primerBeam; j<indiceBeam; j++) {
			beams.remove(primerBeam);
		}
	}
	
	private void dibujarPlicaDeNota(final Nota nota, final int yBeams) 
	{
		final int mitadCabezaNota = nota.notaDeGracia() ? 
				config.mitadCabezaVerticalNotaGracia : config.mitadCabezaVertical;
		final int anchoCabezaNota = nota.notaDeGracia() ? 
				config.anchoCabezaNotaGracia : config.anchoCabezaNota;
		final int longitudPlica = nota.notaDeGracia() ? 
				config.longitudPlicaNotaGracia : config.longitudPlica;
		
		int x1 = 0;
		final int y1 = nota.getY() + mitadCabezaNota;
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
		
		if (yBeams != 0) { 
			y2 = yBeams;
		}
		
		ordenesDibujo.add( new OrdenDibujo(2, x1, y1, x2, y2));
	}
	
	private void dibujarCorcheteDeNota(final Nota nota) 
	{
		//  Si la nota forma parte de un acorde, su corchete ya fue dibujado, 
		//  por lo que no hay que dibujarlo de nuevo
		if (!nota.acorde() && !nota.silencio()) 
		{
			final int anchoCabezaNota = nota.notaDeGracia() ? 
					config.anchoCabezaNotaGracia : config.anchoCabezaNota;
			final int longitudPlica = nota.notaDeGracia() ? 
					config.longitudPlicaNotaGracia : config.longitudPlica;
			final int anchoCorchete = nota.notaDeGracia() ? 
					config.largoImagenCorcheteNotaGracia : config.largoImagenCorchete;
			final int distanciaCorchete = nota.haciaArriba() ?
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
			}
			
			if (nota.tieneSlash()) {
				dibujarSlash(x, y);
			}
		}
	}
	
	private void dibujarCorcheteDeNotaCrearOrden(final Nota nota, final int x, final int y, final int distanciaCorchete) 
	{
		ordenesDibujo.add( new OrdenDibujo(
				calculador.obtenerImagenDeCorcheteDeNota(nota), x, y + distanciaCorchete));
	}
	
	private void dibujarSlash(final int x, final int y) 
	{
		ordenesDibujo.add( new OrdenDibujo(2, x + config.xInicioSlash, 
				y + config.yInicioSlash, x - config.xFinSlash, y + config.yFinSlash));
	}
	
	private void dibujarFigurasGraficasDeNota(final Nota nota, final int yBeams) 
	{
		final ArrayList<Byte> figurasGraficas = nota.getFigurasGraficas();
		final int numFiguras = figurasGraficas.size();
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
	
	private void dibujarFiguraGrafica(final Nota nota, final byte figura, final int yBeams, final int xSillo) 
	{
		int x = 0;
		IndiceNota indiceNota;

		switch (figura) {
			case 3:
				xIniTresillo = nota.getX();
				break;

			case 4:
				final int margenTresillo = nota.haciaArriba() ? 
						- config.yTresilloArriba : config.yTresilloAbajo;
				int x_tresillo = (nota.getX() + xIniTresillo) / 2;
				if (nota.haciaArriba()) {
					x_tresillo += config.xTresillo;
				}
				
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
				if (nota.haciaArriba()) {
					ordenesDibujo.add( new OrdenDibujo(config.radioStaccatos, 
							nota.getX() + config.xStaccato, nota.getY() + config.yStaccatoArriba));
				} else { 
					ordenesDibujo.add( new OrdenDibujo(config.radioStaccatos, 
							nota.getX() + config.xStaccato, nota.getY() - config.yStaccatoAbajo));
				}
				break;

			case 9:
				ordenesDibujo.add( new OrdenDibujo(config.tamanoLetraTapping, 
						false, "T", nota.getX(), nota.getY() - config.yTapping));
				break;

			case 10:
				indiceNota = new IndiceNota();
				indiceNota.compas = compasActual;
				indiceNota.nota = notaActual;
				indiceNota.ligadura = nota.getLigaduraUnion();
				indiceNota.beamId = (byte) 0;
				
				ligaduras.add(indiceNota);
				break;
				
			case 11:
				final int indLigaduraUnion = encontrarIndiceLigadura(nota.getLigaduraUnion());
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
				x = nota.haciaArriba() ? nota.getX() - config.offsetAccent : nota.getX();
				
				ordenesDibujo.add( new OrdenDibujo(
						bitmapManager.getAccent(), x, nota.getY() - config.yAccentUp));
				break;
				
			case 31:
				ordenesDibujo.add( new OrdenDibujo(bitmapManager.getAccent(), nota.getX(), 
						nota.getY() + config.longitudPlica + config.yAccentUp));
				break;
				
			case 32:
				indiceNota = new IndiceNota();
				indiceNota.compas = compasActual;
				indiceNota.nota = notaActual;
				indiceNota.ligadura = nota.getLigaduraExpresion();
				indiceNota.beamId = (byte) 0;
				
				ligaduras.add(indiceNota);
				buscandoLigaduraExpresion = true;
				break;
				
			case 33:
				final int indLigaduraExpresion = encontrarIndiceLigadura(nota.getLigaduraExpresion());
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
		}
	}
	
	private void dibujarSlide(final Nota nota, final int xIniSlide, final int yIniSlide) 
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
	
	private void dibujarLigadura(final int indLigadura, final Nota notaFinal, final boolean union) 
	{
		final int compasNotaInicio = ligaduras.get(indLigadura).compas;
		final int notaInicio = ligaduras.get(indLigadura).nota;
		final Nota notaInicial = partitura.getCompas(compasNotaInicio).getNota(notaInicio);

		if (union) {
			dibujarLigaduraUnion(notaInicial, notaFinal);
		} else {
			dibujarLigaduraExpresion(notaInicial, notaFinal);
		}
		
		ligaduras.remove(indLigadura);
	}
	
	private void dibujarLigaduraUnion(final Nota notaInicial, final Nota notaFinal) 
	{
		if (notaInicial.getX() < notaFinal.getX()) {
			dibujarLigaduraUnionNormal(notaInicial, notaFinal);
		} else {
			dibujarLigaduraUnionPartida(notaInicial, notaFinal);
		}
	}
	
	private void dibujarLigaduraExpresion(final Nota notaInicial, final Nota notaFinal) 
	{
		if (notaInicial.getX() < notaFinal.getX()) {
			dibujarLigaduraExpresionNormal(notaInicial, notaFinal);
		} else {
			dibujarLigaduraExpresionPartida(notaInicial, notaFinal);
		}
	}
	
	private void dibujarLigaduraUnionNormal(final Nota notaInicial, final Nota notaFinal) 
	{
		final int anchoCabezaNota = notaInicial.notaDeGracia() ? 
				config.anchoCabezaNotaGracia : config.anchoCabezaNota;
		RectF rectf = null;
		
		if (notaInicial.ligaduraUnionEncima()) {
			rectf = new RectF(notaInicial.getX() + anchoCabezaNota +
				config.xLigadurasUnion, notaFinal.getY() - config.yLigadurasUnion, 
					notaFinal.getX() - config.xLigadurasUnion, 
						notaFinal.getY() + config.alturaArcoLigadurasUnion);
		} else {
			rectf = new RectF(notaInicial.getX() + anchoCabezaNota +
				config.xLigadurasUnion, notaFinal.getY(), 
					notaFinal.getX() - config.xLigadurasUnion, 
						notaFinal.getY() + config.alturaArcoLigadurasUnion);
		}
		
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, notaInicial.ligaduraUnionEncima()));
	}
	
	private void dibujarLigaduraExpresionNormal(final Nota notaInicial, final Nota notaFinal) 
	{
		final int anchoCabezaNota = notaInicial.notaDeGracia() ? 
				config.anchoCabezaNotaGracia : config.anchoCabezaNota;
		RectF rectf = null;
		final int y = Math.min(notaInicial.getY(), notaFinal.getY());
		
		if (notaInicial.ligaduraExpresionEncima()) {
			rectf = new RectF(notaInicial.getX(), y - config.yLigadurasExpresion, 
				notaFinal.getX() + anchoCabezaNota, y + config.alturaArcoLigadurasExpresion);
		} else { 
			rectf = new RectF(notaInicial.getX(), y + config.yLigadurasExpresion / 2, 
				notaFinal.getX() + anchoCabezaNota, 
				y + config.yLigadurasExpresion / 2 + config.alturaArcoLigadurasExpresion);
		}
		
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 
				notaFinal.getAnguloRotacionLigaduraExpresion(), notaInicial.ligaduraExpresionEncima()));
	}
	
	private void dibujarLigaduraUnionPartida(final Nota notaInicial, final Nota notaFinal) 
	{
		RectF rectf = new RectF(notaInicial.getX() + config.anchoCabezaNota +
				config.xLigadurasUnion, notaInicial.getY() - config.yLigadurasUnion, 
				config.xFinalPentagramas, notaInicial.getY() + config.alturaArcoLigadurasUnion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, notaInicial.ligaduraUnionEncima()));

		final int yFinal = notaInicial.getY() +
				(config.distanciaLineasPentagrama * 4 + config.distanciaPentagramas) * 
				(partitura.getStaves());
		rectf = new RectF(config.xInicialPentagramas, yFinal - config.yLigadurasUnion, 
				notaFinal.getX() - config.xLigadurasUnion, yFinal + config.alturaArcoLigadurasUnion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, notaInicial.ligaduraUnionEncima()));
	}
	
	private void dibujarLigaduraExpresionPartida(final Nota notaInicial, final Nota notaFinal) 
	{
		final int anchoCabezaNota = notaInicial.notaDeGracia() ? 
				config.anchoCabezaNotaGracia : config.anchoCabezaNota;
		
		RectF rectf = new RectF(notaInicial.getX(), notaInicial.getY() - config.yLigadurasExpresion, 
				config.xFinalPentagramas, notaInicial.getY() + config.alturaArcoLigadurasExpresion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, notaInicial.ligaduraExpresionEncima()));

		rectf = new RectF(config.xInicialPentagramas, notaFinal.getY() - config.yLigadurasExpresion, 
				notaFinal.getX() + anchoCabezaNota, notaFinal.getY() + config.alturaArcoLigadurasExpresion);
		ordenesDibujo.add( new OrdenDibujo(2, rectf, 0, notaInicial.ligaduraExpresionEncima()));
	}

	private int gestionarLigaduras(final Nota nota, final ArrayList<Byte> figurasGraficas, 
			final int ind, final int yBeams) 
	{
		if (figurasGraficas.get(ind + 1) == 0) 
		{
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
	
	private void dibujarAlteraciones(final Nota nota, final Bitmap alteracion, final int offset) 
	{
		final int xAccidental = nota.notaDeGracia() ? 
				config.xAccidentalNotaGracia : config.xAccidental;
		int x = nota.getX() - xAccidental - offset;
		if (nota.desplazadaALaIzquierda()) {
			x -= config.anchoCabezaNota - offset;
		}
		
		ordenesDibujo.add( new OrdenDibujo(alteracion, x, nota.getY() - config.yAccidental));
	}
	
	//  Busca, en el array de ligaduras de unión, el índice
	//  del elemento que contiene el inicio de esta ligadura
	private int encontrarIndiceLigadura(final byte ligadura) {
		final int numLigaduras = ligaduras.size();
		int indice = -1;
		
		for (int i=0; i<numLigaduras; i++) {
			if (ligaduras.get(i).ligadura == ligadura) {
				indice = i;
				break;
			}
		}
		
		return indice;
	}
	
	private void gestionarAlteracion(final Nota nota, final ArrayList<Byte> figurasGraficas, 
			final int ind, final int yBeams, final int xAlteraciones) 
	{
		final int desplazamiento = obtenerDesplazamientoDeNota(nota);
		
		nota.setX(nota.getX() + desplazamiento);
		dibujarFiguraGrafica(nota, figurasGraficas.get(ind), yBeams, xAlteraciones);
	}

	private void dibujarLineasFueraDelPentagrama(final Nota nota, final int yIniCompas) 
	{
		int yMarginCustom = yIniCompas + 
				(config.distanciaLineasPentagrama * 4 + 
						config.distanciaPentagramas) * (nota.getPentagrama() - 1);
		
		AbstractOutOfStaveLineDrawer outOfStaveLineDrawer = nota.getY() < yMarginCustom ? 
				new AboveStaveLineDrawer(ordenesDibujo) : new BelowStaveLineDrawer(ordenesDibujo);
		
		if (outOfStaveLineDrawer instanceof BelowStaveLineDrawer) {
			yMarginCustom += config.distanciaLineasPentagrama * 4;
		}
		
		final int yNota = nota.notaDeGracia() ? nota.getY() - config.margenNotaGracia : nota.getY();
		outOfStaveLineDrawer.drawOutOfStaveLines(nota, yMarginCustom, yNota);
	}

	private void gestionarOctavarium(final Nota nota, final int marginY) 
	{
		if (nota.octavada()) {
			prepararValoresOctavarium(nota.getX(), marginY);
		}
		else {
			if (octavarium > 0) {
				dibujarOctavarium();
				inicializarValoresOctavarium();
			}
		}
	}
	
	private void prepararValoresOctavarium(final int x, final int marginY)
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
	private void dibujarBarlines(final Compas compas) 
	{
		final int numBarlines = compas.numBarlines();

		for (int i=0; i<numBarlines; i++) {
			ordenesDibujo.add( new OrdenDibujo(
				4, compas.getXFin(), compas.getYIni(), compas.getXFin(), compas.getYFin()));
			ordenesDibujo.add( new OrdenDibujo(
					2, compas.getXFin() - config.margenBarlines, 
					compas.getYIni(), compas.getXFin() - config.margenBarlines, compas.getYFin()));
		}
	}
}