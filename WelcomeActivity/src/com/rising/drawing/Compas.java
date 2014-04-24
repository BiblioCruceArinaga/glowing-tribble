package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Compas {
	private ArrayList<Nota> notas;
	
	private ArrayList<ElementoGrafico> barlines;
	private ArrayList<ElementoGrafico> clefs;

    private boolean endingBegin;
    private boolean endingEnd;
    private boolean endingDis;
	
	private ElementoGrafico dynamics;
	private ElementoGrafico pedalStart;
	private ElementoGrafico pedalStop;
	private ElementoGrafico repeatBegin;
    private ElementoGrafico repeatEnd;
	private ElementoGrafico time;
	private ElementoGrafico words;
	
	//  Este valor está expresado en unidades dependientes del valor divisions
	//  de la partitura, y será usado posteriormente para calcular x_fin
	private int anchoCompas;
	
	private int x_ini;
	private int x_fin;
	private int y_ini;
	private int y_fin;
	
	public Compas() {
		notas = new ArrayList<Nota>();
		
		barlines = new ArrayList<ElementoGrafico>();
		clefs = new ArrayList<ElementoGrafico>();
		
		endingBegin = false;
		endingEnd = false;
		endingDis = false;
		
		dynamics = null;
		pedalStart = null;
		pedalStop = null;
		repeatBegin = null;
		repeatEnd = null;
		time = null;
		words = null;
		
		x_ini = -1;
		x_fin = -1;
		y_ini = -1;
		y_fin = -1;
	}
	
	public void addBarline(ElementoGrafico barline) {
		barlines.add(barline);
	}
	
	public void addClef(ElementoGrafico clef) {
		clefs.add(clef);
	}
	
	public void addNote(Nota note) {
		notas.add(note);
	}
	
	public ArrayList<ElementoGrafico> getClaves() {
		return clefs;
	}
	
	public ArrayList<Nota> getNotas() {
		return notas;
	}
	
	public int getAnchoCompas() {
		return anchoCompas;
	}
	
	public int getXIni() {
		return x_ini;
	}
	
	public int getXFin() {
		return x_fin;
	}
	
	public int getYIni() {
		return y_ini;
	}
	
	public int getYFin() {
		return y_fin;
	}
	
	public void setAnchoCompas(ArrayList<Byte> arrayAnchoCompas) {
		byte[] bytesArray = new byte[arrayAnchoCompas.size()];
        int len = bytesArray.length;
        for (int i=0; i<len; i++)
            bytesArray[i] = arrayAnchoCompas.get(i);
        
        try {
            String bytesString = new String(bytesArray, "UTF-8");
            anchoCompas = Integer.parseInt(bytesString);
        }
        catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
	}
	
	public void setDynamics(ElementoGrafico dynamics) {
		this.dynamics = dynamics;
	}
	
	public void setPedalStart(ElementoGrafico pedalStart) {
		this.pedalStart = pedalStart;
	}
	
	public void setPedalStop(ElementoGrafico pedalStop) {
		this.pedalStop = pedalStop;
	}
	
	public void setRepeatOrEnding(ElementoGrafico repeatOrEnding) {
		switch (repeatOrEnding.getValue(0)) {
			case 1:
				repeatBegin = repeatOrEnding;
				break;
			case 2:
				repeatEnd = repeatOrEnding;
				break;
			case 3:
				endingBegin = true;
				break;
			case 4:
				endingEnd = true;
				break;
			case 5:
				endingBegin = true;
				endingEnd = true;
				break;
			case 6:
				endingBegin = true;
				endingDis = true;
				break;
			default:
				break;
		}
	}
	
	public void setTime(ElementoGrafico time) {
		this.time = time;
	}
	
	public void setWords(ElementoGrafico words) {
		this.words = words;
	}
	
	public void setXIni(int x_ini) {
		this.x_ini = x_ini;
	}
	
	public void setXFin(int x_fin) {
		this.x_fin = x_fin;
	}
	
	public void setYIni(int y_ini) {
		this.y_ini = y_ini;
	}
	
	public void setYFin(int y_fin) {
		this.y_fin = y_fin;
	}
}
