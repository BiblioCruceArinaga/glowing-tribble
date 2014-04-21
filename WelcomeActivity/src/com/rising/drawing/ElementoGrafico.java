package com.rising.drawing;

import java.util.ArrayList;

/*
 * Clase genérica que representa cualquier figura gráfica
 * que pueda dibujarse en un compás: claves, tempos, indicaciones
 * textuales, etc. Con esta clase nos evitamos tener que crear
 * una clase distinta para cada tipo de elemento gráfico que
 * puede haber en una partitura.
 * 
 * El array values almacena los bytes que representan la
 * información asociada al elemento gráfico. La manera en la que
 * hay que interpretar estos bytes varía dependiendo del elemento
 * gráfico que estén representando. Por ello, hay que tener claro
 * a qué elemento hace alusión el objeto. Por ejemplo: si el objeto
 * representa una clave, deberá tratarse con una función diseñada
 * para tratar las claves; si representa un tempo, hará falta una
 * función que maneje los tempos, y así sucesivamente.
 * 
 * El entero position representa la posición en el eje X en la
 * que se coloca el elemento gráfico. No es la posición real, 
 * ya que esa posición sólo se conoce cuando se va a dibujar el
 * elemento, pero es un indicador de cuál es su posición en el 
 * compás en relación al resto de elementos que hay. En otras
 * palabras: conociendo este valor y comparándolo con el de el
 * resto de elementos gráficos (incluyendo las notas), podemos
 * saber si es, de izquierda a derecha, el primer elemento, el
 * segundo, el tercero, etc.
 */
public class ElementoGrafico {
	private ArrayList<Byte> values;
    private int position;
    
    public ElementoGrafico() {
        values = new ArrayList<Byte>();
        position = 0;
    }
    
    private String bytesArrayToString(ArrayList<Byte> array) {
    	String string = "";
        int num = array.size();
        int intToASCII = 0;
        
        for (int i=0; i<num; i++) {
        	intToASCII = array.get(i);
        	string += Character.toString((char) intToASCII);
        }
        
        return string;
    }
    
    public int getPosition() {
        return position;
    }
    
    public byte getValue(int index) {
        return values.get(index);
    }
    
    public ArrayList<Byte> getValues() {
        return values;
    }
    
    public void setPosition(ArrayList<Byte> position) {
        String positionString = bytesArrayToString(position);
        
        if (!positionString.equals("")) 
        	this.position = Integer.parseInt(positionString);
    }
    
    public void addValue(byte value) {
        values.add(value);
    }
    
    public void addAllValues(ArrayList<Byte> allValues) {
        values.addAll(allValues);
    }
}
