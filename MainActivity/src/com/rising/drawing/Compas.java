package com.rising.drawing;

import java.util.ArrayList;

public class Compas {
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
	
	public Compas() {
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
	}
	
	public void addBarline(ElementoGrafico barline) {
		barlines.add(barline);
	}
	
	public void addClef(ElementoGrafico clef) {
		clefs.add(clef);
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
}
