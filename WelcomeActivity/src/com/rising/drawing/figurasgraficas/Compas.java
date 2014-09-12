package com.rising.drawing.figurasgraficas;

import java.util.ArrayList;
import java.util.Collections;

import com.rising.drawing.ElementoGrafico;
import com.rising.drawing.StaticMethods;

public class Compas {

	//  Información tal cual fue leída en el fichero
	private transient final ArrayList<ElementoGrafico> barlines;
	private transient final ArrayList<ElementoGrafico> clefs;
	private transient final ArrayList<ElementoGrafico> dynamics;
	private ElementoGrafico fifths;
	private transient final ArrayList<ElementoGrafico> pedalStarts;
	private transient final ArrayList<ElementoGrafico> pedalStops;
	private ElementoGrafico time;
	private transient final ArrayList<ElementoGrafico> wedges;
	private transient final ArrayList<ElementoGrafico> words;
	
	//  Información ya analizada
	private transient final ArrayList<Nota> notas;
	private transient final ArrayList<Clave> claves;
	private transient final ArrayList<Wedge> crescendos;
	private transient final ArrayList<Wedge> diminuendos;
	private transient final ArrayList<Intensidad> intensidades;
	private transient final ArrayList<Pedal> pedalesInicio;
	private transient final ArrayList<Pedal> pedalesFin;
	private Quintas quintas;
	private Tempo tempo;
	private transient final ArrayList<Texto> textos;
    
    private int bpm;
    private int bpmIndex;
    
	private transient int xIni;
	private transient int xFin;
	private transient int yIni;
	private transient int yFin;
	private transient int xIniNotas;
	
	private int numeroCompas;
	
	public Compas() 
	{
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

		xIni = -1;
		xFin = -1;
		yIni = -1;
		yFin = -1;
		xIniNotas = -1;
		
		numeroCompas = -1;
	}
	
	public void addBarline(final ElementoGrafico barline) 
	{
		barlines.add(barline);
	}
	
	public void addClave(final Clave clave) 
	{
		claves.add(clave);
		
		updateXFin(clave.x);
	}

	public void addClef(final ElementoGrafico clef) 
	{
		if (clef != null) {
			clefs.add(clef);
		}
	}
	
	public void addCrescendo(final Wedge crescendo) 
	{
		crescendos.add(crescendo);
		
		updateXFin(crescendo.getXFin());
	}
	
	public void addDiminuendo(final Wedge diminuendo) 
	{
		diminuendos.add(diminuendo);
		
		updateXFin(diminuendo.getXFin());
	}
	
	public void addDynamics(final ElementoGrafico dynamics) 
	{
		if (dynamics != null) {
			this.dynamics.add(dynamics);
		}
	}
	
	public void addIntensidad(final Intensidad intensidad) 
	{
		intensidades.add(intensidad);
		
		updateXFin(intensidad.x);
	}
	
	public void addNote(final Nota note) 
	{
		notas.add(note);
	}
	
	public void addPedalFin(final Pedal pedalFin) 
	{
		pedalesFin.add(pedalFin);
		
		updateXFin(pedalFin.x);
	}
	
	public void addPedalInicio(final Pedal pedalInicio) 
	{
		pedalesInicio.add(pedalInicio);
		
		updateXFin(pedalInicio.x);
	}

	public void addPedalStart(final ElementoGrafico pedalStart) 
	{
		pedalStarts.add(pedalStart);
	}
	
	public void addPedalStop(final ElementoGrafico pedalStop) 
	{
		pedalStops.add(pedalStop);
	}
	
	public void addTexto(final Texto texto) 
	{
		textos.add(texto);
		
		updateXFin(texto.x);
	}
	
	public void addWedge(final ElementoGrafico wedge) 
	{
		wedges.add(wedge);
	}
	
	public void addWords(final ElementoGrafico words) 
	{
		this.words.add(words);
	}
	
	public int[] clavesAlFinalDelCompas(final int staves) 
	{
		int[] clavesAlFinal = new int[staves];
		for (int i=0; i<clavesAlFinal.length; i++) {
			clavesAlFinal[i] = -1;
		}
		
		Clave clave;
		for (int i=0; i<claves.size(); i++) 
		{
			clave = claves.get(i);
			
			if (noHayNotasDelanteDeClave(clave)) {
				clavesAlFinal[clave.pentagrama - 1] = i;
			}
		}
		
		return clavesAlFinal;
	}
	
	private boolean noHayNotasDelanteDeClave(final Clave clave) 
	{
		final int numNotas = notas.size();
		for (int i=0; i<numNotas; i++) 
		{
			if (notaDelanteDeClave(clave, notas.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean notaDelanteDeClave(final Clave clave, final Nota nota)
	{
		return (clave.pentagrama == nota.getPentagrama()) &&
			   (nota.getPosition() > clave.position);
	}

	public ElementoGrafico getBarline(final int index) 
	{
		return barlines.get(index);
	}
	
	public int getBpm() 
	{
		return bpm;
	}
	
	public int getBpmIndex() 
	{
		return bpmIndex;
	}
	
	public Clave getClave(final int index) 
	{
		return claves.get(index);
	}
	
	public byte getClaveDeNota(final Nota nota) 
	{
		final int claveActual = obtenerClaveDeNota(nota);
		return claveActual > -1 ? claves.get(claveActual).valorClave : (byte) claveActual;
	}
	
	private byte obtenerClaveDeNota(final Nota nota)
	{
		byte claveActual = -1;
		
		for (int i=0; i<claves.size(); i++) 
		{
			if (posibleClaveActual(claves.get(i), nota)) {
				claveActual = (byte) i;
			}
		}
		
		return claveActual;
	}
	
	private boolean posibleClaveActual(final Clave clave, final Nota nota)
	{
		return clave.pentagrama == nota.getPentagrama() &&
			   clave.x <= nota.getX();
	}
	
	public ElementoGrafico getClef(final int index) 
	{
		return clefs.get(index);
	}
	
	public Wedge getCrescendo(final int index) 
	{
		return crescendos.get(index);
	}
	
	public Wedge getDiminuendo(final int index) 
	{
		return diminuendos.get(index);
	}
	
	public ElementoGrafico getDynamics(final int index) 
	{
		return dynamics.get(index);
	}
	
	public ElementoGrafico getFifths() 
	{
		return fifths;
	}
	
	public Intensidad getIntensidad(final int index) 
	{
		return intensidades.get(index);
	}

	public Nota getNota(final int index) 
	{
		return notas.get(index);
	}

	public ArrayList<Nota> getNotas() 
	{
		return notas;
	}
	
	public int getNumeroCompas() 
	{
		return numeroCompas;
	}
	
	public Object[] getPedals()
	{
		return StaticMethods.joinToArrayListsIntoOneArray(pedalStarts, pedalStops);
	}
	
	public Object[] getPedales()
	{
		return StaticMethods.joinToArrayListsIntoOneArray(pedalesInicio, pedalesFin);
	}
	
	public Object[] getWedges()
	{
		return StaticMethods.joinToArrayListsIntoOneArray(crescendos, diminuendos);
	}
	
	public Pedal getPedalFin(final int index) 
	{
		return pedalesFin.get(index);
	}
	
	public Pedal getPedalInicio(final int index) 
	{
		return pedalesInicio.get(index);
	}
	
	public ElementoGrafico getPedalStart(final int index) 
	{
		return pedalStarts.get(index);
	}
	
	public ElementoGrafico getPedalStop(final int index) 
	{
		return pedalStops.get(index);
	}
	
	public Quintas getQuintas() 
	{
		return quintas;
	}
	
	public Tempo getTempo() 
	{
		return tempo;
	}
	
	public Texto getTexto(final int index) 
	{
		return textos.get(index);
	}
	
	public ElementoGrafico getTime() 
	{
		return time;
	}
	
	public ElementoGrafico getWedge(final int index) 
	{
		return wedges.get(index);
	}

	public ElementoGrafico getWords(int index)
	{
		return words.get(index);
	}
	
	public byte getWordsLocation(final int index) 
	{
		return words.get(index).getValue(0);
	}
	
	public int getWordsPosition(final int index) 
	{
		return words.get(index).getPosition();
	}
	
	public String getWordsString(final int index) 
	{
		return StaticMethods.convertByteArrayListToString(
				words.get(index).getValues(), 1);
	}

	public int getXIni() 
	{
		return xIni;
	}
	
	public int getXFin() 
	{
		return xFin;
	}
	
	public int getYIni() 
	{
		return yIni;
	}
	
	public int getYFin() 
	{
		return yFin;
	}
	
	public int getXIniNotas() 
	{
		return xIniNotas;
	}
	
	//  Devuelve el número de golpes de sonido que debe leer el micro para considerar
	//  que este compás ya ha sido interpretado en su totalidad. Los acordes cuentan 
	//  como un único sonido. Las notas tocadas a la vez en diferentes pentagramas o
	//  voces comparten la misma x, y por tanto cuentan como un unico golpe de sonido.
	public int golpesDeSonido() 
	{
		int numGolpes = 0;
		final ArrayList<Integer> xEncontradas = new ArrayList<Integer>();
		
		final int numNotas = notas.size();
		for (int i=0; i<numNotas; i++) 
		{
			if (!xEncontradas.contains(notas.get(i).getX())) {
				xEncontradas.add(notas.get(i).getX());
				
				//  La "unidad mínima" de "golpe de sonido" es la corchea. 
				//  Si se tocan negras o notas de mayor duración, se 
				//  añaden golpes de sonido extra. El +1 es el golpe
				//  mínimo que corresponde a la nota encontrada
				numGolpes += golpesExtra(notas.get(i)) + 1;
			}
		}
		
		return numGolpes;
	}
	
	private int golpesExtra(final Nota nota) 
	{
		if (nota.getFiguracion() == 11) {
			return nota.tienePuntillo() ? 6 : 5;
		}
		else {
			return 0;
		}
	}

	public boolean hasBpm()
	{
		return bpm != -1;
	}
	
	public ArrayList<Nota> notasConPulsos() 
	{
		final ArrayList<Nota> notasConPulsos = new ArrayList<Nota>();
		
		final int numNotas = notas.size();
		for (int i=0; i<numNotas; i++) 
		{
			if (notas.get(i).getPulsos() > 0) {
				notasConPulsos.add(notas.get(i));
			}
		}
		
		Collections.sort(notasConPulsos);
		return notasConPulsos;
	}
	
	public int numBarlines()
	{
		return barlines.size();
	}
	
	public int numClefs()
	{
		return clefs.size();
	}
	
	public int numClaves() 
	{
		return claves.size();
	}
	
	public int numCrescendos() 
	{
		return crescendos.size();
	}
	
	public int numDiminuendos() 
	{
		return diminuendos.size();
	}
	
	public int numDynamics() 
	{
		return dynamics.size();
	}
	
	public int numIntensidades() 
	{
		return intensidades.size();
	}

	public int numNotas() 
	{
		return notas.size();
	}
	
	public int numPedalesFin() 
	{
		return pedalesFin.size();
	}
	
	public int numPedalesInicio() 
	{
		return pedalesInicio.size();
	}
	
	public int numPedalStarts() 
	{
		return pedalStarts.size();
	}
	
	public int numPedalStops() 
	{
		return pedalStops.size();
	}
	
	public int numPulsos() 
	{
		return tempo.numeroDePulsos();
	}
	
	public int numTextos() 
	{
		return textos.size();
	}

	public int numWedges() 
	{
		return wedges.size();
	}
	
	public int numWords() 
	{
		return words.size();
	}
	
	public boolean hayFifths() 
	{
		return fifths != null;
	}
	
	public boolean hayQuintas() 
	{
		return quintas != null;
	}
	
	public boolean hayTempo() 
	{
		return tempo != null && tempo.dibujar();
	}
	
	public boolean hayTime() 
	{
		return time != null;
	}

	//  Devuelve un array con cada valor de X de cada elemento
	//  del compás. Por elemento se entiende cualquier nota, 
	//  acorde o figura gráfica que ocupe una posición X única en el compás
	public ArrayList<Integer> xsDeCompas(boolean userWantsAllElements) 
	{
		final ArrayList<Integer> xEncontradas = new ArrayList<Integer>();

		for (int i=0; i<notas.size(); i++) 
		{
			if (!xEncontradas.contains(notas.get(i).getX())) {
				xEncontradas.add(notas.get(i).getX());
			}
		}

		for (int i=0; i<claves.size(); i++) 
		{
			if (!xEncontradas.contains(getClave(i).x)) {
				xEncontradas.add(getClave(i).x);
			}
		}
		
		if (userWantsAllElements)
		{
			for (int i=0; i<intensidades.size(); i++) 
			{
				if (!xEncontradas.contains(getIntensidad(i).x)) {
					xEncontradas.add(getIntensidad(i).x);
				}
			}
	
			for (int i=0; i<pedalesInicio.size(); i++) 
			{
				if (!xEncontradas.contains(getPedalInicio(i).x)) {
					xEncontradas.add(getPedalInicio(i).x);
				}
			}
	
			for (int i=0; i<pedalesFin.size(); i++) 
			{
				if (!xEncontradas.contains(getPedalFin(i).x)) {
					xEncontradas.add(getPedalFin(i).x);
				}
			}
			
			if ( hayTempo() && !xEncontradas.contains(getTempo().getX()) ) {
				xEncontradas.add(getTempo().getX());
			}
			
			for (int i=0; i<textos.size(); i++) 
			{
				if (!xEncontradas.contains(getTexto(i).x)) {
					xEncontradas.add(getTexto(i).x);
				}
			}
			
			for (int i=0; i<crescendos.size(); i++) 
			{
				if (!xEncontradas.contains(getCrescendo(i).getXIni())) {
					xEncontradas.add(getCrescendo(i).getXIni());
				}
				if (!xEncontradas.contains(getCrescendo(i).getXFin())) {
					xEncontradas.add(getCrescendo(i).getXFin());
				}
			}
			
			for (int i=0; i<diminuendos.size(); i++) 
			{
				if (!xEncontradas.contains(getDiminuendo(i).getXIni())) {
					xEncontradas.add(getDiminuendo(i).getXIni());
				}
				if (!xEncontradas.contains(getDiminuendo(i).getXFin())) {
					xEncontradas.add(getDiminuendo(i).getXFin());
				}
			}
		}
		
		Collections.sort(xEncontradas);
		return xEncontradas;
	}
	
	public int saberXPrimeraNota() 
	{
		return notas.get(0).getX();
	}
	
	public void setBpm(final int bpm) 
	{
		this.bpm = bpm;
	}
	
	public void setBpmIndex(final int bpmIndex) 
	{
		this.bpmIndex = bpmIndex;
	}
	
	public void setFifths(final ElementoGrafico fifths) 
	{
		this.fifths = fifths;
	}
	
	public void setNumeroCompas(final int numeroCompas) 
	{
		this.numeroCompas = numeroCompas;
	}
	
	public void setQuintas(final Quintas quintas) 
	{
		this.quintas = quintas;
		
		updateXFin(quintas.x);
	}

	public void setTempo(final Tempo tempo) 
	{
		this.tempo = tempo;
		
		updateXFin(tempo.getX());
	}
	
	public void setTime(final ElementoGrafico time) 
	{
		this.time = time;
	}
	
	public void setXIni(final int xIni) 
	{
		this.xIni = xIni;
	}
	
	public void setXFin(final int xFin) 
	{
		this.xFin = xFin;
	}
	
	public void setYIni(final int yIni) 
	{
		this.yIni = yIni;
	}
	
	public void setYFin(final int yFin) 
	{
		this.yFin = yFin;
	}
	
	public void setXIniNotas(final int xIniNotas) 
	{
		this.xIniNotas = xIniNotas;
	}
	
	private void updateXFin(final int xFin) 
	{
		if (xFin > this.xFin) {
			setXFin(xFin);
		}
	}

	public Compas clonar() 
	{
		Clonador clonador = new Clonador();
		final Compas nuevoCompas = clonador.clonarCompas(this);
		return nuevoCompas;
	}

	public void clear() 
	{
		for (int i=0; i<notas.size(); i++) {
			notas.get(i).setX(0);
			notas.get(i).setY(0);
		}
		
		claves.clear();
		crescendos.clear();
		diminuendos.clear();
		intensidades.clear();
		pedalesInicio.clear();
		pedalesFin.clear();
		quintas = null;
		tempo = null;
		textos.clear();

		bpm = -1;
		bpmIndex = -1;
		xIni = -1;
		xFin = -1;
		yIni = -1;
		yFin = -1;
		xIniNotas = -1;
		numeroCompas = -1;
	}
}