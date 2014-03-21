package com.rising.drawing;

public class NotaDeGracia {
	
	private Nota nota;
	private int compas;
	private int subcompas;
	
	//  Considerando las notas individuales y los acordes como elementos,
	//  este valor indica en qué posición se encuentra como elemento
	private int posicion;
	
	public NotaDeGracia(Nota nota, int compas, int subcompas, int posicion) {
		this.nota = nota;
		this.compas = compas;
		this.subcompas = subcompas;
		this.posicion = posicion;
	}
	
	/**
	 * 
	 * @return Devuelve la nota de gracia
	 */
	public Nota nota() {
		return this.nota;
	}
	
	/**
	 * 
	 * @return Devuelve el compás en el que se encuentra la nota de gracia
	 */
	public int compas() {
		return this.compas;
	}
	
	/**
	 * 
	 * @return Devuelve el subcompás en el que se encuentra la nota de gracia
	 */
	public int subcompas() {
		return this.subcompas;
	}
	
	/**
	 * 
	 * @return Devuelve la posición de la nota de gracia
	 */
	public int posicion() {
		return this.posicion;
	}
}
