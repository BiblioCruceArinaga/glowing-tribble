package com.rising.drawing;

import java.util.ArrayList;

public class Partitura {
	private String work;
    private String creator;
    
    private int staves;
    private int instrument;
    private int divisions;

    private ArrayList<Compas> compases;
    
    public Partitura() {
        work = "";
        creator = "";
        compases = new ArrayList<Compas>();
        
        instrument = 0;
        divisions = 0;
        staves = 1;
    }
    
    public void addCompas(Compas compas) {
    	compases.add(compas);
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
    
    public String getCreator() {
    	return creator;
    }
    
    public int getStaves() {
    	return staves;
    }
    
    public String getWork() {
    	return work;
    }
    
    public void setCreator(ArrayList<Byte> creator) {
    	String creatorString = bytesArrayToString(creator);
        this.creator = creatorString;
    }
    
    public void setDivisions(ArrayList<Byte> divisions) {
        String divisionsString = bytesArrayToString(divisions);
        this.divisions = Integer.parseInt(divisionsString);
    }
    
    public void setInstrument(byte instrument) {
    	this.instrument = instrument;
    }
    
    public void setStaves(byte staves) {
    	this.staves = staves;
    }
    
    public void setWork(ArrayList<Byte> work) {
    	String workString = bytesArrayToString(work);
        this.work = workString;
    }
}