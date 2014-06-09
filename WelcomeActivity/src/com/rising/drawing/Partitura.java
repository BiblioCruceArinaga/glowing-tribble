package com.rising.drawing;

import java.util.ArrayList;

public class Partitura {
	private String work;
    private String creator;
    
    private int staves;
    private byte instrument;
    private int divisions;
    private int firstNumber;

    private ArrayList<Compas> compases;
    
    public Partitura() {
        work = "";
        creator = "";
        compases = new ArrayList<Compas>();
        
        instrument = 0;
        divisions = 0;
        staves = 1;
        firstNumber = 1;
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
    
    public void destruir() {
    	work = null;
    	creator = null;
    	compases.clear();
    	
    	instrument = 0;
    	divisions = 0;
    	staves = 1;
    }
    
    public Compas getCompas(int index) {
    	return compases.get(index);
    }
    
    public ArrayList<Compas> getCompases() {
    	return compases;
    }   
    
    public String getCreator() {
    	return creator;
    }
    
    public int getDivisions() {
    	return divisions;
    }
    
    public int getFirstNumber() {
    	return firstNumber;
    }
    
    public byte getInstrument() {
    	return instrument;
    }
    
    public int getLastMarginY() {
    	return compases.get(compases.size() - 1).getYFin();
    }
    
    public int getStaves() {
    	return staves;
    }
    
    public String getWork() {
    	return work;
    }
    
    private String sanitizeString(String oldString) {
    	int index = oldString.indexOf('&');
    	
    	if (index > -1) {
    		switch (oldString.charAt(index - 1)) {
	    		case 'a':
	    			oldString = oldString.replace("a&", "á");
	    			break;
	    		case 'e':
	    			oldString = oldString.replace("e&", "é");
	    			break;
	    		case 'i':
	    			oldString = oldString.replace("i&", "í");
	    			break;
	    		case 'o':
	    			oldString = oldString.replace("o&", "ó");
	    			break;
	    		case 'u':
	    			oldString = oldString.replace("u&", "ú");
	    			break;
	    		case 'n':
	    			oldString = oldString.replace("n&", "ñ");
	    			break;
    			default:
    				break;
    		}
    	}
    	
    	return oldString;
    }
    
    public void setCompases(ArrayList<Compas> nuevosCompases) {
    	compases.clear();
    	
    	int numCompases = nuevosCompases.size();
    	for (int i=0; i<numCompases; i++) {
    		compases.add(nuevosCompases.get(i));
    	}
    }
    
    public void setCreator(ArrayList<Byte> creator) {
    	String creatorString = bytesArrayToString(creator);
        this.creator = sanitizeString(creatorString);
    }
    
    public void setDivisions(ArrayList<Byte> divisions) {
        String divisionsString = bytesArrayToString(divisions);
        this.divisions = Integer.parseInt(divisionsString);
    }
    
    public void setFirstNumber(int firstNumber) {
    	this.firstNumber = firstNumber;
    }
    
    public void setInstrument(byte instrument) {
    	this.instrument = instrument;
    }
    
    public void setStaves(byte staves) {
    	this.staves = staves;
    }
    
    public void setWork(ArrayList<Byte> work) {
    	String workString = bytesArrayToString(work);
        this.work = sanitizeString(workString);
    }
}