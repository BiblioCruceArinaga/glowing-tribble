package com.rising.mainscreen;


public class Score implements Comparable<Object> {
    private String Author;
  	private String Title;
  	private String Instrument;
    private String image;
    private String Format;
 
    public Score(String Author, String Title, String image, String Instrument, String Format) {
        this.Author = Author;
        this.Title = Title;
        this.Instrument = Instrument;
        this.image = image;
        this.Format = Format;
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
	
    public String getImage() {
        return this.image;
    }
    
	public void setImage(String image) {
		this.image = image;
	}

    public String getFormat() {
		return this.Format;
	}
    
	public void setFormat(String format) {
		this.Format = format;
	}
    
	@Override
	public int compareTo(Object arg0) {
		Score f = (Score)arg0;
        return Title.compareTo(f.getTitle());
	}
}