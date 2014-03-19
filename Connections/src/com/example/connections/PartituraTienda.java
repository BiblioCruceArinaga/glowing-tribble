package com.example.connections;

public class PartituraTienda {
	
	int id;
	String nombre;
	String autor;
	String instrumento;
	float precio;
	
	public PartituraTienda(int id, String nombre, String autor, String instrumento, float precio) {
		this.id = id;
		this.nombre = nombre;
		this.autor = autor;
		this.instrumento = instrumento;
		this.precio = precio;
	}
	
	public int id() {
		return this.id;
	}
	
	public String nombre() {
		return this.nombre;
	}
	
	public String autor() {
		return this.autor;
	}
	
	public String instrumento() {
		return this.instrumento;
	}
	
	public float precio() {
		return this.precio;
	}
}
