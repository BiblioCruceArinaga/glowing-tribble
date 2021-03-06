package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public class Compas {

	//  Información tal cual fue leída en el fichero
	private ArrayList<ElementoGrafico> barlines;
	private ElementoGrafico[] clefs = {null, null};
	private ArrayList<Integer> positions;
	private ElementoGrafico dynamics;
	private ElementoGrafico pedalStart;
	private ElementoGrafico pedalStop;
	private ElementoGrafico time;
	private ElementoGrafico words;
	
	//  Información ya analizada
	private ArrayList<Nota> notas;
	private Clave[] claves = {null, null};
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
		positions = new ArrayList<Integer>();
		dynamics = null;
		pedalStart = null;
		pedalStop = null;
		time = null;
		words = null;
		
		notas = new ArrayList<Nota>();
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

	public void addClef(ElementoGrafico clef) {
		if (clefs[0] == null)
			clefs[0] = clef;
		else
			clefs[1] = clef;
		
		if (clef != null)
			if (!positions.contains(clef.getPosition()))
				positions.add(clef.getPosition());
	}
	
	public void addNote(Nota note) {
		notas.add(note);
		
		if (note != null)
			if (!positions.contains(note.getPosition())) 
				positions.add(note.getPosition());
	}
	
	public void clearClefs() {
		clefs[0] = null;
		clefs[1] = null;
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
		return claves[index];
	}
	
	public Clave getClavePorPentagrama(int pentagrama) {
		return claves[pentagrama - 1];
	}
	
	public Clave[] getClaves() {
		return claves;
	}
	
	public ElementoGrafico[] getClefs() {
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

	public ArrayList<Nota> getNotas() {
		return notas;
	}
	
	public int getNumeroCompas() {
		return numeroCompas;
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
		return claves[0] != null || claves[1] != null;
	}
	
	public boolean hayClefs() {
		return clefs[0] != null || clefs[1] != null;
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
		return tempo != null && tempo.dibujar();
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
	
	//  Devuelve true si no hay notas en este compás
	//  después de esta clave
	public boolean noHayNotasDelanteDeClave(Clave clave) {
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			if (clave.getPentagrama() == getNota(i).getPentagrama())
				if (getNota(i).getPosition() > clave.getPosition())
					return false;
		
		return true;
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
	public ArrayList<Integer> saberXsDelCompas() {
		ArrayList<Integer> xEncontradas = new ArrayList<Integer>();

		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			if (!xEncontradas.contains(notas.get(i).getX()))
				xEncontradas.add(notas.get(i).getX());

		if (hayClaves())
			for (int i=0; i<claves.length; i++)
				if (getClave(i) != null)
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
		
		if (hayTempo())
			if (!xEncontradas.contains(getTempo().getX()))
				xEncontradas.add(getTempo().getX());
		
		Collections.sort(xEncontradas);
		return xEncontradas;
	}
	
	//  Devuelve un array con todas las posiciones x de
	//  todas las notas del compás
	public ArrayList<Integer> saberXsDeNotas() {
		ArrayList<Integer> xEncontradas = new ArrayList<Integer>();

		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			if (!xEncontradas.contains(notas.get(i).getX()))
				xEncontradas.add(notas.get(i).getX());
		
		Collections.sort(xEncontradas);
		return xEncontradas;
	}
	
	public int saberXPrimeraNota() {
		return notas.get(0).getX();
	}
	
	//  Devuelve la posición X de la nota más cercana al margen derecho
	public int saberXUltimaNota() {
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
	
	public void setClave(Clave clave, byte pentagrama) {
		claves[pentagrama - 1] = clave;
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
		
		if (time != null)
			if (!positions.contains(time.getPosition())) 
				positions.add(time.getPosition());
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

	public Compas clonar() {
		Compas nuevoCompas = new Compas();
		
		clonarFigurasGraficas(nuevoCompas);
		clonarNotas(nuevoCompas);
		
		return nuevoCompas;
	}
	
	private ElementoGrafico clonarElementoGrafico(ElementoGrafico old) {
		if (old == null) 
			return null;
		else {			
			ElementoGrafico nuevo = new ElementoGrafico();
			
			nuevo.setPosition(old.getPosition());
			nuevo.addAllValues(old.getValues());
			nuevo.setX(old.getX());
			
			return nuevo;
		}
	}
	
	private void clonarFigurasGraficas(Compas nuevoCompas) {
		int numBarlines = barlines.size();
		for (int i=0; i<numBarlines; i++)
			nuevoCompas.addBarline(clonarElementoGrafico(barlines.get(i)));
		
		nuevoCompas.addClef(clonarElementoGrafico(clefs[0]));
		nuevoCompas.addClef(clonarElementoGrafico(clefs[1]));
		
		nuevoCompas.setDynamics(clonarElementoGrafico(getDynamics()));
		nuevoCompas.setPedalStart(clonarElementoGrafico(getPedalStart()));
		nuevoCompas.setPedalStop(clonarElementoGrafico(getPedalStop()));
		nuevoCompas.setTime(clonarElementoGrafico(getTime()));
		nuevoCompas.setWords(clonarElementoGrafico(getWords()));
	}
	
	private Nota clonarNota(Nota oldNote) {
		Nota newNote = new Nota(oldNote.getStep(), oldNote.getOctava(), oldNote.getFiguracion(),
				oldNote.getBeam(), oldNote.getBeamId(), oldNote.getPlica(), oldNote.getVoz(),
				oldNote.getPentagrama(), oldNote.getFigurasGraficas(), oldNote.getPosicionArray());
		
		return newNote;
	}
	
	private void clonarNotas(Compas nuevoCompas) {
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			nuevoCompas.addNote(clonarNota(notas.get(i)));
	}
}