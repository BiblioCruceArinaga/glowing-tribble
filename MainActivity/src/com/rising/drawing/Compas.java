package com.rising.drawing;

public class Compas {

	//  Atributos
	private int x_inicio;
	private int x_final;
	private Subcompas[] subcompases;
	private int numSubcompases;
	private int numeroDelCompas;
	
	//  Primera posici�n = Repetici�n al inicio si vale 1
	//  Segunda posici�n = Repetici�n al final si vale 1
	private int[] repeticiones = {0,0};
	private int[] repeticiones_x = {0,0};
	
	private int ending;
	private int tempo;
	private int tempo_x;
	
	//  Primera posici�n = Clave del primer subcomp�s
	//  Segunda posici�n = Clave del segundo subcomp�s
	private int[] claves = {0,0};
	private int claves_x;
	private int numClaves;
	
	private int intensidad;
	private int intensidad_x;
	
	//  Posiciones X de los pulsos de metr�nomo
	private int[] pulsosMetronomo;
	private int pulsosMetronomoInd;
	
	//  Constructor
	public Compas(int staves) {
		this.subcompases = new Subcompas[staves];
		this.numSubcompases = 0;
		this.ending = 0;
		this.tempo = 0;
		this.tempo_x = 0;
		this.numClaves = 0;
		this.claves_x = 0;
		this.x_inicio = 0;
		this.x_final = 0;
		this.intensidad = 0;
		this.intensidad_x = 0;
		this.numeroDelCompas = -1;
	}
	
	/**
	 *   A�adir un nuevo subcomp�s al comp�s
	 * @param subcompas
	 * Subcomp�s que se a�ade al comp�s
	 */
	public void nuevoSubcompas(Subcompas subcompas) {
		this.subcompases[numSubcompases++] = subcompas;
	}
	
	/**
	 *   Devolver el n�mero de subcompases del comp�s
	 * @return
	 */
	public int numeroDeSubcompases() {
		return this.numSubcompases;
	}
	
	/**
	 *   Devolver el subcomp�s i del comp�s
	 * @param i
	 * �ndice del subcomp�s
	 * @return
	 */
	public Subcompas subcompas(int i) {
		return this.subcompases[i];
	}
	
	/**
	 *   Asignar repetici�n
	 * @param repeticiones
	 * 1 para asignar repetici�n al principio del comp�s. 2 para asignar repetici�n al final.
	 * @param x
	 */
	public void asignarRepeticion(int repeticiones, int x) {
		switch (repeticiones) {
			case 1:
			case 2: {
				this.repeticiones[repeticiones - 1] = 1;
				this.repeticiones_x[repeticiones - 1] = x;
				break;
			}
			default: break;
		}
	}
	
	/**
	 *   Devolver la repetici�n i del comp�s
	 * @param i
	 * �ndice de la repetici�n del comp�s que queremos (0 para inicio y 1 para final)
	 * @return
	 */
	public int repeticion(int i) {
		return this.repeticiones[i];
	}
	
	/**
	 *   Devolver la posici�n x de la repetici�n i del pentagrama
	 * @param i
	 * �ndice de la repetici�n (0 para inicio, 1 para final) de la que queremos su posici�n x
	 * @return
	 */
	public int repeticion_x(int i) {
		return this.repeticiones_x[i];
	}
	
	/**
	 *   Asignar un ending al comp�s
	 * @param ending
	 * Valor del tipo de ending que tendr� el comp�s
	 */
	public void asignarEnding(int ending) {
		this.ending = ending;
	}
	
	/**
	 *   Devolver el ending del comp�s
	 * @return
	 */
	public int ending() {
		return this.ending;
	}
	
	/**
	 *   Asignar tempo
	 * @param tempo
	 * Valor del tempo que tendr� el comp�s
	 * @param x
	 * Posici�n x donde ir� ubicado el tempo
	 */
	public void asignarTempo(int tempo, int x) {
		this.tempo = tempo;
		this.tempo_x = x;
	}
	
	/**
	 *   Devolver el tempo del pentagrama
	 * @return
	 */
	public int tempo() {
		return this.tempo;
	}
	
	/**
	 *   Devolver la posici�n x del tempo del pentagrama
	 * @return
	 */
	public int tempo_x() {
		return this.tempo_x;
	}
	
	/**
	 *   A�adir una clave al comp�s. 
	 *   Entendemos que un comp�s tendr� tantas claves como subcompases tenga.
	 *   El array de claves almacena la clave de cada subcomp�s
	 * @param clave
	 * Valor de la clave que se a�adir� al comp�s
	 * @param x
	 * Posici�n x donde ir� ubicada la clave
	 */
	public void nuevaClave(int clave, int x) {
		this.claves[numClaves++] = clave;
		this.claves_x = x;
	}
	
	/**
	 *   Devolver la posici�n x de las claves del comp�s
	 * @return
	 */
	public int claves_x() {
		return this.claves_x;
	}
	
	/**
	 *   Devolver la clave i del comp�s
	 * @param i
	 * @return
	 */
	public int clave(int i) {
		return this.claves[i];
	}
	
	/**
	 *   Asignar valor a la x inicial. Entendemos por posici�n inicial el lugar en el eje
	 *   x en el que se empieza a dibujar el comp�s.
	 * @param x
	 * Valor de la posici�n inicial del comp�s en el eje x
	 */
	public void asignarXInicial(int x) {
		this.x_inicio = x;
	}
	
	/**
	 *   Asignar valor a la x final. Entendemos por posici�n final el lugar en el eje
	 *   x en el que se empieza a dibujar el comp�s.
	 * @param x
	 * Valor de la posici�n final del comp�s en el eje x.
	 */
	public void asignarXFinal(int x) {
		this.x_final = x;
	}
	
	/**
	 *   Devolver la x inicial del comp�s
	 * @return
	 */
	public int x_inicial() {
		return this.x_inicio;
	}
	
	/**
	 *   Devolver la x final del comp�s
	 * @return
	 */
	public int x_final() {
		return this.x_final;
	}
	
	/**
	 *   Asignar intensidad al comp�s (piano, pianissimo, forte, fortisimo, etc.)
	 * @param intensidad
	 * Valor de la intensidad que tendr� el comp�s
	 * @param x
	 * Posici�n x donde ir� ubicada la intensidad
	 */
	public void asignarIntensidad(int intensidad, int x) {
		this.intensidad = intensidad;
		this.intensidad_x = x;
	}
	
	/**
	 *   Devolver la intensidad del comp�s
	 * @return
	 */
	public int intensidad() {
		return this.intensidad;
	}
	
	/**
	 *   Devolver la posici�n x de la intensidad del comp�s
	 * @return
	 */
	public int intensidad_x() {
		return this.intensidad_x;
	}
	
	/**
	 *   Asignar un n�mero al comp�s. 
	 *   Normalmente s�lo se usar� para los compases que comienzan un rengl�n
	 * @param numero
	 * Valor numérico que se asocia al comp�s
	 */
	public void asignarNumeroAlCompas(int numero) {
		this.numeroDelCompas = numero;
	}
	
	/**
	 *   Devolver el n�mero del comp�s. 
	 *   Si no se le ha asignado un n�mero previamente, devolver� un 0
	 * @return
	 */
	public int numeroDelCompas() {
		return this.numeroDelCompas;
	}
	
	/**
	 * Devolver el ancho del comp�s
	 * @return
	 */
	public int ancho() {
		return this.x_final - this.x_inicio;
	}
	
	/**
	 *   Modificar la posici�n x de las claves del comp�s
	 * @param x Nuevo valor que tendr� la x
	 */
	public void modificarXdeClaves(int x) {
		this.claves_x = x;
	}

	/**
	 *   Modificar la posici�n x de la repetici�n i
	 * @param i �ndice de la repetici�n que queremos modificar
	 * @param x Nuevo valor que tendr� la x
	 */
	public void modificarXdeRepeticion(int i, int x) {
		this.repeticiones_x[i] = x;
	}

	/**
	 *   Modificar la posici�n x del tempo del comp�s
	 * @param x Nuevo valor que tendr� la x
	 */
	public void modificarXdeTempo(int x) {
		this.tempo_x = x;
	}

	/**
	 *   Preparar el array en el que ser�n guardados los 
	 *   pulsos de metr�nomo
	 * @param n N�mero de pulsos que tendr� este comp�s
	 */
	public void inicializarPulsos(int n) {
		this.pulsosMetronomo = new int[n];
		this.pulsosMetronomoInd = 0;
	}
	
	/**
	 *   A�ade un nuevo pulso de metr�nomo al comp�s
	 * @param x Posici�n x del nuevo pulso
	 */
	public void nuevoPulsoMetronomo(int x) {
		this.pulsosMetronomo[pulsosMetronomoInd++] = x;
	}
	
	/**
	 *   Devuelve el pulso de metr�nomo i del comp�s
	 * @param i �ndice del pulso deseado
	 * @return
	 */
	public int pulso(int i) {
		return this.pulsosMetronomo[i];
	}
	
	/**
	 *   Devuelve el n�mero de pulsos del comp�s
	 * @return
	 */
	public int numeroPulsos() {
		return this.pulsosMetronomoInd;
	}
}
