package com.rising.drawing;

public class Subcompas {

	//  Atributos
	private Nota[] notas;
	private int numNotas;
	
	//  Constructor
	public Subcompas() {
		this.numNotas = 0;
		this.notas = new Nota[50];
	}

	/**
	 *   Añadir una nota al subcompás
	 * @param nota
	 * Valor de la nota añadida al subcompás
	 */
	public void nuevaNota(Nota nota) {
		this.notas[numNotas++] = nota;
	}
	
	/**
	 *   Inserta (nota) en la posición indicada por (indice)
	 * @param indice Posición en la que queremos que se inserte la nota
	 * @param nota Nota a insertar
	 */
	public void nuevaNotaEnIndice(int indice, Nota nota) {
		for (int i=numNotas; i>=indice; i--) {
			this.notas[i+1] = this.notas[i];
		}
		this.notas[indice] = nota;
		numNotas++;
	}

	/**
	 *   Devolver el nï¿½mero de notas del subcompï¿½s
	 * @return
	 */
	public int numeroDeNotas() {
		return numNotas;
	}
	
	/**
	 *   Devolver la nota que se encuentra en la posiciï¿½n indicada por parï¿½metro
	 * @param i
	 * ï¿½ndice de la nota que queremos
	 * @return
	 */
	public Nota nota(int i) {
		return notas[i];
	}
}
