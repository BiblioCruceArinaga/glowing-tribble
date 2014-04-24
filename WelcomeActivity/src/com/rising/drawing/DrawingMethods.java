package com.rising.drawing;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DrawingMethods {
	
	private ArrayList<OrdenDibujo> ordenesDibujo = new ArrayList<OrdenDibujo>();
	
	//  Partitura y sus datos "físicos" (límites, densidad de pantalla, etc.)
	private Partitura partitura;
	private int margin_top;
	private int width;
	private float density;
	
	//  Variables para la gestión de los múltiples compases
	private int compas_margin_y = 0;
	private int compas_x = 50;
	private final int dist_lineas_pent = 19;
	private final int dist_pentagramas = 150;
	private final int x_inicial_pentagramas = 50;
	private int x_final_pentagramas = 0;
	private final int margen_lateral_compases = 30;
	private final int radio_puntillos = 5;
	private final int unidad_desplazamiento = 25;
	
	//  Variables para la gestión de las múltiples notas
	private boolean octavarium = false;
	
	//  Bitmaps
	private final Bitmap trebleclef;
	private final Bitmap bassclef;
	private final Bitmap mezzoforte;
	private final Bitmap forte;
	private final Bitmap rectangle;
	private final Bitmap quarterrest;
	private final Bitmap eighthrest;
	private final Bitmap noterest16;
	private final Bitmap noterest32;
	private final Bitmap noterest64;
	private final Bitmap whitehead;
	private final Bitmap blackheadlittle;
	private final Bitmap blackhead;
	private final Bitmap head;
	private final Bitmap headinv;
	private final Bitmap headinvlittle;
	private final Bitmap sharp;
	private final Bitmap flat;
	private final Bitmap natural;
	private final Bitmap ligato;
	private final Bitmap vibrato;
	private final Bitmap tremolobar;
	private final Bitmap hammeron;
	private final Bitmap bend;
	private final Bitmap octavariumImage;
	
	public DrawingMethods(Partitura partitura, int margin_top, int width, Resources resources) {
		this.partitura = partitura;
		this.margin_top = margin_top;
		this.width = width;
		this.density = density;
		
		compas_margin_y = margin_top;
		x_final_pentagramas = width - x_inicial_pentagramas;
		
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
	}
	
	public ArrayList<OrdenDibujo> crearOrdenesDeDibujo() {
		
		//  Obra
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, 80);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_ALIGN, -1);
		ordenDibujo.setTexto(partitura.getWork());
		ordenDibujo.setX1(width / 2);
		ordenDibujo.setY1(compas_margin_y + 100);
		ordenesDibujo.add(ordenDibujo);
		
		//  Autor
		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, 50);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_ALIGN, -1);
		ordenDibujo.setTexto(partitura.getCreator());
		ordenDibujo.setX1(width / 2);
		ordenDibujo.setY1(compas_margin_y + 180);
		ordenesDibujo.add(ordenDibujo);
		
		//  Densidad
		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, 50);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_ALIGN, -1);
		ordenDibujo.setTexto(density + "");
		ordenDibujo.setX1(width / 2);
		ordenDibujo.setY1(compas_margin_y + 230);
		ordenesDibujo.add(ordenDibujo);
		
		compas_margin_y += 300;
		
		crearOrdenesDeCompases(partitura);
		return ordenesDibujo;
	}
	
	private void crearOrdenesDeCompases(Partitura partitura) {
		ArrayList<Compas> compases = partitura.getCompases();
		int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++)
			crearOrdenesDeCompas(compases.get(i));
	}
	
	private void crearOrdenesDeCompas(Compas compas) {
		compas.setXIni(compas_x);
		compas.setYIni(compas_margin_y);
		
		//  Margen inicial
		compas_x += margen_lateral_compases;
		/*
		//  Puesto que la posición de cada elemento está calculada
		//  de antemano, da igual en qué orden se dibujen
		dibujarClaves(compas.getClaves());
		dibujarTempo();
		dibujarBarlines();
		dibujarEndings();
		dibujarRepeticiones();
		dibujarIntensidad();
		dibujarPedales();
		dibujarTexto();
		
		ArrayList<Nota> notas = compas.getNotas();
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++) {
			crearOrdenesDeNota(notas.get(i));
		}
		*/
		
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
		ordenDibujo.setImagen(blackhead);
		ordenDibujo.setX1(compas_x);
		ordenDibujo.setY1(compas_margin_y + dist_lineas_pent * 2);
		ordenesDibujo.add(ordenDibujo);
		compas_x += 50;
		
		//  Margen final
		compas_x += margen_lateral_compases;
		
		compas.setXFin(compas_x);
		compas.setYFin(compas_margin_y + dist_lineas_pent * 4 + 
				(dist_pentagramas + dist_lineas_pent * 4) * (partitura.getStaves() - 1));
		
		if (compas.getXFin() > x_final_pentagramas) {
			moverCompasAlSiguienteRenglon(compas);
		}
		
		dibujarLineasDePentagramaDeCompas(compas);
	}
	/*
	private void dibujarClaves(ArrayList<ElementoGrafico> claves) {
		int numClaves = claves.size();
		int x_position = -1;
		
		for (int i=0; i<numClaves; i++) {
			x_position = obtenerXDeElementoGrafico(claves.get(i).getPosition());
			
			byte pentagrama = claves.get(i).getValue(0);
			byte clave = claves.get(i).getValue(1);
			byte alteracion = claves.get(i).getValue(2);
			
			//  El margen Y depende del pentagrama al que pertenezca el compás
			int marginY = compas_margin_y + 
					(dist_lineas_pent * 4 + dist_pentagramas) * (pentagrama - 1);
			
			OrdenDibujo ordenDibujo = new OrdenDibujo();
			switch (alteracion) {
				case 0:
					ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
					ordenDibujo.setImagen(obtenerImagenDeClave(clave));
					ordenDibujo.setX1(x_position);
					ordenDibujo.setY1(obtenerPosicionYDeClave());
					ordenesDibujo.add(ordenDibujo);
					break;
					
				case 1:
					octavarium = true;

					ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
					ordenDibujo.setImagen(octavariumImage);
					ordenDibujo.setX1(x_position);
					ordenDibujo.setY1(marginY - dist_lineas_pent * 4);
					ordenesDibujo.add(ordenDibujo);
					break;
				
				case -1:
					octavarium = false;

					ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
					ordenDibujo.setRadius(radio_puntillos);
					ordenDibujo.setX1(x_position);
					ordenDibujo.setY1(marginY - dist_lineas_pent * 4);
					ordenesDibujo.add(ordenDibujo);
					break;
					
				default: 
					break;
			}
		}
	}
	*/
	
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
		float y_linea = compas.getYIni();
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
				
				y_linea += dist_lineas_pent;
			}
			
			y_linea += dist_pentagramas - dist_lineas_pent;
			pentagramas_pendientes--;
		
		} while (pentagramas_pendientes > 0);
	}
	
	private void moverCompasAlSiguienteRenglon(Compas compas) {
		float distancia_x = compas.getXIni() - x_inicial_pentagramas;
		compas.setXIni(x_inicial_pentagramas);
		compas.setXFin(compas.getXFin() - distancia_x);
		
		compas_x = x_inicial_pentagramas;
		compas_margin_y = compas_margin_y + 
				(dist_lineas_pent * 4 + dist_pentagramas) * partitura.getStaves();
		
		compas.setYIni(compas_margin_y);
		compas.setYFin(compas_margin_y + dist_lineas_pent * 4 + 
				(dist_pentagramas + dist_lineas_pent * 4) * (partitura.getStaves() - 1));
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
	
	private int obtenerXDeElementoGrafico(int position) {
		return position * unidad_desplazamiento / partitura.getDivisions();
	}
}