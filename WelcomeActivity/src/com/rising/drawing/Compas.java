package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public class Compas {

	//  Información tal cual fue leída en el fichero
	private ArrayList<ElementoGrafico> barlines;
	private ArrayList<ElementoGrafico> clefs;
	private ArrayList<Integer> positions;
	private ElementoGrafico dynamics;
	private ElementoGrafico pedalStart;
	private ElementoGrafico pedalStop;
	private ElementoGrafico time;
	private ElementoGrafico words;
	
	//  Información ya analizada
	private ArrayList<Nota> notas;
	private ArrayList<Clave> claves;
	private Intensidad intensidad;
	private Pedal pedalInicio;
	private Pedal pedalFin;
	private Tempo tempo;
	private Texto texto;
	
	private boolean repeatBegin;
    private boolean repeatEnd;
    private boolean endingBegin;
    private boolean endingEnd;
    private boolean endingDis;
    
    //  Bpm y su posición en el array de órdenes de dibujo
    private int bpm;
    private int bpmIndex;
    
	private int x_ini;
	private int x_fin;
	private int y_ini;
	private int y_fin;
	
	//  Posición X donde empiezan a colocarse las notas 
	private int x_ini_notas;
	
	public Compas() {
		barlines = new ArrayList<ElementoGrafico>();
		clefs = new ArrayList<ElementoGrafico>();
		positions = new ArrayList<Integer>();
		dynamics = null;
		pedalStart = null;
		pedalStop = null;
		time = null;
		words = null;
		
		notas = new ArrayList<Nota>();
		claves = new ArrayList<Clave>();
		intensidad = null;
		pedalInicio = null;
		pedalFin = null;
		tempo = null;
		texto = null;
		
		repeatBegin = false;
		repeatEnd = false;
		endingBegin = false;
		endingEnd = false;
		endingDis = false;
		
		bpm = -1;
		bpmIndex = -1;

		x_ini = -1;
		x_fin = -1;
		y_ini = -1;
		y_fin = -1;
		x_ini_notas = -1;
	}
	
	public void addBarline(ElementoGrafico barline) {
		barlines.add(barline);
	}
	
	public void addClave(Clave clave) {
		claves.add(clave);
	}
	
	public void addClef(ElementoGrafico clef) {
		clefs.add(clef);
		
		if (!positions.contains(clef.getPosition()))
			positions.add(clef.getPosition());
	}
	
	public void addNote(Nota note) {
		notas.add(note);
		
		if (!positions.contains(note.getPosicion())) 
			positions.add(note.getPosicion());
		
		int i = 0;
		if (i == 0) {
			i++;
		}
	}
	
	public void clearClefs() {
		clefs.clear();
	}
	
	public ArrayList<ElementoGrafico> getBarlines() {
		return barlines;
	}
	
	public int getBpm() {
		return bpm;
	}
	
	public int getBpmIndex() {
		return bpmIndex;
	}
	
	public ArrayList<Clave> getClaves() {
		return claves;
	}
	
	public ArrayList<ElementoGrafico> getClefs() {
		return clefs;
	}
	
	public ElementoGrafico getDynamics() {
		return dynamics;
	}
	
	public boolean getEndingBegin() {
		return endingBegin;
	}
	
	public boolean getEndingDis() {
		return endingDis;
	}
	
	public boolean getEndingEnd() {
		return endingEnd;
	}
	
	public Intensidad getIntensidad() {
		return intensidad;
	}

	public Nota getNota(int index) {
		return notas.get(index);
	}
	
	public ArrayList<Nota> getNotas() {
		return notas;
	}
	
	public Pedal getPedalFin() {
		return pedalFin;
	}
	
	public Pedal getPedalInicio() {
		return pedalInicio;
	}
	
	public ElementoGrafico getPedalStart() {
		return pedalStart;
	}
	
	public ElementoGrafico getPedalStop() {
		return pedalStop;
	}
	
	public ArrayList<Integer> getPositions() {
		Collections.sort(positions);
		return positions;
	}
	
	public boolean getRepeatBegin() {
		return repeatBegin;
	}
	
	public boolean getRepeatEnd() {
		return repeatEnd;
	}
	
	public Tempo getTempo() {
		return tempo;
	}
	
	public Texto getTexto() {
		return texto;
	}
	
	public ElementoGrafico getTime() {
		return time;
	}
	
	public ElementoGrafico getWords() {
		return words;
	}
	
	public byte getWordsLocation() {
		return words.getValue(0);
	}
	
	public int getWordsPosition() {
		return words.getPosition();
	}
	
	public String getWordsString() {
		ArrayList<Byte> bytesWords = words.getValues();
		byte[] bytesArray = new byte[bytesWords.size()];
        int len = bytesArray.length;
        for (int i=1; i<len; i++) bytesArray[i] = bytesWords.get(i);
        
        try {
            return new String(bytesArray, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            return "";
        }
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
	
	public int getXIniNotas() {
		return x_ini_notas;
	}
	
	//  Devuelve el número de golpes de sonido que debe leer el micro para considerar
	//  que este compás ya ha sido interpretado en su totalidad. Los acordes cuentan 
	//  como un único sonido. Las notas tocadas a la vez en diferentes pentagramas o
	//  voces comparten la misma x, y por tanto cuentan como un unico golpe de sonido.
	public int golpesDeSonido() {
		ArrayList<Integer> xEncontradas = new ArrayList<Integer>();
		
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			if (!xEncontradas.contains(notas.get(i).getX()))
				xEncontradas.add(notas.get(i).getX());
		
		return xEncontradas.size();
	}
	
	public boolean hayBarlines() {
		return !barlines.isEmpty();
	}
	
	public boolean hayClefs() {
		return !clefs.isEmpty();
	}
	
	public boolean hayDynamics() {
		return dynamics != null;
	}
	
	//  Esta implementación de los ending está asumiendo que los
	//  ending son de un compás de ancho máximo, y también que sólo
	//  habrá dos ending seguidos como mucho
	public boolean hayEnding1() {
		return endingBegin && endingEnd;
	}
	
	public boolean hayIntensidad() {
		return intensidad != null;
	}

	public boolean hayPedals() {
		return pedalStart != null || pedalStop != null;
	}
	
	public boolean hayPedales() {
		return pedalFin != null || pedalInicio != null;
	}
	
	public boolean hayPedalFin() {
		return pedalFin != null;
	}
	
	public boolean hayPedalInicio() {
		return pedalInicio != null;
	}
	
	public boolean hayPedalStart() {
		return pedalStart != null;
	}
	
	public boolean hayPedalStop() {
		return pedalStop != null;
	}
	
	public boolean hayTempo() {
		return tempo != null;
	}
	
	public boolean hayTexto() {
		return texto != null;
	}
	
	public boolean hayTime() {
		return time != null;
	}
	
	public boolean hayWords() {
		return words != null;
	}

	public int numeroDeNotas() {
		return notas.size();
	}
	
	public int numeroDePulsos() {
		return tempo.numeroDePulsos();
	}
	
	public void setBpm(int bpm) {
		this.bpm = bpm;
	}
	
	public void setBpmIndex(int bpmIndex) {
		this.bpmIndex = bpmIndex;
	}
	
	public void setDynamics(ElementoGrafico dynamics) {
		this.dynamics = dynamics;
		
		if (dynamics != null)
			if (!positions.contains(dynamics.getPosition()))
				positions.add(dynamics.getPosition());
	}
	
	public void setEndingBegin(boolean endingBegin) {
		this.endingBegin = endingBegin;
	}
	
	public void setEndingDis(boolean endingDis) {
		this.endingDis = endingDis;
	}
	
	public void setEndingEnd(boolean endingEnd) {
		this.endingEnd = endingEnd;
	}
	
	public void setIntensidad(Intensidad intensidad) {
		this.intensidad = intensidad;
	}
	
	public void setPedalFin(Pedal pedalFin) {
		this.pedalFin = pedalFin;
	}
	
	public void setPedalInicio(Pedal pedalInicio) {
		this.pedalInicio = pedalInicio;
	}

	public void setPedalStart(ElementoGrafico pedalStart) {
		this.pedalStart = pedalStart;
		
		if (pedalStart != null)
			if (!positions.contains(pedalStart.getPosition()))
				positions.add(pedalStart.getPosition());
	}
	
	public void setPedalStop(ElementoGrafico pedalStop) {
		this.pedalStop = pedalStop;
		
		if (pedalStop != null)
			if (!positions.contains(pedalStop.getPosition()))
				positions.add(pedalStop.getPosition());
	}
	
	public void setRepeatBegin(boolean repeatBegin) {
		this.repeatBegin = repeatBegin;
	}
	
	public void setRepeatEnd(boolean repeatEnd) {
		this.repeatEnd = repeatEnd;
	}
	
	public void setRepeatOrEnding(byte repeatOrEnding) {
		switch (repeatOrEnding) {
			case 1:
				repeatBegin = true;
				break;
			case 2:
				repeatEnd = true;
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
	
	public void setTempo(Tempo tempo) {
		this.tempo = tempo;
	}
	
	public void setTexto(Texto texto) {
		this.texto = texto;
	}
	
	public void setTime(ElementoGrafico time) {
		this.time = time;
	}
	
	public void setWords(ElementoGrafico words) {
		this.words = words;
		
		if (words != null)
			if (!positions.contains(words.getPosition()))
				positions.add(words.getPosition());
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
	
	public void setXIniNotas(int x_ini_notas) {
		this.x_ini_notas = x_ini_notas;
	}
}
