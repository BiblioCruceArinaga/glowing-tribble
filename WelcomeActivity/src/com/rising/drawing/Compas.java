package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public class Compas {

	//  Información tal cual fue leída en el fichero
	private ArrayList<ElementoGrafico> barlines;
	private ArrayList<ElementoGrafico> clefs;
	private ArrayList<ElementoGrafico> dynamics;
	private ElementoGrafico fifths;
	private ArrayList<ElementoGrafico> pedalStarts;
	private ArrayList<ElementoGrafico> pedalStops;
	private ElementoGrafico time;
	private ArrayList<ElementoGrafico> wedges;
	private ArrayList<ElementoGrafico> words;
	
	//  Información ya analizada
	private ArrayList<Nota> notas;
	private ArrayList<Clave> claves;
	private ArrayList<Wedge> crescendos;
	private ArrayList<Wedge> diminuendos;
	private ArrayList<Intensidad> intensidades;
	private ArrayList<Pedal> pedalesInicio;
	private ArrayList<Pedal> pedalesFin;
	private Quintas quintas;
	private Tempo tempo;
	private ArrayList<Texto> textos;
    
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
		dynamics = new ArrayList<ElementoGrafico>();
		fifths = null;
		pedalStarts = new ArrayList<ElementoGrafico>();
		pedalStops = new ArrayList<ElementoGrafico>();
		time = null;
		wedges = new ArrayList<ElementoGrafico>();
		words = new ArrayList<ElementoGrafico>();
		
		claves = new ArrayList<Clave>();
		crescendos = new ArrayList<Wedge>();
		diminuendos = new ArrayList<Wedge>();
		notas = new ArrayList<Nota>();
		intensidades = new ArrayList<Intensidad>();
		pedalesInicio = new ArrayList<Pedal>();
		pedalesFin = new ArrayList<Pedal>();
		quintas = null;
		tempo = null;
		textos = new ArrayList<Texto>();
		
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
		
		updateXFin(clave.getX());
	}

	public void addClef(ElementoGrafico clef) {
		if (clef != null) {
			clefs.add(clef);
		}
	}
	
	public void addCrescendo(Wedge crescendo) {
		crescendos.add(crescendo);
		
		updateXFin(crescendo.getXFin());
	}
	
	public void addDiminuendo(Wedge diminuendo) {
		diminuendos.add(diminuendo);
		
		updateXFin(diminuendo.getXFin());
	}
	
	public void addDynamics(ElementoGrafico dynamics) {
		if (dynamics != null) {
			this.dynamics.add(dynamics);
		}
	}
	
	public void addIntensidad(Intensidad intensidad) {
		intensidades.add(intensidad);
		
		updateXFin(intensidad.getX());
	}
	
	public void addNote(Nota note) {
		notas.add(note);
	}
	
	public void addPedalFin(Pedal pedalFin) {
		pedalesFin.add(pedalFin);
		
		updateXFin(pedalFin.getX());
	}
	
	public void addPedalInicio(Pedal pedalInicio) {
		pedalesInicio.add(pedalInicio);
		
		updateXFin(pedalInicio.getX());
	}

	public void addPedalStart(ElementoGrafico pedalStart) {
		pedalStarts.add(pedalStart);
	}
	
	public void addPedalStop(ElementoGrafico pedalStop) {
		pedalStops.add(pedalStop);
	}
	
	public void addTexto(Texto texto) {
		textos.add(texto);
		
		updateXFin(texto.getX());
	}
	
	public void addWedge(ElementoGrafico wedge) {
		wedges.add(wedge);
	}
	
	public void addWords(ElementoGrafico words) {
		this.words.add(words);
	}
	
	public int[] clavesAlFinalDelCompas(int staves) {
		int[] clavesAlFinal = new int[staves];
		for (int i=0; i<clavesAlFinal.length; i++)
			clavesAlFinal[i] = -1;
		
		for (int i=0; i<claves.size(); i++) {
			if (noHayNotasDelanteDeClave(claves.get(i))) {
				clavesAlFinal[claves.get(i).getPentagrama() - 1] = i;
			}
		}
		
		return clavesAlFinal;
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
	
	public byte getClavePorPentagrama(Nota nota) {
		int claveMasCercana = -1;
		for (int i=0; i<claves.size(); i++) {
			if (claves.get(i).getPentagrama() == nota.getPentagrama()) {
				if (claves.get(i).getX() <= nota.getX()) {
					claveMasCercana = i;
				}
			}
		}
		
		if (claveMasCercana > -1) return claves.get(claveMasCercana).getByteClave();
		else return (byte) claveMasCercana;
	}
	
	public ArrayList<ElementoGrafico> getClefs() {
		return clefs;
	}
	
	public Wedge getCrescendo(int index) {
		return crescendos.get(index);
	}
	
	public Wedge getDiminuendo(int index) {
		return diminuendos.get(index);
	}
	
	public ElementoGrafico getDynamics(int index) {
		return dynamics.get(index);
	}
	
	public ElementoGrafico getFifths() {
		return fifths;
	}
	
	public Intensidad getIntensidad(int index) {
		return intensidades.get(index);
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
	
	public Pedal getPedalFin(int index) {
		return pedalesFin.get(index);
	}
	
	public Pedal getPedalInicio(int index) {
		return pedalesInicio.get(index);
	}
	
	public ElementoGrafico getPedalStart(int index) {
		return pedalStarts.get(index);
	}
	
	public ElementoGrafico getPedalStop(int index) {
		return pedalStops.get(index);
	}
	
	public Quintas getQuintas() {
		return quintas;
	}
	
	public Tempo getTempo() {
		return tempo;
	}
	
	public Texto getTexto(int index) {
		return textos.get(index);
	}
	
	public ElementoGrafico getTime() {
		return time;
	}
	
	public ElementoGrafico getWedge(int index) {
		return wedges.get(index);
	}
	
	public byte getWordsLocation(int index) {
		return words.get(index).getValue(0);
	}
	
	public int getWordsPosition(int index) {
		return words.get(index).getPosition();
	}
	
	public String getWordsString(int index) {
		ArrayList<Byte> bytesWords = words.get(index).getValues();
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

	private boolean noHayNotasDelanteDeClave(Clave clave) {
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			if (clave.getPentagrama() == getNota(i).getPentagrama())
				if (getNota(i).getPosition() > clave.getPosition())
					return false;
		
		return true;
	}
	
	public ArrayList<Nota> notasConPulsos() {
		ArrayList<Nota> notasConPulsos = new ArrayList<Nota>();
		
		final int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			if (notas.get(i).getPulsos() > 0)
				notasConPulsos.add(notas.get(i));
		
		Collections.sort(notasConPulsos);
		return notasConPulsos;
	}
	
	public int numClaves() {
		return claves.size();
	}
	
	public int numCrescendos() {
		return crescendos.size();
	}
	
	public int numDiminuendos() {
		return diminuendos.size();
	}
	
	public int numDynamics() {
		return dynamics.size();
	}
	
	public int numIntensidades() {
		return intensidades.size();
	}

	public int numNotas() {
		return notas.size();
	}
	
	public int numPedalesFin() {
		return pedalesFin.size();
	}
	
	public int numPedalesInicio() {
		return pedalesInicio.size();
	}
	
	public int numPedalStarts() {
		return pedalStarts.size();
	}
	
	public int numPedalStops() {
		return pedalStops.size();
	}
	
	public int numPulsos() {
		return tempo.numeroDePulsos();
	}
	
	public int numTextos() {
		return textos.size();
	}

	public int numWedges() {
		return wedges.size();
	}
	
	public int numWords() {
		return words.size();
	}
	
	public boolean hayFifths() {
		return fifths != null;
	}
	
	public boolean hayQuintas() {
		return quintas != null;
	}
	
	public boolean hayTempo() {
		return tempo != null && tempo.dibujar();
	}
	
	public boolean hayTime() {
		return time != null;
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

		for (int i=0; i<claves.size(); i++)
			if (getClave(i) != null)
				if (!xEncontradas.contains(getClave(i).getX()))
					xEncontradas.add(getClave(i).getX());
		
		for (int i=0; i<intensidades.size(); i++)
			if (getIntensidad(i) != null)
				if (!xEncontradas.contains(getIntensidad(i).getX()))
					xEncontradas.add(getIntensidad(i).getX());

		for (int i=0; i<pedalesInicio.size(); i++)
			if (!xEncontradas.contains(getPedalInicio(i).getX()))
				xEncontradas.add(getPedalInicio(i).getX());

		for (int i=0; i<pedalesFin.size(); i++)
			if (!xEncontradas.contains(getPedalFin(i).getX()))
				xEncontradas.add(getPedalFin(i).getX());
		
		if (hayTempo())
			if (!xEncontradas.contains(getTempo().getX()))
				xEncontradas.add(getTempo().getX());
		
		for (int i=0; i<textos.size(); i++)
			if (!xEncontradas.contains(getTexto(i).getX()))
				xEncontradas.add(getTexto(i).getX());
		
		for (int i=0; i<crescendos.size(); i++) {
			if (!xEncontradas.contains(getCrescendo(i).getXIni()))
				xEncontradas.add(getCrescendo(i).getXIni());
			if (!xEncontradas.contains(getCrescendo(i).getXFin()))
				xEncontradas.add(getCrescendo(i).getXFin());
		}
		
		for (int i=0; i<diminuendos.size(); i++) {
			if (!xEncontradas.contains(getDiminuendo(i).getXIni()))
				xEncontradas.add(getDiminuendo(i).getXIni());
			if (!xEncontradas.contains(getDiminuendo(i).getXFin()))
				xEncontradas.add(getDiminuendo(i).getXFin());
		}
		
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
	
	public void setFifths(ElementoGrafico fifths) {
		this.fifths = fifths;
	}
	
	public void setNumeroCompas(int numeroCompas) {
		this.numeroCompas = numeroCompas;
	}
	
	public void setQuintas(Quintas quintas) {
		this.quintas = quintas;
		
		updateXFin(quintas.getX());
	}

	public void setTempo(Tempo tempo) {
		this.tempo = tempo;
		
		updateXFin(tempo.getX());
	}
	
	public void setTime(ElementoGrafico time) {
		this.time = time;
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
	
	private void updateXFin(int x_fin) {
		if (x_fin > this.x_fin)
			setXFin(x_fin);
	}

	/*
	 * 
	 * FUNCIONES DE CLONACIÓN
	 * 
	 */
	
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
		for (int i=0; i<barlines.size(); i++)
			nuevoCompas.addBarline(clonarElementoGrafico(barlines.get(i)));
		for (int i=0; i<clefs.size(); i++)
			nuevoCompas.addClef(clonarElementoGrafico(clefs.get(i)));
		for (int i=0; i<dynamics.size(); i++)
			nuevoCompas.addDynamics(clonarElementoGrafico(dynamics.get(i)));
		for (int i=0; i<pedalStarts.size(); i++)
			nuevoCompas.addPedalStart(clonarElementoGrafico(getPedalStart(i)));
		for (int i=0; i<pedalStops.size(); i++)
			nuevoCompas.addPedalStop(clonarElementoGrafico(getPedalStop(i)));
		for (int i=0; i<words.size(); i++)
			nuevoCompas.addWords(clonarElementoGrafico(words.get(i)));
		for (int i=0; i<wedges.size(); i++)
			nuevoCompas.addWedge(clonarElementoGrafico(wedges.get(i)));
		
		nuevoCompas.setFifths(clonarElementoGrafico(getFifths()));
		nuevoCompas.setTime(clonarElementoGrafico(getTime()));
	}
	
	private void clonarNotas(Compas nuevoCompas) {
		int numNotas = notas.size();
		for (int i=0; i<numNotas; i++)
			nuevoCompas.addNote(clonarNota(notas.get(i)));
	}
	
	private Nota clonarNota(Nota oldNote) {
		Nota newNote = new Nota(oldNote.getStep(), oldNote.getOctava(), oldNote.getFiguracion(),
				oldNote.getPulsos(), oldNote.getBeam(), oldNote.getBeamId(), oldNote.getPlica(), 
				oldNote.getVoz(), oldNote.getPentagrama(), oldNote.getFigurasGraficas(), 
				oldNote.getPosicionArray());
		
		return newNote;
	}
}