package com.rising.store;


public class PartituraTienda {
	
	private int id;
	private String nombre;
	private String autor;
	private String instrumento;
	private String description;
	private int year;
	private float precio;
	private boolean comprado;
	private String url;
	//private Bitmap imagen;
		
	public PartituraTienda(int id, String nombre, String autor, String instrumento, float precio, String description, int year, boolean comprado, String url) {
		this.id = id;
		this.nombre = nombre;
		this.autor = autor;
		this.instrumento = instrumento;
		this.precio = precio;
		this.description = description;
		this.year = year;
		this.comprado = comprado;
		this.url = url;
		//this.imagen = imagen;
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getAutor() {
		return autor;
	}


	public void setAutor(String autor) {
		this.autor = autor;
	}


	public String getInstrumento() {
		return instrumento;
	}


	public void setInstrumento(String instrumento) {
		this.instrumento = instrumento;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}


	public float getPrecio() {
		return precio;
	}


	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public boolean getComprado() {
		return comprado;
	}


	public void setComprado(boolean c) {
		this.comprado = c;
	}

	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	
	/*public Bitmap getImage(){
		return imagen;
	}
	
	public void setImage(Bitmap img){
		this.imagen = img;
	}*/
}
