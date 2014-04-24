package com.rising.drawing;

public class Config {
	
	private boolean supported = true;
	
	private int ancho_cabeza_nota;
	private int distancia_lineas_pentagrama;
	private int distancia_lineas_pentagrama_mitad;
	private int distancia_pentagramas;
	private int x_inicial_pentagramas;
	private int x_final_pentagramas;
	private int longitud_plica;
	private int margen_autor;
	private int margen_inferior_autor;
	private int margen_lateral_compases;
	private int margen_obra;
	private int margen_superior;
	private int mitad_cabeza_nota;
	private int radio_puntillos;
	private int tamano_letra_obra;
	private int tamano_letra_autor;
	private int unidad_desplazamiento;
	private int width;
	
	public Config(int densityDPI, int width) {
		switch (densityDPI) {
			case 120:
				break;
			case 160:
				break;
			case 213:
				ancho_cabeza_nota = 10;
				distancia_lineas_pentagrama = 12;
				distancia_lineas_pentagrama_mitad = 6;
				distancia_pentagramas = 150;
				x_inicial_pentagramas = 50;
				x_final_pentagramas = width - x_inicial_pentagramas;
				longitud_plica = 40;
				margen_autor = 120;
				margen_inferior_autor = 230;
				margen_lateral_compases = 30;
				margen_obra = 60;
				margen_superior = 50;
				mitad_cabeza_nota = 6;
				radio_puntillos = 5;
				tamano_letra_obra = 50;
				tamano_letra_autor = 30;
				unidad_desplazamiento = 25;
				this.width = width;
				break;
			case 240:
				break;
			case 320:
				ancho_cabeza_nota = 26;
				distancia_lineas_pentagrama = 19;
				distancia_lineas_pentagrama_mitad = 9;
				distancia_pentagramas = 200;
				x_inicial_pentagramas = 80;
				x_final_pentagramas = width - x_inicial_pentagramas;
				longitud_plica = 60;
				margen_autor = 180;
				margen_inferior_autor = 320;
				margen_lateral_compases = 50;
				margen_obra = 90;
				margen_superior = 80;
				mitad_cabeza_nota = 10;
				radio_puntillos = 5;
				tamano_letra_obra = 80;
				tamano_letra_autor = 50;
				unidad_desplazamiento = 200;
				this.width = width;
				break;
			case 400:
				break;
			case 480:
				break;
			default: 
				supported = false;
		}
	}
	
	public int getAnchoCabezaNota() {
		return ancho_cabeza_nota;
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
	
	public int getLongitudPlica() {
		return longitud_plica;
	}
	
	public int getMargenAutor() {
		return margen_autor;
	}
	
	public int getMargenInferiorAutor() {
		return margen_inferior_autor;
	}
	
	public int getMargenLateralCompases() {
		return margen_lateral_compases;
	}
	
	public int getMargenObra() {
		return margen_obra;
	}
	
	public int getMargenSuperior() {
		return margen_superior;
	}
	
	public int getMitadCabezaNota() {
		return mitad_cabeza_nota;
	}
	
	public int getRadioPuntillos() {
		return radio_puntillos;
	}
	
	public int getTamanoLetraAutor() {
		return tamano_letra_autor;
	}
	
	public int getTamanoLetraObra() {
		return tamano_letra_obra;
	}
	
	public int getUnidadDesplazamiento() {
		return unidad_desplazamiento;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getXInicialPentagramas() {
		return x_inicial_pentagramas;
	}
	
	public int getXFinalPentagramas() {
		return x_final_pentagramas;
	}
	
	/**
	 * 
	 * @return True if the screen specifications of the device are supported, false otherwise
	 */
	public boolean supported() {
		return supported;
	}
}
