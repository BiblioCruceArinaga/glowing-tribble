package com.rising.drawing.figurasgraficas;

import java.util.ArrayList;

import com.rising.drawing.ElementoGrafico;

public class Clonador {

	public Compas clonarCompas(final Compas compasViejo)
	{
		final Compas compasNuevo = new Compas();
		
		clonarFigurasGraficas(compasViejo, compasNuevo);
		clonarNotas(compasViejo, compasNuevo);
		
		return compasNuevo;
	}
	
	private void clonarFigurasGraficas(final Compas compasViejo, final Compas compasNuevo) 
	{
		for (int i=0; i<compasViejo.numBarlines(); i++) {
			compasNuevo.addBarline(clonarElementoGrafico(compasViejo.getBarline(i)));
		}
		
		for (int i=0; i<compasViejo.numClefs(); i++) {
			compasNuevo.addClef(clonarElementoGrafico(compasViejo.getClef(i)));
		}
		
		for (int i=0; i<compasViejo.numDynamics(); i++) {
			compasNuevo.addDynamics(clonarElementoGrafico(compasViejo.getDynamics(i)));
		}
		
		for (int i=0; i<compasViejo.numPedalStarts(); i++) {
			compasNuevo.addPedalStart(clonarElementoGrafico(compasViejo.getPedalStart(i)));
		}
		
		for (int i=0; i<compasViejo.numPedalStops(); i++) {
			compasNuevo.addPedalStop(clonarElementoGrafico(compasViejo.getPedalStop(i)));
		}
		
		for (int i=0; i<compasViejo.numWedges(); i++) {
			compasNuevo.addWedge(clonarElementoGrafico(compasViejo.getWedge(i)));
		}
		
		for (int i=0; i<compasViejo.numWords(); i++) {
			compasNuevo.addWords(clonarElementoGrafico(compasViejo.getWords(i)));
		}

		compasNuevo.setFifths(clonarElementoGrafico(compasViejo.getFifths()));
		compasNuevo.setTime(clonarElementoGrafico(compasViejo.getTime()));
	}
	
	private ElementoGrafico clonarElementoGrafico(final ElementoGrafico old) {
		if (old == null)  {
			return null;
		}
		else {			
			final ElementoGrafico nuevo = new ElementoGrafico();
			
			nuevo.setPosition(old.getPosition());
			nuevo.addAllValues(old.getValues());
			nuevo.setX(old.getX());
			
			return nuevo;
		}
	}
	
	private void clonarNotas(final Compas compasViejo, final Compas compasNuevo) 
	{
		final ArrayList<Nota> notas = compasViejo.getNotas();
		for (int i=0; i<notas.size(); i++) {
			compasNuevo.addNote(clonarNota(notas.get(i)));
		}
	}

	private Nota clonarNota(final Nota oldNote) 
	{
		final Nota newNote = new Nota(oldNote.getStep(), oldNote.getOctava(), oldNote.getFiguracion(),
				oldNote.getPulsos(), oldNote.getBeam(), oldNote.getBeamId(), oldNote.getPlica(), 
				oldNote.getVoz(), oldNote.getPentagrama(), oldNote.getFigurasGraficas(), 
				oldNote.getPosicionArray());
		
		return newNote;
	}
}