package com.rising.mainscreen;

public class Score implements Comparable<Object> {
    private String Author;
  	private String Title;
  	private String Instrument;
    private int image;
 
    public Score(String Author, String Title, int image) {
        this.Author = Author;
        this.Title = Title;
        this.Instrument = "Piano";
        this.image = image;
    }
 
    public String getAuthor() {
        return this.Author;
    }
 
    public void setAuthor(String author) {
		Author = author;
	}
    
    public String getTitle() {
        return this.Title;
    }

	public void setTitle(String title) {
		Title = title;
	}

	public String getInstrument() {
		return this.Instrument;
	}
	
	public void setInstrument(String instrument) {
		Instrument = instrument;
	}
	
    public int getImage() {
        return this.image;
    }
    
	public void setImage(int image) {
		this.image = image;
	}

	@Override
	public int compareTo(Object arg0) {
		Score f = (Score)arg0;
        return Title.compareTo(f.getTitle());
	}
}