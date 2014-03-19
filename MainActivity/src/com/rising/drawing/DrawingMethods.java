package com.rising.drawing;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawingMethods {
	
	/*
	 * Órdenes de dibujo
	 * 
	 * 0 - No hay órdenes
	 * 1 - Dibujar una línea
	 * 2 - Dibujar un bitmap
	 * 3 - Dibujar un círculo
	 * 4 - Dibujar texto
	 * 5 - Establecer el ancho de las líneas del paint
	 * 6 - Establecer el tamaño del texto
	 * 7 - Establecer estilo del paint
	 * 8 - Establecer color del paint
	 * 
	 */

	static int indOrdenesStrings;
	static int[] orden;

	//  Cálculos predefinidos
	static int yDistanceBy2;
	static int yDistanceBy3;
	static int yDistanceBy4;
	static int yDistanceBy5;
	static int yDistanceBy6;
	static int yDistanceBy7;
	
	//  Dibujar partitura
	public static ArrayList<int[]> drawScore(
        ArrayList<String> ordenesStrings, 
        Canvas canvas, 
        Paint paint, 
		final Resources resources, 
        final Partitura partitura, 
        final int y_distance, 
        final int y_distance_half, 
		final int y_distance_staves, 
        final int staves, 
        final int x_ini, 
        final int x_end, 
		final int y_distance_limits, 
        final String nombreObra, 
        final String nombreAutor) {

		yDistanceBy2 = y_distance * 2;
		yDistanceBy3 = y_distance * 3;
		yDistanceBy4 = y_distance * 4;
		yDistanceBy5 = y_distance * 5;
		yDistanceBy6 = y_distance * 6;
		yDistanceBy7 = y_distance * 7;
		
		ArrayList<int[]> ordenes = new ArrayList<int[]>();
		indOrdenesStrings = 0;

		//  Variable con el número de compases en la partitura
		final int compasesPartitura = partitura.numeroDeCompases();
		
		int margenY;
		int division;

		//  Grosor de línea de pentagrama
		orden = new int[5];
    	orden[0] = 5;
    	orden[1] = 1;
    	ordenes.add(orden);
		
		//  Color negro
		orden = new int[5];
		orden[0] = 8;
		orden[1] = 0;
		orden[2] = 0;
		orden[3] = 0;
		ordenes.add(orden);
		
		//  Metadatos
		orden = new int[5];
		orden[0] = 6;
		orden[1] = 42;
		ordenes.add(orden);
    	
		orden = new int[5];
    	orden[0] = 4;
    	ordenesStrings.add(nombreObra);
    	orden[1] = (canvas.getWidth() - 42 * Math.abs(nombreObra.length() / 2)) / 2;
    	orden[2] = 70;
    	orden[3] = indOrdenesStrings++;
    	ordenes.add(orden);
    	
    	orden = new int[5];
    	orden[0] = 6;
    	orden[1] = 28;
    	ordenes.add(orden);
    	
    	orden = new int[5];
    	orden[0] = 4;
    	ordenesStrings.add(nombreAutor);
    	orden[1] = (canvas.getWidth() - 28 * Math.abs(nombreAutor.length() / 2)) / 2;
    	orden[2] = 120;
    	orden[3] = indOrdenesStrings++;
    	ordenes.add(orden);
		
		//  Imprime los compases
		for (int i = 0; i < compasesPartitura; i++) {
			
			margenY = partitura.margenY(i);
			division = partitura.division(i);
			
			//  Dibuja la partitura (ineficiente redibujarlo con cada compï¿½s, pero servirï¿½ por ahora)
			drawStaves(ordenes, canvas, paint, margenY, x_ini, x_end, y_distance, y_distance_staves, staves);
			
			//  Dibuja el compï¿½s
        	DrawingMethods.drawBar(ordenes, ordenesStrings, canvas, paint, resources, partitura.compas(i), margenY, 
        			y_distance, y_distance_half, y_distance_staves, staves, y_distance_limits);
			
			//  Dibuja la linea vertical de fin de compï¿½s
        	orden = new int[5];
        	orden[0] = 5;
        	orden[1] = 2;
        	ordenes.add(orden);
        	
        	orden = new int[5];
        	orden[0] = 1;
        	orden[1] = division;
        	orden[2] = margenY;
        	orden[3] = division;
        	orden[4] = margenY + yDistanceBy4;
        	ordenes.add(orden);

			if (staves == 2) {
				orden = new int[5];
				orden[0] = 1;
	        	orden[1] = division;
	        	orden[2] = margenY + yDistanceBy4 + y_distance_staves;
	        	orden[3] = division;
	        	orden[4] = margenY + yDistanceBy4 + y_distance_staves + yDistanceBy4;
	        	ordenes.add(orden);
			}
		}
		
		//  Dejar preparado el color rojo para el metrï¿½nomo
		orden = new int[5];
		orden[0] = 8;
		orden[1] = 255;
		orden[2] = 0;
		orden[3] = 0;
		ordenes.add(orden);
		
		// Grosor de la linea de metrónomo
		orden = new int[5];
    	orden[0] = 5;
    	orden[1] = 4;
    	ordenes.add(orden);
		return ordenes;
	}
	
    //  Dibujar un pentagrama (o varios, si son varios por "renglón") de la partitura
	public static void drawStaves(ArrayList<int[]> ordenes, Canvas canvas, Paint paint, final int margin_y, final int x_ini, final int x_end, 
			final int y_distance, final int y_distance_staves, final int staves) {
		
		int y = 0;
		
		//  Lï¿½neas horizontales
		for (int i=0; i < 5; i++) {
			orden = new int[5];
			orden[0] = 1;
        	orden[1] = x_ini;
        	orden[2] = y + margin_y;
        	orden[3] = x_end;
        	orden[4] = y + margin_y;
        	ordenes.add(orden);

        	if (i < 4) y += y_distance;
		}

		//  Lï¿½neas verticales laterales
		orden = new int[5];
		orden[0] = 1;
    	orden[1] = x_ini;
    	orden[2] = margin_y;
    	orden[3] = x_ini;
    	orden[4] = y + margin_y;
    	ordenes.add(orden);
    	
    	orden = new int[5];
    	orden[0] = 1;
    	orden[1] = x_end;
    	orden[2] = margin_y;
    	orden[3] = x_end;
    	orden[4] = y + margin_y;
    	ordenes.add(orden);

		y += y_distance_staves;
		
		//  2 pentagramas paralelos
        if (staves == 2) {
        	
			for (int i=0; i < 5; i++) {
				orden = new int[5];
				orden[0] = 1;
		    	orden[1] = x_ini;
		    	orden[2] = y + margin_y;
		    	orden[3] = x_end;
		    	orden[4] = y + margin_y;
		    	ordenes.add(orden);
		    	
	        	if (i < 4) y += y_distance;
			}

			orden = new int[5];
			orden[0] = 1;
	    	orden[1] = x_ini;
	    	orden[2] = margin_y;
	    	orden[3] = x_ini;
	    	orden[4] = y + margin_y;
	    	ordenes.add(orden);
	    	
	    	orden = new int[5];
	    	orden[0] = 1;
	    	orden[1] = x_end;
	    	orden[2] = margin_y;
	    	orden[3] = x_end;
	    	orden[4] = y + margin_y;
	    	ordenes.add(orden);
        }
	}
	
	//  Dibuja un compï¿½s
	public static void drawBar(ArrayList<int[]> ordenes, ArrayList<String> ordenesStrings, Canvas canvas, Paint paint, final Resources resources, final Compas compas, 
			final int margin_y, final int y_distance, final int y_distance_half, final int y_distance_staves, final int staves, final int y_distance_limits) {

		int y_intensity = 40;
    	int y_height1 = 0;
		int y_height2 = 0;
		int y_height3 = 0;
		int y_height4 = 0;
		int y_height5 = 0;
		int y_height_corchete = 0;
		int y_height_corchete2 = 0;
		int y_height_corchete3 = 0;
		int y_height_corchete4 = 0;
		int x_margin1 = 0;
		int x_margin_corchete = 0;
		int y_height_beams = 0;
		int y_height_chords = 0;
		int y_height_chords2 = 0;
		int x_margin_chords = 0;
		int chord_notes = 0;
		int y_height_corchete_chords = 0;
		int y_height_corchete2_chords = 0;
		int y_height_corchete3_chords = 0;
		int y_height_corchete4_chords = 0;
		int x_margin_corchete_chords = 0;
		boolean corchete_chords_dibujado = false;
		int y_height_vibrato = 0;
		int y_height_accentuated = 0;
		int y_height_slide = 0;
		int y_height_staccato = 0;
		int y_height_tresillo = 0;
		int x_tresillo = 0;
		int y_distance_palmmute = 140;
		int y_distance_bend = 0;
		int anchoFhBh = 10;
		int corchete = 0;
		int corchete_chords = 0;
		boolean arriba = false;
		boolean octavarium = false;
		boolean tresillo = false;
		int orientacionInversion = 0;
		
		//  Distancias para dibujar notas de gracia
		int distancia_de_gracia = 0;
		int distancia_de_gracia_2 = 0;
		int distancia_de_gracia_3 = 0;
		int distancia_de_gracia_4 = 0;
		
		//  Variables que representarï¿½n la nota que estï¿½ siendo tratada
		int x = 0;
		int y = 0;
		int nota = 0;
		int octava = 0;
		int figuracion = 0;
		int accion = 0;
		int beams = 0;
		boolean inversionEnX = false;
		
		//  Variables que representarï¿½n el compï¿½s que vamos a dibujar
		final int x_inicial = compas.x_inicial();
		final int x_final = compas.x_final();
		final int repeticion_0 = compas.repeticion(0);
		final int repeticionX_0 = compas.repeticion_x(0);
		final int repeticion_1 = compas.repeticion(1);
		final int repeticionX_1 = compas.repeticion_x(1);
		final int ending = compas.ending();
		final int clave_0 = compas.clave(0);
		final int clave_1 = compas.clave(1);
		final int claves_x = compas.claves_x();
		final int tempo = compas.tempo();
		final int tempo_x = compas.tempo_x();
		final int intensidad = compas.intensidad();
		final int intensidad_x = compas.intensidad_x();
		
		Subcompas sc;

        //  Lï¿½mites de iteraciï¿½n de bucles
		final int numSubcompases = compas.numeroDeSubcompases();
        int numNotas = 0;
        
        //  Indicadores de si hemos dibujado ya las repeticiones, claves o tiempos del subcompï¿½s
        boolean elementosDibujados;
		
        //  Si el compï¿½s tiene algï¿½n nï¿½mero asignado distinto de -1, dibujamos el nï¿½mero
        if (compas.numeroDelCompas() != -1) {
        	orden = new int[5];
        	orden[0] = 6;
        	orden[1] = 26;
        	ordenes.add(orden);
        	
        	orden = new int[5];
        	orden[0] = 4;
        	ordenesStrings.add(compas.numeroDelCompas() + "");
        	orden[1] = 20;
        	orden[2] = margin_y - 20;
        	orden[3] = indOrdenesStrings++;
        	ordenes.add(orden);
        }
        
		for (int j=0; j < numSubcompases; j++) {
			
			sc = compas.subcompas(j);
			numNotas = sc.numeroDeNotas();
			elementosDibujados = false;
			
			//  Lï¿½mites de dibujo de plicas
	        int limite = margin_y + (yDistanceBy4 + y_distance_staves) * j + yDistanceBy2 - y_distance_half;
	        int limsup = margin_y + (yDistanceBy4 + y_distance_staves) * j - y_distance_limits;
	        int liminf = margin_y + (yDistanceBy4 + y_distance_staves) * j + yDistanceBy4 + y_distance_limits;
			
			for (int i=0; i<numNotas; i++) {
				y_height1 = 0;
				y_height2 = 0;
				y_height3 = 0;
				y_height4 = 0;
				y_height5 = 0;
				y_height_corchete = 0;
				y_height_corchete2 = 0;
				y_height_corchete3 = 0;
				y_height_corchete4 = 0;
				x_margin1 = 0;
				x_margin_corchete = 0;
				y_height_vibrato = 0;
				y_height_accentuated = 0;
				y_height_slide = 0;
				y_height_staccato = 0;
				y_height_tresillo = 0;
				x_tresillo = 0;
				
				//  Datos de la nota que estamos tratando en esta iteraciï¿½n
				x = sc.nota(i).x();
				y = sc.nota(i).y();
				nota = sc.nota(i).nota();
				octava = sc.nota(i).octava();
				figuracion = sc.nota(i).figuracion();
				accion = sc.nota(i).accion();
				beams = sc.nota(i).union();
				inversionEnX = sc.nota(i).inversionEnX();
				
				//  Elementos que no son notas, como repeticiones, claves o tempos
				if ( ( (repeticion_0 != 0) || (repeticion_1 != 0) || (ending != 0) || (tempo != 0) || (intensidad != 0) || (clave_0 != 0) ) && (!elementosDibujados) ) {
					
					//  Repeticiï¿½n al inicio
					if (repeticion_0 == 1) {
						orden = new int[5];
						orden[0] = 5;
			        	orden[1] = 5;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = repeticionX_0;
			        	orden[2] = margin_y;
			        	orden[3] = repeticionX_0;
			        	orden[4] = margin_y + yDistanceBy4;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 5;
			        	orden[1] = 1;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = repeticionX_0 + 6;
			        	orden[2] = margin_y;
			        	orden[3] = repeticionX_0 + 6;
			        	orden[4] = margin_y + yDistanceBy4;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 7;
			        	orden[1] = 0;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 3;
			        	orden[1] = repeticionX_0 + 13;
			        	orden[2] = margin_y + y_distance + 6;
			        	orden[3] = 3;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 3;
			        	orden[1] = repeticionX_0 + 13;
			        	orden[2] = margin_y + yDistanceBy2 + 6;
			        	orden[3] = 3;
			        	ordenes.add(orden);

						if (staves == 2) {
							orden = new int[5];
							orden[0] = 5;
				        	orden[1] = 5;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 1;
				        	orden[1] = repeticionX_0;
				        	orden[2] = margin_y;
				        	orden[3] = repeticionX_0;
				        	orden[4] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy4;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 5;
				        	orden[1] = 1;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 1;
				        	orden[1] = repeticionX_0 + 6;
				        	orden[2] = margin_y;
				        	orden[3] = repeticionX_0 + 6;
				        	orden[4] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy4;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 7;
				        	orden[1] = 0;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 3;
				        	orden[1] = repeticionX_0 + 13;
				        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + y_distance + 6;
				        	orden[3] = 3;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 3;
				        	orden[1] = repeticionX_0 + 13;
				        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy2 + 6;
				        	orden[3] = 3;
				        	ordenes.add(orden);
						}
						
					}
					
					//  Repeticiï¿½n al final
					if (repeticion_1 == 1) {
						orden = new int[5];
						orden[0] = 5;
			        	orden[1] = 5;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = repeticionX_1;
			        	orden[2] = margin_y;
			        	orden[3] = repeticionX_1;
			        	orden[4] = margin_y + yDistanceBy4;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 5;
			        	orden[1] = 1;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = repeticionX_1 - 6;
			        	orden[2] = margin_y;
			        	orden[3] = repeticionX_1 - 6;
			        	orden[4] = margin_y + yDistanceBy4;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 7;
			        	orden[1] = 0;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 3;
			        	orden[1] = repeticionX_1 - 13;
			        	orden[2] = margin_y + y_distance + 6;
			        	orden[3] = 3;
			        	ordenes.add(orden);
			        	
			        	orden = new int[5];
			        	orden[0] = 3;
			        	orden[1] = repeticionX_1 - 13;
			        	orden[2] = margin_y + yDistanceBy2 + 6;
			        	orden[3] = 3;
			        	ordenes.add(orden);

						if (staves == 2) {
							orden = new int[5];
							orden[0] = 5;
				        	orden[1] = 5;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 1;
				        	orden[1] = repeticionX_1;
				        	orden[2] = margin_y;
				        	orden[3] = repeticionX_1;
				        	orden[4] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy4;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 5;
				        	orden[1] = 1;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 1;
				        	orden[1] = repeticionX_1 - 6;
				        	orden[2] = margin_y;
				        	orden[3] = repeticionX_1 - 6;
				        	orden[4] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy4;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 7;
				        	orden[1] = 0;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 3;
				        	orden[1] = repeticionX_1 - 13;
				        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + y_distance + 6;
				        	orden[3] = 3;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 3;
				        	orden[1] = repeticionX_1 - 13;
				        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy2 + 6;
				        	orden[3] = 3;
				        	ordenes.add(orden);
						}
					}

					switch (ending) {
					
						//  Inicio y final primer ending
						case 3: {
							orden = new int[5];
							orden[0] = 1;
				        	orden[1] = x_inicial;
				        	orden[2] = margin_y - yDistanceBy4;
				        	orden[3] = x_final;
				        	orden[4] = margin_y - yDistanceBy4;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 1;
				        	orden[1] = x_inicial;
				        	orden[2] = margin_y - yDistanceBy4;
				        	orden[3] = x_inicial;
				        	orden[4] = margin_y;
				        	ordenes.add(orden);
				        	
				        	orden = new int[5];
				        	orden[0] = 1;
				        	orden[1] = x_final;
				        	orden[2] = margin_y - yDistanceBy4;
				        	orden[3] = x_final;
				        	orden[4] = margin_y;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 6;
				        	orden[1] = 24;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 4;
				        	ordenesStrings.add("1");
				        	orden[1] = x_inicial + 10;
				        	orden[2] = margin_y - yDistanceBy2;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

							break;
						}
						
						//  Inicio y finalDIS segundo ending
						case 4: {
				        	orden = new int[5];
							orden[0] = 1;
				        	orden[1] = x_inicial;
				        	orden[2] = margin_y - yDistanceBy4;
				        	orden[3] = x_final;
				        	orden[4] = margin_y - yDistanceBy4;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 1;
				        	orden[1] = x_inicial;
				        	orden[2] = margin_y - yDistanceBy4;
				        	orden[3] = x_inicial;
				        	orden[4] = margin_y;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 6;
				        	orden[1] = 24;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 4;
				        	ordenesStrings.add("2");
				        	orden[1] = x_inicial + 10;
				        	orden[2] = margin_y - yDistanceBy2;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

							break;
						}
						default: break;
					}
				
					//  Claves
					switch (clave_0) {
						case 2: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 1;
				        	orden[2] = claves_x;
				        	orden[3] = margin_y - 30;
				        	ordenes.add(orden);

							break;
						}
						case 9: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 2;
				        	orden[2] = claves_x;
				        	orden[3] = margin_y - 30;
				        	ordenes.add(orden);

							break;
						}
						default: break;
					}
					
					switch (clave_1) {
						case 2: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 1;
				        	orden[2] = claves_x;
				        	orden[3] = margin_y + yDistanceBy4 + y_distance_staves;
				        	ordenes.add(orden);

							break;
						}
						case 9: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 2;
				        	orden[2] = claves_x;
				        	orden[3] = margin_y + yDistanceBy4 + y_distance_staves;
				        	ordenes.add(orden);

							break;
						}
						default: break;
					}
				
					//  Tempos
		        	orden = new int[5];
					orden[0] = 6;
		        	orden[1] = 24;
		        	ordenes.add(orden);
					
					switch (tempo) {
						case 2: {
				        	orden = new int[5];
							orden[0] = 4;
				        	ordenesStrings.add("2");
				        	orden[1] = tempo_x;
				        	orden[2] = margin_y + yDistanceBy2;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 4;
				        	ordenesStrings.add("4");
				        	orden[1] = tempo_x;
				        	orden[2] = margin_y + yDistanceBy4;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

							break;
						}
						case 4: {
				        	orden = new int[5];
							orden[0] = 4;
				        	ordenesStrings.add("4");
				        	orden[1] = tempo_x;
				        	orden[2] = margin_y + yDistanceBy2;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 4;
				        	ordenesStrings.add("4");
				        	orden[1] = tempo_x;
				        	orden[2] = margin_y + yDistanceBy4;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

							break;
						}
						case 7: {
				        	orden = new int[5];
							orden[0] = 4;
				        	ordenesStrings.add("7");
				        	orden[1] = tempo_x;
				        	orden[2] = margin_y + yDistanceBy2;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 4;
				        	ordenesStrings.add("4");
				        	orden[1] = tempo_x;
				        	orden[2] = margin_y + yDistanceBy4;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

							break;
						}
						case 19: {
				        	orden = new int[5];
							orden[0] = 4;
				        	ordenesStrings.add("3");
				        	orden[1] = tempo_x;
				        	orden[2] = margin_y + yDistanceBy2;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

				        	orden = new int[5];
				        	orden[0] = 4;
				        	ordenesStrings.add("8");
				        	orden[1] = tempo_x;
				        	orden[2] = margin_y + yDistanceBy4;
				        	orden[3] = indOrdenesStrings++;
				        	ordenes.add(orden);

							break;
						}
						default: break;
					}
					
					if (staves == 2) {
						switch (tempo) {
							case 2: {
					        	orden = new int[5];
								orden[0] = 4;
					        	ordenesStrings.add("2");
					        	orden[1] = tempo_x;
					        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy2;
					        	orden[3] = indOrdenesStrings++;
					        	ordenes.add(orden);

					        	orden = new int[5];
					        	orden[0] = 4;
					        	ordenesStrings.add("4");
					        	orden[1] = tempo_x;
					        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy4;
					        	orden[3] = indOrdenesStrings++;
					        	ordenes.add(orden);

								break;
							}
							case 4: {
					        	orden = new int[5];
								orden[0] = 4;
					        	ordenesStrings.add("4");
					        	orden[1] = tempo_x;
					        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy2;
					        	orden[3] = indOrdenesStrings++;
					        	ordenes.add(orden);

					        	orden = new int[5];
					        	orden[0] = 4;
					        	ordenesStrings.add("4");
					        	orden[1] = tempo_x;
					        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy4;
					        	orden[3] = indOrdenesStrings++;
					        	ordenes.add(orden);

								break;
							}
							case 7: {
					        	orden = new int[5];
								orden[0] = 4;
					        	ordenesStrings.add("7");
					        	orden[1] = tempo_x;
					        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy2;
					        	orden[3] = indOrdenesStrings++;
					        	ordenes.add(orden);

					        	orden = new int[5];
					        	orden[0] = 4;
					        	ordenesStrings.add("4");
					        	orden[1] = tempo_x;
					        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy4;
					        	orden[3] = indOrdenesStrings++;
					        	ordenes.add(orden);

								break;
							}
							case 19: {
					        	orden = new int[5];
								orden[0] = 4;
					        	ordenesStrings.add("3");
					        	orden[1] = tempo_x;
					        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy2;
					        	orden[3] = indOrdenesStrings++;
					        	ordenes.add(orden);

					        	orden = new int[5];
					        	orden[0] = 4;
					        	ordenesStrings.add("8");
					        	orden[1] = tempo_x;
					        	orden[2] = margin_y + yDistanceBy4 + y_distance_staves + yDistanceBy4;
					        	orden[3] = indOrdenesStrings++;
					        	ordenes.add(orden);

								break;
							}
							default: break;
						}
					}
					
					//  Intensidad del compï¿½s
					switch (intensidad) {
						case 1: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 3;
				        	orden[2] = intensidad_x;
				        	orden[3] = margin_y + yDistanceBy4 + y_intensity;
				        	ordenes.add(orden);
				        	
							break;
						}
						case 2: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 4;
				        	orden[2] = intensidad_x;
				        	orden[3] = margin_y + yDistanceBy4 + y_intensity;
				        	ordenes.add(orden);
				        	
							break;
						}
						default: break;
					}
					
					elementosDibujados = true;
					i--;
				}
				
				//  Notas
				else {
					
					//  Silencio
					if (nota == 0) {
	
						switch (figuracion) {
						
							//  Redonda
							case 0: case 1: case 2: {
					        	orden = new int[5];
								orden[0] = 2;
					        	orden[1] = 5;
					        	orden[2] = x;
					        	orden[3] = y;
					        	ordenes.add(orden);

						        break;
							}
							
							//  Blanca
							case 3: case 4: case 5: {
					        	orden = new int[5];
								orden[0] = 2;
					        	orden[1] = 5;
					        	orden[2] = x;
					        	orden[3] = y;
					        	ordenes.add(orden);

						        break;
							}
							
							//  Negra
							case 6: case 7: case 8: {
					        	orden = new int[5];
								orden[0] = 2;
					        	orden[1] = 6;
					        	orden[2] = x;
					        	orden[3] = y;
					        	ordenes.add(orden);

						        break;
							}
							
							//  Corchea
							case 9: case 10: case 11: {
					        	orden = new int[5];
								orden[0] = 2;
					        	orden[1] = 7;
					        	orden[2] = x;
					        	orden[3] = y;
					        	ordenes.add(orden);

						        break;
							}
							
							//  Semicorchea
							case 12: case 13: case 14: {
					        	orden = new int[5];
								orden[0] = 2;
					        	orden[1] = 8;
					        	orden[2] = x;
					        	orden[3] = y;
					        	ordenes.add(orden);

						        break;
							}
							
							//  Fusa
							case 15: case 16: case 17: {
					        	orden = new int[5];
								orden[0] = 2;
					        	orden[1] = 9;
					        	orden[2] = x;
					        	orden[3] = y;
					        	ordenes.add(orden);

						        break;
							}
							
							//  Semifusa
							case 18: case 19: case 20: {
					        	orden = new int[5];
								orden[0] = 2;
					        	orden[1] = 10;
					        	orden[2] = x;
					        	orden[3] = y;
					        	ordenes.add(orden);

						        break;
							}
							
							default: break;
						}
					}
					
					//  Nota normal
					else {
	
						//  Nota de gracia, habrï¿½ que reducir los tamaï¿½os
						if (accion == 9) {
							 distancia_de_gracia = 6;
							 distancia_de_gracia_2 = 2;
							 distancia_de_gracia_3 = 20;
							 distancia_de_gracia_4 = 12;
						}
						else {
							distancia_de_gracia = 0;
							distancia_de_gracia_2 = 0;
							distancia_de_gracia_3 = 0;
							distancia_de_gracia_4 = 0;
						}
						
						//  Antes de dibujar nada, debemos averiguar si es un acorde
						if ( (sc.nota(i+1) != null) && (sc.nota(i+1).accion() > 30) && (sc.nota(i+1).accion() < 36) ) {
							
							if (y_height_chords == 0) {
								
								int min = Integer.MAX_VALUE;
								int max = 0;
								int indmin = 0;
								int indmax = 0;
								int k = i;
	
								do {
									if (min > sc.nota(k).y()) {
										min = sc.nota(k).y();
										indmin = k;
									}
									if (max < sc.nota(k).y()) {
										max = sc.nota(k).y();
										indmax = k;
									}
									k++;
									chord_notes++;
								} while ((sc.nota(k) != null) && (sc.nota(k).accion() > 30) && (sc.nota(k).accion() < 36));
	
								//  Orientaremos todas las plicas hacia donde haya mï¿½s espacio
								int distsup = sc.nota(indmin).y() - limsup;
								int distinf = liminf - sc.nota(indmax).y();
								if (distsup > distinf) {
									
									//  Arriba
									y_height_chords = sc.nota(indmin).y();
									y_height_chords2 = 40;
									x_margin_chords = 13;
									y_height_corchete_chords = 40;
									y_height_corchete2_chords = 33;
									y_height_corchete3_chords = 26;
									y_height_corchete4_chords = 19;
									x_margin_corchete_chords = 14;
									corchete_chords = 14;
									orientacionInversion = 15;
								}
								else {
									
									//  Abajo
									y_height_chords = sc.nota(indmax).y();
									y_height_chords2 = -56;
									x_margin_chords = 1;
									y_height_corchete_chords = -32;
									y_height_corchete2_chords = -25;
									y_height_corchete3_chords = -18;
									y_height_corchete4_chords = -11;
									x_margin_corchete_chords = 1;
									corchete_chords = 15;
									orientacionInversion = -15;
								}
							}
						}

						//  Cabeza blanca
						if (figuracion < 6) {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 11;
				        	orden[2] = x;
				        	if (inversionEnX) orden[2] += orientacionInversion;
				        	orden[3] = y + distancia_de_gracia_2;
				        	ordenes.add(orden);
						}
						
						//  Cabeza negra
						else {
				        	orden = new int[5];
							if (accion == 9) {
					        	orden[1] = 12;
							}
							else {
								orden[1] = 13;
							}
							
							orden[0] = 2;
							orden[2] = x;
							if (inversionEnX) orden[2] += orientacionInversion;
				        	orden[3] = y + distancia_de_gracia_2;
				        	ordenes.add(orden);
						}
						
						//  Figura distinta a la redonda, hay que dibujar la plica
						if (figuracion > 2) {
							
							//  Plica hacia arriba
							if (y > limite) {
								y_height1 = 6;
								y_height2 = 40;
								y_height_corchete = 40;
								y_height_corchete2 = 33;
								y_height_corchete3 = 26;
								y_height_corchete4 = 19;
								x_margin1 = 13;
								x_margin_corchete = 14;
								y_height_vibrato = 17;
								y_height_accentuated = 28;
								y_height_slide = 25;
								y_height_staccato = 8;
								y_distance_bend = 20;
								corchete = 14;
							}
							
							//  Plica hacia abajo
							else {
								y_height1 = 6;
								y_height2 = -56 + distancia_de_gracia_3;
								y_height_corchete = -32 + distancia_de_gracia_4;
								y_height_corchete2 = -25;
								y_height_corchete3 = -18;
								y_height_corchete4 = -11;
								x_margin1 = 1;
								x_margin_corchete = 1;
								y_height_vibrato = -17;
								y_height_accentuated = -28;
								y_height_slide = -7;
								y_height_staccato = -8;
								y_distance_bend = -y_height2 - 20;
								if (accion != 9) {
									corchete = 15;
								}
								else {
									corchete = 16;
								}
							}
	
							//  Si es blanca o negra, dibujamos la plica directamente
							if (figuracion < 9) {
								if (chord_notes > 0) {
									chord_notes--;
						        	orden = new int[5];
									orden[0] = 1;
						        	orden[1] = x + x_margin_chords;
						        	orden[2] = y + y_height1;
						        	orden[3] = x + x_margin_chords;
						        	orden[4] = y_height_chords - y_height_chords2;
						        	ordenes.add(orden);
								}
								else {
						        	orden = new int[5];
									orden[0] = 1;
						        	orden[1] = x + x_margin1;
						        	orden[2] = y + y_height1;
						        	orden[3] = x + x_margin1;
						        	orden[4] = y - y_height2;
						        	ordenes.add(orden);
								}
								
								if (chord_notes == 0) {
									y_height_chords = 0;
									y_height_chords2 = 0;
									x_margin_chords = 0;
									y_height_corchete_chords = 0;
									y_height_corchete2_chords = 0;
									y_height_corchete3_chords = 0;
									y_height_corchete4_chords = 0;
									x_margin_corchete_chords = 0;
									corchete_chords_dibujado = false;
								}
							}
							
							//  Corchea o inferior, hay que calcular la uniï¿½n
							else {
	
								//  Si no tenemos beams, dibujamos la nota tal cual
								if (beams == 0) {
									
									//  Plica
									if (chord_notes > 0) {
							        	orden = new int[5];
										orden[0] = 1;
							        	orden[1] = x + x_margin_chords;
							        	orden[2] = y + y_height1;
							        	orden[3] = x + x_margin_chords;
							        	orden[4] = y_height_chords - y_height_chords2;
							        	ordenes.add(orden);
									}
									else {
							        	orden = new int[5];
										orden[0] = 1;
							        	orden[1] = x + x_margin1;
							        	orden[2] = y + y_height1;
							        	orden[3] = x + x_margin1;
							        	orden[4] = y - y_height2;
							        	ordenes.add(orden);
									}

									//  No es un acorde
									if (y_height_chords == 0) {
										
										//  Corchetes: su nï¿½mero cambia en funciï¿½n de la figuraciï¿½n
										switch (figuracion) {
											
											//  Corchea
											case 9: case 10: case 11: {
									        	orden = new int[5];
												orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete;
									        	ordenes.add(orden);

												break;
											}
											
											//  Semicorchea
											case 12: case 13: case 14: {
									        	orden = new int[5];
												orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete2;
									        	ordenes.add(orden);

												break;
											}
											
											//  Fusa
											case 15: case 16: case 17: {
									        	orden = new int[5];
												orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete3;
									        	ordenes.add(orden);

												break;
											}
											
											//  Semifusa
											case 18: case 19: case 20: {
									        	orden = new int[5];
												orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 2;
									        	orden[1] = corchete;
									        	orden[2] = x + x_margin_corchete;
									        	orden[3] = y - y_height_corchete4;
									        	ordenes.add(orden);

												break;
											}
												
											default: break;
										}
									}
									
									//  Es un acorde
									else {
											
										//  Sï¿½lo dibujaremos el corchete una vez
										if (!corchete_chords_dibujado) {
										
											//  Corchetes: su nï¿½mero cambia en funciï¿½n de la figuraciï¿½n
											switch (figuracion) {
											
												//  Corchea
												case 9: case 10: case 11: {
										        	orden = new int[5];
													orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete_chords;
										        	ordenes.add(orden);

													break;
												}
												
												//  Semicorchea
												case 12: case 13: case 14: {
										        	orden = new int[5];
													orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete_chords;
										        	ordenes.add(orden);

										        	orden = new int[5];
										        	orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete2_chords;
										        	ordenes.add(orden);

													break;
												}
												
												//  Fusa
												case 15: case 16: case 17: {
										        	orden = new int[5];
													orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete_chords;
										        	ordenes.add(orden);

										        	orden = new int[5];
										        	orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete2_chords;
										        	ordenes.add(orden);

										        	orden = new int[5];
										        	orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete3_chords;
										        	ordenes.add(orden);

													break;
												}
												
												//  Semifusa
												case 18: case 19: case 20: {
										        	orden = new int[5];
													orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete_chords;
										        	ordenes.add(orden);

										        	orden = new int[5];
										        	orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete2_chords;
										        	ordenes.add(orden);

										        	orden = new int[5];
										        	orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete3_chords;
										        	ordenes.add(orden);

										        	orden = new int[5];
										        	orden[0] = 2;
										        	orden[1] = corchete_chords;
										        	orden[2] = x + x_margin_corchete_chords;
										        	orden[3] = y_height_chords - y_height_corchete4_chords;
										        	ordenes.add(orden);

													break;
												}
												
												default: break;
											}
											
											corchete_chords_dibujado = true;
										}
									}
									
									if (chord_notes == 0) {
										y_height_chords = 0;
										y_height_chords2 = 0;
										x_margin_chords = 0;
										y_height_corchete_chords = 0;
										y_height_corchete2_chords = 0;
										y_height_corchete3_chords = 0;
										y_height_corchete4_chords = 0;
										x_margin_corchete_chords = 0;
										corchete_chords_dibujado = false;
									}
								}
								
								//  Tenemos beams que hay que unir
								else {
									
									//  Calculamos la altura a la que hay que dibujar la uniï¿½n de los corchetes
									//  Cuanto menor es el valor, mayor es la altura de la nota
									if (y_height_beams == 0) {
									
										int min = Integer.MAX_VALUE;
										int max = 0;
										int indmin = 0;
										int indmax = 0;
										int k = i;
	
										while ( (sc.nota(k) != null) && (sc.nota(k).union() < 31) ) {
											if (min > sc.nota(k).y()) {
												min = sc.nota(k).y();
												indmin = k;
											}
											if (max < sc.nota(k).y()) {
												max = sc.nota(k).y();
												indmax = k;
											}
											k++;
										}
										
										while ( (sc.nota(k) != null) && (sc.nota(k).union() > 30) ) {
											if (min > sc.nota(k).y()) {
												min = sc.nota(k).y();
												indmin = k;
											}
											if (max < sc.nota(k).y()) {
												max = sc.nota(k).y();
												indmax = k;
											}
											k++;
										}
										
										//  Comprobaciï¿½n de si nos encontramos ante un tresillo
										if (sc.nota(k - 1).union() == 38) {
											tresillo = true;
										}
										
										//  Orientaremos todas las plicas hacia donde haya mï¿½s espacio
										int distsup = sc.nota(indmin).y() - limsup;
										int distinf = liminf - sc.nota(indmax).y();
										if (distsup > distinf) {
											y_height_beams = sc.nota(indmin).y() + distancia_de_gracia_3;
											arriba = true;
										}
										else {
											y_height_beams = sc.nota(indmax).y() - distancia_de_gracia_3;
											arriba = false;
										}
									}

									//  Podrï¿½amos necesitar cambiar la orientaciï¿½n de la plica
									if (arriba) {
										y_height1 = 6;
										y_height2 = 40;
										y_height3 = 32;
										y_height4 = 24;
										y_height5 = 16;
										x_margin1 = 13 - distancia_de_gracia;
										y_height_vibrato = 17;
										y_height_accentuated = 28;
										y_height_slide = 25;
										y_height_staccato = y_height_beams - y_height2 - 10;
										y_height_tresillo = y_height_staccato;
										x_tresillo = 16;
										y_distance_bend = 20;
									}
									else {
										y_height1 = 6;
										y_height2 = -56;
										y_height3 = -48;
										y_height4 = -40;
										y_height5 = -32;
										x_margin1 = 1 - distancia_de_gracia;
										y_height_vibrato = -17;
										y_height_accentuated = -28;
										y_height_slide = -7;
										y_height_staccato = y_height_beams - y_height2 + 10;
										y_height_tresillo = y_height_staccato + 15;
										x_tresillo = 28;
										y_distance_bend = y_height_beams - y_height2 + 20;
									}
									
									//  Plica
						        	orden = new int[5];
									orden[0] = 1;
						        	orden[1] = x + x_margin1;
						        	orden[2] = y + y_height1;
						        	orden[3] = x + x_margin1;
						        	orden[4] = y_height_beams - y_height2;
						        	ordenes.add(orden);
									
									//  Uniones
									if (beams != 31) {
							        	orden = new int[5];
										orden[0] = 5;
							        	orden[1] = 5;
							        	ordenes.add(orden);
										
										int beamFinal = 0;
										
										//  El beam a unir se dibuja con la nota siguiente si es una ï¿½nica nota
										if (chord_notes == 0) {
											beamFinal = 1;
										}
										else {
											beamFinal = chord_notes;
										}
										
										switch (beams) {
											case 1: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

												break;
											}
											case 2: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

												break;
											}
											case 3: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 4: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 5: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

												break;
											}
											case 6: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

												break;
											}
											case 7: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 8: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 9: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 10: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

												break;
											}
											case 11: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 12: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 13: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 14: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

												break;
											}
											case 15: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 16: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 17: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

												break;
											}
											case 18: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 19: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 20: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 21: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 22: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 23: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 24: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 25: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 26: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 27: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 28: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 29: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 + anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 30: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height2;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height2;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = sc.nota(i+beamFinal).x() + x_margin1 + 1;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 31: {
												break;
											}
											case 32: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

												break;
											}
											case 33: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 34: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height3;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height3;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 35: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

												break;
											}
											case 36: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height4;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height4;
									        	ordenes.add(orden);

									        	orden = new int[5];
									        	orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 37: {
									        	orden = new int[5];
												orden[0] = 1;
									        	orden[1] = x + x_margin1;
									        	orden[2] = y_height_beams - y_height5;
									        	orden[3] = x + x_margin1 - anchoFhBh;
									        	orden[4] = y_height_beams - y_height5;
									        	ordenes.add(orden);

												break;
											}
											case 38: {
									        	orden = new int[5];
												orden[0] = 6;
										    	orden[1] = 42;
										    	ordenes.add(orden);

									        	orden = new int[5];
										    	orden[0] = 4;
										    	ordenesStrings.add("3");
										    	orden[1] = x - x_tresillo;
										    	orden[2] = y_height_tresillo;
										    	orden[3] = indOrdenesStrings++;
										    	ordenes.add(orden);

												break;
											}
											default: break;
										}

							        	orden = new int[5];
										orden[0] = 5;
							        	orden[1] = 2;
							        	ordenes.add(orden);
									}
								}

								//  Una nota menos que dibujar del acorde
								if (chord_notes > 0) {
									chord_notes--;
									
									//  Hemos terminado con este acorde
									if (chord_notes == 0) {
										y_height_chords = 0;
										y_height_chords2 = 0;
										x_margin_chords = 0;
										y_height_corchete_chords = 0;
										y_height_corchete2_chords = 0;
										y_height_corchete3_chords = 0;
										y_height_corchete4_chords = 0;
										x_margin_corchete_chords = 0;
										corchete_chords_dibujado = false;
									}
								}
								
								//  Reiniciar y_height_beams tras haber terminado con esta uniï¿½n
								if (beams > 30) {
									if (chord_notes == 0) {
										y_height_beams = 0;
									}
								}
							}
						}
					}
					
					//  Dibujar puntillos (si no se trata de un tresillo)
					if (!tresillo) {
					
						//  Nota normal
						if (nota != 0) {
							switch (figuracion) {
								case 1:case 4:case 7:case 10:case 13:case 16:case 19: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 26;
						        	if ( (inversionEnX) && (orientacionInversion > 0) ) 
						        		orden[1] += orientacionInversion;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 2:case 5:case 8:case 11:case 14:case 17:case 20: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 26;
						        	if ( (inversionEnX) && (orientacionInversion > 0) ) 
						        		orden[1] += orientacionInversion;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

						        	orden = new int[5];
						        	orden[0] = 3;
						        	orden[1] = x + 34;
						        	if ( (inversionEnX) && (orientacionInversion > 0) ) 
						        		orden[1] += orientacionInversion;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								default: break;
							}
						}
						
						//  Silencio
						else {
							switch (figuracion) {
								case 1: 
								case 4: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 20;
						        	orden[2] = y + 3;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 2:
								case 5: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 20;
						        	orden[2] = y + 3;
						        	orden[3] = 2;
						        	ordenes.add(orden);

						        	orden = new int[5];
						        	orden[0] = 3;
						        	orden[1] = x + 28;
						        	orden[2] = y + 3;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 7: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 20;
						        	orden[2] = y + 10;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 8: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 20;
						        	orden[2] = y + 10;
						        	orden[3] = 2;
						        	ordenes.add(orden);

						        	orden = new int[5];
						        	orden[0] = 3;
						        	orden[1] = x + 28;
						        	orden[2] = y + 18;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 10: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 24;
						        	orden[2] = y + 20;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 11: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 24;
						        	orden[2] = y + 20;
						        	orden[3] = 2;
						        	ordenes.add(orden);

						        	orden = new int[5];
						        	orden[0] = 3;
						        	orden[1] = x + 32;
						        	orden[2] = y + 20;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 13: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 27;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 14: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 27;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

						        	orden = new int[5];
						        	orden[0] = 3;
						        	orden[1] = x + 35;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 16: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 30;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 17: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 30;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

						        	orden = new int[5];
						        	orden[0] = 3;
						        	orden[1] = x + 38;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 19: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 34;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								case 20: {
						        	orden = new int[5];
									orden[0] = 3;
						        	orden[1] = x + 34;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

						        	orden = new int[5];
						        	orden[0] = 3;
						        	orden[1] = x + 42;
						        	orden[2] = y + 6;
						        	orden[3] = 2;
						        	ordenes.add(orden);

									break;
								}
								default: break;
							}
						}
					}
					else {
						
						//  Hemos terminado con este tresillo
						if (beams == 38) {
							tresillo = false;
						}
					}

					//  Dibujar sostenidos, bemoles o becuadros
					switch (nota) {
						case 8:case 9:case 10:case 11:case 12:case 13:case 14: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 17;
				        	orden[2] = x - 8;
				        	if ( (inversionEnX) && (orientacionInversion < 0) ) 
				        		orden[2] += orientacionInversion;
				        	orden[3] = y - 3;
				        	ordenes.add(orden);

							break;
						}
						case 15:case 16:case 17:case 18:case 19:case 20:case 21: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 18;
				        	orden[2] = x - 8;
				        	if ( (inversionEnX) && (orientacionInversion < 0) ) 
				        		orden[2] += orientacionInversion;
				        	orden[3] = y - 8;
				        	ordenes.add(orden);

							break;
						}
						case 22:case 23:case 24:case 25:case 26:case 27:case 28: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 19;
				        	orden[2] = x - 8;
				        	if ( (inversionEnX) && (orientacionInversion < 0) ) 
				        		orden[2] += orientacionInversion;
				        	orden[3] = y - 5;
				        	ordenes.add(orden);

							break;
						}
						default: break;
					}
					
					//  Si la nota se sale del pentagrama, dibujar la lï¿½nea asociada a la cabeza
					//  La posiciï¿½n y depende del subcompï¿½s que estemos dibujando
					int y_margin_custom = margin_y + (yDistanceBy4 + y_distance_staves) * j;			
					
					if (y == y_margin_custom + yDistanceBy4 + y_distance_half) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance_half;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom + yDistanceBy5) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y;
			        	orden[3] = x + 18;
			        	orden[4] = y;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom + yDistanceBy5 + y_distance_half) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy5;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy5;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance_half;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom + yDistanceBy6) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy5;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy5;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y;
			        	orden[3] = x + 18;
			        	orden[4] = y;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom + yDistanceBy6 + y_distance_half) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy5;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy5;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy6;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy6;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance_half;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom + yDistanceBy7) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy5;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy5;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy6;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy6;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y;
			        	orden[3] = x + 18;
			        	orden[4] = y;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom + yDistanceBy7 + y_distance_half) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy5;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy5;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy6;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy6;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy7;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy7;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance_half;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom + y_distance * 8) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy5;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy5;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy6;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy6;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y_margin_custom + yDistanceBy7;
			        	orden[3] = x + 18;
			        	orden[4] = y_margin_custom + yDistanceBy7;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 2;
			        	orden[2] = y;
			        	orden[3] = x + 18;
			        	orden[4] = y;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom - y_distance - y_distance_half) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance_half;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom - yDistanceBy2) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom - yDistanceBy2 - y_distance_half) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance_half;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance + y_distance_half;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom - yDistanceBy3) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + yDistanceBy2;
			        	orden[3] = x + 18;
			        	orden[4] = y + yDistanceBy2;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom - yDistanceBy3 - y_distance_half) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance_half;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance + y_distance_half;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + yDistanceBy2 + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + yDistanceBy2 + y_distance_half;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom - yDistanceBy4) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + yDistanceBy2;
			        	orden[3] = x + 18;
			        	orden[4] = y + yDistanceBy2;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + yDistanceBy3;
			        	orden[3] = x + 18;
			        	orden[4] = y + yDistanceBy3;
			        	ordenes.add(orden);
					}
					if (y == y_margin_custom - yDistanceBy4 - y_distance_half) {
			        	orden = new int[5];
						orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance_half;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + y_distance + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + y_distance + y_distance_half;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + yDistanceBy2 + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + yDistanceBy2 + y_distance_half;
			        	ordenes.add(orden);

			        	orden = new int[5];
			        	orden[0] = 1;
			        	orden[1] = x - 3;
			        	orden[2] = y + yDistanceBy3 + y_distance_half;
			        	orden[3] = x + 18;
			        	orden[4] = y + yDistanceBy3 + y_distance_half;
			        	ordenes.add(orden);
					}
	
					//  Penï¿½ltimo paso, dibujar la acciï¿½n asociada a la nota (si tiene alguna)
					switch (accion) {
						case 2: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 20;
				        	orden[2] = x - 24;
				        	orden[3] = y - 6;
				        	ordenes.add(orden);
				        	
							break;
						}
						case 5: {
				        	orden = new int[5];
							orden[0] = 6;
					    	orden[1] = 22;
					    	ordenes.add(orden);

				        	orden = new int[5];
							orden[0] = 4;
					    	ordenesStrings.add(">");
					    	orden[1] = x + 3;
					    	orden[2] = y + y_height_accentuated;
					    	orden[3] = indOrdenesStrings++;
					    	ordenes.add(orden);

							break;
						}
						case 6: {
				        	orden = new int[5];
							orden[0] = 6;
					    	orden[1] = 28;
					    	ordenes.add(orden);

				        	orden = new int[5];
							orden[0] = 4;
					    	ordenesStrings.add("^");
					    	orden[1] = x + 3;
					    	orden[2] = liminf + 20;
					    	orden[3] = indOrdenesStrings++;
					    	ordenes.add(orden);

							break;
						}
						case 10: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 21;
				        	orden[2] = x;
				        	orden[3] = y + y_height_vibrato;
				        	ordenes.add(orden);

							break;
						}
						case 11: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 22;
				        	orden[2] = x;
				        	orden[3] = liminf + 20;
				        	ordenes.add(orden);

							break;
						}
						case 12: {
				        	orden = new int[5];
							orden[0] = 1;
				        	orden[1] = x + 5;
				        	orden[2] = y + y_height_slide;
				        	orden[3] = x + 20;
				        	orden[4] = y + y_height_slide;
				        	ordenes.add(orden);

							break;
						}
						case 14: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 23;
				        	orden[2] = x + 6;
				        	orden[3] = liminf + 20;
				        	ordenes.add(orden);

							break;
						}
						case 17: {
				        	orden = new int[5];
							orden[0] = 6;
					    	orden[1] = 14;
					    	ordenes.add(orden);

				        	orden = new int[5];
							orden[0] = 4;
					    	ordenesStrings.add("P.M");
					    	orden[1] = x;
					    	orden[2] = margin_y + y_distance_palmmute;
					    	orden[3] = indOrdenesStrings++;
					    	ordenes.add(orden);

							break;
						}
						case 18: {
							if (beams > 0) {
					        	orden = new int[5];
								orden[0] = 3;
					        	orden[1] = x + x_margin1;
					        	orden[2] = y_height_staccato;
					        	orden[3] = 3;
					        	ordenes.add(orden);
							}
							else {
					        	orden = new int[5];
								orden[0] = 3;
					        	orden[1] = x + x_margin1;
					        	orden[2] = y - y_height2 - y_height_staccato;
					        	orden[3] = 3;
					        	ordenes.add(orden);
							}
							break;
						}
						case 19: {
				        	orden = new int[5];
							orden[0] = 6;
					    	orden[1] = 14;
					    	ordenes.add(orden);

				        	orden = new int[5];
							orden[0] = 4;
					    	ordenesStrings.add("T");
					    	orden[1] = x + 3;
					    	orden[2] = liminf + 10;
					    	orden[3] = indOrdenesStrings++;
					    	ordenes.add(orden);

							break;
						}
						case 20: {
				        	orden = new int[5];
							orden[0] = 6;
					    	orden[1] = 14;
					    	ordenes.add(orden);

				        	orden = new int[5];
							orden[0] = 4;
					    	ordenesStrings.add("S");
					    	orden[1] = x + 3;
					    	orden[2] = liminf + 10;
					    	orden[3] = indOrdenesStrings++;
					    	ordenes.add(orden);

							break;
						}
						case 21: {
				        	orden = new int[5];
							orden[0] = 6;
					    	orden[1] = 14;
					    	ordenes.add(orden);

				        	orden = new int[5];
							orden[0] = 4;
					    	ordenesStrings.add("P");
					    	orden[1] = x + 3;
					    	orden[2] = liminf + 10;
					    	orden[3] = indOrdenesStrings++;
					    	ordenes.add(orden);

							break;
						}
						
						//  Bending
						case 36:
						case 37:
						case 38:
						case 39:
						case 40:
						case 41:
						case 42:
						case 43:
						case 44:
						case 45:
						case 46:
						case 47:
						case 48:
						case 49:
						case 50:
						case 51: {
				        	orden = new int[5];
							orden[0] = 2;
				        	orden[1] = 24;
				        	orden[2] = x + 2;
				        	orden[3] = y + y_distance_bend;
				        	ordenes.add(orden);

							break;
						}
						default: break;
					}
					
					//  Último paso: si la nota está subida una octava, lo hacemos constar
					if (octava > 11) {
						
						//  Si es la primera nota en estar subida, dibujamos un 8
						if (!octavarium) {
				        	orden = new int[5];
							orden[0] = 6;
					    	orden[1] = 18;
					    	ordenes.add(orden);

				        	orden = new int[5];
							orden[0] = 4;
					    	ordenesStrings.add("8");
					    	orden[1] = x + 1;
					    	orden[2] = margin_y - yDistanceBy5;
					    	orden[3] = indOrdenesStrings++;
					    	ordenes.add(orden);
					    	
					    	octavarium = true;
						}
						
						//  Si no, dibujamos un punto
						else {
				        	orden = new int[5];
							orden[0] = 3;
				        	orden[1] = x + 6;
				        	orden[2] = margin_y - yDistanceBy5;
				        	orden[3] = 2;
				        	ordenes.add(orden);
						}
					}
					else {
						octavarium = false;
					}
				}
			}
		}
	}
	
	//  Devuelve una posiciï¿½n Y para la nota que recibe por parï¿½metro
	public static int posicion_nota(byte nota, byte octava, byte figuracion, byte clave, byte instrumento,
			int y_margin, int y_distance, int y_distance_half){
		int coo_y = 0;
		
		//  Silencio
		if (nota > 0) {
		
			//  Instrumento
			switch (instrumento) {
			
				//  Guitarra
				case 1: {
				
					//  Clave
					switch (clave) {
						
						//  Clave de sol segunda
						case 2: {
		
							//  Octava
							switch (octava) {
							
								//  Octava 3
								case 4: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin + y_distance * 5 + y_distance_half;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin + y_distance * 5;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance * 8;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance * 7 + y_distance_half;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin + y_distance * 7;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin + y_distance * 6 + y_distance_half;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin + y_distance * 6;
											break;
										
										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
			
								//  Octava 4
								case 5: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin + y_distance * 2;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin + y_distance + y_distance_half;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance * 4 + y_distance_half;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance * 4;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin + y_distance * 3 + y_distance_half;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin + y_distance * 3;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin + y_distance * 2 + y_distance_half;
											break;
										
										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
								
								//  Octava 5
								case 6: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin - y_distance - y_distance_half;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin - y_distance * 2;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance_half;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin - y_distance_half;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin - y_distance;
											break;
										
										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
								
								//  Octava 6
								case 7: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin - y_distance * 5;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin - y_distance * 5 - y_distance_half;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin - y_distance * 2 - y_distance_half;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin - y_distance * 3;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin - y_distance * 3 - y_distance_half;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin - y_distance * 4;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin - y_distance * 4 - y_distance_half;
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
				
				//  Piano
				case 2: {
				
					//  Clave
					switch (clave) {
						
						//  Clave de sol segunda
						case 2: {
		
							//  Octava
							switch (octava) {
							
								//  Octava 2
								case 14:
								case 3: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin + y_distance * 9;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin + y_distance * 8 + y_distance_half;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance * 11 + y_distance_half;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance * 11;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin + y_distance * 10 + y_distance_half;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin + y_distance * 10;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin + y_distance * 9 + y_distance_half;
											break;

										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
			
								//  Octava 3
								case 15:
								case 4: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin + y_distance * 5 + y_distance_half;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin + y_distance + 5;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance * 8;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance * 7 + y_distance_half;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin + y_distance * 7;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin + y_distance * 6 + y_distance_half;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin + y_distance * 6;
											break;
										
										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
								
								//  Octava 4
								case 16:
								case 5: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin + y_distance * 2;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin + y_distance + y_distance_half;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance * 4 + y_distance_half;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance * 4;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin + y_distance * 3 + y_distance_half;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin + y_distance * 3;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin + y_distance * 2 + y_distance_half;
											break;

										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
								
								//  Octava 5
								case 17:
								case 6: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin - y_distance - y_distance_half;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin - y_distance * 2;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance_half;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin - y_distance_half;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin - y_distance;
											break;

										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
								
								//  Octava 6
								case 18:
								case 7: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin - y_distance * 5;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin - y_distance * 5 - y_distance_half;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin - y_distance * 2 - y_distance_half;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin - y_distance * 3;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin - y_distance * 3 - y_distance_half;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin - y_distance * 4;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin - y_distance * 4 - y_distance_half;
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
						
						//  Clave de fa cuarta
						case 9: {
		
							//  Octava
							switch (octava) {
							
								//  Octava 1
								case 13:
								case 2: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin + y_distance * 6 + y_distance_half;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin + y_distance * 6;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance * 9;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance * 8 + y_distance_half;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin + y_distance * 8;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin + y_distance * 7 + y_distance_half;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin + y_distance * 7;
											break;
	
										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
							
								//  Octava 2
								case 14:
								case 3: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin + y_distance * 3;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin + y_distance * 2 + y_distance_half;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance * 5 + y_distance_half;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance * 5;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin + y_distance * 4 + y_distance_half;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin + y_distance * 4;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin + y_distance * 3 + y_distance_half;
											break;

										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
			
								//  Octava 3
								case 15:
								case 4: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin - y_distance_half;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin - y_distance;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin + y_distance * 2;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin + y_distance + y_distance_half;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin + y_distance;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin + y_distance - y_distance_half;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin;
											break;

										default:
											coo_y = 0;
											break;
									}
									
									break;
								}
								
								//  Octava 4
								case 16:
								case 5: {
									switch(nota){
										case 1:
										case 8:
										case 15:
										case 22:
											coo_y = y_margin - y_distance * 4;
											break;
											
										case 2:
										case 9:
										case 16:
										case 23:
											coo_y = y_margin - y_distance * 4 - y_distance_half;
											break;
											
										case 3:
										case 10:
										case 17:
										case 24:
											coo_y = y_margin - y_distance - y_distance_half;
											break;
											
										case 4:
										case 11:
										case 18:
										case 25:
											coo_y = y_margin - y_distance * 2;
											break;
											
										case 5:
										case 12:
										case 19:
										case 26:
											coo_y = y_margin - y_distance * 2 - y_distance_half;
											break;
											
										case 6:
										case 13:
										case 20:
										case 27:
											coo_y = y_margin - y_distance * 3;
											break;
											
										case 7:
										case 14:
										case 21:
										case 28:
											coo_y = y_margin - y_distance * 3 - y_distance_half;
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
		
		//  Silencio
		else {
			
			switch (figuracion) {
			
				//  Redonda
				case 0:
				case 1:
				case 2:
					coo_y = y_margin + y_distance;
					break;
				
				//  Blanca
				case 3:
				case 4:
				case 5:
					coo_y = y_margin + y_distance + 5;
					break;			

				//  Negra
				case 6:
				case 7:
				case 8:
					coo_y = y_margin + y_distance_half;
					break;

				//  Corchea
				case 9:
				case 10:
				case 11:
					coo_y = y_margin + y_distance;
					break;
				
				//  Semicorchea
				case 12:
				case 13:
				case 14:
					coo_y = y_margin;
					break;
				
				//  Fusa
				case 15:
				case 16:
				case 17:
					coo_y = y_margin;
					break;
				
				//  Semifusa
				case 18:
				case 19:
				case 20:
					coo_y = y_margin;
					break;
				
				default: break;
			}
		}
		
		return coo_y;
	}
}
