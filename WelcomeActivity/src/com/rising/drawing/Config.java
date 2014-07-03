package com.rising.drawing;


public class Config {
	
	private boolean supported = true;
	
	private int alto_dialog_bpm;
	private int altura_arco_ligaduras_expresion;
	private int altura_arco_ligaduras_union;
	private int ancho_beams;
	private int ancho_beams_nota_gracia;
	private int ancho_cabeza_nota;
	private int ancho_cabeza_nota_gracia;
	private int ancho_claves;
	private int ancho_dialog_bpm;
	private int ancho_hooks;
	private int ancho_tempo;
	private int change_account_horizontal;
	private int change_account_vertical;
	private int desplazamiento_extra_nota_gracia;
	private int distancia_corchetes;
	private int distancia_entre_beams;
	private int distancia_entre_beams_notas_gracia;
	private int distancia_lineas_pentagrama;
	private int distancia_lineas_pentagrama_mitad;
	private int distancia_pentagramas;
	private int x_inicial_pentagramas;
	private int x_final_pentagramas;
	private int largo_imagen_corchete;
	private int largo_imagen_corchete_gracia;
	private int longitud_plica;
	private int longitud_plica_nota_gracia;
	private int margen_ancho_cabeza_nota;
	private int margen_autor;
	private int margen_barlines;
	private int margen_derecho_compases;
	private int margen_inferior_autor;
	private int margen_izquierdo_compases;
	private int margen_nota_gracia;
	private int margen_obra;
	private int margen_superior;
	private int mitad_cabeza_nota_vertical;
	private int mitad_cabeza_nota_gracia_vertical;
	private int offset_ultima_nota_gracia;
	private int radio_octavarium;
	private int radio_puntillos;
	private int radio_staccatos;
	private int tamano_letra_autor;
	private int tamano_letra_bip_preparacion;
	private int tamano_letra_bpm;
	private int tamano_letra_numero_compas;
	private int tamano_letra_obra;
	private int tamano_letra_palm_mute;
	private int tamano_letra_pulso;
	private int tamano_letra_tapping;
	private int tamano_letra_tempo;
	private int tamano_letra_tresillo;
	private int tamano_letra_words;
	private int unidad_desplazamiento;
	private int width;
	private int height;
	
	private int y_accent_up;
	private int x_accidental;
	private int y_accidental;
	private int y_accidental_flat;
	private int y_bend;
	private int y_bpm;
	private int y_clave_sol_segunda;
	private int x_inicio_slash;
	private int x_fin_slash;
	private int y_inicio_slash;
	private int y_fin_slash;
	private int x_fermata;
	private int y_fermata;
	private int y_ligaduras_expresion;
	private int x_ligaduras_union;
	private int y_ligaduras_union;
	private int x_numero_compas;
	private int y_numero_compas;
	private int x_octavarium;
	private int y_octavarium;
	private int y_octavarium_2;
	private int y_palm_mute;
	private int x_puntillo;
	private int y_puntillo_arriba;
	private int y_puntillo_abajo;
	private int y_silencio_blanca;
	private int x_staccato;
	private int y_staccato_arriba;
	private int y_staccato_abajo;
	private int y_tapping;
	private int x_tresillo;
	private int y_tresillo_arriba;
	private int y_tresillo_abajo;
	
	public Config(int densityDPI, int width, int height) {
		
		switch (densityDPI) {
			case 120:
				break;
			case 160:
				break;
			case 213:
				alto_dialog_bpm = 770;
				altura_arco_ligaduras_expresion = 30;
				altura_arco_ligaduras_union = 20;
				ancho_beams = 5;
				ancho_beams_nota_gracia = 2;
				ancho_cabeza_nota = 10;
				ancho_cabeza_nota_gracia = 5;
				ancho_claves = 30;
				ancho_dialog_bpm = 600;
				ancho_hooks = 16;
				ancho_tempo = 20;
				change_account_horizontal = 2;
				change_account_vertical = 4;
				desplazamiento_extra_nota_gracia = 30;
				distancia_corchetes = 15;
				distancia_entre_beams = 5;
				distancia_entre_beams_notas_gracia = 2;
				distancia_lineas_pentagrama = 12;
				distancia_lineas_pentagrama_mitad = 6;
				distancia_pentagramas = 150;
				largo_imagen_corchete = 10;
				largo_imagen_corchete_gracia = 5;
				longitud_plica = 40;
				longitud_plica_nota_gracia = 20;
				margen_ancho_cabeza_nota = 5;
				margen_autor = 120;
				margen_barlines = 10;
				margen_derecho_compases = 30;
				margen_inferior_autor = 230;
				margen_izquierdo_compases = 30;
				margen_nota_gracia = 6;
				margen_obra = 60;
				margen_superior = 50;
				mitad_cabeza_nota_vertical = 6;
				mitad_cabeza_nota_gracia_vertical = 3;
				offset_ultima_nota_gracia = 15;
				radio_octavarium = 3;
				radio_puntillos = 4;
				radio_staccatos = 4;
				tamano_letra_autor = 30;
				tamano_letra_bip_preparacion = 150;
				tamano_letra_bpm = 30;
				tamano_letra_numero_compas = 30;
				tamano_letra_obra = 50;
				tamano_letra_palm_mute = 30;
				tamano_letra_pulso = 50;
				tamano_letra_tapping = 30;
				tamano_letra_tempo = 10;
				tamano_letra_tresillo = 5;
				tamano_letra_words = 30;
				unidad_desplazamiento = 30;
				this.width = width;				
				
				y_accent_up = 30;
				x_accidental = 10;
				y_accidental = 10;
				y_accidental_flat = 15;
				y_bend = 40;
				y_bpm = 20;
				y_clave_sol_segunda = 15;
				x_inicial_pentagramas = 50;
				x_final_pentagramas = width - x_inicial_pentagramas;
				x_inicio_slash = 5;
				x_fin_slash = 5;
				y_inicio_slash = 5;
				y_fin_slash = 10;
				x_fermata = 10;
				y_fermata = 10;
				y_ligaduras_expresion = 10;
				x_ligaduras_union = 20;
				y_ligaduras_union = 10;
				x_numero_compas = 50;
				y_numero_compas = 15;
				x_octavarium = 15;
				y_octavarium = 8;
				y_octavarium_2 = 16;
				y_palm_mute = 15;
				x_puntillo = ancho_cabeza_nota + 10;
				y_puntillo_arriba = mitad_cabeza_nota_vertical - 10;
				y_puntillo_abajo = mitad_cabeza_nota_vertical + 10;
				y_silencio_blanca = 5;
				x_staccato = 15;
				y_staccato_arriba = 20;
				y_staccato_abajo = 8;
				y_tapping = 15;
				x_tresillo = 6;
				y_tresillo_arriba = 7;
				y_tresillo_abajo = 7;
				break;
				
			case 240:
				break;
				
			case 320:
				
				switch(height){
					case 720:
						alto_dialog_bpm = 850;
						altura_arco_ligaduras_expresion = 30;
						altura_arco_ligaduras_union = 50;
						ancho_beams = 8;
						ancho_beams_nota_gracia = 4;
						ancho_cabeza_nota = 25;
						ancho_cabeza_nota_gracia = 15;
						ancho_claves = 88; 
						ancho_dialog_bpm = 600;
						ancho_hooks = 16;
						ancho_tempo = 65;
						change_account_horizontal = 2;
						change_account_vertical = 4;
						desplazamiento_extra_nota_gracia = 40;
						distancia_corchetes = 15;
						distancia_entre_beams = 12;
						distancia_entre_beams_notas_gracia = 6;
						distancia_lineas_pentagrama = 18; 
						distancia_lineas_pentagrama_mitad = 9; 
						distancia_pentagramas = 220; // 220
						largo_imagen_corchete = 25;
						largo_imagen_corchete_gracia = 5;
						longitud_plica = 60; 
						longitud_plica_nota_gracia = 30; 
						margen_ancho_cabeza_nota = 5;
						margen_autor = 60; 
						margen_barlines = 10;
						margen_derecho_compases = 70;
						margen_inferior_autor = 120; 
						margen_izquierdo_compases = 50;
						margen_nota_gracia = 4;
						margen_obra = 20; 
						margen_superior = 35;
						mitad_cabeza_nota_vertical = 10;
						mitad_cabeza_nota_gracia_vertical = 4;
						offset_ultima_nota_gracia = 24;
						radio_octavarium = 3;
						radio_puntillos = 4;
						radio_staccatos = 4;
						tamano_letra_autor = 25;
						tamano_letra_bip_preparacion = 300;
						tamano_letra_bpm = 50;
						tamano_letra_numero_compas = 30; 
						tamano_letra_obra = 40; 
						tamano_letra_palm_mute = 30;
						tamano_letra_pulso = 50;
						tamano_letra_tapping = 30;
						tamano_letra_tresillo = 25;
						tamano_letra_tempo = 45;
						tamano_letra_words = 30;
						unidad_desplazamiento = 50;
						this.width = width;
										
						y_accent_up = 30;
						x_accidental = 20;
						y_accidental = 10;
						y_accidental_flat = 17;
						y_bend = 40;
						y_bpm = 30;
						y_clave_sol_segunda = 63;
						x_inicial_pentagramas = 80;
						x_final_pentagramas = width - x_inicial_pentagramas;
						x_inicio_slash = 15;
						x_fin_slash = 5;
						y_inicio_slash = 10;
						y_fin_slash = 20;
						x_fermata = 15;
						y_fermata = 50;
						y_ligaduras_expresion = 50;
						x_ligaduras_union = 20;
						y_ligaduras_union = 24;
						x_numero_compas = 50;
						y_numero_compas = 15;
						x_octavarium = 15;
						y_octavarium = 8;
						y_octavarium_2 = 30;
						y_palm_mute = 50;
						x_puntillo = ancho_cabeza_nota + 10;
						y_puntillo_arriba = mitad_cabeza_nota_vertical - 10;
						y_puntillo_abajo = mitad_cabeza_nota_vertical + 10;
						x_staccato = 15;
						y_staccato_arriba = 28;
						y_staccato_abajo = 14;
						y_tapping = 50;
						x_tresillo = 18;
						y_tresillo_arriba = 9;
						y_tresillo_abajo = 26;
						break;
					case 800:
						
					case 960:
						break;
					case 1080:
						break;
					default:
						alto_dialog_bpm = 850;
						altura_arco_ligaduras_expresion = 30;
						altura_arco_ligaduras_union = 50;
						ancho_beams = 8;
						ancho_beams_nota_gracia = 4;
						ancho_cabeza_nota = 26;
						ancho_cabeza_nota_gracia = 15;
						ancho_claves = 88;
						ancho_dialog_bpm = 600;
						ancho_hooks = 16;
						ancho_tempo = 65;
						change_account_horizontal = 2;
						change_account_vertical = 4;
						desplazamiento_extra_nota_gracia = 40;
						distancia_corchetes = 15;
						distancia_entre_beams = 12;
						distancia_entre_beams_notas_gracia = 6;
						distancia_lineas_pentagrama = 19;
						distancia_lineas_pentagrama_mitad = 9;
						distancia_pentagramas = 240;
						largo_imagen_corchete = 25;
						largo_imagen_corchete_gracia = 5;
						longitud_plica = 60;
						longitud_plica_nota_gracia = 30;
						margen_ancho_cabeza_nota = 5;
						margen_autor = 180;
						margen_barlines = 10;
						margen_derecho_compases = 70;
						margen_inferior_autor = 320;
						margen_izquierdo_compases = 50;
						margen_nota_gracia = 4;
						margen_obra = 90;
						margen_superior = 80;
						mitad_cabeza_nota_vertical = 10;
						mitad_cabeza_nota_gracia_vertical = 4;
						offset_ultima_nota_gracia = 24;
						radio_octavarium = 3;
						radio_puntillos = 4;
						radio_staccatos = 4;
						tamano_letra_autor = 50;
						tamano_letra_bip_preparacion = 300;
						tamano_letra_bpm = 50;
						tamano_letra_numero_compas = 30;
						tamano_letra_obra = 80;
						tamano_letra_palm_mute = 30;
						tamano_letra_pulso = 50;
						tamano_letra_tapping = 30;
						tamano_letra_tresillo = 25;
						tamano_letra_tempo = 45;
						tamano_letra_words = 30;
						unidad_desplazamiento = 50;
						this.width = width;
										
						y_accent_up = 30;
						x_accidental = 20;
						y_accidental = 10;
						y_accidental_flat = 17;
						y_bend = 40;
						y_bpm = 30;
						y_clave_sol_segunda = 63;
						x_inicial_pentagramas = 80;
						x_final_pentagramas = width - x_inicial_pentagramas;
						x_inicio_slash = 15;
						x_fin_slash = 5;
						y_inicio_slash = 10;
						y_fin_slash = 20;
						y_ligaduras_expresion = 50;
						x_ligaduras_union = 20;
						y_ligaduras_union = 24;
						x_numero_compas = 50;
						y_numero_compas = 15;
						x_octavarium = 15;
						y_octavarium = 8;
						y_octavarium_2 = 30;
						y_palm_mute = 50;
						x_puntillo = ancho_cabeza_nota + 10;
						y_puntillo_arriba = mitad_cabeza_nota_vertical - 10;
						y_puntillo_abajo = mitad_cabeza_nota_vertical + 10;
						x_staccato = 15;
						y_staccato_arriba = 28;
						y_staccato_abajo = 14;
						y_tapping = 50;
						x_tresillo = 18;
						y_tresillo_arriba = 9;
						y_tresillo_abajo = 26;
						break;
				
				}

			case 400:
				break;
			case 480:
				break;
			default: 
				supported = false;
		}
	}
	
	public int getAltoDialogBpm() {
		return alto_dialog_bpm;
	}
	
	public int getAlturaArcoLigadurasExpresion() {
		return altura_arco_ligaduras_expresion;
	}
	
	public int getAlturaArcoLigadurasUnion() {
		return altura_arco_ligaduras_union;
	}
	
	public int getAnchoBeams() {
		return ancho_beams;
	}
	
	public int getAnchoBeamsNotaGracia() {
		return ancho_beams_nota_gracia;
	}
	
	public int getAnchoCabezaNota() {
		return ancho_cabeza_nota;
	}
	
	public int getAnchoCabezaNotaGracia() {
		return ancho_cabeza_nota_gracia;
	}
	
	public int getAnchoClaves() {
		return ancho_claves;
	}
	
	public int getAnchoDialogBpm() {
		return ancho_dialog_bpm;
	}
	
	public int getAnchoHooks() {
		return ancho_hooks;
	}
	
	public int getAnchoTempo() {
		return ancho_tempo;
	}
	
	public int getChangeAccountHorizontal() {
		return change_account_horizontal;
	}
	
	public int getChangeAccountVertical() {
		return change_account_vertical;
	}
	
	public int getDesplazamientoExtraNotaGracia() {
		return desplazamiento_extra_nota_gracia;
	}
	
	public int getDistanciaCorchetes() {
		return distancia_corchetes;
	}

	public int getDistanciaEntreBeams() {
		return distancia_entre_beams;
	}
	
	public int getDistanciaEntreBeamsNotasGracia() {
		return distancia_entre_beams_notas_gracia;
	}
	
	public int getDistanciaLineasPentagrama() {
		return distancia_lineas_pentagrama;
	}
	
	public int getDistanciaLineasPentagramaMitad() {
		return distancia_lineas_pentagrama_mitad;
	}
	
	public int getDistanciaPentagramas() {
		return distancia_pentagramas;
	}
	
	public int getLargoImagenCorchete() {
		return largo_imagen_corchete;
	}
	
	public int getLargoImagenCorcheteGracia() {
		return largo_imagen_corchete_gracia;
	}
	
	public int getLongitudPlica() {
		return longitud_plica;
	}
	
	public int getLongitudPlicaNotaGracia() {
		return longitud_plica_nota_gracia;
	}
	
	public int getMargenAnchoCabezaNota() {
		return margen_ancho_cabeza_nota;
	}
 	
	public int getMargenAutor() {
		return margen_autor;
	}
	
	public int getMargenBarlines() {
		return margen_barlines;
	}
	
	public int getMargenInferiorAutor() {
		return margen_inferior_autor;
	}
	
	public int getMargenDerechoCompases() {
		return margen_derecho_compases;
	}
	
	public int getMargenIzquierdoCompases() {
		return margen_izquierdo_compases;
	}
	
	public int getMargenNotaGracia() {
		return margen_nota_gracia;
	}
	
	public int getMargenObra() {
		return margen_obra;
	}
	
	public int getMargenSuperior() {
		return margen_superior;
	}
	
	public int getMitadCabezaNotaVertical() {
		return mitad_cabeza_nota_vertical;
	}
	
	public int getMitadCabezaNotaGraciaVertical() {
		return mitad_cabeza_nota_gracia_vertical;
	}
	
	public int getOffsetUltimaNotaGracia() {
		return offset_ultima_nota_gracia;
	}
	
	public int getRadioOctavarium() {
		return radio_octavarium;
	}
	
	public int getRadioPuntillos() {
		return radio_puntillos;
	}
	
	public int getRadioStaccatos() {
		return radio_staccatos;
	}
	
	public int getTamanoLetraAutor() {
		return tamano_letra_autor;
	}
	
	public int getTamanoLetraBipPreparacion() {
		return tamano_letra_bip_preparacion;
	}
	
	public int getTamanoLetraBpm() {
		return tamano_letra_bpm;
	}
	
	public int getTamanoLetraNumeroCompas() {
		return tamano_letra_numero_compas;
	}
	
	public int getTamanoLetraObra() {
		return tamano_letra_obra;
	}
	
	public int getTamanoLetraPalmMute() {
		return tamano_letra_palm_mute;
	}
	
	public int getTamanoLetraPulso() {
		return tamano_letra_pulso;
	}
	
	public int getTamanoLetraTapping() {
		return tamano_letra_tapping;
	}
	
	public int getTamanoLetraTempo() {
		return tamano_letra_tempo;
	}
	
	public int getTamanoLetraTresillo() {
		return tamano_letra_tresillo;
	}
	
	public int getTamanoLetraWords() {
		return tamano_letra_words;
	}
	
	public int getUnidadDesplazamiento() {
		return unidad_desplazamiento;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getYAccentUp() {
		return y_accent_up;
	}
	
	public int getXAccidental() {
		return x_accidental;
	}
	
	public int getYAccidental() {
		return y_accidental;
	}
	
	public int getYAccidentalFlat() {
		return y_accidental_flat;
	}
	
	public int getYBend() {
		return y_bend;
	}
	
	public int getYBpm() {
		return y_bpm;
	}
	
	public int getYClaveSolSegunda() {
		return y_clave_sol_segunda;
	}
	
	public int getXInicialPentagramas() {
		return x_inicial_pentagramas;
	}
	
	public int getXFinalPentagramas() {
		return x_final_pentagramas;
	}
	
	public int getXInicioSlash() {
		return x_inicio_slash;
	}
	
	public int getXFinSlash() {
		return x_fin_slash;
	}
	
	public int getYInicioSlash() {
		return y_inicio_slash;
	}
	
	public int getYFinSlash() {
		return y_fin_slash;
	}
	
	public int getXFermata() {
		return x_fermata;
	}
	
	public int getYFermata() {
		return y_fermata;
	}
	
	public int getYLigadurasExpresion() {
		return y_ligaduras_expresion;
	}
	
	public int getXLigadurasUnion() {
		return x_ligaduras_union;
	}
	
	public int getYLigadurasUnion() {
		return y_ligaduras_union;
	}
	
	public int getXNumeroCompas() {
		return x_numero_compas;
	}
	
	public int getYNumeroCompas() {
		return y_numero_compas;
	}

	public int getXOctavarium() {
		return x_octavarium;
	}
	
	public int getYOctavarium() {
		return y_octavarium;
	}
	
	public int getYOctavarium2() {
		return y_octavarium_2;
	}
	
	public int getYPalmMute() {
		return y_palm_mute;
	}
	
	public int getXPuntillo() {
		return x_puntillo;
	}
	
	public int getYPuntilloArriba() {
		return y_puntillo_arriba;
	}
	
	public int getYPuntilloAbajo() {
		return y_puntillo_abajo;
	}
	
	public int getYSilencioBlanca() {
		return y_silencio_blanca;
	}
	
	public int getXStaccato() {
		return x_staccato;
	}
	
	public int getYStaccatoArriba() {
		return y_staccato_arriba;
	}
	
	public int getYStaccatoAbajo() {
		return y_staccato_abajo;
	}
	
	public int getYTapping() {
		return y_tapping;
	}
	
	public int getXTresillo() {
		return x_tresillo;
	}
	
	public int getYTresilloArriba() {
		return y_tresillo_arriba;
	}
	
	public int getYTresilloAbajo() {
		return y_tresillo_abajo;
	}
	
	/**
	 * 
	 * @return True if the screen specifications of the device are supported, false otherwise
	 */
	public boolean supported() {
		return supported;
	}
}
