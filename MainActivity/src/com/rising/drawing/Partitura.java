package com.rising.drawing;

public class Partitura {

	//  Atributos
	private Compas[] compases;
	private int[] divisiones;
	private int[] margenes_y;
	private int numCompases;
	private int numDivisiones;
	private int numMargenesY;
	private int margenInferior;
	
	//  Constructor
	public Partitura() {
		this.compases = new Compas[200];
		this.divisiones = new int[200];
		this.margenes_y = new int[200];
		this.numCompases = 0;
		this.numDivisiones = 0;
		this.numMargenesY = 0;
		this.margenInferior = 0;
	}
	
	/**
	 * A�ade un nuevo comp�s a la partitura.
	 * @param compas
	 * El comp�s que se a�ade
	 * @param division
	 * La posici�n de la l�nea divisoria del comp�s
	 * @param margen_y
	 * El margen y de este comp�s
	 */
	public void nuevoCompas(Compas compas, int division, int margen_y, int pulso) {
		this.compases[numCompases++] = compas;
		this.divisiones[numDivisiones++] = division;
		this.margenes_y[numMargenesY++] = margen_y;
	}

	/**
	 *   Devuelve el n�mero de compases de la partitura
	 * @return
	 */
	public int numeroDeCompases() {
		return this.numCompases;
	}

	/**
	 *   Devuelve el n�mero de m�rgenes y de la partitura
	 * @return
	 */
	public int numeroDeMargenesY() {
		return this.numMargenesY;
	}
	
	/**
	 *   Devuelve el comp�s i de la partitura
	 * @param i
	 * �ndice en el array de una variable Partitura del comp�s que queremos
	 * @return
	 */
	public Compas compas(int i) {
		return this.compases[i];
	}
	
	/**
	 *   Devuelve la divisi�n i de la partitura
	 * @param i
	 * �ndice en el array de una variable Partitura de la divisi�n i que queremos
	 * @return
	 */
	public int division(int i) {
		return this.divisiones[i];
	}
	
	/**
	 *   Devuelve el margen y i de la partitura
	 * @param i
	 * �ndice en el array de una variable Partitura del margen y que queremos
	 * @return
	 */
	public int margenY(int i) {
		return this.margenes_y[i];
	}
	
	/**
	 *   Devuelve todas las divisiones de la partitura
	 * @return
	 */
	public int[] divisiones()  {
		return this.divisiones;
	}

	/**
	 *   Devuelve todos los m�rgenes y de la partitura
	 * @return
	 */
	public int[] margenesY()  {
		return this.margenes_y;
	}
	
	/**
	 * Asigna la posici�n x de la divisi�n i de la partitura
	 * @param i 
	 * �ndice de la divisi�n que queremos modificar
	 * @param x
	 * Nuevo valor de la posici�n x
	 */
	public void asignarDivision(int i, int x) {
		this.divisiones[i] = x;
	}
	
	/**
	 *   Asigna un valor al margen inferior de la partitura
	 * @param margen
	 * Valor del margen inferior que queremos asignar
	 */
	public void asignarMargenInferior(int margen) {
		this.margenInferior = margen;
	}
	
	/**
	 *   Devuelve el margen inferior de la partitura
	 * @return
	 */
	public int margenInferior() {
		return this.margenInferior;
	}

}