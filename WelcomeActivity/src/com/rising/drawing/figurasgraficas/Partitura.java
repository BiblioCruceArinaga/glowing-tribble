package com.rising.drawing.figurasgraficas;

import java.util.ArrayList;

import com.rising.drawing.StaticMethods;

public class Partitura 
{
	private String work;
    private String creator;
    private int staves;
    private byte instrument;
    private int firstNumber;    
    private int width;
    private int height;
    private final ArrayList<Compas> compases;
    
    public Partitura() 
    {
        work = "";
        creator = "";
        compases = new ArrayList<Compas>();
        
        instrument = 0;
        staves = 1;
        firstNumber = 1;
        
        width = 0;
        height = 0;
    }
    
    public void addCompas(final Compas compas) 
    {
    	compases.add(compas);
    }
    
    public void destruir() 
    {
    	work = null;
    	creator = null;
    	compases.clear();
    	instrument = 0;
    	staves = 1;
    }
    
    public Compas getCompas(final int index) 
    {
    	return compases.get(index);
    }
    
    public ArrayList<Compas> getCompases() 
    {
    	return compases;
    }
    
    public String getCreator() 
    {
    	return creator;
    }
    
    public int getFirstNumber() 
    {
    	return firstNumber;
    }
    
    public int getHeight() 
    {
    	return height;
    }
    
    public byte getInstrument() 
    {
    	return instrument;
    }
    
    public int getLastMarginY() 
    {
    	return compases.get(compases.size() - 1).getYFin();
    }
    
    public int getNumeroDeCompases() 
    {
    	return compases.size();
    }
    
    public int getStaves() 
    {
    	return staves;
    }
    
    public int getWidth() 
    {
    	return width;
    }
    
    public String getWork() 
    {
    	return work;
    }

    public void setCompases(final ArrayList<Compas> nuevosCompases) 
    {
    	compases.clear();
    	
    	final int numCompases = nuevosCompases.size();
    	for (int i=0; i<numCompases; i++) {
    		compases.add(nuevosCompases.get(i));
    	}
    }
    
    public void setCreator(final ArrayList<Byte> creator) 
    {
    	final String creatorString = StaticMethods.convertByteArrayListToString(creator, 0);
        this.creator = StaticMethods.sanitizeString(creatorString);
    }
    
    public void setFirstNumber(final int firstNumber) 
    {
    	this.firstNumber = firstNumber;
    }
    
    public void setHeight(final int height) 
    {
    	this.height = height;
    }
    
    public void setInstrument(final byte instrument) 
    {
    	this.instrument = instrument;
    }
    
    public void setStaves(final byte staves) 
    {
    	this.staves = staves;
    }
    
    public void setWidth(final int width) 
    {
    	this.width = width;
    }
    
    public void setWork(final ArrayList<Byte> work) 
    {
    	final String workString = StaticMethods.convertByteArrayListToString(work, 0);
        this.work = StaticMethods.sanitizeString(workString);
    }
}