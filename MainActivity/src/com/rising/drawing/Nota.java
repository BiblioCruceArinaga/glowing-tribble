package com.rising.drawing;

public class Nota {

	//  Atributos
	private int x;
	private int y;
	private int nota;
	private int octava;
	private int figuracion;
	private int accion;
	private int union;
	private boolean invertidaEnX;
	
	/**
	 * Clase que representa una nota. Contiene toda la informaci�n asociada a la nota.
	 * 
	 * @param x Posici�n en el eje X de la nota.
	 * @param y Posici�n en el eje Y de la nota.
	 * @param nota Valor musical de la nota (Do, Re, Mi, etc.). Representado con un entero.
	 * @param octava Octava de la nota.
	 * @param figuracion Figuraci�n (Blanca, negra, corchea, etc.) de la nota.
	 * @param accion Acci�n que se ejecuta sobre la nota (vibrato, bending, slide, etc.).
	 * @param union Representa de qu� forma el corchete de la nota est� unido con la siguiente.
	 */
	public Nota(int x, int y, int nota, int octava, int figuracion, int accion, int union) {
		this.x = x;
		this.y = y;
		this.nota = nota;
		this.octava = octava;
		this.figuracion = figuracion;
		this.accion = accion;
		this.union = union;
		this.invertidaEnX = false;
	}

	/**
	 * Devuelve la posici�n en el eje X de la nota
	 * @return 
	 */
	public int x() {
		return this.x;
	}
	
	/**
	 * Devuelve la posici�n en el eje Y de la nota
	 * @return 
	 */
	public int y() {
		return this.y;
	}
	
	/**
	 *   Devuelve el valor de la nota (Do, Re, Mi, etc.)
	 * @return
	 */
	public int nota() {
		return this.nota;
	}
	
	/**
	 *   Devolver la octava de la nota
	 * @return
	 */
	public int octava() {
		return this.octava;
	}
	
	/**
	 *   Devolver la figuraci�n de la nota
	 * @return
	 */
	public int figuracion() {
		return this.figuracion;
	}
	
	/**
	 *   Devolver la acci�n ejecutada sobre la nota
	 * @return
	 */
	public int accion() {
		return this.accion;
	}
	
	/**
	 *  Devolver el tipo de uni�n del corchete de la nota
	 * @return
	 */
	public int union() {
		return this.union;
	}
	
	/**
	 *   Asignar un valor a la x de la nota
	 * @param x
	 */
	public void asignarX(int x) {
		this.x = x;
	}
	
	/**
	 *   Asignar un valor a la y de la nota
	 * @param y
	 */
	public void asignarY(int y) {
		this.y = y;
	}
	
	/**
	 *   Asignar inversi�n en X a la nota
	 * @param inversion
	 */
	public void asignarInversionEnX(boolean inversion) {
		this.invertidaEnX = inversion;
	}
	
	/**
	 *   Devolver la inversi�n en X de la nota
	 * @return
	 */
	public boolean inversionEnX() {
		return this.invertidaEnX;
	}
}
