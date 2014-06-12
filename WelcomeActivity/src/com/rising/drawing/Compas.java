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
    
    //  Bpm y su posición en el array de órdenes de dibujo
    private int bpm;
    private int bpmIndex;
    
	private int x_ini;
	private int x_fin;
	private int y_ini;
	private int y_fin;
	private int x_ini_notas;
	
	private int numeroCompas;
	
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
		
		bpm = -1;
		bpmIndex = -1;

		x_ini = -1;
		x_fin = -1;
		y_ini = -1;
		y_fin = -1;
		x_ini_notas = -1;
		
		numeroCompas = -1;
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
	}
	
	public void arreglarPosicionesPorClave(int unidadDesplazamiento) {
		for (int i=0; i<claves.size(); i++) {
			int posicion = claves.get(i).getX();
			
			for (int j=0; j<notas.size(); j++) {
				if (notas.get(j).getX() >= posicion)
					notas.get(j).setX(notas.get(j).getX() + unidadDesplazamiento);
			}
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
	
	public Clave getClave(int index) {
		return claves.get(index);
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
	
	public Intensidad getIntensidad() {
		return intensidad;
	}

	public Nota getNota(int index) {
		return notas.get(index);
	}
	
	public int getNumeroCompas() {
		return numeroCompas;
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
		int numGolpes = 0;
		ArrayList<Integer> xEncontradas = new ArrayList<Integer>();
		
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++) {
			if (!xEncontradas.contains(notas.get(i).getX())) {
				xEncontradas.add(notas.get(i).getX());
				
				//  La unidad mínima de "golpe de sonido" es la corchea. 
				//  Si se tocan negras o notas de mayor duración, se 
				//  añaden golpes de sonido extra. El +1 es el golpe
				//  mínimo que corresponde a la nota encontrada
				numGolpes += golpesExtra(notas.get(i)) + 1;
			}
		}
		
		return numGolpes;
	}
	
	private int golpesExtra(Nota nota) {
		switch (nota.getFiguracion()) {
			case 11: 
				if (nota.tienePuntillo()) return 6;
				else return 5;
			default: 
				return 0;
		}
	}
	
	public boolean hayBarlines() {
		return !barlines.isEmpty();
	}
	
	public boolean hayClaves() {
		return !claves.isEmpty();
	}
	
	public boolean hayClefs() {
		return !clefs.isEmpty();
	}
	
	public boolean hayDynamics() {
		return dynamics != null;
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
	
	//  Devuelve un array con cada valor de X de cada elemento
	//  del compás. Por elemento se entiende cualquier nota, 
	//  acorde o figura gráfica que ocupe una posición X única en el compás
	public ArrayList<Integer> saberNumeroDeElementosDeCompas() {
		ArrayList<Integer> xEncontradas = new ArrayList<Integer>();

		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			if (!xEncontradas.contains(notas.get(i).getX()))
				xEncontradas.add(notas.get(i).getX());

		if (hayClaves())
			for (int i=0; i<claves.size(); i++)
				if (!xEncontradas.contains(getClave(i).getX()))
					xEncontradas.add(getClave(i).getX());
		
		if (hayIntensidad())
			if (!xEncontradas.contains(getIntensidad().getX()))
				xEncontradas.add(getIntensidad().getX());
		
		if (hayPedalInicio())
			if (!xEncontradas.contains(getPedalInicio().getX()))
				xEncontradas.add(getPedalInicio().getX());
		
		if (hayPedalFin())
			if (!xEncontradas.contains(getPedalFin().getX()))
				xEncontradas.add(getPedalFin().getX());
		
		Collections.sort(xEncontradas);
		return xEncontradas;
	}
	
	//  Devuelve la posición X de la nota más cercana al margen derecho
	public int saberXMasGrande() {
		int xMasGrande = 0;
		
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++) {
			if (xMasGrande < notas.get(i).getX())
				xMasGrande = notas.get(i).getX();
		}
		
		return xMasGrande;
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
	
	public void setIntensidad(Intensidad intensidad) {
		this.intensidad = intensidad;
	}
	
	public void setNumeroCompas(int numeroCompas) {
		this.numeroCompas = numeroCompas;
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
